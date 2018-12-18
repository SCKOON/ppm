<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true" id="lywidth_demo">
  <div region="center" style="padding:0px;border:0px">
  <t:datagrid name="creditUpdateRecMainList" checkbox="false" fitColumns="true" title="Credit Update Record " actionUrl="creditUpdateRecController.do?datagrid" 
      		idField="id" fit="true" collapsible="true" queryMode="group"  filter="true"  autoLoadData="false">
    <t:dgCol title="id"  field="id" hidden="true" queryMode="single" width="120"></t:dgCol>
    <t:dgCol align="center" title="Account Number" field="accNo" width="120"></t:dgCol>
    <%--<t:dgCol align="center" title="Meter Number" field="meterId" width="120"></t:dgCol>--%>
    <t:dgCol align="right" title="Credit Balance" field="curBal"  width="100"></t:dgCol>
    <t:dgCol align="center" title="Operation type" field="updateFlag" dictionary="BALANCE_UPDATE_FLAG,typecode,showname"  width="100"></t:dgCol>
    <t:dgCol align="center" title="Update Time" field="updateTime" queryMode="group" formatter="yyyy-MM-dd hh:mm:ss"  width="150"></t:dgCol>
    <t:dgCol align="center" title="Control No." field="ctlNo" width="150"></t:dgCol>
    <t:dgCol align="center" title="SMS No." field="smsNo" width="150" ></t:dgCol>
    <t:dgCol align="right" title="Last Balance" field="lastBal"  width="100"></t:dgCol>
   </t:datagrid>

      <div id=creditUpdateRecMainListtb style="padding: 0px; height: 65px;">
          <div style="float: left;" class="searchColums">
              <table id="searchtab">
                  <tr>
                      <td>
                          &nbsp;&nbsp;Account Number&nbsp;&nbsp;<input class="inuptxt ac_input" type="text" id="accNo" name="accNo"  style="width: 135px">
                      </td>
<%--                      <td>
                          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Meter Number&nbsp;&nbsp;<input class="inuptxt ac_input" type="text" id="meterId" name="meterId"  style="width: 135px">
                      </td>--%>
                      <td align="right">
                          &nbsp;&nbsp;&nbsp;Operation type&nbsp;&nbsp;<select id="updateFlag" name="updateFlag"  style="width: 135px">
                          <option selected value="">Please Select</option>
                      </select>
                      </td>
                  </tr>
                  <tr>
                      <td>
                          Update Time From&nbsp;&nbsp;<input class="Wdate" type="text" id="updateTime_begin" name="updateTime_begin" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'updateTime_end\')||\'%y-%M-%d\'}'})"  style="width: 135px">
                      </td>
                      <td align="right">
                          &nbsp;&nbsp;To&nbsp;&nbsp;<input class="Wdate" type="text" id="updateTime_end" name="updateTime_end" onclick="WdatePicker({minDate:'#F{$dp.$D(\'updateTime_begin\')}',maxDate:'%y-%M-%d'})"  style="width: 135px">
                      </td>
                  </tr>
              </table>
          </div>
          <div align="right" >
              <%--<a href="#" class="easyui-linkbutton" iconCls="icon-search"	onclick="creditUpdateRecMainListsearch();">Search</a>--%>
              <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="Search();">Search</a>
              <a href="#" class="easyui-linkbutton" iconCls="icon-reload" onclick="searchReset('creditUpdateRecMainList')">Clear</a>
          </div>
      </div>
  </div>
 </div> 
 <script type="text/javascript">

	$(function(){
        $("input[name='accNo']").attr("maxlength", "11");
        $("input[name='meterId']").attr("maxlength", "9");
		 $("#creditUpdateRecMainList").datagrid({
			 onClickRow: function (index, row) {
				 getSmsList(row.smsNo);
				 getRemoteList(row.ctlNo);
				}
			});			 
	})

    function getSmsList(smsNo){
		parent.getSmsList(smsNo);
	}


	function getRemoteList(txnId){
		parent.getRemoteList(txnId);
	}

 </script>


<script>

    $(function () {
        var updateFlagList = '${updateFlagList}';
        var updateFlagObj = $.parseJSON(updateFlagList);
        if (updateFlagObj) {
            for (var i = 0; i < updateFlagObj.length; i++) {
                $("#updateFlag").append("<option value='" + updateFlagObj[i].code + "'>" + updateFlagObj[i].name + "</option>");
            }
        }
    });

    function Search() {

        var accNo = $("#accNo").val();
        var accountRegExp = /^[pP0-9-]{10,11}$/;
        var accountReg = new RegExp(accountRegExp);
        if (accNo != "" && !accountReg.test(accNo)) {
            alertTip("[Account Number] <br> Please enter 10 to 11 valid characters.");
            return false;
        }

/*        var meterId = $("#meterId").val();
        var meterRegExp = /^[a-zA-Z0-9]{9}$/;
        var meterReg = new RegExp(meterRegExp);
        if (meterId != "" && !meterReg.test(meterId)) {
            alertTip("[Meter Number] <br> Please enter 9 valid characters.");
            return false;
        }*/

        var updateFlag = $("#updateFlag").val();
        var updateTime_begin = $("#updateTime_begin").val();
        var updateTime_end = $("#updateTime_end").val();

        if(accNo==''&&updateFlag==''&&updateTime_begin==''&&updateTime_end==''){
            alertTip("No record found.");
            return false;
        }

        creditUpdateRecMainListsearch();
    }

</script>