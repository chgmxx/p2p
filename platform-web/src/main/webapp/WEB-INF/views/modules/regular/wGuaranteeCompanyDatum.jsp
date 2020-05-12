<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>担保公司档案</title>
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
		<li><a href="${ctx}/pro/wguarantee/">担保公司列表</a></li>
		<shiro:hasPermission name="pro:wguaranteecompany:view">
			<li><a href="${ctx}/pro/wguarantee/form">添加担保公司</a>
			</li>
		</shiro:hasPermission> 
		<li class="active"><a href="">担保公司档案</a></li>
	</ul>
		<form:form id="searchForm" modelAttribute="wGuaranteeCompany" action="#" method="post" class="breadcrumb form-search">
			<label>企业名称：</label>	<c:choose>
						<c:when test="${empty wGuaranteeCompany.name}">
							<span class="help-inline"><font color="red">无</font></span>
						</c:when>
						<c:otherwise>
							<span class="help-inline">${wGuaranteeCompany.name}</span>
						</c:otherwise>
					</c:choose>
				<label>企业法人：</label><c:choose>
						<c:when test="${empty wGuaranteeCompany.corporation}">
							<span class="help-inline"><font color="red">无</font></span>
						</c:when>
						<c:otherwise>
							<span class="help-inline">${wGuaranteeCompany.corporation}</span>
						</c:otherwise>
					</c:choose>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" >
		<thead>
			<tr><th>档案类型</th><th style="width:35%;">档案</th><th>时间</th><th>操作</th></tr>
		</thead>
		<tbody>
		  	<c:forEach items="${annexFileList}" var="annexFile">
		  		<tr style="height:100px;">
					<td>${annexFile.label }</td>
					<td>
						<c:forEach items="${annexFile.urlList}" var="url">
							<img src="${url}" width="107px" height="72px"/>
						</c:forEach>
					</td>
					<td><fmt:formatDate value="${annexFile.createDate }" pattern="yyyy-MM-dd"/></td>
					<td>
						 <a href="${ctx}/sys/annexfile/form?id=${annexFile.id}&returnUrl=${returnUrl }&dictType=wguarantee_company_datum&title=${wGuaranteeCompany.name }">
							修改
						</a>
						<a href="${ctx}/sys/annexfile/delete?id=${annexFile.id}&returnUrl=${returnUrl }">
							删除
						</a> 
					</td>
		  		</tr>
		  	</c:forEach>
		</tbody>
	</table>  
	<div class="pagination">${page}</div>
		<div align="right">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="档案添加" onclick="addDatum('${wGuaranteeCompany.id}','${returnUrl }','wguarantee_company_datum','${wGuaranteeCompany.name }')" />&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />&nbsp;&nbsp;&nbsp;
	    </div>
    <script type="text/javascript">
    	function addDatum(otherId,returnUrl,dictType,title){
			location.href="${ctx}/sys/annexfile/form?otherId="+otherId+"&returnUrl="+returnUrl+"&dictType=wguarantee_company_datum&title="+title;		
    	}
    </script>
</body>
</html>