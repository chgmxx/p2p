<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>二维码</title>
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
		<li class="active"><a href="${ctx}/wechat/erweima/list">二维码</a></li>
		<shiro:hasPermission name="wechat:erweima:edit">
			<li><a href="${ctx}/wechat/erweima/form">添加二维码</a>
			</li>
		</shiro:hasPermission> 
	</ul>
	<form:form id="searchForm" modelAttribute="accountErWeiMa" action="${ctx}/wechat/erweima/list " method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>渠道名称：</label><form:input path="channelName" htmlEscape="false" maxlength="50" class="input-small"/>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>渠道编号</th><th>渠道名称</th><th>二维码</th><th>地址</th><shiro:hasPermission name="wechat:erweima:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="accountErWeiMa">
			<tr>
				<td>
					 ${accountErWeiMa.channelCode} 
				</td>
				<td>${accountErWeiMa.channelName} </td>
				<td><img alt="" src="${accountErWeiMa.fileUrl}" >  </td>
				<td>${accountErWeiMa.fileUrl}  </td>
				 <shiro:hasPermission name="wechat:erweima:edit"><td>
						<a href="${ctx}/wechat/erweima/form?id=${accountErWeiMa.id }">修改</a>
						<a href="${ctx}/wechat/erweima/delete?id=${accountErWeiMa.id}" 
							onclick="return confirmx('确认要删除该二维码吗？', this.href)">删除</a>
				</td></shiro:hasPermission> 
			</tr>
		</c:forEach> 
		</tbody>
	</table>  
	<div class="pagination">${page}</div>
</body>
</html>