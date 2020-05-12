<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>抵押物信息管理</title>
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
		<li class="active"><a href="#">抵押物信息列表</a></li>
		<shiro:hasPermission name="collateral:creditCollateralInfo:edit"></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="creditCollateralInfo" action="${ctx}/collateral/creditCollateralInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			<th>姓名</th>
			<th>车辆型号</th>
			<th>车辆号码</th>
			<th>购买价格</th>
			<th>行程里数</th>
			<th>购买时间</th>
			<th>创建时间</th>
			<th>修改时间</th>
			<th>状态</th>
				<!--<shiro:hasPermission name="collateral:creditCollateralInfo:edit"><th>操作</th></shiro:hasPermission> -->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditCollateralInfo">
			<tr>
			    <td>
					${creditCollateralInfo.userInfo.name}
				</td>
				<td>
					<a href="${ctx}/credit/annexFile/list?creditUserId=${creditUserInfo.id}&otherId=${creditCollateralInfo.id}">${creditCollateralInfo.modelNumber}</a>
				</td>
				<td>
					${creditCollateralInfo.plateNumber}
				</td>
				<td>
					${creditCollateralInfo.buyPrice}
				</td>
				<td>
					${creditCollateralInfo.mileage}
				</td>
				<td>
					${creditCollateralInfo.buyDate}
				</td>
			    <td>
					<fmt:formatDate value="${creditCollateralInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${creditCollateralInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
				 <c:if test="${creditCollateralInfo.state =='1' }">
				   审核中
				 </c:if>
				 <c:if test="${creditCollateralInfo.state =='2' }">
				 申请通过
				 </c:if>
				 <c:if test="${creditCollateralInfo.state =='3' }">
				 申请拒绝
				 </c:if>
			    </td>
				<!--<shiro:hasPermission name="collateral:creditCollateralInfo:edit"><td>
    				<a href="${ctx}/collateral/creditCollateralInfo/form?id=${creditCollateralInfo.id}">修改</a>
					<a href="${ctx}/collateral/creditCollateralInfo/delete?id=${creditCollateralInfo.id}" onclick="return confirmx('确认要删除该抵押物信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>-->
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