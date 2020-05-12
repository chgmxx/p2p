<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>账户管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/useraccount/userAccountInfo/">客户账户列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="userAccountInfo" action="${ctx}/useraccount/userAccountInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号：</label>
				<form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>姓名：</label>
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label></label>
				<label></label>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机</th>
				<th>姓名</th>
				<th>账户总额</th>
				<th>可用金额</th>
				<th>提现金额</th>
				<th>充值总额</th>
				<th>冻结金额</th>
				<th>总收益</th>
				<th>定期投资总金额</th>
				<th>定期累计收益</th>
				<th>定期待收本金</th>
				<th>定期待收收益</th>
				<shiro:hasPermission name="useraccount:userAccountInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userAccountInfo">
			<tr>
				<td><a href="${ctx}/useraccount/userAccountInfo/form?id=${userAccountInfo.id}">
					${userAccountInfo.userInfo.name}
				</td>
				<td>
					${userAccountInfo.userInfo.realName}
				</td>
				<td>
					<fmt:formatNumber type="number" value="${userAccountInfo.totalAmount } " minFractionDigits="2" maxFractionDigits="2" />
				</a></td>
				<td>
					<fmt:formatNumber type="number" value="${userAccountInfo.availableAmount } " minFractionDigits="2" maxFractionDigits="2" />
				</td>
				<td>
					<fmt:formatNumber type="number" value="${userAccountInfo.cashAmount } " minFractionDigits="2" maxFractionDigits="2" />
				</td>
				<td>
					<fmt:formatNumber type="number" value="${userAccountInfo.rechargeAmount } " minFractionDigits="2" maxFractionDigits="2" />
				</td>
				<td>
					<fmt:formatNumber type="number" value="${userAccountInfo.freezeAmount } " minFractionDigits="2" maxFractionDigits="2" />
				</td>
				<td>
					<fmt:formatNumber type="number" value="${userAccountInfo.totalInterest } " minFractionDigits="2" maxFractionDigits="2" />
				</td>
				<td>
					<fmt:formatNumber type="number" value="${userAccountInfo.regularTotalAmount } " minFractionDigits="2" maxFractionDigits="2" />
				</td>
				<td>
					<fmt:formatNumber type="number" value="${userAccountInfo.regularTotalInterest } " minFractionDigits="2" maxFractionDigits="2" />
				</td>
				<td>
					<fmt:formatNumber type="number" value="${userAccountInfo.regularDuePrincipal } " minFractionDigits="2" maxFractionDigits="2" />
				</td>
				<td>
					<fmt:formatNumber type="number" value="${userAccountInfo.regularDueInterest } " minFractionDigits="2" maxFractionDigits="2" />
				</td>
				<shiro:hasPermission name="useraccount:userAccountInfo:edit"><td>
    				<a href="${ctx}/useraccount/userAccountInfo/form?id=${userAccountInfo.id}">修改</a>
					<a href="${ctx}/useraccount/userAccountInfo/delete?id=${userAccountInfo.id}" onclick="return confirmx('确认要删除该账户管理吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>