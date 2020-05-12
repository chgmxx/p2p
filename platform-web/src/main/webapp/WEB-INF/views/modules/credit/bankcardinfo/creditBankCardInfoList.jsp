<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>信贷银行卡管理</title>
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
		<li class="active"><a href="#">信贷银行卡列表</a></li>
		<shiro:hasPermission name="credit:bankcardinfo:creditBankCardInfo:edit"><li></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="creditBankCardInfo" action="${ctx}/credit/bankcardinfo/creditBankCardInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<!-- <ul class="ul-form">
			<li><label>外键ID：</label>
				<form:input path="creditUserId" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>银行卡号：</label>
				<form:input path="bankCardNo" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>银行预留手机：</label>
				<form:input path="mobile" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul> -->
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>姓名</th>
				<th>银行卡号</th>
				<th>开户行</th>
				<th>银行预留手机</th>
				<th>修改时间</th>
				<!-- <shiro:hasPermission name="credit:bankcardinfo:creditBankCardInfo:edit"><th>操作</th></shiro:hasPermission> -->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditBankCardInfo">
			<tr>
				<td><a href="${ctx}/credit/bankcardinfo/creditBankCardInfo/form?id=${creditBankCardInfo.id}">
					${creditBankCardInfo.creditUserInfo.name}
				</a></td>
				<td>
					${creditBankCardInfo.bankCardNo}
				</td>
				<td>
					${creditBankCardInfo.bankName}
				</td>
				<td>
					${creditBankCardInfo.mobile}
				</td>
				<td>
					<fmt:formatDate value="${creditBankCardInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<!-- <shiro:hasPermission name="credit:bankcardinfo:creditBankCardInfo:edit"><td>
    				<a href="${ctx}/credit/bankcardinfo/creditBankCardInfo/form?id=${creditBankCardInfo.id}">修改</a>
					<a href="${ctx}/credit/bankcardinfo/creditBankCardInfo/delete?id=${creditBankCardInfo.id}" onclick="return confirmx('确认要删除该信贷银行卡吗？', this.href)">删除</a>
				</td></shiro:hasPermission> -->
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<div class="form-actions">
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</body>
</html>