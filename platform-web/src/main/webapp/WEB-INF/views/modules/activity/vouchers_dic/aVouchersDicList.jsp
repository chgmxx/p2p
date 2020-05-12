<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>抵用券类型管理</title>
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
		<li class="active"><a href="${ctx}/activity/aVouchersDic/">抵用券类型列表</a></li>
		<shiro:hasPermission name="activity:aVouchersDic:edit">
			<li><a href="${ctx}/activity/aVouchersDic/addForm">抵用券类型添加</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="aVouchersDic" action="${ctx}/activity/aVouchersDic/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<!-- 
		<ul class="ul-form">
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
		 -->
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>编号，唯一标识</th>
				<th>抵用券金额(元)</th>
				<th>逾期天数(天)</th>
				<th>起投金额(元)</th>
				<th>状态</th>
				<th>项目期限范围</th>
				<th>备注</th>
				<th>创建日期</th>
				<th>更新日期</th>
				<shiro:hasPermission name="activity:aVouchersDic:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="aVouchersDic">
				<tr>
					<td><a href="${ctx}/activity/aVouchersDic/updateForm?id=${aVouchersDic.id}">${aVouchersDic.id}</a></td>
					<td>${aVouchersDic.amountStr}</td>
					<td>${aVouchersDic.overdueDays}</td>
					<td>${aVouchersDic.limitAmountStr}</td>
					<td>${fns:getDictLabel(aVouchersDic.state, 'a_vouchers_dic_state', '')}</td>
					<td>
						<c:choose>
							<c:when test="${aVouchersDic.spans == '1'}">
								<b>通用</b>
							</c:when>
							<c:otherwise>
								<b>${aVouchersDic.spans}</b>
							</c:otherwise>
						</c:choose>
					</td>
					<td>${aVouchersDic.remarks}</td>
					<td><fmt:formatDate value="${aVouchersDic.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${aVouchersDic.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="activity:aVouchersDic:edit">
						<td><a href="${ctx}/activity/aVouchersDic/updateForm?id=${aVouchersDic.id}">修改</a> <a href="${ctx}/activity/aVouchersDic/delete?id=${aVouchersDic.id}" onclick="return confirmx('确认要删除该抵用券字典数据吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>