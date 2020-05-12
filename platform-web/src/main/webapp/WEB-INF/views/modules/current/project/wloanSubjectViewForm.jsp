<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>融资主体管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#inputForm").validate({
			submitHandler : function(form) {
				loading('正在提交，请稍等...');
				form.submit();
			},
			errorContainer : "#messageBox",
			errorPlacement : function(error, element) {
				$("#messageBox").text("输入有误，请先更正。");
				if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
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
		<li><a href="${ctx}/current/project/wloanCurrentProject/check?id=${wloanCurrentProject.id}">活期融资项目
		<c:if test="${wloanCurrentProject.state == '1'}">审核</c:if>
		<c:if test="${wloanCurrentProject.state == '2'}">上线</c:if>
		<c:if test="${wloanCurrentProject.state == '3'}">查看</c:if>
		<c:if test="${wloanCurrentProject.state == '4'}">查看</c:if>
		<c:if test="${wloanCurrentProject.state == '5'}">查看</c:if>
		</a></li>
		<li class="active"><a href="#">融资主体查看</a></li>
	</ul><br/><br>
	
	<form:form id="inputForm" modelAttribute="wloanSubject" action="#" method="post" class="form-horizontal">
		<!-- 融资主体类别. -->
		<div class="control-group">
		<div class="span6">
			<label class="control-label">类型：</label>
			<div class="controls">
				<form:select path="type" class="input-large" readonly="true" id="wloan_subject_select" disabled="true">
					<form:options items="${fns:getDictList('wloan_subject_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="span6">
				<label class="control-label">所在地/注册地：</label>
				<div class="controls">
					<form:input path="area.name" htmlEscape="false" maxlength="50" class="input-large" readonly="true" />
				</div>
			</div>
		</div>
			<div class="control-group">
			<div class="span6">
			<label class="control-label">企业名称：</label>
			<div class="controls">
				<form:input path="companyName" htmlEscape="false" maxlength="50" class="input-large" readonly="true" />
			</div>
		</div>
			<div class="span6">
				<label class="control-label">联系电话：</label>
				<div class="controls">
					<form:input path="loanPhone" htmlEscape="false" maxlength="11" class="input-large" readonly="true" onkeyup="value=value.replace(/[^\d]/g,'')" />
				</div>
			</div>
		</div>
		<!-- 企业. -->
		<div id="enterprise_id" style="display: none;">
		<span class="help-inline">借款企业</span>
		<div class="control-group">
		<div class="span6">
				<label class="control-label">成立日期：</label>
				<div class="controls">
					<input name="registerDate" type="text" readonly="readonly" maxlength="20" class="input-large Wdate " value="<fmt:formatDate value="${wloanSubject.registerDate}" pattern="yyyy-MM-dd"/>" />
				</div>
			</div>
				<div class="span6">
					<label class="control-label">所属行业：</label>
				<div class="controls">
					<form:input path="industry" htmlEscape="false" maxlength="100" class="input-large" readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
					<label class="control-label">营业执照编号：</label>
					<div class="controls">
						<form:input path="businessNo" htmlEscape="false" maxlength="64" class="input-large " readonly="true"/>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">组织机构代码：</label>
					<div class="controls">
						<form:input path="organNo" htmlEscape="false" maxlength="64" class="input-large " readonly="true"/>
					</div>
				</div>
		</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">税务登记号：</label>
					<div class="controls">
						<form:input path="taxCode" htmlEscape="false" maxlength="64" class="input-large " readonly="true"/>
					</div>
			</div>
			<div class="span6">
				<label class="control-label">上年现金流量：</label>
				<div class="controls">
					<form:input path="lastYearCash" htmlEscape="false" class="input-large " onkeyup="value=value.replace(/[^\d\.]/g,'')" readonly="true"/>
					<span class="help-inline">RMB</span>
				</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">注册资金：</label>
					<div class="controls">
						<form:input path="registerAmount" htmlEscape="false" class="input-large " onkeyup="value=value.replace(/[^\d\.]/g,'')" readonly="true"/>
						<span class="help-inline">RMB</span>
					</div>
				</div>
				<div class="span6">
				<label class="control-label">资产净值：</label>
				<div class="controls">
					<form:input path="netAssetAmount" htmlEscape="false" class="input-large " onkeyup="value=value.replace(/[^\d\.]/g,'')" readonly="true"/>
					<span class="help-inline">RMB</span>
				</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">网址：</label>
					<div class="controls">
						<form:input path="webSite" htmlEscape="false" maxlength="64" class="input-large " readonly="true"/>
					</div>
				</div>
			</div>
		<div class="control-group">
			<div class="span14">
				<label class="control-label">名称简介：</label>
				<div class="controls">
					<form:textarea path="briefName" htmlEscape="false" maxlength="1000" class="input-xxlarge " readonly="true"/>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span14">
					<label class="control-label">简介信息：</label>
				<div class="controls">
					<form:textarea path="briefInfo" htmlEscape="false" maxlength="1000" class="input-xxlarge " readonly="true"/>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span14">
			<label class="control-label">经营情况：</label>
			<div class="controls">
				<form:textarea path="runCase" htmlEscape="false" maxlength="500" class="input-xxlarge " readonly="true"/>
			</div>
			</div>
		</div>
		</div>
		<!-- 个人. -->
		<div id="personal_id" style="display: block;">
		<span class="help-inline">借款人</span>
		<div class="control-group">
			<div class="span6">
					<label class="control-label">身份证：</label>
				<div class="controls">
					<form:input path="loanIdCard" htmlEscape="false" maxlength="18" class="input-large" readonly="true" onkeyup="value=value.replace(/[^\d\a-z\A-Z]/g,'')" />
				</div>
			</div>
		</div>
		</div>
		<span class="help-inline">收款人</span>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">姓名：</label>
				<div class="controls">
					<form:input path="cashierUser" htmlEscape="false" maxlength="64" class="input-large" readonly="true" />
					
				</div>
			</div>
			<div class="span6">
				<label class="control-label">身份证：</label>
				<div class="controls">
					<form:input path="cashierIdCard" htmlEscape="false" maxlength="18" class="input-large" readonly="true" onkeyup="value=value.replace(/[^\d\a-z\A-Z]/g,'')" />
					
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">银行卡：</label>
				<div class="controls">
					<form:input path="cashierBankNo" htmlEscape="false" maxlength="64" class="input-large" readonly="true" onkeyup="value=value.replace(/[^\d]/g,'')" />
				</div>
			</div>
			<div class="span6">
				<label class="control-label">开户银行：</label>
				<div class="controls">
					<form:input path="cashierBankAdderss" htmlEscape="false" maxlength="255" class="input-large" readonly="true" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
					<label class="control-label">银行预留手机：</label>
				<div class="controls">
					<form:input path="cashierBankPhone" htmlEscape="false" maxlength="11" class="input-large" readonly="true" onkeyup="value=value.replace(/[^\d]/g,'')" />
				</div>
			</div>
		</div>
		<div class="control-group">
		<div class="span14">
			<label class="control-label">备注信息：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" maxlength="255" class="input-xxlarge" readonly="true"/>
			</div>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>