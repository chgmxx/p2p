<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>中等网满标落单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//
			$("#messageBox").show();
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
		<li class="active"><a href="${ctx}/zdw/register/zdwProOrderInfo/">满标落单列表</a></li>
		<%-- <shiro:hasPermission name="zdw:register:zdwProOrderInfo:edit"><li><a href="${ctx}/zdw/register/zdwProOrderInfo/form">中等网满标落单添加</a></li></shiro:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="zdwProOrderInfo" action="${ctx}/zdw/register/zdwProOrderInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li>
				<label class="label">状态：</label>
				<form:select path="status" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:option value="00" label="登记成功"/>
					<form:option value="01" label="等待登记"/>
					<form:option value="02" label="登记失败"/>
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
				<th>散标ID</th>
				<th>散标编号</th>
				<th>状态</th>
				<th>满标时间</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<%-- <shiro:hasPermission name="zdw:register:zdwProOrderInfo:edit"><th>操作</th></shiro:hasPermission> --%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="zdwProOrderInfo">
			<tr>
				<td>
					${zdwProOrderInfo.proId}
				</td>
				<td>
					${zdwProOrderInfo.proNo}
				</td>
				<td>
					<c:if test="${zdwProOrderInfo.status == '00'}">
						<b>登记成功</b>
					</c:if>
					<c:if test="${zdwProOrderInfo.status == '01'}">
						<b>等待登记</b>
					</c:if>
					<c:if test="${zdwProOrderInfo.status == '02'}">
						<b>登记失败</b>
					</c:if>
				</td>
				<td>
					<fmt:formatDate value="${zdwProOrderInfo.fullDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${zdwProOrderInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${zdwProOrderInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${zdwProOrderInfo.remarks}
				</td>
				<%-- <shiro:hasPermission name="zdw:register:zdwProOrderInfo:edit"><td>
    				<a href="${ctx}/zdw/register/zdwProOrderInfo/form?id=${zdwProOrderInfo.id}">修改</a>
					<a href="${ctx}/zdw/register/zdwProOrderInfo/delete?id=${zdwProOrderInfo.id}" onclick="return confirmx('确认要删除该中等网满标落单吗？', this.href)">删除</a>
				</td></shiro:hasPermission> --%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>