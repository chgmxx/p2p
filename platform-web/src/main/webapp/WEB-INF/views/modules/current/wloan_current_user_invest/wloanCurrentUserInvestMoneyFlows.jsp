<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>活期客户投资管理</title>
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
		<li class="active"><a href="#">客户资金流向</a></li>
		<li><a href="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/">活期客户投资列表</a></li>
	</ul>
	<span class="help-inline">活期项目真实投资记录列表</span>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>投资人手机</th>
				<th>投资人姓名</th>
				<th>项目名称</th>
				<th>金额(RMB)</th>
				<th>抵用券金额(RMB)</th>
				<th>投资日期</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${projectInvests.list}" var="a">
				<tr>
					<td>${a.userInfo.name}</td>
					<td>${a.userInfo.realName}</td>
					<td>${a.currentProjectInfo.name}</td>
					<td>${a.amount}</td>
					<td>${a.vouvherAmount}</td>
					<td><fmt:formatDate value="${a.bidDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<span class="help-inline">活期项目客户投资剩余资金临时存储列表</span>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>投资人手机</th>
				<th>投资人姓名</th>
				<th>金额(RMB)</th>
				<th>抵用券金额(RMB)</th>
				<th>状态</th>
				<th>创建日期</th>
				<th>更改日期</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${momentInvests.list}" var="b">
				<tr>
					<td>${b.userInfo.name}</td>
					<td>${b.userInfo.realName}</td>
					<td>${b.amount}</td>
					<td>${b.voucherAmount}</td>
					<td><c:if test="${b.state == 1}">
							待投资
						</c:if> <c:if test="${b.state == 2}">
							已投资
						</c:if></td>
					<td><fmt:formatDate value="${b.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${b.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>