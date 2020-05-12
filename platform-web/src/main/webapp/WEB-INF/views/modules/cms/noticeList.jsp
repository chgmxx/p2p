<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>公告管理</title>
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
		<li class="active"><a href="${ctx}/cms/notice/?type=2">公告列表</a></li>
		<shiro:hasPermission name="cms:notice:edit">
			<li><a href="${ctx}/cms/notice/form?type=2">公告添加</a>
			</li>
		</shiro:hasPermission> 
	</ul>
	<form:form id="searchForm" modelAttribute="notice" action="${ctx}/cms/notice/?type=2" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>分类：</label>
			<form:select id="state" path="state" >
				<form:option value="" label="全部"/>
				<form:option value="1" label="上线"  />
				<form:option value="0" label="下线"/>
			</form:select>
		<label>标题：</label><form:input path="title" htmlEscape="false" maxlength="50" class="input-small"/>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		<%-- <label>状态：</label><form:radiobuttons onclick="$('#searchForm').submit();" path="delFlag" items="${fns:getDictList('cms_notice_flag')}" itemLabel="label" itemValue="value" htmlEscape="false" /> --%>
	</form:form> 
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>标题</th><th>创建时间</th><th>是否在线</th><shiro:hasPermission name="cms:notice:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="notice">
			<tr>
				<td>
					<a href="${ctx}/cms/notice/form?id=${notice.id }&viewType=1">${notice.title}</a>
				</td>
				<td><fmt:formatDate value="${notice.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${notice.state ne 0 ? '上线' :'下线' }</td>
				
					 <shiro:hasPermission name="cms:notice:edit">
					 <td> 
						 <c:if test="${notice.delFlag == 0}">
							<a href="${ctx}/cms/notice/edit?id=${notice.id }" 
								onclick="return confirmx('确认要${notice.state ne 0?'下线':'上线'}该公告吗？', this.href)">
								${notice.state ne 0 ? '下线':'上线'}
							</a>
							<c:if test="${notice.state==0 }">
								<a href="${ctx}/cms/notice/form?id=${notice.id }">修改</a>
							
								<a href="${ctx}/cms/notice/delete?id=${notice.id}" 
								onclick="return confirmx('确认要删除该公告吗？', this.href)">删除</a>
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