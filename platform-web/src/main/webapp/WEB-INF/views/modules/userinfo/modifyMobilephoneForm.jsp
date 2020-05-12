<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				rules : {
					name : {digits:true,minlength:[11],maxlength:[11]}
				},
				messages : {
					name : {digits:"请输入正确格式的手机号码",minlength:"请输入正确长度的手机号码",maxlength:"请输入正确长度的手机号码"}
				},
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
		<li><a href="${ctx}/userinfo/userInfo/">用户信息列表</a></li>
		<li class="active"><a href="${ctx}/userinfo/userInfo/modifyMobilephoneForm?id=${userInfo.id}">更换手机</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="userInfo" action="${ctx}/userinfo/userInfo/modifySave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>

		<div class="control-group">
			<label class="control-label">原手机号码：</label>
			<div class="controls">
				<form:input path="oldMobilephone" htmlEscape="false" maxlength="32" class="input-xlarge required" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">新手机号码：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="32" class="input-xlarge required" onkeyup="value=value.replace(/[^\d]/g,'')" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="activity:aVouchersDic:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" />&nbsp;
			</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>