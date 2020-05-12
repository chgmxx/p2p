<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>活期融资项目管理</title>
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
		<li class="active"><a href="${ctx}/current/project/wloanCurrentProject/">活期融资项目列表</a></li>
		<shiro:hasPermission name="current:project:wloanCurrentProject:edit"><li><a href="${ctx}/current/project/wloanCurrentProject/form">活期融资项目添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanCurrentProject" action="${ctx}/current/project/wloanCurrentProject/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>编号：</label>
				<form:input path="sn" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>项目名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>期限：</label>
				<form:select path="span" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('regular_wloan_span')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>状态：</label>
				<form:select path="state" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('current_project_state')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns"><label><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></label></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目名称</th>
				<th>融资金额(元)</th>
				<th>上线时间</th>
				<th>结束日期</th>
				<th>期限（天）</th>
				<th>手续费（%）</th>
				<th>融资进度（元）</th>
				<th>状态</th>
				<th>创建日期</th>
				<th>更改日期</th>
				<shiro:hasPermission name="current:project:wloanCurrentProject:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wloanCurrentProject">
			<tr>
				<td>
					<c:choose>
						<c:when test="${wloanTermProject.state == '1' || wloanTermProject.state == '0' }">
							<a href="${ctx}/current/project/wloanCurrentProject/form?id=${wloanCurrentProject.id}">
						</c:when>
						<c:otherwise>
							<a href="${ctx}/current/project/wloanCurrentProject/check?id=${wloanCurrentProject.id}">
						</c:otherwise>
					</c:choose>
					${wloanCurrentProject.name}
				</a></td>
				<td>
					${wloanCurrentProject.amount}
				</td>
				<td>
					<fmt:formatDate value="${wloanCurrentProject.onlineDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${wloanCurrentProject.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(wloanCurrentProject.span, 'regular_wloan_span', '')}
				</td>
				<td>
					${wloanCurrentProject.feeRate}
				</td>
				<td>
					${wloanCurrentProject.currentRealAmount == null ? 0.00 : wloanCurrentProject.currentRealAmount}
				</td>
				<td>
					${fns:getDictLabel(wloanCurrentProject.state, 'current_project_state', '')}
				</td>
				<td>
					<fmt:formatDate value="${wloanCurrentProject.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${wloanCurrentProject.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="current:project:wloanCurrentProject:edit"><td>
					<c:if test="${wloanCurrentProject.state == '1' }">
	    				<a href="${ctx}/current/project/wloanCurrentProject/form?id=${wloanCurrentProject.id}">修改</a>
						<a href="${ctx}/current/project/wloanCurrentProject/delete?id=${wloanCurrentProject.id}" onclick="return confirmx('确认要删除该活期融资项目吗？', this.href)">删除</a>
					</c:if>
					<c:if test="${wloanCurrentProject.state == '1' && usertype == '4' }">
	    				<a href="${ctx}/current/project/wloanCurrentProject/check?id=${wloanCurrentProject.id}">提交审核</a>
					</c:if>
					<c:if test="${wloanCurrentProject.state == '2'}">
	    				<a href="${ctx}/current/project/wloanCurrentProject/check?id=${wloanCurrentProject.id}">上线</a>
					</c:if>
					<c:if test="${wloanCurrentProject.state == '3'}">
	    				<a href="${ctx}/current/project/wloanCurrentProject/check?id=${wloanCurrentProject.id}">放款</a>
	    				<c:if test="${isCanForward }">
	    					<a href="${ctx}/current/project/wloanCurrentProject/toForwardThis?id=${wloanCurrentProject.id}">转入</a>
	    				</c:if>
					</c:if>
					<c:if test="${wloanCurrentProject.state == '4'}">
	    				<a href="${ctx}/current/project/wloanCurrentProject/check?id=${wloanCurrentProject.id}">查看</a>
					</c:if>
					<c:if test="${wloanCurrentProject.state == '5'}">
	    				<a href="${ctx}/current/project/wloanCurrentProject/check?id=${wloanCurrentProject.id}">查看</a>
					</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>