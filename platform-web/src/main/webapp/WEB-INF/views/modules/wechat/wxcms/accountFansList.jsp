<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>粉丝管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function doSync(){
			if(confirm("确定同步?")){
				window.location.href='${ctx}/wxapi/syncAccountFansList';
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/wechat/accountfans/list">粉丝管理</a></li>
	</ul>
	
	 <form:form id="searchForm" modelAttribute="accountmenuFans" action="${ctx}/wechat/accountmenufans/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<a href="${ctx}/wxapi/syncAccountFansList" 
		onclick="return confirmx('确定同步粉丝信息吗？', this.href)"><input class="btn btn-primary" type="button" value="批量同步粉丝"/></a>
		
	</form:form> 
					
	<sys:message content="${message}"/>				
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>昵称</th><th>性别</th><th>省-市</th><th>同步</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="fans">
			<tr>
				<td>
					 ${fans.nicknameStr }
				</td>
				<td>
					<c:if test="${fans.gender==0 } ">女</c:if>
					<c:if test="${fans.gender==1 } ">男</c:if>
					<c:if test="${fans.gender==2 } ">未知</c:if>
				</td>
				<td> ${fans.province }-${fans.city }
				</td>
				<td>
					<a href="${ctx}/wxapi/syncAccountFans.html?openId=${fans.openId}">
					<input type="button" style="width:150px;" class="btn" value="同步粉丝信息"/>
					</a>
					
					<a href="${ctx}/wxapi/syncAccountFans.html?openId=${fans.openId}" 
					onclick="return confirmx('确定同步粉丝信息吗？', this.href)">同步</a>
				</td>
			</tr>
		</c:forEach> 
		</tbody>
	</table>  
	<div class="pagination">${page}</div>
</body>
</html>