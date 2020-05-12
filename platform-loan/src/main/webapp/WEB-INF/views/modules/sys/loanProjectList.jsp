<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>项目列表</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
	<script type="text/javascript">
	// 核心企业ID.
	var creditUserId = "${creditUserId}";
	// 供应商ID.
	var creditSupplyId = "${creditSupplyId}";
		$(document).ready(function() {
			$("#btnExport").click(function(){
				var r = confirm("确认要导出用户数据吗？")				
					if(r){
						if(creditSupplyId != ""){
						$("#searchForm").attr("action","${ctx}/apply/creditUserApply/exportCreditUserApplyList?creditSupplyId=" + creditSupplyId);
						$("#searchForm").submit();
						//$("#searchForm").attr("action","${ctx}/apply/creditUserApply/loanCreditUserApplyList?creditUserId=${creditUserId}");
						}else if(creditUserId != null){
							$("#searchForm").attr("action","${ctx}/apply/creditUserApply/exportCreditUserApplyList?creditUserId=${creditUserId}");
							$("#searchForm").submit();
						}
					}		
			});
			$("#btnSubmit").click(function(){			
				//$("#searchForm").attr("action","${ctx}/apply/creditUserApply/exportCreditUserApplyList?creditUserId=${creditUserId}");						
				if(creditUserId != null){
		 			$("#searchForm").attr("action","${ctx}/apply/creditUserApply/loanCreditUserApplyList?creditUserId=${creditUserId}");
					}else{
					$("#searchForm").attr("action","${ctx}/apply/creditUserApply/loanCreditUserApplyList?creditSupplyId=${creditSupplyId}");	
					}
				$("#searchForm").submit();
			});
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
		});
		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
			if(creditUserId != null){
 			$("#searchForm").attr("action","${ctx}/apply/creditUserApply/loanCreditUserApplyList?creditUserId=${creditUserId}");
			}else{
			$("#searchForm").attr("action","${ctx}/apply/creditUserApply/loanCreditUserApplyList?creditSupplyId=${creditSupplyId}");	
			}
			$("#searchForm").submit();
	    	return false;
	    }
	</script>
	<style>
	 .table-condensed th, .table-condensed td {
	  padding: 12px 2px!important;
	       text-align: center!important;
	}
	.padding_wrap {
	    padding: 8px 15px;
	}
	.form-search .ul-form li label{
	    color: #666;
	    font-weight: normal;
	}
	.ul-form li:nth-of-type(1) input{
	    padding: 6px;
	    box-sizing: border-box;
	    height: 28px;
	}
	.ul-form li:nth-of-type(2) input{
	    padding: 6px;
	    width: 233px;
	    box-sizing: border-box;
	    height: 32px;
	}
		#contentTable td{
	width:auto;
	}
	#contentTable td:nth-of-type(1){
	 padding-left:4px!important;
	 padding-right:4px!important;
	}	
	#contentTable td:nth-of-type(7){
    width: 72px;
    padding: 0;
    padding-left: 0!important;
    padding-right: 0!important;
	}
	#contentTable td:nth-of-type(1) a{
	 width:80px;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    display: block;
	}

	</style>
</head>
<body>
	
	<div class="nav_head"><a href="${ctx}/sys/user/list">项目列表</a></div>
	<form:form id="searchForm" modelAttribute="wloanTermProject" action="${ctx}/apply/creditUserApply/loanCreditUserApplyList?creditUserId=${creditUserId}" method="post" class="breadcrumb form-search ">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<ul class="ul-form">
			<li><label>需求编号：</label>
				<form:input path="creditUserApply.creditApplyName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label >项目编号：</label>
				<form:input path="sn" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>			
			<li><label>放款日期：</label>
				<input name="beginRealLoanDate" id="d4311" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="开始日期"
					value="<fmt:formatDate value="${wloanTermProject.beginRealLoanDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,maxDate:'#F{$dp.$D(\'d4312\',{d:-1})}'});"/>-			
				<input name="endRealLoanDate" id="d4312" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="结束日期"
					value="<fmt:formatDate value="${wloanTermProject.endRealLoanDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,minDate:'#F{$dp.$D(\'d4311\',{d:1})}'});"/>
			</li>
			<li><label>融资方：</label>
				<form:input path="wloanSubject.companyName" htmlEscape="false" maxlength="64" class="input-medium" style="width:200px"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			<input id="btnExport" class="btn btn-primary" type="submit" value="导出"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
		
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>需求编号</th><th>项目编号</th><th>融资类型</th><th>融资方</th><th>还款方</th><th>融资金额（元）</th> <th>放款日期</th><th>状态</th><th>操作</th><th>还款计划</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="wloanTermProject">
			<tr>
				<td><a href="javascript:;" title="${wloanTermProject.creditUserApplyName}">${wloanTermProject.creditUserApplyName}</a></td>
				<td>${wloanTermProject.sn}</td>
				<td>应收账款转让</td>
				<td>${wloanTermProject.wloanSubject.companyName}</td>
				<td>${wloanTermProject.replaceRepayName}</td>
				<td>${wloanTermProject.amount}</td>
				<td><fmt:formatDate value="${wloanTermProject.realLoanDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<c:if test="${wloanTermProject.state == '0'}">
					<td>已撤销</td>
				</c:if>
				<c:if test="${wloanTermProject.state == '1'}">
					<td>草稿</td>
				</c:if>
				<c:if test="${wloanTermProject.state == '2'}">
					<td>审核中</td>
				</c:if>
				<c:if test="${wloanTermProject.state == '3'}">
					<td>发布中</td>
				</c:if>
				<c:if test="${wloanTermProject.state == '4'}">
					<td>投标中</td>
				</c:if>
				<c:if test="${wloanTermProject.state == '5'}">
					<td>已满标</td>
				</c:if>
				<c:if test="${wloanTermProject.state == '6'}">
					<td>还款中</td>
				</c:if>
				<c:if test="${wloanTermProject.state == '7'}">
					<td>已结束</td>
				</c:if>
				<c:if test="${wloanTermProject.state == '8'}">
					<td>流标</td>
				</c:if>
				<c:if test="${wloanTermProject.projectProductType == '1'}">
					<td><a href="${ctxURL}/invest_details.html?id=${wloanTermProject.id}" target="_blank">查看</a></td>
				</c:if>
				<c:if test="${wloanTermProject.projectProductType == '2'}">
					<td><a href="${ctxURL}/invest_details.html?id=${wloanTermProject.id}" target="_blank">查看</a></td>
				</c:if>
				<td><a href="${ctx}/sys/user/repayment?id=${wloanTermProject.id}">还款计划</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>

</body>
</html>