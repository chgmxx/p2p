<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>banner列表</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(
			function() {
				$("#name").focus();
				$("#inputForm")
						.validate(
								{
									submitHandler : function(form) {
										loading('正在提交，请稍等...');
										form.submit();
									},
									errorContainer : "#messageBox",
									errorPlacement : function(error, element) {
										$("#messageBox").text("输入有误，请先更正。");
										if (element.is(":checkbox")
												|| element.is(":radio")
												|| element.parent().is(
														".input-append")) {
											error.appendTo(element.parent()
													.parent());
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
		<shiro:hasPermission name="cms:notice:view">
			<li><a href="${ctx}/cms/notice/?type=6">WeChat</a></li>
		</shiro:hasPermission>
		<li class="active"><a href="${ctx}/cms/notice/form?id=${notice.id }&type=6"><shiro:hasPermission name="cms:notice:edit">${not empty notice.id ?'修改':'添加'}</shiro:hasPermission> <shiro:lacksPermission name="cms:notice:edit">查看</shiro:lacksPermission>WeChat</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="notice" action="${ctx}/cms/notice/save" method="post" class="form-horizontal">
		<form:hidden path="id" value="${notice.id}" />
		<form:hidden path="type" value="${notice.type}" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">标题:</label>
			<div class="controls">
				<form:textarea path="title" htmlEscape="false" maxlength="200" class="required" />
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">picture:</label>
			<div class="controls">
				<form:hidden id="nameImage" path="logopath" htmlEscape="false" maxlength="255" class="input-xlarge" />
				<sys:ckfinder input="nameImage" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">内容:</label>
			<div class="controls">
				<form:textarea path="text" htmlEscape="false" maxlength="200" />
				<span class="help-inline"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分享方式:</label>
			<div class="controls">
				<form:select path="orderSum" class="input-medium">
					<form:options items="${fns:getDictList('wechat_share_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<span class="help-inline"></span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="cms:notice:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" />&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>