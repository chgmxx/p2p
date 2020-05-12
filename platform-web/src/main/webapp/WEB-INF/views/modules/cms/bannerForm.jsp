<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>banner列表</title>
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
		<shiro:hasPermission name="cms:notice:view">
			<li><a href="${ctx}/cms/notice/?type=1">电脑端-BANNER</a></li>
			<li><a href="${ctx}/cms/notice/?type=0">移动端-BANNER</a></li>
		</shiro:hasPermission>
		<li class="active">
			<a href="${ctx}/cms/notice/form?id=${notice.id }&type=1"><shiro:hasPermission name="cms:notice:edit">${not empty notice.id ?'修改':'新增'}</shiro:hasPermission><shiro:lacksPermission name="cms:notice:edit">查看</shiro:lacksPermission>-BANNER</a>
		</li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="notice" action="${ctx}/cms/notice/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">标题：</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="200" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">BANNER：</label>
			<div class="controls">
				<form:hidden id="nameImage" path="logopath" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="nameImage" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">指向地址：</label>
			<div class="controls">
				<form:input path="text" htmlEscape="false" maxlength="200"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">显示顺序：</label>
			<div class="controls">
				<form:input path="orderSum" class="input-large number required" htmlEscape="false" maxlength="200"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">应用范围：</label>
			<div class="controls">
				<form:select path="type">
					<form:option value="1">电脑端</form:option>
					<form:option value="0">移动端</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否用于活动展示：</label>
			<div class="controls">
				<form:select path="bannerType">
					<form:option value="-1">否</form:option>
					<form:option value="0">是</form:option>
				</form:select>
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