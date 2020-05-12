<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>定期融资档案管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#inputForm").validate({
			submitHandler : function(form) {
				loading('正在提交，请稍等...');
				form.submit();
			},
			errorContainer : "#messageBox",
			errorPlacement : function(error, element) {
				$("#messageBox").text("输入有误，请先更正。");
				if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
					error.appendTo(element.parent().parent());
				} else {
					error.insertAfter(element);
				}
			}
		});
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/current/project/wloanCurrentProject/">活期融资项目列表</a></li>
		<li><a href="${ctx}/current/project/wloanCurrentProject/check?id=${wloanCurrentProject.id}">活期融资项目
		<c:if test="${wloanCurrentProject.state == '1'}">审核</c:if>
		<c:if test="${wloanCurrentProject.state == '2'}">上线</c:if>
		<c:if test="${wloanCurrentProject.state == '3'}">查看</c:if>
		<c:if test="${wloanCurrentProject.state == '4'}">查看</c:if>
		<c:if test="${wloanCurrentProject.state == '5'}">查看</c:if>
		</a></li>
		<li class="active"><a href="">项目融资档案查看</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="wloanTermDoc" action="#" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty wloanTermDoc.name}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${wloanTermDoc.name}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注信息：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty wloanTermDoc.remarks}">
						<span class="help-inline"><font color="red">无</font></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">${wloanTermDoc.remarks}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>类型</th>
				<th>照片</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${annexFiles}" var="annexFile">
				<tr>
					<td>${fns:getDictLabel(annexFile.type, 'wloan_term_doc_type', '')}</td>
					<td>
						<c:forEach items="${annexFile.urlList}" var="attributes">
							<img style="max-width:107px;max-height:72px;_height:72px;border:0;padding:3px;" alt="" src="${imgUrl}${attributes}">
						</c:forEach>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>