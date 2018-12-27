package com.constants;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: Han Yu
 * @Creation: 2018/8/28 9:45
 */
public class Constants {

    /*public static final String HH_MM_SS = "HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY = "yyyy";
    public static final String MM = "MM";
    public static final String DD = "dd";
    public static final String HH = "HH";
    public static final String MI = "mm";
    public static final String SS = "ss";
    public static final String YYYY_MM_DD_T_HH_MM_SS_z = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String YYYY_MM_DD_T_HH_MM_SS_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";*/
    public static final String SIMPLE_YYYY_MM_DD = "yyyyMMdd";
    public static final String DD_MM_YYYY_HH_MM_SS_DOT = "dd.MM.yyyy HH:mm:ss";
    public static final String DD_MM_YYYY_DOT = "dd.MM.yyyy";
    public static final String SIMPLE_YYYY_MM_DD_HH_MM_SS_SSS = "yyyyMMddHHmmssSSS";

    //全局的日期格式 方便以后替换
    public static final String GLOBAL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String GLOBAL_DATETIME_FORMAT = "yyyy-MM-dd hh:mm:ss";

    public static final String SMS_PARAMS_PREFIX = "\\{";

    public static final String SMS_PARMS_SUFFIX = "\\}";

    public static final int AMT_DIGITS = 2;

    public static final String COMMA = ",";

    public static final BigDecimal ZERO = new BigDecimal(0);

    public static final String RETURN_STATUS = "SUCCESS";
    public static final String ERROR_MSG = "ERROR_MSG";
    //add by yangyong 20181203 start reason:数据字典查询paymode的标识
    public static final String PAY_MODE_TYPEGROUPCODE = "PAY_MODE";
    public static final String PAY_MODE_TYPEGROUPNAME = "PAYMENT_MODE";
    //add by yangyong 20181203 end   reason:数据字典查询paymode的标识

    //第一次打印电卡费用
    public static final String INITIAL_CARD_FEE = "0";

    //余额转移状态
    public static final String TRANSFER_PENDING = "01";
    public static final String TRANSFER_APPROVE = "02";
    public static final String TRANSFER_DECLINE = "03";

    //余额转移报表查询，类型 01代表按天查询，02代表按月份查询
    public static final String QUERYTYPE_DAILY = "01";
    public static final String QUERYTYPE_MONTHLY = "02";

    //余额转移记录报表明细查询，区分是查询pending，approved，declined的明细记录
    public static final String QUERYSTATUS_PENDING = "01";
    public static final String QUERYSTATUS_APPROVED = "02";
    public static final String QUERYSTATUS_DECLINED = "03";

    //远程跳合闸类型
    public static final String REMOTE_CONTROL_TYPE_DISCON = "1";
    public static final String REMOTE_CONTROL_TYPE_RECON = "2";

    //SP_TOP_UP_REC txn_status
    public static final String TOP_UP = "01";
    public static final String REVERSED= "02";//已冲正
    public static final String REVERSE = "03";
    public static final String RECONCILIATION_IN= "04";//对账补入
    public static final String RECONCILIATION_DELETE = "05";//对账补入
    public static final String CARD_FEE = "06";
    public static final String ACCOUNT_OPENING = "07";
    public static final String ACCOUNT_RE_OPENING = "08";
    public static final String ISSUE_CARD = "09";

    //top-up card type
    public static final String TOPUPCARD_NEW = "01";
    public static final String TOPUPCARD_REPLACEMENT = "02";

    public static final String TOPUPCARD_TYPE_NEW = "New";
    public static final String TOPUPCARD_TYPE_REPLACEMENT = "Replacement";

    public static final String NOAUTH = "No Auth.";

    public enum SMS_GENERATION_FAILURE_STATUS{
        NULL_IMPORT_PARM("-1","The account number|smsType|smsParameter is null.."),
        NOT_EXIST("-2","The customer does not exist in PPMS."),
        FAILED_GENERATION("-3","Failed to generate SMS content based on SMS template"),
        NULL_MOBILE_NO("-4","The customer's SMS number is empty and PPMS will not send SMS"),
        FAILED_SMS_REC_INSERTION("-5","inserting the sms record fails"),
        OTHER_ERROR("-10","Others errors.");

        private String code;
        private String description;

        SMS_GENERATION_FAILURE_STATUS(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

    }

    public enum SMS_TEMPLATE_PARAMS{
        ACCOUNT_NO("AccountNo"),
        OPEN_DATE("OpenDate"),
        ACTIVATION_DATE("ActivationDate"),
        CREDIT_BALANCE("CreditBalance"),
        TRANSFER_AMT("TransferAmount"),
        SRC_ACCOUNT_NO("SourceAccountNo"),
        TGT_ACCOUNT_NO("TargetAccountNo"),
        SRC_ACCOUNT_NO_BALANCE("SrcAcctNoBalance"),
        TGT_ACCOUNT_NO_BALANCE("TgtAcctNoBalance"),
        TOPUP_AMT("TopupAmt"),
        TOPUP_DATE("TopupDate"),
        REVERSE_AMT("ReverseAmt"),
        REVERSE_DATE("ReverseDate"),
        EMERGENCY_CREDIT("EmergencyCredit"),
        DEDUCTION_AMT("DeductionAmt"),
        DUDUCTION_DATE("DeductionDate"),
        METER_NO("MeterNo"),
        RECONNECT_DATE("ReconnectDate"),
        CLOSING_DATE("CloseDate");

        public String getValue() {
            return value;
        }

        SMS_TEMPLATE_PARAMS(String value) {
            this.value = value;
        }

        private String value;

    }

    /**
     SMS_TYPE
     01 - Account Opening
     02 - Account Activation
     03 - Credit Transferring Out
     04 - Credit Transferring Received
     05 - Cancel Credit Transferring
     06 - Top-up
     07 - Reverse
     08 - Emergency Credit Alert
     09 - Credit Balance Deduction
     10 - Remote Reconnection
     11 - Low Credit Alert
     12 - Remote Disconnection
     13 - Account Closing
     **/
    public enum SMS_TYPE{
        ACCOUNT_OPENING("01"),
        ACCOUNT_ACTIVATION("02"),
        CREDIT_TRANSFERRING_OUT("03"),
        CREDIT_TRANSFERRING_RECEIVED("04"),
        CANCEL_CREDIT_TRANSFERRING("05"),
        TOPUP("06"),
        REVERSE("07"),
        EMERGENCY_CREDIT_ALERT("08"),
        CREDIT_BALANCE_DEDUCT("09"),
        REMOTE_RECONNECTION("10"),
        LOW_CREDIT_ALERT("11"),
        REMOTE_DISCONNECTION("12"),
        ACCOUNT_CLOSING("13");


        private String type;

        private SMS_TYPE(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    /**
     01: 新开户
     02: PAYU
     03: 充值
     04: 电费扣减
     05: 电费调整
     06: 冲正
     07: 退款
     08: 余额转出
     09: 余额转入
     10: 对账补入
     11: 对账删除
     12: 换表电费结算
     13. 销户
     14. 发行电卡
     15. 重新开户
     */
    public enum BALANCE_UPDATED_FLAG{
        NEW_ACCOUNT_OPENING("01"),
        PAYU_ACCOUNT_OPENING("02"),
        TOPUP("03"),
        CHARGES_DEDUCTION("04"),
        CHARGES_ADJUSTION("05"),
        REVERSE("06"),
        REFUND("07"),
        CREDIT_TRANSFER_OUT("08"),
        CREDIT_TRANSFER_IN("09"),
        RECON_ADD("10"),
        RECON_DELETE("11"),
        FAULTY_METER_REPLACEMENT("12"),
        ACCOUNT_CLOSING("13"),
        ISSUE_TOPUP_CARD("14"),
        ACCOUNT_REOPENING("15");

        BALANCE_UPDATED_FLAG(String flag) {
            this.flag = flag;
        }

        public String getFlag() {
            return flag;
        }

        private String flag;

    }

    public enum REMOTER_PRO_STATUS{
        //01- 未处理状态
        //02- 已处理
        //03- 丢弃状态
        WAITING("01"),
        PENDING("02"),
        ABANDON("03"),
        DELAYED("04"),
        ABOVE_ZERO("05");

        private String type;

        private REMOTER_PRO_STATUS(String type) {
            this.setType(type);
        }

        private void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
    public enum REMOTER_CTRL_STATUS{
        //01- SUCCESS
        //02- FAILURE
        //03- UNKNOWN
        SUCCESS("01"),
        FAILURE("02"),
        UNKNOWN("03"),
        NON_CTRL("99");

        private String type;

        private REMOTER_CTRL_STATUS(String type) {
            this.setType(type);
        }

        private void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    //REMOTE_RECON_REC TXN_TYPE
    public enum REMOTE_RECON_TXN_TYPE{
        //01:充值
        TOPUP("01"),
        //02 余额转入
        CREDIT_TRANSFER_IN("02"),
        //04:对账补入
        RECON_ADD("03"),
        //取消余额转移
        CANCEL_CREDIT_TRANSFER("04");

        private String txnStatus;

        public String getTxnStatus() {
            return txnStatus;
        }

        private void setTxnStatus(String txnStatus) {
            this.txnStatus = txnStatus;
        }

        private REMOTE_RECON_TXN_TYPE(String txnStatus){
            this.setTxnStatus(txnStatus);

        }
    }

    //REMOTE_DISCON_TXN_TYPE
    public enum REMOTE_DISCON_TXN_TYPE{
        //01电费校正
        REGISTER_DATA ("01"),
        //02电费扣减
        INTERVAL_DATA ("02"),
        //03冲正
        REVERSE ("03"),
        //04换表
        FAULTY_METER_REPLACEMENT ("04"),
        //05退款
        REFUND ("05"),
        //06:对账删除
        RECON_DELETE("06");

        private String txnStatus;

        public String getTxnStatus() {
            return txnStatus;
        }

        private void setTxnStatus(String txnStatus) {
            this.txnStatus = txnStatus;
        }

        private REMOTE_DISCON_TXN_TYPE(String txnStatus){
            this.setTxnStatus(txnStatus);

        }
    }

    public enum REMOTER_OPER_STATUS{
        //01- PENDING
        //02- EXECUTING
        //03- COMPLETED
        PENDING("01"),
        EXECUTING("02"),
        COMPLETED("03"),
        ABANDON("99");

        private String type;

        private REMOTER_OPER_STATUS(String type) {
            this.setType(type);
        }

        private void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    /**
     * 电表的状态：待用、正常运行、停用、拆除
     * @author hanyu
     * @date 2014-12-8 上午11:33:17
     */
    public enum METER_STATUS{
        INSTALLING("01"),   //启动安装流程
        /**
         * 01：待用
         */
        INSTALLED("02"),    //02：待用
        /**
         * 02：正常运行
         */
        RUN("03"),          //03：正常运行
        /**
         * 04：停用
         */
        STOPPED("04"),         //04：停用
        /**
         * 05：表后重新激活电表
         */
        WAITING_ACTIVATION("05"),         //05：表后重新激活电表
        /**
         * 06：换表后电表信号获取成功
         */
        COMMUNICATED ("06"),         //06：换表后电表信号获取成功

        //modify by yangyong  start
        UNINSTALLED("10"),  //10：拆除

        UNINSTALLEDCONSUMPTION("11");//11:拆表结算
        //modify by yangyong end

        private String status;

        private METER_STATUS(String status) {
            this.setStatus(status);
        }

        public String getStatus() {
            return status;
        }

        private void setStatus(String status) {
            this.status = status;
        }
    }

    public enum FUNCTION_URL{

        CREDIT_TRANSFER_APPROVAL("creditTransferController.do?creditApprove"),   //启动安装流程
        REPRINT_CARD("reprintCardController.do?toPage"),
        HOLIDAY("holidayController.do?toHoliday"),
        METER_REMOTE_CONTROL("creditUpdateRecController.do?remotecontrollist"),
        FAULTY_METER_REPLACEMENT("creditUpdateRecController.do?replacementmainlist"),
        TOPUP_RECORD_QUERY("spTopUpRecController.do?list"),
        METER_DATA_QUERY("meterDataController.do?list"),
        METER_COMPUTION_QUERY("collectionComputationController.do?list"),
        RECEIPT_QUERY("receiptController.do?list"),
        METER_EVENT_INFORMATION("meterEventController.do?list"),
        TOP_UP("topupRecordController.do?topup"),
        REVERSAL("topupRecordController.do?recordReverse"),
        CREDIT_TOP_UP_QUERY("topupRecordController.do?recordQuery"),
        DAY_END_RECON("dayendReconController.do?dayendRecon"),
        REFUND_APPLY("topupRecordController.do?refundApply"),
        REFUND_APPROVAL("topupRecordController.do?refundApproval"),
        RECEIPT_REPRINT("topupRecordController.do?receiptReprint"),
        ACCOUNT_CLOSING("customerInfoController.do?accountClosing"),
        CREDIT_TRANSFER_APPLY("creditTransferController.do?toview"),
        ACCOUNT_OPENING_FOR_PAYU_CONVERSION("customerInfoController.do?payuOpening"),
        ACCOUNT_ACTIVATION_FOR_PAYU_CONVERSION("customerInfoController.do?payuConversion"),
        CUSTOMER_TOPUP_RECORD_QUERY("topupRecordController.do?recordQuery"),
        COLLECTION_AGENT_RECONCILIATION("caReconController.do?caReconResult"),
        CA_DISCREPANCY_APPROVAL("caReconController.do?reconApproval"),
        CA_TRANSACTION_AUDIT("caReconController.do?caReconAudit"),
        REMOTE_DISCONNECTION("remoteDisconController.do?view"),
        MENU_MANAGEMENT("functionController.do?function"),
        ROLE_MANAGEMENT("roleController.do?role"),
        SYSTEM_PARAMETER_CONFIGURATION("systemController.do?typeGroupList"),
        TARIFF_MANAGEMENT("tariffController.do?toTariff"),
        TARIFF_AUDIT("tariffController.do?toTariffLog"),
        GST_MANAGEMENT("gstController.do?toGst"),
        GST_AUDIT("gstController.do?toGstLog"),
        SMS_TEMPLATE_CONFIGURATION("smsTemplateController.do?toSMS"),
        CHANNEL_MANAGEMENT("channelController.do?goChannel"),
        CENTER_MANAGEMENT("terminalController.do?goTerminal"),
        TERMINAL_MANAGEMENT("counterController.do?goCounter"),
        CREDIT_UPDATE_RECORD_QUERY("creditUpdateRecController.do?list"),
        SMS_QUERY("smsController.do?smsQueryList"),
        EMAIL_TEMPLATE_MANAGEMENT("emailController.do?toView"),
        PRINT_TOPUP_CARD("reprintCardController.do?toPage"),
        ELECTRICITY_CONSUMPUTION_BILLING_QUERY("consumptionBillingController.do?list"),
        COMPUTATION_FAILURE_INFORMATION_QUERY("computationFailureController.do?list"),
        REMOTE_RECONNECTION("remoteReconRecController.do?view"),
        ACCOUNT_OPENING("customerInfoController.do?accountOpening"),
        ACCOUNT_ACTIVATION("customerInfoController.do?accountActivation"),
        PAYU_OPENING("customerInfoController.do?payuOpening"),
        CUSTOMER_INFORMATION_QUERY("customerInfoQueryController.do?customerInfoQueryList");

        private String status;

        FUNCTION_URL(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }
}
