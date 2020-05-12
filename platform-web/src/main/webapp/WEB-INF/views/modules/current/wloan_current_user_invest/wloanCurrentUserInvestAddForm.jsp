<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>活期客户投资管理</title>
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
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/">活期客户投资列表</a></li>
		<li class="active"><a href="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/addForm?id=${wloanCurrentUserInvest.id}">活期客户投资<shiro:hasPermission name="wloan_current_user_invest:wloanCurrentUserInvest:edit">${not empty wloanCurrentUserInvest.id?'修改':'添加'}</shiro:hasPermission> <shiro:lacksPermission name="wloan_current_user_invest:wloanCurrentUserInvest:edit">查看</shiro:lacksPermission></a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="wloanCurrentUserInvest" action="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/addSave" method="post" class="form-horizontal">
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">客户ID：</label>
			<div class="controls">
				<form:input path="userInfo.id" htmlEscape="false" maxlength="64" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">投资金额：</label>
			<div class="controls">
				<form:input path="amount" htmlEscape="false" class="input-xlarge " />
				<span class="help-inline">RMB</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">抵用券金额：</label>
			<div class="controls">
				<form:input path="voucherAmount" htmlEscape="false" class="input-xlarge " />
				<span class="help-inline">RMB</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注信息：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge " />
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="wloan_current_user_invest:wloanCurrentUserInvest:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" />&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>