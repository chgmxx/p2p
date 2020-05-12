<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>活期融资项目管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
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
		<li><a href="${ctx}/current/project/wloanCurrentProject/">活期融资项目列表</a></li>
		<li class="active"><a href="${ctx}/current/project/wloanCurrentProject/form?id=${wloanCurrentProject.id}">活期融资项目<shiro:hasPermission name="current:project:wloanCurrentProject:edit">${not empty wloanCurrentProject.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="current:project:wloanCurrentProject:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="wloanCurrentProject" action="${ctx}/current/project/wloanCurrentProject/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">项目编号：</label>
				<div class="controls">
					<form:input path="sn" htmlEscape="false" maxlength="32" class="input-xlarge required" style="width:250px"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">项目名称：</label>
				<div class="controls">
					<form:input path="name" htmlEscape="false" maxlength="55" class="input-xlarge required" style="width:250px"/>&nbsp;
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">融资主体：</label>
				<div class="controls">
					<form:select path="subjectId" class="input-xlarge required" style="width:264px">
						<form:option value="" label="请选择"/>
						<c:forEach items="${wSubjects}" var="wSubjects">
							<form:option value="${wSubjects.id }" label="${wSubjects.companyName }"/>
						</c:forEach>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">担保机构：</label>
				<div class="controls">
					<form:select path="guaranteeId" class="input-xlarge required" style="width:264px" id="wloan_guarant_select">
						<form:option value="0" label="请选择"/>
						<c:forEach items="${wgCompanys}" var="wgCompanys">
							<form:option value="${wgCompanys.id }" label="${wgCompanys.name }"/>
						</c:forEach>
					</form:select>&nbsp;
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">融资档案：</label>
				<div class="controls">
					<form:select path="docId" class="input-xlarge required" style="width:264px" id="wloan_doc_select">
						<form:option value="0" label="请选择"/>
						<c:forEach items="${wloanDocs}" var="wloanDocs">
							<form:option value="${wloanDocs.id }" label="${wloanDocs.name }"/>
						</c:forEach>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">融资金额：</label>
				<div class="controls">
					<form:input path="amount" htmlEscape="false" class="input-large number required" style="width:216px"/>
					<span class="help-inline">RMB</span>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">手续费：</label>
				<div class="controls">
					<form:input path="feeRate" htmlEscape="false" class="input-xlarge number required" style="width:230px"/>
					<span class="help-inline">%</span>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">保证金：</label>
				<div class="controls">
					<form:input path="marginPercentage" htmlEscape="false" class="input-large number required" style="width:216px"/>
					<span class="help-inline">&nbsp;&nbsp;&nbsp;%</span>
					<span class="help-inline"><font color="red">&nbsp;*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">状态：</label>
				<div class="controls">
					<input type="text" readonly="readonly" value="草  稿" style="width:250px">
					<form:hidden path="state" value="1"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">期限：</label>
				<div class="controls">
					<form:select path="span" class="input-large required" style="width:230px">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('regular_wloan_span')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<span class="help-inline">&nbsp;&nbsp;天</span>
					<span class="help-inline"><font color="red">&nbsp;&nbsp;*</font></span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">担保函编号：</label>
				<div class="controls">
					<form:input path="guaranteeSn" htmlEscape="false" maxlength="32" class="input-xlarge " style="width:250px"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">备注：</label>
				<div class="controls">
					<form:textarea path="remark" htmlEscape="false" maxlength="255" class="input-xxlarge" rows="4"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">担保方案：</label>
				<div class="controls">
					<form:textarea path="guaranteeScheme" htmlEscape="false" maxlength="255" class="input-xxlarge" rows="4"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">资金用途：</label>
				<div class="controls">
					<form:textarea path="purpose" htmlEscape="false" class="input-xxlarge" rows="4" maxlength="500"/>
				</div>
			</div>
		</div>	
		
		<div class="form-actions">
			<shiro:hasPermission name="current:project:wloanCurrentProject:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>