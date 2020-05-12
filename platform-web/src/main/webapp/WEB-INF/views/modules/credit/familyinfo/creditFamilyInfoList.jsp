<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>信贷家庭信息管理</title>
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
		<li class="active"><a href="${ctx}/credit/familyinfo/creditFamilyInfo/">信贷家庭信息列表</a></li>
		<shiro:hasPermission name="credit:familyinfo:creditFamilyInfo:edit"><li><a href="${ctx}/credit/familyinfo/creditFamilyInfo/form">信贷家庭信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="creditFamilyInfo" action="${ctx}/credit/familyinfo/creditFamilyInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<!-- <ul class="ul-form">
			<li><label>用户ID：</label>
				<form:input path="creditUserId" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>关系类型('1'，父母，'2'，配偶，'3'，子女)：</label>
				<form:select path="relationType" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>姓名：</label>
				<form:input path="name" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul> -->
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户</th>
				<th>附件表</th>
				<th>关系类型</th>
				<th>姓名</th>
				<th>手机号</th>
				<th>身份证号码</th>
				<th>修改时间</th>
				<!-- <shiro:hasPermission name="credit:familyinfo:creditFamilyInfo:edit"><th>操作</th></shiro:hasPermission> -->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditFamilyInfo">
			<tr>
				<td><!--<a href="${ctx}/credit/familyinfo/creditFamilyInfo/form?id=${creditFamilyInfo.id}">  -->
					${creditFamilyInfo.creditUserInfo.name}
				</td>
				<td>
						<c:forEach items="${creditFamilyInfo.imgList}" var="imgList">
							<img style="max-width:500px;max-height:500px;_height:500px;border:0;padding:3px;" alt="" src="${creditImgUrl}${imgList}" id="imgId">
						</c:forEach>
				</td>
				<td>
					<c:if test="${creditFamilyInfo.relationType == '1' }">父母</c:if>
					<c:if test="${creditFamilyInfo.relationType == '2' }">配偶</c:if>
					<c:if test="${creditFamilyInfo.relationType == '3' }">子女</c:if>
				</td>
				<td>
					${creditFamilyInfo.name}
				</td>
				<td>
					${creditFamilyInfo.phone}
				</td>
				<td>
					${creditFamilyInfo.idCard}
				</td>
				<td>
					<fmt:formatDate value="${creditFamilyInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<!-- <shiro:hasPermission name="credit:familyinfo:creditFamilyInfo:edit"><td>
    				<a href="${ctx}/credit/familyinfo/creditFamilyInfo/form?id=${creditFamilyInfo.id}">修改</a>
					<a href="${ctx}/credit/familyinfo/creditFamilyInfo/delete?id=${creditFamilyInfo.id}" onclick="return confirmx('确认要删除该信贷家庭信息吗？', this.href)">删除</a>
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