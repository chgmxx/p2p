<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Token</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
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
		<li class="active">
			<shiro:lacksPermission name="wechat:token:view">查看</shiro:lacksPermission>
		</li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="account" action="${ctx}/wechat/cms/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group" >
			<div class="span6"  style="width:1000px">
				<label class="control-label">公众号Id：</label>
				<div class="controls">
					<form:input path="account" htmlEscape="false" maxlength="100" class="input-xxlarge required"  />
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		<div class="control-group">
				<div class="span6" style="width:1000px">
					<label class="control-label">URL：</label>
					<div class="controls">
						<form:input path="url" htmlEscape="false" maxlength="100" class="input-xxlarge required" readonly="true" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
		</div>
		<div class="control-group">
				<div class="span6" style="width:1000px">
					<label class="control-label">Token：</label>
					<div class="controls">
						<form:input path="token" htmlEscape="false" maxlength="100" class="input-xxlarge" readonly="true" />
					</div>
				</div>
		</div>
		<div class="control-group">
				<div class="span6" style="width:1000px">
					<label class="control-label">AppId：</label>
					<div class="controls">
						<form:input path="appid" htmlEscape="false" maxlength="100" class="input-xxlarge required"  />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
		</div>
		<div class="control-group">
				<div class="span6" style="width:1000px">
					<label class="control-label">AppSecret：</label>
					<div class="controls">
						<form:input path="appsecret" htmlEscape="false" maxlength="100" class="input-xxlarge required"  />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
		</div>
		<div class="control-group">
				<div class="span6" style="width:500px">
					<label class="control-label">消息条数：</label>
					<div class="controls">
					<form:select path="msgcount" class="input-large" style="width:100px" >
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('token_msg_count')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<span class="help-inline"><label>回复图文消息条数</label></span>
					</div>
				</div>
		</div>
		 
		<div class="form-actions">
			<shiro:hasPermission name="pro:wguaranteecompany:view">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

</body>
</html>