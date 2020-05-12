<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>账户管理管理</title>
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
		<li><a href="${ctx}/useraccount/userAccountInfo/">客户账户列表</a></li>
		<li class="active"><a href="${ctx}/useraccount/userAccountInfo/form?id=${userAccountInfo.id}">客户账户<shiro:hasPermission name="useraccount:userAccountInfo:edit">${'查看'}</shiro:hasPermission><shiro:lacksPermission name="useraccount:userAccountInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="userAccountInfo" action="${ctx}/useraccount/userAccountInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">姓名：</label>
				<div class="controls">
					<form:input path="userInfo.realName" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">手机号：</label>
				<div class="controls">
					<form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-xlarge required" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">账户总额：</label>
				<div class="controls">
					<input name="totalAmount" type="text" class="input-xlarge required number" readonly="readonly"
						value="<fmt:formatNumber type="number" value="${userAccountInfo.totalAmount } " minFractionDigits="2" maxFractionDigits="2" />"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">总收益：</label>
				<div class="controls">
					<form:input path="totalInterest" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">可用金额：</label>
				<div class="controls">
					<input name="availableAmount" type="text" class="input-xlarge required number" readonly="readonly"
						value="<fmt:formatNumber type="number" value="${userAccountInfo.availableAmount } " minFractionDigits="2" maxFractionDigits="2" />"/>
				</div>
			</div>
			<div class="span6 ">
				<label class="control-label">冻结金额：</label>
				<div class="controls">
					<form:input path="freezeAmount" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
		</div>
				
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">充值总额：</label>
				<div class="controls">
					<form:input path="rechargeAmount" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">充值总次数：</label>
				<div class="controls">
					<form:input path="rechargeCount" htmlEscape="false" maxlength="11" class="input-xlarge required digits" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">提现金额：</label>
				<div class="controls">
					<form:input path="cashAmount" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">提现总次数：</label>
				<div class="controls">
					<form:input path="cashCount" htmlEscape="false" maxlength="11" class="input-xlarge required digits" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">定期投资总金额：</label>
				<div class="controls">
					<form:input path="regularTotalAmount" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
			<div class="span6 ">
				<label class="control-label">定期累计收益：</label>
				<div class="controls">
					<form:input path="regularTotalInterest" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">定期待收本金：</label>
				<div class="controls">
					<form:input path="regularDuePrincipal" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
			<div class="span6 ">
				<label class="control-label">定期待收收益：</label>
				<div class="controls">
					<form:input path="regularDueInterest" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">活期总收益：</label>
				<div class="controls">
					<form:input path="currentTotalInterest" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
			<div class="span6 ">
				<label class="control-label">活期累计投资金额：</label>
				<div class="controls">
					<form:input path="currentTotalAmount" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">活期昨日收益：</label>
				<div class="controls">
					<form:input path="currentYesterdayInterest" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
			<div class="span6 ">
				<label class="control-label">定期昨日收益：</label>
				<div class="controls">
					<form:input path="reguarYesterdayInterest" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">活期投资金额：</label>
				<div class="controls">
					<form:input path="currentAmount" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="form-actions">
			<div class="span6 ">
				<label class="control-label"></label>
				<div class="controls">
				</div>
			</div>
			<div class="span6">
				<label class="control-label"></label>
				<div class="controls">
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>