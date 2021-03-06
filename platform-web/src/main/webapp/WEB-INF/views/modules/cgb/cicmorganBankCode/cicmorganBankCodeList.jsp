<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>银行编码对照管理</title>
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
		<li class="active"><a href="${ctx}/cgb/cicmorganBankCode/">银行编码对照列表</a></li>
		<shiro:hasPermission name="cgb:cicmorganBankCode:edit">
			<li><a href="${ctx}/cgb/cicmorganBankCode/form">银行编码对照添加</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="cicmorganBankCode" action="${ctx}/cgb/cicmorganBankCode/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label>银行名称：</label> <form:input path="bankName" htmlEscape="false" maxlength="128" class="input-medium" /></li>
			<li><label>银行编码：</label> <form:input path="bankCode" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>银行名称</th>
				<th>银行编码</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<shiro:hasPermission name="cgb:cicmorganBankCode:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="cicmorganBankCode">
				<tr>
					<td><a href="${ctx}/cgb/cicmorganBankCode/form?id=${cicmorganBankCode.id}"> ${cicmorganBankCode.bankName} </a></td>
					<td>${cicmorganBankCode.bankCode}</td>
					<td><fmt:formatDate value="${cicmorganBankCode.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${cicmorganBankCode.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="cgb:cicmorganBankCode:edit">
						<td><a href="${ctx}/cgb/cicmorganBankCode/form?id=${cicmorganBankCode.id}">修改</a> <a href="${ctx}/cgb/cicmorganBankCode/delete?id=${cicmorganBankCode.id}" onclick="return confirmx('确认要删除该银行编码对照吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>