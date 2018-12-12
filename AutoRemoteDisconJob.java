package com.sp.ppms.console.jobs;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sp.ppms.console.dao.HolidayInfoMapper;
import com.sp.ppms.console.dao.MeterInfoMapper;
import com.sp.ppms.console.dao.ODPJobDetailMapper;
import com.sp.ppms.console.dao.RemoteProvisioningJobMapper;
import com.sp.ppms.console.domain.*;
import com.sp.ppms.console.service.CustomerInfoService;
import com.sp.ppms.console.service.RemoteDisconRecService;
import com.sp.ppms.console.service.RemoteProvisioningJobService;
import com.sp.ppms.console.service.SmsService;
import com.sp.ppms.console.util.*;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.sp.ppms.console.util.Constants.OneHourMaxLimit;
import static com.sp.ppms.console.util.HttpClientUtil.doPostHttps;


/**
 * @description:
 * @author: helanpeng
 * @create: 2018-07-25 16:16
 **/
@Service
public class AutoRemoteDisconJob implements Job {
    private Logger logger = LoggerFactory.getLogger(AutoRemoteDisconJob.class);

    @Autowired
    private RemoteProvisioningJobService remoteProvisioningJobService;
    @Autowired
    private RemoteDisconRecService remoteDisconRecService;
    @Autowired
    private CustomerInfoService customerInfoService;
    @Autowired
    private RemoteProvisioningJobMapper remoteProvisioningJobMapper;
    @Autowired
    private ODPJobDetailMapper odpJobDetailMapper ;
    @Autowired
    private MeterInfoMapper meterInfoMapper;
    @Autowired
    private SmsService smsService;
    @Autowired
    private SftpConfig sftpConfig;
    @Autowired
    private HolidayInfoMapper holidayInfoMapper;

    /**
    * @Description: 自动创建跳闸任务，每一小时最多处理100条
    * @Param:
    * @return:
    * @Author: helanpeng
    * @Date: 2018/7/25
    */
    public void autoMeterRemoteDiscon(){
        //判断nms是否在更新系统
        if(!CommonUtils.isNmsAvailable()){
            logger.info("NMS is not available! try again later!");
            return;
        }
        if(!CommonUtils.isDisconAvailable()){
            logger.info("Discon is not available! try again later!");
            return;
        }
        int isHoliday = holidayInfoMapper.countByNowDate();
        if(isHoliday > 0){
            logger.info("today is holiday! try again tomorrow!");
            return;
        }
        //查询出需要跳闸的电表
        // 每个小时最多跳闸100条
//        int disconCount = remoteDisconRecService.getOneHourDisconCount(DateUtil.getStartHour(new Date()),DateUtil.getStartLastHour(new Date()));
//        if(disconCount >= OneHourMaxLimit){
//            return;
//        }
        //该小时内最多跳闸数
//        int selecCount = OneHourMaxLimit -disconCount;
        List<RemoteDisconRec> remoteDisconRecList = remoteDisconRecService.getWatingDisconList(OneHourMaxLimit);
        // 新激活用户三天内不允许跳闸
        // 查询余额，如果高于于信用余额则不执行跳闸指令
        if(remoteDisconRecList.isEmpty()){
            return;
        }
        Iterator<RemoteDisconRec> iterator = remoteDisconRecList.iterator();
        while(iterator.hasNext()){
            RemoteDisconRec remoteDisconRec = iterator.next();
            try {
                if(remoteDisconRec.getAccNo() != null){
                    //查询激活时间和余额
                    Map<String,Object> accInfo = customerInfoService.getByAccNo(remoteDisconRec.getAccNo());
                    //判断如果激活时间在三天内或者余额高于0则将此记录pro_staus状态修改为废弃（03）
                    // 增加04状态 激活时间在三天以内 05-余额大于0
                    if(accInfo.get("date") == null || accInfo.get("balance") == null || accInfo.get("emergencyCredit")  == null){
                        //数据异常，去掉改记录即可
                        logger.info("------accInfo return null--------");
                        remoteDisconRec.setProStatus(Constants.REMOTER_PRO_STATUS.ABANDON.getType());
                        remoteDisconRecService.doUpdate(remoteDisconRec);
                        iterator.remove();
                    }else {
                        BigDecimal balance = (BigDecimal) accInfo.get("balance");
                        Date activationDate = (Date) accInfo.get("date");

                        BigDecimal emergencyCredit = BigDecimal.ZERO.subtract((BigDecimal)accInfo.get("emergencyCredit"));//可透支余额
                        //TODO sms
                        if(balance.compareTo(emergencyCredit) > 0 ){
                            logger.info("------balance > -emergencyCredit--------");
                            remoteDisconRec.setProStatus(Constants.REMOTER_PRO_STATUS.ABOVE_ZERO.getType());
                            remoteDisconRecService.doUpdate(remoteDisconRec);
                            iterator.remove();
                        }else if(activationDate.after(DateUtil.getSettedDay(new Date(),-3))){
                            logger.info("------activationDate.after(DateUtil.getSettedDay(new Date(),-3))--------");
                            remoteDisconRec.setProStatus(Constants.REMOTER_PRO_STATUS.DELAYED.getType());
                            remoteDisconRecService.doUpdate(remoteDisconRec);
                            iterator.remove();
                        }
                    }
                }
            }catch (Exception e){
                logger.error(e.getMessage(),e);
                continue;
            }
        }
        if(!remoteDisconRecList.isEmpty()){
            //插入一条RemoteProvisioningJob记录
            RemoteProvisioningJob remoteProvisioningJob = new RemoteProvisioningJob();
            remoteProvisioningJob.setName(Constants.METER_CTRL_TYPE.REMOTE_DISCONNECTION+"_"+DateUtils.format(new Date()));
            remoteProvisioningJob.setCtrlType(Constants.METER_CTRL_TYPE.REMOTE_DISCONNECTION.getType());
            remoteProvisioningJob.setPriority(Priority.JOB_PRIORITY_HIGH);
            remoteProvisioningJob.setStartTime(new Date());
            try {
                //保存RemoteProvisioningJob记录  返回主键Id
                logger.info("-----------insert remoteProvisioningJob ---------");
                int jobId = remoteProvisioningJobService.doSave(remoteProvisioningJob);
                logger.info("-----------insert remoteProvisioningJob seccuss---------");
                List<String> meterIdList = new ArrayList<>();
                List<String> customers = new ArrayList<>();
                //更新remoteDisconRecList中所有RemoteDisconRec对象job_ids字段
                for(RemoteDisconRec remoteDisconRec:remoteDisconRecList){
                    remoteDisconRec.setJobId(String.valueOf(jobId));
                    remoteDisconRec.setOperStatus(Constants.REMOTER_OPER_STATUS.EXECUTING.getType());
                    remoteDisconRec.setProStatus(Constants.REMOTER_PRO_STATUS.PENDING.getType());
                    remoteDisconRec.setOperTime(new Date());
                    remoteDisconRec.setExeStartTime(new Date());
                    meterIdList.add(remoteDisconRec.getMeterId());
                    customers.add(remoteDisconRec.getAccNo());
                }
                //更新跳闸记录
                remoteDisconRecService.doUpdateBatch(remoteDisconRecList);
                //添加远程控制任务
                logger.info("-----------disconnect addRemoteProvisioningJob ---------");
                remoteProvisioningJobService.addRemoteProvisioningJob(meterIdList,Constants.METER_CTRL_TYPE.REMOTE_DISCONNECTION, remoteProvisioningJob);
                logger.info("-----------disconnect addRemoteProvisioningJob end ---------");
                //send message
                for(int k = 0;k < customers.size();k++){
                    try {
                        Map<String,String> smsParams = new HashMap<>();
                        //AccountNo, CreditBalance
                        smsParams.put(Constants.SMS_TEMPLATE_PARAMS.ACCOUNT_NO.getValue(),customers.get(k));
                        MeterInfo meterInfo = meterInfoMapper.selectByAccNo(customers.get(k),Constants.METER_STATUS.RUN.getStatus());
                        smsParams.put(Constants.SMS_TEMPLATE_PARAMS.METER_NO.getValue(),meterInfo.getMeterId());
                        smsService.generateSmsRecord(customers.get(k),Constants.SMS_TYPE.REMOTE_DISCONNECTION.getType(),smsParams);
                    } catch (Exception e) {
                        logger.error("send message failed: accNo:" + customers.get(k) + e.getMessage(),e);
                    }
                }
            }catch (Exception e){
                logger.error(remoteProvisioningJob.getName() + " doSave failed:" + e.getMessage(),e);
            }
        }
    }

    /**
     * @Description:  获取远程控制任务结果
     * @Param:
     * @return:
     * @Author: helanpeng
     * @Date: 2018/7/25
     */
    public void getRemoteDisconResult(){
        if(!CommonUtils.isNmsAvailable()){
            logger.info("NMS is not available! try again later!");
            return;
        }
        //查询出所有正在执行的job
        List<String> non_excutionStatus = new ArrayList<>();
        non_excutionStatus.add(Constants.JOB_STATUS.JOB_EXEC_STATUS_COMPLETE.getStatus());
        non_excutionStatus.add(Constants.JOB_STATUS.JOB_EXEC_STATUS_FAILURE.getStatus());
        non_excutionStatus.add(Constants.JOB_STATUS.JOB_EXEC_STATUS_UNKNOWN.getStatus());
        List<RemoteDisconRec> remoteDisconRecs =  remoteDisconRecService.getStatusList(non_excutionStatus);
        if(!remoteDisconRecs.isEmpty()){
            String odrId = "";
            JSONObject params = new JSONObject();
            Long jobId = -1L;

            for(RemoteDisconRec remoteDisconRec:remoteDisconRecs){
//                if("".equals(odrId)){
                    odrId = remoteDisconRec.getJobId();
                    RemoteProvisioningJob remoteProvisioningJob =  remoteProvisioningJobMapper.selectByPrimaryKey(Integer.valueOf(odrId));
                    if(null != remoteProvisioningJob && null != remoteProvisioningJob.getJobId()) {
                        jobId = Long.parseLong(remoteProvisioningJob.getJobId());
                        JSONArray array = new  JSONArray();
                        array.add(remoteDisconRec.getMeterId());
//                    }
//                }
//                if(remoteDisconRec.getJobId().equals(odrId) && -1 != jobId && array.size() > 0){
                    params.put("jobID",jobId);
                    params.put("deviceID",array);
                    String response = null;
                    try{
                        String url = sftpConfig.getImPath() + "getStatus";
                        response = doPostHttps(url,params.toJSONString(),sftpConfig);
                        if(response != null && !"".equals(response)){
                            Map<String,Object> map = JSONUtils.jsonToMap(response);
                            Long reJobId  = Long.parseLong(map.get("jobID")+"");
                            List<Map<String,Object>> list = (List<Map<String, Object>>) map.get("devices");
                            if(list != null && list.size() > 0){
                                for(int j=0;j<list.size();j++){
                                    Map<String,Object> result = list.get(j);
                                    String deviceId = (String) result.get("deviceID");
                                    RemoteDisconRec recFDB = remoteDisconRecService.queryByMeterId(deviceId,reJobId.toString());
                                    if(recFDB != null){
                                        String executionStatus = (String) result.get("executionStatus");
                                        logger.info("-----------disconnect deviceId="+deviceId+",jobId="+reJobId+",executionStatus="+executionStatus+"------------");
                                        if (executionStatus != null) {
                                            MeterInfo meterInfo = meterInfoMapper.findByMeterId(remoteDisconRec.getMeterId());
                                            if (executionStatus.equals(Constants.REMOTER_CTRL_STATUS.JOB_EXEC_STATUS_FAILURE.name())) {
                                                recFDB.setCtlStatus(Constants.REMOTER_CTRL_STATUS.JOB_EXEC_STATUS_FAILURE.getType());
                                                recFDB.setOperStatus(Constants.REMOTER_OPER_STATUS.COMPLETED.getType());
                                                //增加odp任务记录表
                                                ODPJobDetail odpJobDetail = new ODPJobDetail();
                                                odpJobDetail.setMacId(meterInfo.getMacId());
                                                odpJobDetail.setMeterId(recFDB.getMeterId());
                                                odpJobDetail.setOperatetime(new Date());

                                                odpJobDetailMapper.insert(odpJobDetail);
                                            } else if (executionStatus.equals(Constants.REMOTER_CTRL_STATUS.JOB_EXEC_STATUS_COMPLETE.name())) {
                                                recFDB.setCtlStatus(Constants.REMOTER_CTRL_STATUS.JOB_EXEC_STATUS_COMPLETE.getType());
                                                recFDB.setOperStatus(Constants.REMOTER_OPER_STATUS.COMPLETED.getType());

                                                meterInfo.setSwitchStatus(Constants.METER_CTRL_TYPE.REMOTE_DISCONNECTION.getType());
                                                meterInfoMapper.updateByPrimaryKey(meterInfo);
                                            } else if (executionStatus.equals(Constants.REMOTER_CTRL_STATUS.JOB_EXEC_STATUS_NOT_STARTED.name())) {
                                                recFDB.setCtlStatus(Constants.REMOTER_CTRL_STATUS.JOB_EXEC_STATUS_NOT_STARTED.getType());
                                            } else if (executionStatus.equals(Constants.REMOTER_CTRL_STATUS.JOB_EXEC_STATUS_RUNNING)) {
                                                recFDB.setCtlStatus(Constants.REMOTER_CTRL_STATUS.JOB_EXEC_STATUS_RUNNING.getType());
                                            } else if (executionStatus.equals(Constants.REMOTER_CTRL_STATUS.JOB_EXEC_STATUS_UNKNOWN.name())) {
                                                recFDB.setCtlStatus(Constants.REMOTER_CTRL_STATUS.JOB_EXEC_STATUS_UNKNOWN.getType());
                                                recFDB.setOperStatus(Constants.REMOTER_OPER_STATUS.COMPLETED.getType());
                                            }
                                        }
                                    }
                                    remoteDisconRecService.doUpdate(recFDB);
                                }
                            }
                        }
                    }catch (Exception e){
                        logger.error("getResponse failed:" + e.getMessage(),e);
                    }finally {
//                        array.clear();
                        params.clear();
                    }
                }else{
//                    array.add(remoteDisconRec.getMeterId());
                }
            }
        }

    }
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            logger.info("getRemoteDisconResult starts at:"+DateUtil.toStr(new Date(),DateUtil.YYYY_MM_DD_HH_MM_SS));
            getRemoteDisconResult();
            logger.info("getRemoteDisconResult ends at:"+DateUtil.toStr(new Date(),DateUtil.YYYY_MM_DD_HH_MM_SS));
        } catch (Exception e) {
            logger.error("getRemoteDisconResult failed"+e.getMessage(),e);
        }
        try {
            logger.info("autoMeterRemoteDiscon starts at:"+DateUtil.toStr(new Date(),DateUtil.YYYY_MM_DD_HH_MM_SS));
            autoMeterRemoteDiscon();
            logger.info("autoMeterRemoteDiscon ends at:"+DateUtil.toStr(new Date(),DateUtil.YYYY_MM_DD_HH_MM_SS));
        } catch (Exception e) {
            logger.error("autoMeterRemoteDiscon failed"+e.getMessage(),e);
        }
    }
}
