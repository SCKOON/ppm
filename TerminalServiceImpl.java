package com.ppms.terminalManagement.service.impl;

import com.constants.ChannelTypeEnum;
import com.constants.SiteStatus;
import com.ppms.entity.ChannelEntity;
import com.ppms.entity.CounterEntity;
import com.ppms.entity.TerminalEntity;
import com.ppms.terminalManagement.dao.TerminalDao;
import com.ppms.terminalManagement.dto.TerminalDTO;
import com.ppms.terminalManagement.service.TerminalServiceI;
import com.ppms.utils.DataReturn;
import com.ppms.utils.DataSourceValue;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.extend.datasource.DataSourceType;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("terminalService")
@Transactional
@DataSourceValue(DataSourceType.dataSource_ppms)
public class TerminalServiceImpl extends CommonServiceImpl implements TerminalServiceI {

    @Autowired
    private TerminalDao terminalDao;

    @Override
    public void delete(TerminalDTO entity) throws Exception {
        if (entity == null || entity.getId() == null) {
            throw new BusinessException("Center data exception!");
        }
        TerminalEntity terminalEntity = terminalDao.getEntity(TerminalEntity.class, entity.getId());
        if (terminalEntity.getCounterEntities() != null && terminalEntity.getCounterEntities().size() > 0) {
            throw new BusinessException("Please ensure all terminal tags have been removed from this center.");
        }
        terminalDao.delete(terminalEntity);
    }

    @Override
    public Serializable save(TerminalEntity entity) throws Exception {
        if (oConvertUtils.isNotEmpty(entity.getName()) && oConvertUtils.isNotEmpty(entity.getCode())) {
            if(this.queryTerminalNameOrCodeIsUse(entity)){
                throw new BusinessException(" Center name or code have already existed ");
            }
            if(entity.getChannelEntity()==null||entity.getChannelEntity().getId()==null){
                throw new BusinessException(" Center must be binded to a channel!");
            }
            ChannelEntity channelEntity = getEntity(ChannelEntity.class,entity.getChannelEntity().getId());
            if(!ChannelTypeEnum.SP.getStatus().equals(channelEntity.getType()) || !SiteStatus.ACTIVE.getStatus().equals(channelEntity.getStatus())){
                throw new BusinessException("Center should be bind to a actived sp channel!");
            }
            entity.setStatus(SiteStatus.CREATED.getStatus());
            entity.setChannelEntity(channelEntity);
            return terminalDao.save(entity);
        } else {
            throw new BusinessException(" Center name or code can't be empty!");
        }
    }

    @Override
    public void saveOrUpdate(TerminalEntity entity) throws Exception {
        if (oConvertUtils.isNotEmpty(entity.getName()) && oConvertUtils.isNotEmpty(entity.getCode())) {
            if(this.queryTerminalNameOrCodeIsUse(entity)){
                throw new BusinessException("Center name or code have already existed ");
            }
            if(entity.getChannelEntity()==null||entity.getChannelEntity().getId()==null){
                throw new BusinessException("Center must be binded to a channel ");
            }
            //查询terminal绑定的channel
            ChannelEntity channelEntity = getEntity(ChannelEntity.class,entity.getChannelEntity().getId());
            if(channelEntity == null || !ChannelTypeEnum.SP.getStatus().equals(channelEntity.getType()) || !SiteStatus.ACTIVE.getStatus().equals(channelEntity.getStatus())){
                throw new BusinessException("Center should be bind to a actived sp channel!");
            }
            if(!queryCounterStatusIsValid(entity)){
                throw new BusinessException("Center Status should be in accordance with the attached terminals!");
            }
            TerminalEntity t = terminalDao.get(TerminalEntity.class, entity.getId());
            MyBeanUtils.copyBeanNotNull2Bean(entity, t);
            t.setChannelEntity(channelEntity);
            terminalDao.saveOrUpdate(t);
        }else {
            throw new BusinessException(" Center name or code can't be empty ");
        }
    }

    @Override
    public void batchDelete(String ids) throws Exception {
        for (String id : ids.split(",")) {
            TerminalEntity terminalEntity = terminalDao.getEntity(TerminalEntity.class, Integer.parseInt(id));
            if (terminalEntity.getCounterEntities() != null && terminalEntity.getCounterEntities().size() > 0) {
                throw new BusinessException("Please ensure all terminal tags have been removed from this center " + terminalEntity.getName() + ".");
            }
            terminalDao.delete(terminalEntity);
        }
    }


    @Override
    @DataSourceValue(DataSourceType.dataSource_ppms)
    public DataReturn getNewAllEntities(TerminalEntity terminalEntity, int page, int rows, HttpServletRequest request, String chalId) {
        return terminalDao.getNewAllEntities(terminalEntity, page, rows, request, chalId);
    }

    @Override
    public boolean queryTerminalNameOrCodeIsUse(TerminalEntity terminalEntity) {

        return terminalDao.queryTerminalInUser(terminalEntity).size() > 0;
    }

    /**
     * 判断切换terminal状态是否合法
     * @param terminalEntity
     * @return
     */
    @Override
    public Boolean queryCounterStatusIsValid(TerminalEntity terminalEntity) {
        if(oConvertUtils.isEmpty(terminalEntity.getStatus())){
            return false;
        }
        //如果terminal由其它的状态切换为创建状态，返回false
        if(SiteStatus.CREATED.getStatus().equals(terminalEntity.getStatus())){
            return false;
        }
        //如果terminal状态切换为暂停或者关闭状态，则需要查询挂在该terminal下面的counter是否还有在用的
        List<CounterEntity> counterEntities = terminalDao.queryCounterStatus(terminalEntity);
        if(SiteStatus.SUSPEND.getStatus().equals(terminalEntity.getStatus())||SiteStatus.CLOSED.getStatus().equals(terminalEntity.getStatus())){
            if(counterEntities.size()>0){
                return false;
            }
        }
        return true;
    }

    @Override
    @DataSourceValue(DataSourceType.dataSource_ppms)
    public TerminalEntity getEntity(Serializable id) {
        TerminalEntity terminalEntity = (TerminalEntity) terminalDao.getSession().get(TerminalEntity.class, id);
        return terminalEntity;
    }

    @Override
    @DataSourceValue(DataSourceType.dataSource_ppms)
    public void getDataGridReturn(CriteriaQuery cq, boolean isOffset) {
        terminalDao.getDataGridReturn(cq, isOffset);
    }

    @Override
    public void queryAllTerminalForView(TerminalEntity terminalEntity, DataGrid dataGrid) {
        terminalDao.queryAllTerminalForView(terminalEntity, dataGrid);
    }

    @Override
    public <T> T getEntity(Class entityName, Serializable id) {
        return terminalDao.getEntity(entityName,id);
    }

    @Override
    public <T> List<T> getListByCriteriaQuery(CriteriaQuery cq, Boolean ispage) {
        return terminalDao.getListByCriteriaQuery(cq,ispage);
    }

    @Override
    public List<Map<String, String>> queryAllTerminalCodeAndName() {
        return terminalDao.queryAllTerminalCodeAndName();
    }

    @Override
    public List queryCounterByTerminalCode(String terminalCode) {
        CriteriaQuery criteriaQuery = new CriteriaQuery(TerminalEntity.class);
        if (oConvertUtils.isNotEmpty(terminalCode)) {
            criteriaQuery.eq("code", terminalCode);
        }
        criteriaQuery.add();
        List<TerminalEntity> TerminalEntityList = super.getListByCriteriaQuery(criteriaQuery,false);
        List<CounterEntity> counterEntityList = new ArrayList();
        if(TerminalEntityList.size()>0){
            TerminalEntity terminal = TerminalEntityList.get(0);
            counterEntityList.addAll(terminal.getCounterEntities());
        }
        return counterEntityList;
    }
}