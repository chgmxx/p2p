<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>定期项目信息管理</title>
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
		<%-- <li><a href="${ctx}/wloanproject/wloanTermProject/">定期项目信息列表</a></li>
		<shiro:hasPermission name="wloanproject:wloanTermProject:edit"><li><a href="${ctx}/wloanproject/wloanTermProject/form">定期项目信息添加</a></li></shiro:hasPermission> --%>
		<li class="active"><a href="${ctx}/wloanproject/wloanTermProjectPlan/proid?proid=${proid }">项目还款计划</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermProjectPlan" action="${ctx}/wloanproject/wloanTermProjectPlan/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目名称</th>
				<th>还款类型</th>
				<th>还款金额</th>
				<th>还款日期</th>
				<c:if test="${surplusAmount == 0  }">
					<shiro:hasPermission name="wloanproject:wloanTermProjectPlan:edit"><th>操作</th></shiro:hasPermission>
				</c:if>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${plans}" var="wloanTermProjectPlan">
			<tr>
				<td>
					<a href="${ctx}/wloanproject/wloanTermProjectPlan/fromWloanTermProjectPlanDetail?projectPlanId=${wloanTermProjectPlan.id}&viewType=1">${wloanTermProjectPlan.wloanTermProject.name}</a>
				</td>
				<td>
					${wloanTermProjectPlan.principal == '0' ? '利息' : '本息'}
				</td>
				<td>
					${wloanTermProjectPlan.interest}
				</td>
				<td>
					<fmt:formatDate value="${wloanTermProjectPlan.repaymentDate}" pattern="yyyy-MM-dd"/>
				</td>
				<c:choose>
				   <c:when test="${surplusAmount == 0  }">   
				   		<shiro:hasPermission name="wloanproject:wloanTermProjectPlan:edit">
						   <td>
								<c:if test="${wloanTermProjectPlan.state==1 }">
									<a href="${ctx}/wloanproject/wloanTermProjectPlan/fromWloanTermProjectPlanDetail?projectPlanId=${wloanTermProjectPlan.id}">还款</a>
								</c:if>
								<c:if test="${wloanTermProjectPlan.state==2 }">
									还款成功
								</c:if>
								<c:if test="${wloanTermProjectPlan.state==3 }">
									<a href="${ctx}/wloanproject/wloanTermProjectPlan/fromWloanTermProjectPlanDetail?projectPlanId=${wloanTermProjectPlan.id}">还款失败</a>
								</c:if>
							</td>
						</shiro:hasPermission>
				   </c:when>
				</c:choose>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<div align="right">
		 <input id="btnCancel" class="btn btn-primary" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</body>
</html>