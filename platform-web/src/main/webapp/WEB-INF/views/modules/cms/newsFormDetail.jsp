<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>新闻管理</title>
	<meta name="decorator" content="default"/>
	 
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="cms:notice:view"><li><a href="${ctx}/cms/notice/?type=3">新闻列表</a></li></shiro:hasPermission>
		<li class="active">
			<a href="${ctx}/cms/notice/form?type=3">新闻<shiro:hasPermission name="cms:notice:edit">${not empty notice.id ?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="cms:notice:edit">查看</shiro:lacksPermission></a>
		</li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="notice" action="${ctx}/cms/notice/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="type" value="${notice.type }" />
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">标题:</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="200" class="input-xxlarge required" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">副标题:</label>
			<div class="controls">
				<form:textarea path="head" htmlEscape="true" rows="4" maxlength="2000" class="input-xxlarge required" readonly="true"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">新闻来源:</label>
			<div class="controls">
				<form:input path="sources" htmlEscape="false" maxlength="600" class="input-xxlarge" readonly="true"/>
				<span class="help-inline"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">来源时间:</label>
			<div class="controls">
				<input id="sourcesDate" name="sourcesDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${notice.sourcesDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					  />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">显示顺序:</label>
			<div class="controls">
				<form:input path="orderSum" htmlEscape="false" maxlength="200" readonly="true"/>
				<span class="help-inline"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">新闻图片:</label>
			<div class="controls">
				<form:hidden id="nameImage" path="logopath" htmlEscape="false" maxlength="255" class="input-xlarge" readonly="true"/>
				<sys:ckfinder input="nameImage" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100"  readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">新闻内容:</label>
			<div class="controls">
				<form:textarea id="text" htmlEscape="true" path="text" rows="4" maxlength="4000" class="input-xxlarge" readonly="true"/>
				<sys:ckeditor replace="text" uploadPath="/cms/notice" />
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div> 
	</form:form>
</body>
</html>