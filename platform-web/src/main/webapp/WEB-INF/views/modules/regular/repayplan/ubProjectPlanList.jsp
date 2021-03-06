<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>UB-还款计划【所有项目】</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#errmsg").hide();
		//查询
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action","${ctx}/wloanproject/wloanTermProjectPlan/projectPlanList");
			$("#searchForm").submit();
		});
		//导出
		$("#btnExport").click(function(){
			top.$.jBox.confirm("确认要导出供应链还款列表数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/wloanproject/wloanTermProjectPlan/export");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});
		//发送邮件
		$("#btnEmail").click(function(){
			var coreName = $("#coreName").val();
			if(coreName==""){
				$("#errmsg").show();
				return;
			}
			top.$.jBox.confirm("确认要发送邮件吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/wloanproject/wloanTermProjectPlan/sendEmail");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});
	});
	function page(n, s) {

		// 单选按钮赋值.
		$("#repayPlanRadioType").val($('input[name="repay_plan_type"]:checked').val());
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		// 分页时，重新定义action.
		$("#searchForm").attr("action","${ctx}/wloanproject/wloanTermProjectPlan/projectPlanList");
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
		<li><a href="${ctx}/wloanproject/wloanTermProject/creditUserLoanList/">供应链列表</a></li>
		<li class="active"><a href="${ctx}/wloanproject/wloanTermProjectPlan/projectPlanList">供应链还款【P-ALL】</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermProjectPlan" action="${ctx}/wloanproject/wloanTermProjectPlan/projectPlanList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form" id="errmsg">
			<label style="color:red;">*请选择核心企业</label>
		</ul>
		<ul class="ul-form">
			<label style="color:red;">*如需发送邮件，请先选择核心企业，并选择还款日期区间(起始日期需大于等于当前日期)</label>
		</ul>
		<ul class="ul-form">
			<li>
				<label class="label">核心企业：</label>
				<form:select path="wloanTermProject.replaceRepayId" id="coreName">
					<form:option value="" label="请选择"/>
					<c:forEach var="middlemen" items="${middlemenList}">
						<form:option value="${middlemen.id}" label="${middlemen.enterpriseFullName}" />
					</c:forEach>
				</form:select>
			</li>
			<li>
				<label class="label">还款类型：</label>
				<form:select path="principal">
					<form:option value="" label="请选择" />
					<form:option value="1" label="本息" />
					<form:option value="0" label="利息" />
				</form:select>
			</li>
			<li>
				<label class="label">还款日期：</label>
				<input name="beginRepaymentDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="开始日期"
					value="<fmt:formatDate value="${wloanTermProjectPlan.beginRepaymentDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/> -
				<input name="endRepaymentDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="结束日期"
					value="<fmt:formatDate value="${wloanTermProjectPlan.endRepaymentDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" />
				<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
				<input id="btnEmail" class="btn btn-primary" type="button" value="发送邮件"/>
			</li>
			<li>
				<form:hidden path="repayPlanRadioType"/>
				<label class="btn">还款中<input id="repay_plan_id" type="radio" name="repay_plan_type" value="1" checked="checked" onclick="return page();" /></label>
				<label class="btn">历史还款<input id="repay_plan_history_id" type="radio" name="repay_plan_type" value="2" onclick="return page();" /></label>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目名称</th>
				<th>融资主体</th>
				<th>项目编号</th>
				<th>放款金额</th>
				<th>还款金额</th>
				<th>还款类型</th>
				<th>还款日期</th>
				<th>约定放款日期</th>
				<th>实际放款日期</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanTermProjectPlan">
				<tr>
					<td><a href="${ctx}/wloanproject/wloanTermProjectPlan/findByProId?proid=${wloanTermProjectPlan.wloanTermProject.id}">${wloanTermProjectPlan.wloanTermProject.name}</a></td>
					<td>${wloanTermProjectPlan.wloanSubject.companyName}</td>
					<td>${wloanTermProjectPlan.wloanTermProject.sn}</td>
					<td>${wloanTermProjectPlan.wloanTermProject.currentAmount}</td>
					<td>${wloanTermProjectPlan.interest}</td>
					<td>${wloanTermProjectPlan.principal == '0' ? '利息' : '本息'}</td>
					<td><fmt:formatDate value="${wloanTermProjectPlan.repaymentDate}" pattern="yyyy-MM-dd" /></td>
					<td><fmt:formatDate value="${wloanTermProjectPlan.wloanTermProject.loanDate}" pattern="yyyy-MM-dd" /></td>
					<td><fmt:formatDate value="${wloanTermProjectPlan.wloanTermProject.realLoanDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><a href="${ctx}/wloanproject/wloanTermProjectPlan/findByProId?proid=${wloanTermProjectPlan.wloanTermProject.id}">还款</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>