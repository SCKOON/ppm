package com.ppms.customerInfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.filter.xssfilter.TxtUtils;
import com.ppms.customerInfo.CustInfoManConstants;
import com.ppms.customerInfo.bean.EBSArrearsInfoBean;
import com.ppms.customerInfo.bean.TariffBean;
import com.ppms.customerInfo.generateCode.BarCodeUtil;
import com.ppms.customerInfo.service.CustomerInfoServiceI;
import com.ppms.customerInfo.vo.ClosingDetailVo;
import com.ppms.customerInfo.vo.ResultVo;
import com.ppms.dayendRecon.service.DayendReconServiceI;
import com.ppms.entity.*;
import com.ppms.tstypeQuery.service.TstypeServiceI;
import com.ppms.utils.*;
import com.ppms.vo.AClosingRecordVo;
import com.ppms.vo.CustomerInfoDTO;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.p3.core.util.oConvertUtils;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yadongliang on 2018/4/17 0017.
 */
@Controller
@RequestMapping("/customerInfoController")
public class CustomerInfoController extends BaseController {

    private static final Logger logger = Logger.getLogger(CustomerInfoController.class);

    @Autowired
    private CustomerInfoServiceI customerInfoService;
    @Autowired
    private DayendReconServiceI dayendReconServiceI;
    @Autowired
    private TstypeServiceI tstypeService;
    @Autowired
    private SystemService systemService;

    private static String middleStr = "****";

    /**
     * @description: Account Opening Page
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:22
     */
    @RequestMapping(params = "accountOpening")
    public ModelAndView accountOpening(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("ppms/customerManagement/account_opening");
        Date date = new Date();
        String spresetCredit = CustInfoManConstants.ACCOUNT_OPENING_TYPEGROUPCODE.PRESET_CREDIT.getTypeName();
        String semergencyCredit = CustInfoManConstants.ACCOUNT_OPENING_TYPEGROUPCODE.EMER_CREDIT_LIMIT.getTypeName();
        String slowCreditAlarm = CustInfoManConstants.ACCOUNT_OPENING_TYPEGROUPCODE.LOW_CREDIT_ALARM.getTypeName();
        String sarrearPercent = CustInfoManConstants.ACCOUNT_OPENING_TYPEGROUPCODE.DEFAULT_ARREAR_PCT.getTypeName();
        String presetCredit = dayendReconServiceI.getTypeCodeByGroupId(dayendReconServiceI.getGroupIdByGroupCode(spresetCredit));
        String emergencyCredit = dayendReconServiceI.getTypeCodeByGroupId(dayendReconServiceI.getGroupIdByGroupCode(semergencyCredit));
        String lowCreditAlarm = dayendReconServiceI.getTypeCodeByGroupId(dayendReconServiceI.getGroupIdByGroupCode(slowCreditAlarm));
        String arrearPct = dayendReconServiceI.getTypeCodeByGroupId(dayendReconServiceI.getGroupIdByGroupCode(sarrearPercent));

        modelAndView.addObject("presetCredit",presetCredit);
        modelAndView.addObject("emergencyCredit",emergencyCredit);
        modelAndView.addObject("lowCreditAlarm",lowCreditAlarm);
        modelAndView.addObject("arrearPct",arrearPct);

        //gst
        List list = customerInfoService.getGstCodeAndValue(date);
        String gstList = JSONObject.toJSONString(list);
        modelAndView.addObject("gstList",gstList);

        //tariff,应从数据库中查询可用tariffCode,根据code查询字典表中的name,存入list返回到前台
        List tarifCodeList = customerInfoService.getTarifCode(date);
        List generateTariffList = customerInfoService.generateTariffList(tarifCodeList);
        String tarifflist = JSONObject.toJSONString(generateTariffList);
        modelAndView.addObject("tarifflist",tarifflist);

        return modelAndView;
    }

    /**
     * @description: Account Activation Page
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:22
     */
    @RequestMapping(params = "accountActivation")
    public ModelAndView accountActivation(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("ppms/customerManagement/account_activation");
        String accountStatus = tstypeService.getGroupIdByGroupCode("ACCOUNT_STATUS");
        List<Map<String, Object>> accountStatusList = tstypeService.getTypeCodeAndNameByGroupId(accountStatus);
        String statusList = JSONObject.toJSONString(accountStatusList);
        modelAndView.addObject("statusList",statusList);
        return modelAndView;
    }

    /**
     * @description: Account Closing Page
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:22
     */
    @RequestMapping(params = "accountClosing")
    public ModelAndView accountClosing(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("ppms/customerManagement/account_closing");
        String accountStatus = tstypeService.getGroupIdByGroupCode("ACCOUNT_STATUS");
        List<Map<String, Object>> accountStatusList = tstypeService.getTypeCodeAndNameByGroupId(accountStatus);
        String statusList = JSONObject.toJSONString(accountStatusList);
        modelAndView.addObject("statusList",statusList);
        return modelAndView;
    }

    /**
     * @description: PAYU Account Opening Page
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:22
     */
    @RequestMapping(params = "payuOpening")
    public ModelAndView payuOpening(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("ppms/customerManagement/payu_opening");
        Date date = new Date();
        String spresetCredit = CustInfoManConstants.ACCOUNT_OPENING_TYPEGROUPCODE.PRESET_CREDIT.getTypeName();
        String semergencyCredit = CustInfoManConstants.ACCOUNT_OPENING_TYPEGROUPCODE.EMER_CREDIT_LIMIT.getTypeName();
        String slowCreditAlarm = CustInfoManConstants.ACCOUNT_OPENING_TYPEGROUPCODE.LOW_CREDIT_ALARM.getTypeName();
        String sarrearPercent = CustInfoManConstants.ACCOUNT_OPENING_TYPEGROUPCODE.DEFAULT_ARREAR_PCT.getTypeName();
        String presetCredit = dayendReconServiceI.getTypeCodeByGroupId(dayendReconServiceI.getGroupIdByGroupCode(spresetCredit));
        String emergencyCredit = dayendReconServiceI.getTypeCodeByGroupId(dayendReconServiceI.getGroupIdByGroupCode(semergencyCredit));
        String lowCreditAlarm = dayendReconServiceI.getTypeCodeByGroupId(dayendReconServiceI.getGroupIdByGroupCode(slowCreditAlarm));
        String arrearPct = dayendReconServiceI.getTypeCodeByGroupId(dayendReconServiceI.getGroupIdByGroupCode(sarrearPercent));

        modelAndView.addObject("presetCredit",presetCredit);
        modelAndView.addObject("emergencyCredit",emergencyCredit);
        modelAndView.addObject("lowCreditAlarm",lowCreditAlarm);
        modelAndView.addObject("arrearPct",arrearPct);

        //gst
        List list = customerInfoService.getGstCodeAndValue(date);
        String gstList = JSONObject.toJSONString(list);
        modelAndView.addObject("gstList",gstList);

        //tariff,应从数据库中查询可用tariffCode,根据code查询字典表中的name,存入list返回到前台
        List tarifCodeList = customerInfoService.getTarifCode(date);
        List generateTariffList = customerInfoService.generateTariffList(tarifCodeList);
        String tarifflist = JSONObject.toJSONString(generateTariffList);
        modelAndView.addObject("tarifflist",tarifflist);

        return modelAndView;
    }

    /**
     * @description: PAYU Account Conversion(Activation) Page
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:22
     */
    @RequestMapping(params = "payuConversion")
    public ModelAndView payuConversion(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("ppms/customerManagement/payu_conversion");
        String accountStatus = tstypeService.getGroupIdByGroupCode("ACCOUNT_STATUS");
        List<Map<String, Object>> accountStatusList = tstypeService.getTypeCodeAndNameByGroupId(accountStatus);
        String statusList = JSONObject.toJSONString(accountStatusList);
        modelAndView.addObject("statusList",statusList);
        return modelAndView;
    }

    /**
     * @description: Account Opening - save customer information
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:23
     */
    @RequestMapping(params = "saveCustomerInformation")
    @ResponseBody
    public AjaxJson saveCustomerInformation(CustomerInfoDTO customerInfoDTO, HttpServletRequest request) throws ParseException {
        AjaxJson j = new AjaxJson();
        String message = "";
        BigDecimal arrearPct = customerInfoDTO.getArrearPct();
        customerInfoDTO.setArrearPct(arrearPct.divide(new BigDecimal(100)));//前台接收20转为0.2入库
        CustomerInfoEntity databaseEntity = new CustomerInfoEntity();
        try {
            MyBeanUtils.copyBeanNotNull2Bean(customerInfoDTO, databaseEntity);
            message = customerInfoService.accountOpening(databaseEntity, request);
        } catch (Exception e) {
            logger.error(e);
        }
        j.setMsg(message);
        return j;
    }

    /**
     * @description: PAYU Account Opening - save customer information
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:23
     */
    @RequestMapping(params = "savePayuCustomerInformation")
    @ResponseBody
    public AjaxJson savePayuCustomerInformation(CustomerInfoDTO customerInfoDTO, HttpServletRequest request) throws ParseException {
        AjaxJson j = new AjaxJson();
        String message = "";
        BigDecimal arrearPct = customerInfoDTO.getArrearPct();
        customerInfoDTO.setArrearPct(arrearPct.divide(new BigDecimal(100)));//前台接收20转为0.2入库
        CustomerInfoEntity databaseEntity = new CustomerInfoEntity();
        try {
            MyBeanUtils.copyBeanNotNull2Bean(customerInfoDTO, databaseEntity);
            message = customerInfoService.payuAccountOpening(databaseEntity, request);
        } catch (Exception e) {
            logger.error(e);
        }
        j.setMsg(message);
        return j;
    }

    /**
     * @description: Account Opening - getCustomerInformation from EBS
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:23
     */
    @RequestMapping(params = "search")
    @ResponseBody
    public CustomerInfoDTO search(HttpServletRequest req) {
        String accountNumber = req.getParameter("accountNumber");
        CustomerInfoDTO dto = new CustomerInfoDTO();
        dto.setSucceed(true);
        try {
            CustomerInfoEntity customerInfoFromEBS = getCustomerInfoFromEBS(accountNumber);
            if (customerInfoFromEBS != null) {
                accountNumber += "P";
                customerInfoFromEBS.setAccNo(accountNumber);
                try {
                    MyBeanUtils.copyBeanNotNull2Bean(customerInfoFromEBS, dto);
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }catch (Exception e){
            dto.setSucceed(false);
            dto.setErrorMsg(e.getMessage());
        }

        return dto;
    }

    /**
     * @description: payusearch
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:23
     */
    @RequestMapping(params = "pauysearch")
    @ResponseBody
    public CustomerInfoDTO pauysearch(HttpServletRequest req) {
        return this.search(req);
    }

    /**
     * @description: get Customer Information from EBS
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:23
     */
    public CustomerInfoEntity getCustomerInfoFromEBS(String accNo) {
        SimpleDateFormat sdf = PersDateUtils.getSdf(PersDateUtils.FORMAT_DATE_TIME);
        Properties properties = PropertiesUtil.readProperties("ebs_interface_url.properties");
        String requestUrl = properties.getProperty("getCustomerInfoFromEBSUrl");
        String requestParamJson = JSONUtils.stringToJsonByFastjson("accountNo", accNo);
        try {
            String responseJsonStr = HttpClientUtils.httpRequest(requestUrl, "POST", requestParamJson);
            if(responseJsonStr != null && !responseJsonStr.equals("")){
                Map<String, Object> responseMap = JSONUtils.jsonToMap(responseJsonStr);
                if(responseMap!=null&&responseMap.size()>1){//新增判断,ERROR_MSG时size()=1
                    String returnCode = responseMap.get("RETURN_CODE").toString();
                    if(returnCode.equals("100")){//成功success
                        String salute = (String) responseMap.get("SALUTE");//Customer Salutation
                        String name = (String) responseMap.get("NAME");//Customer Full Name
                        String nric = (String) responseMap.get("NRIC");//Customer NRIC or Passport
                        String premAddr = (String) responseMap.get("PREM_ADDR");//Premise Address
                        String premType = (String) responseMap.get("PREM_TYPE");//Premise Type
                        String blkNo = (String) responseMap.get("BLK_NO");//Block Number
                        String street = (String) responseMap.get("STREET");//Street/Road name
                        String unitNo = (String) responseMap.get("UNIT_NO");//Unit Number
                        String postcode = (String) responseMap.get("POSTCODE");//Postal Code
                        String mailAddr = (String) responseMap.get("MAIL_ADDR");//Mailing Address
                        String emailAddr = (String) responseMap.get("EMAIL_ADDR");
                        String phone = (String) responseMap.get("PHONE");
                        String fax = (String) responseMap.get("FAX");
                        String mobile = (String) responseMap.get("MOBILE");

                        CustomerInfoEntity customerInfoEntity = new CustomerInfoEntity();
                        customerInfoEntity.setSalutation(salute);
                        customerInfoEntity.setName(name);
                        customerInfoEntity.setNric(nric);
                        customerInfoEntity.setAddress(premAddr);
                        customerInfoEntity.setPremiseType(premType);
                        customerInfoEntity.setBlockNumber(blkNo);
                        customerInfoEntity.setStreetName(street);
                        customerInfoEntity.setUnitNumber(unitNo);
                        customerInfoEntity.setPostalCode(postcode);
                        customerInfoEntity.setMailAddr(mailAddr);
                        customerInfoEntity.setEmailAddr(emailAddr);
                        customerInfoEntity.setTelephoneNumber(phone);
                        customerInfoEntity.setFaxNumber(fax);
                        customerInfoEntity.setMobileNumber(mobile);
                        logger.info("******Account Opening : get Customer Information from EBS Success*****");
                        return customerInfoEntity;
                    } else if (returnCode.equals("999")) {
                        String errorMsg = responseMap.get("ERROR_MSG").toString();
                        logger.info("******Account Opening : get Customer Information from EBS is Error*****" + errorMsg);
                        throw new RuntimeException(errorMsg);
                    }
                }else{
                    String errorMsg = "Account Opening : Internal Server Error.";
                    logger.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            } else {
                String errorMsg = "Account Opening : Internal Server Error.";
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        } catch (IOException e) {
            String errorMsg = "Account Opening : get Customer Information from EBS Failed.Connection failed due to timeout.";
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        return null;

    }

    /**
     * @description: Account Opeing - print card - get print information
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:23
     */
    @RequestMapping(params = "print")
    @ResponseBody
    public CustomerInfoDTO printDialog(HttpServletRequest request) {
        String accNo = request.getParameter("accNo");
        CustomerInfoEntity entity = customerInfoService.get(CustomerInfoEntity.class, accNo);
        CustomerInfoDTO dto = new CustomerInfoDTO();
        if(entity!=null){
            try {
                MyBeanUtils.copyBeanNotNull2Bean(entity, dto);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }
        return dto;
    }

    /**
     * @description: Account Opening - print card page
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:23
     */
    @RequestMapping(params = "printDialog")
    public ModelAndView printDialog(HttpServletRequest request, ModelMap map) throws Exception {
        String accNo = request.getParameter("accNo");
        map.addAttribute("customerName", oConvertUtils.getString(request.getParameter("name")));
        map.addAttribute("accNo", accNo);
        String realPath = request.getSession().getServletContext().getRealPath("/") + "webpage\\ppms\\customerManagement\\tempCodePng\\temp_code" + accNo + ".png";
        TxtUtils.file_filter(accNo);
        File file = new File(realPath);
        if (!file.exists()) {
            if (file.createNewFile())
                BarCodeUtil.generateFile(accNo, realPath);
        }
        return new ModelAndView("ppms/customerManagement/topupCard_print", map);
    }

    /*申请销户前校验该账户是否有未处理完成的销户流程*/
    @RequestMapping(params = "validForClosing")
    @ResponseBody
    public AjaxJson validForClosing(HttpServletRequest request) {
        AjaxJson json = new AjaxJson();
        json.setSuccess(true);
        List list = customerInfoService.queryClosingRec(request);
        if (list != null && list.size() > 0) {
            json.setSuccess(false);
            json.setMsg("The account is in the process of closing.");
        }
        return json;
    }

    /*查询EBS arrear信息*/
    @RequestMapping(params = "getEBSArrearInfo")
    @ResponseBody
    public EBSArrearsInfoBean getEBSArrearInfo(HttpServletRequest request) {
        EBSArrearsInfoBean ebsArrearsInfoBean = new EBSArrearsInfoBean();
        try {
            ebsArrearsInfoBean = customerInfoService.EBSArrearsInfo(request);
            ebsArrearsInfoBean.setSucceed(true);
        } catch (Exception e) {
            String message = e.getMessage();
            ebsArrearsInfoBean.setSucceed(false);
            ebsArrearsInfoBean.setErrormsg(message);
        }
        return ebsArrearsInfoBean;
    }

    /*销户申请查看EBS欠款信息*/
    @RequestMapping(params = "toEBSDetailView")
    @ResponseBody
    public ModelAndView toEBSDetailView(HttpServletRequest request) {
        EBSArrearsInfoBean ebsArrearsInfoBean = new EBSArrearsInfoBean();
        ebsArrearsInfoBean.setAccNo(request.getParameter("accNo"));
        ebsArrearsInfoBean.setOpenAmt(request.getParameter("openAmt"));
        ebsArrearsInfoBean.setOverdueAmt(request.getParameter("overdueAmt"));
        ebsArrearsInfoBean.setCreditAmt(request.getParameter("creditAmt"));
        ModelAndView mv = new ModelAndView("ppms/customerManagement/account_closing_ebsDetail");
        mv.addObject("ebsArrearsInfoBean", ebsArrearsInfoBean);
        return mv;
    }

    /**
     * @description: Account Closing - manual input data - page
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:24
     */
    @RequestMapping(params = "goManualDelete")
    public ModelAndView goManualDelete(HttpServletRequest request) {
        AClosingRecordEntity entity = customerInfoService.getClosingRecord(request);
        ModelAndView mv = new ModelAndView("ppms/customerManagement/account_closing_manual");
        mv.addObject("entity", entity);
        return mv;
    }

    /**
     * @description: 查看销户详情
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:24
     */
    @RequestMapping(params = "lookClosingDetail")
    @ResponseBody
    public ModelAndView lookClosingDetail(HttpServletRequest request){
        ClosingDetailVo entity = customerInfoService.lookClosingDetail(request);
        ModelAndView mv = new ModelAndView("ppms/customerManagement/account_closing_detail");
        mv.addObject("entity", entity);
        return mv;
    }

    /**
     * @description: Account Activation - update customer information page
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:24
     */
    @RequestMapping(params = "goUpdate")
    public ModelAndView goUpdate(CustomerInfoDTO customerInfoDTO, HttpServletRequest request) {
        if (StringUtil.isNotEmpty(customerInfoDTO.getAccNo())) {
            CustomerInfoEntity customerInfoEntity = customerInfoService.getEntity(CustomerInfoEntity.class, customerInfoDTO.getAccNo());
            try {
                MyBeanUtils.copyBeanNotNull2Bean(customerInfoEntity, customerInfoDTO);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            /*获取最新的计划激活日期: 当前日期 + 3个工作日*/
            List holidayList = customerInfoService.getHolidayList();
            Date minActivationDate = null;
            try {
                minActivationDate = HolidayUtils.getScheduleActiveDate(new Date(), holidayList, 3);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            customerInfoDTO.setMinActivationDate(minActivationDate);
            request.setAttribute("customerInfoPage", customerInfoDTO);
            AcctBalEntity acctBalEntity = customerInfoService.getEntity(AcctBalEntity.class, customerInfoDTO.getAccNo());
            BigDecimal balance = acctBalEntity.getBalance();
            DecimalFormat df = new DecimalFormat("0.00");//保留小数点后两位
            BigDecimal big3 = (balance.setScale(2, BigDecimal.ROUND_DOWN));
            double db3 = big3.doubleValue();
            String format = df.format(db3);
            BigDecimal balance2  = new BigDecimal(format);
            request.setAttribute("balance", balance2);
        }

        return new ModelAndView("ppms/customerManagement/customerInfo_update");
    }

    /**
     * @description: PAYU Account Conversion - update customer information page
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:24
     */
    @RequestMapping(params = "payuGoUpdate")
    @ResponseBody
    public ModelAndView payuGoUpdate(CustomerInfoDTO customerInfoDTO, HttpServletRequest request) {
        if (StringUtil.isNotEmpty(customerInfoDTO.getAccNo())) {
            CustomerInfoEntity customerInfoEntity = customerInfoService.getEntity(CustomerInfoEntity.class, customerInfoDTO.getAccNo());
            try {
                MyBeanUtils.copyBeanNotNull2Bean(customerInfoEntity, customerInfoDTO);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            /*获取最新的计划激活日期: 当前日期 + 3个工作日*/
            List holidayList = customerInfoService.getHolidayList();
            Date minActivationDate = null;
            try {
                minActivationDate = HolidayUtils.getScheduleActiveDate(new Date(), holidayList, 3);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            customerInfoDTO.setMinActivationDate(minActivationDate);
            request.setAttribute("customerInfoPage", customerInfoDTO);
            AcctBalEntity acctBalEntity = customerInfoService.getEntity(AcctBalEntity.class, customerInfoDTO.getAccNo());
            BigDecimal balance = acctBalEntity.getBalance();
            DecimalFormat df = new DecimalFormat("0.00");//保留小数点后两位
            BigDecimal big3 = (balance.setScale(2, BigDecimal.ROUND_DOWN));
            double db3 = big3.doubleValue();
            String format = df.format(db3);
            BigDecimal balance2  = new BigDecimal(format);
            request.setAttribute("balance", balance2);
        }

        return new ModelAndView("ppms/customerManagement/payu_customerInfo_update");
    }

    /**
     * @description: Account Activation - get customer information
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:24
     */
    @RequestMapping(params = "activationDatagrid")
    public void datagrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        if(!systemService.hasPrivilege(request.getSession().getId(), Constants.FUNCTION_URL.ACCOUNT_ACTIVATION.getStatus())){
            try {
                request.getRequestDispatcher("/webpage/common/401.htm").forward(request,response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            customerInfoService.queryCustomerInfoForActivation(request, dataGrid, null);
            TagUtil.datagrid(response, dataGrid);
        }
    }

    /**
     * Credit Transfer Apply - get customer information
     */
    @RequestMapping(params = "getCustomerInfoToCreditTransfer")
    public void getCustomerInfoToCreditTransfer(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        String accno = request.getParameter("accNo");
        try {
            customerInfoService.queryCustomerForCreditTransfer(accno,dataGrid);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * @description: PAYU Account Activation - get customer information
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:24
     */
    @RequestMapping(params = "payuActivationDatagrid")
    public void payuActivationDatagrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        customerInfoService.queryCustomerInfoForActivation(request, dataGrid, CustInfoManConstants.ACCOUNT_OPENING_TYPE.ACCOUNT_PAYU.getType());
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * @description: Account Closing - definedDatagrid
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:24
     */
    @RequestMapping(params = "definedDatagrid")
    public void definedDatagrid(ResultVo resultVo, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        DataReturn dataReturn = this.customerInfoService.getAllEntities(resultVo, dataGrid.getPage(), dataGrid.getRows(), request);
        dataGrid.setResults(dataReturn.getRows());
        dataGrid.setTotal((int) dataReturn.getTotal());
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * @description: Account Closing - query customer information
     * @auther: liangyadong
     * @date: 2018/9/30 0030 下午 5:04
     */
    @RequestMapping(params = "getCustomerInfo")
    public void getCustomerInfo(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        DataReturn dataReturn = this.customerInfoService.getCustomerInfo(dataGrid.getPage(), dataGrid.getRows(), request);
        dataGrid.setResults(dataReturn.getRows());
        dataGrid.setTotal((int) dataReturn.getTotal());
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * @description: Account Closing - closing apply - auto closing
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:24
     */
    @RequestMapping(params = "goApplyClosing")
    @ResponseBody
    public AjaxJson goApplyClosing(HttpServletRequest request) {
        String message = "";
        AjaxJson j = new AjaxJson();
        message = this.customerInfoService.goApplyClosing(request);
        j.setMsg(message);
        return j;
    }

    /**
     * @description: Account Closing - save manual input data
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:25
     */
    @RequestMapping(params = "doManualDelete")
    @ResponseBody
    public AjaxJson doManualDelete(AClosingRecordVo aClosingRecordVo, HttpServletRequest request) {
        String message = "";
        AjaxJson j = new AjaxJson();
        AClosingRecordEntity t = customerInfoService.getClosingRecord(request);
        String readingType = t.getReadingType();
        if(readingType.equals("03")){//校验readingtype是否为03,03则表示已经手动输入过了,不允许再次修改
            message = "Refused to modify as manual is completed.";
            j.setMsg(message);
            return j;
        }
        try {
            aClosingRecordVo.setUpdateTime(new Date());
            aClosingRecordVo.setReadingType("03");
            MyBeanUtils.copyBeanNotNull2Bean(aClosingRecordVo, t);
            customerInfoService.saveOrUpdate(t);
            message = CustInfoManConstants.ACCOUNT_CLOSING_APPLY_MESSAGE.ACCOUNT_CLOSING_MANUAL_SUCCESS.getMessage();
            j.setMsg(message);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            message = CustInfoManConstants.ACCOUNT_CLOSING_APPLY_MESSAGE.ACCOUNT_CLOSING_MANUAL_FAILED.getMessage();
            j.setMsg(message);
        }

        return j;
    }

    /**
     * @description: 激活页面-修改档案信息
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:25
     */
    @RequestMapping(params = "goUpdateActivationDate")
    @ResponseBody
    public AjaxJson goUpdateActivationDate(HttpServletRequest request) {
        String message = "";
        AjaxJson j = new AjaxJson();
        message = customerInfoService.goUpdateActivationDate(request);
        j.setMsg(message);
        return j;
    }

    /**
     * @description: payu激活页面-修改档案信息
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:25
     */
    @RequestMapping(params = "doUpdateCustomerEntity")
    @ResponseBody
    public AjaxJson doUpdateCustomerEntity(HttpServletRequest request) {
        String message = "";
        AjaxJson j = new AjaxJson();
        j.setSuccess(true);
        try {
            message = customerInfoService.doUpdateCreditBalance(request);
            j.setMsg(message);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            message = CustInfoManConstants.SAVE_ACCOUNT_INFO.SAVE_ACCOUNT_INFO_FAILED.getMessage();
            j.setMsg(message);
            j.setSuccess(false);
        }
        return j;
    }

    /**
     * @description: 根据tariffCode查询tariffrate
     * @auther: liangyadong
     * @date: 2018/9/29 0029 下午 4:25
     */
    @RequestMapping(params = "getTariffRateByCode")
    @ResponseBody
    public TariffBean getTariffRateByCode(HttpServletRequest request){
        String tariffCode = request.getParameter("tariffCode");
        String tariffRate = "";
        TariffBean tariffBean = new TariffBean();
        try{
            tariffRate = customerInfoService.getTariffInfoGroupByCode(new Date(), tariffCode);
            if(oConvertUtils.isNotEmpty(tariffRate)){
                tariffBean.setSucceed(true);
                tariffBean.setTariffRate(tariffRate);
            }else{
                tariffBean.setSucceed(false);
                tariffBean.setErrormsg("No valid TariffRate found, contact administrator to add valid TariffRate.");
            }
        }catch(Exception e){
            tariffBean.setSucceed(false);
            tariffBean.setErrormsg("System error : Get TariffRate failed.");
        }
        return  tariffBean;
    }

//    /*打印电卡*/
//    @RequestMapping(params = "topUpCardFee")
//    @ResponseBody
//    public AjaxJson topUpCardFee(HttpServletRequest request) {
//        AjaxJson json = new AjaxJson();
//        json.setSuccess(false);
//        String accno = request.getParameter("accNo");
//        //校验账户是否已在系统中存在, 并且是否已打印过
//        boolean isValid = customerInfoService.validPrintCardTimes(accno);
//        if(!isValid){
//            json.setMsg("Account does not exist or has been printed, refused to print.");
//            return json;
//        }
//
//        try {
//            String ip = GetClientIPUtil.getIpAddr(request);
//            logger.info("current ip:"+ip);
//            if (oConvertUtils.isNotEmpty(ip)) {
//                String ipv4Reg = "^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})$";
//                String ipv6Reg = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
//                if(match(ipv4Reg, ip) || match(ipv6Reg, ip)){
//                    ZebraCardPrinter zebraCardPrinter = null;
//                    DiscoveredPrinter[] printers = null;
//                    Connection connection = null;
//                    ZebraCardGraphics graphics = null;
//                    ByteArrayOutputStream baos = null;
//                    BufferedImage image = null;
//                    GraphicsInfo grInfo = null;
//                    //扫描打印机
//                    printers = UsbDiscoverer.getZebraUsbPrinters();
//                    if (printers.length > 0) {
//                        logger.info("通过usb扫描到打印机");
//                        connection = printers[0].getConnection();
//                        connection.open();
//                        zebraCardPrinter = ZebraCardPrinterFactory.getInstance(connection);
//                    }
//                    if (zebraCardPrinter == null) {
//                        logger.info("未扫描到通过usb连接的打印机...");
//                        json.setMsg("Not scanned to a valid printer connected via usb");
//                        return json;
//                    }
//
//                    //生成条形码图片信息
//                    List<GraphicsInfo> graphicsData = getGraphicsInfos(request, accno, zebraCardPrinter);
//
//                    //打印条形码
//                    int jobId = zebraCardPrinter.print(1, graphicsData);
//
//                    pollJobStatus(zebraCardPrinter, jobId);//自定义方法-轮询job状态
//                    JobStatusInfo jStatus = zebraCardPrinter.getJobStatus(jobId);
//                    logger.info("JobStatusInfo:"+jStatus);
//                    json.setMsg("JobStatusInfo"+jStatus.toString());
//
//                    //保存电卡打印次数
//                    customerInfoService.updatePrintCardTimes(accno);
//                    json.setSuccess(true);
//                }else{
//                    json.setMsg("current counter is not in the ppms system!");
//                }
//            }
//        } catch (Exception e) {
//            logger.info("未扫描到通过usb连接的打印机...");
//            json.setMsg("Not scanned to a valid printer connected via usb");
//        }
//        return json;
//    }

//    private List<GraphicsInfo> getGraphicsInfos(HttpServletRequest request, String accno, ZebraCardPrinter zebraCardPrinter) throws ConnectionException, ZebraCardException, IOException {
//        ZebraCardGraphics graphics;
//        GraphicsInfo grInfo;
//        BufferedImage image;
//        ByteArrayOutputStream baos;
//        List<GraphicsInfo> graphicsData = new ArrayList<GraphicsInfo>();
//        graphics = new ZebraCardGraphics(zebraCardPrinter);
//        graphics.initialize(0, 0, OrientationType.Landscape, PrintType.MonoK, Color.WHITE);
//
//        grInfo = new GraphicsInfo();
//        grInfo.side = CardSide.Front;
//        grInfo.printType = PrintType.MonoK;
//        grInfo.graphicType = GraphicType.BMP;
//
//        //获取BufferedImage对象
//        String filepath = request.getSession().getServletContext().getRealPath("/") + "webpage\\ppms\\customerManagement\\tempCodePng\\temp_code" + accno + ".png";
//        image = ImageIO.read(new File(filepath));
//        baos = new ByteArrayOutputStream();
//        ImageIO.write(image, "bmp", baos);
//
//        //设置image参数x,y,w,h
//        Properties properties = PropertiesUtil.readProperties("graphicsPosition.properties");
//        String x = properties.getProperty("barcode.x");
//        String y = properties.getProperty("barcode.y");
//        String w = properties.getProperty("barcode.w");
//        String h = properties.getProperty("barcode.h");
//        Integer ix = Integer.valueOf(x);
//        Integer iy = Integer.valueOf(y);
//        Integer iw = Integer.valueOf(w);
//        Integer ih = Integer.valueOf(h);
//
//        graphics.drawImage(baos.toByteArray(), ix, iy, iw, ih, RotationType.RotateNoneFlipNone);//drawImage(byte[] imageData, int x, int y, int width, int height, RotationType rotation)
//        grInfo.graphicData = graphics.createImage(null);
//        graphics.clear();
//        graphicsData.add(grInfo);
//
//        return graphicsData;
//    }

//    public static boolean pollJobStatus(ZebraCardPrinter device, int actionID) throws ConnectionException, ZebraCardException, ZebraIllegalArgumentException {
//        boolean success = false;
//        long dropDeadTime = System.currentTimeMillis() + 40000;
//        long pollInterval = 500;
//        JobStatusInfo jStatus = null;
//        do {
//            jStatus = device.getJobStatus(actionID);
//            if (jStatus.contactSmartCard.contains("station")) {
//                success = true;
//                break;
//            } else if (jStatus.contactlessSmartCard.contains("station")) {
//                success = true;
//                break;
//            } else if (jStatus.printStatus.contains("done_ok")) {
//                success = true;
//                break;
//            } else if (jStatus.printStatus.contains("alarm_handling")) {
//                success = false;
//            } else if (jStatus.printStatus.contains("error") || jStatus.printStatus.contains("cancelled")) {
//                success = false;
//                break;
//            }
//            if (System.currentTimeMillis() > dropDeadTime) {
//                success = false;
//                break;
//            }
//            try {
//                Thread.sleep(pollInterval);
//            } catch (InterruptedException e) {
//                logger.error(e.getMessage(),e);
//            }
//
//        } while (true);
//
//        return success;
//    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
