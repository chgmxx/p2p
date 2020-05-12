<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>项目出借详情</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		//
		$("#messageBox").show();
		// 导出.
		$("#btnInvestInfoExportId").click(function(){
			top.$.jBox.confirm("确认要执行【Excel-导出】操作吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/wloan_term_invest/wloanTermInvest/exportInvestInfo?projectId=${projectId}");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});// --.
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
		<li class="active"><a href="${ctx}/wloan_term_invest/wloanTermInvest/findInvestByProId?projectId=${projectId}">出借详情</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="" action="" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<input id="btnInvestInfoExportId" class="btn btn-inverse" style="float: right;" type="button" value="Excel-导出" />
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>姓名</th>
				<th>电话</th>
				<th>身份证号</th>
				<th>出借金额</th>
				<th>出借时间</th>
				<th>出借合同</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${investList}" var="invest">
				<tr>
					<td><b>${invest.userInfo.realName}</b></td>
					<td><b>${invest.userInfo.name}</b></td>
					<td><b>${invest.userInfo.certificateNo}</b></td>
					<td><b>${invest.amount}</b></td>
					<td><fmt:formatDate value="${invest.beginDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>
						<c:choose>
							<c:when test="${empty invest.contractPdfPath}">
								<b>项目募集中，暂未生成合同</b>
							</c:when>
							<c:otherwise>
								<a href="${imgUrl}${invest.contractPdfPath}" target="_blank"><b>项目合同</b></a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div align="right">
		<input id="btnCancel" class="btn btn-primary" type="button" value="返 回" onclick="history.go(-1)" />
	</div>
</body>
</html>