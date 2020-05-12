<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>活期融资资金池管理</title>
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
		<li class="active"><a href="${ctx}/current/pool/wloanCurrentPool/">活期融资资金池列表</a></li>
		<shiro:hasPermission name="current:pool:wloanCurrentPool:edit"><li><a href="${ctx}/current/pool/wloanCurrentPool/form">活期融资资金池添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanCurrentPool" action="${ctx}/current/pool/wloanCurrentPool/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>产品名称</th>
				<th>总金额(元)</th>
				<th>资金池剩余金额(元)</th>
				<th>年化收益(%)</th>
				<th>最小金额(元)</th>
				<th>最大金额(元)</th>
				<th>递增金额(元)</th>
				<th>修改日期</th>
				<th>备注</th>
				<shiro:hasPermission name="current:pool:wloanCurrentPool:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wloanCurrentPool">
			<tr>
				<td><a href="${ctx}/current/pool/wloanCurrentPool/form?id=${wloanCurrentPool.id}">
					${wloanCurrentPool.name}
				</a></td>
				<td>
					<fmt:formatNumber value="${wloanCurrentPool.amount }" pattern="###########0.00" />
				</td>
				<td>
					<fmt:formatNumber value="${wloanCurrentPool.surplusAmount == null ? '0.00' : wloanCurrentPool.surplusAmount }" pattern="###########0.00" />
				</td>
				<td>
					${wloanCurrentPool.annualRate}
				</td>
				<td>
					${wloanCurrentPool.minAmount}
				</td>
				<td>
					${wloanCurrentPool.maxAmount}
				</td>
				<td>
					${wloanCurrentPool.stepAmount}
				</td>
				<td>
					<fmt:formatDate value="${wloanCurrentPool.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${wloanCurrentPool.remark}
				</td>
				<shiro:hasPermission name="current:pool:wloanCurrentPool:edit"><td>
    				<a href="${ctx}/current/pool/wloanCurrentPool/form?id=${wloanCurrentPool.id}">修改</a>
					<a href="${ctx}/current/pool/wloanCurrentPool/delete?id=${wloanCurrentPool.id}" onclick="return confirmx('确认要删除该活期融资资金池吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>