<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>定期融资档案管理</title>
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
		<li class="active"><a href="${ctx}/wloan_term_doc/wloanTermDoc/">定期融资档案列表</a></li>
		<shiro:hasPermission name="wloan_term_doc:wloanTermDoc:edit">
			<li><a href="${ctx}/wloan_term_doc/wloanTermDoc/addForm">定期融资档案添加</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermDoc" action="${ctx}/wloan_term_doc/wloanTermDoc/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label>档案名称：</label> <form:input path="name" htmlEscape="false" maxlength="255" class="input-medium" /></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>档案名称</th>
				<th>备注信息</th>
				<shiro:hasPermission name="wloan_term_doc:wloanTermDoc:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanTermDoc">
				<tr>
					<td><a href="${ctx}/wloan_term_doc/wloanTermDoc/viewForm?id=${wloanTermDoc.id}"> ${wloanTermDoc.name} </a></td>
					<td>${wloanTermDoc.remarks}</td>
					<shiro:hasPermission name="wloan_term_doc:wloanTermDoc:edit">
						<td><a href="${ctx}/wloan_term_doc/wloanTermDoc/manageForm?id=${wloanTermDoc.id}">档案管理</a> <a href="${ctx}/wloan_term_doc/wloanTermDoc/updateForm?id=${wloanTermDoc.id}">修改</a> <a href="${ctx}/wloan_term_doc/wloanTermDoc/delete?id=${wloanTermDoc.id}" onclick="return confirmx('确认要删除该定期融资档案吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>