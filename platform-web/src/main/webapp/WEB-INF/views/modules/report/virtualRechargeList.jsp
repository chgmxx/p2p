<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>虚拟充值</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action","${ctx}/report/virtualRecharge/");
			$("#searchForm").submit();
		});
		$("#btnExport").click(function(){
			top.$.jBox.confirm("确认要导出虚拟充值数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/report/virtualRecharge/export");
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
		$("#searchForm").attr("action","${ctx}/report/virtualRecharge/");
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/report/virtualRecharge/">虚拟充值列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="ztmgWechatReturningCash" action="${ctx}/report/virtualRecharge/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<li><label>虚拟充值日期：</label> <input placeholder="开始日期" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${ztmgWechatReturningCash.beginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${ztmgWechatReturningCash.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
		<li class="btns">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" />
			<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
		</li>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>订单号</th>
				<th>用户名</th>
				<th>姓名</th>
				<th>金额(元)</th>
				<th>充值时间</th>
				<th>备注</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="ztmgWechatReturningCash">
				<tr>
					<td>${ztmgWechatReturningCash.id}</td>
					<td>${ztmgWechatReturningCash.mobilePhone}</td>
					<td>${ztmgWechatReturningCash.realName}</td>
					<td>${ztmgWechatReturningCash.payAmount}</td>
					<td><fmt:formatDate value="${ztmgWechatReturningCash.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<th><b style="color: red;">虚拟充值</b></th>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>