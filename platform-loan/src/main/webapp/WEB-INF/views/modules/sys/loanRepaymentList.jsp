<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>项目列表</title>
	<meta name="decorator" content="default"/>
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sys/user/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
		});
/* 		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
// 			$("#searchForm").attr("action","${ctx}/apply/creditUserApply/loanCreditUserApplyList?id=${id}");
			$("#searchForm").submit();
	    	return false;
	    } */
	</script>
	<style>

	 
  .table-condensed th, .table-condensed td {
  padding: 12px 5px!important;
       text-align: center!important;
}
.breadcrumb{
    padding: 0!important;
}

	</style>
</head>
<body>
	
	<div class="nav_head"><a href="${ctx}/sys/user/repayment">还款计划</a></div>
	<form:form id="searchForm" modelAttribute="user" action="" method="post" class="breadcrumb form-search ">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		
	</form:form>
	<sys:message content="${message}"/>
	<div class="padding_wrap">
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>项目名称</th><th>还款金额(元)</th><th>期数</th><th>时间</th><th>项目状态</th></tr></thead>
		<tbody>
		<c:forEach items="${repayList}" var="repayment">
			<tr>
				<td>${repayment.projectName}</td>
				<td>${repayment.repayAmount}</td>
				<td>${repayment.periods}</td>
				<td>${repayment.repayDate}</td>
				<c:if test="${repayment.planState == '1'}">
					<td>未还款</td>
				</c:if>
				<c:if test="${repayment.planState == '2'}">
					<td>还款成功</td>
				</c:if>
				<c:if test="${repayment.planState == '3'}">
					<td>还款失败</td>
				</c:if>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	</div>
</body>
</html>