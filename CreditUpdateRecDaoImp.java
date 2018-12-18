package com.ppms.creditupdaterecQuery.dao.impl;

import com.ppms.creditupdaterecQuery.dao.CreditUpdateRecDao;
import com.ppms.entity.MeterReplacementEntity;
import com.ppms.entity.ProcessChargeEntity;
import com.ppms.entity.RemoteRecEntity;
import com.ppms.entity.SmsRecEntity;
import com.ppms.utils.DataReturn;
import com.ppms.utils.FormatValidateUtil;
import com.ppms.vo.*;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.jeecgframework.core.common.dao.impl.GenericBaseCommonDao;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class CreditUpdateRecDaoImp extends GenericBaseCommonDao implements CreditUpdateRecDao{

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public DataReturn getAllEntities(CreditURecResultVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException{


        //  StringBuilder hql = new StringBuilder("select b.accNo,m.meterId,b.curBal,b.updateFlag,b.updateTime,b.ctlNo,case when b.smsNo < '0' then NULL else b.smsNo end,b.lastBal from  BalanceRecordEntity as b,MeterInfoEntity as m where b.accNo = m.accNo  ");
        //StringBuilder sql = new StringBuilder("SELECT b.acc_no,m.meter_id,b.cur_bal,b.update_flag,b.update_time,b.ctl_no,case when b.sms_no < '0' then NULL when b.sms_no is null then NULL else b.sms_no end as sms_no,b.last_bal FROM A_BALANCE_REC b,A_METER_INFO m where b.acc_no = m.acc_no  ");
        StringBuilder sql = new StringBuilder("SELECT b.acc_no,b.cur_bal,b.update_flag,b.update_time,b.ctl_no,case when b.sms_no < '0' then NULL when b.sms_no is null then NULL else b.sms_no end as sms_no,b.last_bal FROM A_BALANCE_REC b where 1=1 ");

        StringBuilder condition = new StringBuilder("");
        List params = new ArrayList();

        String accNo = FormatValidateUtil.validateAccNo(request.getParameter("accNo"));
       // String meterId = FormatValidateUtil.validateMeterId(request.getParameter("meterId"));
        String updateFlag = request.getParameter("updateFlag");
        String date_begin = request.getParameter("updateTime_begin");
        String date_end = request.getParameter("updateTime_end");

        if(!StringUtil.isEmpty(accNo)){
            condition.append(" and b.acc_no = ? ");
            params.add(accNo);
        }

/*        if(!StringUtil.isEmpty(meterId)){
            condition.append(" and m.meter_id = ? ");
            params.add(meterId);
        }*/

        if(!StringUtil.isEmpty(updateFlag)){
            condition.append(" and b.update_flag = ? ");
            params.add(updateFlag);
        }

        if(!StringUtil.isEmpty(date_begin)){
            condition.append(" and b.update_time >= ? ");
            params.add(sdf.parse(date_begin));
        }

        if(!StringUtil.isEmpty(date_end)){
            condition.append(" and b.update_time <= ? ");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(date_end));
            calendar.add(Calendar.DAY_OF_MONTH,1);
            params.add(calendar.getTime());
        }

        String sqlQuery = sql.append(condition.toString()).append(" order by b.update_time desc ").toString();


        SQLQuery q = getSession().createSQLQuery(sqlQuery);
        if (params != null && params.size() > 0) {
            for (int i = 0; i < params.size(); i++) {
                q.setParameter(i, params.get(i));
            }
        }

        List listSize = q.list();

        q.setFirstResult((page - 1) * rows).setMaxResults(rows);
        List list = q.list();


        List<CreditURecResultVo> resultVos = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");

        for(int i=0; i < list.size(); i++){
            Object[] objects = (Object[]) list.get(i);
            CreditURecResultVo resultVo1 = new CreditURecResultVo();
            resultVo1.setAccNo((String) objects[0]);
          //  resultVo1.setMeterId((String) objects[1]);
            //resultVo1.setCurBal((BigDecimal) objects[2]);
            if (objects[1] != null) {
                BigDecimal big2 = ((BigDecimal) objects[1]).setScale(2, BigDecimal.ROUND_HALF_UP);
                double db2 = big2.doubleValue();
                String format = df.format(db2);
                BigDecimal bigbal = new BigDecimal(format);
                resultVo1.setCurBal(bigbal);
            }
            resultVo1.setUpdateFlag((String) objects[2]);
            resultVo1.setUpdateTime((Date) objects[3]);
            resultVo1.setCtlNo((String) objects[4]);
            resultVo1.setSmsNo((String) objects[5]);
            //resultVo1.setLastBal((BigDecimal) objects[7]);
            if (objects[6] != null) {
                BigDecimal big7 = ((BigDecimal) objects[6]).setScale(2, BigDecimal.ROUND_HALF_UP);
                double db7 = big7.doubleValue();
                String format = df.format(db7);
                BigDecimal bigbal = new BigDecimal(format);
                resultVo1.setLastBal(bigbal);
            }

            resultVos.add(resultVo1);
        }
        int count = listSize.size();
        DataReturn data = new DataReturn();
        data.setRows(resultVos);
        data.setTotal(count);

        return data;
    }


    @Override
    public DataReturn getSmsAllEntities(SmsRecExVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException{

        StringBuilder hql = new StringBuilder("from SmsRecEntity where 1=1 ");
        StringBuilder condition = new StringBuilder("");
        List params = new ArrayList();

        String smsNo = resultVo.getSmsNo();
        if(oConvertUtils.isNotEmpty(smsNo)&&smsNo.contains(",")){
            smsNo = smsNo.substring(0,smsNo.length()-1);
        }


        if(!StringUtil.isEmpty(smsNo)){
            condition.append(" and id = ? ");
            params.add(Integer.parseInt(smsNo));
        }

/*        System.out.println("smsNo:"+smsNo);*/

        String hqlQuery = hql.append(condition.toString()).toString();

        Query q = getSession().createQuery(hqlQuery);
        if (params != null && params.size() > 0) {
            for (int i = 0; i < params.size(); i++) {
                q.setParameter(i, params.get(i));
            }
        }
        q.setFirstResult((page-1)*rows).setMaxResults(rows);
        int count = this.findHql(hqlQuery,params.toArray()).size();
        List<SmsRecEntity> SmsRecEntity = q.list();
        DataReturn data = new DataReturn();
        data.setRows(SmsRecEntity);
        data.setTotal(count);
        return data;
    }


    @Override
    public DataReturn getControlAllEntities(RemoteRecVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException{

        StringBuilder hql = new StringBuilder("SELECT  r.txnId,r.accNo,r.meterId,r.controlType,r.updateFlag,r.genTime,r.ctlStatus,r.ctlTime,r.exeStartTime,r.exeEndTime from RemoteRecEntity r where 1=1");

        StringBuilder condition = new StringBuilder("");
        List params = new ArrayList();

        String accNo = FormatValidateUtil.validateAccNo(resultVo.getAccNo());
        String controlType = request.getParameter("controlType");
        String ctlStatus = request.getParameter("ctlStatus");
        String date_begin = request.getParameter("ctlTime_begin");
        String date_end = request.getParameter("ctlTime_end");

        if (accNo != null && !"".equals(accNo)) {
            condition.append(" and r.accNo = ? ");
            params.add(accNo);
        }
        if (controlType != null && !"".equals(controlType)) {
            condition.append(" and r.controlType = ? ");
            params.add(controlType);
        }
        if (ctlStatus != null && !"".equals(ctlStatus)) {
            condition.append(" and r.ctlStatus = ? ");
            params.add(ctlStatus);
        }


        if (date_begin != null && !"".equals(date_begin)) {
            condition.append(" and r.ctlTime >= ? ");
            params.add(sdf.parse(date_begin));
            System.out.println(date_begin);
            System.out.println(sdf.parse(date_begin));
        }

        if (date_end != null && !"".equals(date_end)) {
            condition.append(" and r.ctlTime <= ? ");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(date_end));
            calendar.add(Calendar.DAY_OF_MONTH,1);
            params.add(calendar.getTime());
        }


        String hqlQuery = hql.append(condition.toString()).toString();
        Query q = getSession().createQuery(hqlQuery);
        if (params != null && params.size() > 0) {
            for (int i = 0; i < params.size(); i++) {
                q.setParameter(i, params.get(i));
            }
        }
        q.setFirstResult((page - 1) * rows).setMaxResults(rows);
        int count = this.findHql(hqlQuery, params.toArray()).size();
        List list = q.list();
        List<RemoteRecVo> resultVos = new ArrayList<>();

        for(int i=0; i < list.size(); i++){
            Object[] objects = (Object[]) list.get(i);
            RemoteRecVo resultVo1 = new RemoteRecVo();
            resultVo1.setTxnId((String) objects[0]);
            resultVo1.setAccNo((String) objects[1]);
            resultVo1.setMeterId((String) objects[2]);
            resultVo1.setControlType((String) objects[3]);
            resultVo1.setUpdateFlag((String) objects[4]);
            resultVo1.setGenTime((Date) objects[5]);
            resultVo1.setCtlStatus((String) objects[6]);
            resultVo1.setCtlTime((Date) objects[7]);
            resultVo1.setExeStartTime((Date) objects[8]);
            resultVo1.setExeEndTime((Date) objects[9]);

            resultVos.add(resultVo1);
        }

        DataReturn data = new DataReturn();
        data.setRows(resultVos);
        data.setTotal(count);

        return data;
    }


    @Override
    public DataReturn getremoteAllEntities(RemoteCtrlRecVo resultVo, int page, int rows, HttpServletRequest request) throws ParseException{

        StringBuilder hql = new StringBuilder("SELECT  r.txnId,r.accNo,r.txnType,r.genTime,r.operStatus,r.operTime,r.exeStartTime,r.exeEndTime,r.ctlStatus,r.meterId from RemoteRecEntity r where 1=1");

        StringBuilder condition = new StringBuilder("");
        List params = new ArrayList();

        String txnId = request.getParameter("txnId");

        if (txnId != null && !"".equals(txnId)) {
            condition.append(" and r.id = ? ");
            params.add(txnId);
        }

        String hqlQuery = hql.append(condition.toString()).toString();
        Query q = getSession().createQuery(hqlQuery);
        if (params != null && params.size() > 0) {
            for (int i = 0; i < params.size(); i++) {
                q.setParameter(i, params.get(i));
            }
        }
        q.setFirstResult((page - 1) * rows).setMaxResults(rows);
        int count = this.findHql(hqlQuery, params.toArray()).size();
        List list = q.list();
        List<RemoteCtrlRecVo> resultVos = new ArrayList<>();

        for(int i=0; i < list.size(); i++){
            Object[] objects = (Object[]) list.get(i);
            RemoteCtrlRecVo resultVo1 = new RemoteCtrlRecVo();
            resultVo1.setTxnId((String) objects[0]);
            resultVo1.setAccNo((String) objects[1]);
            resultVo1.setTxnType((String) objects[2]);
            resultVo1.setGenTime((Date) objects[3]);
            resultVo1.setOperStatus((String) objects[4]);
            resultVo1.setOperTime((Date) objects[5]);
            resultVo1.setExeStartTime((Date) objects[6]);
            resultVo1.setExeEndTime((Date) objects[7]);
            resultVo1.setCtlStatus((String) objects[8]);
            resultVo1.setMeterId((String) objects[9]);

            resultVos.add(resultVo1);
        }

        DataReturn data = new DataReturn();
        data.setRows(resultVos);
        data.setTotal(count);

        return data;
    }

    @Override
    public void getMeterReplacementResult(DataGrid dataGrid, ReplacementVo vo) {
        Map<String,Object> paramMap = new HashMap();
        String accno = vo.getAccno();
        Date beginDate = vo.getReplaceTime_begin();
        Date endDate = vo.getReplaceTime_end();
        StringBuilder builder = new StringBuilder(" from MeterReplacementEntity entity where 1=1 ");

        if(oConvertUtils.isNotEmpty(accno)){
            builder.append(" and entity.accNo = :accNo ");
            paramMap.put("accNo",accno);
        }
        if(oConvertUtils.isNotEmpty(beginDate)){
            builder.append(" and entity.replaceTime >= :beginDate ");
            paramMap.put("beginDate",beginDate);
        }
        if(oConvertUtils.isNotEmpty(beginDate)){
            builder.append(" and entity.replaceTime <= :endDate ");
            paramMap.put("endDate",endDate);
        }

        if(oConvertUtils.isNotEmpty(dataGrid.getSort())){
            builder.append(" order by "+dataGrid.getSort()+" "+dataGrid.getOrder());
        }
        String hql = builder.toString();
        Query query = super.getSession().createQuery(hql);
        query.setFirstResult((dataGrid.getPage()-1)*dataGrid.getRows()).setMaxResults(dataGrid.getRows());
        query.setProperties(paramMap);
        List<MeterReplacementEntity> list = query.list();
        dataGrid.setResults(list);
    }

    @Override
    public void getMeterReplacementCount(DataGrid dataGrid, ReplacementVo vo) {
        Map<String,Object> paramMap = new HashMap();
        String accno = vo.getAccno();
        Date beginDate = vo.getReplaceTime_begin();
        Date endDate = vo.getReplaceTime_end();
        StringBuilder builder = new StringBuilder("select count(*) from MeterReplacementEntity entity where 1=1 ");

        if(oConvertUtils.isNotEmpty(accno)){
            builder.append(" and entity.accNo = :accNo ");
            paramMap.put("accNo",accno);
        }
        if(oConvertUtils.isNotEmpty(beginDate)){
            builder.append(" and CONVERT(nvarchar(10),entity.replaceTime,120) >= :beginDate ");
            paramMap.put("beginDate",beginDate);
        }
        if(oConvertUtils.isNotEmpty(beginDate)){
            builder.append(" and CONVERT(nvarchar(10),entity.replaceTime,120) <= :endDate ");
            paramMap.put("endDate",endDate);
        }
        String hql = builder.toString();
        Query query = super.getSession().createQuery(hql);
        query.setProperties(paramMap);
        Integer size = ((Long)query.uniqueResult()).intValue();
        dataGrid.setTotal(size);
    }

    @Override
    public void getProcessChargeDataResult(DataGrid dataGrid, String txn_id) {
        Map<String,Object> paramMap = new HashMap();
        StringBuilder builder = new StringBuilder(" from ProcessChargeEntity entity where 1=1 ");
        if(oConvertUtils.isNotEmpty(txn_id)){
            builder.append(" and entity.txnId = :txnId ");
            paramMap.put("txnId",txn_id);
        }
        if(oConvertUtils.isNotEmpty(dataGrid.getSort())){
            builder.append(" order by "+dataGrid.getSort()+" "+dataGrid.getOrder());
        }
        String hql = builder.toString();
        Query query = super.getSession().createQuery(hql);
        query.setFirstResult((dataGrid.getPage()-1)*dataGrid.getRows()).setMaxResults(dataGrid.getRows());
        query.setProperties(paramMap);
        List<ProcessChargeEntity> list = query.list();
        dataGrid.setResults(list);
    }

    @Override
    public void getProcessChargeDataCount(DataGrid dataGrid, String txn_id) {
        Map<String,Object> paramMap = new HashMap();
        StringBuilder builder = new StringBuilder("select count(*) from ProcessChargeEntity entity where 1=1 ");
        if(oConvertUtils.isNotEmpty(txn_id)){
            builder.append(" and entity.txnId = :txnId ");
            paramMap.put("txnId",txn_id);
        }
        String hql = builder.toString();
        Query query = super.getSession().createQuery(hql);
        query.setProperties(paramMap);
        Integer size = ((Long)query.uniqueResult()).intValue();
        dataGrid.setTotal(size);
    }
}
