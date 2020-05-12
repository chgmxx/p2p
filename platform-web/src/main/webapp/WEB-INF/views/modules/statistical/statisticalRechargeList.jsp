<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>投资记录管理</title>
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
		<li><a href="${ctx}/statistical/">运营数据统计</a></li>
		<li class="active"><a href="#">充值详情统计</a></li>
	</ul>
		<form:form id="searchForm" modelAttribute="wloanTermInvest" action="${ctx}/statistical/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<li><label>投资日期：</label> <input placeholder="开始日期" id="beginBeginDate" name="beginBeginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.beginBeginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" id="endBeginDate" name="endBeginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.endBeginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
		<li>
			<label>用户类型：</label> 
			<form:select id="userFlag" path="userFlag" class="input-medium">
				<form:options items="${fns:getDictList('investment_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</li>
		<li>
			<label>投资金额：</label> 
			<form:select id="amount" path="amount" class="input-medium">
				<form:option value="" label="全部" />
				<form:option value="50000" label="大于50000" />
			</form:select>
		</li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>投资人手机</th>
				<th>投资人姓名</th>
				<th>充值日期</th>
				<th>充值金额(RMB)</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${rechargeList}" var="userRecharge">
				<tr>
					<td>${userRecharge.userInfo.name}</td>
					<td>${userRecharge.userInfo.realName}</td>
					<td><fmt:formatDate value="${userRecharge.beginDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${userRecharge.amount}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>