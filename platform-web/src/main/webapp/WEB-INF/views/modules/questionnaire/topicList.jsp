<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>题目管理</title>
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
		<li class="active"><a href="${ctx}/questionnaire/topic/">题目列表</a></li>
		<shiro:hasPermission name="topic:edit"><li><a href="${ctx}/questionnaire/topic/form">题目添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="topic" action="${ctx}/questionnaire/topic/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>题目名称：</label>
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
				<th>题目名称</th>
				<th>修改时间</th>
				<shiro:hasPermission name="topic:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="topic">
			<tr>
				<td><a href="${ctx}/questionnaire/topic/form?id=${topic.id}">
					${topic.name}
				</a></td>
				<td>
					<fmt:formatDate value="${topic.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="topic:edit"><td>
					<a href="${ctx}/questionnaire/answer/assign?id=${topic.id}">分配</a>
    				<a href="${ctx}/questionnaire/topic/form?id=${topic.id}">修改</a>
					<a href="${ctx}/questionnaire/topic/delete?id=${topic.id}" onclick="return confirmx('确认要删除该题目吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>