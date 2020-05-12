<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分配角色</title>
	<meta name="decorator" content="blank"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<script type="text/javascript">

		// 初始化
		$(document).ready(function(){

			// $("#messageBox").show();

			$("#inputForm").validate({
				submitHandler: function(form){
					var zipUrl = $("#nameImage").val();
					if(zipUrl == ""){
						$("#messageBox").show();
					} else {
						loading('正在提交，请稍等...');
						form.submit();
					}
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					// $("#messageBox").text("输入有误，请先更正。");
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
	<div class="form-actions">
	</div>
	<form:form id="inputForm" modelAttribute="creditAuditInfo" action="${ctx}/loan/audit/creditAuditInfo/auditSave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="status"/>
		<sys:message content="请上传压缩包。"/>
		<div class="control-group">
			<label class="control-label">压缩包:</label>
			<div class="controls">
				<form:hidden id="nameImage" path="zipUrl" htmlEscape="false" maxlength="255" class="input-xxlarge required"/>
				<sys:ckfinder input="nameImage" type="files" uploadPath="/files" selectMultiple="false" maxWidth="100" maxHeight="100"/>
			</div>
		</div>
		<c:if test="${creditAuditInfo.status == '3'}">
			<div class="control-group">
				<label class="control-label">驳回原因:</label>
				<div class="controls">
					<form:textarea path="actionMessage" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge required"/>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" cols="" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="loan:audit:creditAuditInfo:view">
				<c:if test="${creditAuditInfo.status == '2'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="通 过"/>&nbsp;
				</c:if>
				<c:if test="${creditAuditInfo.status == '3'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="驳 回"/>&nbsp;
				</c:if>
			</shiro:hasPermission>
			<!-- <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/> -->
		</div>
	</form:form>
</body>
</html>
