package com.ppms.creditTopup.dao;

import com.ppms.entity.*;
import com.ppms.utils.DataReturn;
import com.ppms.vo.ResultVo;
import org.jeecgframework.core.common.model.json.DataGrid;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by yadongliang on 2018/5/7 0007.
 */
public interface TopupDao {

    //查询账户信息(弹框查询Query)
    DataReturn getAllEntities(ResultVo resultVo, int page, int rows, HttpServletRequest request);

    //充值记录查询-冲正页面和充值记录查询页面
    void getTopupRecord(DataGrid dataGrid, HttpServletRequest request, String curLoginName);

    void getCreditTopupRec(DataGrid dataGrid, HttpServletRequest request);

    Integer updateTxnStatus(SpTopUpRecEntity topupRecord, String status) throws Exception;

    DataReturn getCustomerInfo(CustomerInfoEntity customerInfoEntity, AcctBalEntity acctBalEntity, int page, int rows, HttpServletRequest request);

    void getRefundApplyDatagrid(RefundRecordEntity refundRecordEntity, CustomerInfoEntity customerInfoEntity, DataGrid dataGrid, HttpServletRequest request);

    //凭证打印列表查询
    void getReceiptPrintDatagrid(HttpServletRequest request, DataGrid dataGrid);

    ReceiptInfoEntity getReceiptInfo(ReceiptInfoEntity receiptInfoEntity);

    Integer topup(SpTopUpRecEntity topupRecordEntity);

    List getAcctBalByAccNo(String accNo);

    BigDecimal getOneDayTopupAmt(String accNo, String txnDate);

    //根据refNo查询充值记录
    SpTopUpRecEntity getTopupRecordByRefNo(String refNo);

    //根据refNo查询充值记录
    ReceiptInfoEntity getReceiptInfo(String refNo);

    //更新用户最新余额
    void updateAccountBalance(String accNo, BigDecimal currentBal, Date txnDate);

    //查询counter信息 根据macAddr
    CounterEntity getCounterInfo(String macAddr);

    //根据ip查询counter信息
    CounterEntity getCounterByIP(String clientIP);

    //查询terminal信息 根据counter信息对应的terminalId(外键)
    TerminalEntity getTerminalInfo(int terminalId);

    //查询channel信息 根据terminal信息对应的channelId(外键)
    ChannelEntity getChannelInfo(int channelId);

    //充值页面离焦查询用户电表及其他信息
    ResultVo getCustInfo(HttpServletRequest request);

    BigDecimal getTotalRefundAmtByAccNo(String accNo);

    //冲正-充值记录查询
    void getTopupRecordForReverse(DataGrid datagrid, HttpServletRequest request, String curLoginName);
    
}
