<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>投资汇总列表</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action","${ctx}/report/invest");
			$("#searchForm").submit();
		});
		$("#btnExport").click(function(){
			top.$.jBox.confirm("确认要导出提现数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/report/invest/export");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});
		$("#inOutCount").click(function(){
			
 			window.location.href="https://www.cicmorgan.com/echarts/doc/example/event.html";
			/* window.location.href="http://192.168.1.29:8020/cic-pc2.0/echarts/doc/example/event.html"; */
		});
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		// 分页时，重新定义action.
		$("#searchForm").attr("action","${ctx}/report/invest/");
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/report/invest">投资汇总列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermInvest" action="${ctx}/report/invest" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<li><label>投资日期：</label> <input placeholder="开始日期" name="beginBeginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.beginBeginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" name="endEndDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.endEndDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
		<li class="btns">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" />
			<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
			<input id="inOutCount" class="btn btn-primary" type="button" value="资金净流入"/>
		</li>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>投资人手机</th>
				<th>投资人名称</th>
				<th>满标日期</th>
				<th>项目编号</th>
				<th>项目名称</th>
				<th>融资主体</th>
				<th>年利率</th>
				<th>融资期限</th>
				<th>到期日期</th>
				<th>投资日期</th>
				<th>融资金额</th>
				<th>投资金额</th>
				<th>应付本金</th>
				<th>应付利息</th>
				<th>备注</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanTermInvest">
				<tr>
					<td>${wloanTermInvest.userInfo.name}</td>
					<td>${wloanTermInvest.userInfo.realName}</td>
					<td><fmt:formatDate value="${wloanTermInvest.wloanTermProject.fullDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${wloanTermInvest.wloanTermProject.sn}</td>
					<td>${wloanTermInvest.wloanTermProject.name}</td>
					<td>${wloanTermInvest.wloanSubject.companyName}</td>
					<td>${wloanTermInvest.wloanTermProject.annualRate}</td>
					<td>${wloanTermInvest.wloanTermProject.span}</td>
					<td><fmt:formatDate value="${wloanTermInvest.wloanTermProject.endDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${wloanTermInvest.beginDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${wloanTermInvest.wloanTermProject.amount}</td>
					<td>${wloanTermInvest.amount}</td>
					<td>${wloanTermInvest.amount}</td>
					<td>${wloanTermInvest.interest}</td>
					<th><b style="color: red;">投资汇总</b></th>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>