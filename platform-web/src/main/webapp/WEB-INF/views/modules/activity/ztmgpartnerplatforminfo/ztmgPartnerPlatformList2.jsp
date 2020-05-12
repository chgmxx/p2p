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
		<li ><a href="${ctx}/partner/ztmgPartnerPlatform/list2">渠道用户信息列表</a></li>
		<li class="active"><a href="${ctx}/partner/ztmgPartnerPlatform/listForBrokerage">渠道用户信息详情</a></li>
		
	</ul>
	<form:form id="searchForm" modelAttribute="ztmgPartnerPlatform" action="" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
<!-- 		<ul class="ul-form"> -->
<!-- 			<li><label>联系人姓名：</label> -->
<%-- 				<form:input path="name" htmlEscape="false" maxlength="64" class="input-medium"/> --%>
<!-- 			</li> -->
<!-- 			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li> -->
<!-- 			<li class="clearfix"></li> -->
<!-- 		</ul> -->
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>邀请人</th>
				<th>被邀请人</th>
				<th>投资金额</th>
				<th>返利金额</th>
				<th>投资日期</th>
				<shiro:hasPermission name="ztmgpartnerplatforminfo:ztmgPartnerPlatform:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ztmgPartnerPlatform">
			<tr>
				<td>
					${ztmgPartnerPlatform.phone}
				</td>
				<td>
					${ztmgPartnerPlatform.userInfoName}
				</td>
				<td>
					${ztmgPartnerPlatform.moneyToOne}
				</td>
				<td>
					${ztmgPartnerPlatform.userTransDetail.amount}
				</td>
				<td>
					${ztmgPartnerPlatform.transDate}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>