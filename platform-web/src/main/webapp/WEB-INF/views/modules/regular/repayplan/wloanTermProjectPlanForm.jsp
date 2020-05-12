<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>定期项目信息管理</title>
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
		<li><a href="${ctx}/wloanproject/wloanTermProjectPlan/">项目还款计划列表</a></li>
		<li class="active">
			<a href="${ctx}/wloanproject/wloanTermProjectPlan/form?id=${wloanTermProjectPlan.id}">项目还款计划查看
			<%-- <shiro:hasPermission name="wloanproject:wloanTermProjectPlan:edit">${not empty wloanTermProjectPlan.id?'修改':'添加'}</shiro:hasPermission> --%>
			</a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="wloanTermProjectPlan" action="${ctx}/wloanproject/wloanTermProjectPlan/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">项目名称：</label>
				<div class="controls">
					<form:input path="wloanTermProject.name" htmlEscape="false" maxlength="32" class="input-xlarge required" style="width:250px"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">还款类型：</label>
				<div class="controls">
					<input type="text" value="${wloanTermProjectPlan.principal == '0' ? '利息' : '本息' }" style="width:250px"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">还款金额：</label>
				<div class="controls">
					<form:input path="interest" htmlEscape="false" class="input-xlarge  number required" style="width:250px"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">还款日期：</label>
				<div class="controls">
					<form:input path="repaymentDate" htmlEscape="false" class="input-xlarge  number required" style="width:250px"/>	
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="form-actions">
				<%-- <shiro:hasPermission name="wloanproject:wloanTermProjectPlan:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission> --%>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</div>	
		
	</form:form>
</body>
</html>