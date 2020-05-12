<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户优惠券信息管理</title>
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
		<li class="active"><a href="${ctx}/coupon/couponInfoUser/">客户优惠券信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="couponInfoUser" action="${ctx}/coupon/couponInfoUser/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户：</label>
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>手机号：</label>
				<form:input path="userInfo.name" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>优惠券类型：</label>  
				<form:select path="couponInfo.type" class="input-medium">
					<form:option value="">请选择</form:option>
					<form:options items="${fns:getDictList('coupon_info_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select> 
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			<th>姓名</th>
			<th>手机号</th>
			<th>优惠券金额</th>
			<th>优惠券类型</th>
			<th>到期时间</th>
			<th>优惠券状态</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="couponInfoUser">
			<tr>
			<td>${couponInfoUser.userInfo.realName }</td>
			<td>${couponInfoUser.userInfo.name }</td>
			<td>${couponInfoUser.couponInfo.amount }</td>
			<td>${fns:getDictLabel(couponInfoUser.couponInfo.type, 'coupon_info_type', '-')}</td>
			<td> 
			<fmt:formatDate value="${couponInfoUser.endDate }" pattern="yyyy-MM-dd"/>
			</td>
			<td>
				<c:if test="${couponInfoUser.state=='1'}">
					未使用
				</c:if>
				<c:if test="${couponInfoUser.state=='2'}">
					已使用
				</c:if>
			</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>