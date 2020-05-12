<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>融资主体管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		// 单选按钮事件.
		Init_Wloan_Subject_Choice();
		// 个人表单.
		$("#personalForm").validate({
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

	// 融资主体类型选择.
	function Init_Wloan_Subject_Choice() {

		$("#wloan_subject_select").change(function() {
			var type_value = $(this).children('option:selected').val();
			if (type_value == '1') {
				document.getElementById("personal_id").style.display = "block";
				document.getElementById("enterprise_id").style.display = "none";
				document.getElementById("g_name").style.display = "block";
				document.getElementById("c_name").style.display = "none";
			} else if (type_value == '2') {
				document.getElementById("personal_id").style.display = "none";
				document.getElementById("enterprise_id").style.display = "block";
				document.getElementById("g_name").style.display = "none";
				document.getElementById("c_name").style.display = "block";
			}
		});
	}//--
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/wloan_subject/wloanSubject/">融资主体列表</a></li>
		<li class="active"><a href="${ctx}/wloan_subject/wloanSubject/addForm?id=${wloanSubject.id}">融资主体添加</a></li>
	</ul>
	<br />
	<form:form id="personalForm" modelAttribute="wloanSubject" action="${ctx}/wloan_subject/wloanSubject/addSave" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<!-- 融资主体类别. -->
		<div class="control-group">
			<div class="span6">
				<label class="control-label">类型：</label>
				<div class="controls">
					<form:select path="type" class="input-large" id="wloan_subject_select">
						<form:options items="${fns:getDictList('wloan_subject_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
	
		<div class="control-group">
			<div class="span6">
				<div id="g_name" style="display: block;"><label class="control-label">个人姓名：</label></div>
				<div id="c_name" style="display: none;"><label class="control-label">企业名称：</label></div>
			<div class="controls">
				<form:input path="companyName" htmlEscape="false" maxlength="50" class="input-large required" />
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
			<div class="span6">
				<label class="control-label">联系电话：</label>
				<div class="controls">
					<form:input path="loanPhone" htmlEscape="false" maxlength="11" class="input-large required" onkeyup="value=value.replace(/[^\d]/g,'')" />
					<span class="help-inline"><font color="red">*</font></span>
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
					<input name="registerDate" type="text" readonly="readonly" maxlength="20" class="input-large Wdate " value="<fmt:formatDate value="${wloanSubject.registerDate}" pattern="yyyy-MM-dd"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" />
				</div>
			</div>
			<div class="span6">
					<label class="control-label">所属行业：</label>
				<div class="controls">
					<form:input path="industry" htmlEscape="false" maxlength="100" class="input-large" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
					<label class="control-label">营业执照编号：</label>
					<div class="controls">
						<form:input path="businessNo" htmlEscape="false" maxlength="64" class="input-large " />
					</div>
				</div>
					<div class="span6">
					<label class="control-label">组织机构代码：</label>
					<div class="controls">
						<form:input path="organNo" htmlEscape="false" maxlength="64" class="input-large " />
					</div>
				</div>
		</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">税务登记号：</label>
					<div class="controls">
						<form:input path="taxCode" htmlEscape="false" maxlength="64" class="input-large " />
					</div>
			</div>
			<div class="span6">
				<label class="control-label">上年现金流量：</label>
				<div class="controls">
					<form:input path="lastYearCash" htmlEscape="false" class="input-large " onkeyup="value=value.replace(/[^\d\.]/g,'')" />
					<span class="help-inline">RMB</span>
				</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">注册资金：</label>
					<div class="controls">
						<form:input path="registerAmount" htmlEscape="false" class="input-large " onkeyup="value=value.replace(/[^\d\.]/g,'')" />
						<span class="help-inline">RMB</span>
					</div>
				</div>
				<div class="span6">
				<label class="control-label">资产净值：</label>
				<div class="controls">
					<form:input path="netAssetAmount" htmlEscape="false" class="input-large " onkeyup="value=value.replace(/[^\d\.]/g,'')" />
					<span class="help-inline">RMB</span>
				</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">网址：</label>
					<div class="controls">
						<form:input path="webSite" htmlEscape="false" maxlength="64" class="input-large " />
					</div>
				</div>
			</div>
		<div class="control-group">
			<div class="span14">
				<label class="control-label">名称简介：</label>
				<div class="controls">
					<form:textarea path="briefName" htmlEscape="false" maxlength="1000" class="input-xxlarge " />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span14">
					<label class="control-label">简介信息：</label>
				<div class="controls">
					<form:textarea path="briefInfo" htmlEscape="false" maxlength="1000" class="input-xxlarge " />
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span14">
			<label class="control-label">经营情况：</label>
			<div class="controls">
				<form:textarea path="runCase" htmlEscape="false" maxlength="500" class="input-xxlarge " />
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
					<form:input path="loanIdCard" htmlEscape="false" maxlength="18" class="input-large required" onkeyup="value=value.replace(/[^\d\a-z\A-Z]/g,'')" />
				</div>
			</div>
		</div>
			</div>
		<span class="help-inline">收款人</span>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">姓名：</label>
				<div class="controls">
					<form:input path="cashierUser" htmlEscape="false" maxlength="64" class="input-large required" />
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">身份证：</label>
				<div class="controls">
					<form:input path="cashierIdCard" htmlEscape="false" maxlength="18" class="input-large required" onkeyup="value=value.replace(/[^\d\a-z\A-Z]/g,'')" />
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">银行卡：</label>
				<div class="controls">
					<form:input path="cashierBankNo" htmlEscape="false" maxlength="64" class="input-large required" onkeyup="value=value.replace(/[^\d]/g,'')" />
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
				<div class="span6">
					<label class="control-label">银行预留手机：</label>
				<div class="controls">
					<form:input path="cashierBankPhone" htmlEscape="false" maxlength="11" class="input-large required" onkeyup="value=value.replace(/[^\d]/g,'')" />
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">开户行所在地：</label>
				<div class="controls">
					<sys:treeselect id="area" name="area.id" value="${wloanSubject.area.id}" labelName="area.name" labelValue="${wloanSubject.area.name}" title="区域" url="/sys/area/treeData" cssClass="required" />
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">开户行名称：</label>
				<div class="controls">
					<form:input path="cashierBankAdderss" htmlEscape="false" maxlength="255" class="input-large required" />
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">开户银行：</label>
				<div class="controls">
					<form:select path="cashierBankCode" class="input-large" id="wloan_subject_select">
						<form:option value="" label="请选择" />
						<form:options items="${fns:getDictList('bank_code')}" itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				<font color="red">（如收款卡为公户时必填，私户时不可填写任何信息）</font>
				</div>
			</div>
		</div>
		<div class="control-group">
		<div class="span14">
			<label class="control-label">备注信息：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" maxlength="255" class="input-xxlarge " />
			</div>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" />&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>