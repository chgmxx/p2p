<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>抵用券管理</title>
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
		<li class="active"><a href="${ctx}/activity/userRateCouponHistory/">加息券列表</a></li>
		<shiro:hasPermission name="activity:userRateCouponHistory:edit">
			<li><a href="${ctx}/activity/userRateCouponHistory/rechargeForm">加息券充值</a></li>
		</shiro:hasPermission>
		<shiro:hasPermission name="activity:userRateCouponHistory:edit">
			<li><a href="${ctx}/activity/userRateCouponHistory/rechargeAllForm">加息券批量充值</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userRateCouponHistory" action="${ctx}/activity/userRateCouponHistory/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label>客户手机：</label> <form:input path="userInfo.name" htmlEscape="false" class="input-medium" /></li>
			<li><label>状态：</label> <form:select path="state" class="input-medium">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('a_user_awards_history_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>客户手机</th>
				<th>客户姓名</th>
				<th>加息券(%)</th>
				<th>状态</th>
				<th>类型</th>
				<th>投资项目</th>
				<th>获取日期</th>
				<th>逾期日期</th>
				<th>修改日期</th>
				<shiro:hasPermission name="activity:userRateCouponHistory:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="userRateCouponHistory">
				<tr>
					<td><a href="${ctx}/activity/userRateCouponHistory/viewForm?id=${userRateCouponHistory.id}"> ${userRateCouponHistory.userInfo.name}</a></td>
					<td>${userRateCouponHistory.userInfo.realName}</td>
					<td>${userRateCouponHistory.value}</td>
					<td>${fns:getDictLabel(userRateCouponHistory.state, 'a_user_awards_history_state', '')}</td>
					<td>${fns:getDictLabel(userRateCouponHistory.type, 'a_user_awards_history_type', '')}</td>
					<td>${userRateCouponHistory.wloanTermProject.name}</td>
					<td><fmt:formatDate value="${userRateCouponHistory.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${userRateCouponHistory.overdueDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${userRateCouponHistory.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="activity:userRateCouponHistory:edit">
						<td><a href="${ctx}/activity/userRateCouponHistory/viewForm?id=${userRateCouponHistory.id}">修改</a> <a href="${ctx}/activity/userRateCouponHistory/delete?id=${userRateCouponHistory.id}" onclick="return confirmx('确认要删除该抵用券吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>