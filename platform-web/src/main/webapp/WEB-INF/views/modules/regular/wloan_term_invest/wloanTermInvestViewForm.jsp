<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>投资记录管理</title>
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
		<li><a href="${ctx}/wloan_term_invest/wloanTermInvest/">投资记录列表</a></li>
		<li class="active"><a href="${ctx}/wloan_term_invest/wloanTermInvest/viewForm?id=${wloanTermInvest.id}">投资记录查看</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="wloanTermInvest" action="#" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<div class="span6">
				<label class="control-label">项目名称：</label>
				<div class="controls">
					<form:input path="wloanTermProject.name" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">利息：</label>
				<div class="controls">
					<form:input path="interest" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">投资人：</label>
				<div class="controls">
					<form:input path="userInfo.name" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">金额：</label>
				<div class="controls">
					<form:input path="amount" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">投标IP：</label>
				<div class="controls">
					<form:input path="ip" htmlEscape="false" maxlength="255" class="input-xlarge " readonly="true" />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">抵用券金额：</label>
				<div class="controls">
					<form:input path="voucherAmount" htmlEscape="false" maxlength="255" class="input-xlarge " readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span9">
				<label class="control-label">投资时间：</label>
				<div class="controls">
					<input name="registerDate" type="text" readonly="readonly" maxlength="20" class="input-large Wdate " value="<fmt:formatDate value="${wloanTermInvest.beginDate}" pattern="yyyy-MM-dd"/>" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span9">
				<label class="control-label">备注信息：</label>
				<div class="controls">
					<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge " readonly="true" />
				</div>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>