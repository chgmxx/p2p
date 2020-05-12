<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>问卷管理</title>
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
		<li class="active"><a href="${ctx}/questionnaire/">问卷列表</a></li>
		<shiro:hasPermission name="questionnaire:edit">
			<li><a href="${ctx}/questionnaire/form">问卷添加</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="questionnaire" action="${ctx}/questionnaire/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label>试卷名称：</label> <form:input path="name" htmlEscape="false" maxlength="128" class="input-medium" /></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>试卷名称</th>
				<th>状态</th>
				<th>修改时间</th>
				<shiro:hasPermission name="questionnaire:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="questionnaire">
				<tr>
					<td><a href="${ctx}/questionnaire/form?id=${questionnaire.id}"> ${questionnaire.name} </a></td>
					<td>
						<c:if test="${questionnaire.state == '1'}">
							<b style="color: green;">可用</b>
						</c:if>
						<c:if test="${questionnaire.state == '2'}">
							<b style="color:red;">不可用</b>
						</c:if>
					</td>
					<td><fmt:formatDate value="${questionnaire.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="questionnaire:edit">
						<td>
						<a href="${ctx}/questionnaire/topic/assign?id=${questionnaire.id}">分配</a>
						<a href="${ctx}/questionnaire/form?id=${questionnaire.id}">修改</a>
						<a href="${ctx}/questionnaire/delete?id=${questionnaire.id}" onclick="return confirmx('确认要删除该问卷吗？', this.href)">删除</a>
						</td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>