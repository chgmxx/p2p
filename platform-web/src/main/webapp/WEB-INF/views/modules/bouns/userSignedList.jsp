<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户签到管理</title>
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
		<li class="active"><a href="${ctx}/bouns/userSigned/">客户签到列表</a></li>
		<shiro:hasPermission name="bouns:userSigned:edit"><li><a href="${ctx}/bouns/userSigned/form">客户签到添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userSigned" action="${ctx}/bouns/userSigned/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>签到日期：</label>
				<input name="beginCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${userSigned.beginCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="endCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${userSigned.endCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>客户账号</th>
				<th>客户姓名</th>
				<th>连续签到次数(次)</th>
				<th>签到日期</th>
				<th>修改日期</th>
				<th>备注</th>
				<%-- <shiro:hasPermission name="bouns:userSigned:edit"><th>操作</th></shiro:hasPermission> --%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userSigned">
			<tr>
				<td>
					<!-- <a href="${ctx}/bouns/userSigned/form?id=${userSigned.id}"></a> -->
					${userSigned.userInfo.name}
				</td>
				<td>
					${userSigned.userInfo.realName}
				</td>
				<td>
					${userSigned.continuousTime}
				</td>
				<td>
					<fmt:formatDate value="${userSigned.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${userSigned.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<th><b style="color: red;">客户签到</b></th>
<%-- 				<shiro:hasPermission name="bouns:userSigned:edit"><td>
    				<a href="${ctx}/bouns/userSigned/form?id=${userSigned.id}">修改</a>
					<a href="${ctx}/bouns/userSigned/delete?id=${userSigned.id}" onclick="return confirmx('确认要删除该客户签到吗？', this.href)">删除</a>
				</td></shiro:hasPermission> --%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>