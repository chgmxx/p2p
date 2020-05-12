<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>银行托管-银行卡管理</title>
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
		<li><a href="${ctx}/cgb/cgbUserBankCard/">银行卡列表</a></li>
		<li class="active"><a href="${ctx}/cgb/cgbUserBankCard/form?id=${cgbUserBankCard.id}">银行卡<shiro:hasPermission name="cgb:cgbUserBankCard:edit">${not empty cgbUserBankCard.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="cgb:cgbUserBankCard:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="cgbUserBankCard" action="${ctx}/cgb/cgbUserBankCard/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">银行卡：</label>
			<div class="controls">
				<form:input path="bankAccountNo" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">银行代码：</label>
			<div class="controls">
				<form:input path="bankNo" htmlEscape="false" maxlength="10" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">绑卡时间：</label>
			<div class="controls">
				<input name="bindDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
					value="<fmt:formatDate value="${cgbUserBankCard.bindDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="state" class="input-xlarge ">
					<form:option value="" label=""/>
					<form:option value="0" label="未认证"/>
					<form:option value="1" label="已认证"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否默认银行卡：</label>
			<div class="controls">
				<form:select path="isDefault" class="input-xlarge ">
					<form:option value="" label=""/>
					<form:option value="2" label="默认"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">银行预留手机：</label>
			<div class="controls">
				<form:input path="bankCardPhone" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">银行名称：</label>
			<div class="controls">
				<form:input path="bankName" htmlEscape="false" maxlength="255" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="cgb:cgbUserBankCard:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>