package org.jeecgframework.web.system.controller.core;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.*;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil;
import org.jeecgframework.core.util.MutiLangUtil;
import org.jeecgframework.core.util.NumberComparator;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.tag.vo.datatable.SortDirection;
import org.jeecgframework.tag.vo.easyui.ComboTreeModel;
import org.jeecgframework.tag.vo.easyui.TreeGridModel;
import org.jeecgframework.web.system.pojo.base.*;
import org.jeecgframework.web.system.service.FunctionServiceI;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.web.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 菜单权限处理类
 *
 * @author 张代浩
 */
//@Scope("prototype")
@Controller
@RequestMapping("/functionController")
public class FunctionController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(FunctionController.class);
    private UserService userService;
    private SystemService systemService;

    @Autowired
    private FunctionServiceI functionServiceI;

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    public UserService getUserService() {
        return userService;

    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 权限列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "function")
    public ModelAndView function(ModelMap model) {
        return new ModelAndView("system/function/functionList");
    }

    /**
     * 操作列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "operation")
    public ModelAndView operation(HttpServletRequest request, String functionId) {
        request.setAttribute("functionId", functionId);
        return new ModelAndView("system/operation/operationList");
    }

    /**
     * 数据规则列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "dataRule")
    public ModelAndView operationData(HttpServletRequest request,
                                      String functionId) {
        request.setAttribute("functionId", functionId);
        return new ModelAndView("system/dataRule/ruleDataList");
    }

    /**
     * easyuiAJAX请求数据
     *
     * @param request
     * @param response
     * @param dataGrid
     */

    @RequestMapping(params = "datagrid")
    public void datagrid(HttpServletRequest request,
                         HttpServletResponse response, DataGrid dataGrid) {
        CriteriaQuery cq = new CriteriaQuery(TSFunction.class, dataGrid);
        this.systemService.getDataGridReturn(cq, true);
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * easyuiAJAX请求数据
     *
     * @param request
     * @param response
     * @param dataGrid
     */

    @RequestMapping(params = "opdategrid")
    public void opdategrid(HttpServletRequest request,
                           HttpServletResponse response, DataGrid dataGrid) {
        CriteriaQuery cq = new CriteriaQuery(TSOperation.class, dataGrid);
        String functionId = oConvertUtils.getString(request
                .getParameter("functionId"));
        cq.eq("TSFunction.id", functionId);
        cq.add();
        this.systemService.getDataGridReturn(cq, true);
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * 删除权限
     *
     * @param function
     * @return
     */
    @RequestMapping(params = "del")
    @ResponseBody
    public AjaxJson del(TSFunction function, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        String message = "Delete successfully";
        try {
            functionServiceI.delete(function);
        } catch (BusinessException e) {
            message = e.getMessage();
            logger.error(e);
        } catch (Exception e) {
            message = "Delete failed";
            logger.error(e);
        }

        try {
            systemService.addLog(message, Globals.Log_Type_DEL,
                    Globals.Log_Leavel_INFO);
        } catch (Exception e) {
            logger.error(e);
        }

        // // 删除权限时先删除权限与角色之间关联表信息
        // List<TSRoleFunction> roleFunctions =
        // systemService.findByProperty(TSRoleFunction.class, "TSFunction.id",
        // function.getId());
        //
        // if (roleFunctions.size() > 0) {
        // j.setMsg("菜单已分配无法删除");
        //
        // }
        // else {
        // userService.delete(function);
        // systemService.addLog(message, Globals.Log_Type_DEL,
        // Globals.Log_Leavel_INFO);
        // }

        j.setMsg(message);
        return j;
    }

    /**
     * 删除操作
     * 删除操作时同步更新已分配此操作的的  角色功能记录中的oeration 字段。
     *
     * @param operation
     * @return
     */
    @RequestMapping(params = "delop")
    @ResponseBody
    public AjaxJson delop(TSOperation operation, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        operation = systemService.getEntity(TSOperation.class,
                operation.getId());
        message = "Operate successfully";
        userService.delete(operation);

        String operationId = operation.getId();
        String hql = "from TSRoleFunction rolefun where rolefun.operation like '%" + operationId + "%'";
        List<TSRoleFunction> roleFunctions = userService.findByQueryString(hql);
        for (TSRoleFunction roleFunction : roleFunctions) {
            String newOper = roleFunction.getOperation().replace(operationId + ",", "");
            if (roleFunction.getOperation().length() == newOper.length()) {
                newOper = roleFunction.getOperation().replace(operationId, "");
            }
            roleFunction.setOperation(newOper);
            userService.updateEntitie(roleFunction);
        }


        systemService.addLog(message, Globals.Log_Type_DEL,
                Globals.Log_Leavel_INFO);

        j.setMsg(message);

        return j;
    }


    /**
     * 权限录入
     *
     * @param function
     * @return
     */
    @RequestMapping(params = "saveFunction")
    @ResponseBody
    public AjaxJson saveFunction(TSFunction function, HttpServletRequest request) {
        String message = "";
        AjaxJson j = new AjaxJson();
        function.setFunctionUrl(function.getFunctionUrl().trim());
        String functionOrder = function.getFunctionOrder();
        if (StringUtils.isEmpty(functionOrder)) {
            function.setFunctionOrder("0");
        }
        if (function.getTSFunction().getId().equals("")) {
            function.setTSFunction(null);
        } else {
            TSFunction parent = systemService.getEntity(TSFunction.class, function.getTSFunction().getId());
            function.setFunctionLevel(Short.valueOf(parent.getFunctionLevel() + 1 + ""));
        }
        if (StringUtil.isNotEmpty(function.getId())) {
            message = "Update Menu:" + function.getFunctionName() + "  failed";
            try {
                functionServiceI.update(function);
                message = "Update Menu:" + function.getFunctionName() + "  successfully";
                systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
            } catch(BusinessException e){
                message = e.getMessage();
            } catch (Exception e) {
                logger.error(e);
            }
        } else {
            message = "Add Menu:" + function.getFunctionName() + " failed";
            function.setFunctionOrder(function.getFunctionOrder());
            try {
                functionServiceI.save(function);
                message = "Add Menu:" + function.getFunctionName() + " successfully";
                systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
            } catch(BusinessException e){
                message = e.getMessage();
            } catch (Exception e) {
                logger.error(e);
            }
        }

        j.setMsg(message);
        return j;
    }

    /**
     * 权限列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "addorupdate")
    public ModelAndView addorupdate(TSFunction function, HttpServletRequest req) {
        String functionid = req.getParameter("id");
        try {
            List<TSIcon> iconlist = systemService
                    .findByQueryString("from TSIcon where iconType != 3");
            req.setAttribute("iconlist", iconlist);
            List<TSIcon> iconDeskList = systemService
                    .findByQueryString("from TSIcon where iconType = 3");
            req.setAttribute("iconDeskList", iconDeskList);
        } catch (Exception e) {
            logger.error(e);
        }

        if (functionid != null) {
            try {
                function = systemService.getEntity(TSFunction.class, functionid);
            } catch (Exception e) {
                logger.error(e);
            }
        }
        if (function.getTSFunction() != null
                && function.getTSFunction().getId() != null) {
            function.setFunctionLevel((short) 1);
            try {
                function.setTSFunction((TSFunction) systemService.getEntity(
                        TSFunction.class, function.getTSFunction().getId()));
            } catch (Exception e) {
                logger.error(e);
            }
        }
        req.setAttribute("function", function);
        return new ModelAndView("system/function/function");
    }

    /**
     * 操作列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "addorupdateop")
    public ModelAndView addorupdateop(TSOperation operation,
                                      HttpServletRequest req) {
        List<TSIcon> iconlist = systemService.getList(TSIcon.class);
        req.setAttribute("iconlist", iconlist);
        if (operation.getId() != null) {
            operation = systemService.getEntity(TSOperation.class,
                    operation.getId());
            req.setAttribute("operation", operation);
        }
        String functionId = oConvertUtils.getString(req
                .getParameter("functionId"));
        req.setAttribute("functionId", functionId);
        return new ModelAndView("system/operation/operation");
    }

    /**
     * 权限列表
     */
    @RequestMapping(params = "functionGrid")
    @ResponseBody
    public List<TreeGrid> functionGrid(HttpServletRequest request, TreeGrid treegrid, Integer type) {
        CriteriaQuery cq = new CriteriaQuery(TSFunction.class);
        String selfId = request.getParameter("selfId");
        if (selfId != null) {
            cq.notEq("id", selfId);
        }
        if (treegrid.getId() != null) {
            cq.eq("TSFunction.id", treegrid.getId());
        }
        if (treegrid.getId() == null) {
            cq.isNull("TSFunction");
        }
        if (type != null) {
            cq.eq("functionType", type.shortValue());
        }
        cq.addOrder("functionOrder", SortDirection.asc);
        cq.add();

        //获取装载数据权限的条件HQL
        cq = HqlGenerateUtil.getDataAuthorConditionHql(cq, new TSFunction());
        cq.add();


        List<TSFunction> functionList = null;
        try {
            functionList = systemService.getListByCriteriaQuery(cq, false);
        } catch (Exception e) {
            logger.error(e);
        }
        List<TreeGrid> treeGrids = new ArrayList<TreeGrid>();
        if (functionList != null && functionList.size() > 0) {

            Collections.sort(functionList, new NumberComparator());

            TreeGridModel treeGridModel = new TreeGridModel();
            treeGridModel.setIcon("TSIcon_iconPath");
            treeGridModel.setTextField("functionName");
            treeGridModel.setParentText("TSFunction_functionName");
            treeGridModel.setParentId("TSFunction_id");
            treeGridModel.setSrc("functionUrl");
            treeGridModel.setIdField("id");
            treeGridModel.setChildList("TSFunctions");
            // 添加排序字段
            treeGridModel.setOrder("functionOrder");

            treeGridModel.setIconStyle("functionIconStyle");


            treeGridModel.setFunctionType("functionType");

            try {
                treeGrids = systemService.treegrid(functionList, treeGridModel);
            } catch (Exception e) {
                logger.error(e);
            }
            for (TreeGrid tg : treeGrids) {
                if ("closed".equals(tg.getState())) tg.setSrc("");
            }
        }


//        MutiLangUtil.setMutiTree(treeGrids);
        return treeGrids;
    }

    /**
     * 权限列表
     */
    @RequestMapping(params = "functionList")
    @ResponseBody
    public void functionList(HttpServletRequest request,
                             HttpServletResponse response, DataGrid dataGrid) {
        CriteriaQuery cq = new CriteriaQuery(TSFunction.class, dataGrid);
        String id = oConvertUtils.getString(request.getParameter("id"));
        cq.isNull("TSFunction");
        if (id != null) {
            cq.eq("TSFunction.id", id);
        }
        cq.add();
//        List<TSFunction> functionList = systemService.getListByCriteriaQuery(
//                cq, false);
//        List<TreeGrid> treeGrids = new ArrayList<TreeGrid>();
        try {
            this.systemService.getDataGridReturn(cq, true);
        } catch (Exception e) {
            logger.error(e);
        }
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * 父级权限下拉菜单
     */
    @RequestMapping(params = "setPFunction")
    @ResponseBody
    public List<ComboTree> setPFunction(HttpServletRequest request,
                                        ComboTree comboTree) {
        CriteriaQuery cq = new CriteriaQuery(TSFunction.class);
        if (null != request.getParameter("selfId")) {
            cq.notEq("id", request.getParameter("selfId"));
        }
        if (comboTree.getId() != null) {
            cq.eq("TSFunction.id", comboTree.getId());
        }
        if (comboTree.getId() == null) {
            cq.isNull("TSFunction");
        }
        cq.add();
        List<TSFunction> functionList = systemService.getListByCriteriaQuery(cq, false);
        List<ComboTree> comboTrees = new ArrayList<ComboTree>();
        ComboTreeModel comboTreeModel = new ComboTreeModel("id", "functionName", "TSFunctions");
        comboTrees = systemService.ComboTree(functionList, comboTreeModel, null, false);
        MutiLangUtil.setMutiTree(comboTrees);
        return comboTrees;
    }

    /**
     * 菜单模糊检索功能
     *
     * @return
     */
    @RequestMapping(params = "searchApp")
    public ModelAndView searchApp(TSFunction function, HttpServletRequest req) {

        String name = req.getParameter("name");
        String menuListMap = "";
        CriteriaQuery cq = new CriteriaQuery(TSFunction.class);

        cq.notEq("functionLevel", Short.valueOf("0"));
        if (name == null || "".equals(name)) {
            cq.isNull("TSFunction");
        } else {
            String name1 = "%" + name + "%";
            cq.like("functionName", name1);
        }
        cq.add();
        List<TSFunction> functionList = systemService.getListByCriteriaQuery(
                cq, false);
        if (functionList.size() > 0 && functionList != null) {
            for (int i = 0; i < functionList.size(); i++) {
                String icon = "";
                if (!"".equals(functionList.get(i).getTSIconDesk())
                        && functionList.get(i).getTSIconDesk() != null) {
                    icon = functionList.get(i).getTSIconDesk().getIconPath();
                } else {
                    icon = "plug-in/sliding/icon/default.png";
                }
                menuListMap = menuListMap
                        + "<div type='"
                        + i
                        + 1
                        + "' class='menuSearch_Info' id='"
                        + functionList.get(i).getId()
                        + "' title='"
                        + functionList.get(i).getFunctionName()
                        + "' url='"
                        + functionList.get(i).getFunctionUrl()
                        + "' icon='"
                        + icon
                        + "' style='float:left;left: 0px; top: 0px;margin-left: 30px;margin-top: 20px;'>"
                        + "<div ><img alt='"
                        + functionList.get(i).getFunctionName()
                        + "' src='"
                        + icon
                        + "'></div>"
                        + "<div class='appButton_appName_inner1' style='color:#333333;'>"
                        + functionList.get(i).getFunctionName() + "</div>"
                        + "<div class='appButton_appName_inner_right1'></div>" +
                        // "</div>" +
                        "</div>";
            }
        } else {
            menuListMap = menuListMap + "很遗憾，在系统中没有检索到与“" + name + "”相关的信息！";
        }
        // menuListMap = menuListMap + "</div>";
        req.setAttribute("menuListMap", menuListMap);
        return new ModelAndView("system/function/menuAppList");
    }


    public int justHaveDataRule(TSDataRule dataRule) {
        String sql = "SELECT id FROM t_s_data_rule WHERE functionId='" + dataRule.getTSFunction()
                .getId() + "' AND rule_column='" + dataRule.getRuleColumn() + "' AND rule_conditions='" + dataRule
                .getRuleConditions() + "'";

        sql += " AND rule_column IS NOT NULL AND rule_column <> ''";

        List<String> hasOperList = this.systemService.findListbySql(sql);
        return hasOperList.size();
    }

    //------------------------------------------------------------------------------------------------------------
    @RequestMapping(params = "checkFunctionName")
    @ResponseBody
    public ValidForm checkFunctionName(HttpServletRequest request) {
        ValidForm v = new ValidForm();
        String name = oConvertUtils.getString(request.getParameter("param"));
        String oldname = oConvertUtils.getString(request.getParameter("name"));
        String pid = oConvertUtils.getString(request.getParameter("pid"));

        Map<String, Object> map = new HashMap();
        String hql = " from TSFunction as entity where 1=1 and entity.functionName =:functionName";
        map.put("functionName", name);
        if (oConvertUtils.isNotEmpty(pid)) {
            hql += " and entity.TSFunction.id = :functionId";
            map.put("functionId", pid);
        }else{
            hql += " and entity.functionLevel = :functionLevel";
            map.put("functionLevel", new Short("0"));
        }
        Query query = this.systemService.getSession().createQuery(hql);
        query.setProperties(map);
        List<Object> types = query.list();
        if (types.size() > 0 && !name.equals(oldname)) {
            v.setInfo("function name has already existed");
            v.setStatus("n");
        }

        return v;
    }
}
