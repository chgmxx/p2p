<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>某天的数据统计查询</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$("#btnSubmit").click(function(){
		$("#searchForm").attr("action","${ctx}/data/search/dataStatistics/list");
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

		<li class="active">某天的数据统计查询</li>
	</ul>

	<form:form id="searchForm" modelAttribute="creditUserInfo" action="${ctx}/data/search/dataStatistics/" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			
			<%-- <li><label class="label">平台用户编号：</label>
				<form:input path="id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li> --%>
			<li>
				<label class="label">核心企业或安心投：</label>
				<%-- <form:select path="id">
					<form:option value="" label="请选择" />
					<form:option value="6809349449100994498" label="山西美特好连锁超市股份有限公司" />
					<form:option value="5685145015583919274" label="北京爱亲科技股份有限公司" />
					<form:option value="8109132022784559441" label="山西妈妈宝贝美特好孕婴童用品有限公司" />
					<form:option value="7826464034456156057" label="宁波熙耘科技有限公司" />
					<form:option value="10000" label="安心投" />
				</form:select> --%>
				<form:select path="id">
					<c:forEach var="middlemen" items="${middlemenList}">
						<%-- <form:option value="10000" label="安心投" /> --%>
						<form:option value="${middlemen.id}" label="${middlemen.enterpriseFullName}" />
					</c:forEach>
				</form:select>
			</li>
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
				<th>在贷供应商数量</th>
				<th>累计供应商数量</th>
				<th>在贷项目数量</th>
				<th>累计项目数量</th>
				<th>在贷本金（元）</th>
			</tr>
		</thead>
		<tbody>
	        <tr>
	        	<td>${result.loanSupplierCount}</td> 
		        <td>${result.totalSupplierCount}</td>    
		        <td>${result.loanProjectCount}</td> 
		        <td>${result.totalProjectCount}</td> 
		        <td>${result.loanPrincipal}</td> 
	        </tr>
	        <tr>
		        <th>待还金额（元）</th>
				<!-- <th>累计放款笔数</th>
				<th>累计还款笔数</th>
				<th>累计放款金额（元）</th>
		        <th>累计还款金额（元）</th> -->
			</tr>
			<tr>
				<td>${result.amountToPaid}</td> 
		       <%--  <td>${result.loanAmountCount}</td> 
		        <td>${result.repaymentAmountCount}</td> 
		        <td>${result.loanAmount}</td>  
				<td>${result.repaymentAmount}</td>  --%>
	        </tr>
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