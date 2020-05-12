<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>菜单列表</title>
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
		<li ><a href="${ctx}/wechat/accountmenugroup/list">菜单组</a></li>
		<shiro:hasPermission name="wechat:accountmenugroup:edit">
			<li class="active">
				<a href="${ctx}/wechat/accountmenugroup/form?id=${accountMenuGroup.id}">${not empty accountMenuGroup.id?'修改':'添加'}菜单组</a>
			</li>
		</shiro:hasPermission> 
	</ul>
	<form:form id="searchForm" modelAttribute="accountMenuGroup" action="${ctx}/wechat/accountmenugroup/save" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/> 
		<form:hidden path="id"/>
		<form:input path="name" htmlEscape="false" maxlength="100" class="required"  />
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>
	</form:form> 
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th rowspan="2">名称</th><th colspan="3" >消息类型</th><th rowspan="2">一级菜单</th><th rowspan="2">顺序</th> <shiro:hasPermission name="wechat:accountmenugroup:edit"><th rowspan="2">操作</th></shiro:hasPermission>
			</tr>
			<tr>
				<th style="width:150px;">关键字消息</th>
				<th style="width:150px;">指定消息</th>
				<th>链接消息</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="menu">
			<tr>
				<td>	
					${menu.name }
				</td>
				<td> 
					${menu.inputcode }
				</td>
				<td> 
					${menu.msgBase.id }
				</td>
				<td> 
					${menu.url }
				</td>
				<td> 
					${menu.parentName }
				</td>
				<td> 
					${menu.sort }
				</td>
				 <shiro:hasPermission name="wechat:accountmenu:edit">
				 	<td>
						<a href="${ctx}/wechat/accountmenu/form?id=${menu.id }">修改</a>
						<a href="${ctx}/wechat/accountmenu/delete?id=${menu.id}" 
							onclick="return confirmx('确认要删除该菜单组吗？', this.href)">删除</a>
					</td>
				</shiro:hasPermission> 
			</tr>
		</c:forEach> 
		</tbody>
	</table>  
	<div class="pagination">${page}</div>
</body>
</html>