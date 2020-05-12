<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>加息券字典数据管理</title>
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
		<li class="active"><a href="${ctx}/activity/aRateCouponDic/">加息券字典数据列表</a></li>
		<shiro:hasPermission name="activity:aRateCouponDic:edit">
			<li><a href="${ctx}/activity/aRateCouponDic/addForm">加息券字典数据添加</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="aRateCouponDic" action="${ctx}/activity/aRateCouponDic/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<!-- 
		<ul class="ul-form">
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
		 -->
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>息率</th>
				<th>逾期天数(天)</th>
				<th>起投金额(RMB)</th>
				<th>状态</th>
				<th>备注</th>
				<th>修改日期</th>
				<shiro:hasPermission name="activity:aRateCouponDic:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="aRateCouponDic">
				<tr>
					<td><a href="${ctx}/activity/aRateCouponDic/updateForm?id=${aRateCouponDic.id}"> ${aRateCouponDic.rate} </a></td>
					<td>${aRateCouponDic.overdueDays}</td>
					<td>${aRateCouponDic.limitAmount}</td>
					<td>${fns:getDictLabel(aRateCouponDic.state, 'a_rate_coupon_dic_state', '')}</td>
					<td>${aRateCouponDic.remarks}</td>
					<td><fmt:formatDate value="${aRateCouponDic.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="activity:aRateCouponDic:edit">
						<td><a href="${ctx}/activity/aRateCouponDic/updateForm?id=${aRateCouponDic.id}">修改</a> <a href="${ctx}/activity/aRateCouponDic/delete?id=${aRateCouponDic.id}" onclick="return confirmx('确认要删除该加息券字典数据吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>