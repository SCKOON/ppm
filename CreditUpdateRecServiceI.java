package com.ppms.creditupdaterecQuery.service;

import com.ppms.utils.DataReturn;
import com.ppms.vo.*;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.common.service.CommonService;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;


public interface CreditUpdateRecServiceI extends CommonService{

    public DataReturn getAllEntities(CreditURecResultVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException;
    public DataReturn getSmsAllEntities(SmsRecExVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException;
    public DataReturn getControlAllEntities(RemoteRecVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException;
    public DataReturn getremoteAllEntities(RemoteCtrlRecVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException;


    public void getMeterReplacementResultList(DataGrid dataGrid, ReplacementVo vo);
    public void getProcessChargeDataResultList(DataGrid dataGrid, String txnId);

}
