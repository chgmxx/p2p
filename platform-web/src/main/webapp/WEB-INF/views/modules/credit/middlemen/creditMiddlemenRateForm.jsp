<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>项目期限和利率管理</title>
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
	<style type="text/css">
	.select2-container{
	width: 300px;
	}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/credit/middlemen/creditMiddlemenRate/">项目期限和利率列表</a></li>
		<li class="active"><a href="${ctx}/credit/middlemen/creditMiddlemenRate/form?id=${creditMiddlemenRate.id}">项目期限和利率<shiro:hasPermission name="credit:middlemen:creditMiddlemenRate:edit">${not empty creditMiddlemenRate.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="credit:middlemen:creditMiddlemenRate:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="creditMiddlemenRate" action="${ctx}/credit/middlemen/creditMiddlemenRate/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">核心企业：</label>
			<div class="controls">
				<form:select path="creditUserId">
					<c:forEach var="middlemen" items="${middlemenList}">
						<form:option value="${middlemen.id}" label="${middlemen.enterpriseFullName}" />
					</c:forEach>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">项目期限：</label>
			<div class="controls">
				<form:input path="span" htmlEscape="false" maxlength="64" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">利率：</label>
			<div class="controls">
				<form:input path="rate" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">服务费率：</label>
			<div class="controls">
				<form:input path="serviceRate" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="credit:middlemen:creditMiddlemenRate:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>