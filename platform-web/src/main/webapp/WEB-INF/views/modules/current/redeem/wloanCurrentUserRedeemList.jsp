<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>活期赎回管理</title>
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
		<li class="active"><a href="${ctx}/redeem/wloanCurrentUserRedeem/">活期赎回列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanCurrentUserRedeem" action="${ctx}/redeem/wloanCurrentUserRedeem/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
		<!-- <li><label>项目名称：</label>
				<form:input path="wloanCurrentProject.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li> -->
			<li><label>转到用户：</label>
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>转让用户：</label>
				<form:input path="userInfo1.realName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>转让金额：</label>
				<form:input path="amount" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>转让日期：</label>
				<input name="beginRedeemDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${wloanCurrentUserRedeem.beginRedeemDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="endRedeemDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${wloanCurrentUserRedeem.endRedeemDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
		  <!--  <th>项目</th> -->
				<th>转到用户</th>
				<th>转让用户</th>
				<th>转让金额</th>
				<th>转让日期</th>
				<th>赎回状态</th>
				<shiro:hasPermission name="redeem:wloanCurrentUserRedeem:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wloanCurrentUserRedeem">
			<tr>
			<!-- <td><a href="${ctx}/redeem/wloanCurrentUserRedeem/form?id=${wloanCurrentUserRedeem.id}">
					${wloanCurrentUserRedeem.wloanCurrentProject.name}
				</a></td> -->
				<td>
					${wloanCurrentUserRedeem.userInfo.realName}
				</td>
				<td>
					${wloanCurrentUserRedeem.userInfo1.realName}
				</td>
				<td>
					${wloanCurrentUserRedeem.amount}
				</td>
				<td>
					<fmt:formatDate value="${wloanCurrentUserRedeem.redeemDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
				<c:if test="${wloanCurrentUserRedeem.state == 1}">待审批</c:if>
				<c:if test="${wloanCurrentUserRedeem.state == 2}">已审批</c:if>
				<c:if test="${wloanCurrentUserRedeem.state == 3}">审批失败</c:if>	
				</td>
				<shiro:hasPermission name="redeem:wloanCurrentUserRedeem:edit"><td>
				 <c:if test="${wloanCurrentUserRedeem.state == '1' && usertype == '4' }">
				  <a href="${ctx}/redeem/wloanCurrentUserRedeem/check?id=${wloanCurrentUserRedeem.id}">审核</a>
				  </c:if>
    				<a href="${ctx}/redeem/wloanCurrentUserRedeem/form?id=${wloanCurrentUserRedeem.id}">查看</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>