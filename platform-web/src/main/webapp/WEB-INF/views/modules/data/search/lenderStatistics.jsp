<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>出借人统计查询</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$("#btnSubmit").click(function(){
		$("#searchForm").attr("action","${ctx}/data/search/lenderStatistics/list");
		$("#searchForm").submit();
	}); 
	$(document).ready(function() {
		$("#errmsg").hide();
		 //查询
		$("#messageBox").show();
		//
		$("#btnLenderExport").click(function(){
			top.$.jBox.confirm("确认要导出出借人信息吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/data/search/lenderStatistics/exportLenderStatistics");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}); // --.
		
	});
	
	// 回车键上抬.	
	$(document).keyup(function(event){
		if(event.keyCode ==13){
			page();
		}
	});// --.
	
	// 分页查询.
	function page(n, s) {
		
		// 单选按钮赋值.
	/* 	$("#projectProductType").val($('input[name="project_product_type"]:checked').val()); */
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		// 分页时，重新定义action.
		$("#searchForm").attr("action","${ctx}/data/search/lenderStatistics/");
		$("#searchForm").submit();
		return false;
	}
</script>
<style type="text/css">
.select2-chosen{width: 250px;}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active">出借人统计查询</li>
		<li class="btns">
			<input id="btnLenderExport" class="btn btn-primary" type="button" value="项目导出" />
		</li>
	</ul>

	<form:form id="searchForm" modelAttribute="lenderStatistics" action="${ctx}/data/search/lenderStatistics/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<%-- <ul class="ul-form">
			
			<li><label class="label">平台用户编号：</label>
				<form:input path="id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<!-- <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li> -->
			
		</ul> --%>
		<li class="clearfix" style="color:red">${message}</li>
	</form:form> 

	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>出借人Id</th>
				<th>出借人名称</th>
				<th>出借人手机号</th>
				<th>注册时间</th>
				<th>首次出借时间</th>
				<th>累计已收利息（元）</th>
				<th>年化收益率</th>
				<th>供应链待收余额（元）</th>
				<th>安心投待收余额（元）</th>
				<th>合计待收余额（元）</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="lenderStatistics">
		        <tr>
		        	<td>${lenderStatistics.lenderId}</td> 
		        	<td>${lenderStatistics.lenderName}</td> 
		        	<td>${lenderStatistics.lenderPhone}</td> 
			        <td>${lenderStatistics.registrationTime}</td>    
			        <td>${lenderStatistics.firstLendingTime}</td> 
			        <td>${lenderStatistics.totalInterestReceived}</td> 
			        <td>${lenderStatistics.annualizedRate}</td> 
			        <td>${lenderStatistics.gylBalance}</td> 
			        <td>${lenderStatistics.axtBalance}</td> 
			        <td>${lenderStatistics.totalBalance}</td>
		        </tr>
		   </c:forEach>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>