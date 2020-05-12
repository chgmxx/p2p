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
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/bank/userBankCard/">客户银行卡列表</a></li>
		<li class="active"><a href="${ctx}/bank/userBankCard/viewForm?id=${userBankCard.id}">客户银行卡展示</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="userBankCard" action="#" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<%-- <sys:message content="客户银行卡展示" /> --%>
		<div class="control-group">
			<label class="control-label">银行卡号码：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCard.bankAccountNo}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCard.bankAccountNo}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户账号：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCard.userId}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCard.userId}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户账户：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCard.accountId}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCard.accountId}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">连连绑定号：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCard.userInfo.llagreeNo}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCard.userInfo.llagreeNo}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">银行代码：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCard.bankNo}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${userBankCard.bankNo}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">绑卡时间：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCard.bindDate}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline"><fmt:formatDate value="${userBankCard.bindDate}" pattern="yyyy-MM-dd HH:mm:ss" /></span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCard.state}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${fns:getDictLabel(userBankCard.state, 'bank_card_state', '')}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否默认银行卡：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty userBankCard.isDefault}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${fns:getDictLabel(userBankCard.isDefault, 'bank_card_is_default', '')}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>