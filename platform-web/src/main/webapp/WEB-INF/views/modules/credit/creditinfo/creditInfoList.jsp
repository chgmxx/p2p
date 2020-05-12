<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>借款资料管理</title>
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
		<li class="active"><a href="${ctx}/credit/creditinfo/creditInfo/">借款资料列表</a></li>
		<shiro:hasPermission name="credit:creditinfo:creditInfo:edit"><li><a href="${ctx}/credit/creditinfo/creditInfo/form">借款资料添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="creditInfo" action="${ctx}/credit/creditinfo/creditInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>资料名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>资料名称</th>
				<th>修改时间</th>
				<shiro:hasPermission name="credit:creditinfo:creditInfo:edit"><th>清单</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditInfo">
			<tr>
				<td><a href="${ctx}/credit/creditinfo/creditInfo/form?id=${creditInfo.id}">
					${creditInfo.name}
				</a></td>
				<td>
					<fmt:formatDate value="${creditInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="credit:creditinfo:creditInfo:edit"><td>
    				<a href="${ctx}/credit/annexFile/list?otherId=${creditInfo.id}&&remark=${creditInfo.name}">查看</a>
					<!-- <a href="${ctx}/credit/creditinfo/creditInfo/delete?id=${creditInfo.id}" onclick="return confirmx('确认要删除该借款资料吗？', this.href)">删除</a>-->
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<div class="form-actions">
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</body>
</html>