<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文件上传</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#value").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					if($("#type").val()==''){
						alert('请选择类型');
					}
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
		<li class="active"><a href="#">文件上传</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="annexFile" action="${ctx}/sys/annexfile/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="otherId" id="otherId"/>
		<form:hidden path="returnUrl"/>
		<div class="control-group">
			<label class="control-label">企业名称：</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="80" class="required" readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型：</label>
			<div class="controls">
				<form:select path="type" class="input-medium required" id="type">
					<form:options items="${fns:getDictList(annexFile.dictType)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">上传文件：</label>
			<div class="controls">
				<form:hidden id="url" path="url" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="url" type="images" uploadPath="/photo" selectMultiple="true" maxWidth="100" maxHeight="100"/>
			</div>
		</div>
		<div class="form-actions">
			<c:if test="${empty annexFile.id}">
				<input id="btnSubmit" class="btn btn-primary" type="submit"  value="保 存"/>&nbsp;
			</c:if>
			<c:if test="${not empty annexFile.id}">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>