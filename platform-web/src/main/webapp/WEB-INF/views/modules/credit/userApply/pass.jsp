<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分配角色</title>
	<meta name="decorator" content="blank"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<script type="text/javascript">

		// 初始化
		$(document).ready(function(){
			$("#messageBox").show();
		});
	</script>
</head>
<body>
	<div class="form-actions">
		<sys:message content="审批信息已存库，请继续您的工作！" />
	</div>
</body>
</html>
