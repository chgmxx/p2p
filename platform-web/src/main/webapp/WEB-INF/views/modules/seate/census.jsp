<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
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
		<li class="active"><a href="${ctx}/seate/census/form">用户户籍信息查询</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="seateEntity" action="${ctx}/seate/census/query" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li><label>姓名：</label>
				<form:input path="name" htmlEscape="false" maxlength="32" class="input-medium required"/>
			</li>
			<li><label>身份证号：</label>
				<form:input path="idcardNumber" htmlEscape="false" maxlength="32" class="input-medium required" />
			</li>
			<li class="btns">
				<label></label>
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	
	<sys:message content="${message }"/>
	<c:choose>
		<c:when test="${ seateEntity.result.code == '0000' }">
			<table id="contentTable" class="table table-striped table-bordered" style="max-width: 60%" >
				<tbody>
					<tr>
						<th>姓名</th>
						<th>${seateEntity.result.name }</th>
					</tr>
					<tr>
						<th>性别</th>
						<th>${seateEntity.result.sex }</th>
					</tr>
					<tr>
						<th>民族</th>
						<th>${seateEntity.result.nation }</th>
					</tr>
					<tr>
						<th>身份证</th>
						<th>${seateEntity.result.idcardNumber }</th>
					</tr>
					<tr>
						<th>出生日期</th>
						<th>${seateEntity.result.birthday }</th>
					</tr>
					<tr>
						<th>学历</th>
						<th>${seateEntity.result.education }</th>
					</tr>
					<tr>
						<th>婚否</th>
						<th>${seateEntity.result.maritalStatus }</th>
					</tr>
					<tr>
						<th>籍贯</th>
						<th>${seateEntity.result.nativePlace }</th>
					</tr>
					<tr>
						<th>出生地</th>
						<th>${seateEntity.result.birthPlace }</th>
					</tr>
					<tr>
						<th>户籍地址</th>
						<th>${seateEntity.result.address }</th>
					</tr>
				</tbody>
			</table>
		</c:when>
			
		<c:otherwise>
			<c:if test="${seateEntity.result.code != null && seateEntity.result.code != '' && seateEntity.result.code != '0000' }">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<tbody>
						<tr>
							<td>
								查询失败,代码 ${seateEntity.result.code }
							</td>
						</tr>
					</tbody>
				</table>
			</c:if>
		</c:otherwise>
	</c:choose>
	
</body>
</html>