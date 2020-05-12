<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户流水记录管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li>订单查询结果</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="userTransDetail" action="${ctx}/transdetail/userTransDetail/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">订单号：</label>
				<div class="controls">
					<form:input path="transId" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">交易类型：</label>
				<div class="controls">
					<form:input path="trustTypeStr" htmlEscape="false" maxlength="32" class="input-xlarge required" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">交易金额：</label>
				<div class="controls">
					<form:input path="amount" htmlEscape="false" class="input-xlarge required" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">交易状态：</label>
				<div class="controls">
				     <form:input path="stateStr" htmlEscape="false" class="input-xlarge required" readonly="true"/>
				</div>
			</div>
		</div>
		

		
		<div class="form-actions">
			<div class="span6 ">
				<label class="control-label"></label>
				<div class="controls">
				</div>
			</div>
			<div class="span6">
				<label class="control-label"></label>
				<div class="controls">
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>