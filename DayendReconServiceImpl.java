package com.ppms.dayendRecon.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.constants.DayendConstants;
import com.constants.SiteStatus;
import com.ppms.creditTopup.bean.TopupConstants;
import com.ppms.creditTopup.dao.TopupDao;
import com.ppms.dayendRecon.ServiceCheck;
import com.ppms.dayendRecon.dao.DayendReconDao;
import com.ppms.dayendRecon.service.DayendReconServiceI;
import com.ppms.entity.*;
import com.ppms.utils.DataSourceValue;
import com.ppms.utils.JSONUtils;
import com.ppms.vo.SpTopupRecordVo;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.extend.datasource.DataSourceType;
import org.jeecgframework.core.util.DateUtils;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.web.system.pojo.base.TSType;
import org.jeecgframework.web.system.pojo.base.TSTypegroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by yadongliang on 2018/5/29 0029.
 */
@Service("dayendReconService")
@Transactional(isolation = Isolation.REPEATABLE_READ)
@DataSourceValue(DataSourceType.dataSource_ppms)
public class DayendReconServiceImpl extends CommonServiceImpl implements DayendReconServiceI {

    private static final Logger logger = Logger.getLogger(DayendReconServiceImpl.class);

    @Autowired
    private DayendReconDao dayendReconDao;
    @Autowired
    private TopupDao topupDao;

    private final String batchNoSuffix = "01";

    //查询字典表
    @Override
    @DataSourceValue(DataSourceType.dataSource_jeecg)
    public List getTstypeList(CriteriaQuery cq) {
        return super.getListByCriteriaQuery(cq,false);
    }

    @Override
    @DataSourceValue(DataSourceType.dataSource_jeecg)
    public List getTstypeGroupList(CriteriaQuery cq) {
        return super.getListByCriteriaQuery(cq,false);
    }

    //cardFee
    @Override
    public JSONObject getTotalCardFeeInfo(HttpServletRequest request, HttpServletResponse response, int page, int rows, String curLoginName) {
        Map<String, Object> returnMap = dayendReconDao.getTotalCardFeeInfo(request, response, page, rows, curLoginName);
        String jsonStr = JSONUtils.beanToJson(returnMap);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);

        return jsonObject;
    }

    //refund
//    @Override
//    public JSONObject getTotalRefundInfo(HttpServletRequest request, HttpServletResponse response, int page, int rows, String curLoginName) {
//        Map<String, Object> returnMap = dayendReconDao.getTotalRefundInfo(request, response, page, rows, curLoginName);
//        String jsonStr = JSONUtils.beanToJson(returnMap);
//        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
//
//        return jsonObject;
//    }

    //topup
    @Override
    public JSONObject getTotalTopupInfo(HttpServletRequest request, HttpServletResponse response, int page, int rows, String curLoginName) {
        Map<String, Object> returnMap = dayendReconDao.getTotalTopupInfo(request, response, page, rows, curLoginName);
        String jsonStr = JSONUtils.beanToJson(returnMap);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);

        return jsonObject;
    }

    //reverse
    @Override
    public JSONObject getTotalReverseInfo(HttpServletRequest request, HttpServletResponse response, int page, int rows, String curLoginName) {
        Map<String, Object> returnMap = dayendReconDao.getTotalReverseInfo(request, response, page, rows, curLoginName);
        String jsonStr = JSONUtils.beanToJson(returnMap);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);

        return jsonObject;
    }

    //四种交易类型汇总 all
    @Override
    public JSONObject getDayendAll(HttpServletRequest request, HttpServletResponse response, int page, int rows, String curLoginName) {
        Map<String, Object> returnMap = dayendReconDao.getDayendAll(request, response, page, rows, curLoginName);
        String jsonStr = JSONUtils.beanToJson(returnMap);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);

        return jsonObject;
    }

    //detail-tab2
    @Override
    public JSONObject getDayendDetail(HttpServletRequest request, HttpServletResponse response, int page, int rows, String curLoginName) {
        Map<String, Object> returnMap = dayendReconDao.getDayendDetail(request, response, page, rows, curLoginName);
        String jsonStr = JSONUtils.beanToJson(returnMap);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);

        return jsonObject;
    }

    /**
     * @description: DAYEND UPDATE RECONSTATUS
     * @auther: liangyadong
     * @date: 2018/10/23 0023 上午 11:05
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String doUpdateReconStatus(HttpServletRequest request, String curLoginName) {
        String message = "";
        try{
            //1.sp_topup_rec
            List<SpTopUpRecEntity> topupRecordList = dayendReconDao.getTopupRecordList(request, curLoginName);
            if(topupRecordList != null && topupRecordList.size() > 0){
                for(SpTopUpRecEntity topupRecordEntity : topupRecordList){
                    topupRecordEntity.setReconStatus(TopupConstants.RECON_STATUS.RECON_SUCCESS.getReconStatus());
                    super.save(topupRecordEntity);
                }
            }
            //2.refund_rec
//            List<RefundRecordEntity> refundRecordList = dayendReconDao.getRefundRecordList(request, curLoginName);
//            if(topupRecordList != null && topupRecordList.size() > 0){
//                for(RefundRecordEntity refundRecordEntity : refundRecordList){
//                    refundRecordEntity.setReconStatus(TopupConstants.RECON_STATUS.RECON_SUCCESS.getReconStatus());
//                    super.save(refundRecordEntity);
//                }
//            }
            message = DayendConstants.RECON_RESULT_MESSAGE.RECON_SUCCESS.getMessage();
        } catch(Exception e){
            logger.error(e);
            message = DayendConstants.RECON_RESULT_MESSAGE.RECON_FAILED.getMessage();
            return message;
        }

        return message;
    }

    /**
     * A.根据日期和当前登录人用户名查询counterCodeList
     */
    @Override
    public List<Map<String, String>> getCounterCodeList(String date, String cashierName) {
        List<Map<String, String>> counterList = new ArrayList();
        //查询sp_topup_rec中counterList
        List counterCodeList = dayendReconDao.getCounterCodeList(date, cashierName);
        if(counterCodeList!=null&&counterCodeList.size()>0){
            for(int i = 0; i < counterCodeList.size(); i++){
                Map map = new HashMap();
                String counterCode = counterCodeList.get(i).toString();
                String counterName = getCounterNameByCode(counterCode);
                map.put("code",counterCode);
                map.put("name",counterName);
                counterList.add(map);
            }
        }

        return counterList;
    }
    /**
     * a.根据countercode查询countername
     */
    public String getCounterNameByCode(String counterCode){
        CriteriaQuery cq = new CriteriaQuery(CounterEntity.class);
        cq.eq("status", SiteStatus.ACTIVE.getStatus());
        cq.eq("code", counterCode);
        cq.add();
        List<CounterEntity> counterEntityList = this.getListByCriteriaQuery(cq, false);
        String counterName = "";
        if(counterEntityList!=null&&counterEntityList.size()>0){
            CounterEntity counterEntity = counterEntityList.get(0);
            counterName = counterEntity.getName();
        }
        return counterName;
    }

    /**
     * B.根据clientIp查询所在center,返回centercode和centername对应关系
     */
    @Override
    public List<Map<String, String>> getCenterCodeList(String clientIp) {
        List<Map<String, String>> centerList = new ArrayList();
        Map map = new HashMap();

        CounterEntity counterEntity = topupDao.getCounterByIP(clientIp);
        if(counterEntity!=null){
            int terminalId = counterEntity.getTerminalEntity().getId();
            TerminalEntity terminalEntity = topupDao.getTerminalInfo(terminalId);
            String terminalCode = terminalEntity.getCode();
            String terminalName = terminalEntity.getName();
            map.put("code",terminalCode);
            map.put("name",terminalName);
            centerList.add(map);
        }

        return centerList;
    }

    /**
     * 获取center信息
     */
    public List<Map<String, String>> generateCenterList(HttpServletRequest request) {
        Object terminalId = request.getSession().getAttribute("terminalId");
        List<Map<String, String>> centerList = new ArrayList<>();
        TerminalEntity terminalEntity = null;
        if(oConvertUtils.isNotEmpty(terminalId)){
            int tmnlId = Integer.parseInt(terminalId.toString());
            terminalEntity = this.getEntity(TerminalEntity.class, tmnlId);
        }
        if(terminalEntity!=null){
            Map<String, String> map = new HashMap();
            String tmnlcode = terminalEntity.getCode();
            String tmnlname = terminalEntity.getName();
            map.put("code",tmnlcode);
            map.put("name",tmnlname);
            centerList.add(map);
        }

        return centerList;
    }

    /**
     * 根据terminalid查询其下的所有counter
     */
    @Override
    public List<Map<String, String>> getCounterList(HttpServletRequest request) {
        Object terminalId = request.getSession().getAttribute("terminalId");
        List<Map<String, String>> counterlist = new ArrayList<>();
        if(oConvertUtils.isNotEmpty(terminalId)){
            int tmnlId = Integer.parseInt(terminalId.toString());
            List<CounterEntity> list = this.dayendReconDao.getcounterListByTmnlId(tmnlId);
            if(list!=null&&list.size()>0){
                for(CounterEntity counterEntity : list){
                    Map<String, String> map = new HashMap();
                    String countercode = counterEntity.getCode();
                    String countername = counterEntity.getName();
                    map.put("code",countercode);
                    map.put("name",countername);
                    counterlist.add(map);
                }
            }
        }

        return counterlist;
    }

    /**
     * 根据groupName查询groupId
     */
    @Override
    @DataSourceValue(DataSourceType.dataSource_jeecg)
    public String getGroupIdByGroupCode(String typegroupcode){
        String typeGroupId = "";
        CriteriaQuery criteriaQuery = new CriteriaQuery(TSTypegroup.class);
        criteriaQuery.eq("typegroupcode",typegroupcode);
        criteriaQuery.add();
        List<TSTypegroup> tsTypegroupList = this.getTstypeGroupList(criteriaQuery);
        if (tsTypegroupList!=null&&tsTypegroupList.size()>0){
            TSTypegroup tsTypegroup = tsTypegroupList.get(0);
            typeGroupId = tsTypegroup.getId();
        }

        return typeGroupId;
    }

    /**
     * 根据groupId查询typecode(单个)
     */
    @Override
    @DataSourceValue(DataSourceType.dataSource_jeecg)
    public String getTypeCodeByGroupId(String typeGroupId){
        CriteriaQuery criteriaQuery = new CriteriaQuery(TSType.class);
        criteriaQuery.eq("TSTypegroup.id", typeGroupId);
        criteriaQuery.add();
        String typecode = "";
        List<TSType> tsTypeList = this.getTstypeList(criteriaQuery);
        if(tsTypeList!=null&&tsTypeList.size()>0){
            TSType tsType = tsTypeList.get(0);
            typecode = tsType.getTypecode();
        }
        return typecode;
    }

    /**
     * @description: 根据refNo查询sptopuprec或refundrec
     * @auther: liangyadong
     * @date: 2018/10/9 0009 下午 6:49
     */
    @Override
    public SpTopupRecordVo updatePayMode(HttpServletRequest request) {
        String refNo = request.getParameter("refNo");
        String id = request.getParameter("id");
        SpTopupRecordVo spTopupRecordVo = null;
        if(StringUtil.isNotEmpty(refNo)&&StringUtil.isNotEmpty(id)){
            //查询sptopup表
            spTopupRecordVo = dayendReconDao.getSptopupRecord(request);
        }
//        else if(StringUtil.isEmpty(refNo)&&StringUtil.isNotEmpty(id)){
//            //查询refund表
//            spTopupRecordVo = dayendReconDao.getRefundRecord(request);
//        }

        return spTopupRecordVo;
    }

    /**
     * @description: 执行修改paymode
     * @auther: liangyadong
     * @date: 2018/10/10 0010 下午 3:25
     */
    @Override
    public String doUpdatePaymode(HttpServletRequest request) {
        String message = "";
        String refNo = request.getParameter("refNo");
        String sid = request.getParameter("id");
        String payMode = request.getParameter("payMode");
        if(StringUtil.isNotEmpty(refNo)&&StringUtil.isNotEmpty(sid)&&StringUtil.isNotEmpty(payMode)){
            //根据id修改sptopup表
            Integer id = Integer.valueOf(sid);
            SpTopUpRecEntity spTopUpRecEntity = this.getEntity(SpTopUpRecEntity.class, id);
            spTopUpRecEntity.setPayMode(payMode);
            save(spTopUpRecEntity);
            message = "Payment Mode update success.";
        }
//        else if(StringUtil.isEmpty(refNo)&&StringUtil.isNotEmpty(sid)&&StringUtil.isNotEmpty(payMode)){
//            //根据id修改refund表
//            Integer id = Integer.valueOf(sid);
//            RefundRecordEntity refundRecordEntity = this.getEntity(RefundRecordEntity.class, id);
//            refundRecordEntity.setPayMode(payMode);
//            save(refundRecordEntity);
//            message = "Payment Mode update failed.";
//        }

        return message;
    }

    //经理settlement前查询该网点是否有未对账记录
    @Override
    public Map<String, Object> getDayendAllSettlement(HttpServletRequest request, HttpServletResponse response, int page, int rows, String curLoginName) {
        return dayendReconDao.getDayendAll(request, response, page, rows, curLoginName);
    }

    //查询DAY_END_RECON表,校验是否已经执行过settle,禁止重复settle
    @Override
    public boolean checkSettleStatus(String dayendDate, String terminal, String counter, String cashierId, String operId) {
        String[] strings = dayendDate.split("-");
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < strings.length; i++){
            sb. append(strings[i]);
        }
        String batch = sb.toString();
        String  batchNo = batch + batchNoSuffix;
        List settleList = dayendReconDao.checkSettleStatus(batchNo, terminal, counter, cashierId, operId);
        if(settleList!=null&&settleList.size()>0){
            return true;//执行过settle
        }
        return false;//未执行过settle
    }

    //执行settlement时,查询DAY_END_RECON表所需数据
    public List<DayendReconEntity> getDayendreconList(HttpServletRequest request, String curLoginName){
        return dayendReconDao.getDayendreconList(request, curLoginName);
    }

    //SETTLEMENT 1.职员 DAY_END_RECON插入记录
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackForClassName = {"Exception.class"})
    public String insertDayendRecon(HttpServletRequest request, String curLoginName) {
        String message = null;
        String centerCode = request.getParameter("terminal");
        String counterCode = request.getParameter("counter");
        Date txnDate = new Date();
        String strDate = DateUtils.formatDate(txnDate, Constants.SIMPLE_YYYY_MM_DD);
        String batchNo = strDate + batchNoSuffix;
        List<DayendReconEntity> dayendreconList = getDayendreconList(request, curLoginName);
        if(dayendreconList!=null&&dayendreconList.size()>0){
            for(DayendReconEntity dayendReconEntity : dayendreconList){
                dayendReconEntity.setSettleDate(txnDate);
                dayendReconEntity.setSettleType(DayendConstants.SETTLE_TYPE.COUNTER.getSettletype());
                dayendReconEntity.setBatchNo(batchNo);
                dayendReconEntity.setTmnlCode(centerCode);
                dayendReconEntity.setCounterId(counterCode);
                dayendReconEntity.setCashierId(curLoginName);
                super.save(dayendReconEntity);
            }
        }

        message = "Settlement Success.";
        return message;
    }
    //SETTLEMENT 2.经理
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackForClassName = {"Exception.class"})
    public String serviceSettlement(HttpServletRequest request, String curLoginName) {
        String message = null;
        Date txnDate = new Date();
        String strDate = DateUtils.formatDate(txnDate, Constants.SIMPLE_YYYY_MM_DD);
        String batchNo = strDate + "01";
        String centerCode = request.getParameter("terminal");
        String centerName = getCenterName(centerCode);

        try{
            //1.DAY_END_RECON插入记录
            //TODO查询条件只设置date,center
            List<DayendReconEntity> dayendreconList = getDayendreconList(request, null);
            if(dayendreconList!=null&&dayendreconList.size()>0){
                for(DayendReconEntity dayendReconEntity : dayendreconList){
                    dayendReconEntity.setSettleDate(txnDate);
                    dayendReconEntity.setSettleType(DayendConstants.SETTLE_TYPE.SERVICE.getSettletype());
                    dayendReconEntity.setBatchNo(batchNo);
                    dayendReconEntity.setTmnlCode(centerCode);
                    dayendReconEntity.setOperId(curLoginName);
                    super.save(dayendReconEntity);
                }
            }
            //2.SETTELMENT_INFO插入记录,同时判断C1C2是否都为02,如果是02则更新RECON_STATUS字段为02-全部已对账
            //根据批次号查询SETTELMENT_INFO表中是否有记录,有则更新,没有则插入记录
            SettlementInfoEntityEntity entity = dayendReconDao.getSettleInfoEntityByBatchNo(batchNo);
            if(entity!=null){
                if(ServiceCheck.isC1(centerName)){//MLT=C1
                    entity.setC1ReconStatus(DayendConstants.C_RECON_STATUS.RECONED.getCrReconStatus());
                    if(entity.getC2ReconStatus().equals(DayendConstants.C_RECON_STATUS.RECONED.getCrReconStatus())){
                        entity.setReconStatus(DayendConstants.RECON_STATUS.RECONED.getReconStatus());
                    }
                }else if(ServiceCheck.isC2(centerName)){//HDB=C2
                    entity.setC2ReconStatus(DayendConstants.C_RECON_STATUS.RECONED.getCrReconStatus());
                    if(entity.getC1ReconStatus().equals(DayendConstants.C_RECON_STATUS.RECONED.getCrReconStatus())){
                        entity.setReconStatus(DayendConstants.RECON_STATUS.RECONED.getReconStatus());
                    }
                }
                super.save(entity);
            }else{
                SettlementInfoEntityEntity entity0 = new SettlementInfoEntityEntity();
                entity0.setBatchNo(batchNo);
                entity0.setSettleDate(txnDate);
                entity0.setReconStatus(DayendConstants.RECON_STATUS.RECONING.getReconStatus());
                if(ServiceCheck.isC1(centerName)){//MLT=C1
                    entity0.setC1ReconStatus(DayendConstants.C_RECON_STATUS.RECONED.getCrReconStatus());
                    entity0.setC2ReconStatus(DayendConstants.C_RECON_STATUS.UNRECON.getCrReconStatus());
                }else if(ServiceCheck.isC2(centerName)){//HDB=C2
                    entity0.setC2ReconStatus(DayendConstants.C_RECON_STATUS.RECONED.getCrReconStatus());
                    entity0.setC1ReconStatus(DayendConstants.C_RECON_STATUS.UNRECON.getCrReconStatus());
                }
                super.save(entity0);
            }
        }catch (Exception e){
            throw new RuntimeException("Dayend Recon: Settlement failed."+e);
        }

        message = "Settlement Success.";
        return message;
    }

    //根据centerCode查询centerName
    public String getCenterName(String centerCode){
        String centerName = "";
        TerminalEntity entity = dayendReconDao.getTerminalEntityByCenterCode(centerCode);
        if(entity!=null){
            centerName = entity.getName();
        }
        return centerName;
    }

}
