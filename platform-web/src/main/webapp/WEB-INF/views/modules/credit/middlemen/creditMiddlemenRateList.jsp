<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>核心企业利率管理</title>
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
	<style type="text/css">
	.select2-container{
	width: 300px;
	}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/credit/middlemen/creditMiddlemenRate/">核心企业利率列表</a></li>
		<shiro:hasPermission name="credit:middlemen:creditMiddlemenRate:edit"><li><a href="${ctx}/credit/middlemen/creditMiddlemenRate/form">核心企业利率添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="creditMiddlemenRate" action="${ctx}/credit/middlemen/creditMiddlemenRate/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>核心企业：</label>
				<form:select path="creditUserId">
					<c:forEach var="middlemen" items="${middlemenList}">
						<form:option value="${middlemen.id}" label="${middlemen.enterpriseFullName}" />
					</c:forEach>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>核心企业名称</th>
				<th>项目期限</th>
				<th>利率</th>
				<th>服务费率</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<shiro:hasPermission name="credit:middlemen:creditMiddlemenRate:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditMiddlemenRate">
			<tr>
				<td><a href="${ctx}/credit/middlemen/creditMiddlemenRate/form?id=${creditMiddlemenRate.id}">
					${creditMiddlemenRate.userInfo.enterpriseFullName}
				</a></td>
				<td>
					${creditMiddlemenRate.span}天
				</td>
				<td>
					${creditMiddlemenRate.rate}%
				</td>
				<td>
				    ${creditMiddlemenRate.serviceRate}%
				</td>
				<td>
					<fmt:formatDate value="${creditMiddlemenRate.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${creditMiddlemenRate.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="credit:middlemen:creditMiddlemenRate:edit"><td>
    				<a href="${ctx}/credit/middlemen/creditMiddlemenRate/form?id=${creditMiddlemenRate.id}">修改</a>
					<a href="${ctx}/credit/middlemen/creditMiddlemenRate/delete?id=${creditMiddlemenRate.id}" onclick="return confirmx('确认要删除该项目期限和利率吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>