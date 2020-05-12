<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>账户信息</title>
</head>
<body>
	<form:form id="inputForm" modelAttribute="userInfo" action="${ctx}/sys/user/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>

		<div class="control-group">
			<label class="control-label">账户名称:</label>
			<div class="controls">
				<input id="accountName" name="accountName" type="text" value="${userInfo.enterpriseFullName}">
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">账户余额:</label>
			<div class="controls">
				<input id="accountAmount" name="accountAmount" type="text" value="${userInfo.creditUserAccount.availableAmount}">
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">账户余额:</label>
			<div class="controls">
                <sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"
					title="部门" url="/sys/office/treeData?type=2" cssClass="required" notAllowSelectParent="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">绑定银行卡:</label>
			<div class="controls">
				<input id="bankCard" name="bankCard" type="text" value="${userInfo.cgbUserBankCard.bankAccountNo}">
			</div>
		</div>	
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="充值" onclick="history.go(-1)"/>
			<input id="btnCancel" class="btn" type="button" value="提现" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>