<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>投资用户剩余资金信息管理</title>
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
		<li class="active"><a href="${ctx}/current/wloanCurrentMomentInvest/">投资用户剩余资金信息列表</a></li>
		<shiro:hasPermission name="current:wloanCurrentMomentInvest:edit"><li><a href="${ctx}/current/wloanCurrentMomentInvest/form">投资用户剩余资金信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanCurrentMomentInvest" action="${ctx}/current/wloanCurrentMomentInvest/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户姓名</th>
				<th>金额</th>
				<th>状态</th>
				<th>抵用券金额</th>
				<th>创建日期</th>
				<th>更改日期</th>
				<shiro:hasPermission name="current:wloanCurrentMomentInvest:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wloanCurrentMomentInvest">
			<tr>
				<td><a href="${ctx}/current/wloanCurrentMomentInvest/form?id=${wloanCurrentMomentInvest.id}">
					${wloanCurrentMomentInvest.userInfo.realName}
				</a></td>
				<td>
					${wloanCurrentMomentInvest.amount}
				</td>
				<td>
					未投资
				</td>
				<td>
					${wloanCurrentMomentInvest.voucherAmount}
				</td>
				<td>
					<fmt:formatDate value="${wloanCurrentMomentInvest.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${wloanCurrentMomentInvest.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="current:wloanCurrentMomentInvest:edit"><td>
    				<a href="${ctx}/current/wloanCurrentMomentInvest/form?id=${wloanCurrentMomentInvest.id}">修改</a>
					<a href="${ctx}/current/wloanCurrentMomentInvest/delete?id=${wloanCurrentMomentInvest.id}" onclick="return confirmx('确认要删除该投资用户剩余资金信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>