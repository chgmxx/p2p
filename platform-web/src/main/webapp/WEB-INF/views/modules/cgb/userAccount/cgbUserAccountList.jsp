<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>银行托管-账户管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#messageBox").show();
		
		// 查询.
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action", "${ctx}/cgb/cgbUserAccount/");
			$("#searchForm").submit();
		});
		//导出
		$("#btnExport").click(function(){
			top.$.jBox.confirm("确认要导出账户数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/cgb/cgbUserAccount/export");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").attr("action","${ctx}/cgb/cgbUserAccount/");
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/cgb/cgbUserAccount/">账户列表</a></li>
		<!-- 
		<shiro:hasPermission name="cgb:cgbUserAccount:edit">
			<li><a href="${ctx}/cgb/cgbUserAccount/form">账户添加</a></li>
		</shiro:hasPermission>
		 -->
	</ul>
	<form:form id="searchForm" modelAttribute="cgbUserAccount" action="${ctx}/cgb/cgbUserAccount/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label class="label">帐号：</label> <form:input path="userInfo.name" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label class="label">姓名：</label> <form:input path="userInfo.realName" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label class="label">余额：</label>
				<form:select path="canUseAmount" class="input-medium">
					<form:option value="" label="请选择" />
					<form:option value="0" label="可用余额为0" />
					<form:option value="1" label="可用余额不为0" />
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="查询" /></li>
			<li class="btns"><input id="btnExport" class="btn btn-primary" type="button" value="导出" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>帐号</th>
				<th>姓名</th>
				<th>账户总额</th>
				<th>可用余额</th>
				<th>冻结金额</th>
				<th>充值金额</th>
				<th>充值次数</th>
				<th>提现金额</th>
				<th>提现次数</th>
				<th>定期投资总额</th>
				<th>定期待收本金</th>
				<th>定期待收收益</th>
				<th>定期累计收益</th>
				<th>定期昨日收益</th>
				<th>总收益</th>
				<th>佣金</th>
				
				<shiro:hasPermission name="cgb:cgbUserAccount:edit">
					<th>操作</th>
				</shiro:hasPermission>
				 
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="cgbUserAccount">
				<tr>
					<td>${cgbUserAccount.userInfo.name}</td>
					<td>${cgbUserAccount.userInfo.realName}</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.totalAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.availableAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.freezeAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.rechargeAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.rechargeCount}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.cashAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.cashCount}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.regularTotalAmountStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.regularDuePrincipalStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.regularDueInterestStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.regularTotalInterestStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.reguarYesterdayInterestStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.totalInterestStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					<td>
						<fmt:formatNumber type="number" value="${cgbUserAccount.commissionStr}" minFractionDigits="2" maxFractionDigits="2" />
					</td>
					
					<shiro:hasPermission name="cgb:cgbUserAccount:edit">
						<td><%-- <a href="${ctx}/cgb/cgbUserAccount/checkAmount?userId=${cgbUserAccount.userInfo.id}">存管宝查询</a> --%></td>
					</shiro:hasPermission>
					
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>