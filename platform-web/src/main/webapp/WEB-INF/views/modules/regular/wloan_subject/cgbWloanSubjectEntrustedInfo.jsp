<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>融资主体-企业列表</title>
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
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/wloan_subject/wloanSubject/cgbPersonalList">主体列表</a></li>
		<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
			<li class="active"><a href="${ctx}/wloan_subject/wloanSubject/cgbWloanSubjectEntrustedInfo?id=${wloanSubject.id}">受托人信息</a></li>
		</shiro:hasPermission>
	</ul>
	<div class="container-fluid breadcrumb">
		<div class="row-fluid span12">
			<label class="label">名称:</label>
			<b>${wloanSubject.companyName}</b>
		</div>
	</div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>受托人</th>
				<th>受托人身份证号</th>
				<th>受托人银行卡号</th>
				<th>受托人银行预留手机</th>
				<th>受托人开户行</th>
				<th>受托人开户行银行代码</th>
				<th>账户对公对私标识</th>
				<c:if test="${wloanSubject.cashierBankNoFlag == '1'}">
					<th>联行号</th>
				</c:if>
				<th>创建时间</th>
				<th>更新时间</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>${wloanSubject.cashierUser}</td>
				<td>${wloanSubject.cashierIdCard}</td>
				<td>${wloanSubject.cashierBankNo}</td>
				<td>${wloanSubject.cashierBankPhone}</td>
				<td>${wloanSubject.cashierBankAdderss}</td>
				<td>${wloanSubject.cashierBankCode}</td>
				<td>
					<c:if test="${wloanSubject.cashierBankNoFlag == '1'}">
						<b>对公</b>
					</c:if>
					<c:if test="${wloanSubject.cashierBankNoFlag == '2'}">
						<b>对私</b>
					</c:if>
				</td>
				<c:if test="${wloanSubject.cashierBankNoFlag == '1'}">
					<td>${wloanSubject.cashierBankIssuer}</td>
				</c:if>
				<td><fmt:formatDate value="${wloanSubject.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<td><fmt:formatDate value="${wloanSubject.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
		</tbody>
	</table>
	<div class="form-actions">
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
	</div>
</body>
</html>