<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>投资记录管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		// 查询.
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action", "${ctx}/statistical/detailList");
			$("#searchForm").submit();
		});
		//导出
		$("#btnUserInfoExport").click(function(){
			top.$.jBox.confirm("确认要导出运营数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/statistical/export");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").attr("action","${ctx}/statistical/detailList");
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/statistical/">运营数据统计</a></li>
		<li class="active"><a href="#">运营详情统计</a></li>
	</ul>
		<form:form id="searchForm" modelAttribute="wloanTermInvest" action="${ctx}/statistical/detailList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
		<li><label>投资日期：</label> <input placeholder="开始日期" id="beginBeginDate" name="beginBeginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.beginBeginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" id="endBeginDate" name="endBeginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.endBeginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
		<li>
			<label>用户类型：</label> 
			<form:select id="userFlag" path="userFlag" class="input-medium">
				<form:options items="${fns:getDictList('investment_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</li>
		<li>
			<label>投资金额：</label> 
			<form:select id="amount" path="amount" class="input-medium">
				<form:option value="" label="全部" />
				<form:option value="50000" label="大于50000" />
			</form:select>
		</li>
		</ul>
	    <ul class="ul-form">
	    <li><label>注册日期：</label> <input placeholder="开始日期" id="userInfo.beginRegisterDate" name="userInfo.beginRegisterDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.userInfo.beginRegisterDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" id="userInfo.beginRegisterDate" name="userInfo.endRegisterDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermInvest.userInfo.endRegisterDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
		<li><label>渠道来源：</label>
			<form:input path="partnerForm.platformName" htmlEscape="false" maxlength="32" class="input-medium"/>
		</li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="查询" /></li>
		<li class="btns"><input id="btnUserInfoExport" class="btn btn-primary" type="button" value="导出" /></li>
	    </ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>投资人手机</th>
				<th>投资人姓名</th>
				<th>投资日期</th>
				<th>投资金额(RMB)</th>
				<th>投资期限(天)</th>
				<th>项目名称</th>
				<th>用户类型</th>
				<th>注册日期</th>
				<th>渠道来源</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list}" var="wloanTermInvest">
				<tr>
					<td>${wloanTermInvest.userInfo.name}</td>
					<td>${wloanTermInvest.userInfo.realName}</td>
					<td><fmt:formatDate value="${wloanTermInvest.beginDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${wloanTermInvest.amount}</td>
					<td>${wloanTermInvest.wloanTermProject.span}</td>
					<td>${wloanTermInvest.wloanTermProject.name}</td>
					<td>${userType}</td>
					<td><fmt:formatDate value="${wloanTermInvest.userInfo.registerDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${wloanTermInvest.partnerForm.platformName}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>