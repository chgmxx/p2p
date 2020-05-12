<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>标的信息查询</title>
	<meta name="decorator" content="default"/>
<script type="text/javascript">
	$(document).ready(function() {
		
	});
</script>
<style type="text/css">
	.select2-container {
		width: 300px;
	}
	.input-medium{
	width:250px
	}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active">标的信息查询</li>
		
	</ul>
	<form:form id="searchForm" modelAttribute="lanMaoWhiteList" action="${ctx}/lanMao/search/searchProject/" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			
			<li><label class="label">标的号：</label>
				<form:input path="projectNo" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix" style="color:red">${message}</li>
		</ul>
	</form:form> 
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>借款方平台用户编号</th>
				<th>标的号</th>
				<th>标的金额（元）</th>
				<th>标的名称</th>
				<th>标的类型</th>
				<th>标的期限（天）</th>
				<th>标的属性</th>			
			</tr>
		</thead>
		<tbody>
        <tr>
        <td>${result.platformUserNo}</td>    
        <td>${result.projectNo}</td> 
        <td>${result.projectAmount}</td> 
        <td>${result.projectName}</td> 
        <td>${result.projectType}</td> 
        <td>${result.projectPeriod}</td> 
        <td>${result.projectProperties}</td>              
        </tr>
        <tr>
        <th>年化利率</th>
		<th>还款方式</th>
		<th>标的状态</th>
		<th>出借金额</th>
		<th>已出借确认金额</th>
        <th>已还款确认本金</th>
		<th>已还利息</th>
		
		</tr>
		<tr>
		 <td>${result.annualInterestRate}</td> 
        <td>${result.repaymentWay}</td> 
        <td>${result.projectStatus}</td> 
        <td>${result.tenderAmount}</td> 
        <td>${result.loanAmount}</td>  
		<td>${result.repaymentAmount}</td> 
        <td>${result.income}</td> 
              
          </tr>
		</tbody>
		
	</table>
</body>
</html>