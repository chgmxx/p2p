<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>抵用券管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(
			function() {
				//$("#name").focus();
				$("#inputForm")
						.validate(
								{
									submitHandler : function(form) {
										loading('正在提交，请稍等...');
										form.submit();
									},
									errorContainer : "#messageBox",
									errorPlacement : function(error, element) {
										$("#messageBox").text("输入有误，请先更正。");
										if (element.is(":checkbox")
												|| element.is(":radio")
												|| element.parent().is(
														".input-append")) {
											error.appendTo(element.parent()
													.parent());
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
		<li><a href="${ctx}/activity/userVouchersHistory/">抵用券列表</a></li>
		<li class="active"><a href="${ctx}/activity/userVouchersHistory/viewForm?id=${userVouchersHistory.id}">抵用券详情</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="userVouchersHistory" action="${ctx}/activity/userVouchersHistory/save" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">客户手机：</label>
			<div class="controls">
				<form:input path="userInfo.name" htmlEscape="false" maxlength="64" class="input-xlarge" readonly="true" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">充值原因：</label>
			<div class="controls">
				<form:textarea path="rechargeReason" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge" readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户姓名：</label>
			<div class="controls">
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="64" class="input-xlarge" readonly="true" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">抵用券(RMB)：</label>
			<div class="controls">
				<form:input path="value" htmlEscape="false" maxlength="64" class="input-xlarge" readonly="true" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:input path="state" htmlEscape="false" maxlength="1" class="input-xlarge" readonly="true" value="${fns:getDictLabel(userVouchersHistory.state, 'a_user_awards_history_state', '')}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型：</label>
			<div class="controls">
				<form:input path="type" htmlEscape="false" maxlength="1" class="input-xlarge" readonly="true" value="${fns:getDictLabel(userVouchersHistory.type, 'a_user_awards_history_type', '')}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">投资项目：</label>
			<div class="controls">
				<form:input path="wloanTermProject.name" htmlEscape="false" maxlength="64" class="input-xlarge" readonly="true" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">获取日期：</label>
			<div class="controls">
				<input name="createDate" type="text" readonly="readonly" maxlength="20" class="input-large Wdate" value="<fmt:formatDate value="${userVouchersHistory.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">逾期日期：</label>
			<div class="controls">
				<input name="overdueDate" type="text" readonly="readonly" maxlength="20" class="input-large Wdate" value="<fmt:formatDate value="${userVouchersHistory.overdueDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">修改日期：</label>
			<div class="controls">
				<input name="updateDate" type="text" readonly="readonly" maxlength="20" class="input-large Wdate" value="<fmt:formatDate value="${userVouchersHistory.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" />
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>