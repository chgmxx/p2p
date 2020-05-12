<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>banner列表</title>
<meta name="decorator" content="default" />

</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="cms:notice:view">
			<li><a href="${ctx}/cms/notice/?type=6">WeChat</a></li>
		</shiro:hasPermission>
		<li class="active"><a href="${ctx}/cms/notice/form?type=6"><shiro:hasPermission name="cms:notice:edit">${not empty notice.id ?'修改':'添加'}</shiro:hasPermission>
				<shiro:lacksPermission name="cms:notice:edit">查看</shiro:lacksPermission>WeChat</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="notice" action="${ctx}/cms/notice/save" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<form:hidden path="type" value="${notice.type }" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">标题:</label>
			<div class="controls">
				<form:textarea path="title" htmlEscape="false" maxlength="200" class="required" readonly="true" />
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">picture:</label>
			<div class="controls">
				<form:hidden id="nameImage" path="logopath" htmlEscape="false" maxlength="255" class="input-xlarge" readonly="true" />
				<sys:ckfinder input="nameImage" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100" readonly="true" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">内容:</label>
			<div class="controls">
				<form:textarea path="text" htmlEscape="false" maxlength="200" readonly="true" />
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
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>