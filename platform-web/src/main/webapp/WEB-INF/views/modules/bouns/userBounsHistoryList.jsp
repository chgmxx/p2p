<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户积分历史明细管理</title>
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
		<li class="active"><a href="${ctx}/bouns/userBounsHistory/">用户积分明细</a></li>
		<%-- <shiro:hasPermission name="bouns:userBounsHistory:edit"><li><a href="${ctx}/bouns/userBounsHistory/form">用户积分历史明细添加</a></li></shiro:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="userBounsHistory" action="${ctx}/bouns/userBounsHistory/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号：</label>
				<form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>姓名：</label>
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>积分类型：</label>
				<form:select path="bounsType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:option value="0" label="投资" />
					<form:option value="1" label="注册" />
					<form:option value="2" label="邀请好友" />
					<form:option value="3" label="签到" />
					<form:option value="4" label="积分抽奖" />
					<form:option value="5" label="积分兑换" />
					<form:option value="6" label="好友投资" />
					<form:option value="7" label="流标" />
				</form:select>
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
				<th>积分值</th>
				<th>积分类型</th>
				<th>创建日期</th>
				<%-- <shiro:hasPermission name="bouns:userBounsHistory:edit"><th>操作</th></shiro:hasPermission> --%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userBounsHistory">
			<tr>
				<td>
					${userBounsHistory.userInfo.name }
				</td>
				<td>
					${userBounsHistory.userInfo.realName }
				</td>
				<td>
					${userBounsHistory.amount}
				</td>
				<td>
					${userBounsHistory.bounsType}
				</td>
				<td>
					<fmt:formatDate value="${userBounsHistory.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
		<%-- 		<shiro:hasPermission name="bouns:userBounsHistory:edit"><td>
    				<a href="${ctx}/bouns/userBounsHistory/form?id=${userBounsHistory.id}">修改</a>
					<a href="${ctx}/bouns/userBounsHistory/delete?id=${userBounsHistory.id}" onclick="return confirmx('确认要删除该用户积分历史明细吗？', this.href)">删除</a>
				</td></shiro:hasPermission> --%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>