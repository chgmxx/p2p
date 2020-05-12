<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>答案管理</title>
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
		<li class="active"><a href="${ctx}/questionnaire/answer/">答案列表</a></li>
		<shiro:hasPermission name="questionnaire:answer:edit"><li><a href="${ctx}/questionnaire/answer/form">答案添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="answer" action="${ctx}/questionnaire/answer/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>答案：</label>
				<form:input path="name" htmlEscape="false" maxlength="256" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>答案</th>
				<th>分值(分)</th>
				<th>修改时间</th>
				<shiro:hasPermission name="questionnaire:answer:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="answer">
			<tr>
				<td><a href="${ctx}/questionnaire/answer/form?id=${answer.id}">
					${answer.name}
				</a></td>
				<td>${answer.score}</td>
				<td>
					<fmt:formatDate value="${answer.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="questionnaire:answer:edit"><td>
    				<a href="${ctx}/questionnaire/answer/form?id=${answer.id}">修改</a>
					<a href="${ctx}/questionnaire/answer/delete?id=${answer.id}" onclick="return confirmx('确认要删除该答案吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>