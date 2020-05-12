<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>粉丝详情</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
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
		 <li><a href="${ctx}/wechat/accountfans/list">粉丝管理</a></li>
		 <li class="active"><a href="#">粉丝详情</a></li>
	</ul>
	<br/>
		<div class="control-group">
			<label class="control-label">头像:</label>
			<div class="controls">
				 
				 <img style="width:64px;height:64px;margin:5px;" src="${fans.headimgurl }"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">昵称:</label>
			<div class="controls">
				 ${fans.nicknameStr}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址:</label>
			<div class="controls">
				 ${fans.province}-${fans.city}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">openId:</label>
			<div class="controls">
				${fans.openId}
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div> 
</body>
</html>