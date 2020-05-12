<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>抵用券管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(
			function() {
				//$("#name").focus();
				$("#inputForm").validate(
							{
								submitHandler : function(form) {
									loading('正在提交，请稍等...');
									form.submit();
								},
								errorContainer : "#messageBox",
								errorPlacement : function(error, element) {
									$("#messageBox").text("输入有误，请先更正。");
									if (element.is(":checkbox")
											|| element.is(":radio")
											|| element.parent().is(
													".input-append")) {
										error.appendTo(element.parent()
												.parent());
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
		<li><a href="${ctx}/activity/userRateCouponHistory/">加息券列表</a></li>
		<li class="active"><a href="${ctx}/activity/userRateCouponHistory/rechargeForm?id=${userRateCouponHistory.id}">加息券充值</a></li>
		<li><a href="${ctx}/activity/userRateCouponHistory/rechargeAllForm?id=${userRateCouponHistory.id}">加息券批量充值</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="userRateCouponHistory" action="${ctx}/activity/userRateCouponHistory/save" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">客户手机：</label>
			<div class="controls">
				<form:input path="userInfo.name" htmlEscape="false" maxlength="64" class="input-xlarge required" />
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">加息券(%)：</label>
			<div class="controls">
				<form:select path="awardId" class="input-medium">
					<c:forEach var="dic" items="${userRateCouponHistory.rateCouponDics}">
						<form:option value="${dic.id}" label="${dic.rate}%" />
					</c:forEach>
				</form:select>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="state" class="input-medium">
					<form:options items="${fns:getDictList('a_user_awards_history_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="activity:userRateCouponHistory:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="充 值" />&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>