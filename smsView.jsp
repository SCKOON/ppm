<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker,autocomplete"></t:base>
<div class="easyui-layout" fit="true">
    <div region="center" style="padding:0px;border:0px">
        <t:datagrid name="smsQuery" pageSize="10"  nowrap="true" checkbox="false" pagination="true"  fitColumns="false" title="smsQuery" actionUrl="smsController.do?datagrid" idField="id" fit="true" autoLoadData="false">
            <t:dgCol align="center" title="id"  field="id" frozenColumn="true" hidden="true" width="100"></t:dgCol>
            <t:dgCol align="center" title="Account Number" frozenColumn="true" field="accNo" width="120"></t:dgCol>
            <t:dgCol align="center" title="Contact Number" field="cellNumber" frozenColumn="true" width="120"></t:dgCol>
            <t:dgCol align="center" title="Send Times"  field="sendTimes"  width="180"></t:dgCol>
            <t:dgCol align="center" title="Generate Time"  field="genTime"  autocomplete="true" width="180" formatter="yyyy-MM-dd hh:mm:ss"></t:dgCol>
            <t:dgCol align="center" title="First Send Time" field="firstSendTime" formatter="yyyy-MM-dd hh:mm:ss" queryMode="group"  width="180"></t:dgCol>
            <t:dgCol align="center" title="Last Send Time" field="lastSendTime"    formatter="yyyy-MM-dd hh:mm:ss" width="180"></t:dgCol>
            <t:dgCol align="center" title="Send Status"  field="flag" width="100" replace="Succeed_1,Failed_0"></t:dgCol>
            <t:dgCol align="left" title="Message Text"   field="smsTxt"  width="1200"></t:dgCol>
        </t:datagrid>
        <div id="smsQuerytb" style="padding: 3px; height: 27px;border-bottom:1px solid #D7D7D7">
            <div style="float: left;" class="searchColums" >
                Account Number&nbsp;&nbsp;&nbsp;&nbsp;<input class="inuptxt ac_input" type="text" name="accNo" id="accNo" maxlength="11">
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Contact Number&nbsp;&nbsp;&nbsp;&nbsp;<input class="inuptxt ac_input"  type="text" name="cellNumber" id="cellNumber" maxlength="8">
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Send Time&nbsp;&nbsp;&nbsp;<input class="Wdate" type="text" id="firstSendTime" name="firstSendTime" onclick="WdatePicker()">
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;To&nbsp;&nbsp;&nbsp;<input class="Wdate" type="text" id="lastSendTime" name="lastSendTime" onclick="WdatePicker()">
            </div>
            <div align="right">
                <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="searchSms();">Search</a>
                <a href="#" class="easyui-linkbutton" iconCls="icon-reload" onclick="searchReset('smsQuery')">clear</a>
            </div>
        </div>
    </div>
    </div>
</div>
<script type="text/javascript">
    function searchSms(){
        var accNo =  $("#accNo").val();
        var accountRegExp = /^[pP0-9-]{10,11}$/;
        var accountReg = new RegExp(accountRegExp);
        if(accNo!=""&&!accountReg.test(accNo)){
            alertTip("[Account Number] <br> Please enter 10 to 11 valid characters.");
            return false;
        }
        var cellNumber =  $("#cellNumber").val();
        var cellRegExp = /^[0-9]{8}$/;
        var cellReg = new RegExp(cellRegExp);
        if(cellNumber!=""&&!cellReg.test(cellNumber)){
            alertTip("[Contact Number] <br> Please enter 8 valid characters.");
            return false;
        }
        var firstSendTime =  $("#firstSendTime").val();
        var lastSendTime =  $("#lastSendTime").val();
        if((accNo==null||accNo=="")&&(cellNumber==null||cellNumber=="")&&(firstSendTime==null||firstSendTime=="")&&(lastSendTime==null||lastSendTime=="")){
            alertTip("No records found.");
            return false;
        }
        smsQuerysearch();
    }
    function  editor(index){
        alert("2");
        $('#smsQuery').datagrid('selectRow',index);
        var row = $('#smsQuery').datagrid('getSelected');
        if (row){
            var smsTxt = row.smsTxt;
            alert(smsTxt);

        }

    }
</script>