<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>融资主体-企业列表</title>
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
		<li class="active"><a href="${ctx}/wloan_subject/wloanSubject/cgbPersonalList">主体列表</a></li>
		<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
			<li><a href="${ctx}/wloan_subject/wloanSubject/cgbWloanSubjectForm">主体添加</a></li>
		</shiro:hasPermission>
	</ul>
	<!-- 查询. -->
	<form:form id="searchForm" modelAttribute="wloanSubject" action="${ctx}/wloan_subject/wloanSubject/cgbPersonalList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<label class="label">名称：</label>
		<form:input path="companyName" htmlEscape="false" maxlength="255" class="input-medium" />
		<label class="label">受托人：</label>
		<form:input path="cashierUser" htmlEscape="false" maxlength="64" class="input-medium" />
		<label class="label">借款人：</label>
		<form:input path="loanUser" htmlEscape="false" maxlength="64" class="input-medium" />
		<li>
			<label class="label">移动电话：</label>
			<form:input path="loanPhone" htmlEscape="false" maxlength="64" class="input-medium" /></li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
		<!-- 融资主体类别. -->
		<label class="label">状态：</label>
		<form:radiobuttons onclick="$('#searchForm').submit();" path="type" items="${fns:getDictList('wloan_subject_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<th>类型</th>
				<th>法人</th>
				<th>法人证件类型</th>
				<th>法人证件号</th>
				<th>联系人姓名</th>
				<th>联系人电话</th>
				<th>企业证照类型</th>
				<th>证照编号</th>
				<th>银行开户许可证编号</th>
				<th>受托支付标识</th>
				<th>受托人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanSubject">
				<tr>
					<td><a href="${ctx}/wloan_subject/wloanSubject/cgbWloanSubjectViewForm?id=${wloanSubject.id}"> ${wloanSubject.companyName} </a></td>
					<td>${fns:getDictLabel(wloanSubject.type, 'wloan_subject_type', '')}</td>
					<td>${wloanSubject.loanUser}</td>
					<td>
						<c:if test="${wloanSubject.corporationCertType == 'IDC'}">
							<b>身份证</b>
						</c:if>
						<c:if test="${wloanSubject.corporationCertType == 'GAT'}">
							<b>港澳台身份证</b>
						</c:if>
						<c:if test="${wloanSubject.corporationCertType == 'MILIARY'}">
							<b>军官证</b>
						</c:if>
						<c:if test="${wloanSubject.corporationCertType == 'PASS_PORT'}">
							<b>护照</b>
						</c:if>
					</td>
					<td>${wloanSubject.corporationCertNo}</td>
					<td>${wloanSubject.agentPersonName}</td>
					<td>${wloanSubject.agentPersonPhone}</td>
					<td>
						<c:if test="${wloanSubject.businessLicenseType == 'BLC'}">
							<b>营业执照</b>
						</c:if>
						<c:if test="${wloanSubject.businessLicenseType == 'USCC'}">
							<b>统一社会信用代码</b>
						</c:if>
					</td>
					<td>${wloanSubject.businessNo}</td>
					<td>${wloanSubject.bankPermitCertNo}</td>
					<c:if test="${wloanSubject.isEntrustedPay == '0'}">
						<td><b>否</b></td>
						<td><b>无</b></td>
					</c:if>
					<c:if test="${wloanSubject.isEntrustedPay == '1'}">
						<td><b>是</b></td>
						<td><a href="${ctx}/wloan_subject/wloanSubject/cgbWloanSubjectEntrustedInfo?id=${wloanSubject.id}">${wloanSubject.cashierUser}</a></td>
					</c:if>
					<td><fmt:formatDate value="${wloanSubject.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${wloanSubject.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
						<td><a href="${ctx}/wloan_subject/wloanSubject/cgbWloanSubjectForm?id=${wloanSubject.id}">修改</a> <a href="${ctx}/wloan_subject/wloanSubject/cgbDelete?id=${wloanSubject.id}" onclick="return confirmx('确认要删除该融资主体吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>