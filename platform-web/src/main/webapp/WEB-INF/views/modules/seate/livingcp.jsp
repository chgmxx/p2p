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
		<li class="active"><a href="${ctx}/seate/livingcp/form">活体人像核验</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="seateEntity" action="${ctx}/seate/livingcp/query" method="post" class="form-horizontal">
		<div class="control-group">
			<div class="span6">
				<label class="control-label">姓名：</label>
				<div class="controls">
					<form:input path="name" htmlEscape="false" maxlength="32" class="input-medium required"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">身份证号：</label>
				<div class="controls">
					<form:input path="idcardNumber" htmlEscape="false" maxlength="32" class="input-medium required" />
				</div>
			</div>
		</div>	
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">照片：</label>
				<div class="controls">
					<form:hidden id="imgB64A" path="imgB64A" htmlEscape="false" maxlength="255" class="input-xlarge"/>
					<sys:ckfinder input="imgB64A" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">照片类型：</label>
				<div class="controls">
					<form:select path="imgB64AType" class="input-xlarge required" style="width:177px" id="wloan_guarant_select">
						<form:option value="1" label="高清照"/>
					</form:select>
				</div>
			</div>
		</div>	
		
		<div class="control-group">
			<div class="form-actions">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</div>
		</div>	
		
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