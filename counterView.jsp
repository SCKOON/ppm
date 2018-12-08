<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
    <div region="center" style="padding:0px;border:0px">
        <div id="countertb" style="padding:3px; height: auto" class="datagrid-toolbar">
            <div name="searchColums" id="searchColums">
                <form onkeydown="if(event.keyCode==13){terminalsearch();return false;}" id="counterForm"><span
                        style="max-width: 83%;display: inline-block;display:-moz-inline-box;"><span
                        style="display:-moz-inline-box;display:inline-block;margin-bottom:2px;text-align:justify;"><span
                        style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 120px;text-align:right;text-overflow:ellipsis;-o-text-overflow:ellipsis; overflow: hidden;white-space:nowrap; "
                        title="Terminal Name">Terminal Name&nbsp;</span><input onkeypress="EnterPress(event)"
                                                                               onkeydown="EnterPress()" type="text"
                                                                               name="name" style="width: 120px"
                                                                               class="inuptxt"></span><span
                        style="display:-moz-inline-box;display:inline-block;margin-bottom:2px;text-align:justify;"><span
                        style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 120px;text-align:right;text-overflow:ellipsis;-o-text-overflow:ellipsis; overflow: hidden;white-space:nowrap; "
                        title="Terminal Code">Terminal Code&nbsp;</span><input onkeypress="EnterPress(event)"
                                                                               onkeydown="EnterPress()" type="text"
                                                                               name="code" style="width: 120px"
                                                                               class="inuptxt"></span><span
                        style="display:-moz-inline-box;display:inline-block;margin-bottom:2px;text-align:justify;"><span
                        style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 120px;text-align:right;text-overflow:ellipsis;-o-text-overflow:ellipsis; overflow: hidden;white-space:nowrap; "
                        title="Center Name">Center Name&nbsp;</span><select id="tmlCode" name="tmlCode" style="width: 120px"><option value="">--Please Select--</option></select></span><span
                        style="display:-moz-inline-box;display:inline-block;margin-bottom:2px;text-align:justify;"><span
                        style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 120px;text-align:right;text-overflow:ellipsis;-o-text-overflow:ellipsis; overflow: hidden;white-space:nowrap; "
                        title="Status">Status&nbsp;</span><select name="status" width="120" style="width: 120px"> <option
                        value="">--Please Select--</option><option value="01">CREATED</option><option
                        value="02">ACTIVE</option><option value="03">SUSPEND</option><option value="04">CLOSED</option></select></span></span><span><span
                        style="float:right;"><a href="#" class="easyui-linkbutton l-btn" iconcls="icon-search"
                                                onclick="countersearch()">Search</a><a href="#"
                                                                                       class="easyui-linkbutton l-btn"
                                                                                       iconcls="icon-reload"
                                                                                       onclick="searchReset('counter')"
                                                                                       id="">Clear</a></span></span>
                </form>
            </div>
            <div style="border-bottom-width:0;" class="datagrid-toolbar"><span style="float:left;"><a href="#"
                                                                                                      class="easyui-linkbutton l-btn l-btn-plain"
                                                                                                      plain="true"
                                                                                                      icon="icon-add"
                                                                                                      onclick="add('Add','counterController.do?goAdd','counter',700,380)"
            >Add</a><a
                    href="#" class="easyui-linkbutton l-btn l-btn-plain" plain="true" icon="icon-edit"
                    onclick="update('Edit','counterController.do?goUpdate','counter',700,380)" >Edit</a>
                <%--<a href="#" class="easyui-linkbutton l-btn l-btn-plain" plain="true" icon="icon-remove"--%>
                    <%--onclick="deleteALLSelect('Batch Del','terminalController.do?doBatchDel','terminal',null,null)" >Batch Del</a>--%>
                <a href="#" class="easyui-linkbutton l-btn l-btn-plain" plain="true" icon="icon-search"
                   onclick="detail('View','counterController.do?goUpdate','counter',700,380)" >View</a></span>
                <div style="clear:both"></div>
            </div>
        </div>
        <t:datagrid name="counter" checkbox="true" sortName="cIndex" pagination="true" fitColumns="false" title="Terminal Management"
                    actionUrl="counterController.do?datagrid" singleSelect="true" idField="id" fit="true" queryMode="group" filter="true">
            <t:dgCol title="id"  field="id" hidden="true" width="120" align="center"></t:dgCol>
            <t:dgCol title="Terminal Name"  field="name"  width="160" align="center"></t:dgCol>
            <t:dgCol title="Terminal Code" field="code" width="120" align="center"></t:dgCol>
            <t:dgCol title="Service Center" field="terminalName" width="160" align="center"></t:dgCol>
            <t:dgCol title="Status" field="status" width="120" replace="CREATED_01,ACTIVE_02,SUSPEND_03,CLOSED_04" align="center"></t:dgCol>
            <t:dgCol title="Index"  field="cIndex"  width="150" align="center"></t:dgCol>
            <t:dgCol title="IP"  field="ip"  width="150"></t:dgCol>
            <t:dgCol title="HostName"  field="hostName"  width="150" hidden="true"></t:dgCol>
            <t:dgCol title="macAddr"   field="macAddr"  width="170" hidden="true"></t:dgCol>
            <t:dgCol title="Operation" field="opt" width="90"></t:dgCol>
            <%--<t:dgDelOpt title="delete" url="counterController.do?doDel&id={id}" urlclass="ace_button"  urlfont="fa-trash-o"/>--%>
            <t:dgFunOpt funname="deleteDialog(id)" title="Delete" urlclass="ace_button" urlStyle="background-color:#ec4758;" urlfont="fa-trash-o"></t:dgFunOpt>
            <t:dgToolBar title="Add" icon="icon-add" url="counterController.do?goAdd" funname="add" width="700" height="350"></t:dgToolBar>
            <t:dgToolBar title="Edit" icon="icon-edit" url="counterController.do?goUpdate" funname="update" width="700" height="350"></t:dgToolBar>
            <%--<t:dgToolBar title="batch del"  icon="icon-remove" url="counterController.do?doBatchDel" funname="deleteALLSelect"></t:dgToolBar>--%>
            <t:dgToolBar title="View" icon="icon-search" url="counterController.do?goUpdate" funname="detail" width="700" height="350"></t:dgToolBar>
        </t:datagrid>
    </div>
</div>
<script type="text/javascript">
    var terminalList = '${terminalList}';
    $(function(){
        if (terminalList != null && terminalList != undefined) {
            var obj = $.parseJSON(terminalList);
            for (var i = 0; i < obj.length; i++) {
                $("#tmlCode").append("<option value='" + obj[i].code + "'>" + obj[i].name + "</option>");
            }
        }
    })
</script>
<script type="text/javascript" src="<%=basePath%>/js/counter/counter.js"></script>