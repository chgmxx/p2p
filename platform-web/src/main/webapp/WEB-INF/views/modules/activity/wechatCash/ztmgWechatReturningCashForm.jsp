<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信返现管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
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
		<li><a href="${ctx}/activity/ztmgWechatReturningCash/">微信返现列表</a></li>
		<li><a href="${ctx}/activity/ztmgWechatReturningCash/formall?id=${ztmgWechatReturningCash.id}">微信返现批量充值</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ztmgWechatReturningCash" action="${ctx}/activity/ztmgWechatReturningCash/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">手机号：</label>
			<div class="controls">
				<form:input path="mobilePhone" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户名：</label>
			<div class="controls">
				<form:input path="realName" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">返现金额：</label>
			<div class="controls">
				<form:input path="payAmount" htmlEscape="false" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>