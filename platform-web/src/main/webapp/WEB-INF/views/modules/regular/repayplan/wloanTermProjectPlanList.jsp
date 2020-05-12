<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>定期项目信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//
			$("#messageBox").show();
			// 导出.
			$("#btnProjectRepayPlanExportId").click(function(){
				top.$.jBox.confirm("确认要执行【Excel-导出】操作吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/wloanproject/wloanTermProjectPlan/exportProjectRepayPlanList?exportFlag=1");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});// --.
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			// 分页时，重新定义action.
			$("#searchForm").attr("action","${ctx}/wloanproject/wloanTermProjectPlan/");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/wloanproject/wloanTermProjectPlan/">项目还款计划列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermProjectPlan" action="${ctx}/wloanproject/wloanTermProjectPlan/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<input id="btnProjectRepayPlanExportId" class="btn btn-inverse" style="float: right;" type="button" value="Excel-导出" />
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
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wloanTermProjectPlan">
			<tr>
				<td><a href="${ctx}/wloanproject/wloanTermProjectPlan/proid?proid=${wloanTermProjectPlan.wloanTermProject.id}">
					${wloanTermProjectPlan.wloanTermProject.name}</a>
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
				<td><a href="${ctx}/wloanproject/wloanTermProjectPlan/proid?proid=${wloanTermProjectPlan.wloanTermProject.id}">还款</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>