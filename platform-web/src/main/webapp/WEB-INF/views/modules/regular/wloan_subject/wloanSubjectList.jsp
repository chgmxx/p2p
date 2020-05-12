<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>融资主体管理</title>
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
		<li class="active"><a href="${ctx}/wloan_subject/wloanSubject/">融资主体列表</a></li>
		<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
			<li><a href="${ctx}/wloan_subject/wloanSubject/addForm">融资主体添加</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanSubject" action="${ctx}/wloan_subject/wloanSubject/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label>名称：</label> <form:input path="companyName" htmlEscape="false" maxlength="255" class="input-medium" /></li>
			<li><label>类型：</label> <form:select path="type" class="input-medium">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('wloan_subject_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select></li>
			<li class="clearfix"></li>
		</ul>
		<ul class="ul-form">
			<li><label>收款人：</label> <form:input path="cashierUser" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label>身份证号：</label> <form:input path="cashierIdCard" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label>银行卡号：</label> <form:input path="cashierBankNo" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label>预留手机：</label> <form:input path="cashierBankPhone" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li class="clearfix"></li>
		</ul>
		<ul class="ul-form">
			<li><label>借款人：</label> <form:input path="loanUser" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label>身份证号：</label> <form:input path="loanIdCard" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label>手机号码：</label> <form:input path="loanPhone" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li class="clearfix"></li>
		</ul>
		<ul class="ul-form">
			<li><label>创建时间：</label> <input name="beginCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanSubject.beginCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input name="endCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanSubject.endCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
			<li><label>更新时间：</label> <input name="beginUpdateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanSubject.beginUpdateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input name="endUpdateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanSubject.endUpdateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<th>类型</th>
				<th>注册资金</th>
				<th>收款人</th>
				<th>收款人银行预留手机</th>
				<th>借款人姓名</th>
				<th>借款人身份证号</th>
				<th>借款人手机号</th>
				<th>创建者</th>
				<th>创建时间</th>
				<th>更新者</th>
				<th>更新时间</th>
				<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanSubject">
				<tr>
					<td><a href="${ctx}/wloan_subject/wloanSubject/viewForm?id=${wloanSubject.id}"> ${wloanSubject.companyName} </a></td>
					<td>${fns:getDictLabel(wloanSubject.type, 'wloan_subject_type', '')}</td>
					<td>${wloanSubject.registerAmount}</td>
					<td>${wloanSubject.cashierUser}</td>
					<td>${wloanSubject.cashierBankPhone}</td>
					<td>${wloanSubject.loanUser}</td>
					<td>${wloanSubject.loanIdCard}</td>
					<td>${wloanSubject.loanPhone}</td>
					<td>${wloanSubject.createBy.loginName}</td>
					<td><fmt:formatDate value="${wloanSubject.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${wloanSubject.updateBy.loginName}</td>
					<td><fmt:formatDate value="${wloanSubject.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
						<td><a href="${ctx}/wloan_subject/wloanSubject/updateForm?id=${wloanSubject.id}">修改</a> <a href="${ctx}/wloan_subject/wloanSubject/delete?id=${wloanSubject.id}" onclick="return confirmx('确认要删除该融资主体吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>