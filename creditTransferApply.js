$(document).ready(function () {
    $("#sourceCustomer").datagrid({
        onClickRow: function (index, row) {
            $("form:eq(0) input[name='accNo']").val(row.accNo);
            $("form:eq(0) input[name='nric']").val(row.nric);
        }
    });
    $("#targetCustomer").datagrid({
        onClickRow: function (index, row) {
            $("form:eq(1) input[name='accNo']").val(row.accNo);
            $("form:eq(1) input[name='nric']").val(row.nric);
        }
    });
});
$(function () {
        $("form :eq(0) input[name='accNo']").click(function () {
            $("form :eq(0) span").remove(".Validform_wrong");
        });
        $("form :eq(1) input[name='accNo']").click(function () {
            $("form :eq(1) span").remove(".Validform_wrong");
        })
    }
)

function searchReset(name) {
    $("#" + name + "tb").find(":input").val("");
    // var queryParams = $('#'+name).datagrid('options').queryParams;
    // $("#" + name + "tb").find('*').each(function () {
    //     queryParams[$(this).attr('name')] = $(this).val();
    // });
    $("#" + name).datagrid('loadData', {total: 0, rows: []});
}

function sourceCustomersearch() {
    try {
        if (!$("#sourceCustomerForm").Validform({tiptype: 3}).check()) {
            return false;
        }
    } catch (e) {
    }
    if (true) {
        var queryParams = {};
        var flag = true;
        $('#sourceCustomertb').find('*').each(function () {
            if ($(this).attr('name') == 'accNo') {
                var accNo = $(this).val();
                if (!accNo) {
                    var nextElement = $(this).next();
                    if (!$(this).next().hasClass("Validform_checktip Validform_wrong")) {
                        $(this).after("<span class='Validform_checktip'></span>");
                        $(".Validform_checktip:first").addClass("Validform_wrong").text("Account Number can't be empty");
                    }
                    flag = false;
                }
            }
            queryParams[$(this).attr('name')] = $(this).val();
        });
        if (!flag) {
            return false;
        }
        $('#sourceCustomer').datagrid({
            url: 'customerInfoController.do?getCustomerInfoToCreditTransfer&field=accNo,nric,name,meterId,balance,mobileNumber,openDate,type,address,streetName,',
            pageNumber: 1,
            queryParams: queryParams
        });
    }
}

function targetCustomersearch() {
    try {
        if (!$("#targetCustomer").Validform({tiptype: 3}).check()) {
            return false;
        }
    } catch (e) {
    }
    if (true) {
        var queryParams = {};
        var flag = true;
        $('#targetCustomertb').find('*').each(function () {
            if ($(this).attr('name') == 'accNo') {
                var accNo = $(this).val();
                if (!accNo) {
                    var nextElement = $(this).next();
                    if (!$(this).next().hasClass("Validform_checktip Validform_wrong")) {
                        $(this).after("<span class='Validform_checktip'></span>");
                        $(".Validform_checktip:last").addClass("Validform_wrong").text("Account Number can't be empty");
                    }
                    flag = false;
                }
            }
            queryParams[$(this).attr('name')] = $(this).val();
        });
        if (!flag) {
            return false;
        }
        $('#targetCustomer').datagrid({
            url: 'customerInfoController.do?getCustomerInfoToCreditTransfer&field=accNo,nric,name,meterId,balance,mobileNumber,openDate,type,address,streetName,',
            pageNumber: 1,
            queryParams: queryParams
        });
    }
}

function transferCreditApply() {
    var selectedrow1 = $("#sourceCustomer").datagrid('getSelected');
    var selectedrow2 = $("#targetCustomer").datagrid('getSelected');
    if (selectedrow1 == null || selectedrow2 == null) {
        alertTip("The Source/Target Account shouldn't be empty.!");
        return false;
    }
    if (selectedrow1.accNo == selectedrow2.accNo) {
        alertTip("The Source/Target Account shouldn't be the same one.!");
        return false;
    }
    var balanceFrom = selectedrow1['balance'];
    var balanceTo = selectedrow2['balance'];
    var amount = $("input[name='amount']").val();
    var regexe = '^\\d{1,4}(?:\\.\\d{1,2})?$';
    var reg = new RegExp(regexe);
    if (amount == '') {
        $("input[name='amount']").css({"border": "1px red solid"});
        return false;
    }

    if (!reg.test(amount)) {
        alertTip("The Credit Transfer Amount is limit from 0~9999.99 SGD.!");
        return false;
    }
    if (balanceFrom != undefined && parseFloat(amount) > parseFloat(balanceFrom)) {
        alertTip("No sufficient Credit Balance to do credit transfer.!");
        return false;
    }
    var remark = $("textarea").val();
    var accNoFrom = selectedrow1.accNo;
    var accNoTo = selectedrow2.accNo;

    var srcName = selectedrow1.name;
    var tgtName = selectedrow2.name;
    var srcAddress = selectedrow1.address;
    var tgtAddress = selectedrow2.address;

    //传送数据生成条件
    var condition = "&srcAccNo=" + accNoFrom + "&tgtAccNo=" + accNoTo + "&applyReason=" + remark
        + "&amount=" + amount + "&srcName=" + srcName + "&tgtName=" + tgtName + "&srcAddress=" + srcAddress + "&tgtAddress=" + tgtAddress;

    //生成弹出窗口的页面
    //需要考虑申请余额转移在很久之后，有可能中间重新算费了但还没点按钮提交申请的场景
    var title = 'detail';
    var addurl = 'creditTransferController.do?toDetailView' + condition;
    var width = 900;
    var height = 430;
    width = width ? width : 700;
    height = height ? height : 400;
    if (width == "100%" || height == "100%") {
        width = window.top.document.body.offsetWidth;
        height = window.top.document.body.offsetHeight - 100;
    }
    //--author：JueYue---------date：20140427---------for：弹出bug修改,设置了zindex()函数
    if (typeof(windowapi) == 'undefined') {
        $.dialog({
            content: 'url:' + addurl,
            lock: true,
            zIndex: getzIndex(),
            width: width,
            height: height,
            title: title,
            opacity: 0.3,
            cache: false,
            okVal: "Confirm",
            ok: function () {
                var content = "Confirm to Credit Transfer?";
                layer.confirm(content, {
                    btn: ['Confirm', 'Cancel'], //按钮
                    shade: false //不显示遮罩
                }, function () {
                    layer.close(1);
                    $.post("creditTransferController.do?transfer", {
                            "srcAccNo": accNoFrom,
                            "tgtAccNo": accNoTo,
                            "amount": amount,
                            "applyReason": remark
                        },
                        function (data) {
                            alertTip(data);
                            $(":input").each(
                                function () {
                                    $(this).val("");
                                }
                            );
                            $('#sourceCustomer').datagrid('loadData', {total: 0, rows: []});
                            $('#targetCustomer').datagrid('loadData', {total: 0, rows: []});
                        }, "json");
                }, function () {
                    return;
                });
            },
            cancelVal: 'close',
            cancel: true /*为true等价于function(){}*/
        });
    } else {

        /*W.*/
        $.dialog({//使用W，即为使用顶级页面作为openner，造成打开的次级窗口获取不到关联的主窗口
            content: 'url:' + addurl,
            lock: true,
            width: width,
            zIndex: getzIndex(),
            height: height,
            parent: windowapi,
            title: title,
            opacity: 0.3,
            cache: false,
            ok: function () {
                iframe = this.iframe.contentWindow;
                saveObj();
                return false;
            },
            cancelVal: 'close',
            cancel: true /*为true等价于function(){}*/
        });

    }

}