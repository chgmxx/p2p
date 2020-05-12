<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>信贷联保管理</title>
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
		<li class="active"><a href="${ctx}/credit/coinsuranceinfo/creditCoinsuranceInfo/">信贷联保列表</a></li>
		<shiro:hasPermission name="credit:coinsuranceinfo:creditCoinsuranceInfo:edit"></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="creditCoinsuranceInfo" action="${ctx}/credit/coinsuranceinfo/creditCoinsuranceInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<!-- <ul class="ul-form">
			<li><label>用户ID：</label>
				<form:input path="creditUserId" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>1-个人,2-公司：</label>
				<form:select path="coinsuranceType" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('credit_coinsurance_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>姓名：</label>
				<form:input path="name" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>-->
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户</th>
				<th>联保关系</th>
				<th>姓名</th>
				<th>手机号</th>
				<th>附件表</th>
				<th>修改时间</th>
				<!--<shiro:hasPermission name="credit:coinsuranceinfo:creditCoinsuranceInfo:edit"><th>操作</th></shiro:hasPermission>-->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditCoinsuranceInfo">
			<tr>
				<td>
					${creditCoinsuranceInfo.creditUserInfo.name}
				</td>
				<td>
					<c:if test="${creditCoinsuranceInfo.coinsuranceType == '1' }">个人</c:if>
					<c:if test="${creditCoinsuranceInfo.coinsuranceType == '2' }">公司</c:if>
				</td>
				<td>
					${creditCoinsuranceInfo.name}
				</td>
				<td>
					${creditCoinsuranceInfo.phone}
				</td>
				<td>
						<c:forEach items="${creditCoinsuranceInfo.imgList}" var="imgList">
							<img style="max-width:500px;max-height:500px;_height:500px;border:0;padding:3px;" alt="" src="${creditImgUrl}${imgList}" id="imgId">
						</c:forEach>
				</td>
				<td>
					<fmt:formatDate value="${creditCoinsuranceInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<!--<shiro:hasPermission name="credit:coinsuranceinfo:creditCoinsuranceInfo:edit"><td>
    				<a href="${ctx}/credit/coinsuranceinfo/creditCoinsuranceInfo/form?id=${creditCoinsuranceInfo.id}">修改</a>
					<a href="${ctx}/credit/coinsuranceinfo/creditCoinsuranceInfo/delete?id=${creditCoinsuranceInfo.id}" onclick="return confirmx('确认要删除该信贷联保吗？', this.href)">删除</a>
				</td></shiro:hasPermission>-->
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