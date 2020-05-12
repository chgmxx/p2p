<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文件上传</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#value").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/coupon/list">优惠券管理列表</a></li>
		<li class="active"><a href="${ctx}/coupon/form">优惠券添加</a></li>
	</ul><br>
	<form:form id="inputForm" modelAttribute="couponInfo" action="${ctx}/coupon/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<div class="control-group">
			<label class="control-label">金额：</label>
			<div class="controls">
			<form:select path="amount" class="input-medium">
					<form:options items="${fns:getDictList('coupon_info_money')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select><span class="help-inline">RMB<font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型：</label>
			<div class="controls">
				<form:select path="type" class="input-medium">
				<form:options items="${fns:getDictList('coupon_info_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select><span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">有效期限：</label>
			<div class="controls">
				<form:select path="overdue" class="input-medium">
					<form:options items="${fns:getDictList('regular_wloan_span')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select><span class="help-inline">天<font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">起投金额：</label>
			<div class="controls">
				<form:input path="limitMoney" htmlEscape="false" maxlength="80" class="number required" /><span class="help-inline">RMB<font color="red">*</font></span>
			</div>
		</div>
		<div class="form-actions">
		<shiro:hasPermission name="coupon:info:edit">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>