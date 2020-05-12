<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>用户提现管理</title>
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
		<li class="active"><a href="${ctx}/withdraw/withdraw/">提现列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="userCash" action="${ctx}/withdraw/withdraw/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label>提现日期：</label> <input name="beginBeginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="开始日期" value="<fmt:formatDate value="${userCash.beginBeginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
			<li><label>至：</label> <input name="endBeginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="结束日期" value="<fmt:formatDate value="${userCash.endBeginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
			<li class="btns"><label></label><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>移动电话</th>
				<th>真实姓名</th>
				<th>订单号</th>
				<th>提现金额</th>
				<th>手续费</th>
				<th>提现日期</th>
				<th>状态</th>
				<shiro:hasPermission name="withdraw:withdraw:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="userCash">
				<tr>
					<td>${userCash.userInfo.name}</td>
					<td>${userCash.userInfo.realName}</td>
					<td>${userCash.sn}</td>
					<td>${userCash.amount}</td>
					<td>${userCash.feeAmount}</td>
					<td><fmt:formatDate value="${userCash.beginDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${fns:getDictLabel(userCash.state, 'cash_state', '')}</td>
					<shiro:hasPermission name="withdraw:withdraw:edit">
						<td><c:if test="${userCash.state == 1}">
								<a href="${ctx}/withdraw/withdraw/approved?id=${userCash.id}" onclick="return confirmx('确认该笔提现审核无误，进行付款吗？', this.href)">通过</a>
								<a href="${ctx}/withdraw/withdraw/refused?id=${userCash.id}" onclick="return confirmx('确认要拒绝该笔提现吗？', this.href)">拒绝</a>
							</c:if> <c:if test="${userCash.state == 2}">
						审核通过
					</c:if> <c:if test="${userCash.state == 3}">
						已拒绝
					</c:if> <c:if test="${userCash.state == 4}">
						到账成功
					</c:if> <c:if test="${userCash.state == 5}">
						到账失败
					</c:if></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>