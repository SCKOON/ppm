<%@ page language="java" import="java.net.*" import="java.io.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ppms.utils.UdpGetClientMacAddr" %>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<style type="text/css">
    /*select {*/
        /*direction: rtl;*/
    /*}*/
    select option {
        direction: ltr;
    }
</style>

<%--根据客户端IP获取客户端MAC地址--%>
<%--<%--%>
<%--String macAddress = "";--%>
<%--String sip = request.getHeader("x-forwarded-for");--%>
<%--if(sip == null || sip.length() == 0 || "unknown".equalsIgnoreCase(sip)) {--%>
<%--sip = request.getHeader("Proxy-Client-IP");--%>
<%--}--%>
<%--if(sip == null || sip.length() == 0 || "unknown".equalsIgnoreCase(sip)) {--%>
<%--sip = request.getHeader("WL-Proxy-Client-IP");--%>
<%--}--%>
<%--if(sip == null || sip.length() == 0 || "unknown".equalsIgnoreCase(sip)) {--%>
<%--sip = request.getRemoteAddr();--%>
<%--}--%>
<%--UdpGetClientMacAddr umac = new UdpGetClientMacAddr(sip);--%>
<%--macAddress = umac.GetRemoteMacAddr();--%>
<%--%>--%>

<div class="easyui-layout" fit="true">
    <div region="center" style="padding:0px;border:0px" title="Customer Information Query from EBS (Account Opening)">
        <div align="left" class="searchColums" style="padding: 3px">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            Customer Account Number &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input class="inuptxt ac_input" id="inputxt" type="text" name="accountNumber" style="width: 150px;" onkeydown="if(event.keyCode==13){searchData();return false;}">
            <span style="color: red">*</span>
            <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="searchData();">Search EBS</a>
            <a href="#" class="easyui-linkbutton" iconCls="icon-reload" onclick="searchReset()">Clear</a>
        </div>
        <t:formvalid formid="ff" dialog="true" tiptype="4" layout="table" action="customerInfoController.do?saveCustomerInformation">
            <table cellpadding="0" cellspacing="1" class="formtable">
                <tbody>
                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Account Number
                        </label>
                    </td>
                    <td class="value">
                        <input id="accNo" name="accNo" type="text" style="width: 150px;" class="accNo" value="" readonly="readonly">
                        <span style="color: red">*</span>
                    </td>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            NRIC
                        </label>
                    </td>
                    <td class="value">
                        <input id="nric" name="nric" type="text" style="width: 150px;" class="nric" value="" readonly="readonly">
                        <span style="color: red">*</span>
                    </td>
                </tr>

                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Customer Name
                        </label>
                    </td>
                    <td class="value">
                        <input id="name" name="name" type="text" style="width: 150px;" class="name" value="" readonly="readonly">
                        <span class="Validform_checktip"></span>
                        <label class="Validform_label" style="display: none;">Customer Name</label>
                    </td>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Salutation
                        </label>
                    </td>
                    <td class="value">
                        <input id="salutation" name="salutation" type="text" style="width: 150px;" class="salutation" value="" readonly="readonly">
                    </td>
                </tr>
                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Street
                        </label>
                    </td>
                    <td class="value">
                        <input id="streetName" name="streetName" type="text" style="width: 150px;" class="streetName" value="" readonly="readonly">
                    </td>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Block Number
                        </label>
                    </td>
                    <td class="value">
                        <input id="blockNumber" name="blockNumber" type="text" style="width: 150px;" class="blockNumber" value="" readonly="readonly">
                    </td>
                </tr>
                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Unit Number
                        </label>
                    </td>
                    <td class="value">
                        <input id="unitNumber" name="unitNumber" type="text" style="width: 150px;" class="unitNumber" value="" readonly="readonly">
                    </td>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Postalcode
                        </label>
                    </td>
                    <td class="value">
                        <input id="postalCode" name="postalCode" type="text" style="width: 150px;" class="postalCode" value="" readonly="readonly">
                    </td>
                </tr>
                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Phone
                        </label>
                    </td>
                    <td class="value">
                        <input id="telephoneNumber" name="telephoneNumber" type="text" style="width: 150px;" class="telephoneNumber" value="" readonly="readonly">
                    </td>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Mobile
                        </label>
                    </td>
                    <td class="value">
                        <input id="mobileNumber" name="mobileNumber" type="text" style="width: 150px;" class="mobileNumber" value="" readonly="readonly">
                    </td>
                </tr>

                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Premise Type
                        </label>
                    </td>
                    <td class="value">
                        <input id="premiseType" name="premiseType" type="text" style="width: 150px;" class="premiseType" value="" readonly="readonly">
                        <span style="color: red">*</span>
                    </td>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Email Address
                        </label>
                    </td>
                    <td class="value">
                        <input id="emailAddr" name="emailAddr" type="text" style="width: 150px;" class="emailAddr" value="" readonly="readonly">
                    </td>
                </tr>
                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Fax
                        </label>
                    </td>
                    <td class="value">
                        <input id="faxNumber" name="faxNumber" type="text" style="width: 150px;" class="faxNumber" value="" readonly="readonly">
                    </td>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Arrear Percentage
                        </label>
                    </td>
                    <td class="value">
                        <input id="arrearPct" name="arrearPct" type="text" style="width: 150px;text-align:right" class="arrearPct" value="" readonly="readonly">
                        <span style="color: red">*</span>%
                    </td>
                </tr>
                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Preset Credit
                        </label>
                    </td>
                    <td class="value">
                        <input id="presetCredit" name="presetCredit" type="text" style="width: 150px;text-align:right" class="presetCredit" value="" readonly="readonly">
                        <span style="color: red">*</span>SGD
                    </td>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Emergency Credit Limit
                        </label>
                    </td>
                    <td class="value">
                        <input id="emergencyCredit" name="emergencyCredit" type="text" style="width: 150px;text-align:right" class="emergencyCredit" value="" readonly="readonly">
                        <span style="color: red">*</span>SGD
                    </td>
                </tr>
                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Low Credit Alarm
                        </label>
                    </td>
                    <td class="value">
                        <input id="lowCreditAlarm" name="lowCreditAlarm" type="text" style="width: 150px;text-align:right" class="lowCreditAlarm" value="" readonly="readonly">
                        <span style="color: red">*</span>SGD
                    </td>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            GST
                        </label>
                    </td>
                    <td class="value">
                        <%--<select id="gstCode" name="gstCode" style="width: 150px">--%>
                        <%--</select>--%>
                        <input id="gstRate" name="gstRate" type="text" style="width: 150px;text-align:right" class="gstRate" value="" readonly="readonly">
                        <input id="gstCode" name="gstCode" type="hidden" style="width: 150px;text-align:right" class="gstCode" value="" readonly="readonly">
                        <span style="color: red">*</span>%
                    </td>
                </tr>
                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Tariff Type
                        </label>
                    </td>
                    <td class="value">
                        <select id="tariffCode" name="tariffCode" style="width: 150px" onchange="changeTariff()">
                            <%--<option selected value="">Please Select</option>--%>
                        </select>
                        <span style="color: red">*</span>
                    </td>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Tariffs
                        </label>
                    </td>
                    <td class="value">
                        <input id="tariffs" name="tariffs" type="text" style="width: 150px;text-align:right" class="tariffs" value="" readonly="readonly">&nbsp;
                        <span style="color: red">*</span>SGD/kwh
                    </td>
                </tr>
                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Premise Address
                        </label>
                    </td>
                    <td class="value" colspan="3">
                        <input id="address" name="address" type="text" style="width: 76.8%" class="address" value="" readonly="readonly">
                    </td>
                </tr>
                <tr>
                    <td align="right" style="width: 200px;">
                        <label class="Validform_label">
                            Mail Address
                        </label>
                    </td>
                    <td class="value" colspan="3">
                        <input id="mailAddr" name="mailAddr" type="text" style="width: 76.8%" class="mailAddr" value="" readonly="readonly">
                    </td>
                </tr>
                </tbody>
            </table>
            <div id="jeecgMysearchListtb2" style="padding: 3px; height: 25px">
                <div style="float: right;">
                    <a href="#" class="easyui-linkbutton l-btn l-btn-plain" plain="true" icon="icon-save" id="btn_sub" onclick="saveData()">
                        Confirm
                    </a>
                    <a href="#" class="easyui-linkbutton l-btn l-btn-plain" plain="true" icon="icon-print" onclick="printCard()">
                        Print Card
                    </a>
                </div>
            </div>
        </t:formvalid>
    </div>

</div>
<script type="text/javascript">
    //查询数据
    function searchData(){
        //accountNumber输入框长度校验
        var accountNumber = $("#inputxt").val();
        if(accountNumber.length < 10){
            alertTip("Account number less than 10 digits. Please confirm.");
            return;
        }
        if(accountNumber.length > 10){
            alertTip("Account number more than 10 digits. Please confirm.");
            return;
        }
        //accountNumber格式校验  0-9,-,
        var accNoRegExp = /^[A-Za-z0-9-]+$/;
        var reg = new RegExp(accNoRegExp);
        if (!reg.test(accountNumber)) {
            alertTip("Invalid account number. Please enter again.");
            return false;
        }

        $.ajax({
            type: "post",
            url: "customerInfoController.do?search",
            data: {accountNumber:accountNumber},
            dataType: "json",
            success: function(result){
                var returnCode = result.succeed;
                if(returnCode){
                    $("#accNo").val(result.accNo);
                    $("#name").val(result.name);
                    $("#nric").val(result.nric);
                    $("#salutation").val(result.salutation);
                    $("#address").val(result.address);
                    $("#premiseType").val(result.premiseType);
                    $("#blockNumber").val(result.blockNumber);
                    $("#streetName").val(result.streetName);
                    $("#unitNumber").val(result.unitNumber);
                    $("#postalCode").val(result.postalCode);
                    $("#mailAddr").val(result.mailAddr);
                    $("#emailAddr").val(result.emailAddr);
                    $("#telephoneNumber").val(result.telephoneNumber);
                    $("#faxNumber").val(result.faxNumber);
                    $("#mobileNumber").val(result.mobileNumber);
                    loadDefaultData();
                }else{
                    alertTip(result.errorMsg);
                }
            }
        });
    }

    //重置按钮清空搜索框&表单
    function searchReset(){
        $('#inputxt').val("");
        clearff();
    }

    //清空表单内容
    function clearff(){
        var mac = $("#mac").val();
        $("#ff").form('clear');
        $("#mac").val(mac);//防止mac被清空,无法提交到后台
    }

    //选择tariffCode动态加载tariffrate
    function changeTariff(){
        var tariffCode = $("#tariffCode").val();
        $.ajax({
            type: "post",
            url: "customerInfoController.do?getTariffRateByCode",
            data: {tariffCode:tariffCode},
            dataType: "json",
            success: function(result){
                var returnCode = result.succeed;
                if(returnCode){
                    $("#tariffs").val(result.tariffRate);
                }else{
                    alertTip(result.errormsg);
                }
            }
        });
    }

    /*加载缺省数据*/
    function loadDefaultData() {
        $("#gstCode").empty();
        $("#tariffCode").empty();
//        $("#gstCode").append("<option value=''>" + "Please Select" + "</option>");
        $("#tariffCode").append("<option value=''>" + "Please Select" + "</option>");

        var presetCredit = '${presetCredit}';
        var emergencyCredit = '${emergencyCredit}';
        var lowCreditAlarm = '${lowCreditAlarm}';
        var arrearPct = '${arrearPct}';
        $("#presetCredit").val(presetCredit);
        $("#emergencyCredit").val(emergencyCredit);
        $("#lowCreditAlarm").val(lowCreditAlarm);
        $("#arrearPct").val(arrearPct);

        var gstList = '${gstList}';
        var gstObj = $.parseJSON(gstList);
        if (gstObj) {
            for (var i = 0; i < gstObj.length; i++) {
//                $("#gstCode").append("<option value='" + gstObj[i].code + "'>" + gstObj[i].rate + "</option>");
                $("#gstRate").val(gstObj[0].rate);
                $("#gstCode").val(gstObj[0].code);

            }
        }

        var tarifflist = '${tarifflist}';
        var tariffObj = $.parseJSON(tarifflist);
        if (tariffObj) {
            for (var i = 0; i < tariffObj.length; i++) {
                $("#tariffCode").append("<option value='" + tariffObj[i].code + "'>" + tariffObj[i].name + "</option>");
            }
        }
    }

    //开户按钮-保存用户信息
    function saveData(){
        //校验 账号,电价类型,电价
        var accNo = $("#accNo").val();
        if(accNo=="" || accNo == null){
            alertTip("Cannot obtain account info. Please synchronize from EBS.");
            return;
        }
        var tariffCode = $("#tariffCode").val();
        if(tariffCode==""){
            alertTip("Please Select TariffCode.");
            return;
        }
        var tariffs = $("#tariffs").val();
        if(tariffs==""){
            alertTip("Tariffs can't be null.");
            return;
        }
        $("#ff").Validform({
            callback:function(data){
                alertTip(data.msg);
            }
        }).ajaxPost(false,true);
    }

    // 打印电卡
    function printCard(){
        var accNo = $("#accNo").val();
        var name = $("#name").val();
        var addurl="customerInfoController.do?printDialog&accNo="+accNo+"&name="+name;
        var title = 'Print Top-Up Card';
        var width = 670;
        var height = 450;

        if(accNo=="" || accNo == null){
            alertTip("Cannot obtain account info. Please synchronize from EBS.");
            return;
        }
        if(typeof(windowapi) == 'undefined'){
            $.dialog({
                content: 'url:'+addurl,
                zIndex: getzIndex(),
                lock : true,
                width:width,
                height: height,
                title:title,
                opacity : 0.3,
                okVal: ' Print ',
                /*ok: function () {
                    iframe = this.iframe.contentWindow;
                    iframe.printall();
                    return false;
                },*/
                ok: function () {
                    var content = "Sure to Print?";
                    layer.confirm(content, {
                        btn: ['Confirm', 'Cancel'],
                        shade: false
                    }, function () {
                        layer.closeAll();
                        $.post("customerInfoController.do?topUpCardFee", {
                                "accNo": accNo
                            },
                            function (data) {
                                if (data.success) {
                                    iframe = parent.window.frames[0].frameElement.contentWindow;
                                    iframe.printall();
                                    alertTip("Print card success.");
                                } else {
                                    alertTip(data.msg);
                                }
                            }, "json");
                    }, function () {
                        return;
                    });
                },
                cache:false,
                cancelVal: 'Cancel',
                cancel: true
            });
        }else{
            W.$.dialog({
                content: 'url:'+addurl,
                zIndex: getzIndex(),
                lock : true,
                width:width,
                height: height,
                parent:windowapi,
                title:title,
                opacity : 0.3,
                cache:false,
                cancelVal: 'Close',
                cancel: function(){
                    windowapi.zindex();
                }
            });

        }
    }
</script>