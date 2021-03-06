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
		<c:if test="${ usertype == '8' }">
		<li class="active"><a href="${ctx}/riskmanagement/riskManagementMessage/form?id=${riskManagement.id}">风控企业信息<shiro:hasPermission name="riskmanagement:riskManagementMessage:edit">${not empty riskManagement.id?'审批':'添加'}</shiro:hasPermission><shiro:lacksPermission name="riskmanagement:riskManagementMessage:edit">查看</shiro:lacksPermission></a></li>
	    </c:if>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="riskManagement" action="${ctx}/riskmanagement/riskManagementMessage/pass" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">企业名称：</label>
			<div class="controls">
				<form:input path="companyName" htmlEscape="false" maxlength="255" class="input-xlarge "/>
			</div>
		</div>
		 
			<div class="control-group">
				<label class="control-label">风控专员：</label>
				<div class="controls">
					<form:input path="checkUser1" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true"/>
				</div>
			</div>
			<div class="control-group">
			<label class="control-label">审批时间：</label>
			<div class="controls">
				<input name="checkDate1" type="text" readonly="true" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${riskManagement.checkDate1}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					/>
			</div>
		    </div>
			<div class="control-group">
				<label class="control-label">审批意见：</label>
				<div class="controls">
					<form:input path="checkNote1" htmlEscape="false" maxlength="255" class="input-xlarge " readonly="true"/>
				</div>
			</div>
			
		
		
			<div class="control-group">
				<label class="control-label">风控经理：</label>
				<div class="controls">
					<form:input path="checkUser2" htmlEscape="false" maxlength="64" class="input-xlarge "  readonly="true"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">审批时间：</label>
				<div class="controls">
				   <input name="checkDate2" type="text" readonly="true" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${riskManagement.checkDate2}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">审批意见：</label>
				<div class="controls">
					<form:input path="checkNote2" htmlEscape="false" maxlength="64" class="input-xlarge "  readonly="true"/>
				</div>
			</div>
		

        
	        <div class="control-group">
				<label class="control-label">总经理：</label>
				<div class="controls">
					<form:input path="checkUser3" htmlEscape="false" maxlength="64" class="input-xlarge "  readonly="true"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">审批时间：</label>
				<div class="controls">
					<input name="checkDate3" type="text" readonly="true" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${riskManagement.checkDate3}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					/>
				</div>
			</div>

	   <div class="control-group">
				<label class="control-label">审批意见：</label>
				<div class="controls">
					<form:input path="checkNote3" htmlEscape="false" maxlength="255" class="input-xlarge "  readonly="true"/>
				</div>
	   </div>
       <c:if test="${usertype == '5' }">
       	   <div class="control-group">
				<label class="control-label">网站查询：</label>
				<div class="controls">
					<a href="#">房屋价值评估</a>&nbsp;|&nbsp;<a href="#">企业情况查询</a>
		       </div>
	      </div>
       </c:if>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>