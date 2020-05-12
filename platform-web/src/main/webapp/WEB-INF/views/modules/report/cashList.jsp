<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>提现</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action","${ctx}/report/cash/");
			$("#searchForm").submit();
		});
		$("#btnExport").click(function(){
			top.$.jBox.confirm("确认要导出提现数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/report/cash/export");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		// 分页时，重新定义action.
		$("#searchForm").attr("action","${ctx}/report/cash/");
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/report/cash">提现列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="userCash" action="${ctx}/report/cash" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<li><label>提现日期：</label> <input placeholder="开始日期" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${userCash.beginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${userCash.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
		<li class="btns">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" />
			<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
		</li>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户名</th>
				<th>姓名</th>
				<th>订单号</th>
				<th>提现时间</th>
				<th>金额(元)</th>
				<th>手续费(元)</th>
				<th>备注</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="userCash">
				<tr>
					<td>${userCash.userInfo.name}</td>
					<td>${userCash.userInfo.realName}</td>
					<td>${userCash.sn}</td>
					<td><fmt:formatDate value="${userCash.beginDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${userCash.amount}</td>
					<td>${userCash.feeAmount}</td>
					<th><b style="color: red;">提现</b></th>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>