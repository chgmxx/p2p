<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>教育栏目管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
//					 var nameImage =$("#nameImage").val();
//					 if(nameImage==''){
//						 alert('新闻图片不能为空');
//						 return false;
//					 }else 
				     if(CKEDITOR.instances.text.getData()==''){
						alert('新闻内容不能为空');
						return false;
					 } 
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
		<shiro:hasPermission name="cms:notice:view"><li><a href="${ctx}/cms/notice/?type=7">教育栏目列表</a></li></shiro:hasPermission>
		<li class="active">
			<a href="${ctx}/cms/notice/form?id=${notice.id }&type=3">教育栏目<shiro:hasPermission name="cms:notice:edit">${not empty notice.id ?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="cms:notice:edit">查看</shiro:lacksPermission></a>
		</li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="notice" action="${ctx}/cms/notice/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="type" value="${notice.type }" />
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">标题:</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="200" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">简介:</label>
			<div class="controls">
				<form:textarea path="head" htmlEscape="true" rows="4" maxlength="2000" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">文章来源:</label>
			<div class="controls">
				<form:input path="sources" htmlEscape="false" maxlength="600" class="input-xxlarge"/>
				<span class="help-inline"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">标签:</label>
			<div class="controls">
				<form:select path="bannerType" class="input-xlarge required">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('education_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<span class="help-inline"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">显示顺序:</label>
			<div class="controls">
				<form:input path="orderSum" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
				<span class="help-inline">数字越大位置越靠前</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">来源时间:</label>
			<div class="controls">
				<input id="sourcesDate" name="sourcesDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${notice.sourcesDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">教育图片:</label>
			<div class="controls">
				<form:hidden id="nameImage" path="logopath" htmlEscape="false" maxlength="255" class="input-xlarge" />
				<sys:ckfinder input="nameImage" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100"  />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">文章内容:</label>
			<div class="controls">
				<form:textarea id="text" htmlEscape="true" path="text" rows="4" maxlength="4000" class="input-xxlarge"/>
				<sys:ckeditor replace="text" uploadPath="/cms/notice" />
			</div>
		</div>
		
		<div class="form-actions">
			<shiro:hasPermission name="cms:notice:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div> 
	</form:form>
</body>
</html>