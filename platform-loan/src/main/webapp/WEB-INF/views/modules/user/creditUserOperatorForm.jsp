<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>借款端，操作人管理-添加操作人</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
	<script type="text/javascript" src="${ctxStatic}/js/CheckUtils.js"></script>
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
	<style>
		.nav-tabs>li>a{
	    font-size: 20px;
    margin-bottom: 15px;
	}

.form-actions {
    background-color: #ffffff;
    border-top:0;
}
	</style>
</head>
<body>
  <div class="nav_head">添加操作人</div>
	<form:form id="inputForm" modelAttribute="creditUserOperator" action="${ctx}/credit/cuoperator/creditUserOperator/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="creditUserId"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">手机号：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" maxlength="64" class="input-xlarge required" id="phone"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">姓名：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">密码：</label>
			<div class="controls">
				<form:input path="password" htmlEscape="false" maxlength="255" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">账户状态</label>
			<div class="controls">
				<form:select path="state" style="width:177px">
					<form:option value="" label="请选择" />
					<form:option value="0" label="不可用" />
					<form:option value="1" label="可用" />
				</form:select>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>