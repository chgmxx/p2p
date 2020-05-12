<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>银行托管-账户管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#messageBox").show();
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/credit/userAccount/">账户列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="creditUserAccount" action="${ctx}/credit/userAccount/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label class="label">帐号：</label> <form:input path="creditUserInfo.phone" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label class="label">姓名：</label> <form:input path="creditUserInfo.name" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label class="label">企业名称：</label> <form:input path="creditUserInfo.enterpriseFullName" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>帐号</th>
				<th>借款人</th>
				<th>账户类型</th>
				<th>企业名称</th>
				<th>账户总额</th>
				<th>可用余额</th>
				<th>冻结金额</th>
				<th>充值金额</th>
				<th>提现金额</th>
				<th>待还金额</th>
				<th>已还金额</th>
				<th>变更日期</th>
				<shiro:hasPermission name="credit:userAccount:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="creditUserAccount">
				<tr>
					<td>${creditUserAccount.creditUserInfo.phone}</td>
					<td>${creditUserAccount.creditUserInfo.enterpriseFullName}</td><!-- ${creditUserAccount.creditUserInfo.name} -->
					<td>${creditUserAccount.creditUserInfo.creditUserType}</td>
					<td>${creditUserAccount.creditUserInfo.enterpriseFullName}</td>
					<td>
						<fmt:formatNumber type="number" value="${creditUserAccount.totalAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${creditUserAccount.availableAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${creditUserAccount.freezeAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${creditUserAccount.rechargeAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${creditUserAccount.withdrawAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${creditUserAccount.surplusAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${creditUserAccount.repayAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td><fmt:formatDate value="${creditUserAccount.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="credit:userAccount:edit">
						<td><a href="${ctx}/credit/userAccount/checkAmount?userId=${creditUserAccount.creditUserInfo.id}">存管宝查询</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>