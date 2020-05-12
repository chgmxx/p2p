<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>客户银行卡更换管理</title>
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
		<li><a href="${ctx}/bank/userBankCardHistory/">客户银行卡更换列表</a></li>
		<li class="active"><a href="${ctx}/bank/userBankCardHistory/addForm?id=${userBankCardHistory.id}">客户银行卡更换<shiro:hasPermission name="bank:userBankCardHistory:edit">新增</shiro:hasPermission> <shiro:lacksPermission name="bank:userBankCardHistory:edit">查看</shiro:lacksPermission></a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="userBankCardHistory" action="${ctx}/bank/userBankCardHistory/save" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">客户ID：</label>
			<div class="controls">
				<form:input path="userId" htmlEscape="false" maxlength="64" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户姓名：</label>
			<div class="controls">
				<form:input path="realName" htmlEscape="false" maxlength="64" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户移动电话：</label>
			<div class="controls">
				<form:input path="mobilePhone" htmlEscape="false" maxlength="11" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">身份证号码：</label>
			<div class="controls">
				<form:input path="identityCardNo" htmlEscape="false" maxlength="64" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">旧银行卡号码：</label>
			<div class="controls">
				<form:input path="oldBankCardNo" htmlEscape="false" maxlength="64" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">新银行卡号码：</label>
			<div class="controls">
				<form:input path="newBankCardNo" htmlEscape="false" maxlength="64" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="state" class="input-xlarge ">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('replace_identity_card_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">更换时间：</label>
			<div class="controls">
				<input name="replaceDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate " value="<fmt:formatDate value="${userBankCardHistory.replaceDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注信息：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">身份证正面照片：</label>
			<div class="controls">
				<form:input path="identityCardForwardSidePicUrl" htmlEscape="false" maxlength="255" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">身份证反面照片：</label>
			<div class="controls">
				<form:input path="identityCardBackSidePicUrl" htmlEscape="false" maxlength="255" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">本人手持身份证照片：</label>
			<div class="controls">
				<form:input path="identityCardAndPersonPicUrl" htmlEscape="false" maxlength="255" class="input-xlarge " />
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="bank:userBankCardHistory:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" />&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>