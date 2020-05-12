<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>ZTMG合作方信息管理</title>
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
		<li class="active"><a href="${ctx}/partner/ztmgPartnerPlatform/">【中投摩根】合作方信息列表</a></li>
		<shiro:hasPermission name="partner:ztmgPartnerPlatform:edit">
			<li><a href="${ctx}/partner/ztmgPartnerPlatform/addForm">【中投摩根】合作方信息新增</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="ztmgPartnerPlatform" action="${ctx}/partner/ztmgPartnerPlatform/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label>平台名称：</label> <form:input path="platformName" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label>平台类型：</label> <form:select path="platformType" class="input-medium">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('partner_platform_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select></li>
			<li><label>联系人电话：</label> <form:input path="phone" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label>联系人姓名：</label> <form:input path="name" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>平台名称</th>
				<th>平台编码</th>
				<th>平台类型</th>
				<th>联系人电话</th>
				<th>联系人姓名</th>
				<th>电子邮箱</th>
				<th>利率</th>
				<th>金额</th>
				<th>合作方所在地</th>
				<th>更新者</th>
				<th>更新时间</th>
				<shiro:hasPermission name="partner:ztmgPartnerPlatform:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="ztmgPartnerPlatform">
				<tr>
					<td><a href="${ctx}/partner/ztmgPartnerPlatform/viewForm?id=${ztmgPartnerPlatform.id}"> ${ztmgPartnerPlatform.platformName} </a></td>
					<td>${ztmgPartnerPlatform.platformCode}</td>
					<td>${fns:getDictLabel(ztmgPartnerPlatform.platformType, 'partner_platform_type', '')}</td>
					<td>${ztmgPartnerPlatform.phone}</td>
					<td>${ztmgPartnerPlatform.name}</td>
					<td>${ztmgPartnerPlatform.email}</td>
					<td>${ztmgPartnerPlatform.rate}</td>
					<td>${ztmgPartnerPlatform.money}</td>
					<td>${ztmgPartnerPlatform.area.name}</td>
					<td>${ztmgPartnerPlatform.updateBy.loginName}</td>
					<td><fmt:formatDate value="${ztmgPartnerPlatform.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="partner:ztmgPartnerPlatform:edit">
						<td><a href="${ctx}/partner/ztmgPartnerPlatform/updateForm?id=${ztmgPartnerPlatform.id}">修改</a> <a href="${ctx}/partner/ztmgPartnerPlatform/delete?id=${ztmgPartnerPlatform.id}" onclick="return confirmx('确认要删除该【中投摩根】合作方信息吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>