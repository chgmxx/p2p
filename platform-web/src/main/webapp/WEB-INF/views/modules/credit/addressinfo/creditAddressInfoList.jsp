<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>信贷家庭住址管理</title>
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
		<li class="active"><a href="#">信贷家庭住址列表</a></li>
		<shiro:hasPermission name="credit:addressinfo:creditAddressInfo:edit"></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="creditAddressInfo" action="${ctx}/credit/addressinfo/creditAddressInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<!-- <ul class="ul-form">
			<li><label>用户ID：</label>
				<form:input path="creditUserId" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul> -->
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>姓名</th>
				<th>现住址地址省市</th>
				<th>具体位置</th>
				<th>房产证</th>
				<th>修改时间</th>
				<!-- <shiro:hasPermission name="credit:addressinfo:creditAddressInfo:edit"><th>操作</th></shiro:hasPermission> -->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditAddressInfo">
			<tr>
				<td>
					${creditAddressInfo.creditUserInfo.name}
				</td>
				<td>
					${creditAddressInfo.province.name}${creditAddressInfo.city.name}
				</td>
				<td>
					${creditAddressInfo.address}
				</td>
				<td>
						<c:forEach items="${creditAddressInfo.imgList}" var="imgList">
							<img style="max-width:500px;max-height:500px;_height:500px;border:0;padding:3px;" alt="" src="${creditImgUrl}${imgList}" id="imgId">
						</c:forEach>
				</td>
				<td>
					<fmt:formatDate value="${creditAddressInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<!-- <shiro:hasPermission name="credit:addressinfo:creditAddressInfo:edit"><td>
    				<a href="${ctx}/credit/addressinfo/creditAddressInfo/form?id=${creditAddressInfo.id}">修改</a>
					<a href="${ctx}/credit/addressinfo/creditAddressInfo/delete?id=${creditAddressInfo.id}" onclick="return confirmx('确认要删除该信贷家庭住址吗？', this.href)">删除</a>
				</td></shiro:hasPermission> -->
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<div class="form-actions">
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</body>
</html>