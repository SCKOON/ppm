<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
    <div region="center" style="padding:0px;border:0px">
        <t:datagrid name="customerInfoList" pageSize="10" checkbox="true" singleSelect="true" pagination="true" fitColumns="true"
                    title="Account Conversion for PAYU" actionUrl="customerInfoController.do?payuActivationDatagrid" idField="accNo" fit="true" autoLoadData="false">
            <t:dgCol align="center" title="Account Number" field="accNo"   width="120"></t:dgCol>
            <t:dgCol align="center" title="Scheduled Activation Date" field="sheduledActivationDate" formatter="yyyy-MM-dd"  width="120"></t:dgCol>
            <t:dgCol align="center" title="Account Status" field="accountStatus" dictionary="ACCOUNT_STATUS,typecode,showname" width="120"></t:dgCol>
            <t:dgCol align="center" title="Meter Serial Number" field="meterId" width="120"></t:dgCol>
            <t:dgCol align="center" title="Address" field="address" width="120"></t:dgCol>
            <t:dgCol align="center" title="Operation" field="opt" width="120"></t:dgCol>
            <t:dgFunOpt title="Edit" exp="accountStatus#eq#01,02,03" funname="edit(accNo)" urlclass="ace_button" urlfont="fa-edit"/>
        </t:datagrid>
        <div id="customerInfoListtb" style="padding: 3px; height: 28px;">
            <div style="float: left;" class="searchColums" >
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                Account Number
                &nbsp;
                <input class="inuptxt ac_input" type="text" id="accNo" name="accNo">
                &nbsp;&nbsp;&nbsp;&nbsp;
                Account Status
                &nbsp;
                <select id="accountStatus" name="accountStatus" style="width: 145px">
                    <option selected value="">Please Select</option>
                </select>
            </div>
            <div align="right">
                <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="doSearch();">Search</a>
                <a href="#" class="easyui-linkbutton" iconCls="icon-reload" onclick="searchReset()">Clear</a>
            </div>
        </div>
    </div>
</div>
<script>
    //重置
    function searchReset() {
        $(":input").each(
            function () {
                $(this).val("");
            }
        );
        $("#customerInfoList").datagrid('loadData',{total:0,rows:[]});
    }

    $(function () {
        var statusList = '${statusList}';
        var accountStatusObj = $.parseJSON(statusList);
        if (accountStatusObj) {
            for (var i = 0; i < accountStatusObj.length; i++) {
                $("#accountStatus").append("<option value='" + accountStatusObj[i].code + "'>" + accountStatusObj[i].name + "</option>");
            }
        }

    });

    function edit(accNo){
        var title = 'Customer Account Information';
        var url = 'customerInfoController.do?payuGoUpdate';
        url += '&accNo=' + accNo;
        createwindow(title,url,600,350);
    }

    //重写查询方法
    function doSearch() {
        var accNo = $("#accNo").val();
        var accountStatus = $("#accountStatus").val();

        //accountNumber输入框长度校验
        if(accNo.length == 0){
            alertTip("Please enter account number.");
            return;
        }
        if(accNo.length > 11){
            alertTip("Account number more than 11 digits. Please confirm.");
            return;
        }
        //accountNumber格式校验  0-9,-,
        var accNoRegExp = /^[A-Za-z0-9-]+$/;
        var reg = new RegExp(accNoRegExp);
        if (!reg.test(accNo)) {
            alertTip("Invalid account number. Please enter again.");
            return false;
        }

        $('#customerInfoList').datagrid({
            url: 'customerInfoController.do?activationDatagrid&field=accNo,sheduledActivationDate,accountStatus,meterId,address,',
            pageNumber: 1,
            queryParams: {
                accNo: accNo,
                accountStatus: accountStatus
            },
            onLoadSuccess: function (data) {
                if (data.total == 0) {
                    $(this).datagrid('appendRow', {accNo: '<div style="text-align:center;color:red">No results found.</div>'}).datagrid('mergeCells', {
                        index: 0,
                        field: 'accNo',
                        colspan: 6
                    })
                }
            }
        });
    }

</script>