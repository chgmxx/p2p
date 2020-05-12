<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>风控企业信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
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
		<li><a href="${ctx}/riskmanagement/riskManagementMessage/">风控企业信息列表</a></li>
		<c:if test="${ usertype == '8' }">
		<li class="active"><a href="${ctx}/riskmanagement/riskManagementMessage/form?id=${riskManagement.id}">风控企业信息<shiro:hasPermission name="fk:riskmanagement:riskManagement:edit">${not empty riskManagement.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="fk:riskmanagement:riskManagement:edit">查看</shiro:lacksPermission></a></li>
	    </c:if>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="riskManagement" action="${ctx}/riskmanagement/riskManagementMessage/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">企业名称：${riskManagement.companyName}</label><label class="control-label">操作类型：档案</label><label class="control-label"><a href="${riskManagement.docUrl}">${riskManagement.companyName}企业介绍</a></label>
		</div>
		<!-- <div class="form-actions">
			<shiro:hasPermission name="riskmanagement:riskManagementMessage:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div> -->
	</form:form>
		<form:form id="inputForm" modelAttribute="annexFile" action="#" method="post" class="form-horizontal">
		<sys:message content="${message}" />
	<!-- 不同的资料类别所对应的图片列表. -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<!-- <th>类型</th> -->
				<th>照片</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${annexFiles}" var="annexFile">
				<tr>
					<!-- <td>${fns:getDictLabel(annexFile.type, 'wloan_term_doc_type', '')}</td> -->
					<td>
						<c:forEach items="${annexFile.urlList}" var="attributes">
							<img style="max-width:500px;max-height:500px;_height:500px;border:0;padding:3px;" alt="" src="${attributes}" id="imgId">
						</c:forEach>
					</td>
					<td>
					<c:if test="${ (riskManagement.state == '0' || riskManagement.state == '1' )&& usertype == '8' }">
					<a href="${ctx}/sys/annexfile/formRiskManagement?returnUrl=${annexFile.returnUrl}&otherId=${annexFile.otherId}&dictType=${annexFile.dictType}&id=${annexFile.id}&title=${annexFile.title}">修改</a>
					</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div align="right">
	       <c:if test="${ (riskManagement.state == '0' || riskManagement.state == '1' )&& usertype == '8' }">
			<input id="btnSubmit" class="btn btn-primary" type="button" onclick="addDatum('${annexFile.otherId}','${annexFile.returnUrl}','${annexFile.dictType}','${annexFile.title}','${annexFile.type}')" value="档案添加" />&nbsp;&nbsp;&nbsp;
		   </c:if>	
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />&nbsp;&nbsp;&nbsp;
	</div>
	</form:form>
	<script type="text/javascript">
    	function addDatum(otherId,returnUrl,dictType,title,type){
			location.href="${ctx}/sys/annexfile/formRiskManagement?otherId="+otherId+"&returnUrl="+returnUrl+"&dictType=risk_management&title="+title+"&type="+type;		
    	}
    	function pic(){
    		alert("11");
    		var s=document.getElementById("picUrl").src;
    		alert(s.substring(s.lastIndexOf("/")+1)); 
    	}
    	window.onload = function pic(){ 
    	var img = document.getElementById('imgId'); 
    	var src = img.getAttribute('src'); 
    	img.setAttribute('src',''); 
    	img.onload = function(){ 
    	//alert(src.substring(src.lastIndexOf("/")+1));
    	}; 
    	img.setAttribute('src',src); 
    	}; 

    </script>
</body>
</html>