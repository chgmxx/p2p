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
			<a href="${ctx}/pro/wguarantee/form?id=${wGuaranteeCompany.id}&flag=view">
			担保公司<shiro:hasPermission name="pro:wguaranteecompany:view">查看</shiro:hasPermission></a>
		</li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="wGuaranteeCompany" action="" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<div class="control-group">
				<div class="span6">
					<label class="control-label">担保机构图标：</label>
					<div class="controls">
						<img alt="logo" src="${wGuaranteeCompany.wguaranteeLogo }" width="107px" height="72px">
					</div>
				</div>
				<div class="span6">
					<label class="control-label">电子签章URL：</label>
					<div class="controls">
						<img alt="logo" src="${wGuaranteeCompany.electronicSignUrl }" width="107px" height="72px">
					</div>
				</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">企业名称：</label>
				<div class="controls">
					<form:input path="name" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">注册日期：</label>
				<div class="controls">
					<input id="registerDate" name="registerDate" type="text" readonly="readonly" maxlength="20" class="input-medium" style="width:210px"
						value="<fmt:formatDate value="${wGuaranteeCompany.registerDate}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">注册地：</label>
				<div class="controls">
					<input type="text" readonly="readonly" value="${wGuaranteeCompany.area.name}" id="area" name="area.id">
				</div>
			</div>
				<div class="span6">
						<label class="control-label">详细地址：</label>
					<div class="controls">
						<form:input path="address" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
					</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">法人名称：</label>
				<div class="controls">
					<form:input path="corporation" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">电话：</label>
				<div class="controls">
					<form:input path="phone" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">税务登记号：</label>
				<div class="controls">
					<form:input path="taxCode" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">营业执照编号：</label>
				<div class="controls">
					<form:input path="businessNo" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
		<div class="span6">
				<label class="control-label">组织机构代码：</label>
				<div class="controls">
					<form:input path="organNo" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
				</div>
			</div>	
			<div class="span6">
				<label class="control-label">所属行业：</label>
				<div class="controls">
					<form:input path="industry" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">注册资金：</label>
				<div class="controls">
					<form:input path="registerAmount" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">资产净值：</label>
				<div class="controls">
					<form:input path="netAssetAmount" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">上年现金流量：</label>
				<div class="controls">
					<form:input path="lastYearCash" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
				</div>
			</div>
			 <div class="span6">
				<label class="control-label">网址：</label>
				<div class="controls">
					<form:input path="webSite" htmlEscape="false" maxlength="200" class="input-large" readonly="true" />
				</div>
			</div>
		</div>
		 
		<div class="control-group">
				<div class="span12">
					<label class="control-label">备注：</label>
					<div class="controls">
						<form:textarea path="remarks" htmlEscape="true" rows="3" maxlength="2000" class="input-large valid span7" readonly="true" />
					</div>
				</div>
		</div>
		<div class="control-group">
			<div class="span12">
				<label class="control-label">经营情况：</label>
				<div class="controls">
					<form:textarea path="runCase" htmlEscape="true" rows="3" maxlength="2000" class="input-large valid span7" readonly="true"/>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">简介：</label>
				<div class="controls">
							${wGuaranteeCompany.briefInfo}
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
			<label class="control-label">担保方案：</label>
			<div class="controls">
				${wGuaranteeCompany.guaranteeScheme}
			</div>
			</div>
		</div>
		<div class="span6">
			<div class="control-group">
				<label class="control-label">担保情况：</label>
				<div class="controls">
					${wGuaranteeCompany.guaranteeCase}
				</div>
			</div>
		</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed" >
		<thead>
			<tr><th>档案类型</th><th style="width:35%;">档案</th><th>时间</th></tr>
		</thead>
		<tbody>
		  	<c:forEach items="${annexFileList}" var="annexFile">
		  		<tr style="height:100px;">
					<td>${annexFile.label }</td>
					<td>
						<c:forEach items="${annexFile.urlList}" var="url">
							<img src="${imgUrl}${url}"  width="107px" height="72px"/>
						</c:forEach>
					</td>
					<td><fmt:formatDate value="${annexFile.createDate }" pattern="yyyy-MM-dd"/></td>
		  		</tr>
		  	</c:forEach>
		</tbody>
	</table>  
			<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	
</body>
</html>