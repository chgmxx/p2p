<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>公告管理</title>
	<meta name="decorator" content="default"/>
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="cms:notice:view"><li><a href="${ctx}/cms/notice/?type=2">公告列表</a></li></shiro:hasPermission>
		<li class="active">
			<a href="${ctx}/cms/notice/form?type=2">公告<shiro:hasPermission name="cms:notice:edit">${not empty notice.id ?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="cms:notice:edit">查看</shiro:lacksPermission></a>
		</li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="notice" action="${ctx}/cms/notice/save" method="post" class="form-horizontal">
		<form:hidden path="id" value="${notice.id }"/>
		<form:hidden path="type" value="${notice.type }"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">标题:</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="200" value="${notice.title }" class="required input-xxlarge" readonly="true"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">公告内容:</label>
			<div class="controls">
				<form:textarea id="text" htmlEscape="true" path="text" rows="4" maxlength="4000" value="${notice.text }" class="required" readonly="true"/>
				<sys:ckeditor replace="text" uploadPath="/cms/notice"  />
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div> 
	</form:form>
</body>
</html>