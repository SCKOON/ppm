package org.jeecgframework.web.system.service.impl;

import org.hibernate.Query;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.web.system.pojo.base.TSFunction;
import org.jeecgframework.web.system.service.FunctionServiceI;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FunctionServiceImpl extends CommonServiceImpl implements FunctionServiceI {

    @Autowired
    private SystemService systemService;

    @Override
    public void delete(TSFunction function) {
        function = getEntity(TSFunction.class, function.getId());
        if(function.getTSFunctions() !=null && function.getTSFunctions().size()>0){
            throw new BusinessException("Please ensure all the sub-menu have been removedd from this Menu.");
        }
        TSFunction parent = function.getTSFunction();
        if (parent != null) {
            parent.getTSFunctions().remove(function);
        }
        super.updateBySqlString("delete from t_s_role_function where functionid='"
                + function.getId() + "'");

        super.delete(function);
    }

    @Override
    public void update(TSFunction function) throws Exception {
        //判断是否有重名的functioName
        if (checkHasFunctionNameOrNot(function)) {
            throw new BusinessException("function name has already existed!");
        }

        TSFunction t = systemService.getEntity(TSFunction.class, function.getId());
        try {
            MyBeanUtils.copyBeanNotNull2Bean(function, t);
        } catch (Exception e) {
            throw new Exception(e);
        }
        super.saveOrUpdate(t);
        List<TSFunction> subFunction = systemService.findByProperty(TSFunction.class, "TSFunction.id", function.getId());
        updateSubFunction(subFunction, function);
    }

    @Override
    public void save(TSFunction function) throws Exception{
        //判断是否有重名的functioName
        if (checkHasFunctionNameOrNot(function)) {
            throw new BusinessException("function name has already existed!");
        }
        TSFunction t = new TSFunction();
        MyBeanUtils.copyBeanNotNull2Bean(function, t);
        systemService.save(t);
    }

    /**
     * 递归更新子菜单的FunctionLevel
     *
     * @param subFunction
     * @param parent
     */
    private void updateSubFunction(List<TSFunction> subFunction, TSFunction parent) {
        if (subFunction.size() == 0) {//没有子菜单是结束
            return;
        } else {
            for (TSFunction tsFunction : subFunction) {
                tsFunction.setFunctionLevel(Short.valueOf(parent.getFunctionLevel()
                        + 1 + ""));
                super.saveOrUpdate(tsFunction);
                List<TSFunction> subFunction1 = null;

                subFunction1 = super.findByProperty(TSFunction.class, "TSFunction.id", tsFunction.getId());

                if (subFunction1 != null) {
                    updateSubFunction(subFunction1, tsFunction);
                }
            }
        }
    }

    /**
     * 判断是否有重名的function
     *
     * @param tsFunction
     * @return
     */
    private boolean checkHasFunctionNameOrNot(TSFunction tsFunction) {
        if (tsFunction == null || oConvertUtils.isEmpty(tsFunction.getFunctionName())) {
            return true;
        }
        Map<String, Object> map = new HashMap();
        String hql = " from TSFunction as entity where 1=1 and entity.functionName =:functionName";
        map.put("functionName", tsFunction.getFunctionName());
        if (tsFunction.getTSFunction() != null && oConvertUtils.isNotEmpty(tsFunction.getTSFunction().getId())) {
            hql += " and entity.TSFunction.id = :functionId";
            map.put("functionId", tsFunction.getTSFunction().getId());
        } else {
            hql += " and entity.functionLevel = :functionLevel";
            map.put("functionLevel", new Short("0"));
        }
        if (oConvertUtils.isNotEmpty(tsFunction.getId())) {
            hql += " and entity.id = :functionId";
            map.put("functionId", tsFunction.getId());
        }
        Query query = this.systemService.getSession().createQuery(hql);
        query.setProperties(map);
        List<Object> types = query.list();
        if (types.size() > 0) {
            return true;
        }
        return false;
    }

}
