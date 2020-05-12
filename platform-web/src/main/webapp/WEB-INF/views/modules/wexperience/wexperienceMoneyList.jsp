<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>体验金信息管理</title>
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
		<li class="active"><a href="${ctx}/wexperience/wexperienceMoney/">体验金信息列表</a></li>
		<shiro:hasPermission name="wexperience:wexperienceMoney:edit"><li><a href="${ctx}/wexperience/wexperienceMoney/form">体验金信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wexperienceMoney" action="${ctx}/wexperience/wexperienceMoney/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户名：</label>
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>状态：</label>
				<form:select path="state" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:option value="1" label="可用"/>
					<form:option value="2" label="已使用"/>
					<form:option value="3" label="已到期"/>
				</form:select>
			</li>
			<li><label>投资类型：</label>
				<form:select path="type" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:option value="1" label="定期" />
					<form:option value="2" label="活期" />
				</form:select>
			</li>
			<li><label>项目名：</label>
				<form:input path="projectId" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户名</th>
				<th>投资项目</th>
				<th>体验金额</th>
				<th>利息</th>
				<th>投资日期</th>
				<th>状态</th>
				<th>来源</th>
				<th>获得日期</th>
				<th>投资类型</th>
				<shiro:hasPermission name="wexperience:wexperienceMoney:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wexperienceMoney">
			<tr>
				<td><a href="${ctx}/wexperience/wexperienceMoney/form?id=${wexperienceMoney.id}">
					${wexperienceMoney.userInfo.realName }
				</a></td>
				<td>
					${wexperienceMoney.projectId}
				</td>
				<td>
					<fmt:formatNumber type="number" value="${wexperienceMoney.amount }" minFractionDigits="2" maxFractionDigits="2"/>
				</td>
				<td>
					<fmt:formatNumber type="number" value="${wexperienceMoney.inverst }" minFractionDigits="2" maxFractionDigits="2"/>
				</td>
				<td>
					<fmt:formatDate value="${wexperienceMoney.bidTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${wexperienceMoney.state }
				</td>
				<td>
					${wexperienceMoney.comeForm}
				</td>
				<td>
					<fmt:formatDate value="${wexperienceMoney.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(wexperienceMoney.type, '', '')}
				</td>
				<shiro:hasPermission name="wexperience:wexperienceMoney:edit"><td>
    				<a href="${ctx}/wexperience/wexperienceMoney/form?id=${wexperienceMoney.id}">修改</a>
					<a href="${ctx}/wexperience/wexperienceMoney/delete?id=${wexperienceMoney.id}" onclick="return confirmx('确认要删除该体验金信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>