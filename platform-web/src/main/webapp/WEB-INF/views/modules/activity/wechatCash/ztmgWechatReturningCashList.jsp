<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信返现管理</title>
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
		<li class="active"><a href="${ctx}/activity/ztmgWechatReturningCash/">微信返现列表</a></li>
		<shiro:hasPermission name="activity:ztmgWechatReturningCash:edit"><li><a href="${ctx}/activity/ztmgWechatReturningCash/formall">微信返现批量充值</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="ztmgWechatReturningCash" action="${ctx}/activity/ztmgWechatReturningCash/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号：</label>
				<form:input path="mobilePhone" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>客户：</label>
				<form:input path="realName" htmlEscape="false" maxlength="64" class="input-medium"/>
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
				<th>客户名</th>
				<th>返现金额</th>
				<th>修改日期</th>
				<shiro:hasPermission name="activity:ztmgWechatReturningCash:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ztmgWechatReturningCash">
			<tr>
				<td><a href="${ctx}/activity/ztmgWechatReturningCash/form?id=${ztmgWechatReturningCash.id}">
					${ztmgWechatReturningCash.mobilePhone}
				</a></td>
				<td>
					${ztmgWechatReturningCash.realName}
				</td>
				<td>
					${ztmgWechatReturningCash.payAmount}
				</td>
				<td>
					<fmt:formatDate value="${ztmgWechatReturningCash.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="activity:ztmgWechatReturningCash:edit"><td>
    				<a href="${ctx}/activity/ztmgWechatReturningCash/form?id=${ztmgWechatReturningCash.id}">修改</a>
					<a href="${ctx}/activity/ztmgWechatReturningCash/delete?id=${ztmgWechatReturningCash.id}" onclick="return confirmx('确认要删除该微信返现吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>