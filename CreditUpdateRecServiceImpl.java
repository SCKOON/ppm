package com.ppms.creditupdaterecQuery.service.impl;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import com.ppms.entity.RemoteRecEntity;
import com.ppms.vo.*;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ppms.creditupdaterecQuery.dao.CreditUpdateRecDao;
import com.ppms.creditupdaterecQuery.service.CreditUpdateRecServiceI;
import com.ppms.utils.DataReturn;
import com.ppms.utils.DataSourceValue;

import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.extend.datasource.DataSourceType;

@Service("creditUpdateRecService")
@Transactional
@DataSourceValue(DataSourceType.dataSource_ppms)
public class CreditUpdateRecServiceImpl extends CommonServiceImpl implements CreditUpdateRecServiceI {

    @Autowired
    private CreditUpdateRecDao creditUpdateRecDao;

    @Override
    @DataSourceValue(DataSourceType.dataSource_ppms)
    public DataReturn getAllEntities(CreditURecResultVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException{

        return creditUpdateRecDao.getAllEntities(resultVo,page,rows,request);
    }

    public DataReturn getSmsAllEntities(SmsRecExVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException{

        return creditUpdateRecDao.getSmsAllEntities(resultVo,page,rows,request);
    }

    public DataReturn getControlAllEntities(RemoteRecVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException{
        return creditUpdateRecDao.getControlAllEntities(resultVo,page,rows,request);
    }
    public DataReturn getremoteAllEntities(RemoteCtrlRecVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException{
        return creditUpdateRecDao.getremoteAllEntities(resultVo,page,rows,request);

    }


    @Override
    public void getMeterReplacementResultList(DataGrid dataGrid, ReplacementVo vo) {
        creditUpdateRecDao.getMeterReplacementCount(dataGrid, vo);
        if(dataGrid.getTotal() != 0){
            creditUpdateRecDao.getMeterReplacementResult(dataGrid, vo);
        }
    }

    @Override
    public void getProcessChargeDataResultList(DataGrid dataGrid, String txnId) {
        creditUpdateRecDao.getProcessChargeDataCount(dataGrid, txnId);
        if(dataGrid.getTotal() != 0){
            creditUpdateRecDao.getProcessChargeDataResult(dataGrid, txnId);
        }
    }
}
