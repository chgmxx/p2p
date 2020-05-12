<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>用户关系管理</title>
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
		<li class="active"><a href="${ctx}/levelDistribution/list">用户关系列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="levelDistribution" action="${ctx}/levelDistribution/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />

		<ul class="ul-form">
			<li><label>推荐人：</label> <form:input path="parentUserInfo.name" htmlEscape="false" maxlength="32" class="input-medium" /></li>
			<li><label>被推荐人：</label> <form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-medium" /></li>
			<li class="btns"><label></label><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>推荐人手机</th>
				<th>推荐人姓名</th>
				<th>被推荐人手机</th>
				<th>被推荐人姓名</th>
				<th>创建时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="levelDistribution">
				<tr>
					<td>${levelDistribution.parentUserInfo.name}</td>
					<td>${levelDistribution.parentUserInfo.realName}</td>
					<td>${levelDistribution.userInfo.name}</td>
					<td>${levelDistribution.userInfo.realName}</td>
					<td><fmt:formatDate value="${levelDistribution.createDate }" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>