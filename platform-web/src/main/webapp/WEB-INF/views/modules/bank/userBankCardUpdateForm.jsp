<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户银行卡更换管理</title>
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
		<li><a href="${ctx}/bank/userBankCard/">客户银行卡列表</a></li>
		<li class="active"><a href="${ctx}/bank/userBankCard/updateForm?id=${userBankCard.id}">客户银行卡<shiro:hasPermission name="bank:userBankCard:edit">修改</shiro:hasPermission><shiro:lacksPermission name="bank:userBankCard:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="userBankCard" action="${ctx}/bank/userBankCard/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">银行卡号码：</label>
			<div class="controls">
				<form:input path="bankAccountNo" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户账号：</label>
			<div class="controls">
				<form:input path="userId" htmlEscape="false" maxlength="32" class="input-xlarge required" readonly='true'/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户账户：</label>
			<div class="controls">
				<form:input path="accountId" htmlEscape="false" maxlength="32" class="input-xlarge required" readonly='true'/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">连连绑定号：</label>
			<div class="controls">
				<form:input path="llAgreeNo" htmlEscape="false" maxlength="50" class="input-xlarge required" readonly='true'/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">银行代码：</label>
			<div class="controls">
				<form:input path="bankNo" htmlEscape="false" maxlength="10" class="input-xlarge required" readonly='true'/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">流水号：</label>
			<div class="controls">
				<form:input path="sn" htmlEscape="false" maxlength="50" class="input-xlarge required" readonly='true'/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="state" class="input-xlarge ">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('bank_card_state')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否默认银行卡：</label>
			<div class="controls">
				<form:select path="isDefault" class="input-xlarge ">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('bank_card_is_default')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="bank:userBankCard:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>