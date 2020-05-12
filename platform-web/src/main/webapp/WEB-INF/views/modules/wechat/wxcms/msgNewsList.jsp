<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>图文消息</title>
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
		<li class="active"><a href="${ctx}/wechat/msgnews/list">图文消息</a></li>
		<shiro:hasPermission name="wechat:msgnews:edit">
			<li><a href="${ctx}/wechat/msgnews/form">添加消息</a>
			</li>
		</shiro:hasPermission> 
	</ul>
	<form:form id="searchForm" modelAttribute="msgNews" action="${ctx}/wechat/msgnews/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>标题：</label><form:input path="title" htmlEscape="false" maxlength="50" class="input-small"/>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>关键词</th><th>标题</th> <shiro:hasPermission name="wechat:msgnews:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="msg">
			<tr>
				<td>
					${msg.msgBase.inputCode}
				</td>
				<td>${msg.title}</td>
				 <shiro:hasPermission name="wechat:msgnews:edit"><td>
							<a href="${ctx}/wechat/msgnews/form?id=${msg.id }">修改</a>
							<a href="${ctx}/wechat/msgnews/delete?id=${msg.id}" 
								onclick="return confirmx('确认要删除该链接吗？', this.href)">删除</a>
							
							<c:choose>
								<c:when test="${msg.fromurl !=null}"><a href="${msg.fromurl}" target="_blank">预览</a></c:when>
								<c:otherwise><a href="${msg.url}" target="_blank">预览</a></c:otherwise>
							</c:choose>
				</td></shiro:hasPermission> 
			</tr>
		</c:forEach> 
		</tbody>
	</table>  
	<div class="pagination">${page}</div>
</body>
</html>