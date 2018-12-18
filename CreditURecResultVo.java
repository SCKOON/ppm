package com.ppms.vo;

import java.math.BigDecimal;
import java.util.Date;

 
public class CreditURecResultVo {

    private String accNo;
	//private String meterId;
    private BigDecimal curBal;
    private String updateFlag;
    private Date updateTime;
    private String ctlNo;
    private String smsNo;
    private BigDecimal lastBal;
   
    

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public BigDecimal getCurBal() {
        return curBal;
    }

    public void setCurBal(BigDecimal curBal) {
        this.curBal = curBal;
    }

    public String getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(String updateFlag) {
        this.updateFlag = updateFlag;
    }


    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getCtlNo() {
        return ctlNo;
    }

    public void setCtlNo(String ctlNo) {
        this.ctlNo = ctlNo;
    }

    public String getSmsNo() {
        return smsNo;
    }

    public void setSmsNo(String smsNo) {
        this.smsNo = smsNo;
    }
    
    public BigDecimal getLastBal() {
        return lastBal;
    }

    public void setLastBal(BigDecimal lastBal) {
        this.lastBal = lastBal;
    }
    
}
