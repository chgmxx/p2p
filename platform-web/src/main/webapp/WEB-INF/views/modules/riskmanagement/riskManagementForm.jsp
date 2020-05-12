<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>风控企业信息管理</title>
	<meta name="decorator" content="default"/>
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
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/riskmanagement/riskManagementMessage/">风控企业信息列表</a></li>
		<c:if test="${ usertype == '8' }">
		<li class="active"><a href="${ctx}/riskmanagement/riskManagementMessage/form?id=${riskManagement.id}">风控企业信息<shiro:hasPermission name="riskmanagement:riskManagementMessage:edit">${not empty riskManagement.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="riskmanagement:riskManagementMessage:edit">查看</shiro:lacksPermission></a></li>
	    </c:if>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="riskManagement" action="${ctx}/riskmanagement/riskManagementMessage/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">企业名称：</label>
			<div class="controls">
				<form:input path="companyName" htmlEscape="false" maxlength="255" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">企业介绍：</label>
			<div class="controls">
		        <form:hidden id="nameFiles" path="docUrl" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="nameFiles" type="files" uploadPath="/companyMessage" selectMultiple="true"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="riskmanagement:riskManagementMessage:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>