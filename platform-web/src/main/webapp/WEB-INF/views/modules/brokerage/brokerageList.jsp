<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户佣金管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/brokerage/list">用户信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="brokerage" action="${ctx}/brokerage/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		 
		<ul class="ul-form">
			<li><label>佣金获得者：</label>
				<form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>佣金来源者：</label>
				<form:input path="fromUserInfo.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><label></label><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>佣金获取者手机</th>
				<th>佣金获取者姓名</th>
				<th>佣金来源者手机</th>
				<th>佣金来源者姓名</th>
				<th>佣金(元)</th>
				<th>创建时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="brokerage">
			<tr>
				<td> 
					${brokerage.userInfo.name}
				</td>
				<td>
					${brokerage.userInfo.realName}
				</td>
				<td>
					${brokerage.fromUserInfo.name}
				</td>
				 <td>
				 	${brokerage.fromUserInfo.realName}
				 </td>
				 <td>
				 	${brokerage.amount }
				 </td>
				<td>
					<fmt:formatDate value="${brokerage.createDate }" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>