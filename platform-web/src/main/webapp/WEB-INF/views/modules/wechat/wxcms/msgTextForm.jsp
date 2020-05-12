<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文本消息</title>
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
		<shiro:hasPermission name="wechat:msgtext:view"><li><a href="${ctx}/wechat/msgtext/list">文本消息</a></li></shiro:hasPermission>
		<li class="active">
			<a href="${ctx}/wechat/msgtext/form?id=${msgText.id}"><shiro:hasPermission name="wechat:msgtext:edit">${not empty msgText.id ?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="wechat:msgtext:view">查看</shiro:lacksPermission>消息</a>
		</li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="msgText" action="${ctx}/wechat/msgtext/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">关键字:</label>
			<div class="controls">
				<form:input path="msgBase.inputCode" htmlEscape="false" maxlength="200" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">内容:</label>
			<div class="controls">
				<form:input path="content" htmlEscape="false" maxlength="200" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		
		<div class="form-actions">
			<shiro:hasPermission name="cms:notice:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div> 
	</form:form>
</body>
</html>