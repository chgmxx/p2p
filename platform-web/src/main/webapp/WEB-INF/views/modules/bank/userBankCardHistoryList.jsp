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
		<li class="active"><a href="${ctx}/bank/userBankCardHistory/">客户银行卡更换列表</a></li>
		<!-- 
		<shiro:hasPermission name="bank:userBankCardHistory:edit">
			<li><a href="${ctx}/bank/userBankCardHistory/addForm">银行卡更换新增</a></li>
		</shiro:hasPermission> -->
	</ul>
	<form:form id="searchForm" modelAttribute="userBankCardHistory" action="${ctx}/bank/userBankCardHistory/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label>客户手机：</label> <form:input path="mobilePhone" htmlEscape="false" maxlength="11" class="input-medium" /></li>
			<li><label>客户姓名：</label> <form:input path="realName" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li>
				<label>状态：</label>
				<form:select path="state" class="input-medium">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('replace_identity_card_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</li>
			<li class="clearfix"></li>
		</ul>
		<ul class="ul-form">
			<li><label>更换时间：</label> <input name="beginReplaceDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${userBankCardHistory.beginReplaceDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input name="endReplaceDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${userBankCardHistory.endReplaceDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
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
				<th>身份证</th>
				<th>旧银行卡</th>
				<th>新银行卡</th>
				<th>状态</th>
				<th>更换时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="bank:userBankCardHistory:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="userBankCardHistory">
				<tr>
					<td><a href="${ctx}/bank/userBankCardHistory/viewForm?id=${userBankCardHistory.id}">${userBankCardHistory.mobilePhone}</a></td>
					<td>${userBankCardHistory.realName}</td>
					<td>${userBankCardHistory.identityCardNo}</td>
					<td>${userBankCardHistory.oldBankCardNo}</td>
					<td>${userBankCardHistory.newBankCardNo}</td>
					<td>${fns:getDictLabel(userBankCardHistory.state, 'replace_identity_card_state', '')}</td>
					<td><fmt:formatDate value="${userBankCardHistory.replaceDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${userBankCardHistory.remarks}</td>
					<shiro:hasPermission name="bank:userBankCardHistory:edit">
						<td><a href="${ctx}/bank/userBankCardHistory/updateForm?id=${userBankCardHistory.id}">审核</a> <a href="${ctx}/bank/userBankCardHistory/delete?id=${userBankCardHistory.id}" onclick="return confirmx('确认要删除该银行卡吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>