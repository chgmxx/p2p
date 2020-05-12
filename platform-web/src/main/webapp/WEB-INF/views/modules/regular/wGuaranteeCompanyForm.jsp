<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>担保公司</title>
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
		<shiro:hasPermission name="pro:wguaranteecompany:view"><li><a href="${ctx}/pro/wguarantee/">担保公司</a></li></shiro:hasPermission>
		<li class="active">
			<a href="${ctx}/pro/wguarantee/form">
			<shiro:hasPermission name="pro:wguaranteecompany:edit">担保公司${not empty wGuaranteeCompany.id ?'修改':'添加'}</shiro:hasPermission>
			<shiro:lacksPermission name="pro:wguaranteecompany:view">查看</shiro:lacksPermission>
			</a>
		</li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="wGuaranteeCompany" action="${ctx}/pro/wguarantee/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
				<div class="span6">
					<label class="control-label">担保公司图标：</label>
					<div class="controls">
						<form:hidden id="nameImage" path="wguaranteeLogo" htmlEscape="false" maxlength="255" class="input-large" />
						<sys:ckfinder input="nameImage" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100"  />
					</div>
				</div>
				
				<div class="span6">
					<label class="control-label">电子签章URL：</label>
					<div class="controls">
						<form:hidden id="electronicSignUrl" path="electronicSignUrl" htmlEscape="false" maxlength="255" class="input-large" />
							<sys:ckfinder input="electronicSignUrl" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100"  />
					</div>
				</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">企业名称：</label>
				<div class="controls">
					<form:input path="name" htmlEscape="false" maxlength="200" class="input-large required"  />
						<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">注册日期：</label>
				<div class="controls">
				<input id="registerDate" name="registerDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" style="width:210px"
						value="<fmt:formatDate value="${wGuaranteeCompany.registerDate}" pattern="yyyy-MM-dd"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">注册地：</label>
				<div class="controls">
					  <sys:treeselect id="area"  name="area.id" value="${wGuaranteeCompany.area.id}" labelName="area.name" labelValue="${wGuaranteeCompany.area.name}"
						title="区域" url="/sys/area/treeData" cssClass="input-large" />
				</div>
			</div>
				<div class="span6">
						<label class="control-label">详细地址：</label>
					<div class="controls">
						<form:input path="address" htmlEscape="false" maxlength="200" class="input-large"  />
					</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">法人名称：</label>
				<div class="controls">
					<form:input path="corporation" htmlEscape="false" maxlength="200" class="input-large required"  />
						<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">电话：</label>
				<div class="controls">
					<form:input path="phone" htmlEscape="false" maxlength="11" class="input-large required" onkeyup="value=value.replace(/[^\d]/g,'')"/>
						<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">税务登记号：</label>
				<div class="controls">
					<form:input path="taxCode" htmlEscape="false" maxlength="200" class="input-large required"  />
						<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">营业执照编号：</label>
				<div class="controls">
					<form:input path="businessNo" htmlEscape="false" maxlength="200" class="input-large required"  />
						<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		<div class="control-group">
		<div class="span6">
				<label class="control-label">组织机构代码：</label>
				<div class="controls">
					<form:input path="organNo" htmlEscape="false" maxlength="200" class="input-large"  />
				</div>
			</div>	
			<div class="span6">
				<label class="control-label">所属行业：</label>
				<div class="controls">
					<form:input path="industry" htmlEscape="false" maxlength="200" class="input-large"  />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">注册资金：</label>
				<div class="controls">
					<form:input path="registerAmount" htmlEscape="false" maxlength="200" class="input-large number"  />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">资产净值：</label>
				<div class="controls">
					<form:input path="netAssetAmount" htmlEscape="false" maxlength="200" class="input-large number"  />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">上年现金流量：</label>
				<div class="controls">
					<form:input path="lastYearCash" htmlEscape="false" maxlength="200" class="input-large number"  />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">网址：</label>
				<div class="controls">
					<form:input path="webSite" htmlEscape="false" maxlength="200" class="input-large"  />
				</div>
			</div>
		</div>
		<div class="control-group">
				<div class="span12">
					<label class="control-label">备注：</label>
					<div class="controls">
						<form:textarea path="remarks" htmlEscape="true" rows="3" maxlength="2000" class="input-large valid span7"  />
					</div>
				</div>
		</div>
		<div class="control-group">
			<div class="span12">
				<label class="control-label">经营情况：</label>
				<div class="controls">
					<form:textarea path="runCase" htmlEscape="true" rows="3" maxlength="2000" class="input-large valid span7" />
				</div>
			</div>
		</div>
	<div class="control-group">
		<div class="span12">
			<label class="control-label">简介：</label>
			<div class="controls">
				<form:textarea id="briefInfo" htmlEscape="true" path="briefInfo" rows="4" maxlength="4000" class="input-large"/>
				<sys:ckeditor replace="briefInfo" uploadPath="/pro/wguarantee" />
			</div>
			</div>
		</div>
		
		<div class="control-group">
		<div class="span12">
			<label class="control-label">担保方案：</label>
			<div class="controls">
				<form:textarea id="guaranteeScheme" htmlEscape="true" path="guaranteeScheme" rows="4" maxlength="4000" class="input-large"/>
				<sys:ckeditor replace="guaranteeScheme" uploadPath="/pro/wguarantee" />
			</div>
			</div>
		</div>
		<div class="control-group">
		<div class="span12">
			<label class="control-label">担保情况：</label>
			<div class="controls">
				<form:textarea id="guaranteeCase" htmlEscape="true" path="guaranteeCase" rows="4" maxlength="4000" class="input-large"/>
				<sys:ckeditor replace="guaranteeCase" uploadPath="/pro/wguarantee" />
			</div>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="pro:wguaranteecompany:view"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

</body>
</html>