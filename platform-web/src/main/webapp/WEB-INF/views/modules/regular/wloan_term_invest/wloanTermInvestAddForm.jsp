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
		<li class="active"><a href="${ctx}/wloan_term_invest/wloanTermInvest/addForm?id=${wloanTermInvest.id}">投资记录<shiro:hasPermission name="wloan_term_invest:wloanTermInvest:edit">${not empty wloanTermInvest.id?'修改':'添加'}</shiro:hasPermission> <shiro:lacksPermission name="wloan_term_invest:wloanTermInvest:edit">查看</shiro:lacksPermission></a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="wloanTermInvest" action="${ctx}/wloan_term_invest/wloanTermInvest/addSave" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<div class="span6">
				<label class="control-label">流水号：</label>
				<div class="controls">
					<form:input path="sn" htmlEscape="false" maxlength="64" class="input-xlarge " />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">冻结流水号：</label>
				<div class="controls">
					<form:input path="freezeSn" htmlEscape="false" maxlength="64" class="input-xlarge " />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">解冻流水号：</label>
				<div class="controls">
					<form:input path="unfreezeSn" htmlEscape="false" maxlength="64" class="input-xlarge " />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">项目ID：</label>
				<div class="controls">
					<form:input path="wloanTermProject.id" htmlEscape="false" maxlength="64" class="input-xlarge " />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">用户ID：</label>
				<div class="controls">
					<form:input path="userInfo.id" htmlEscape="false" maxlength="64" class="input-xlarge " />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">金额：</label>
				<div class="controls">
					<form:input path="amount" htmlEscape="false" maxlength="64" class="input-xlarge " />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">利息：</label>
				<div class="controls">
					<form:input path="interest" htmlEscape="false" maxlength="64" class="input-xlarge " />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">手续费：</label>
				<div class="controls">
					<form:input path="feeAmount" htmlEscape="false" maxlength="64" class="input-xlarge " />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">开始日期：</label>
				<div class="controls">
					<input name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate " value="<fmt:formatDate value="${wloanTermInvest.beginDate}" pattern="yyyy-MM-dd"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">结束日期：</label>
				<div class="controls">
					<input name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate " value="<fmt:formatDate value="${wloanTermInvest.endDate}" pattern="yyyy-MM-dd"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">投标IP：</label>
				<div class="controls">
					<form:input path="ip" htmlEscape="false" maxlength="255" class="input-xlarge " />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">状态：</label>
				<div class="controls">
					<form:select path="state" class="input-xlarge ">
						<form:option value="" label="请选择" />
						<form:options items="${fns:getDictList('wloan_term_invest_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">投标状态：</label>
				<div class="controls">
					<form:select path="bidState" class="input-xlarge ">
						<form:option value="" label="请选择" />
						<form:options items="${fns:getDictList('wloan_term_invest_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">抵用券金额：</label>
				<div class="controls">
					<form:input path="voucherAmount" htmlEscape="false" maxlength="255" class="input-xlarge " />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span9">
				<label class="control-label">备注信息：</label>
				<div class="controls">
					<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge " />
				</div>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="wloan_term_invest:wloanTermInvest:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" />&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>