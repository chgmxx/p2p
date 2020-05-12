<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>活期客户投资管理</title>
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
		<li class="active"><a href="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/">活期客户投资列表</a></li>
		<shiro:hasPermission name="wloan_current_user_invest:wloanCurrentUserInvest:edit">
			<li><a href="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/addForm">活期客户投资添加</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanCurrentUserInvest" action="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label>投资日期：</label> <input name="beginBidDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanCurrentUserInvest.beginBidDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input name="endBidDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanCurrentUserInvest.endBidDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
			<li><label>投资人手机：</label> <form:input path="userInfo.name" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label>投资人姓名：</label> <form:input path="userInfo.realName" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>投资人手机</th>
				<th>投资人姓名</th>
				<th>投资金额(RMB)</th>
				<th>在投金额(RMB)</th>
				<th>抵用券金额(RMB)</th>
				<th>投资日期</th>
				<th>投资IP</th>
				<th>状态</th>
				<th>投资状态</th>
				<th>资金流向</th>
				<shiro:hasPermission name="wloan_current_user_invest:wloanCurrentUserInvest:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanCurrentUserInvest">
				<tr>
					<td><a href="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/viewForm?id=${wloanCurrentUserInvest.id}"> ${wloanCurrentUserInvest.userInfo.name} </a></td>
					<td>${wloanCurrentUserInvest.userInfo.realName}</td>
					<td>${wloanCurrentUserInvest.amount}</td>
					<td>${wloanCurrentUserInvest.onLineAmount}</td>
					<td>${wloanCurrentUserInvest.voucherAmount}</td>
					<td><fmt:formatDate value="${wloanCurrentUserInvest.bidDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${wloanCurrentUserInvest.ip}</td>
					<td>${fns:getDictLabel(wloanCurrentUserInvest.state, 'wloan_current_user_invest_state', '')}</td>
					<td>${fns:getDictLabel(wloanCurrentUserInvest.bidState, 'wloan_current_user_invest_bid_state', '')}</td>
					<td>
						<c:if test="${wloanCurrentUserInvest.state == 1}">
							<a href="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/moneyFlows?id=${wloanCurrentUserInvest.id}">待投资</a>
						</c:if>
						<c:if test="${wloanCurrentUserInvest.state == 2}">
							<a href="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/moneyFlows?id=${wloanCurrentUserInvest.id}">已投资</a>
						</c:if>
						<c:if test="${wloanCurrentUserInvest.state == 3}">
							<span class="help-inline">已赎回</span>
						</c:if>
					</td>
					<shiro:hasPermission name="wloan_current_user_invest:wloanCurrentUserInvest:edit">
						<td><a href="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/updateForm?id=${wloanCurrentUserInvest.id}">修改</a> <a href="${ctx}/wloan_current_user_invest/wloanCurrentUserInvest/delete?id=${wloanCurrentUserInvest.id}" onclick="return confirmx('确认要删除该活期客户投资吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>