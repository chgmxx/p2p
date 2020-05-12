<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>抵用券类型管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		//$("#name").focus();
		$("#inputForm").validate({
			submitHandler : function(form) {
				loading('正在提交，请稍等...');
				form.submit();
			},
			errorContainer : "#messageBox",
			errorPlacement : function(error, element) {
				$("#messageBox").text("输入有误，请先更正。");
				if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
					error.appendTo(element.parent().parent());
				} else {
					error.insertAfter(element);
				}
			}
		});

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

	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/activity/aVouchersDic/">抵用券类型列表</a></li>
		<li class="active"><a href="${ctx}/activity/aVouchersDic/addForm?id=${aVouchersDic.id}">抵用券类型<shiro:hasPermission name="activity:aVouchersDic:edit">${not empty aVouchersDic.id?'修改':'添加'}</shiro:hasPermission> <shiro:lacksPermission name="activity:aVouchersDic:edit">查看</shiro:lacksPermission></a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="aVouchersDic" action="${ctx}/activity/aVouchersDic/addSave" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="state" class="input-xlarge required">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('a_vouchers_dic_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">逾期天数(天)：</label>
			<div class="controls">
				<form:input path="overdueDays" htmlEscape="false" maxlength="11" class="input-xlarge number required" onkeyup="value=value.replace(/[^\d]/g,'')"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">抵用券金额(元)：</label>
			<div class="controls">
				<form:input path="amount" htmlEscape="false" class="input-xlarge  number required" onkeyup="value=value.replace(/[^\d]/g,'')"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">起投金额(元)：</label>
			<div class="controls">
				<form:input path="limitAmount" htmlEscape="false" class="input-xlarge  number required" onkeyup="value=value.replace(/[^\d]/g,'')"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">项目期限范围:</label>
			<div class="controls">
				<form:checkboxes path="spanIdList" items="${spans}" itemLabel="name" itemValue="id" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="label">项目期限范围：通用的选择与期限范围的选择属于互斥关系。</label>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge required" />
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="activity:aVouchersDic:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" />&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>