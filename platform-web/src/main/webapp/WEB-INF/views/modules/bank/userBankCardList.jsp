<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>客户银行卡更换管理</title>
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
		<li class="active"><a href="${ctx}/bank/userBankCard/">客户银行卡</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="userBankCard" action="${ctx}/bank/userBankCard/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label>客户手机：</label> <form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-medium" /></li>
			<li><label>客户姓名：</label> <form:input path="userInfo.realName" htmlEscape="false" maxlength="32" class="input-medium" /></li>
			<li><label>银行卡号码：</label> <form:input path="bankAccountNo" htmlEscape="false" maxlength="50" class="input-medium" /></li>
			<li><label>银行代码：</label> <form:input path="bankNo" htmlEscape="false" maxlength="10" class="input-medium" /></li>
			<li><label>状态：</label> <form:select path="state" class="input-medium">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('bank_card_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select></li>
		</ul>
		<ul class="ul-form">
			<li><label>绑卡时间：</label> <input placeholder="开始日期" name="beginBindDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${userBankCard.beginBindDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" name="endBindDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${userBankCard.endBindDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
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
				<th>银行卡号码</th>
				<th>连连绑定号</th>
				<th>银行代码</th>
				<th>绑卡时间</th>
				<th>状态</th>
				<shiro:hasPermission name="bank:userBankCard:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="userBankCard">
				<tr>
					<td><a href="${ctx}/bank/userBankCard/viewForm?id=${userBankCard.id}">${userBankCard.userInfo.name}</a></td>
					<td>${userBankCard.userInfo.realName}</td>
					<td>${userBankCard.bankAccountNo}</td>
					<td>${userBankCard.userInfo.llagreeNo}</td>
					<td>${userBankCard.bankNo}</td>
					<td><fmt:formatDate value="${userBankCard.bindDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${fns:getDictLabel(userBankCard.state, 'bank_card_state', '')}</td>
					<shiro:hasPermission name="bank:userBankCard:edit">
						<td><a href="${ctx}/bank/userBankCard/delete?id=${userBankCard.id}" onclick="return confirmx('确认要删除该客户银行卡吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>