<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>二维码保存</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
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
		<shiro:hasPermission name="wechat:erweima:view"><li><a href="${ctx}/wechat/erweima/list">二维码</a></li></shiro:hasPermission>
		<li class="active">
			<a href="${ctx}/wechat/erweima/form"><shiro:hasPermission name="wechat:erweima:edit">${not empty notice.id ?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="wechat:erweima:edit">查看</shiro:lacksPermission>二维码</a>
		</li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="accountErWeiMa" action="${ctx}/wechat/erweima/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">渠道号:</label>
			<div class="controls">
				<form:input path="channelCode" htmlEscape="false" maxlength="200" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">渠道名称:</label>
			<div class="controls">
				<form:input path="channelName" htmlEscape="false" maxlength="200" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="wechat:erweima:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div> 
	</form:form>
</body>
</html>