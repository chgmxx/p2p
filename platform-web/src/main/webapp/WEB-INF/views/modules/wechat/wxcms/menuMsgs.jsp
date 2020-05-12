<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title> </title>
	<meta name="decorator" content="default"/>
	 <script type="text/javascript">
			function showMsg(type){
				if(type == '1'){//news
					$('#id_msgtype').val('news');
					$('#id_news_msg').css('display','block');
					$('#id_text_msg').css('display','none');
					
					$('#id_news_span').attr('class','btn btn-primary');
					$('#id_text_span').attr('class','btn');
				}else{
					$('#id_msgtype').val('text');
					$('#id_news_msg').css('display','none');
					$('#id_text_msg').css('display','block');
					
					$('#id_text_span').attr('class','btn btn-primary');
					$('#id_news_span').attr('class','btn');
				}
			}
		</script>
</head>
<body>
	 	<input type="hidden" id="id_msgtype" name="msgtype" value="news"/>
		<input id="id_news_span" class="btn btn-primary" onclick="showMsg('1')" type="button" value="图文消息"/>
		<input id="id_text_span" class="btn" onclick="showMsg('2')" type="button" value="文本消息"/>
<div style="text-align:left;margin-top:20px;">		
	<div id="id_news_msg">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead><tr><th>图文消息</th><th>Id</th><th>标题</th>  </tr></thead>
			<tbody>
				<c:forEach items="${newsList}" var="news" >
					<tr>
						<td>
							<input type="checkbox" value="${news.id}" name="checkname"/>
						</td>
						<td> 
							 ${news.id}
						</td>
						<td>
							${news.title}					 
						</td>
					</tr>
				</c:forEach> 
			</tbody>
		</table>  
	</div>
	<div id="id_text_msg" style="display:none;">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead><tr><th>文本消息</th><th>Id</th><th>描述</th></tr></thead>
			<tbody>
				<c:forEach items="${textList}" var="text">
					<tr>
						<td>
							<input value="${text.id}" type="radio" name="radioname"/>
						</td>
						<td> 
							 ${text.id}
						</td>
						<td>
							${text.content}					 
						</td>
					</tr>
				</c:forEach> 
			</tbody>
		</table>  
	</div>
</div>		
</body>
</html>