<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>活期项目投资管理</title>
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
		<li><a href="${ctx}/current/invest/wloanCurrentProjectInvest/">活期项目投资列表</a></li>
		<li class="active"><a href="${ctx}/current/invest/wloanCurrentProjectInvest/form?id=${wloanCurrentProjectInvest.id}">活期项目投资信息查看</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="wloanCurrentProjectInvest" action="${ctx}/current/invest/wloanCurrentProjectInvest/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">项目：</label>
			<div class="controls">
				<form:input path="currentProjectInfo.name" htmlEscape="false" maxlength="32" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="32" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">金额：</label>
			<div class="controls">
				<form:input path="amount" htmlEscape="false" class="input-xlarge  number" readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">投资日期：</label>
			<div class="controls">
				<input name="bidDate" type="text" readonly="readonly" maxlength="20" class="input-medium" style="width:270px"
					value="<fmt:formatDate value="${wloanCurrentProjectInvest.bidDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">抵用券金额：</label>
			<div class="controls">
				<form:input path="vouvherAmount" htmlEscape="false" class="input-xlarge  number" readonly="true"/>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>