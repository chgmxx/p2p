<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文本消息</title>
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
		<li class="active"><a href="${ctx}/wechat/msgtext/list">文本消息</a></li>
		<shiro:hasPermission name="wechat:msgtext:edit">
			<li><a href="${ctx}/wechat/msgtext/form?type=4">添加消息</a>
			</li>
		</shiro:hasPermission> 
	</ul>
	<form:form id="searchForm" modelAttribute="msgText" action="${ctx}/wechat/msgtext/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>消息描述：</label><form:input path="content" htmlEscape="false" maxlength="50" class="input-small"/>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>关键词</th><th>消息描述</th> <shiro:hasPermission name="wechat:msgtext:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="msg">
			<tr>
				<td>
					${msg.msgBase.inputCode}
				</td>
				<td>${msg.content}</td>
				 <shiro:hasPermission name="wechat:msgtext:edit"><td>
							<a href="${ctx}/wechat/msgtext/form?id=${msg.id }">修改</a>
							<c:if test="${notice.delFlag ne '2'}"><a href="${ctx}/wechat/msgtext/delete?id=${msg.id}" 
								onclick="return confirmx('确认要删除该链接吗？', this.href)">删除</a></c:if>
						 
				</td></shiro:hasPermission> 
			</tr>
		</c:forEach> 
		</tbody>
	</table>  
	<div class="pagination">${page}</div>
</body>
</html>