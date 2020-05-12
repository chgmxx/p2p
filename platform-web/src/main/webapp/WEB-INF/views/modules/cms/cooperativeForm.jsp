<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					if((CKEDITOR.instances.text.getData()!='')){
						loading('正在提交，请稍等...');
						form.submit();
					} else{
						alert('公告内容不能为空');
					}
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
		<shiro:hasPermission name="cms:notice:view"><li><a href="${ctx}/cms/notice/?type=5">机构列表</a></li></shiro:hasPermission>
		<li class="active">
			<a href="${ctx}/cms/notice/form?id=${notice.id }&type=5">机构<shiro:hasPermission name="cms:notice:edit">${not empty notice.id ?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="cms:notice:edit">查看</shiro:lacksPermission></a>
		</li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="notice" action="${ctx}/cms/notice/save" method="post" class="form-horizontal">
		<form:hidden path="id" value="${notice.id }"/>
		<form:hidden path="type" value="${notice.type }"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">机构名称:</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="200" value="${notice.title }" class="required input-xxlarge" />
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">机构简介:</label>
			<div class="controls">
				<form:textarea path="head" htmlEscape="true" rows="4" maxlength="2000" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">显示顺序:</label>
			<div class="controls">
				<form:input path="orderSum" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
				<span class="help-inline">数字越大位置越靠前</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">机构图标:</label>
			<div class="controls">
				<form:hidden id="nameImage" path="logopath" htmlEscape="false" maxlength="255" class="input-xlarge" />
				<sys:ckfinder input="nameImage" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100"  />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">机构详情:</label>
			<div class="controls">
				<form:textarea id="text" htmlEscape="true" path="text" rows="4" maxlength="4000" value="${notice.text }" class="required"/>
				<sys:ckeditor replace="text" uploadPath="/cms/notice"  />
			</div>
		</div>
		
		<div class="form-actions">
			<shiro:hasPermission name="cms:notice:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div> 
	</form:form>
</body>
</html>