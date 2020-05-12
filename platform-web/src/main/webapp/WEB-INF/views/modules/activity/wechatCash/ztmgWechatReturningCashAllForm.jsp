<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信返现管理</title>
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
		<li><a href="${ctx}/activity/ztmgWechatReturningCash/">微信返现列表</a></li>
		<li class="active"><a href="${ctx}/activity/ztmgWechatReturningCash/formall?id=${ztmgWechatReturningCash.id}">微信返现批量充值</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ztmgWechatReturningCash" action="${ctx}/activity/ztmgWechatReturningCash/saveall" method="post" class="form-horizontal"  enctype="multipart/form-data">
	 <%-- 类型enctype用multipart/form-data，这样可以把文件中的数据作为流式数据上传，不管是什么文件类型，均可上传。--%> 
	    <label class="control-label" style="margin-left: 11rem;">批量返现：</label><input type="file" name="file" size="50">
	    <br>
	    <br>
	    <br>
	   <input id="btnSubmit" class="btn btn-primary" type="submit" value="返现" style="margin-left: 17rem;"/>
	   <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" style="margin-left: 7rem;"/>
	</form:form>
</body>
</html>