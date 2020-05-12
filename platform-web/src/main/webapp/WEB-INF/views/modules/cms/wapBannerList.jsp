<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>banner列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
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
		<li><a href="${ctx}/cms/notice/?type=1">电脑端-BANNER</a></li>
		<li class="active"><a href="${ctx}/cms/notice/?type=0">移动端-BANNER</a></li>
		<shiro:hasPermission name="cms:notice:edit">
			<li><a href="${ctx}/cms/notice/form?type=1">新增-BANNER</a></li>
		</shiro:hasPermission> 
	</ul>
	<form:form id="searchForm" modelAttribute="notice" action="${ctx}/cms/notice/?type=0" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li>
				<label>状态：</label>
				<form:select id="state" path="state" >
					<form:option value="" label="全部"/>
					<form:option value="1" label="上线" />
					<form:option value="0" label="下线"/>
				</form:select>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</li>
		</ul>
	</form:form> 
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>标题</th><th>图片</th><th>排序</th><th>应用范围</th><th> 链接</th><shiro:hasPermission name="cms:notice:edit"><th  style="width:10%">操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="notice">
			<tr>
				<td>
					<a href="${ctx}/cms/notice/form?id=${notice.id }&viewType=1">${notice.title }</a>
				</td>
				<td><img alt="${notice.title }" src="${notice.logopath }" width="30%" height="15%"></td>
				<td>${notice.orderSum }</td>
				<td>${notice.type eq 0 ? '移动端':'电脑端' }</td>
				<td>${notice.state ne 0 ? '上线' :'下线' }</td>
				 <shiro:hasPermission name="cms:notice:edit">
				 <td>
				 	<c:if test="${notice.delFlag == 0}">
						<a href="${ctx}/cms/notice/edit?id=${notice.id}" 
							onclick="return confirmx('确认要${notice.state ne 0?'下线':'上线'}该banner吗？', this.href)">
							${notice.state ne 0 ? '下线':'上线'}
						</a>
						<c:if test="${notice.state==0 }">
						<a href="${ctx}/cms/notice/form?id=${notice.id }">修改</a>
						<c:if test="${notice.delFlag ne '2'}"><a href="${ctx}/cms/notice/delete?id=${notice.id}${notice.delFlag ne 0?'&isRe=true':''}" 
								onclick="return confirmx('确认要删除该banner吗？', this.href)">删除</a></c:if>
								</c:if>
					</c:if>			
				</td>
				</shiro:hasPermission> 
			</tr>
		</c:forEach> 
		</tbody>
	</table>  
	<div class="pagination">${page}</div>
</body>
</html>