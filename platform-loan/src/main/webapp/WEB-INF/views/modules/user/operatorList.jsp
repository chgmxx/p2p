<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>操作人管理</title>
<meta name="decorator" content="default" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
<style>

.table-condensed th, .table-condensed td {
  padding: 12px 5px!important;
        text-align: center!important;
}
 
.breadcrumb{
    padding: 0!important;
}

.padding_wrap{
 padding:8px 15px;
}
</style>
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
	<div class="nav_head">
	操作人管理 <a href="${ctx}/credit/cuoperator/creditUserOperator/form?creditUserId=${id}"><span class="pull-right">添加操作人</span></a>
	</div>
	<form:form id="searchForm" modelAttribute="creditUserOperator" action="${ctx}/sys/user/supplier?middlemenId=${id}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
	</form:form>
	<sys:message content="${message}" />
	<div class="padding_wrap">
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号</th>
				<th>姓名</th>
				<th>账户状态</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="creditUserOperator">
				<tr>
					<td>${creditUserOperator.phone}</td>
					<td>${creditUserOperator.name}</td>
					<c:if test="${creditUserOperator.state == 0}">
					  <td>不可用</td>
					</c:if>
					<c:if test="${creditUserOperator.state == 1}">
					  <td>可用</td>
					</c:if>
					<td><a href="${ctx}/credit/cuoperator/creditUserOperator/update?id=${creditUserOperator.id}">修改</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	</div>
</body>
</html>