<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>充值</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action","${ctx}/report/recharge");
			$("#searchForm").submit();
		});
		$("#btnExport").click(function(){
			top.$.jBox.confirm("确认要导出充值数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/report/recharge/export");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});
	});
	
	// 回车键上抬.	
	$(document).keyup(function(event){
		if(event.keyCode ==13){
			page();
		}
	});// --.
	
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		// 分页时，重新定义action.
		$("#searchForm").attr("action","${ctx}/report/recharge");
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/report/recharge">充值列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="userRecharge" action="${ctx}/report/recharge" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<li><label class="label">充值时间：</label> <input placeholder="开始日期" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${userRecharge.beginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${userRecharge.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
		<li><label class="label">手机号码：</label> <form:input path="phone" placeholder="手机号码" htmlEscape="false" maxlength="11" class="input-medium" /></li>
		<li><label class="label">状态：</label>
			<form:select path="state" class="input-large" id="">
				<form:option value="" label="请选择" />
				<form:options items="${fns:getDictList('user_recharge_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</li>
		<li class="btns">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" />
			<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
		</li>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户类别</th>
				<th>手机号码</th>
				<th>姓名</th>
				<th>订单号</th>
				<th>银行卡号</th>
				<th>充值时间</th>
				<th>金额(元)</th>
				<th>状态</th>
				<th>充值方式/来源</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="userRecharge">
				<tr>
					<td>${userRecharge.userTypeStr}</td>
					<td>${userRecharge.userInfo.name}</td>
					<td>${userRecharge.userInfo.realName}</td>
					<td>${userRecharge.sn}</td>
					<td>${userRecharge.bankAccount}</td>
					<td><fmt:formatDate value="${userRecharge.beginDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${userRecharge.formatAmountStr}</td>
					<td><c:if test="${userRecharge.state == '1'}"><b>初始化</b></c:if><c:if test="${userRecharge.state == '2'}"><b>申请中</b></c:if><c:if test="${userRecharge.state == '3'}"><b>成功</b></c:if><c:if test="${userRecharge.state == '4'}"><b>失败</b></c:if></td>
					<td>
						<c:if test="${userRecharge.platForm == '0'}"><b>网银充值</b></c:if>
						<c:if test="${userRecharge.platForm == '1'}"><b>认证充值</b></c:if>
						<c:if test="${userRecharge.platForm == '2'}"><b>WAP充值</b></c:if>
						<c:if test="${userRecharge.platForm == '3'}"><b>ANDROID充值</b></c:if>
						<c:if test="${userRecharge.platForm == '4'}"><b>IOS充值</b></c:if>
						<c:if test="${userRecharge.platForm == '5'}"><b>大额充值</b></c:if>
						<c:if test="${userRecharge.platForm == '6'}"><b>转账充值</b></c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>