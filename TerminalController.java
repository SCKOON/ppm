package com.ppms.terminalManagement.controller;

import com.alibaba.fastjson.JSONObject;
import com.constants.ChannelTypeEnum;
import com.constants.SiteStatus;
import com.ppms.entity.CounterEntity;
import com.ppms.terminalManagement.dto.TerminalDTO;
import com.ppms.entity.TerminalEntity;
import com.ppms.terminalManagement.service.TerminalServiceI;
import com.ppms.utils.DataReturn;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.model.json.ValidForm;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.oConvertUtils;
import org.springframework.stereotype.Controller;

import com.ppms.entity.ChannelEntity;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping(value = {"/terminalController"})
public class TerminalController extends BaseController {

    private static final Logger logger = Logger.getLogger(TerminalController.class);

    @Autowired
    private TerminalServiceI terminalService;

    @RequestMapping(params = "goTerminal")
    public ModelAndView goChannelManage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("ppms/terminal/terminalView");
        modelAndView.addObject("terminalList", getAllChannel());
        return modelAndView;
    }

    @RequestMapping(params = "datagrid")
    public void datagrid(TerminalEntity terminalEntity, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        terminalService.queryAllTerminalForView(terminalEntity, dataGrid);
        List<TerminalEntity> list = dataGrid.getResults();
        List<TerminalDTO> dtoList = new ArrayList<>();
        if (null != list && list.size() > 0) {
            for (TerminalEntity entity : list) {
                TerminalDTO dto = new TerminalDTO();
                try {
                    MyBeanUtils.copyBeanNotNull2Bean(entity, dto);
                } catch (Exception e) {
                    logger.error(e);
                }
                dto.setChannelName(entity.getChannelEntity().getName());
                dtoList.add(dto);
            }
        }
        dataGrid.setResults(dtoList);
        TagUtil.datagrid(response, dataGrid);
    }

    @RequestMapping(params = "goAdd")
    public ModelAndView goAdd(TerminalEntity terminalEntity, HttpServletRequest req) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("channelList", getSelectChannel());
        modelAndView.setViewName("ppms/terminal/terminalAdd");
        return modelAndView;
    }

    private String getAllChannel() {
        CriteriaQuery cq = new CriteriaQuery(ChannelEntity.class);
        cq.eq("type", ChannelTypeEnum.SP.getStatus());
        cq.add();
        List<ChannelEntity> channelEntityList = this.terminalService.getListByCriteriaQuery(cq, false);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (channelEntityList != null && channelEntityList.size() > 0) {
            for (ChannelEntity channelEntity : channelEntityList) {
                Map map = new HashMap();
                map.put("id", channelEntity.getId().toString());
                map.put("name", channelEntity.getName());
                list.add(map);
            }
        }

        return JSONObject.toJSONString(list);
    }

    private String getSelectChannel() {
        CriteriaQuery cq = new CriteriaQuery(ChannelEntity.class);
        cq.eq("type", ChannelTypeEnum.SP.getStatus());
        cq.eq("status", SiteStatus.ACTIVE.getStatus());
        cq.add();
        List<ChannelEntity> channelEntityList = null;
        try {
            channelEntityList = this.terminalService.getListByCriteriaQuery(cq, false);
        } catch (Exception e) {
            logger.error(e);
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (channelEntityList != null && channelEntityList.size() > 0) {
            for (ChannelEntity channelEntity : channelEntityList) {
                Map map = new HashMap();
                map.put("id", channelEntity.getId().toString());
                map.put("name", channelEntity.getName());
                list.add(map);
            }
        }

        return JSONObject.toJSONString(list);
    }

    @RequestMapping(params = "goUpdate")
    public ModelAndView goUpdate(TerminalEntity terminalEntity, HttpServletRequest req) {
        ModelAndView modelAndView = new ModelAndView();
        if (StringUtil.isNotEmpty(terminalEntity.getId())) {
            TerminalDTO dto = new TerminalDTO();
            try {
                terminalEntity = terminalService.getEntity(TerminalEntity.class, terminalEntity.getId());
                dto.setChannelId(terminalEntity.getChannelEntity().getId());
                MyBeanUtils.copyBeanNotNull2Bean(terminalEntity, dto);
            } catch (Exception e) {
                logger.error(e);
            }
            String time = terminalEntity.getOffTime();
            req.setAttribute("time", time.split("-"));
            req.setAttribute("terminalUpdatePage", dto);
        }
        modelAndView.addObject("channelList", getSelectChannel());
        modelAndView.setViewName("ppms/terminal/terminalEdit");
        return modelAndView;
    }


    @RequestMapping(params = "doAdd")
    @ResponseBody
    public AjaxJson doAdd(TerminalDTO terminal, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        String message = "Add center " +"["+ terminal.getName()+"]" + " failed";
        //数据从dto转移到po对象
        TerminalEntity terminalEntity = new TerminalEntity();
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        if (oConvertUtils.isNotEmpty(startTime) && oConvertUtils.isNotEmpty(endTime)) {
            String reg = "^([0-1][0-9]|[2][0-3]):([0-5][0-9])$";
            if (match(reg, startTime) && match(reg, endTime)) {
                terminalEntity.setOffTime(oConvertUtils.getString(startTime) + "-" + oConvertUtils.getString(endTime));
            } else {
                message = "startTime and endTime is invalid！";
                j.setMsg(message);
                return j;
            }
        }

        ChannelEntity channelEntity = new ChannelEntity();
        try {
            MyBeanUtils.copyBeanNotNull2Bean(terminal, terminalEntity);
            channelEntity.setId(terminal.getChannelId());
            terminalEntity.setChannelEntity(channelEntity);
            terminalService.save(terminalEntity);
            message = "Add center " +"["+ terminalEntity.getName()+"]" + " successfully";
        } catch (Exception e) {
            logger.error(e);
        }
        j.setMsg(message);
        return j;
    }

    @RequestMapping(params = "doUpdate")
    @ResponseBody
    public AjaxJson doUpdate(TerminalDTO terminal, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        String message = "Update center [ " + terminal.getName() + "] failed";
        TerminalEntity terminalEntity = new TerminalEntity();

        terminalEntity.setId(terminal.getId());
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        if (oConvertUtils.isNotEmpty(startTime) && oConvertUtils.isNotEmpty(endTime)) {
            String reg = "^([0-1][0-9]|[2][0-3]):([0-5][0-9])$";
            if (match(reg, startTime) && match(reg, endTime)) {
                terminalEntity.setOffTime(oConvertUtils.getString(startTime) + "-" + oConvertUtils.getString(endTime));
            } else {
                j.setMsg(message);
                return j;
            }
        }
        try {
            MyBeanUtils.copyBeanNotNull2Bean(terminal, terminalEntity);
            ChannelEntity channelEntity = new ChannelEntity();
            channelEntity.setId(terminal.getChannelId());
            terminalEntity.setChannelEntity(channelEntity);
            terminalService.saveOrUpdate(terminalEntity);
            message = "Update center " +"["+ terminalEntity.getName() +"]"+ " successfully";
        } catch (Exception e) {
            logger.error(e);
        }
        j.setMsg(message);
        return j;
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    @RequestMapping(params = "doDel")
    @ResponseBody
    public AjaxJson doDel(TerminalDTO terminal, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        String message = "Delete center failed";
        try {
            terminalService.delete(terminal);
            message = "Delete center successfully";
        } catch(BusinessException e){
            message = e.getMessage();
        } catch (Exception e) {
            logger.error(e);
        }
        j.setMsg(message);
        return j;
    }

    /**
     * 因为日志表在jeecg自身的库，所以涉及到夸库的情况，放在service就不能同时操作两个库
     *
     * @param ids
     * @param request
     * @return
     */
    @RequestMapping(params = "doBatchDel")
    @ResponseBody
    public AjaxJson doBatchDel(String ids, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        String message = "Delete center Failed";
        try {
            terminalService.batchDelete(ids);
            message = "Delete center successfully";
        } catch(BusinessException e){
            message = e.getMessage();
        } catch (Exception e) {
            logger.error(e);
        }
        j.setMsg(message);
        return j;
    }

    /**
     * 查询name是否有重名的
     *
     * @param request
     * @return
     */
    @RequestMapping(params = "queryNameExistOrNot")
    @ResponseBody
    public ValidForm queryNameExistOrNot(HttpServletRequest request) {
        ValidForm v = new ValidForm();
        String name = oConvertUtils.getString(request.getParameter("param"));
        String oldName = oConvertUtils.getString(request.getParameter("name"));
        List<TerminalEntity> terminalEntityList = new ArrayList<>();
        if (!StringUtils.isEmpty(name)) {
            CriteriaQuery criteriaQuery = new CriteriaQuery(TerminalEntity.class);
            criteriaQuery.eq("name", name);
            criteriaQuery.add();
            try {
                terminalEntityList = this.terminalService.getListByCriteriaQuery(criteriaQuery, false);
            } catch (Exception e) {
                logger.error(e);
            }
        }


        if (terminalEntityList != null && terminalEntityList.size() > 0 && !name.equals(oldName)) {
            v.setInfo("name is already existe");
            v.setStatus("n");
        }
        return v;
    }

    /**
     * 查询code是否有重名的
     *
     * @param request
     * @return
     */
    @RequestMapping(params = "queryCodeExistOrNot")
    @ResponseBody
    public ValidForm queryCodeExistOrNot(HttpServletRequest request) {

        ValidForm v = new ValidForm();
        String code = oConvertUtils.getString(request.getParameter("param"));
        String oldCode = oConvertUtils.getString(request.getParameter("code"));
        List<TerminalEntity> terminalEntityList = new ArrayList<>();
        if (!StringUtils.isEmpty(code)) {
            CriteriaQuery criteriaQuery = new CriteriaQuery(TerminalEntity.class);
            criteriaQuery.eq("code", code);
            criteriaQuery.add();
            try {
                terminalEntityList = this.terminalService.getListByCriteriaQuery(criteriaQuery, false);
            } catch (Exception e) {
                logger.error(e);
            }
        }


        if (terminalEntityList != null && terminalEntityList.size() > 0 && !oldCode.equals(code)) {
            v.setInfo("code is already existe");
            v.setStatus("n");
        }
        return v;
    }

    @RequestMapping(params = "queryCounterStatus")
    @ResponseBody
    public AjaxJson queryTerminalStatus(HttpServletRequest request) {
        AjaxJson json = new AjaxJson();
        Integer terminalId = Integer.parseInt(request.getParameter("id"));
        String status = request.getParameter("status");
        if (terminalId != null && oConvertUtils.isNotEmpty(status)) {
            TerminalEntity terminalEntity = new TerminalEntity();
            terminalEntity.setId(terminalId);
            terminalEntity.setStatus(status);
            try {
                json.setSuccess(terminalService.queryCounterStatusIsValid(terminalEntity));
            } catch (Exception e) {
                logger.error(e);
                json.setSuccess(false);
            }
        }
        return json;
    }

    @RequestMapping(params = "queryChannelStatus")
    @ResponseBody
    public AjaxJson queryChannelStatus(HttpServletRequest request) {
        AjaxJson json = new AjaxJson();
        json.setSuccess(false);
        int terminalId = Integer.parseInt(request.getParameter("id"));
        try {
            TerminalEntity terminalEntity = this.terminalService.getEntity(terminalId);
            if (terminalEntity != null) {
                if (terminalEntity.getChannelEntity() != null && terminalEntity.getChannelEntity().getStatus().equals(SiteStatus.ACTIVE.getStatus())) {
                    json.setSuccess(true);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return json;
    }

    @RequestMapping(params = "getCountersByTermianlCode")
    @ResponseBody
    public String getCountersByTerminalCode(HttpServletRequest request){
        String terminalCode = request.getParameter("center");
        if(oConvertUtils.isEmpty(terminalCode)){
            return "";
        }
        List<Map<String,String>> mapList = new ArrayList();
        List<CounterEntity> entityList = terminalService.queryCounterByTerminalCode(terminalCode);
        if(entityList.size()>0){
            for(CounterEntity entity: entityList){
                Map<String,String> map = new HashMap();
                map.put("name",entity.getName());
                map.put("code",entity.getCode());
                mapList.add(map);
            }
        }
        return JSONObject.toJSONString(mapList);
    }

    //add by yangyong 20180802 start
    @RequestMapping(params = "newDatagrid")
    public void datagrid(TerminalEntity terminalEntity, String chnlId, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) throws ParseException {

        DataReturn dataReturn = this.terminalService.getNewAllEntities(terminalEntity, dataGrid.getPage(), dataGrid.getRows(), request, chnlId);
        dataGrid.setResults(dataReturn.getRows());
        dataGrid.setTotal((int) dataReturn.getTotal());

        TagUtil.datagrid(response, dataGrid);
    }
    //add by yangyong 20180802 end

}
