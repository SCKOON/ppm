<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/context/mytags.jsp" %>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
    <div region="center" style="padding:0px;border:0px">
        <div id="terminaltb" style="padding:3px; height: auto" class="datagrid-toolbar">
            <div name="searchColums" id="searchColums"><input id="isShowSearchId" type="hidden" value="false"><input
                    id="_sqlbuilder" name="sqlbuilder" type="hidden"><input id="_complexSqlbuilder"
                                                                            name="complexSqlbuilder" type="hidden">
                <form onkeydown="if(event.keyCode==13){terminalsearch();return false;}" id="terminalForm"><span
                        style="max-width: 83%;display: inline-block;display:-moz-inline-box;"><span
                        style="display:-moz-inline-box;display:inline-block;margin-bottom:2px;text-align:justify;"><span
                        style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 120px;text-align:right;text-overflow:ellipsis;-o-text-overflow:ellipsis; overflow: hidden;white-space:nowrap; "
                        title="Terminal Name &nbsp&nbsp">Center Name&nbsp; </span><input onkeypress="EnterPress(event)"
                                                                                         onkeydown="EnterPress()" type="text"
                                                                                         name="name" style="width: 120px"
                                                                                         class="inuptxt"></span><span
                        style="display:-moz-inline-box;display:inline-block;margin-bottom:2px;text-align:justify;"><span
                        style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 120px;text-align:right;text-overflow:ellipsis;-o-text-overflow:ellipsis; overflow: hidden;white-space:nowrap; "
                        title="Terminal Code &nbsp&nbsp">Center Code &nbsp; </span><input onkeypress="EnterPress(event)"
                                                                                          onkeydown="EnterPress()" type="text"
                                                                                          name="code" style="width: 120px"
                                                                                          class="inuptxt"></span><span
                        style="display:-moz-inline-box;display:inline-block;margin-bottom:2px;text-align:justify;"><span
                        style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 120px;text-align:right;text-overflow:ellipsis;-o-text-overflow:ellipsis; overflow: hidden;white-space:nowrap; "
                        title="Channel Name &nbsp&nbsp">Channel Name &nbsp;</span><select id="channelId" name="channelEntity.id" style="width: 120px"><option value="">--Please Select--</option></select></span><span
                        style="display:-moz-inline-box;display:inline-block;margin-bottom:2px;text-align:justify;"><span
                        style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 120px;text-align:right;text-overflow:ellipsis;-o-text-overflow:ellipsis; overflow: hidden;white-space:nowrap; "
                        title="Status">Status&nbsp;</span><select name="status" width="120" style="width: 120px"> <option
                        value="">--Please Select--</option><option value="01">CREATED</option><option
                        value="02">ACTIVE</option><option value="03">SUSPEND</option><option value="04">CLOSED</option></select></span></span><span><span
                        style="float:right;"><a href="#" class="easyui-linkbutton l-btn" iconcls="icon-search"
                                                onclick="terminalsearch()">Search</a><a href="#"
                                                                                        class="easyui-linkbutton l-btn"
                                                                                        iconcls="icon-reload"
                                                                                        onclick="searchReset('terminal')"
                                                                                        id="">Clear</a></span></span>
                </form>
            </div>
            <div style="border-bottom-width:0;" class="datagrid-toolbar"><span style="float:left;"><a href="#"
                                                                                                      class="easyui-linkbutton l-btn l-btn-plain"
                                                                                                      plain="true"
                                                                                                      icon="icon-add"
                                                                                                      onclick="add('Add','terminalController.do?goAdd','terminal',700,380)"
            >Add</a><a
                    href="#" class="easyui-linkbutton l-btn l-btn-plain" plain="true" icon="icon-edit"
                    onclick="update('Edit','terminalController.do?goUpdate','terminal',700,380)" >Edit</a>
                <%--<a href="#" class="easyui-linkbutton l-btn l-btn-plain" plain="true" icon="icon-remove"--%>
                    <%--onclick="deleteALLSelect('Batch Del','terminalController.do?doBatchDel','terminal',null,null)" >Batch Del</a>--%>
                <a href="#" class="easyui-linkbutton l-btn l-btn-plain" plain="true" icon="icon-search"
                   onclick="detail('View','terminalController.do?goUpdate','terminal',700,380)" >View</a></span>
                <div style="clear:both"></div>
            </div>
        </div>
        <t:datagrid name="terminal" checkbox="true" sortName="name" pagination="true" fitColumns="false"
                    title="Service Center Management" singleSelect="true"
                    actionUrl="terminalController.do?datagrid" idField="id" fit="true" queryMode="group" filter="true">
            <t:dgCol title="id" field="id" hidden="true" width="120" align="center"></t:dgCol>
            <t:dgCol title="Service Center" field="name" width="150" align="center"></t:dgCol>
            <t:dgCol title="Center Code" field="code" width="150" align="center"></t:dgCol>
            <t:dgCol title="Channel Name" field="channelName" width="150" align="center"></t:dgCol>
            <t:dgCol title="Index" field="index" width="150" align="center"></t:dgCol>
            <t:dgCol title="Status" field="status" replace="CREATED_01,ACTIVE_02,SUSPEND_03,CLOSED_04" query="true"
                     queryMode="single" width="150" align="center"></t:dgCol>
            <t:dgCol title="Working Time" field="offTime" width="150" align="center"></t:dgCol>
            <t:dgCol title="Operation" field="opt" width="100" align="center"></t:dgCol>
            <t:dgFunOpt funname="deleteDialog(id)" title="Delete" urlclass="ace_button" urlStyle="background-color:#ec4758;"
                        urlfont="fa-trash-o"></t:dgFunOpt>
        </t:datagrid>
    </div>
</div>
<script type="text/javascript" src="<%=basePath%>/js/terminal/terminalView.js"></script>
<script type="text/javascript">
    var channelList = '${terminalList}';
    $(function(){
        if (channelList != null && channelList != undefined) {
            var obj = $.parseJSON(channelList);
            for (var i = 0; i < obj.length; i++) {
                $("#channelId").append("<option value='" + obj[i].id + "'>" + obj[i].name + "</option>");
            }
        }
    })
</script>