<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>优惠券管理</title>
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
		<li class="active"><a href="${ctx}/coupon/list">优惠券管理列表</a></li>
		<shiro:hasPermission name="coupon:info:view"><li><a href="${ctx}/coupon/form">优惠券添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="couponInfo" action="${ctx}/coupon/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label>金额：</label>  
				<form:select path="amount" class="input-medium">、
					<form:option value="">请选择</form:option>
					<form:options items="${fns:getDictList('coupon_info_money')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>有效期限：</label>  
				<form:select path="overdue" class="input-medium">
					<form:option value="">请选择</form:option>
					<form:options items="${fns:getDictList('regular_wloan_span')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			 </li>
			<li><label>优惠券类型：</label>  
				<form:select path="type" class="input-medium">
					<form:option value="">请选择</form:option>
					<form:options items="${fns:getDictList('coupon_info_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select> 
			</li>
				<li class="btns"><label><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></label></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>金额</th>
				<th>起投金额</th>
				<th>有效时长</th>
				<th>优惠券类型</th>
				<shiro:hasPermission name="coupon:info:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="couponInfos">
				<tr>
					<td>${couponInfos.amount}元</td>
					<td>${couponInfos.limitMoney}元</td>
					<td>${couponInfos.overdue}天</td>
					<td>
					${fns:getDictLabel(couponInfos.type, 'coupon_info_type', '-')}
					</td>
					<shiro:hasPermission name="coupon:info:edit">
						<td>
						<c:if test="${couponInfos.state=='1' }">
							<a href="${ctx}/coupon/form?id=${couponInfos.id}">修改</a>&nbsp;
							<a href="${ctx}/coupon/delete?id=${couponInfos.id}" onclick="return confirmx('确认要删除该优惠券记录？', this.href)">删除</a>
						</c:if>
							<a href="${ctx}/coupon/couponInfoUser/form?couponInfoId=${couponInfos.id}">发送优惠券</a>
							</td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>