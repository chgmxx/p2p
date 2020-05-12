<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>借代中间表管理</title>
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
		<li class="active"><a href="${ctx}/credit/creditSupplierToMiddlemen/">借代关系</a></li>
		<!-- 
		<shiro:hasPermission name="credit:creditSupplierToMiddlemen:edit"><li><a href="${ctx}/credit/creditSupplierToMiddlemen/form">借代添加</a></li></shiro:hasPermission>
		 -->
	</ul>
	<form:form id="searchForm" modelAttribute="creditSupplierToMiddlemen" action="${ctx}/credit/creditSupplierToMiddlemen/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label class="label">借款户：</label>
				<form:input path="supplierUser.enterpriseFullName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label class="label">代偿户：</label>
				<form:input path="middlemenUser.enterpriseFullName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>借款户</th>
				<th>代偿户</th>
				<th>申请时间</th>
				<th>更新时间</th>
				<th>备注</th>
				<!-- 
				<shiro:hasPermission name="credit:creditSupplierToMiddlemen:edit"><th>操作</th></shiro:hasPermission>
				 -->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditSupplierToMiddlemen">
			<tr>
				<td><a href="${ctx}/credit/creditSupplierToMiddlemen/form?id=${creditSupplierToMiddlemen.id}">
					${creditSupplierToMiddlemen.supplierUser.enterpriseFullName}
				</a></td>
				<td>
					${creditSupplierToMiddlemen.middlemenUser.enterpriseFullName}
				</td>
				<td>
					<fmt:formatDate value="${creditSupplierToMiddlemen.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${creditSupplierToMiddlemen.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${creditSupplierToMiddlemen.remarks}
				</td>
				<!-- 
				<shiro:hasPermission name="credit:creditSupplierToMiddlemen:edit"><td>
    				<a href="${ctx}/credit/creditSupplierToMiddlemen/form?id=${creditSupplierToMiddlemen.id}">修改</a>
					<a href="${ctx}/credit/creditSupplierToMiddlemen/delete?id=${creditSupplierToMiddlemen.id}" onclick="return confirmx('确认要删除该借代中间表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
				 -->
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>