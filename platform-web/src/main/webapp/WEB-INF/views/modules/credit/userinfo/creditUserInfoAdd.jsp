<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>信贷用户管理</title>
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
		<li><a href="${ctx}/credit/userinfo/creditUserInfo/">信贷用户列表</a></li>
		<li class="active"><a href="${ctx}/credit/userinfo/creditUserInfo/form?id=${creditUserInfo.id}">核心企业用户<shiro:hasPermission name="credit:userinfo:creditUserInfo:edit">${not empty creditUserInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="credit:userinfo:creditUserInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="creditUserInfo" action="${ctx}/credit/userinfo/creditUserInfo/middlemenSave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">手机号：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" maxlength="64" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">密码：</label>
			<div class="controls">
				<form:input path="pwd" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">企业名称：</label>
			<div class="controls">
				<form:input path="enterpriseFullName" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">资料图片：</label>
			<div class="controls">
			    <form:hidden id="url" path="annexFile.url" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="url" type="images" uploadPath="/photo" selectMultiple="true" maxWidth="100" maxHeight="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">指向地址:</label>
			<div class="controls">
				<form:input path="annexFile.remark" htmlEscape="false" maxlength="200"/>
				<span class="help-inline"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">企业简称：</label>
			<div class="controls">
				<form:input path="creditScore" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">星级：</label>
			<div class="controls">
				<form:select path="level" class="input-xlarge required">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('comp_level')}" itemLabel="label" itemValue="value" htmlEscape="false" />
			    </form:select>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="credit:userinfo:creditUserInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>