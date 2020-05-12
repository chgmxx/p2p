<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>渠道用户信息管理</title>
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
		<li class="active"><a href="${ctx}/partner/ztmgPartnerPlatform/list2">渠道用户信息列表</a></li>
<%-- 		<shiro:hasPermission name="ztmgpartnerplatforminfo:ztmgPartnerPlatform:edit"><li><a href="${ctx}/ztmgpartnerplatforminfo/ztmgPartnerPlatform/form">渠道用户信息添加</a></li></shiro:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="ztmgPartnerPlatform" action="${ctx}/partner/ztmgPartnerPlatform/list2" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>联系人姓名：</label>
				<form:input path="platformName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>平台名称</th>
				<th>注册人数</th>
				<th>投资人数</th>
				<th>返利金额</th>
				<shiro:hasPermission name="ztmgpartnerplatforminfo:ztmgPartnerPlatform:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ztmgPartnerPlatform">
			<tr>
				<td><a href="${ctx}/partner/ztmgPartnerPlatform/listForBrokerage?id=${ztmgPartnerPlatform.id}">
					${ztmgPartnerPlatform.platformName}
				</a></td>
				<td><a href="${ctx}/partner/ztmgPartnerPlatform/listForRegist?id=${ztmgPartnerPlatform.id}">
					${ztmgPartnerPlatform.registUser}
				</td>
				<td>
					${ztmgPartnerPlatform.investUser}
				</td>
				<td>
					${ztmgPartnerPlatform.sumMoney}
				</td>
				<shiro:hasPermission name="ztmgpartnerplatforminfo:ztmgPartnerPlatform:edit"><td>
    				<a href="${ctx}/ztmgpartnerplatforminfo/ztmgPartnerPlatform/form?id=${ztmgPartnerPlatform.id}">修改</a>
					<a href="${ctx}/ztmgpartnerplatforminfo/ztmgPartnerPlatform/delete?id=${ztmgPartnerPlatform.id}" onclick="return confirmx('确认要删除该渠道用户信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>