package org.jeecgframework.web.system.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.ppms.ldapLogin.util.ResourceUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.BrowserUtils;
import org.jeecgframework.core.util.ContextHolderUtils;
import org.jeecgframework.core.util.IpUtil;
import org.jeecgframework.core.util.MutiLangUtil;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.web.system.dao.JeecgDictDao;
import org.jeecgframework.web.system.pojo.base.*;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.web.system.util.OrgConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("systemService")
@Transactional
public class SystemServiceImpl extends CommonServiceImpl implements SystemService {

    @Autowired
    private JeecgDictDao jeecgDictDao;

    private static final Logger logger = Logger.getLogger(SystemServiceImpl.class);

    public TSUser checkUserExits(TSUser user) throws Exception {
        return this.commonDao.getUserByUserIdAndUserNameExits(user);
    }

    public List<DictEntity> queryDict(String dicTable, String dicCode, String dicText) {
        List<DictEntity> dictList = null;
        //step.1 如果没有字典表则使用系统字典表
        if (StringUtil.isEmpty(dicTable)) {
            dictList = jeecgDictDao.querySystemDict(dicCode);
            for (DictEntity t : dictList) {
                t.setTypename(MutiLangUtil.getMutiLangInstance().getLang(t.getTypename()));
            }
        } else {
            dicText = StringUtil.isEmpty(dicText, dicCode);
            dictList = jeecgDictDao.queryCustomDict(dicTable, dicCode, dicText);
        }
        return dictList;
    }

    /**
     * 添加日志
     */
    public void addLog(String logcontent, Short loglevel, Short operatetype) {
        HttpServletRequest request = ContextHolderUtils.getRequest();
        String broswer = BrowserUtils.checkBrowse(request);
        TSLog log = new TSLog();
        log.setLogcontent(logcontent);
        log.setLoglevel(loglevel);
        log.setOperatetype(operatetype);

        log.setNote(IpUtil.getIpAddr(request));

        log.setBroswer(broswer);
        /*start dangzhenghui 201703016TASK #1784 【online bug】Online 表单保存的时候，报错*/
        log.setOperatetime(new Date());
        /* end dangzhenghui 201703016TASK #1784 【online bug】Online 表单保存的时候，报错*/
//		log.setTSUser(ResourceUtil.getSessionUser());
        /*start chenqian 201708031TASK #2317 【改造】系统日志表，增加两个字段，避免关联查询 [操作人账号] [操作人名字]*/
        Authentication u = ResourceUtils.getSessionUser();
        if (u != null) {
            log.setUsername(u.getName());
        }
        commonDao.save(log);
    }

    /**
     * 根据类型编码和类型名称获取Type,如果为空则创建一个
     * updated by sc
     *
     * @param typecode
     * @param typename
     * @return
     */
    public TSType getType(String typecode, String typename, TSTypegroup tsTypegroup) {
        List<TSType> ls = commonDao.findHql("from TSType where typecode = ? and typegroupid = ?", typecode, tsTypegroup.getId());
        TSType actType;
        if (ls == null || ls.size() == 0) {
            actType = new TSType();
            actType.setTypecode(typecode);
            actType.setTypename(typename);
            commonDao.save(actType);
        } else {
            actType = ls.get(0);
        }
        return actType;

    }

    /**
     * 根据类型分组编码和名称获取TypeGroup,如果为空则创建一个
     *
     * @param typegroupcode
     * @param typgroupename
     * @return
     */
    public TSTypegroup getTypeGroup(String typegroupcode, String typgroupename) {
        TSTypegroup tsTypegroup = commonDao.findUniqueByProperty(TSTypegroup.class, "typegroupcode", typegroupcode);
        if (tsTypegroup == null) {
            tsTypegroup = new TSTypegroup();
            tsTypegroup.setTypegroupcode(typegroupcode);
            tsTypegroup.setTypegroupname(typgroupename);
            commonDao.save(tsTypegroup);
        }
        return tsTypegroup;
    }


    public TSTypegroup getTypeGroupByCode(String typegroupCode) {
        TSTypegroup tsTypegroup = commonDao.findUniqueByProperty(TSTypegroup.class, "typegroupcode", typegroupCode);
        return tsTypegroup;
    }


    public void initAllTypeGroups() {
//        List<TSTypegroup> typeGroups = this.commonDao.loadAll(TSTypegroup.class);
//        if (typeGroups != null && typeGroups.size() > 0) {
//            Map<String, Object> map = new HashMap();
//            try {
//                for (TSTypegroup tsTypegroup : typeGroups) {
//                    String typeGroupName = tsTypegroup.getTypegroupname();
//                    List<TSType> tsTypeList = tsTypegroup.getTSTypes();
//                    DataDictGroupTypeDetail detail = null;
//                    for (TSType tsType : tsTypeList) {
//                        detail = new DataDictGroupTypeDetail();
//                        detail.setShowName(tsType.getShowname());
//                        detail.setTypecode(tsType.getTypecode());
//                        detail.setTypename(tsType.getTypename());
//                        detail.setCreateDate(tsTypegroup.getCreateDate());
//                        detail.setTypegroupname(tsTypegroup.getTypegroupname());
//                        detail.setTypegroupcode(tsTypegroup.getTypegroupcode());
//                        detail.setTypeId(tsType.getId());
//                        detail.setGroupId(tsTypegroup.getId());
//                        map.put(tsType.getTypename(), detail);
//                    }
//                    map.clear();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    //updated by sc
    public void refleshTypesCach(TSType type) {
    }


    public void refleshTypeGroupCach() {
        if (ResourceUtil.allTypeGroups.size() > 0) {
            ResourceUtil.allTypeGroups.clear();
            List<TSTypegroup> typeGroups = this.commonDao.loadAll(TSTypegroup.class);
            for (TSTypegroup tsTypegroup : typeGroups) {
                ResourceUtil.allTypeGroups.put(tsTypegroup.getTypegroupname(), tsTypegroup);

            }
        }
    }

    /**
     * 刷新字典分组缓存&字典缓存
     */
    public void refreshTypeGroupAndTypes() {
//        ResourceUtil.allTypeGroups.clear();
//        List<TSTypegroup> typeGroups = this.commonDao.loadAll(TSTypegroup.class);
//        for (TSTypegroup tsTypegroup : typeGroups) {
//            ResourceUtil.allTypeGroups.put(tsTypegroup.getTypegroupcode().toLowerCase(), tsTypegroup);
//            List<TSType> types = this.commonDao.findByProperty(TSType.class, "TSTypegroup.id", tsTypegroup.getId());
//            ResourceUtil.allTypes.put(tsTypegroup.getTypegroupcode().toLowerCase(), types);
//        }
    }


    /**
     * 根据角色ID 和 菜单Id 获取 具有操作权限的按钮Codes
     *
     * @param roleId
     * @param functionId
     * @return
     */
    public Set<String> getOperationCodesByRoleIdAndFunctionId(String roleId, String functionId) {
        Set<String> operationCodes = new HashSet<String>();
        TSRole role = commonDao.get(TSRole.class, roleId);
        CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
        cq1.eq("TSRole.id", role.getId());
        cq1.eq("TSFunction.id", functionId);
        cq1.add();
        List<TSRoleFunction> rFunctions = getListByCriteriaQuery(cq1, false);
        if (null != rFunctions && rFunctions.size() > 0) {
            TSRoleFunction tsRoleFunction = rFunctions.get(0);
            if (null != tsRoleFunction.getOperation()) {
                String[] operationArry = tsRoleFunction.getOperation().split(",");
                for (int i = 0; i < operationArry.length; i++) {
                    operationCodes.add(operationArry[i]);
                }
            }
        }
        return operationCodes;
    }

    /**
     * 【规则： 反控制-查询授权的权限按钮Code；即授权的人进行按钮控制】
     * 根据用户ID 和 菜单Id 获取 具有操作权限的按钮Codes
     *
     * @param userId     用户ID
     * @param functionId 菜单ID
     * @return
     */
    public Set<String> getOperationCodesByUserIdAndFunctionId(String userId, String functionId) {
        Set<String> operationCodes = new HashSet<String>();
        List<TSRoleUser> rUsers = findByProperty(TSRoleUser.class, "TSUser.id", userId);
        for (TSRoleUser ru : rUsers) {
            TSRole role = ru.getTSRole();
            CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
            cq1.eq("TSRole.id", role.getId());
            cq1.eq("TSFunction.id", functionId);
            cq1.add();
            List<TSRoleFunction> rFunctions = getListByCriteriaQuery(cq1, false);
            if (null != rFunctions && rFunctions.size() > 0) {
                TSRoleFunction tsRoleFunction = rFunctions.get(0);
                if (null != tsRoleFunction.getOperation()) {
                    String[] operationArry = tsRoleFunction.getOperation().split(",");
                    for (int i = 0; i < operationArry.length; i++) {
                        operationCodes.add(operationArry[i]);
                    }
                }
            }
        }
        return operationCodes;
    }

    /**
     * 【规则： 查询未授权的页面控件权限（button、表单控件）】
     *
     * @param currLoginUser 用户ID
     * @param functionId    菜单ID
     */
    @Override
    public List<TSOperation> getOperationsByUserIdAndFunctionId(TSUser currLoginUser, String functionId) {
        String hql = "FROM TSOperation where functionid = '" + functionId + "'";
        List<TSOperation> operations = findHql(hql);
        if (operations == null || operations.size() < 1) {
            return null;
        }
        List<TSRoleUser> rUsers = findByProperty(TSRoleUser.class, "TSUser.id", currLoginUser.getId());
        for (TSRoleUser ru : rUsers) {
            TSRole role = ru.getTSRole();
            CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
            cq1.eq("TSRole.id", role.getId());
            cq1.eq("TSFunction.id", functionId);
            cq1.add();
            List<TSRoleFunction> rFunctions = getListByCriteriaQuery(cq1, false);
            if (null != rFunctions && rFunctions.size() > 0) {
                TSRoleFunction tsRoleFunction = rFunctions.get(0);
                if (oConvertUtils.isNotEmpty(tsRoleFunction.getOperation())) {
                    String[] operationArry = tsRoleFunction.getOperation().split(",");
                    for (int i = 0; i < operationArry.length; i++) {
                        for (int j = 0; j < operations.size(); j++) {
                            if (operationArry[i].equals(operations.get(j).getId())) {
                                operations.remove(j);
                                break;
                            }
                        }

                    }
                }
            }
        }
        return operations;
    }

    /**
     * 获取页面控件权限控制的
     * JS片段
     */
    public String getAuthFilterJS() {
        StringBuilder out = new StringBuilder();
        out.append("<script type=\"text/javascript\">");
        out.append("$(document).ready(function(){");
        if (!Globals.BUTTON_AUTHORITY_CHECK) {
            return "";
        } else {
            HttpServletRequest request = ContextHolderUtils.getRequest();
            Set<String> operationCodes = (Set<String>) request.getAttribute(Globals.OPERATIONCODES);
            if (null != operationCodes) {
                for (String MyoperationCode : operationCodes) {
                    if (oConvertUtils.isEmpty(MyoperationCode))
                        break;
                    TSOperation operation = this.getEntity(TSOperation.class, MyoperationCode);
                    if (operation.getOperationcode().startsWith(".") || operation.getOperationcode().startsWith("#")) {
                        if (operation.getOperationType().intValue() == Globals.OPERATION_TYPE_HIDE) {
                            //out.append("$(\""+name+"\").find(\"#"+operation.getOperationcode().replaceAll(" ", "")+"\").hide();");
                            out.append("$(\"" + operation.getOperationcode().replaceAll(" ", "") + "\").hide();");
                        } else {
                            //out.append("$(\""+name+"\").find(\"#"+operation.getOperationcode().replaceAll(" ", "")+"\").find(\":input\").attr(\"disabled\",\"disabled\");");
                            out.append("$(\"" + operation.getOperationcode().replaceAll(" ", "") + "\").attr(\"disabled\",\"disabled\");");
                            out.append("$(\"" + operation.getOperationcode().replaceAll(" ", "") + "\").find(\":input\").attr(\"disabled\",\"disabled\");");
                        }
                    }
                }
            } else {
                return "";
            }

        }
        out.append("});");
        out.append("</script>");
        return out.toString();
    }

//    public void flushRoleFunciton(String id, TSFunction newFunction) {
//        TSFunction functionEntity = this.getEntity(TSFunction.class, id);
//        if (functionEntity.getTSIcon() == null || !StringUtil.isNotEmpty(functionEntity.getTSIcon().getId())) {
//            return;
//        }
//        TSIcon oldIcon = this.getEntity(TSIcon.class, functionEntity.getTSIcon().getId());
//        if (!oldIcon.getIconClas().equals(newFunction.getTSIcon().getIconClas())) {
//            // 刷新缓存
//            HttpSession session = ContextHolderUtils.getSession();
//            TSUser user = ResourceUtil.getSessionUser();
//            List<TSRoleUser> rUsers = this.findByProperty(TSRoleUser.class, "TSUser.id", user.getId());
//            for (TSRoleUser ru : rUsers) {
//                TSRole role = ru.getTSRole();
//                session.removeAttribute(role.getId());
//            }
//        }
//    }

    public String generateOrgCode(String id, String pid) {

        int orgCodeLength = 2; // 默认编码长度
        if ("3".equals(ResourceUtil.getOrgCodeLengthType())) { // 类型2-编码长度为3，如001
            orgCodeLength = 3;
        }


        String newOrgCode = "";
        if (!StringUtils.hasText(pid)) { // 第一级编码
            String sql = "select max(t.org_code) orgCode from t_s_depart t where t.parentdepartid is null";
            Map<String, Object> pOrgCodeMap = commonDao.findOneForJdbc(sql);
            if (pOrgCodeMap.get("orgCode") != null) {
                String curOrgCode = pOrgCodeMap.get("orgCode").toString();
                newOrgCode = String.format("%0" + orgCodeLength + "d", Integer.valueOf(curOrgCode) + 1);
            } else {
                newOrgCode = String.format("%0" + orgCodeLength + "d", 1);
            }
        } else { // 下级编码
            String sql = "select max(t.org_code) orgCode from t_s_depart t where t.parentdepartid = ?";
            Map<String, Object> orgCodeMap = commonDao.findOneForJdbc(sql, pid);
            if (orgCodeMap.get("orgCode") != null) { // 当前基本有编码时
                String curOrgCode = orgCodeMap.get("orgCode").toString();
                String pOrgCode = curOrgCode.substring(0, curOrgCode.length() - orgCodeLength);
                String subOrgCode = curOrgCode.substring(curOrgCode.length() - orgCodeLength, curOrgCode.length());
                newOrgCode = pOrgCode + String.format("%0" + orgCodeLength + "d", Integer.valueOf(subOrgCode) + 1);
            } else { // 当前级别没有编码时
                String pOrgCodeSql = "select max(t.org_code) orgCode from t_s_depart t where t.id = ?";
                Map<String, Object> pOrgCodeMap = commonDao.findOneForJdbc(pOrgCodeSql, pid);
                String curOrgCode = pOrgCodeMap.get("orgCode").toString();
                newOrgCode = curOrgCode + String.format("%0" + orgCodeLength + "d", 1);
            }
        }

        return newOrgCode;
    }

    public Set<String> getOperationCodesByRoleIdAndruleDataId(String roleId,
                                                              String functionId) {
        Set<String> operationCodes = new HashSet<String>();
        TSRole role = commonDao.get(TSRole.class, roleId);
        CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
        cq1.eq("TSRole.id", role.getId());
        cq1.eq("TSFunction.id", functionId);
        cq1.add();
        List<TSRoleFunction> rFunctions = getListByCriteriaQuery(cq1, false);
        if (null != rFunctions && rFunctions.size() > 0) {
            TSRoleFunction tsRoleFunction = rFunctions.get(0);
            if (null != tsRoleFunction.getDataRule()) {
                String[] operationArry = tsRoleFunction.getDataRule().split(",");
                for (int i = 0; i < operationArry.length; i++) {
                    operationCodes.add(operationArry[i]);
                }
            }
        }
        return operationCodes;
    }

    public Set<String> getOperationCodesByUserIdAndDataId(TSUser currLoginUser,
                                                          String functionId) {
        Set<String> dataRulecodes = new HashSet<String>();
        List<TSRoleUser> rUsers = findByProperty(TSRoleUser.class, "TSUser.id", currLoginUser.getId());
        for (TSRoleUser ru : rUsers) {
            TSRole role = ru.getTSRole();
            CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
            cq1.eq("TSRole.id", role.getId());
            cq1.eq("TSFunction.id", functionId);
            cq1.add();
            List<TSRoleFunction> rFunctions = getListByCriteriaQuery(cq1, false);
            if (null != rFunctions && rFunctions.size() > 0) {
                TSRoleFunction tsRoleFunction = rFunctions.get(0);
                if (oConvertUtils.isNotEmpty(tsRoleFunction.getDataRule())) {
                    String[] operationArry = tsRoleFunction.getDataRule().split(",");
                    for (int i = 0; i < operationArry.length; i++) {
                        dataRulecodes.add(operationArry[i]);
                    }
                }
            }
        }
        return dataRulecodes;
    }

    /**
     * 加载所有图标
     *
     * @return
     */
    public void initAllTSIcons() {
        List<TSIcon> list = this.loadAll(TSIcon.class);
        for (TSIcon tsIcon : list) {
            ResourceUtil.allTSIcons.put(tsIcon.getId(), tsIcon);
        }
    }

    /**
     * 更新图标
     *
     * @param icon
     */
    public void upTSIcons(TSIcon icon) {
        ResourceUtil.allTSIcons.put(icon.getId(), icon);
    }

    /**
     * 更新图标
     *
     * @param icon
     */
    public void delTSIcons(TSIcon icon) {
        ResourceUtil.allTSIcons.remove(icon.getId());
    }

    @Override
    public void addDataLog(String tableName, String dataId, String dataContent) {

        int versionNumber = 0;

        Integer integer = commonDao.singleResult("select max(versionNumber) from TSDatalogEntity where tableName = '" + tableName + "' and dataId = '" + dataId + "'");
        if (integer != null) {
            versionNumber = integer.intValue();
        }

        TSDatalogEntity tsDatalogEntity = new TSDatalogEntity();
        tsDatalogEntity.setTableName(tableName);
        tsDatalogEntity.setDataId(dataId);
        tsDatalogEntity.setDataContent(dataContent);
        tsDatalogEntity.setVersionNumber(versionNumber + 1);
        commonDao.save(tsDatalogEntity);
    }

    /**
     * 获取二级管理员页面控件权限授权配置【二级管理员后台权限配置功能】
     *
     * @param groupId    部门角色组ID
     * @param functionId 选中菜单ID
     * @return
     * @Param type 0:部门管理员组/1:部门角色
     */
    @Override
    public Set<String> getDepartAuthGroupOperationSet(String groupId, String functionId, String type) {
        Set<String> operationCodes = new HashSet<String>();
        TSDepartAuthGroupEntity functionGroup = null;
        if (OrgConstants.GROUP_DEPART_ROLE.equals(type)) {
            TSRole role = commonDao.get(TSRole.class, groupId);
            CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
            cq1.eq("TSRole.id", role.getId());
            cq1.eq("TSFunction.id", functionId);
            cq1.add();
            List<TSRoleFunction> functionGroups = getListByCriteriaQuery(cq1, false);
            if (null != functionGroups && functionGroups.size() > 0) {
                TSRoleFunction tsFunctionGroup = functionGroups.get(0);
                if (null != tsFunctionGroup.getOperation()) {
                    String[] operationArry = tsFunctionGroup.getOperation().split(",");
                    for (int i = 0; i < operationArry.length; i++) {
                        operationCodes.add(operationArry[i]);
                    }
                }
            }
        } else {
            functionGroup = commonDao.get(TSDepartAuthGroupEntity.class, groupId);
            CriteriaQuery cq1 = new CriteriaQuery(TSDepartAuthgFunctionRelEntity.class);
            cq1.eq("tsDepartAuthGroup.id", functionGroup.getId());
            cq1.eq("tsFunction.id", functionId);
            cq1.add();
            List<TSDepartAuthgFunctionRelEntity> functionGroups = getListByCriteriaQuery(cq1, false);
            if (null != functionGroups && functionGroups.size() > 0) {
                TSDepartAuthgFunctionRelEntity tsFunctionGroup = functionGroups.get(0);
                if (null != tsFunctionGroup.getOperation()) {
                    String[] operationArry = tsFunctionGroup.getOperation().split(",");
                    for (int i = 0; i < operationArry.length; i++) {
                        operationCodes.add(operationArry[i]);
                    }
                }
            }
        }
        return operationCodes;
    }

    /**
     * 获取二级管理员数据权限授权配置【二级管理员后台权限配置功能】
     *
     * @param groupId    部门角色组ID
     * @param functionId 选中菜单ID
     * @return
     * @Param type  0:部门管理员组/1:部门角色
     */
    @Override
    public Set<String> getDepartAuthGroupDataRuleSet(String groupId, String functionId, String type) {
        Set<String> dataRuleCodes = new HashSet<String>();
        TSDepartAuthGroupEntity functionGroup = null;
        if (OrgConstants.GROUP_DEPART_ROLE.equals(type)) {
            TSRole role = commonDao.get(TSRole.class, groupId);
            CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
            cq1.eq("TSRole.id", role.getId());
            cq1.eq("TSFunction.id", functionId);
            cq1.add();
            List<TSRoleFunction> functionGroups = getListByCriteriaQuery(cq1, false);
            if (null != functionGroups && functionGroups.size() > 0) {
                TSRoleFunction tsFunctionGroup = functionGroups.get(0);
                if (null != tsFunctionGroup.getDataRule()) {
                    String[] dataRuleArry = tsFunctionGroup.getDataRule().split(",");
                    for (int i = 0; i < dataRuleArry.length; i++) {
                        dataRuleCodes.add(dataRuleArry[i]);
                    }
                }
            }
        } else {
            functionGroup = commonDao.get(TSDepartAuthGroupEntity.class, groupId);
            CriteriaQuery cq1 = new CriteriaQuery(TSDepartAuthgFunctionRelEntity.class);
            cq1.eq("tsDepartAuthGroup.id", functionGroup.getId());
            cq1.eq("tsFunction.id", functionId);
            cq1.add();
            List<TSDepartAuthgFunctionRelEntity> functionGroups = getListByCriteriaQuery(cq1, false);
            if (null != functionGroups && functionGroups.size() > 0) {
                TSDepartAuthgFunctionRelEntity tsFunctionGroup = functionGroups.get(0);
                if (null != tsFunctionGroup.getDatarule()) {
                    String[] dataRuleArry = tsFunctionGroup.getDatarule().split(",");
                    for (int i = 0; i < dataRuleArry.length; i++) {
                        dataRuleCodes.add(dataRuleArry[i]);
                    }
                }
            }
        }
        return dataRuleCodes;
    }

    /**
     * 系统启动的时候，先清除session历史记录
     * 清除的session记录包括redis和数据库
     */
//    @Override
//    public void clearSpringSession() {
//        try {
//            Set keySet = this.redisTemplateUtils.getFromKey(this.SESSIONPREFIX);
//            if (keySet != null) {
//                this.redisTemplateUtils.deleteKeys(keySet);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //清除数据库放入session的两张表
//        super.updateBySqlString(DELETE_SESSION_ATTRIBUTE_QUERY);
//        super.updateBySqlString(DELETE_SESSION_QUERY);
//    }

    /**
     * 基于java stream流做的过滤处理
     *
     * @param typeGroupName
     * @return
     */
    @Override
    public List<TSTypegroup> queryDictByCondition(String typeGroupName) {
        List<TSTypegroup> typegroupList = new ArrayList();
        try {
            typegroupList = super.findByQueryString(" from TSTypegroup");
        } catch (Exception e) {
            logger.error(e);
        }
        List<TSTypegroup> resultList = typegroupList;
        if (typegroupList != null && typegroupList.size() > 0) {
            if (oConvertUtils.isNotEmpty(typeGroupName)) {
                resultList = typegroupList.stream().filter(tsTypegroup -> tsTypegroup.getTypegroupname().contains(typeGroupName) || tsTypegroup.getTypegroupname().contains(typeGroupName.toLowerCase()) || tsTypegroup.getTypegroupname().contains(typeGroupName.toUpperCase()))
                        .collect(Collectors.toList());
            }
        }
        if (resultList == null) {
            resultList = new ArrayList();
        }
        return resultList;
    }

    @Override
    public void deleteMenu(TSFunction function) {
        function = super.getEntity(TSFunction.class, function.getId());
        this.updateBySqlString("delete from t_s_role_function where functionid='"
                        + function.getId() + "'");

        TSFunction parent = function.getTSFunction();
        if (parent != null) {
            parent.getTSFunctions().remove(function);
        }
        super.delete(function);
        this.addLog("Menu:"+function.getFunctionName()+" delete success", Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
    }

    @Override
    public boolean hasMenuAuth(String requestPath) {
        Long authSize = Long.valueOf(0);
        String rolecode = ResourceUtils.getCurrentUserRole();
        if (oConvertUtils.isNotEmpty(rolecode)) {
            String sql = "SELECT count(*) FROM t_s_function f,t_s_role_function  rf,t_s_role ru " +
                    " WHERE f.id=rf.functionid AND rf.roleid=ru.id AND " +
                    "ru.rolecode='" + rolecode + "' AND f.functionurl = '" + requestPath + "'";
            authSize = this.getCountForJdbc(sql);
        }
        return authSize > 0;
    }
}
