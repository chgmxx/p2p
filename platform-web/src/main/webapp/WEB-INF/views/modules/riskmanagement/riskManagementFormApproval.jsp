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
			
			// 拒绝按钮事件
			$("#refuseSubmit").click(function(){
				alert("refuse");
				$("#inputForm").attr("action","${ctx}/riskmanagement/riskManagementMessage/refuse");
				$("#inputForm").submit();
			});
			
		});
		

		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/riskmanagement/riskManagementMessage/">风控企业信息列表</a></li>
		<li class="active"><a href="${ctx}/riskmanagement/riskManagementMessage/form?id=${riskManagement.id}">风控企业信息<shiro:hasPermission name="riskmanagement:riskManagementMessage:edit">${not empty riskManagement.id?'申请':'添加'}</shiro:hasPermission><shiro:lacksPermission name="riskmanagement:riskManagementMessage:edit">查看</shiro:lacksPermission></a></li>
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
				<label class="control-label">一级审批人：</label>
				<div class="controls">
					<form:input path="checkUser1" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true"/>
				</div>
			</div>
			<div class="control-group">
			<label class="control-label">一级审批时间：</label>
			<div class="controls">
				<input name="checkDate1" type="text" readonly="true" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${riskManagement.checkDate1}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					/>
			</div>
		    </div>
			<div class="control-group">
				<label class="control-label">一级审批人意见：</label>
				<div class="controls">
					<form:input path="checkNote1" htmlEscape="false" maxlength="255" class="input-xlarge " readonly="true"/>
				</div>
			</div>
			
		
		
			<div class="control-group">
				<label class="control-label">二级审批人：</label>
				<div class="controls">
					<form:input path="checkUser2" htmlEscape="false" maxlength="64" class="input-xlarge "/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">二级审批时间：</label>
				<div class="controls">
				   <input name="checkDate2" type="text" readonly="true" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${riskManagement.checkDate2}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">二级审批人意见：</label>
				<div class="controls">
					<form:input path="checkNote2" htmlEscape="false" maxlength="64" class="input-xlarge "/>
				</div>
			</div>
		

        
	        <div class="control-group">
				<label class="control-label">三级审批人：</label>
				<div class="controls">
					<form:input path="checkUser3" htmlEscape="false" maxlength="64" class="input-xlarge "/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">三级审批时间：</label>
				<div class="controls">
					<input name="checkDate3" type="text" readonly="true" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${riskManagement.checkDate3}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">三级审批人意见：</label>
				<div class="controls">
					<form:input path="checkNote3" htmlEscape="false" maxlength="64" class="input-xlarge "/>
				</div>
			</div>
			

	   <div class="control-group">
				<label class="control-label">审批意见：</label>
				<div class="controls">
					<form:input path="checkNote" htmlEscape="false" maxlength="255" class="input-xlarge "/>
				</div>
	   </div>

		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提交申请"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>