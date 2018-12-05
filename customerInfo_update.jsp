<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>客户档案信息</title>
    <t:base type="jquery,easyui,tools,DatePicker"></t:base>
</head>
<body style="overflow-y: hidden" scroll="no">
<t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" action="customerInfoController.do?save" beforeSubmit="checkdate()">
    <input accNo="id" name="accNo" type="hidden" value="${customerInfoPage.accNo }">
    <table style="width: 600px;" cellpadding="0" cellspacing="1" class="formtable">
        <tr>
            <td align="right">
                <label class="Validform_label">
                    Account Opening Date
                </label>
            </td>
            <td class="value">
                <input class="Wdate" style="width: 150px" id="openDate" name="openDate" ignore="ignore"  type="text"  value="<fmt:formatDate value='${customerInfoPage.openDate}' type="date" pattern="yyyy-MM-dd"/>" readonly="readonly"/>
                <span class="Validform_checktip"></span>
            </td>
        </tr>

        <tr>
            <td align="right">
                <label class="Validform_label">
                    Scheduled Activation Date
                </label>
            </td>
            <td class="value">
                <input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" type="text" style="width: 150px" id="sheduledActivationDate" name="sheduledActivationDate" ignore="ignore"    value="<fmt:formatDate value='${customerInfoPage.sheduledActivationDate}' type="date" pattern="yyyy-MM-dd"/>" />
                <span style="color: red">*</span>
            </td>
        </tr>
        <tr>
            <td align="right">
                <label class="Validform_label">
                    Credit Balance
                </label>
            </td>
            <td class="value">
                <input class="inputxt" id="balance" name="balance" style="text-align: right" ignore="ignore"  value="${balance}" readonly="readonly"/>&nbsp;SGD
                <span class="Validform_checktip"></span>
            </td>
        </tr>
    </table>
</t:formvalid>
</body>
<script type="text/javascript">
    var solddate = '${customerInfoPage.sheduledActivationDate}';
    if(solddate!=""){
        solddate = solddate.substring(0,10);
    }
    //校验日期
    function checkdate(){
        var snewdate = $("input[name='sheduledActivationDate']").val();
        var olddate = new Date(solddate.replace(/-/g,"/"));
        var newdate = new Date(snewdate.replace(/-/g,"/"));
        if(newdate < olddate) {
            alertTip("The activation date is eailer than the original activation date. Please re-edit.");
            return false;
        }
    }
</script>

