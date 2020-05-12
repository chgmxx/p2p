<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>统计区间数据</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$("#btnSubmit").click(function(){
		alert("000");
		$("#searchForm").attr("action","${ctx}/data/search/statisticalInterval/list");
		alert("1111");
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
		<li class="active">数据统计区间查询</li>
	</ul>
	<form:form id="searchForm" modelAttribute="creditUserInfo" action="${ctx}/data/search/statisticalInterval/" method="post" class="breadcrumb form-search">
		
		<ul class="ul-form">
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
				<input name="beginRepaymentDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="开始日期"
					value="<fmt:formatDate value="${creditUserInfo.beginRepaymentDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/> - 
				<input name="endRepaymentDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="结束日期"
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
				<th>累计放款笔数</th>
				<th>累计还款笔数</th>
				<th>累计放款金额（元）</th>
		        <th>累计还款金额（元）</th>
			</tr>
		</thead>
		<tbody>
	        <tr>
	        	<td>${result.loanAmountCount}</td> 
		        <td>${result.repaymentAmountCount}</td> 
		        <td>${result.loanAmount}</td>  
				<td>${result.repaymentAmount}</td> 
		    </tr>
		</tbody>
	</table>
</body>
</html>