<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>代偿还款订单历史管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		//
		$("#messageBox").show();
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active">
			<a href="${ctx}/replace/repay/ztmgReplaceRepayOrderHistory/">代偿历史</a>
		</li>
		<%-- <shiro:hasPermission name="replacerepay:orderhistory:ztmgReplaceRepayOrderHistory:edit">
			<li>
				<a href="${ctx}/replacerepay/orderhistory/ztmgReplaceRepayOrderHistory/form">代偿还款订单历史添加</a>
			</li>
		</shiro:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="ztmgReplaceRepayOrderHistory" action="${ctx}/replace/repay/ztmgReplaceRepayOrderHistory/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<input id="flag" name="flag" type="hidden" value="1" />
		<ul class="ul-form">
			<label style="color: red;font-size: 15px;">*&nbsp;代偿切换：【产品类型】切换，‘安心投类’切换为‘供应链类’。【是否代偿】切换，‘否’切换为‘是’。【代偿人】默认为，山西文之泉教育科技有限公司。</label>
		</ul>
		<ul class="ul-form">
			<li>
				<label class="label">项目编号：</label>
				<form:input path="projectSn" htmlEscape="false" maxlength="32" class="input-medium" />
			</li>
			<li>
				<label class="label">还款日期：</label>
				<input name="repayDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="还款日期" value="<fmt:formatDate value="${ztmgReplaceRepayOrderHistory.repayDate}" pattern="yyyy-MM-dd"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="代偿切换" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<form:form id="searchForm" modelAttribute="ztmgReplaceRepayOrderHistory" action="${ctx}/replace/repay/ztmgReplaceRepayOrderHistory/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<input id="flag" name="flag" type="hidden" value="0" />
		<ul class="ul-form">
			<label style="color: red;font-size: 15px;">*&nbsp;还款状态：【受理成功】既可以前往‘供应链还款’菜单页进行代偿还款。【成功】即‘安心头类产品’代偿还款成功。</label>
		</ul>
		<ul class="ul-form">
			<li><label class="label">项目编号：</label> <form:input path="proSn" htmlEscape="false" maxlength="32" class="input-medium" /></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目名称</th>
				<th>项目编号</th>
				<th>融资主体名称</th>
				<th>放款金额（元）</th>
				<th>还款金额（元）</th>
				<th>还款类型</th>
				<th>还款日期</th>
				<th>流标日期</th>
				<th>放款日期</th>
				<th>还款状态</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<th>备注</th>
				<%-- <shiro:hasPermission name="replacerepay:orderhistory:ztmgReplaceRepayOrderHistory:edit">
					<th>操作</th>
				</shiro:hasPermission> --%>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="ztmgReplaceRepayOrderHistory">
				<tr>
					<td>${ztmgReplaceRepayOrderHistory.proName}</td>
					<td>${ztmgReplaceRepayOrderHistory.proSn}</td>
					<td>${ztmgReplaceRepayOrderHistory.subName}</td>
					<td>${ztmgReplaceRepayOrderHistory.grantAmount}</td>
					<td>${ztmgReplaceRepayOrderHistory.repayAmount}</td>
					<td>
						<c:if test="${ztmgReplaceRepayOrderHistory.repayType == '0'}">
							<b>付息</b>
						</c:if>
						<c:if test="${ztmgReplaceRepayOrderHistory.repayType == '1'}">
							<b>还本付息</b>
						</c:if>
					</td>
					<td><fmt:formatDate value="${ztmgReplaceRepayOrderHistory.repayDate}" pattern="yyyy-MM-dd" /></td>
					<td><fmt:formatDate value="${ztmgReplaceRepayOrderHistory.cancelDate}" pattern="yyyy-MM-dd" /></td>
					<td><fmt:formatDate value="${ztmgReplaceRepayOrderHistory.grantDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>
						<c:if test="${ztmgReplaceRepayOrderHistory.status == 'S'}">
							<b style="color:green;">成功</b>
						</c:if>
						<c:if test="${ztmgReplaceRepayOrderHistory.status == 'AS'}">
							<b>受理成功</b>
						</c:if>
						<c:if test="${ztmgReplaceRepayOrderHistory.status == 'F'}">
							<b style="color:red;">失败</b>
						</c:if>
					</td>
					<td><fmt:formatDate value="${ztmgReplaceRepayOrderHistory.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${ztmgReplaceRepayOrderHistory.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${ztmgReplaceRepayOrderHistory.remark}</td>
					<%-- <shiro:hasPermission name="replacerepay:orderhistory:ztmgReplaceRepayOrderHistory:edit">
						<td>
							<a href="${ctx}/replacerepay/orderhistory/ztmgReplaceRepayOrderHistory/form?id=${ztmgReplaceRepayOrderHistory.id}">修改</a>
							<a href="${ctx}/replacerepay/orderhistory/ztmgReplaceRepayOrderHistory/delete?id=${ztmgReplaceRepayOrderHistory.id}" onclick="return confirmx('确认要删除该代偿还款订单历史吗？', this.href)">删除</a>
						</td>
					</shiro:hasPermission> --%>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>