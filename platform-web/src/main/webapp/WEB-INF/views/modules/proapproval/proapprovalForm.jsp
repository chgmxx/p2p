<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
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
		<li><a href="${ctx}/approval/proinfo/">放款申请信息列表</a></li>
		<li class="active"><a href="${ctx}/approval/proinfo/form">放款申请信息添加</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="projectApproval" action="${ctx}/approval/proinfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="wloanTermProject.id"/>
		<sys:message content="${message}"/>		
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">放款条件：</label>
				<div class="controls">
					<form:textarea path="rcerLoanTerms" htmlEscape="false" class="input-xxlarge" rows="4" />
				</div>
			</div>
		</div>	
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">落实情况：</label>
				<div class="controls">
					<form:textarea path="rcerImplement" htmlEscape="false"  class="input-xxlarge" rows="4"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="form-actions">
				<shiro:hasPermission name="projectApproval:projectApproval:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</div>	
	</form:form>
</body>
</html>