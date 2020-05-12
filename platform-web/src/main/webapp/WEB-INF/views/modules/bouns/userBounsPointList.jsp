<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户积分信息管理</title>
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
		<li class="active"><a href="${ctx}/bouns/userBounsPoint/">用户积分信息列表</a></li>
		<%-- <shiro:hasPermission name="bouns:userBounsPoint:edit"><li><a href="${ctx}/bouns/userBounsPoint/form">用户积分信息添加</a></li></shiro:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="userBounsPoint" action="${ctx}/bouns/userBounsPoint/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号：</label>
				<form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>姓名：</label>
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号</th>
				<th>姓名</th>
				<th>积分</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<%-- <shiro:hasPermission name="bouns:userBounsPoint:edit"><th>操作</th></shiro:hasPermission> --%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userBounsPoint">
			<tr>
				<td>${userBounsPoint.userInfo.name}</td>
				<td>${userBounsPoint.userInfo.realName}</td>
				<td>${userBounsPoint.score}</td>
				<td>
					<fmt:formatDate value="${userBounsPoint.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${userBounsPoint.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<%-- <shiro:hasPermission name="bouns:userBounsPoint:edit"><td>
    				<a href="${ctx}/bouns/userBounsPoint/form?id=${userBounsPoint.id}">修改</a>
					<a href="${ctx}/bouns/userBounsPoint/delete?id=${userBounsPoint.id}" onclick="return confirmx('确认要删除该用户积分信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission> --%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>