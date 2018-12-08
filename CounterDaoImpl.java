package com.ppms.counterManagement.dao.impl;

import com.ppms.counterManagement.dao.CounterDao;
import com.ppms.entity.CounterEntity;
import com.ppms.utils.DataReturn;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.jeecgframework.core.common.dao.impl.GenericBaseCommonDao;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.p3.core.logger.Logger;
import org.jeecgframework.p3.core.logger.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Repository
public class CounterDaoImpl extends GenericBaseCommonDao implements CounterDao {
    private static Logger logger = LoggerFactory.getLogger(CounterDaoImpl.class);

    @Override
    public DataReturn getAllEntities(CounterEntity counterEntity, int page, int rows, HttpServletRequest request) {

        StringBuilder hql = new StringBuilder("from CounterEntity where 1=1 ");
        StringBuilder condition = new StringBuilder("");
        List params = new LinkedList();
        String code = counterEntity.getCode();
        if (oConvertUtils.isNotEmpty(code)) {
            condition.append(" and code = ? ");
            params.add(code);
        }
        String channelName = counterEntity.getName();
        if (oConvertUtils.isNotEmpty(channelName)) {
            condition.append(" and name like ? ");
            params.add("%" + channelName + "%");
        }

        String status = counterEntity.getStatus();
        if (oConvertUtils.isNotEmpty(status)) {
            condition.append(" and status = ? ");
            params.add(status);
        }
        if (null != counterEntity.getTerminalEntity() && oConvertUtils.isNotEmpty(counterEntity.getTerminalEntity().getName())) {
            condition.append(" and terminalEntity.name like ? ");
            params.add("%" + counterEntity.getTerminalEntity().getName() + "%");
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
        List<CounterEntity> counterEntities = q.list();
        DataReturn data = new DataReturn();
        data.setRows(counterEntities);
        data.setTotal(count);
        return data;
    }

    @Override
    public void queryListForView(DataGrid grid, HttpServletRequest request) {
        List<CounterEntity> emailEntityList = null;
        Map map = new HashMap<String, Object>();
        StringBuilder stringBuilder = new StringBuilder(" from CounterEntity where 1=1  ");

        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String code = request.getParameter("code");
        String tmlCode = request.getParameter("tmlCode");
        if (oConvertUtils.isNotEmpty(name)) {
            stringBuilder.append(" and name like :name");
            map.put("name", "%" + name + "%");
        }
        if (oConvertUtils.isNotEmpty(status)) {
            stringBuilder.append(" and status = :status");
            map.put("status", status);
        }
        if (oConvertUtils.isNotEmpty(code)) {
            stringBuilder.append(" and code = :code");
            map.put("code", code);
        }
        if (oConvertUtils.isNotEmpty(tmlCode)) {
            stringBuilder.append(" and terminalEntity.code = :terminalCode");
            map.put("terminalCode", tmlCode);
        }

        if (null != grid) {
            if (null != grid.getSort() && oConvertUtils.isNotEmpty(grid.getSort())) {
                stringBuilder.append(" order by " + grid.getSort() + " " + grid.getOrder());
            }
        }
        Query query = super.getSession().createQuery(stringBuilder.toString());
        query.setProperties(map);
        try {
            emailEntityList = query.list();
        } catch (HibernateException e) {
            logger.error(e.getMessage(), e);
        }
        super.paginateDataGrid(emailEntityList, grid, query);
    }
}
