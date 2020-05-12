<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>定期融资档案管理</title>
<meta name="decorator" content="default" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/lightbox.css" />
<script type="text/javascript" src="${ctxStatic}/js/lightbox.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		//$("#name").focus();
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
		<li><a href="${ctx}/wloan_term_doc/wloanTermDoc/">定期融资档案列表</a></li>
		<li class="active"><a href="${ctx}/wloan_term_doc/wloanTermDoc/manageForm?id=${wloanTermDoc.id}">定期融资档案管理</a></li>
	</ul>
	<!-- 定期融资档案主体. -->
	<form:form id="searchForm" modelAttribute="wloanTermDoc" action="#" method="post" class="breadcrumb form-search">
			<label>档案名称：</label>	<c:choose>
						<c:when test="${empty wloanTermDoc.name}">
							<span class="help-inline"><font color="red">无</font></span>
						</c:when>
						<c:otherwise>
							<span class="help-inline">${wloanTermDoc.name}</span>
						</c:otherwise>
					</c:choose>
				<label>备注：</label><c:choose>
						<c:when test="${empty wloanTermDoc.remarks}">
							<span class="help-inline"><font color="red">无</font></span>
						</c:when>
						<c:otherwise>
							<span class="help-inline">${wloanTermDoc.remarks}</span>
						</c:otherwise>
					</c:choose>
	</form:form>
	<form:form id="inputForm" modelAttribute="annexFile" action="#" method="post" class="form-horizontal">
		<sys:message content="${message}" />
	<!-- 不同的资料类别所对应的图片列表. -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>类型</th>
				<th>照片</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${annexFiles}" var="annexFile">
				<tr>
					<td>${fns:getDictLabel(annexFile.type, 'wloan_term_doc_type', '')}</td>
					<td>
						<c:forEach items="${annexFile.urlList}" var="attributes">
							<a class="example-image-link" href="${attributes}" data-lightbox="example-1"><img class="example-image" style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;" alt="" src="${attributes}"></a>
						</c:forEach>
					</td>
					<td><a href="${ctx}/sys/annexfile/form?returnUrl=${annexFile.returnUrl}&otherId=${annexFile.otherId}&dictType=${annexFile.dictType}&id=${annexFile.id}&title=${annexFile.title}">修改</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div align="right">
			<input id="btnSubmit" class="btn btn-primary" type="button" onclick="addDatum('${annexFile.otherId}','${annexFile.returnUrl}','${annexFile.dictType}','${annexFile.title}')" value="档案添加" />&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />&nbsp;&nbsp;&nbsp;
	</div>
	</form:form>
	 <script type="text/javascript">
    	function addDatum(otherId,returnUrl,dictType,title){
			location.href="${ctx}/sys/annexfile/form?otherId="+otherId+"&returnUrl="+returnUrl+"&dictType=wguarantee_company_datum&title="+title;		
    	}
    </script>
</body>
</html>