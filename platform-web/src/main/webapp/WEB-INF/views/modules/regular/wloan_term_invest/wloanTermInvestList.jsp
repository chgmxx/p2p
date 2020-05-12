<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>投资记录管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action","${ctx}/wloan_term_invest/wloanTermInvest/");
			$("#searchForm").submit();
		});
		// --
		$("#btnExport").click(function(){
			top.$.jBox.confirm("确认要导出投资列表数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/wloan_term_invest/wloanTermInvest/export");
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
		$("#searchForm").attr("action","${ctx}/wloan_term_invest/wloanTermInvest/");
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/wloan_term_invest/wloanTermInvest/">投资记录列表</a></li>
		<shiro:hasPermission name="wloan_term_invest:wloanTermInvest:edit">
			<li><a href="${ctx}/wloan_term_invest/wloanTermInvest/addForm">投资记录添加</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermInvest" action="${ctx}/wloan_term_invest/wloanTermInvest/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<li><label>投资日期：</label> <input placeholder="开始日期"  name="beginBeginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.beginBeginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" name="endBeginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.endBeginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
		<li><label>投资人名称：</label> <form:input path="userInfo.realName" htmlEscape="false" maxlength="64" class="input-medium" /></li>
		<li><label>投资人手机：</label> <form:input path="userInfo.name" htmlEscape="false" maxlength="64" class="input-medium" /></li>
		<li><label>项目名称：</label> <form:input path="wloanTermProject.name" htmlEscape="false" maxlength="64" class="input-medium" /></li>
		<li><label>投资状态：</label> <form:select path="wloanTermProject.stateItem" class="input-medium">
					<form:option value="" label="请选择" />
					<form:option value="4" label="投资中" />
					<form:option value="5" label="满标中" />
					<form:option value="6" label="还款中" />
					<form:option value="7" label="已结束" />
				</form:select></li>
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
				<th>投资人手机</th>
				<th>投资人名称</th>
				<th>项目名称(项目编号)</th>
				<th>项目期限</th>
				<th>项目状态</th>
				<th>金额(RMB)</th>
				<th>利息(RMB)</th>
				<th>投资日期</th>
				<!-- <th>状态</th> -->
				<th>抵用券金额(RMB)</th>
				<th>还款计划</th>
				<shiro:hasPermission name="wloan_term_invest:wloanTermInvest:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanTermInvest">
				<tr>
				    <td>${wloanTermInvest.id}</td>
					<td><a href="${ctx}/wloan_term_invest/wloanTermInvest/viewForm?id=${wloanTermInvest.id}">${wloanTermInvest.userInfo.name}</a></td>
					<td>${wloanTermInvest.userInfo.realName}</td>
					<td>${wloanTermInvest.wloanTermProject.name}<b>(${wloanTermInvest.wloanTermProject.sn})</b></td>
					<td>${wloanTermInvest.wloanTermProject.span}</td>
					<td>${ wloanTermInvest.wloanTermProject.state == '4'? '投资中' : (wloanTermInvest.wloanTermProject.state == '5'?'满标中':(wloanTermInvest.wloanTermProject.state == '6' ? '还款中' : '已结束'))}</td>
					<td>${wloanTermInvest.amount}</td>
					<td>${wloanTermInvest.interest}</td>
					<td><fmt:formatDate value="${wloanTermInvest.beginDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<%-- <td>${fns:getDictLabel(wloanTermInvest.state, 'wloan_term_invest_state', '')}</td> --%>
					<td>${wloanTermInvest.voucherAmount}</td>
					<td><a href="${ctx}/wloan_term_invest/wloanTermInvest/getInvestRepayPlan?id=${wloanTermInvest.id}">查看</a></td>
					<shiro:hasPermission name="wloan_term_invest:wloanTermInvest:edit">
						<td><a href="${ctx}/wloan_term_invest/wloanTermInvest/updateForm?id=${wloanTermInvest.id}">修改</a> <a href="${ctx}/wloan_term_invest/wloanTermInvest/delete?id=${wloanTermInvest.id}" onclick="return confirmx('确认要删除该投资记录吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>