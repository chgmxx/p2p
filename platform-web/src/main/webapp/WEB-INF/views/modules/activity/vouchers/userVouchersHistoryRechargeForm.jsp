<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>抵用券管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		// --.
		$("#inputForm").validate({
				submitHandler : function(form) {
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer : "#messageBox",
				errorPlacement : function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox") || element.is(":radio") || element.parent().is( ".input-append")) {
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
		}); // --.

		// --.
		$("input[id='spanIdList1']").on('click',function(){
			if(this.checked) { // 通用复选框选中时触发.
				// console.log("通用复选框点击事件-被选中");
				$("input[id='spanIdList2']").removeAttr("checked");
				$("input[id='spanIdList3']").removeAttr("checked");
				$("input[id='spanIdList4']").removeAttr("checked");
				$("input[id='spanIdList5']").removeAttr("checked");
				$("input[id='spanIdList6']").removeAttr("checked");
				$("input[id='spanIdList7']").removeAttr("checked");
			}
		}); // --.

		// --.
		$("input[id='spanIdList2']").on('click',function(){
			if(this.checked) { // 通用复选框选中时触发.
				// console.log("通用复选框点击事件-被选中");
				$("input[id='spanIdList1']").removeAttr("checked");
				// 获取其它复选框的值.
				var spanIdList3 = $("input[id='spanIdList3']").is(':checked');
				var spanIdList4 = $("input[id='spanIdList4']").is(':checked');
				var spanIdList5 = $("input[id='spanIdList5']").is(':checked');
				var spanIdList6 = $("input[id='spanIdList6']").is(':checked');
				var spanIdList7 = $("input[id='spanIdList7']").is(':checked');
				// console.log(spanIdList3);
				if(spanIdList3 && spanIdList4 && spanIdList5 && spanIdList6 && spanIdList7){
					$("input[id='spanIdList1']").attr("checked" , true);
					$("input[id='spanIdList2']").attr("checked" , false);
					$("input[id='spanIdList3']").attr("checked" , false);
					$("input[id='spanIdList4']").attr("checked" , false);
					$("input[id='spanIdList5']").attr("checked" , false);
					$("input[id='spanIdList6']").attr("checked" , false);
					$("input[id='spanIdList7']").attr("checked" , false);
				}
			}
		}); // --.

		// --.
		$("input[id='spanIdList3']").on('click',function(){
			if(this.checked) { // 通用复选框选中时触发.
				// console.log("通用复选框点击事件-被选中");
				$("input[id='spanIdList1']").removeAttr("checked");
				// 获取其它复选框的值.
				var spanIdList2 = $("input[id='spanIdList2']").is(':checked');
				var spanIdList4 = $("input[id='spanIdList4']").is(':checked');
				var spanIdList5 = $("input[id='spanIdList5']").is(':checked');
				var spanIdList6 = $("input[id='spanIdList6']").is(':checked');
				var spanIdList7 = $("input[id='spanIdList7']").is(':checked');
				// console.log(spanIdList3);
				if(spanIdList2 && spanIdList4 && spanIdList5 && spanIdList6 && spanIdList7){
					$("input[id='spanIdList1']").attr("checked" , true);
					$("input[id='spanIdList2']").attr("checked" , false);
					$("input[id='spanIdList3']").attr("checked" , false);
					$("input[id='spanIdList4']").attr("checked" , false);
					$("input[id='spanIdList5']").attr("checked" , false);
					$("input[id='spanIdList6']").attr("checked" , false);
					$("input[id='spanIdList7']").attr("checked" , false);
				}
			}
		}); // --.

		// --.
		$("input[id='spanIdList4']").on('click',function(){
			if(this.checked) { // 通用复选框选中时触发.
				// console.log("通用复选框点击事件-被选中");
				$("input[id='spanIdList1']").removeAttr("checked");
				// 获取其它复选框的值.
				var spanIdList2 = $("input[id='spanIdList2']").is(':checked');
				var spanIdList3 = $("input[id='spanIdList3']").is(':checked');
				var spanIdList5 = $("input[id='spanIdList5']").is(':checked');
				var spanIdList6 = $("input[id='spanIdList6']").is(':checked');
				var spanIdList7 = $("input[id='spanIdList7']").is(':checked');
				// console.log(spanIdList3);
				if(spanIdList2 && spanIdList3 && spanIdList5 && spanIdList6 && spanIdList7){
					$("input[id='spanIdList1']").attr("checked" , true);
					$("input[id='spanIdList2']").attr("checked" , false);
					$("input[id='spanIdList3']").attr("checked" , false);
					$("input[id='spanIdList4']").attr("checked" , false);
					$("input[id='spanIdList5']").attr("checked" , false);
					$("input[id='spanIdList6']").attr("checked" , false);
					$("input[id='spanIdList7']").attr("checked" , false);
				}
			}
		}); // --.

		// --.
		$("input[id='spanIdList5']").on('click',function(){
			if(this.checked) { // 通用复选框选中时触发.
				// console.log("通用复选框点击事件-被选中");
				$("input[id='spanIdList1']").removeAttr("checked");
				// 获取其它复选框的值.
				var spanIdList2 = $("input[id='spanIdList2']").is(':checked');
				var spanIdList3 = $("input[id='spanIdList3']").is(':checked');
				var spanIdList4 = $("input[id='spanIdList4']").is(':checked');
				var spanIdList6 = $("input[id='spanIdList6']").is(':checked');
				var spanIdList7 = $("input[id='spanIdList7']").is(':checked');
				// console.log(spanIdList3);
				if(spanIdList2 && spanIdList3 && spanIdList4 && spanIdList6 && spanIdList7){
					$("input[id='spanIdList1']").attr("checked" , true);
					$("input[id='spanIdList2']").attr("checked" , false);
					$("input[id='spanIdList3']").attr("checked" , false);
					$("input[id='spanIdList4']").attr("checked" , false);
					$("input[id='spanIdList5']").attr("checked" , false);
					$("input[id='spanIdList6']").attr("checked" , false);
					$("input[id='spanIdList7']").attr("checked" , false);
				}
			}
		}); // --.

		// --.
		$("input[id='spanIdList6']").on('click',function(){
			if(this.checked) { // 通用复选框选中时触发.
				// console.log("通用复选框点击事件-被选中");
				$("input[id='spanIdList1']").removeAttr("checked");
				// 获取其它复选框的值.
				var spanIdList2 = $("input[id='spanIdList2']").is(':checked');
				var spanIdList3 = $("input[id='spanIdList3']").is(':checked');
				var spanIdList4 = $("input[id='spanIdList4']").is(':checked');
				var spanIdList5 = $("input[id='spanIdList5']").is(':checked');
				var spanIdList7 = $("input[id='spanIdList7']").is(':checked');
				// console.log(spanIdList3);
				if(spanIdList2 && spanIdList3 && spanIdList4 && spanIdList5 && spanIdList7){
					$("input[id='spanIdList1']").attr("checked" , true);
					$("input[id='spanIdList2']").attr("checked" , false);
					$("input[id='spanIdList3']").attr("checked" , false);
					$("input[id='spanIdList4']").attr("checked" , false);
					$("input[id='spanIdList5']").attr("checked" , false);
					$("input[id='spanIdList6']").attr("checked" , false);
					$("input[id='spanIdList7']").attr("checked" , false);
				}
			}
		}); // --.

		// --.
		$("input[id='spanIdList7']").on('click',function(){
			if(this.checked) { // 通用复选框选中时触发.
				// console.log("通用复选框点击事件-被选中");
				$("input[id='spanIdList1']").removeAttr("checked");
				// 获取其它复选框的值.
				var spanIdList2 = $("input[id='spanIdList2']").is(':checked');
				var spanIdList3 = $("input[id='spanIdList3']").is(':checked');
				var spanIdList4 = $("input[id='spanIdList4']").is(':checked');
				var spanIdList5 = $("input[id='spanIdList5']").is(':checked');
				var spanIdList6 = $("input[id='spanIdList6']").is(':checked');
				// console.log(spanIdList3);
				if(spanIdList2 && spanIdList3 && spanIdList4 && spanIdList5 && spanIdList6){
					$("input[id='spanIdList1']").attr("checked" , true);
					$("input[id='spanIdList2']").attr("checked" , false);
					$("input[id='spanIdList3']").attr("checked" , false);
					$("input[id='spanIdList4']").attr("checked" , false);
					$("input[id='spanIdList5']").attr("checked" , false);
					$("input[id='spanIdList6']").attr("checked" , false);
					$("input[id='spanIdList7']").attr("checked" , false);
				}
			}
		}); // --.
		
		// 初始化，抵用券类型数据.
		Init_Vouchers_Info();
		
		// 根据抵用券类型的唯一标识，联动抵用券类型的相关属性.
		$('#vouchers_id').change(function(){
			// console.log("isEntrustedPay：" + $(this).children('option:selected').val());
			var vouchers_id = $(this).children('option:selected').val();
			// console.log("isEntrustedPay：" + isEntrustedPay);
			vouchersInfoByVouchersId(vouchers_id);
		});// --

	});
	
	function Init_Vouchers_Info() {
		// 抵用券类型ID.
		var vouchers_id = $('#vouchers_id').val();
		vouchersInfoByVouchersId(vouchers_id);
	}
	
	function vouchersInfoByVouchersId(vouchers_id){
		$.ajax({
			url : "${ctx}/activity/aVouchersDic/vouchersInfoByVouchersId", 
			type : "post",
			async : false,
			contentType : "application/x-www-form-urlencoded",
			dataType : "json",
			data : {
				"vouchersId" : vouchers_id
			},
			success : function(data) {
				var a_vouchers_dic = data.aVouchersDic;
				// 项目期限范围.
				if(a_vouchers_dic.spans == 1){
					$("#spans").val("通用");
				} else {
					$("#spans").val(a_vouchers_dic.spans);
				}
				// 起投金额.
				$("#limitAmount").val(a_vouchers_dic.limitAmountStr);
				// 逾期天数.
				$("#overdueDays").val(a_vouchers_dic.overdueDays);
				// 备注.
				$("#remark").val(a_vouchers_dic.remarks);
			},
			error : function(data) {
				alert("程序异常");
			}
		});
	}

</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/activity/userVouchersHistory/">抵用券列表</a></li>
		<li class="active"><a href="${ctx}/activity/userVouchersHistory/rechargeForm?id=${userVouchersHistory.id}">抵用券充值</a></li>
		<li><a href="${ctx}/activity/userVouchersHistory/rechargeAllForm?id=${userVouchersHistory.id}">抵用券批充</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="userVouchersHistory" action="${ctx}/activity/userVouchersHistory/save" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">移动电话：</label>
			<div class="controls">
				<form:input path="userInfo.name" htmlEscape="false" maxlength="11" onkeyup="value=value.replace(/[^\d]/g,'') " class="input-xlarge required" />
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">充值原因：</label>
			<div class="controls">
				<form:textarea path="rechargeReason" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge" readonly="false"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">抵用券：</label>
			<div class="controls">
				<form:select path="awardId" class="input-xxlarge required" id="vouchers_id">
					<form:option value="" label="请选择"/>
					<c:forEach var="dic" items="${userVouchersHistory.vouchersDics}">
						<form:option value="${dic.id}" label="${dic.id}，${dic.amountStr}，${dic.remarks}" />
					</c:forEach>
				</form:select>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">使用状态：</label>
			<div class="controls">
				<form:select path="state" class="input-medium" readonly="true">
					<form:options items="${fns:getDictList('a_user_awards_history_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">项目期限范围：</label>
			<div class="controls">
				<form:input path="spans" htmlEscape="false" class="input-xlarge" readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">起投金额(元)：</label>
			<div class="controls">
				<form:input path="limitAmount" htmlEscape="false" maxlength="11" class="input-xlarge" readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">逾期天数(天)：</label>
			<div class="controls">
				<form:input path="overdueDays" htmlEscape="false" maxlength="11" class="input-xlarge" readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge" readonly="true"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="activity:userVouchersHistory:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="充 值" />&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>