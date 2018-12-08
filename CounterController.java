package com.ppms.counterManagement.controller;

import com.alibaba.fastjson.JSONObject;
import com.constants.ChannelTypeEnum;
import com.constants.SiteStatus;
import com.ppms.counterManagement.dto.CounterDTO;
import com.ppms.counterManagement.service.CounterServiceI;
import com.ppms.entity.CounterEntity;
import com.ppms.entity.TerminalEntity;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.common.model.json.ValidForm;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping(value = {"/counterController"})
public class CounterController extends BaseController {

    private static final Logger logger = Logger.getLogger(CounterController.class);

    @Autowired
    private CounterServiceI counterServiceI;

    @RequestMapping(params = "goCounter")
    public ModelAndView goCounter() {
        ModelAndView view = new ModelAndView("ppms/counter/counterView");
        view.addObject("terminalList", getAllCenter());
        return view;
    }

    private String getAllCenter() {
        CriteriaQuery cq = new CriteriaQuery(TerminalEntity.class);
        cq.createAlias("channelEntity", "channelEntity");
        cq.eq("channelEntity.type", ChannelTypeEnum.SP.getStatus());
        cq.add();
        List<TerminalEntity> terminalEntityList = this.counterServiceI.getListByCriteriaQuery(cq, false);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (terminalEntityList != null && terminalEntityList.size() > 0) {
            for (TerminalEntity entity : terminalEntityList) {
                Map map = new HashMap();
                map.put("code", entity.getCode());
                map.put("name", entity.getName());
                list.add(map);
            }
        }

        return JSONObject.toJSONString(list);
    }

    @RequestMapping(params = "datagrid")
    public void datagrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        logger.info("start query time["+new Date()+"]");
        counterServiceI.queryListForView(dataGrid, request);
        List<CounterEntity> list = dataGrid.getResults();
        List<CounterDTO> dtoList = new ArrayList();
        if (null != list && list.size() > 0) {
            for (CounterEntity entity : list) {
                CounterDTO dto = new CounterDTO();
                try {
                    MyBeanUtils.copyBeanNotNull2Bean(entity, dto);
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
                dto.setTerminalName(entity.getTerminalEntity().getName());
                dtoList.add(dto);
            }
        }
        dataGrid.setResults(dtoList);
        logger.info("end query time["+new Date()+"]");
        TagUtil.datagrid(response, dataGrid);
    }

    @RequestMapping(params = "goAdd")
    public ModelAndView goAdd(CounterEntity counterEntity, HttpServletRequest req) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("ppms/counter/counterAdd");
        modelAndView.addObject("terminalList", getSelectTerminal());
        return modelAndView;
    }

    private String getSelectTerminal() {
        CriteriaQuery cq = new CriteriaQuery(TerminalEntity.class);
        cq.eq("status", SiteStatus.ACTIVE.getStatus());
        cq.add();
        List<TerminalEntity> terminalEntityList = this.counterServiceI.getListByCriteriaQuery(cq, false);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (terminalEntityList != null && terminalEntityList.size() > 0) {
            for (TerminalEntity terminalEntity : terminalEntityList) {
                Map map = new HashMap();
                map.put("id", terminalEntity.getId().toString());
                map.put("name", terminalEntity.getName());
                list.add(map);
            }
        }

        return JSONObject.toJSONString(list);
    }

    @RequestMapping(params = "goUpdate")
    public ModelAndView goUpdate(CounterEntity counterEntity, HttpServletRequest req) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("ppms/counter/counterEdit");
        if (StringUtil.isNotEmpty(counterEntity.getId())) {
            CounterDTO dto = new CounterDTO();
            try {
                counterEntity = counterServiceI.getEntity(CounterEntity.class, counterEntity.getId());
                dto.setTerminalId(counterEntity.getTerminalEntity().getId());
                MyBeanUtils.copyBeanNotNull2Bean(counterEntity, dto);
            } catch (Exception e) {
                logger.error(e);
            }
            req.setAttribute("counterUpdatePage", dto);
        }
        modelAndView.addObject("terminalList", getSelectTerminal());
        return modelAndView;
    }


    @RequestMapping(params = "doAdd")
    @ResponseBody
    public AjaxJson doAdd(CounterDTO counterDTO, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        String message = "Add terminal [" + counterDTO.getName() + "] failed";
        if (counterDTO.getTerminalId() != null) {
            CounterEntity counterEntity = new CounterEntity();
            TerminalEntity terminal = new TerminalEntity();
            terminal.setId(counterDTO.getTerminalId());
            try {
                MyBeanUtils.copyBeanNotNull2Bean(counterDTO, counterEntity);
                counterEntity.setTerminalEntity(terminal);
                //o1 created
                counterEntity.setStatus(SiteStatus.CREATED.getStatus());
                counterServiceI.save(counterEntity);
                message = "Add terminal [" + counterDTO.getName() + "] successfully";
            } catch (Exception e) {
                logger.error(e);
            }
        }else{
            message = "Current terminal has not bind to a center!";
        }
        j.setMsg(message);
        return j;
    }

    @RequestMapping(params = "doUpdate")
    @ResponseBody
    public AjaxJson doUpdate(CounterDTO counterDTO, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        String message = "Update terminal [" + counterDTO.getName() + "] failed";
        if (counterDTO.getId() != null && counterDTO.getTerminalId() != null) {
            CounterEntity counterEntity = new CounterEntity();
            TerminalEntity terminalEntity = new TerminalEntity();
            terminalEntity.setId(counterDTO.getTerminalId());
            try {
                MyBeanUtils.copyBeanNotNull2Bean(counterDTO, counterEntity);
                counterEntity.setTerminalEntity(terminalEntity);
                counterServiceI.saveOrUpdate(counterEntity);
                message = "Update terminal [" + counterDTO.getName() + "] successfully";
            }catch(BusinessException e) {
                logger.error(e);
                message = e.getMessage();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        j.setMsg(message);
        return j;
    }

    @RequestMapping(params = "doDel")
    @ResponseBody
    public AjaxJson doDel(HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        j.setSuccess(false);
        String message = "Delete terminal failed";
        CounterEntity counterEntity = new CounterEntity();
        String id = request.getParameter("id");
        if (oConvertUtils.isNotEmpty(id)) {
            try {
                counterEntity.setId(Integer.parseInt(id));
                counterEntity = counterServiceI.getEntity(CounterEntity.class, counterEntity.getId());
                counterServiceI.delete(counterEntity);
                j.setSuccess(true);
                message = "Delete terminal successfully";
            } catch (Exception e) {
                logger.error(e);
            }
        }
        j.setMsg(message);
        return j;
    }

    @RequestMapping(params = "doBatchDel")
    @ResponseBody
    public AjaxJson doBatchDel(String ids, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        message = "Delete terminals successfully";
        try {
            counterServiceI.batchDelete(ids);
        } catch (Exception e) {
            message = "Delete terminals failed";
            logger.error(e);
        }
        j.setMsg(message);
        return j;
    }

    /**
     * 查询counter_name和counter_code是否有重名的
     *
     * @param request
     * @return
     */
    @RequestMapping(params = "queryExistOrNot")
    @ResponseBody
    public String queryExistOrNot(HttpServletRequest request) {
        Map<String, Boolean> map = new HashMap<>();
        boolean nameFlag = false;
        boolean codeFlag = false;

        String name = request.getParameter("name");
        String code = request.getParameter("code");
        CriteriaQuery criteriaQuery = null;
        List<CounterEntity> counterEntityList = null;
        if (!StringUtils.isEmpty(name)) {
            criteriaQuery = new CriteriaQuery(CounterEntity.class);
            criteriaQuery.eq("name", name);
            criteriaQuery.add();
            try {
                counterEntityList = this.counterServiceI.getListByCriteriaQuery(criteriaQuery, false);
                if (counterEntityList != null && counterEntityList.size() > 0) {
                    nameFlag = true;
                }
            } catch (Exception e) {
                nameFlag = true;
            }
        }
        if (!StringUtils.isEmpty(code)) {
            criteriaQuery = new CriteriaQuery(CounterEntity.class);
            criteriaQuery.eq("code", code);
            criteriaQuery.add();
            try {
                counterEntityList = this.counterServiceI.getListByCriteriaQuery(criteriaQuery, false);
                if (counterEntityList != null && counterEntityList.size() > 0) {
                    codeFlag = true;
                }
            } catch (Exception e) {
                codeFlag = true;
            }
        }
        map.put("nameFlag", nameFlag);
        map.put("codeFlag", codeFlag);
        return JSONObject.toJSONString(map);
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
        List<CounterEntity> counterEntities = new ArrayList<>();
        if (!StringUtils.isEmpty(name)) {
            CriteriaQuery criteriaQuery = new CriteriaQuery(CounterEntity.class);
            criteriaQuery.eq("name", name);
            criteriaQuery.add();
            try {
                counterEntities = this.counterServiceI.getListByCriteriaQuery(criteriaQuery, false);
            } catch (Exception e) {
                logger.error(e);
            }
        }


        if (counterEntities != null && counterEntities.size() > 0 && !name.equals(oldName)) {
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
        List<CounterEntity> counterEntities = new ArrayList<>();
        if (!StringUtils.isEmpty(code)) {
            CriteriaQuery criteriaQuery = new CriteriaQuery(CounterEntity.class);
            criteriaQuery.eq("code", code);
            criteriaQuery.add();
            try {
                counterEntities = this.counterServiceI.getListByCriteriaQuery(criteriaQuery, false);
            } catch (Exception e) {
                logger.error(e);
            }
        }


        if (counterEntities != null && counterEntities.size() > 0 && !oldCode.equals(code)) {
            v.setInfo("code is already existe");
            v.setStatus("n");
        }
        return v;
    }

    @RequestMapping(params = "queryTerminalStatus")
    @ResponseBody
    public AjaxJson queryChannelStatus(HttpServletRequest request) {
        AjaxJson json = new AjaxJson();
        json.setSuccess(false);
        int terminalId = Integer.parseInt(request.getParameter("id"));
        try {
            CounterEntity counterEntity = this.counterServiceI.getEntity(terminalId);
            if (counterEntity != null) {
                if (counterEntity.getTerminalEntity() != null && counterEntity.getTerminalEntity().getStatus() != null && counterEntity.getTerminalEntity().getStatus().equals(SiteStatus.ACTIVE.getStatus())) {
                    json.setSuccess(true);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return json;
    }

}
