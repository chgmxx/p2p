<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>信贷用户管理</title>
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
	<style type="text/css">
	.select2-container{
	width: 300px;
	}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/credit/userinfo/creditUserInfo/">核心企业列表</a></li>
		<c:if test="${creditUserType == 11 }">
		 <shiro:hasPermission name="credit:userinfo:creditUserInfo:edit"><li><a href="${ctx}/credit/userinfo/creditUserInfo//add">核心企业添加</a></li></shiro:hasPermission>
		</c:if>
	</ul>
	<form:form id="searchForm" modelAttribute="creditUserInfo" action="${ctx}/credit/userinfo/creditUserInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号：</label>
				<form:input path="phone" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>姓名：</label>
				<form:input path="name" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<c:if test="${creditUserType == 11}">
			<li><label>核心企业：</label>
				<form:select path="id">
					<c:forEach var="middlemen" items="${middlemenList}">
						<form:option value="${middlemen.id}" label="${middlemen.enterpriseFullName}" />
					</c:forEach>
				</form:select>
			</li>
			</c:if>
			<c:if test="${creditUserType == 02}">
			<li><label>所属企业：</label>
				<form:select path="id">
					<c:forEach var="middlemen" items="${middlemenList}">
						<form:option value="${middlemen.id}" label="${middlemen.enterpriseFullName}" />
					</c:forEach>
				</form:select>
				<input id="creditUserType" name="creditUserType" type="hidden" value="02"/>
			</li>
			</c:if>
			<li><label>企业名称：</label>
				<form:input path="enterpriseFullName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>移动电话</th>
				<th>借款人</th>
				<th>在贷余额（元）</th>
				<th>公司名称</th>
				<th>业务类型</th>
				<th>基本信息</th>
				<th>资料信息</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditUserInfo">
			<tr>
				<td>
					<a href="${ctx}/credit/userinfo/creditUserInfo/form?id=${creditUserInfo.id}">${creditUserInfo.phone}</a>
				</td>
				<td>
					<b>${creditUserInfo.enterpriseFullName}</b><!-- ${creditUserInfo.name} -->
				</td>
				<td>
					<b>${creditUserInfo.inTheLoanBalance}</b>
				</td>
				<td>
					<b>${creditUserInfo.enterpriseFullName}</b>
				</td>
				<td>
					<c:if test="${creditUserInfo.creditUserType == '01'}">
						<b>投资户</b>
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '02'}">
						<b>借款户</b>
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '03'}">
						<b>担保户</b>
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '04'}">
						<b>咨询户</b>
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '05'}">
						<b>p2p平台户</b>
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '08'}">
						<b>营销户</b>
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '10'}">
						<b>收费户</b>
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '11'}">
						<b>代偿户</b>
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '12'}">
						<b>第三方营销账户</b>
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '13'}">
						<b>垫资账户</b>
					</c:if>
				</td>
				<td><a href="${ctx}/credit/annexFile/list?otherId=${creditUserInfo.id}">查看</a></td>
				<td><a href="${ctx}/credit/creditinfo/creditInfo/list?creditUserId=${creditUserInfo.id}">查看</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>