<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>投资记录管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
	});

	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}

	/**
	 * 描述: 详情统计. <br>
	 * 作者: Mr.云.李 <br>
	 */
	function statisticalDetailList(span) {
		// console.log("详情统计. . .");
		var beginBeginDate = $('#beginBeginDate').val();
		var endBeginDate = $('#endBeginDate').val();
		var userFlag = $('#userFlag').val();
		var amount = $('#amount').val();
		if(span != 0 && span != '0'){
			window.location.href = "${ctx}/statistical/detailList?userFlag="
				+ userFlag + "&beginBeginDate=" + beginBeginDate
				+ "&endBeginDate=" + endBeginDate + "&amount=" + amount+ "&wloanTermProject.span=" + span;
		}else{
			window.location.href = "${ctx}/statistical/detailList?userFlag="
				+ userFlag + "&beginBeginDate=" + beginBeginDate
				+ "&endBeginDate=" + endBeginDate + "&amount=" + amount;
		}
	}
	 
	 
	 /**
		 * 描述: 充值统计. <br>
		 * 作者: Mr.云.李 <br>
		 */
		function statisticalRechargeList() {
			// console.log("详情统计. . .");
			var beginBeginDate = $('#beginBeginDate').val();
			var endBeginDate = $('#endBeginDate').val();
			var userFlag = $('#userFlag').val();
			var amount = $('#amount').val();
			window.location.href = "${ctx}/statistical/rechargeList?beginBeginDate=" + beginBeginDate
					+ "&endBeginDate=" + endBeginDate;
		}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/statistical/">运营数据统计</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermInvest" action="${ctx}/statistical/statistical_data_info" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<li><label>投资日期：</label> <input placeholder="开始日期" id="beginBeginDate" name="beginBeginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.beginBeginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" id="endBeginDate" name="endBeginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.endBeginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
		<li><label>用户类型：</label> <form:select id="userFlag" path="userFlag" class="input-medium">
				<form:options items="${fns:getDictList('investment_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select></li>
		<li><label>投资金额：</label> <form:select id="amount" path="amount" class="input-medium">
				<form:option value="" label="全部" />
				<form:option value="50000" label="大于50000" />
			</form:select></li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
	</form:form>
	<sys:message content="注：时间区间默认查询为当前日，比如：今天是2016年8月15号，时间区间为，开始日期：2016-08-15 00:00:00，结束日期：2016-08-15 23:59:59" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>融资总额(元)</th>
				<th>投资(人次/人)</th>
				<th>30天(元/人次/人)</th>
				<th>90天(元/人次/人)</th>
				<th>180天(元/人次/人)</th>
				<th>360天(元/人次/人)</th>
				<th>注册人数(人)</th>
				<th>用户类型</th>
				<th>人均投资额(元)</th>
				<th>客户充值总额(元)</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td><a href="#" onclick="statisticalDetailList(0);">${financingAmount}元</a></td>
				<td>${investCount}人次/${investPeopleCount}人</td>
				<td><a href="#" onclick="statisticalDetailList(30);">${thirtyDaysFinancingAmount}元 / ${thirtyDaysCount}人次 / ${thirtyDaysPeopleCount}人</a></td>
				<td><a href="#" onclick="statisticalDetailList(90);">${ninetyDaysFinancingAmount}元 / ${ninetyDaysCount}人次 / ${ninetyDaysPeopleCount}人</a></td>
				<td><a href="#" onclick="statisticalDetailList(180);">${oneHundredAndEightyDaysFinancingAmount}元 / ${oneHundredAndEightyDaysCount}人次 / ${oneHundredAndEightyDaysPeopleCount}人</a></td>
				<td><a href="#" onclick="statisticalDetailList(360);">${threeHundredAndSixtyDaysFinancingAmount}元 / ${threeHundredAndSixtyDaysCount}人次 / ${threeHundredAndSixtyDaysPeopleCount}人</a></td>
				<td>${registeredCount}人</td>
				<td>${userType}</td>
				<td>${perCapitaAmount}元</td>
				<td><a href="#" onclick="statisticalRechargeList();">${userRechargeTotalAmount}元</a></td>
				<td><a href="#" onclick="statisticalDetailList(0);">查看</a></td>
			</tr>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>