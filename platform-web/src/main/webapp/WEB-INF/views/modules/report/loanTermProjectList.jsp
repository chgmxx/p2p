<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>项目放款</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(
		function() {
		$("#btnSubmit").click(
		function() {
			$("#searchForm").attr("action", "${ctx}/report/loanTermProject");
			$("#searchForm").submit();
		});
		$("#btnExport").click(
		function() {
			top.$.jBox.confirm("确认要导出提现数据吗？", "系统提示",
			function(v, h, f) {
				if (v == "ok") {
					$("#searchForm").attr("action", "${ctx}/report/loanTermProject/export");
					$("#searchForm").submit();
				}
			},{buttonsFocus : 1});
			top.$('.jbox-body .jbox-icon').css('top', '55px');
		});
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		// 分页时，重新定义action.
		$("#searchForm").attr("action","${ctx}/report/loanTermProject/");
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/report/loanTermProject">项目放款列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermProject" action="${ctx}/report/loanTermProject" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<li><label>放款日期：</label> <input placeholder="开始日期" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermProject.beginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermProject.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
		<li>
			<label>状态：</label>
			<form:select path="state" class="input-medium">
				<form:option value="" label="请选择" />
				<form:options items="${fns:getDictList('wloan_term_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="查询" /> <input id="btnExport" class="btn btn-primary" type="button" value="导出" /></li>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>放款日期</th>
				<th>实际放款日期</th>
				<th>到期日期</th>
				<th>项目编号</th>
				<th>担保公司</th>
				<th>项目名称</th>
				<th>融资主体名称</th>
				<th>借款人</th>
				<th>项目期限</th>
				<th>融资金额</th>
				<th>服务费</th>
				<th>保证金</th>
				<th>状态</th>
				<th>备注</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanTermProject">
				<tr>
					<td><fmt:formatDate value="${wloanTermProject.loanDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${wloanTermProject.realLoanDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${wloanTermProject.endDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${wloanTermProject.sn}</td>
					<td>${wloanTermProject.wgCompany.name}</td>
					<td>${wloanTermProject.name}</td>
					<td>${wloanTermProject.wloanSubject.companyName}</td>
					<td>${wloanTermProject.wloanSubject.loanUser}</td>
					<td>${wloanTermProject.span}</td>
					<td>${wloanTermProject.amount}</td>
					<td>${wloanTermProject.feeRate}</td>
					<td>${wloanTermProject.marginPercentage}</td>
					<td>${fns:getDictLabel(wloanTermProject.state, 'wloan_term_state', '')}</td>
					<th><b style="color: red;">项目放款</b></th>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>