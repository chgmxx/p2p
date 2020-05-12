<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
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
		<li><a href="${ctx}/userinfo/userInfo/">用户信息列表</a></li>
		<li class="active"><a href="${ctx}/userinfo/userInfo/form?id=${userInfo.id}">用户信息<shiro:hasPermission name="userinfo:userInfo:edit">${'查看'}</shiro:hasPermission><shiro:lacksPermission name="userinfo:userInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="userInfo" action="${ctx}/userinfo/userInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">姓名：</label>
				<div class="controls">
					<form:input path="realName" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true"/>
				</div>
			</div>
			<div class="span6 ">
				<label class="control-label">手机号码：</label>
				<div class="controls">
					<form:input path="name" htmlEscape="false" maxlength="32" class="input-xlarge required" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">邮箱：</label>
				<div class="controls">
					<form:input path="email" htmlEscape="false" maxlength="255" class="input-xlarge" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">证件号码：</label>
				<div class="controls">
					<form:input path="certificateNo" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">性别：</label>
				<div class="controls">
					<c:if test="${not empty userInfo.sex }">
						<c:forEach items="${fns:getDictList('sex')}" var="usersex">
							<c:if test="${userInfo.sex == usersex.value }">
								<form:input path="sex" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true" value="${usersex.label }"/>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${empty userInfo.sex || userInfo.sex ==''}">
						<input type="text" id="sex" name="sex" value="" readonly="readonly" class="input-xlarge">
					</c:if>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">状态：</label>
				<div class="controls">
					<c:if test="${not empty userInfo.state }">
						<c:forEach items="${fns:getDictList('user_state')}" var="userstate">
							<c:if test="${userInfo.state == userstate.value }">
								<form:input path="state" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true" value="${userstate.label }"/>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${empty userInfo.state || userInfo.state ==''}">
						<input type="text" id="state" name="state" value="" readonly="readonly" class="input-xlarge">
					</c:if>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">用户类型：</label>
				<div class="controls">
					<c:if test="${not empty userInfo.userType }">
						<c:forEach items="${fns:getDictList('user_type')}" var="usertypes">
							<c:if test="${userInfo.userType == usertypes.value }">
								<form:input path="userType" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true" value="${usertypes.label }"/>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${empty userInfo.userType || userInfo.userType ==''}">
						<input type="text" id="userType" name="userType" value="" readonly="readonly" class="input-xlarge">
					</c:if>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">注册来源：</label>
				<div class="controls">
					<c:if test="${not empty userInfo.registerFrom }">
						<c:forEach items="${fns:getDictList('user_register_from')}" var="userregisterfrom">
							<c:if test="${userInfo.registerFrom == userregisterfrom.value }">
								<form:input path="registerFrom" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true" value="${userregisterfrom.label }"/>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${empty userInfo.registerFrom || userInfo.registerFrom ==''}">
						<input type="text" id="registerFrom" name="registerFrom" value="" readonly="readonly" class="input-xlarge">
					</c:if>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">注册日期：</label>
				<div class="controls">
					<input name="registerDate" type="text" readonly="true" maxlength="20" class="input-xlarge"
						value="<fmt:formatDate value="${userInfo.registerDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">最后登录日期：</label>
				<div class="controls">
					<input name="lastLoginDate" type="text" readonly="true" maxlength="20" class="input-xlarge"
						value="<fmt:formatDate value="${userInfo.lastLoginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">推荐人ID：</label>
				<div class="controls">
					<form:input path="recommendUserId" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">最后登录IP地址：</label>
				<div class="controls">
					<form:input path="lastLoginIp" htmlEscape="false" maxlength="16" class="input-xlarge" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">从事行业：</label>
				<div class="controls">
					<form:input path="industry" htmlEscape="false" maxlength="255" class="input-xlarge" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">工作：</label>
				<div class="controls">
					<form:input path="job" htmlEscape="false" maxlength="255" class="input-xlarge" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">学历：</label>
				<div class="controls">
					<form:input path="degree" htmlEscape="false" maxlength="255" class="input-xlarge" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">账户ID：</label>
				<div class="controls">
					<form:input path="accountId" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">推荐类型：</label>
				<div class="controls">
					<c:if test="${not empty userInfo.recomType }">
						<c:forEach items="${fns:getDictList('recom_type')}" var="recometype">
							<c:if test="${userInfo.recomType == recometype.value }">
								<form:input path="recomType" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true" value="${recometype.label }"/>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${empty userInfo.recomType || userInfo.recomType==''}">
						<input type="text" id="recomType" name="recomType" value="" readonly="readonly" class="input-xlarge">
					</c:if>
				</div>
			</div>
			<div class="span6 ">
				<label class="control-label">是否绑定银行卡：</label>
				<div class="controls">
					<c:if test="${not empty userInfo.bindBankCardState }">
						<c:forEach items="${fns:getDictList('bind_bank_card_state')}" var="bindbank">
							<c:if test="${userInfo.bindBankCardState == bindbank.value }">
								<form:input path="bindBankCardState" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true" value="${bindbank.label }"/>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${empty userInfo.bindBankCardState || userInfo.bindBankCardState ==''}">
						<input type="text" id="bindBankCardState" name="bindBankCardState" value="" readonly="readonly" class="input-xlarge">
					</c:if>
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