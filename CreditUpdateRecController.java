package com.ppms.creditupdaterecQuery.controller;

import com.alibaba.fastjson.JSONObject;
import com.ppms.creditupdaterecQuery.service.CreditUpdateRecServiceI;
import com.ppms.entity.CustomerMeterBalEntity;
import com.ppms.entity.MeterReplacementEntity;
import com.ppms.entity.ProcessChargeEntity;
import com.ppms.entity.RemoteRecEntity;
import com.ppms.tstypeQuery.service.TstypeServiceI;
import com.ppms.utils.DataReturn;
import com.ppms.vo.*;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Title: Controller
 * @Description: A_BALANCE_REC
 * @author zhangdaihao
 * @date 2018-04-28 14:29:04
 * @version V1.0
 *
 */
@Controller
@RequestMapping("/creditUpdateRecController")
public class CreditUpdateRecController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CreditUpdateRecController.class);

	@Autowired
	private CreditUpdateRecServiceI creditUpdateRecService;
	@Autowired
	private TstypeServiceI tstypeService;


	/**
	 * A_BALANCE_REC列表 页面跳转
	 *
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("ppms/creditupdaterecQuery/creditUpdateRecMainList");
	}

	/**
	 * A_BALANCE_REC列表 页面跳转
	 *
	 * @return
	 */
	@RequestMapping(params = "mainlist")
	public ModelAndView mainlist(HttpServletRequest request) {
		//return new ModelAndView("ppms/creditupdaterecQuery/creditUpdateRecMainListBase");

		ModelAndView modelAndView = new ModelAndView("ppms/creditupdaterecQuery/creditUpdateRecMainListBase");
		String updateFlag = tstypeService.getGroupIdByGroupCode("BALANCE_UPDATE_FLAG");
		List<Map<String, Object>> bUpdateFlagList = tstypeService.getTypeCodeAndNameByGroupId(updateFlag);
		String updateFlagList = JSONObject.toJSONString(bUpdateFlagList);
		modelAndView.addObject("updateFlagList",updateFlagList);
		return modelAndView;
	}

	/**
	 * A_BALANCE_REC列表 页面跳转
	 *
	 * @return
	 */
	@RequestMapping(params = "tabs")
	public ModelAndView tabs(HttpServletRequest request) {
		return new ModelAndView("ppms/creditupdaterecQuery/creditUpdateRecTabs");
	}

	/**
	 * A_BALANCE_REC列表 页面跳转
	 *
	 * @return
	 */
	@RequestMapping(params = "smslist")
	public ModelAndView smslist(HttpServletRequest request) {
		return new ModelAndView("ppms/creditupdaterecQuery/smsListBase");
	}

	/**
	 * A_BALANCE_REC列表 页面跳转
	 *
	 * @return
	 */
	@RequestMapping(params = "remotelist")
	public ModelAndView remotelist(HttpServletRequest request) {
		return new ModelAndView("ppms/creditupdaterecQuery/remoteListBase");
	}

	/**
	 * BALANCE_REMOTE_REC_VIEW 页面跳转
	 *
	 * @return
	 */
	@RequestMapping(params = "remotecontrollist")
	public ModelAndView remotecontrollist(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView("ppms/creditupdaterecQuery/remoteList");
		String controlStatus = tstypeService.getGroupIdByGroupCode("METER_CTRL_STATUS");
		List<Map<String, Object>> controlStatusList = tstypeService.getTypeCodeAndNameByGroupId(controlStatus);
		String ctlStatusList = JSONObject.toJSONString(controlStatusList);
		modelAndView.addObject("ctlStatusList",ctlStatusList);
		return modelAndView;
	}



	@RequestMapping(params = "replacementmainlist")
	public ModelAndView replacemenmaintlist(HttpServletRequest request) {
		return new ModelAndView("ppms/creditupdaterecQuery/replacementMainList");
	}

	@RequestMapping(params = "replacementlist")
	public ModelAndView replacementlist(HttpServletRequest request) {
		return new ModelAndView("ppms/creditupdaterecQuery/replacementList");
	}

	@RequestMapping(params = "replacetabs")
	public ModelAndView replacetabs(HttpServletRequest request) {
		return new ModelAndView("ppms/creditupdaterecQuery/replacementTabs");
	}

	@RequestMapping(params = "processchargelist")
	public ModelAndView processchargelist(HttpServletRequest request) {
		return new ModelAndView("ppms/creditupdaterecQuery/processChargeList");
	}
	/**
	 * easyui AJAX请求数据
	 *
	 * @param request
	 * @param response
	 * @param dataGrid
	 * @param
	 */
	@RequestMapping(params = "datagrid")
	public void datagrid(CreditURecResultVo resultVo,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		DataReturn dataReturn;
		try {
			dataReturn = this.creditUpdateRecService.getAllEntities(resultVo,dataGrid.getPage(),dataGrid.getRows(),request);
			dataGrid.setResults(dataReturn.getRows());
			dataGrid.setTotal((int)dataReturn.getTotal());
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
		}
		TagUtil.datagrid(response, dataGrid);
	}


	/**
	 * easyui AJAX请求数据
	 *
	 * @param request
	 * @param response
	 * @param dataGrid
	 * @param
	 */

////关联tab远程跳合闸查询界面
	@RequestMapping(params = "remotedatagrid")
	public void remotedatagrid(RemoteCtrlRecVo remoteRec, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {

		DataReturn dataReturn;
		if(request.getParameter("txnId") == null){
		}else{
			try {
				dataReturn = this.creditUpdateRecService.getremoteAllEntities(remoteRec,dataGrid.getPage(),dataGrid.getRows(),request);
				dataGrid.setResults(dataReturn.getRows());
				dataGrid.setTotal((int)dataReturn.getTotal());
			} catch (ParseException e) {
				logger.error(e.getMessage(),e);
			}
			TagUtil.datagrid(response, dataGrid);
		}

	}




	/**
	 * easyui AJAX请求数据
	 *
	 * @param request
	 * @param response
	 * @param dataGrid
	 * @param
	 */

	@RequestMapping(params = "smsdatagrid")
	public void smsdatagrid(SmsRecExVo smsRec,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		DataReturn dataReturn;
		if(request.getParameter("smsNo") == null){
		}else{
			try {
				dataReturn = this.creditUpdateRecService.getSmsAllEntities(smsRec,dataGrid.getPage(),dataGrid.getRows(),request);
				dataGrid.setResults(dataReturn.getRows());
				dataGrid.setTotal((int)dataReturn.getTotal());
			} catch (ParseException e) {
				logger.error(e.getMessage(),e);
			}
			TagUtil.datagrid(response, dataGrid);
		}
	}

     //单独远程跳合闸查询界面
	@RequestMapping(params = "remoteControldatagrid")
	public void remoteControldatagrid(RemoteRecVo remoteRec, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		DataReturn dataReturn;
		try {
			dataReturn = this.creditUpdateRecService.getControlAllEntities(remoteRec,dataGrid.getPage(),dataGrid.getRows(),request);
			dataGrid.setResults(dataReturn.getRows());
			dataGrid.setTotal((int)dataReturn.getTotal());
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
		}
		TagUtil.datagrid(response, dataGrid);
	}



	@RequestMapping(params = "replacementdatagrid")
	public void replacementdatagrid(ReplacementVo vo, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
//		CriteriaQuery cq = new CriteriaQuery(MeterReplacementEntity.class, dataGrid);
		//查询条件组装器
//		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, meterRepl, request.getParameterMap());
/*		try{
		//自定义追加查询条件
		}catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}*/
//		cq.add();
//		this.creditUpdateRecService.getDataGridReturn(cq, true);
		try {
			creditUpdateRecService.getMeterReplacementResultList(dataGrid,vo);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * jeecg_demo编辑页面跳转 CustomerInfoEntity
	 *
	 * @return
	 */
	@RequestMapping(params = "viewcustomerInfo")
	public ModelAndView viewcustomerInfo(RemoteRecEntity remoteRec,CustomerMeterBalEntity customerInfo, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(remoteRec.getAccNo())) {
			customerInfo = creditUpdateRecService.getEntity(CustomerMeterBalEntity.class, remoteRec.getAccNo());
			req.setAttribute("customerInfoPage", customerInfo);
		}
		return new ModelAndView("ppms/customerInfoQuery/customerInfo-view");
	}



	@RequestMapping(params = "processChargedatagrid")
	public void processChargedatagrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
//		CriteriaQuery cq = new CriteriaQuery(ProcessChargeEntity.class, dataGrid);
//		if(processCharge.getTxnId() == null || "".equals(processCharge.getTxnId())){
//		}else{
//			//查询条件组装器
//			org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, processCharge);
//			cq.add();
//			this.creditUpdateRecService.getDataGridReturn(cq, true);
//		}
		String txnId = request.getParameter("txnId");
		if(oConvertUtils.isNotEmpty(txnId)){
			creditUpdateRecService.getProcessChargeDataResultList(dataGrid,txnId);
			if(dataGrid.getResults().size()>0){
				for(ProcessChargeEntity chargeEntity : (List<ProcessChargeEntity>)dataGrid.getResults()){
					if(null != chargeEntity.getBilling()){
						chargeEntity.setBilling(chargeEntity.getBilling().setScale(2, BigDecimal.ROUND_DOWN));
					}
					if(null != chargeEntity.getCharge()){
						chargeEntity.setCharge(chargeEntity.getCharge().setScale(2, BigDecimal.ROUND_DOWN));
					}
					if(null != chargeEntity.getConsumption()){
						chargeEntity.setConsumption(chargeEntity.getConsumption().setScale(2, BigDecimal.ROUND_DOWN));
					}
					if(null != chargeEntity.getCurBal()){
						chargeEntity.setCurBal(chargeEntity.getCurBal().setScale(2, BigDecimal.ROUND_DOWN));
					}
					if(null != chargeEntity.getCurRdg()){
						chargeEntity.setCurRdg(chargeEntity.getCurRdg().setScale(2, BigDecimal.ROUND_DOWN));
					}
					if(null != chargeEntity.getGst()){
						chargeEntity.setGst(chargeEntity.getGst().setScale(2, BigDecimal.ROUND_DOWN));
					}
					if(null != chargeEntity.getLastBal()){
						chargeEntity.setLastBal(chargeEntity.getLastBal().setScale(2, BigDecimal.ROUND_DOWN));
					}
					if(null != chargeEntity.getLastRdg()){
						chargeEntity.setLastRdg(chargeEntity.getLastRdg().setScale(2, BigDecimal.ROUND_DOWN));
					}
				}
			}
		}
		TagUtil.datagrid(response, dataGrid);
	}

}
