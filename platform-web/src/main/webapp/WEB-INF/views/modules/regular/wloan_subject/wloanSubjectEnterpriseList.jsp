<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>融资主体管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		Init_Wloan_Subject_Choice();
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
	// 融资主体类型选择.
	function Init_Wloan_Subject_Choice() {
		$("input[type='radio']").click(function() {
			var id = $(this).attr("id");
			var type_value = $("#" + id).val();
			if (type_value == '1') {
				$("#personal_type").val(type_value);
				document.getElementById("searchTypeForm").action = "${ctx}/wloan_subject/wloanSubject/personalList";
				document.getElementById("searchTypeForm").submit();
			} else if (type_value == '2') {
				$("#personal_type").val(type_value);
				document.getElementById("searchTypeForm").action = "${ctx}/wloan_subject/wloanSubject/enterpriseList";
				document.getElementById("searchTypeForm").submit();
			}
		});
	}//--
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/wloan_subject/wloanSubject/">融资主体列表</a></li>
		<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
			<li><a href="${ctx}/wloan_subject/wloanSubject/addForm">融资主体添加</a></li>
		</shiro:hasPermission>
	</ul>
	<!-- 查询. -->
	<form:form id="searchForm" modelAttribute="wloanSubject" action="${ctx}/wloan_subject/wloanSubject/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<label>名称：</label>
		<form:input path="companyName" htmlEscape="false" maxlength="255" class="input-medium" />
		<label>收款人：</label>
		<form:input path="cashierUser" htmlEscape="false" maxlength="64" class="input-medium" />
		<label>借款人：</label>
		<form:input path="loanUser" htmlEscape="false" maxlength="64" class="input-medium" />
		<li><label>借款人手机：</label> <form:input path="loanPhone" htmlEscape="false" maxlength="64" class="input-medium" /></li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
		<!-- 融资主体类别. -->
		<label>状态：</label>
		<form:radiobuttons onclick="$('#searchForm').submit();" path="type" items="${fns:getDictList('wloan_subject_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<th>类型</th>
				<th>收款人</th>
				<th>联系电话</th>
				<th>营业执照</th>
				<th>税务登记号</th>
				<th>注册资金(RMB)</th>
				<th>资产净值(RMB)</th>
				<th>上年现金流量(RMB)</th>
				<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanSubject">
				<tr>
					<td><a href="${ctx}/wloan_subject/wloanSubject/viewForm?id=${wloanSubject.id}"> ${wloanSubject.companyName} </a></td>
					<td>${fns:getDictLabel(wloanSubject.type, 'wloan_subject_type', '')}</td>
					<td>${wloanSubject.cashierUser}</td>
					<td>${wloanSubject.loanPhone}</td>
					<td>${wloanSubject.businessNo}</td>
					<td>${wloanSubject.taxCode}</td>
					<td>${wloanSubject.registerAmount}</td>
					<td>${wloanSubject.netAssetAmount}</td>
					<td>${wloanSubject.lastYearCash}</td>
					<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
						<td><a href="${ctx}/wloan_subject/wloanSubject/updateForm?id=${wloanSubject.id}">修改</a> <a href="${ctx}/wloan_subject/wloanSubject/delete?id=${wloanSubject.id}" onclick="return confirmx('确认要删除该融资主体吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>