<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>活期项目投资管理</title>
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
		<li class="active"><a href="${ctx}/current/invest/wloanCurrentProjectInvest/">活期项目投资列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanCurrentProjectInvest" action="${ctx}/current/invest/wloanCurrentProjectInvest/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>项目名称：</label>
				<form:input path="currentProjectInfo.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>用户姓名：</label>
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>手机号：</label>
				<form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目名称</th>
				<th>用户姓名</th>
				<th>金额</th>
				<th>投资日期</th>
				<th>抵用券金额</th>
				<shiro:hasPermission name="current:invest:wloanCurrentProjectInvest:edit"><th>资金来源</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wloanCurrentProjectInvest">
			<tr>
				<td><a href="${ctx}/current/invest/wloanCurrentProjectInvest/form?id=${wloanCurrentProjectInvest.id}">
					${wloanCurrentProjectInvest.currentProjectInfo.name}
				</a></td>
				<td>
					${wloanCurrentProjectInvest.userInfo.realName}
				</td>
				<td>
					${wloanCurrentProjectInvest.amount}
				</td>
				<td>
					<fmt:formatDate value="${wloanCurrentProjectInvest.bidDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${wloanCurrentProjectInvest.vouvherAmount}
				</td>
				<shiro:hasPermission name="current:invest:wloanCurrentProjectInvest:edit"><td>
    				<a href="${ctx}/current/invest/wloanCurrentProjectInvest/findCome?id=${wloanCurrentProjectInvest.id }">查看资金来源</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>