<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
	<body style="margin: 0px;padding: 0px;">
		<div style="padding:0px 10px;">
			<div style="font-weight:bold;font-size:18px;margin-top:10px;">${msgNews.title}</div>
		
			<div style="margin:20px 0px;">
				<img src="${msgNews.picpath}" style="width:100%;border:none;">
			</div>
			
			<div>
				${msgNews.description}
			</div>
		</div>
	</body>
</html>

