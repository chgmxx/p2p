<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>图文消息</title>
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
		<shiro:hasPermission name="wechat:msgnews:view"><li><a href="${ctx}/wechat/msgnews/list">图文消息</a></li></shiro:hasPermission>
		<li class="active">
			<a href="${ctx}/wechat/msgnews/form?id=${msgNews.id}"><shiro:hasPermission name="wechat:msgnews:edit">${not empty msgNews.id ?'修改':'添加'}</shiro:hasPermission>消息</a>
		</li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="msgNews" action="${ctx}/wechat/msgnews/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">关键字:</label>
			<div class="controls">
				<form:input path="msgBase.inputCode" htmlEscape="false" maxlength="100" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">作者:</label>
			<div class="controls">
				<form:input path="author" htmlEscape="false" maxlength="100" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">标题:</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="100" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">简介:</label>
			<div class="controls">
				<form:textarea path="brief" htmlEscape="false" maxlength="100" rows="5" class="input-large valid span6" />
				<span class="help-inline"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">封面图片:</label>
			<div class="controls">
				<form:hidden id="picpath" path="picpath" htmlEscape="false" maxlength="255" class="input-xlarge" />
				<sys:ckfinder input="picpath" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100"  />
			</div>
			<div class="controls">
				<form:checkboxes path="showpic" items="${fns:getDictList('msgnews_showpic')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
		</div>
		<div class="control-group">
				<label class="control-label">内容:</label><span style="color:#ccc"> 如果填写了下方的 <span style="color:#555;">原文链接</span> ，内容可以不填写，微信中点击消息，自动跳转到原文链接</span>
				<div class="controls">
					<form:textarea id="description" htmlEscape="true" path="description" rows="4" maxlength="4000" class="input-large"/>
					<sys:ckeditor replace="description" uploadPath="/wechat/msgnews" />
				</div>
		</div>
		<div class="control-group">
			<label class="control-label">原文链接:</label>
			<div class="controls">
				<form:input path="url" htmlEscape="false" maxlength="200" class="input-xxlarge "/>
				<span class="help-inline"></span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="cms:notice:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div> 
	</form:form>
</body>
</html>