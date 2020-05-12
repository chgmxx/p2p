<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>利率期数统计查询</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$("#btnSubmit").click(function(){
		$("#searchForm").attr("action","${ctx}/data/search/averageLoan/list");
		$("#searchForm").submit();
	}); 
	$(document).ready(function() {
		$("#errmsg").hide();
		 //查询
		
		
	});
	
</script>
<style type="text/css">
.select2-chosen{width: 250px;}
</style>
</head>
<body>
	<ul class="nav nav-tabs">

		<li class="active">利率期数统计查询</li>
	</ul>

	<form:form id="searchForm" modelAttribute="creditUserInfo" action="${ctx}/data/search/averageLoan/" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			
			<%-- <li><label class="label">平台用户编号：</label>
				<form:input path="id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li> --%>
			<li>
				<label class="label">选择时间：</label>
				<input name="endRepaymentDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="日期"
					value="<fmt:formatDate value="${creditUserInfo.endRepaymentDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix" style="color:red">${message}</li>
		</ul>
	</form:form> 

	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>平均在贷借款期数（天）--按用户</th>
				<th>平均在贷借款期数（天）--按标的</th>
				<th>平均在贷利率</th>
			</tr>
		</thead>
		<tbody>
	        <tr>
	        	<td>${result.averageLoanPeriod}</td> 
	        	<td>${result.averageProjectPeriod}</td> 
		        <td>${result.averageLoanInterestRate}</td> 
	        </tr>
	    </tbody>
		<%-- <thead>
			<tr>
				<th>项目名称</th>
				<th>融资主体</th>
				<th>项目编号</th>
				<th>放款金额</th>
				<th>还款金额</th>
				<th>还款类型</th>
				<th>还款日期</th>
				<th>约定放款日期</th>
				<th>实际放款日期</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanTermProjectPlan">
				<tr>
					<td><a href="${ctx}/wloanproject/wloanTermProjectPlan/findByProId?proid=${wloanTermProjectPlan.wloanTermProject.id}">${wloanTermProjectPlan.wloanTermProject.name}</a></td>
					<td>${wloanTermProjectPlan.wloanSubject.companyName}</td>
					<td>${wloanTermProjectPlan.wloanTermProject.sn}</td>
					<td>${wloanTermProjectPlan.wloanTermProject.currentAmount}</td>
					<td>${wloanTermProjectPlan.interest}</td>
					<td>${wloanTermProjectPlan.principal == '0' ? '利息' : '本息'}</td>
					<td><fmt:formatDate value="${wloanTermProjectPlan.repaymentDate}" pattern="yyyy-MM-dd" /></td>
					<td><fmt:formatDate value="${wloanTermProjectPlan.wloanTermProject.loanDate}" pattern="yyyy-MM-dd" /></td>
					<td><fmt:formatDate value="${wloanTermProjectPlan.wloanTermProject.realLoanDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><a href="${ctx}/wloanproject/wloanTermProjectPlan/findByProId?proid=${wloanTermProjectPlan.wloanTermProject.id}">还款</a></td>
				</tr>
			</c:forEach>
		</tbody> --%>
	</table>
	<%-- <div class="pagination">${page}</div> --%>
</body>
</html>