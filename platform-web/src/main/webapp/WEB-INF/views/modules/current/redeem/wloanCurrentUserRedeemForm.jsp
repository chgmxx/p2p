<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>活期赎回管理</title>
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
		<li><a href="${ctx}/redeem/wloanCurrentUserRedeem/">活期赎回列表</a></li>
		<li class="active"><a href="${ctx}/redeem/wloanCurrentUserRedeem/form?id=${wloanCurrentUserRedeem.id}">活期赎回<shiro:hasPermission name="redeem:wloanCurrentUserRedeem:edit">${not empty wloanCurrentUserRedeem.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="redeem:wloanCurrentUserRedeem:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="wloanCurrentUserRedeem" action="${ctx}/redeem/wloanCurrentUserRedeem/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
	<!-- <div class="control-group">
			<label class="control-label">项目：</label>
			<div class="controls">
				<form:input path="wloanCurrentProject.name" htmlEscape="false" maxlength="32" class="input-xlarge required" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div> -->
		<div class="control-group">
			<label class="control-label">转到用户：</label>
			<div class="controls">
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="32" class="input-xlarge required" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">转让用户：</label>
			<div class="controls">
				<form:input path="userInfo1.realName" htmlEscape="false" maxlength="32" class="input-xlarge required" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">转让金额：</label>
			<div class="controls">
				<form:input path="amount" htmlEscape="false" class="input-xlarge required" readonly="true" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">转让日期：</label>
			<div class="controls">
				<input name="redeemDate" type="text" readonly="true" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${wloanCurrentUserRedeem.redeemDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>