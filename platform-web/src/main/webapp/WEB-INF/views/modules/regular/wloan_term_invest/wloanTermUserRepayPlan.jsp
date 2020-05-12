<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>投资记录管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {

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
		<li><a href="${ctx}/wloan_term_invest/wloanTermInvest/">投资记录列表</a></li>
		<li class="active"><a href="">还款计划查看</a></li>
	</ul>
	<input id="btnCancel" class="btn btn-primary" type="button" value="返 回" onclick="history.go(-1)"/>
	<form:form id="searchForm" modelAttribute="wloanTermUserPlan" action="${ctx}/wloan_term_invest/wloanTermInvest/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>投资人</th>
				<th>项目名称</th>
				<th>还款金额(RMB)</th>
				<th>还款日期</th>
				<th>状态</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanTermUserPlan">
				<tr>
					<td>${wloanTermUserPlan.userInfo.realName}</td>
					<td>${wloanTermUserPlan.wloanTermProject.name}</td>
					<td>${wloanTermUserPlan.interest}</td>
					<td><fmt:formatDate value="${wloanTermUserPlan.repaymentDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${wloanTermUserPlan.state == '3' ? '已还款' : '待还款'}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>