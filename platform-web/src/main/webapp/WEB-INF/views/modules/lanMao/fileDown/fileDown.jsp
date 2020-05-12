<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>标的信息查询</title>
	<meta name="decorator" content="default"/>
<script type="text/javascript">
	$(document).ready(function() {
		$("#btnProjectInfoExport").click(function(){
					$("#searchForm").attr("action","${ctx}/lanMao/fileDown/fileDown/list");
					$("#searchForm").submit();
		});
		$("#btnSubmit").click(function(){			
					$("#searchForm2").attr("action","${ctx}/lanMao/fileDown/fileDown/down");
					$("#searchForm2").submit();
		});
	});
</script>
<style type="text/css">
	.select2-container {
		width: 300px;
	}
	.input-medium{
	width:250px
	}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active">对账文件</li>
		
	</ul>
	<form:form id="searchForm" modelAttribute="lanMaoWhiteList" action="#" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			
			<li><label class="label">对账日期：</label>
			<input name="startTime"  type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="对账日期"
					value="<fmt:formatDate value="${lanMaoWhiteList.startTime}" pattern="yyyy-MM-dd"/>"
					onFocus="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnProjectInfoExport" class="btn btn-primary" type="submit" value="文件确认"/></li>
			<li class="clearfix" style="color:red">${message}</li>
		</ul>
	</form:form> 
 <form:form id="searchForm2" modelAttribute="lanMaoWhiteList" action="#" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			
			<li><label class="label">对账日期：</label>
			<input name="endTime"  type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="对账日期"
					value="<fmt:formatDate value="${lanMaoWhiteList.endTime}" pattern="yyyy-MM-dd"/>"
					onFocus="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="文件下载"/></li>
			<li class="clearfix" style="color:red">${message2}</li>
		</ul>
	</form:form>  
</body>
</html>