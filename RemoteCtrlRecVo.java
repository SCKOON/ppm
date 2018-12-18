package com.ppms.vo;

import java.util.Date;

public class RemoteCtrlRecVo implements java.io.Serializable {

	private String txnId;
	private String accNo;
	private String txnType;
	private Date genTime;
	private String operStatus;
	private Date operTime;
	private Date exeStartTime;
	private Date exeEndTime;
	private String ctlStatus;
	private String meterId;



	public String getTxnId(){
		return this.txnId;
	}

	public void setTxnId(String txnId){
		this.txnId = txnId;
	}

	public Date getGenTime(){ return this.genTime;	}

	public void setGenTime(Date genTime){
		this.genTime = genTime;
	}

	public String getAccNo(){
		return this.accNo;
	}

	public void setAccNo(String accNo){
		this.accNo = accNo;
	}

	public Date getExeStartTime(){
		return this.exeStartTime;
	}

	public void setExeStartTime(Date exeStartTime){
		this.exeStartTime = exeStartTime;
	}

	public Date getExeEndTime(){
		return this.exeEndTime;
	}

	public void setExeEndTime(Date exeEndTime){
		this.exeEndTime = exeEndTime;
	}

	public String getCtlStatus(){
		return this.ctlStatus;
	}

	public void setCtlStatus(String ctlStatus){
		this.ctlStatus = ctlStatus;
	}

	public String getMeterId(){
		return this.meterId;
	}

	public void setMeterId(String meterId){
		this.meterId = meterId;
	}

	public String getTxnType(){
		return this.txnType;
	}

	public void setTxnType(String txnType){
		this.txnType = txnType;
	}

	public String getOperStatus(){
		return this.operStatus;
	}

	public void setOperStatus(String operStatus){ this.operStatus = operStatus; }

	public Date getoOperTime(){
		return this.operTime;
	}

	public void setOperTime(Date operTime){
		this.operTime = operTime;
	}

}
