<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户流水记录管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			//查询
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/transdetail/seachUserTransDetail/seach");
				$("#searchForm").submit();
			});
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action", "${ctx}/transdetail/userTransDetail/");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/transdetail/seachUserTransDetail/">存管宝订单查询</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="userTransDetail" action="${ctx}/transdetail/seachUserTransDetail/seach" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>订单号：</label>
				<form:input path="transId" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><label></label><input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<!-- <table id="contentTable" class="table table-striped table-bordered table-condensed">
	 	<thead>
			<tr>
				<th>订单号</th>
				<th>交易类型</th>
				<th>交易金额</th>
				<th>交易状态</th>
			</tr>
		</thead>
		<tbody>
		 <c:forEach items="${transDetail}" var="userTransDetail">
			<tr>
				<td>
					${userTransDetail.transId}
				</td>
				<td>
					${userTransDetail.trustTypeStr}
				</td>
				<td>
					${userTransDetail.amount}
				</td>
				<td>
					${userTransDetail.stateStr}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table> -->
</body>
</html>