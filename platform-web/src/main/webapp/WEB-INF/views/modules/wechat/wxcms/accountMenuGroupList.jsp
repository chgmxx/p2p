<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>菜单组</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		function doPublish(){
			var gid = $('input:radio[name=radio_name]:checked').val();
			if(gid != null && gid != 'undefined'){
				if(confirm("确定生成微信账号菜单?")){
					window.location.href='${ctx}/wxapi/publishMenu?gid='+gid;
				}
			}else{
				alert("请选择菜单组");
			}
		}
		function doCancel(){
			if(confirm("确定删除微信账号菜单?")){
				window.location.href='${ctx}/wxapi/deleteMenu';
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/wechat/accountmenugroup/list">菜单组</a></li>
		<shiro:hasPermission name="wechat:accountmenugroup:edit">
			<li><a href="${ctx}/wechat/accountmenugroup/form">添加菜单组</a>
			</li>
		</shiro:hasPermission> 
	</ul>
	 <form:form id="searchForm" modelAttribute="accountmenugroup" action="${ctx}/wechat/accountmenugroup/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="btnSubmit" type="button" class="btn btn-primary" onclick="doPublish()"  value="生成微信菜单"/>
		<input id="btnSubmit" type="button" class="btn btn-primary" onclick="doCancel()" value="删除微信菜单"/>
	</form:form> 
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>选择</th><th>菜单组名称</th><th>是否在用</th> <shiro:hasPermission name="wechat:accountmenugroup:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
			<c:forEach items="${page.list}" var="group" >
				<tr>
					<td><input type="radio" name="radio_name" value="${group.id}"/></td>
					<td>
						${group.name}
					</td>
					<td> 
					<c:choose>
						<c:when test="${group.enable == 1}">
							<span style="color:green;">是</span>
						</c:when>
						<c:otherwise>
							<span style="color:red;">否</span>
						</c:otherwise>
					</c:choose>
					</td>
					<td>
						<shiro:hasPermission name="wechat:accountmenugroup:edit">
							<a href="${ctx}/wechat/accountmenugroup/form?id=${group.id }">修改</a>
							<a href="${ctx}/wechat/accountmenugroup/delete?id=${group.id}" 
								onclick="return confirmx('确认要删除该菜单组吗？', this.href)">删除</a>
								<a href="${ctx}/wechat/accountmenu/form?gid=${group.id}">添加菜单</a>	
						</shiro:hasPermission> 
							
						 
					</td>
				</tr>
			</c:forEach> 
		</tbody>
	</table>  
	<div class="pagination">${page}</div>
</body>
</html>