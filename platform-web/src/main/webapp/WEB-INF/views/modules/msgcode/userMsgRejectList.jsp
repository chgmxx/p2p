<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
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
		<li ><a href="${ctx}/usersms/">用户验证码信息</a></li>
		<li class="active"><a href="${ctx}/usersms/rejectList">屏蔽信息</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="smsRejectHistory" action="${ctx}/usersms/rejectList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="phone" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><label></label><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号码</th>
				<th>请求IP</th>
				<th>请求时间</th>
				<th>请求次数</th>
				<th>屏蔽类型</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="smsRejectHistory">
			<tr>
				<td>
					${smsRejectHistory.phone}
				</td>
				<td>
					${smsRejectHistory.ip}
				</td>
				<td>
					<fmt:formatDate value="${smsRejectHistory.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${smsRejectHistory.times}
				</td>
				<td>
					${fns:getDictLabel(smsRejectHistory.type, 'reject_type', '')}
				</td>
				<td>
					<a href="${ctx}/usersms/rejectDelete?id=${smsRejectHistory.id}" onclick="return confirmx('确认要解除该手机号的屏蔽吗？', this.href)">解除屏蔽</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>