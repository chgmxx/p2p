<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>UB-还款计划【客户还款计划】</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" >
		$(document).ready(function() {
			//
			$("#messageBox").show();
			//
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">账户待结清</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermUserPlan" action="${ctx}/wloanproject/wloanTermProjectPlan/expireUserPlanList" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li>
				<label class="label">结清自然日：</label>
				<form:select path="dayI" class="input-medium-select" cssStyle="width:200px;">
					<form:option value="15" label="15个自然日" />
					<form:option value="13" label="13个自然日" />
					<form:option value="11" label="11个自然日" />
					<form:option value="9" label="9个自然日" />
					<form:option value="7" label="7个自然日" />
					<form:option value="5" label="5个自然日" />
					<form:option value="3" label="3个自然日" />
					<form:option value="1" label="1个自然日" />
				</form:select>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" />
			</li>
			<li class="clearfix"></li>
		</ul>
		<label class="label">默认查询结果为15个自然日，账户待结清的客户列表...</label>
	</form:form>

	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>出借用户</th>
				<th>出借帐号</th>
				<th>待还本息</th>
				<th>最后还款日期</th>
				<th>还款状态</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${wloanTermUserPlanList}" var="wloanTermUserPlan">
			<tr>
				<td>
					${wloanTermUserPlan.userInfo.realName}
				</td>
				<td>
					${wloanTermUserPlan.userInfo.name}
				</td>
				<td>
					${wloanTermUserPlan.interest}
				</td>
				<td>
					<fmt:formatDate value="${wloanTermUserPlan.repaymentDate}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					<b>未还款</b>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>