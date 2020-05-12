<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>融资主体表单页</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		// 单选按钮事件.
		Init_Wloan_Subject_Choice();
		//
		$('#loanApplyId').change(function(){
			// alert($(this).children('option:selected').val());
			var loanApplyId = $(this).children('option:selected').val();
			selectLoanApplyId(loanApplyId);
		});
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
		
		// 默认选择，个人或者企业的页面展示效果.
		var type_value = $("#wloan_subject_select").children('option:selected').val();
		if (type_value == '1') {
			document.getElementById("personal_id").style.display = "block";
			document.getElementById("enterprise_id").style.display = "none";
			document.getElementById("p_name").style.display = "block";
			document.getElementById("e_name").style.display = "none";
			var isEntrustedPay = $("#isEntrustedPay").children('option:selected').val(); // 受托支付标识.
			if (isEntrustedPay == '0') {
				// console.log("isEntrustedPay 2 = " + isEntrustedPay);
				document.getElementById("personal_id").style.display = "block"; // 借款人信息.
				document.getElementById("is_entrusted_pay_id").style.display = "none"; // 受托人信息.
				document.getElementById("cashier_bank_issuer_id").style.display = "none"; // 联行号.
			} else if (isEntrustedPay == '1') {
				// console.log("isEntrustedPay 3 = " + isEntrustedPay);
				document.getElementById("personal_id").style.display = "block"; // 借款人信息.
				document.getElementById("is_entrusted_pay_id").style.display = "block"; // 受托人信息.
				var cashierBankNoFlag = $("#cashierBankNoFlag").children('option:selected').val();
				if (cashierBankNoFlag == '1') {
					document.getElementById("cashier_bank_issuer_id").style.display = "block"; // 联行号.
				} else if (type_value == '2') {
					document.getElementById("cashier_bank_issuer_id").style.display = "none"; // 联行号.
				}
			}
		} else if (type_value == '2') {
			document.getElementById("personal_id").style.display = "block";
			document.getElementById("enterprise_id").style.display = "block";
			document.getElementById("p_name").style.display = "none";
			document.getElementById("e_name").style.display = "block";
			var isEntrustedPay = $("#isEntrustedPay").children('option:selected').val(); // 受托支付标识.
			if (isEntrustedPay == '0') {
				// console.log("isEntrustedPay 2 = " + isEntrustedPay);
				document.getElementById("personal_id").style.display = "block"; // 借款人信息.
				document.getElementById("is_entrusted_pay_id").style.display = "none"; // 受托人信息.
				document.getElementById("cashier_bank_issuer_id").style.display = "none"; // 联行号.
			} else if (isEntrustedPay == '1') {
				// console.log("isEntrustedPay 3 = " + isEntrustedPay);
				document.getElementById("personal_id").style.display = "block"; // 借款人信息.
				document.getElementById("is_entrusted_pay_id").style.display = "block"; // 受托人信息.
				var cashierBankNoFlag = $("#cashierBankNoFlag").children('option:selected').val();
				if (cashierBankNoFlag == '1') {
					document.getElementById("cashier_bank_issuer_id").style.display = "block"; // 联行号.
				} else if (type_value == '2') {
					document.getElementById("cashier_bank_issuer_id").style.display = "none"; // 联行号.
				}
			}
		}
		
		// 默认选择营业执照或者统一社会信用代码的页面展示.
		var business_license_type_value = $("#businessLicenseType").val();
		if (business_license_type_value == 'BLC') {
			document.getElementById("taxRegCertNo_OrgCode_id").style.display = "block"; // 组织机构代码与税务登记号.
		} else if (business_license_type_value == 'USCC') {
			// 清空组织机构代码与税务登记号.
				$("#organNo").val(""); // 组织机构代码.
				$("#taxCode").val(""); // 税务登记号.
			document.getElementById("taxRegCertNo_OrgCode_id").style.display = "none"; // 组织机构代码与税务登记号.
		}
		
		// 融资主体类型，1：个人，2：企业.
		$("#wloan_subject_select").change(function() {
			var type_value = $(this).children('option:selected').val();
			if (type_value == '1') {
				document.getElementById("personal_id").style.display = "block";
				document.getElementById("enterprise_id").style.display = "none";
				document.getElementById("p_name").style.display = "block";
				document.getElementById("e_name").style.display = "none";
			} else if (type_value == '2') {
				document.getElementById("personal_id").style.display = "block";
				document.getElementById("enterprise_id").style.display = "block";
				document.getElementById("p_name").style.display = "none";
				document.getElementById("e_name").style.display = "block";
				document.getElementById("is_entrusted_pay_id").style.display = "none"; // 受托人信息.
				document.getElementById("cashier_bank_issuer_id").style.display = "none"; // 联行号.
			}
		});
		
		// 受托支付标识，0：否，1：是.
		$("#isEntrustedPay").change(function() {
			var type_value = $(this).children('option:selected').val();
			if (type_value == '0') {
				document.getElementById("personal_id").style.display = "block"; // 借款人信息.
				document.getElementById("is_entrusted_pay_id").style.display = "none"; // 受托人信息.
				document.getElementById("cashier_bank_issuer_id").style.display = "none"; // 联行号.
			} else if (type_value == '1') {
				document.getElementById("personal_id").style.display = "block"; // 借款人信息.
				document.getElementById("is_entrusted_pay_id").style.display = "block"; // 受托人信息.
				document.getElementById("cashier_bank_issuer_id").style.display = "none"; // 联行号.
			}
		});
		
		// 受托人银行编码对照表.
		$('#cicmorganBankCodeId').change(function(){
			var cicmorganBankCodeId = $(this).children('option:selected').val();
			// console.log("cicmorganBankCodeId = " + cicmorganBankCodeId);
			selectCicmorganBankCodeId(cicmorganBankCodeId);
		});// --
		
		// 借款人银行编码对照表.
		$('#cicmorgan_bank_code_id').change(function(){
			var cicmorganBankCodeId = $(this).children('option:selected').val();
			// console.log("cicmorganBankCodeId = " + cicmorganBankCodeId);
			selectLoanCicmorganBankCodeId(cicmorganBankCodeId);
		});// --
		
		// 账户对公对私标识，1：对公，2：对私 ，默认：2 （对私）.
		$("#cashierBankNoFlag").change(function() {
			var type_value = $(this).children('option:selected').val();
			if (type_value == '1') {
				document.getElementById("cashier_bank_issuer_id").style.display = "block"; // 联行号.
			} else if (type_value == '2') {
				document.getElementById("cashier_bank_issuer_id").style.display = "none"; // 联行号.
			}
		});// --
		// 证照类型，BLC：营业执照，USCC：统一社会信用代码.
		$("#businessLicenseType").change(function() {
			var type_value = $(this).children('option:selected').val();
			if (type_value == 'BLC') {
				document.getElementById("taxRegCertNo_OrgCode_id").style.display = "block"; // 组织机构代码与税务登记号.
			} else if (type_value == 'USCC') {
				// 清空组织机构代码与税务登记号.
					$("#organNo").val(""); // 组织机构代码.
					$("#taxCode").val(""); // 税务登记号.
				document.getElementById("taxRegCertNo_OrgCode_id").style.display = "none"; // 组织机构代码与税务登记号.
			}
		});// --
	}//--
	
	// 
	function selectLoanApplyId(id){
		$.ajax({
			url : "${ctx}/wloan_subject/wloanSubject/selectLoanApplyId?id=" + id, 
			type : "post", 
			success : function(data) {
				// console.log("借款人姓名：" + data.name);
				if('undefined' == typeof(data.loanUser)){ // undefined-判断.
					$("#companyName").val(""); // 名称.
				} else {
					$("#companyName").val(data.loanUser + "【借款人】"); // 名称.
				}
				$("#loanIdCard").val(data.loanIdCard); // 借款人身份证号码.
				$("#loanUser").val(data.loanUser); // 借款人姓名.
				$("#loanPhone").val(data.loanPhone); // 借款人联系电话.
				$("#loanBankNo").val(data.loanBankNo); // 借款人银行卡号码.
				$("#loanBankPhone").val(data.loanBankPhone); // 借款人银行预留手机.
				$("#loanBankName").val(data.loanBankName); // 借款人银行卡开户名称.
				$("#loanBankCode").val(data.loanBankCode); // 借款人银行卡银行编码.
			},
			error : function(data) {
				alert("程序异常");
			}
		});
	}// --
	
	// 受托人银行编码对照表.
	function selectCicmorganBankCodeId(id){
		$.ajax({
			url : "${ctx}/wloan_subject/wloanSubject/selectCicmorganBankCodeId?id=" + id, 
			type : "post", 
			success : function(data) {
				$("#cashierBankAdderss").val(data.bankName); // 受托人银行卡开户名称.
				$("#cashierBankCode").val(data.bankCode); // 受托人银行卡银行编码.
			},
			error : function(data) {
				alert("程序异常");
			}
		});
	}// --
	
	// 借款人银行编码对照表.
	function selectLoanCicmorganBankCodeId(id){
		$.ajax({
			url : "${ctx}/wloan_subject/wloanSubject/selectCicmorganBankCodeId?id=" + id, 
			type : "post", 
			success : function(data) {
				$("#loan_bank_name_id").val(data.bankName); // 借款人银行卡开户名称.
				$("#loan_bank_code_id").val(data.bankCode); // 借款人银行卡银行编码.
			},
			error : function(data) {
				alert("程序异常");
			}
		});
	}// --
	
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/wloan_subject/wloanSubject/cgbPersonalList">主体列表</a></li>
		<li class="active"><a href="${ctx}/wloan_subject/wloanSubject/cgbWloanSubjectForm?id=${wloanSubject.id}">主体添加</a></li>
	</ul>
	<br />
	<form:form id="personalForm" modelAttribute="wloanSubject" action="${ctx}/wloan_subject/wloanSubject/cgbWloanSubjectSave" method="post" class="form-horizontal">
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
		
		<!-- 个人. -->
		<div id="personal_id" style="display: block;">
			<span class="help-inline">借款人</span>
			<div class="control-group">
				<label class="label">借款人：代表个人或企业进行借款的收款人。</label>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">借款人：</label>
					<div class="controls">
						<form:select path="loanApplyId" class="input-xlarge" style="width:264px">
							<form:option value="" label="请选择"/>
							<c:forEach items="${creditUserInfos}" var="creditUserInfo">
								<form:option value="${creditUserInfo.id }" label="${creditUserInfo.phone}【${creditUserInfo.name }】"/>
							</c:forEach>
						</form:select>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<div>
						<div id="p_name" style="display: block;"><label class="control-label">个人名称：</label></div>
						<div id="e_name" style="display: none;"><label class="control-label">企业名称：</label></div>
						<div class="controls">
							<form:input path="companyName" htmlEscape="false" maxlength="50" class="input-large required" />
							<span class="help-inline"><font color="red">*</font></span>
						</div>
					</div>
				</div>
			</div>	
			<div class="control-group">
				<div class="span6">
					<label class="control-label">姓名（法人）：</label>
					<div class="controls">
						<form:input path="loanUser" readonly="true" htmlEscape="false" maxlength="32" class="input-large required"/>
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">移动电话：</label>
					<div class="controls">
						<form:input path="loanPhone" readonly="true" htmlEscape="false" maxlength="11" class="input-large required" onkeyup="value=value.replace(/[^\d]/g,'')" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">身份证号：</label>
					<div class="controls">
						<form:input path="loanIdCard" readonly="true" htmlEscape="false" maxlength="32" class="input-large required" onkeyup="value=value.replace(/[^\d\a-z\A-Z]/g,'')" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
			</div>
		
			<div class="control-group">
				<div class="span6">
					<label class="control-label">银行卡号：</label>
					<div class="controls">
						<form:input path="loanBankNo" readonly="true" htmlEscape="false" maxlength="32" class="input-large required"/>
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">银行预留手机：</label>
					<div class="controls">
						<form:input path="loanBankPhone" readonly="true" htmlEscape="false" maxlength="11" class="input-large required" onkeyup="value=value.replace(/[^\d]/g,'')" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">开户行：</label>
					<div class="controls">
						<form:input path="loanBankName" readonly="true" htmlEscape="false" maxlength="32" class="input-large required"/>
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">银行编码：</label>
					<div class="controls">
						<form:input path="loanBankCode" readonly="true" htmlEscape="false" maxlength="32" class="input-large required"/>
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			
			<div class="control-group">
				<div class="span6">
					<label class="control-label">受托支付标识：</label>
					<div class="controls">
						<form:select path="isEntrustedPay" class="input-xlarge required" style="width:264px">
							<form:option value="0" label="否"/>
							<form:option value="1" label="是"/>
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
			</div>
			<label class="label">受托支付标识：受托支付是贷款资金的一种支付方式，指贷款人（依法设立的银行业金融机构）根据借款人的提款申请和支付委托，将贷款资金支付给符合合同约定用途的借款人交易对象，目的是为了减小贷款被挪用的风险。受托支付目前适用的情况是：贷款资金单笔金额超过项目总投资5%或超过500万元人民币。</label>
		</div>
		
		<!-- 受托支付信息. -->
		<div id="is_entrusted_pay_id" style="display: none;">
			<span class="help-inline">受托人</span>
			<div class="control-group">
			</div>
			<label class="label">受托人：借款人交易对象。</label>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">姓名：</label>
					<div class="controls">
						<form:input path="cashierUser" htmlEscape="false" maxlength="32" class="input-large required" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">身份证号：</label>
					<div class="controls">
						<form:input path="cashierIdCard" htmlEscape="false" maxlength="32" class="input-large required" onkeyup="value=value.replace(/[^\d\a-z\A-Z]/g,'')" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">银行卡号：</label>
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
				<div class="span9">
					<label class="control-label">银行编码对照表：</label>
					<div class="controls">
						<form:select path="cicmorganBankCodeId" class="input-xlarge" style="width:333px">
							<form:option value="" label="请选择"/>
							<c:forEach items="${cicmorganBankCodes}" var="cicmorganBankCode">
								<form:option value="${cicmorganBankCode.id }" label="${cicmorganBankCode.bankName}"/>
							</c:forEach>
						</form:select>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">开户行：</label>
					<div class="controls">
						<form:input path="cashierBankAdderss" readonly="true" htmlEscape="false" maxlength="255" class="input-large required" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">银行编码：</label>
					<div class="controls">
						<form:input path="cashierBankCode" readonly="true" htmlEscape="false" maxlength="255" class="input-large required" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">账户对公对私标识：</label>
					<div class="controls">
						<form:select path="cashierBankNoFlag" class="input-xlarge required" style="width:264px">
							<form:option value="2" label="对私"/>
							<form:option value="1" label="对公"/>
						</form:select>
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			<label class="label">账户对公对私标识：银行对公，对私分别指的是银行的对公业务和个人银行业务。默认，对私标识。</label>
		</div>
		
		<!-- 联行号. -->
		<div id="cashier_bank_issuer_id" style="display: none;">
			<div class="control-group">
				<div class="span6">
					<label class="control-label">联行号：</label>
					<div class="controls">
						<form:input path="cashierBankIssuer" htmlEscape="false" maxlength="255" class="input-large required" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			<label class="label">银行联行号：银行联行号就是一个地区银行的唯一识别标志。用于人民银行所组织的大额支付系统\小额支付系统\城市商业银行银行汇票系统\全国支票影像系统（含一些城市的同城票据自动清分系统）等跨区域支付结算业务。由12位组成：3位银行代码+4位城市代码+4位银行编号+1位校验位。</label>
		</div>
		
		<!-- 企业表单. -->
		<div id="enterprise_id" style="display: none;">
			<span class="help-inline">借款企业</span>
			<div class="control-group">
				<label class="label">借款企业：企业详细信息。</label>
			</div>
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
			<!-- 企业证照. -->
			<div class="control-group">
				<div class="span6">
					<label class="control-label">证照类型：</label>
					<div class="controls">
						<form:select path="businessLicenseType" class="input-xlarge required" style="width:264px">
							<form:option value="BLC" label="营业执照"/>
							<form:option value="USCC" label="统一社会信用代码"/>
						</form:select>
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">证照编号：</label>
					<div class="controls">
						<form:input path="businessNo" htmlEscape="false" maxlength="64" class="input-large required" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">银行开户许可证编号：</label>
					<div class="controls">
						<form:input path="bankPermitCertNo" htmlEscape="false" maxlength="64" class="input-large required" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			<div id="taxRegCertNo_OrgCode_id" style="display: block;">
				<div class="control-group">
					<div class="span6">
						<label class="control-label">组织机构代码：</label>
						<div class="controls">
							<form:input path="organNo" htmlEscape="false" maxlength="64" class="input-large required" />
							<span class="help-inline"><font color="red">*</font></span>
						</div>
					</div>
					<div class="span6">
						<label class="control-label">税务登记号：</label>
						<div class="controls">
							<form:input path="taxCode" htmlEscape="false" maxlength="64" class="input-large required" />
							<span class="help-inline"><font color="red">*</font></span>
						</div>
					</div>
				</div>
			</div>
			<!-- 联系人. -->
			<div class="control-group">
				<div class="span6">
					<label class="control-label">联系人姓名：</label>
					<div class="controls">
						<form:input path="agentPersonName" htmlEscape="false" maxlength="64" class="input-large required" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">联系人手机号：</label>
					<div class="controls">
						<form:input path="agentPersonPhone" htmlEscape="false" maxlength="64" class="input-large required" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">联系人证件类型：</label>
					<div class="controls">
						<form:select path="agentPersonCertType" class="input-xlarge required" style="width:264px">
							<form:option value="IDC" label="身份证"/>
							<form:option value="GAT" label="港澳台身份证"/>
							<form:option value="MILIARY" label="军官证"/>
							<form:option value="PASS_PORT" label="护照"/>
						</form:select>
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">联系人证件号：</label>
					<div class="controls">
						<form:input path="agentPersonCertNo" htmlEscape="false" maxlength="64" class="input-large required" onkeyup="value=value.replace(/[^\d\a-z\A-Z]/g,'')" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
						<div class="control-group">
				<div class="span6">
					<label class="control-label">法人证件类型：</label>
					<div class="controls">
						<form:select path="corporationCertType" class="input-xlarge required" style="width:264px">
							<form:option value="IDC" label="身份证"/>
							<form:option value="GAT" label="港澳台身份证"/>
							<form:option value="MILIARY" label="军官证"/>
							<form:option value="PASS_PORT" label="护照"/>
						</form:select>
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">法人证件号：</label>
					<div class="controls">
						<form:input path="corporationCertNo" htmlEscape="false" maxlength="64" class="input-large required" onkeyup="value=value.replace(/[^\d\a-z\A-Z]/g,'')" />
						<span class="help-inline"><font color="red">*</font></span>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">上年现金流量：</label>
					<div class="controls">
						<form:input path="lastYearCash" htmlEscape="false" class="input-large " />
						<span class="help-inline"><font color="red">元</font></span>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">注册资金：</label>
					<div class="controls">
						<form:input path="registerAmount" htmlEscape="false" class="input-large " />
						<span class="help-inline"><font color="red">元</font></span>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">资产净值：</label>
					<div class="controls">
						<form:input path="netAssetAmount" htmlEscape="false" class="input-large " onkeyup="value=value.replace(/[^\d\.]/g,'')" />
						<span class="help-inline"><font color="red">元</font></span>
					</div>
				</div>
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
			
		<!-- 备注. -->
		<div class="control-group">
			<div class="span14">
				<label class="control-label">备注信息：</label>
				<div class="controls">
					<input type="text" readonly="readonly" value="借款融资主体" style="width:250px">
		<form:hidden path="remarks" value="借款融资主体"/>
				</div>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="wloan_subject:wloanSubject:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" />&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>