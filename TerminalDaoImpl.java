package com.ppms.terminalManagement.dao.impl;

import com.constants.ChannelTypeEnum;
import com.ppms.entity.ChannelEntity;
import com.ppms.entity.CounterEntity;
import com.ppms.terminalManagement.dao.TerminalDao;
import com.ppms.entity.TerminalEntity;
import com.ppms.utils.DataReturn;
import com.ppms.utils.DataSourceValue;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.jeecgframework.core.common.dao.impl.GenericBaseCommonDao;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.extend.datasource.DataSourceType;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.p3.core.logger.Logger;
import org.jeecgframework.p3.core.logger.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Repository
public class TerminalDaoImpl extends GenericBaseCommonDao implements TerminalDao{
    private static Logger logger = LoggerFactory.getLogger(TerminalDaoImpl.class);
    @Override
    public DataReturn getNewAllEntities(TerminalEntity terminalEntity, int page, int rows, HttpServletRequest request, String chnlId) {
        StringBuilder hql = new StringBuilder("from TerminalEntity where 1=1 ");
        StringBuilder condition = new StringBuilder("");

        List params = new ArrayList();
        String code = terminalEntity.getCode();
        if (code != null && !"".equals(code)) {
            condition.append(" and code like ? ");
            params.add("%"+code+"%");
        }

        String terminalName = terminalEntity.getName();
        if (terminalName != null && !"".equals(terminalName)) {
            condition.append(" and name like ? ");
            params.add("%"+terminalName+"%");
        }
        String status = terminalEntity.getStatus();
        if (status != null && !"".equals(status)) {
            condition.append(" and status = ? ");
            params.add(status);
        }
        //   String chnlId = terminalEntity.getChnlId();
        if (chnlId != null && !"".equals(chnlId)) {
            condition.append(" and chnl_id = ? ");
            params.add(Integer.parseInt(chnlId));
        }
        String channel_name = terminalEntity.getChannelEntity()==null?null:terminalEntity.getChannelEntity().getName();
        if (status != null && !"".equals(status)) {
            condition.append(" and channelEntity.name = ? ");
            params.add(channel_name);
        }
        String countQuery = "from TerminalEntity where 1=1 "+condition.toString();
        String hqlQuery = hql.append(condition.toString()).toString();
        Query q = getSession().createQuery(hqlQuery);
        if (params != null && params.size() > 0) {
            for (int i = 0; i < params.size(); i++) {
                q.setParameter(i, params.get(i));
            }
        }
        q.setFirstResult((page-1)*rows).setMaxResults(rows);
        int count = this.findHql(hqlQuery,params.toArray()).size();
        List<TerminalEntity> channelEntityList = q.list();
        DataReturn data = new DataReturn();
        data.setRows(channelEntityList);
        data.setTotal(count);
        return data;
    }

    @Override
    public void queryAllTerminalForView(TerminalEntity terminalEntity, DataGrid dataGrid) {

        List<TerminalEntity> terminalEntityList = null;
        Map map = new HashMap<String, Object>();
        StringBuilder stringBuilder = new StringBuilder(" from TerminalEntity as entity where 1=1 ");
        if(terminalEntity!=null){
            if(oConvertUtils.isNotEmpty(terminalEntity.getName())){
                stringBuilder.append(" and entity.name like :name");
                map.put("name", "%" + terminalEntity.getName() + "%");
            }
            if(oConvertUtils.isNotEmpty(terminalEntity.getCode())){
                stringBuilder.append(" and entity.code like :code");
                map.put("code", "%" + terminalEntity.getCode() + "%");
            }
            if(oConvertUtils.isNotEmpty(terminalEntity.getStatus())){
                stringBuilder.append(" and entity.status = :status");
                map.put("status",  terminalEntity.getStatus());
            }
            if(terminalEntity.getChannelEntity()!=null&&oConvertUtils.isNotEmpty(terminalEntity.getChannelEntity().getId())){
                stringBuilder.append(" and entity.channelEntity.id = :channelId");
                map.put("channelId",  terminalEntity.getChannelEntity().getId());
            }
        }

        stringBuilder.append(" and entity.channelEntity.type = '"+ ChannelTypeEnum.SP.getStatus()+"'");
        if(dataGrid!=null){
            if (dataGrid.getSort()!=null&&oConvertUtils.isNotEmpty(dataGrid.getSort())) {
                stringBuilder.append(" order by entity."+dataGrid.getSort()+" "+dataGrid.getOrder());
            }
        }
        Query query = super.getSession().createQuery(stringBuilder.toString());
        query.setProperties(map);
        try {
            terminalEntityList = query.list();
        } catch (HibernateException e) {
            logger.error(e.getMessage(),e);
        }
        super.paginateDataGrid(terminalEntityList,dataGrid,query);
    }

    @Override
    public List<TerminalEntity> queryTerminalInUser(TerminalEntity terminalEntity) {
        List<TerminalEntity> terminalEntityList = null;
        Map map = new HashMap<String, Object>();
        StringBuilder stringBuilder = new StringBuilder(" from TerminalEntity as entity where 1=1 ");
        if(terminalEntity!=null){
            if(oConvertUtils.isNotEmpty(terminalEntity.getName())&&oConvertUtils.isNotEmpty(terminalEntity.getCode())){
                stringBuilder.append(" and (entity.name = :name or entity.code = :code) ");
                map.put("name", terminalEntity.getName());
                map.put("code",terminalEntity.getCode());
            } else if(terminalEntity.getName()!=null){
                stringBuilder.append(" and entity.name = :name");
                map.put("name", terminalEntity.getName());
            }else{
                stringBuilder.append(" and entity.code = :code");
                map.put("code", terminalEntity.getCode());
            }
            if(terminalEntity.getId()!=null){
                stringBuilder.append(" and entity.id != :id");
                map.put("id", terminalEntity.getId());
            }
        }
        Query query = super.getSession().createQuery(stringBuilder.toString());
        query.setProperties(map);
        terminalEntityList = query.list();
        return terminalEntityList;
    }

    @Override
    public List<CounterEntity> queryCounterStatus(TerminalEntity terminalEntity) {
        List<CounterEntity> counterEntityList = new ArrayList<>();
        if(terminalEntity!=null&&terminalEntity.getId()!=null &&oConvertUtils.isNotEmpty(terminalEntity.getStatus())){
            String hql = " from CounterEntity As entity where 1=1 and entity.status != :status and entity.terminalEntity.id = :terminalId";
            Map<String, Object> map = new HashMap();
            map.put("status", terminalEntity.getStatus());
            map.put("terminalId", terminalEntity.getId());
            Query query = super.getSession().createQuery(hql);
            query.setProperties(map);
            counterEntityList = query.list();
        }
        return counterEntityList;
    }

    @Override
    public List<Map<String, String>> queryAllTerminalCodeAndName() {
        List<Map<String, String>> mapList = new ArrayList();
        String sql = "select distinct(code),name from s_terminal";
        Query query = getSession().createSQLQuery(sql);
        List list = query.list();
        for(int i = 0;i<list.size();i++){
            Map<String, String> map = new HashMap<>();
            Object[] o = (Object[]) list.get(i);
            map.put("code", oConvertUtils.getString(o[0]));
            map.put("name", oConvertUtils.getString(o[1]));
            mapList.add(map);
        }
        return mapList;
    }
}
