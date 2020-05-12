<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>风控企业信息管理</title>
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
		<li class="active"><a href="${ctx}/riskmanagement/riskManagementMessage/">风控企业信息列表</a></li>
		<c:if test="${  usertype == '8' }">
		<shiro:hasPermission name="riskmanagement:riskManagementMessage:edit"><li><a href="${ctx}/riskmanagement/riskManagementMessage/form">风控企业信息添加</a></li></shiro:hasPermission>
	    </c:if>
	</ul>
	<form:form id="searchForm" modelAttribute="riskManagement" action="${ctx}/riskmanagement/riskManagementMessage/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>企业名称：</label>
				<form:input path="companyName" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>企业名称</th>
				<th>审批状态</th>
				<shiro:hasPermission name="riskmanagement:riskManagementMessage:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="riskManagement">
			<tr>
				<td><a href="${ctx}/riskmanagement/riskManagementMessage/checkMessage?id=${riskManagement.id}">
					${riskManagement.companyName}
				</a></td>
				<td>
					<c:if test="${ riskManagement.state == '0' }">审批拒绝</c:if>
					<c:if test="${ riskManagement.state == '1' }">待审批</c:if>
					<c:if test="${ riskManagement.state == '2' }">风控专员审批通过</c:if>
					<c:if test="${ riskManagement.state == '3' }">风控经理审批通过</c:if>
					<c:if test="${ riskManagement.state == '4' }">总经理审批通过</c:if>
				</td>
				<shiro:hasPermission name="riskmanagement:riskManagementMessage:edit"><td>
				    <a href="${ctx}/riskmanagement/riskManagementMessage/formControl?id=${riskManagement.id}">档案管理</a>
				    <c:if test="${ riskManagement.state == '1' && usertype == '5' }">
				      <a href="${ctx}/riskmanagement/riskManagementMessage/formCheck?id=${riskManagement.id}">审批</a>
				    </c:if>
				    <c:if test="${ riskManagement.state == '2' && usertype == '9' }">
				      <a href="${ctx}/riskmanagement/riskManagementMessage/formCheck?id=${riskManagement.id}">审批</a>
				    </c:if>
				    <c:if test="${ riskManagement.state == '3' && usertype == '1' }">
				      <a href="${ctx}/riskmanagement/riskManagementMessage/formCheck?id=${riskManagement.id}">审批</a>
				    </c:if>
				    <c:if test="${ riskManagement.state == '4' && usertype == '8' }">
				      <a href="${ctx}/riskmanagement/riskManagementMessage/sign?id=${riskManagement.id}">签约</a>
				    </c:if>
				    <c:if test="${ (riskManagement.state == '0') && usertype == '8' }">
				      <a href="${ctx}/riskmanagement/riskManagementMessage/formApproval?id=${riskManagement.id}">提交</a>
				    </c:if>
				    <c:if test="${ (riskManagement.state == '1') && usertype == '8' }">
				    <a href="${ctx}/riskmanagement/riskManagementMessage/form?id=${riskManagement.id}">修改</a>
					  <a href="${ctx}/riskmanagement/riskManagementMessage/delete?id=${riskManagement.id}" onclick="return confirmx('确认要删除该风控企业信息吗？', this.href)">删除</a>
				    </c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>