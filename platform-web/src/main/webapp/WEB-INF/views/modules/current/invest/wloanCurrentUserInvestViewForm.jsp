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
		<li><a href="${ctx}/current/invest/wloanCurrentProjectInvest/">活期项目投资列表</a></li>
		<li class="active"><a href="${ctx}/current/invest/wloanCurrentProjectInvest/findCome?id=${wloanCurrentProjectInvest.id }">活期项目资金来源详细信息查看</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="wloanCurrentUserInvest" action="#" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<div class="span7">
				<label class="control-label">客户手机：</label>
				<div class="controls">
					<form:input path="userInfo.name" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
			<div class="span7">
				<label class="control-label">客户姓名：</label>
				<div class="controls">
					<form:input path="userInfo.realName" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span7">
				<label class="control-label">金额：</label>
				<div class="controls">
					<form:input path="amount" htmlEscape="false" class="input-xlarge " readonly="true" />
					<span class="help-inline">RMB</span>
				</div>
			</div>
			<div class="span7">
				<label class="control-label">抵用券金额：</label>
				<div class="controls">
					<form:input path="voucherAmount" htmlEscape="false" class="input-xlarge " readonly="true" />
					<span class="help-inline">RMB</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span7">
				<label class="control-label">投资日期：</label>
				<div class="controls">
					<input name="bidDate" type="text" readonly="readonly" maxlength="20" class="input-medium" style="width: 270px;"
						value="<fmt:formatDate value="${wloanCurrentUserInvest.bidDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" />
				</div>
			</div>
			<div class="span7">
				<label class="control-label">IP：</label>
				<div class="controls">
					<form:input path="ip" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span7">
				<label class="control-label">数据状态：</label>
				<div class="controls">
					<form:input path="state" htmlEscape="false" maxlength="64" class="input-xlarge " value="${fns:getDictLabel(wloanCurrentUserInvest.state, 'wloan_current_user_invest_state', '')}" readonly="true" />
				</div>
			</div>
			<div class="span7">
				<label class="control-label">投资状态：</label>
				<div class="controls">
					<form:input path="bidState" htmlEscape="false" maxlength="64" class="input-xlarge " value="${fns:getDictLabel(wloanCurrentUserInvest.bidState, 'wloan_current_user_invest_bid_state', '')}" readonly="true" />
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