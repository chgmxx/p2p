<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>借款资料管理</title>
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
		<li><a href="${ctx}/credit/annexFile/">中等网动产登记查询结果上传</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="creditAnnexFile" action="${ctx}/credit/annexFile/save?otherId=${creditInfoId}&returnUrl=${creditAnnexFile.returnUrl}" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<div class="control-group">
			<label class="control-label">资料图片：</label>
			<div class="controls">
			    <form:hidden id="url" path="url" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="url" type="images" uploadPath="/photo" selectMultiple="true" maxWidth="100" maxHeight="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">资料类型：</label>
			<div class="controls">
				<input type="text" value="中等网动产登记查询" readonly="readonly"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="credit:annexFile:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>