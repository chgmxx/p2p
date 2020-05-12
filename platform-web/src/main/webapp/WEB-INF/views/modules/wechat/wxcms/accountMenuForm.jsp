<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>菜单添加</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			menuChange();
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
		
		function menuChange(){
			var menuType = $("#id_type  option:selected").val();
			var eventType = $("#id_event_type  option:selected").val();
			if(menuType == 'click'){
				if(eventType == 'key'){
					$("#id_keymsg").css("display","inline");
					$("#id_fixmsg").css("display","none");
					$("#id_msgIds").val('');
				} else{
					$("#id_fixmsg").css("display","inline");
					$("#id_keymsg").css("display","none");
					$("#inputcode").val('');
				}
				$("#id_msg").css("display","inline");
				$("#id_view").css("display","none");
				$("#id_url").val('');
				
			} else{
				$("#id_view").css("display","inline");
				$("#id_msg").css("display","none");
				$("#id_fixmsg").css("display","none");
				$("#id_keymsg").css("display","none");
				$("#id_msgIds").val('');
				$("#inputcode").val('');
				
			}
		}
	 
		function msgIdsClick(){
			var msgtype = $("#id_msgs_frame").contents().find('input[name="msgtype"]').val();
			if(msgtype == 'news'){
        		var val = [];
        		$("#id_msgs_frame").contents().find('input[name="checkname"]:checked').each(function(){ 
        			val.push($(this).val())
                })
        		if(val.length > 0){
            		$("#id_msgIds").val(val.join(','));
            	}
        	}else{
        		var val = $("#id_msgs_frame").contents().find('input[name="radioname"]:checked').val();
        		if(val != undefined){
            		$("#id_msgIds").val(val);
            	}
        	}
			$('#myModal').modal('hide');
		}
		  
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/wechat/accountmenugroup/list">菜单组</a></li>
		<shiro:hasPermission name="wechat:accountmenugroup:edit">
			<li><a href="${ctx}/wechat/accountmenugroup/form">添加菜单组</a></li>
		</shiro:hasPermission> 
		<shiro:hasPermission name="wechat:accountmenu:edit">
			<li class="active">
				<a href="${ctx}/wechat/accountmenu/form?id=${accountMenu.id }">${not empty accountMenu.id?'修改':'添加'}菜单</a>
			</li>
		</shiro:hasPermission> 
		
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="accountMenu" action="${ctx}/wechat/accountmenu/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="accountMenuGroup.id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">名称:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="200" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
						
		<div class="control-group">
			<label class="control-label">一级菜单 :</label>
			<div class="controls">
				<form:select path="parentid" class="input-xlarge" style="width:100px" >
					<form:option value="0" label="一级菜单"/>
					<form:options items="${menuList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">顺序:</label>
			<div class="controls">
				<form:select path="sort" class="input-large" style="width:100px" >
					<form:options items="${fns:getDictList('token_msg_count')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">菜单类型:</label>
			<div class="controls">
				<form:select id="id_type" path="mtype" class="input-large" style="width:100px"  onchange="menuChange()">
					<form:options items="${fns:getDictList('accountmenu_mtype')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline">消息：点击菜单时回复消息；链接：点击菜单打开链接-${accountMenu.mtype}</span>
			</div>
		</div>
 		<div class="control-group" id="id_msg"  >
			<label class="control-label">消息类型:</label>
			<div class="controls">
				<form:select id="id_event_type" path="eventType" class="input-large" style="width:100px" onchange="menuChange()">
					<form:options items="${fns:getDictList('accountmenu_eventType')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		
 		<div class="control-group" id="id_keymsg">
			<label class="control-label">关键字 :</label>
			<div class="controls">
				<form:input id="inputcode" path="inputcode" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<span class="help-inline">消息的关键字</span>
			</div>
		</div>
 
 		<div class="control-group" id="id_fixmsg" style="display:none;">
			<label class="control-label">指定消息:</label>
			<div class="controls">
				<form:input id="id_msgIds" path="msgBase.id" htmlEscape="false" maxlength="255" class="input-xlarge" readonly="readonly"/>
				<a href="#myModal" role="button" class="btn btn-primary" data-toggle="modal">选择</a>
			</div>
		</div>
		
 		<div class="control-group" id="id_view" style="display: none;">
			<label class="control-label">链接URL:</label>
			<div class="controls">
				<form:input id="id_url" path="url" htmlEscape="false" maxlength="255" class="input-xlarge" readonly="readonly"/>
				<span  style="color:red">*</span>
				
			</div>
		</div>
 
		<div class="form-actions">
			<shiro:hasPermission name="wechat:accountmenu:edit">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div> 
	</form:form>
	
	
	<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
		    <h3 id="myModalLabel">选择消息</h3>
		  </div>
		  <div class="modal-body">
		    <iframe id="id_msgs_frame" style="width:100%;height:100%;border:none;" src="${ctx}/wechat/msgbase/menuMsgs">
		    </iframe> 
		  </div>
		  <div class="modal-footer">
		    <button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
		    <button class="btn btn-primary" onclick="msgIdsClick()">确定</button>
		  </div>
	</div>
</body>
</html>

 