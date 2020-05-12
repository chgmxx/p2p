<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户收货地址管理</title>
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
		<li class="active"><a href="${ctx}/bouns/userConsigneeAddress/">用户收货地址列表</a></li>
		<%-- <shiro:hasPermission name="bouns:userConsigneeAddress:edit"><li><a href="${ctx}/bouns/userConsigneeAddress/form">用户收货地址添加</a></li></shiro:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="userConsigneeAddress" action="${ctx}/bouns/userConsigneeAddress/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号：</label>
				<form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>姓名：</label>
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th colspan="2">账户信息</th>
				<th colspan="7">收货地址信息</th>
			</tr>
			<tr>
				<th>姓名</th>
				<th>手机号</th>
				<th>收货人姓名</th>
				<th>收货人手机号</th>
				<th>是否默认地址</th>
				<th>省份</th>
				<th>城市编码</th>
				<th>详细地址</th>
				<th>创建时间</th>
				<shiro:hasPermission name="bouns:userConsigneeAddress:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userConsigneeAddress">
			<tr>
				<td>
					${userConsigneeAddress.userInfo.realName}
				</td>
				<td>
					${userConsigneeAddress.userInfo.name}
				</td>
				
				<td>
					${userConsigneeAddress.username}
				</td>
				<td>
					${userConsigneeAddress.mobile }
				</td>
				<td>
					${userConsigneeAddress.isDefault}
				</td>
				<td>
					${userConsigneeAddress.province.name}
				</td>
				<td>
					${userConsigneeAddress.city.name}
				</td>
				<td>
					${userConsigneeAddress.address}
				</td>
				<td>
					<fmt:formatDate value="${userConsigneeAddress.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="bouns:userConsigneeAddress:edit"><td>
    				<a href="${ctx}/bouns/userConsigneeAddress/form?id=${userConsigneeAddress.id}">修改</a>
					<a href="${ctx}/bouns/userConsigneeAddress/delete?id=${userConsigneeAddress.id}" onclick="return confirmx('确认要删除该用户收货地址吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>