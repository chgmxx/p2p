<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>客户银行卡更换管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		//$("#name").focus();
		$("#inputForm").validate({
			submitHandler : function(form) {
				loading('正在提交，请稍等...');
				form.submit();
			},
			errorContainer : "#messageBox",
			errorPlacement : function(error, element) {
				$("#messageBox").text("输入有误，请先更正。");
				if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
					error.appendTo(element.parent().parent());
				} else {
					error.insertAfter(element);
				}
			}
		});
	});
</script>
<style type="text/css">
.controls img {
	width: 500px;
}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/bank/userBankCardHistory/">客户银行卡更换列表</a></li>
		<li class="active"><a href="${ctx}/bank/userBankCardHistory/viewForm?id=${userBankCardHistory.id}">客户更换银行卡信息展示</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="userBankCardHistory" action="#" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<%-- <sys:message content="客户更换银行卡信息展示" /> --%>
		<div class="control-group">
			<label class="control-label">客户ID：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCardHistory.userId}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCardHistory.userId}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户姓名：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCardHistory.realName}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCardHistory.realName}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户移动电话：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCardHistory.mobilePhone}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCardHistory.mobilePhone}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">身份证号码：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCardHistory.identityCardNo}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCardHistory.identityCardNo}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">旧银行卡号码：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCardHistory.oldBankCardNo}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCardHistory.oldBankCardNo}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">新银行卡号码：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCardHistory.newBankCardNo}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCardHistory.newBankCardNo}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">新银行卡号码：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCardHistory.replaceDate}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline"><fmt:formatDate value="${userBankCardHistory.replaceDate}" pattern="yyyy-MM-dd HH:mm:ss" /></span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCardHistory.state}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${fns:getDictLabel(userBankCardHistory.state, 'replace_identity_card_state', '')}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注信息：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCardHistory.remarks}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCardHistory.remarks}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">身份证正面照片：</label>
			<div class="controls">
				<%-- <form:input path="identityCardForwardSidePicUrl" htmlEscape="false" maxlength="64" class="input-xlarge " readonly='true' /> --%>
				<img alt="身份证正面照片" src="${baseUrl}/${userBankCardHistory.identityCardForwardSidePicUrl}">
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">身份证反面照片：</label>
			<div class="controls">
				<%-- <form:input path="identityCardBackSidePicUrl" htmlEscape="false" maxlength="64" class="input-xlarge " readonly='true' /> --%>
				<img alt="身份证反面照片" src="${baseUrl}/${userBankCardHistory.identityCardBackSidePicUrl}">
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">本人手持身份证照片：</label>
			<div class="controls">
				<%-- <form:input path="identityCardBackSidePicUrl" htmlEscape="false" maxlength="64" class="input-xlarge " readonly='true' /> --%>
				<img alt="本人手持身份证照片" src="${baseUrl}/${userBankCardHistory.identityCardAndPersonPicUrl}">
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>