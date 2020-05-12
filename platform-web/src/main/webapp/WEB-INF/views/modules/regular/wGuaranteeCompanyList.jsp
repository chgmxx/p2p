<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>担保公司</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function deleteCompany(content, url, id, isRe){
			confirmx(content, function(result){
				 $.ajax({
						url : url,
						type : "post", // 用POST方式传输
						dataType : "json", // 数据格式:JSON
						data : {
							id:id,
							isRe:isRe 
						},
						success : function(data) {
							if(data==2){
								alert("已有项目在使用该担保公司，不能删除");
							} else {
								$("#searchForm").submit();								
							}
						},
						error : function(data) {
							alert("程序异常");
						}
					}); 
			});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/pro/wguarantee/">担保公司列表</a></li>
		<shiro:hasPermission name="pro:wguaranteecompany:view">
			<li><a href="${ctx}/pro/wguarantee/form">担保公司添加</a>
			</li>
		</shiro:hasPermission> 
	</ul>
	<form:form id="searchForm" modelAttribute="wGuaranteeCompany" action="${ctx}/pro/wguarantee/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>企业名称：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>企业法人：</label><form:input path="corporation" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>电话：</label><form:input path="phone" htmlEscape="false" maxlength="50" class="input-small"/>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form> 
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>企业名称</th><th>企业法人</th><th>电话</th><th>注册地</th><th>注册日期</th><th>营业执照编码</th><th>组织机构代码</th><th>税务登记号</th><shiro:hasPermission name="pro:wguaranteecompany:view"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="wGuaranteeCompany">
			<tr>
				<td>
				<a href="${ctx}/pro/wguarantee/form?id=${wGuaranteeCompany.id}&flag=view">${wGuaranteeCompany.name}</a>
				</td>
				<td>${wGuaranteeCompany.corporation}</td>
				<td>${wGuaranteeCompany.phone}</td>
				<td>${wGuaranteeCompany.area.name}</td>
				<td><fmt:formatDate value="${wGuaranteeCompany.registerDate}" pattern="yyyy-MM-dd"/></td>
				<td>${wGuaranteeCompany.businessNo}</td>
				<td>${wGuaranteeCompany.organNo}</td>
				<td>${wGuaranteeCompany.taxCode}</td>
				<td>
					<shiro:hasPermission name="pro:wguaranteecompany:edit">
						<a href = "${ctx}/pro/wguarantee/datum?id=${wGuaranteeCompany.id }">档案管理</a>
						<a href="${ctx}/pro/wguarantee/form?id=${wGuaranteeCompany.id }">修改</a>
						<c:if test="${notice.delFlag ne '2'}">
							 <%-- <a href="#" onclick="deleteCompany('确认删除该担保公司吗?', '${ctx}/pro/wguarantee/delete', '${wGuaranteeCompany.id}','${wGuaranteeCompany.delFlag ne 0?'&isRe=true':''}')">删除</a> --%>
							 <a href="${ctx}/pro/wguarantee/delete?id=${wGuaranteeCompany.id}${wGuaranteeCompany.delFlag ne 0?'&isRe=true':''}" 
								onclick="return confirmx('确认要删除该机构吗？', this.href)">删除</a>
						</c:if>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>  
		</tbody>
	</table>  
	<div class="pagination">${page}</div>
</body>
</html>