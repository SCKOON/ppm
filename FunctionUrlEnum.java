package com.constants;

/**
 * Created by admPPMSd on 12/26/2018.
 */
public enum FunctionUrlEnum {

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

    FunctionUrlEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
