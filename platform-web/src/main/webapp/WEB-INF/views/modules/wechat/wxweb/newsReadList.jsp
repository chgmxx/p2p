<!DOCTYPE HTML>
<html>
	<head>
		<meta charset="UTF-8">
		<meta content="width=device-width,user-scalable=no" name="viewport">
		<script type="text/javascript" src="${s.base}/res/jquery-1.9.1.min.js"></script>
		<style type="text/css">
			.msg-item{
				text-align:center;
				padding:5px;
				margin:10px 5px;
				background-color: #fff;
				border-radius:5px;
				min-height: 80px;
			}
			.msg-item a{
				text-decoration: none;
			}
			.msg-item .nav-a:hover{
				text-decoration: underline;
			}
			.msg-item img{
				border-radius:5px;
				border: none;
			}
			.msg-content{
				text-align:left;
				font-size:16px;
				line-height:20px;
				min-height: 50px;
			}
			.msg-content div{
				font-size:16px;
			}
		</style>
	</head>
	
	<body style="margin: 0px;padding:10px; background-color: #eeeeee;">
		<section style="width:100%;" >
			
			<#list pageList as row>
			<div class="msg-item">
				<div class="msg-content">
					<div style="margin-top:2px;text-align:left;font-size:12px;color:#6f6f6f;">
					${row.createTimeStr}
					</div>
					
					<#list row.msgNewsList as news>
					<#if news.fromurl?? && news.fromurl != ''>
					<a href="${news.fromurl}">
					<#else>
					<a href="${news.url}">
					</#if>
						<div style="padding:5px 0px;border-top:1px solid #f0f0f0;height: 40px;">
							<#if news.picpath?? >
							<img src="${news.picpath}" style="width:40px;height:40px;border-radius:0px;float:left;">
							</#if>
							<div style="margin-left:5px;font-size: 14px;float: left;width:250px;color:#000">${news.title}</div>
							<div class="clearfloat"></div>
						</div>
					</a>
					</#list>
				</div>
			</div>
			</#list>
			
		</section>
	</body>
</html>


