package com.ppms.creditupdaterecQuery.dao;

import com.ppms.entity.RemoteRecEntity;
import com.ppms.vo.*;
import com.ppms.utils.DataReturn;
import org.jeecgframework.core.common.model.json.DataGrid;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface CreditUpdateRecDao {

    public DataReturn getAllEntities(CreditURecResultVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException;
    public DataReturn getSmsAllEntities(SmsRecExVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException;
    public DataReturn getControlAllEntities(RemoteRecVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException;
    public DataReturn getremoteAllEntities(RemoteCtrlRecVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException;

    public void getMeterReplacementResult(DataGrid dataGrid,ReplacementVo vo);

    public void getMeterReplacementCount(DataGrid dataGrid,ReplacementVo vo);

    public void getProcessChargeDataResult(DataGrid dataGrid,String txn_id);

    public void getProcessChargeDataCount(DataGrid dataGrid,String txn_id);
}
