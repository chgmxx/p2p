<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>ZTMG合作方信息管理</title>
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
		<li><a href="${ctx}/partner/ztmgPartnerPlatform/">【中投摩根】合作方信息列表</a></li>
		<li class="active"><a href="${ctx}/partner/ztmgPartnerPlatform/viewForm?id=${ztmgPartnerPlatform.id}">【中投摩根】合作方信息查看</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ztmgPartnerPlatform" action="${ctx}/partner/ztmgPartnerPlatform/updateSave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<div class="span7">
				<label class="control-label">平台类型：</label>
				<div class="controls">
					<form:input path="platformName" value="${fns:getDictLabel(ztmgPartnerPlatform.platformType, 'partner_platform_type', '')}" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span7">
				<label class="control-label">平台名称：</label>
				<div class="controls">
					<form:input path="platformName" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
			<div class="span7">
				<label class="control-label">平台编码：</label>
				<div class="controls">
					<form:input path="platformCode" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span7">
				<label class="control-label">联系人电话：</label>
				<div class="controls">
					<form:input path="phone" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
			<div class="span7">
				<label class="control-label">联系人姓名：</label>
				<div class="controls">
					<form:input path="name" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span7">
				<label class="control-label">电子邮箱：</label>
				<div class="controls">
					<form:input path="email" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true" />
				</div>
			</div>
			<div class="span7">
				<label class="control-label">合作方所在地：</label>
				<div class="controls">
					<form:input path="area.name" htmlEscape="false" maxlength="50" class="input-large" readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span7">
				<label class="control-label">利率：</label>
				<div class="controls">
					<form:input path="rate" htmlEscape="false" maxlength="10" class="input-xlarge " />
				</div>
			</div>
			<div class="span7">
				<label class="control-label">有效用户返利：</label>
				<div class="controls">
					<form:input path="money" htmlEscape="false" maxlength="10" class="input-xlarge " />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span9">
				<label class="control-label">备注信息：</label>
				<div class="controls">
					<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge " readonly="true" />
				</div>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>