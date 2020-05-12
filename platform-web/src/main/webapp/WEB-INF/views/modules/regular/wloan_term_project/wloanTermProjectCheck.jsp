<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>定期项目信息管理</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/lightbox.css" />
	<script type="text/javascript" src="${ctxStatic}/js/lightbox.js"></script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			
			// 初始化项目表单页面.
			Init_Wloan_Project_Choice();
			//
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
			}); // --
		
			$("#cancleCheck").click(function(){
				$("#state").val(0);
				$("#inputForm").attr("action","${ctx}/wloanproject/wloanTermProject/toBeCheck");
				$("#inputForm").submit();
			});
			
			// 标的报备.
			$("#p2pTradeBidCreate").click(function(){
				// $("#inputForm").attr("action","${ctx}/cgb/p2p/trade/bid/create");
				$("#inputForm").attr("action","${ctx}/lm/p2p/trade/establishProject");
				$("#inputForm").submit();
			}); // --
		});
		
		// 初始化项目表单页面.
		function Init_Wloan_Project_Choice() {
			
			var isEntrustedPay = $('#isEntrustedPay').children('option:selected').val(); // 受托支付标识.
			var subjectId = $('#hidden_wloan_subjecct_id').val(); // 融资主体ID.
			// console.log("subjectId：" + subjectId);
			InitWloanSubjectListByisEntrustedPay(isEntrustedPay, subjectId); // 初始化融资主体.
			// -- .
			var wloanSubjectId = $('#hidden_wloan_subject_id').val(); // 融资主体ID.
			InitCreditUserApplysByWloanSubjectId(wloanSubjectId);
		}// --
		
		/**
		 * FN: InitWloanSubjectListByisEntrustedPay.
		 * DESC: 受托支付标识-联动-融资主体.
		 */
		function InitWloanSubjectListByisEntrustedPay(isEntrustedPay, subjectId){
			$.ajax({
				url : "${ctx}/wloanproject/wloanTermProject/isEntrustedPay?isEntrustedPay=" + isEntrustedPay, 
				type : "post", 
				success : function(data) {
					var table = data.wSubjects;
					var $subjectId = $("#wloan_subject_id");
					$subjectId.empty();// 首先清空select现在有的内容.
					$subjectId.append('<option value="">请选择</option>');
					// console.log("value = " + $subjectId.val());
					// console.log("value = " + $subjectId.text());
					// console.log("option:selected = " + $("#subjectId").find("option:selected").text());
					for (var i = 0; i < table.length; i++) {
						var item = table[i];
						if(item.id == subjectId){
							$subjectId.append("<option selected='selected' value=" + item.id + ">" + item.companyName + "</option>");
						} else {
							$subjectId.append("<option value=" + item.id + ">" + item.companyName + "</option>");
						}
					}
					// 上次选中的值重新赋值.
					// console.log($("option:selected").text());
					// console.log($("span.select2-chosen:eq(1)").text());
				},
				error : function(data) {
					console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
				}
			});
		}// --

		/**
		 * FN:InitCreditUserApplysByWloanSubjectId.
		 * DESC:初始化，融资主体-联动-借款申请.
		 */
		function InitCreditUserApplysByWloanSubjectId(wloanSubjectId){
			$.ajax({
				url : "${ctx}/wloanproject/wloanTermProject/creditUserApplysByWloanSubjectId?wloanSubjectId=" + wloanSubjectId, 
				type : "post", 
				success : function(data) {
					// 借款人基本信息.
					var model = data.ztmgLoanApplyAndBasicInfoPojo.ztmgLoanBasicInfo;
					// console.log(model.length);
					if(typeof(model) == "undefined"){
						$("#loan_basic_info_div_id").hide();
					} else {
						// 借款人基本信息ID.
						$("input[name='ztmgLoanBasicInfo.id']").val(model.id);
						// 借款人ID.
						$("input[name='ztmgLoanBasicInfo.creditUserId']").val(model.creditUserId);
						// 省份.
						$("input[id='province_id']").val(model.province);
						// 地级市.
						$("input[id='city_id']").val(model.city);
						// 市、县级市.
						$("input[id='county_id']").val(model.county);
						// 街道.
						$("input[id='street_id']").val(model.street);
						// 征信信息. 
						$("input[id='credit_information_id']").val(model.creditInformation);
						// 所属行业.
						var industry = model.industry;
						var industryObj = document.getElementById("industry_id");
						for (var i = 0; i < industryObj.options.length; i++) {
							if (industryObj.options[i].value == industry) {
								industryObj.options[i].selected = true;
								$("span.select2-chosen:eq(8)").text(industryObj.options[i].text);
								break;
							}
						}
						// 实缴资本.
						$("input[name='ztmgLoanBasicInfo.contributedCapital']").val(model.contributedCapital);
						// 年营业收入.
						$("input[name='ztmgLoanBasicInfo.annualRevenue']").val(model.annualRevenue);
						// 负债.
						$("input[name='ztmgLoanBasicInfo.liabilities']").val(model.liabilities);
						// 其它借款信息.
						$("textarea[name='ztmgLoanBasicInfo.otherCreditInformation']").val(model.otherCreditInformation);
						// JSON数组.
						var shareholdersInfoPojoJsonArrayStr = model.shareholdersJsonArrayStr;
						var siaObject = JSON.parse(shareholdersInfoPojoJsonArrayStr);
						for (var i = 0; i < siaObject.length; i++) {
							// 股东类型.
							var shareholdersType = siaObject[i].shareholdersType;
							var shareholdersTypeValue = "";
							if(shareholdersType == "SHAREHOLDERS_TYPE_01"){ // 自然人.
								shareholdersTypeValue = "自然人";
							} else if (shareholdersType == "SHAREHOLDERS_TYPE_02") { // 法人.
								shareholdersTypeValue = "法人";
							}
							// 股东证件类型.
							var shareholdersCertType = siaObject[i].shareholdersCertType;
							var shareholdersCertTypeValue = "";
							if(shareholdersCertType == "SHAREHOLDERS_CERT_TYPE_01"){ // 居民身份证.
								shareholdersCertTypeValue = "居民身份证";
							} else if (shareholdersCertType == "SHAREHOLDERS_CERT_TYPE_02") { // 营业执照.
								shareholdersCertTypeValue = "营业执照";
							}
							// 股东名称.
							var shareholdersName = siaObject[i].shareholdersName;
							if(i == 0){ // 默认股东信息.
								// 股东类型.
								var shareholdersTypeObj = document.getElementById("shareholders_type_id_" + (i + 1));
								shareholdersTypeObj.value = shareholdersTypeValue;
								// 股东证件类型.
								var shareholdersCertTypeObj = document.getElementById("shareholders_cert_type_id_" + (i + 1));
								shareholdersCertTypeObj.value = shareholdersCertTypeValue;
								// 股东名称.
								var shareholdersNameObj = document.getElementById("shareholders_name_id_" + (i + 1));
								shareholdersNameObj.value = shareholdersName;
							}
							if(i >= 1){
								append_Child_Shareholders_Info((i + 1));
								// 股东类型.
								var shareholdersTypeObj = document.getElementById("shareholders_type_id_" + (i + 1));
								shareholdersTypeObj.value = shareholdersTypeValue;
								// 股东证件类型.
								var shareholdersCertTypeObj = document.getElementById("shareholders_cert_type_id_" + (i + 1));
								shareholdersCertTypeObj.value = shareholdersCertTypeValue;
								// 股东名称.
								var shareholdersNameObj = document.getElementById("shareholders_name_id_" + (i + 1));
								shareholdersNameObj.value = shareholdersName;
							}
						}
						// 征信信息JSON数组.
						var creditAnnexFileJsonArrayStr = model.creditAnnexFileJsonArrayStr;
						var cafObject = JSON.parse(creditAnnexFileJsonArrayStr);
						for (var i = 0; i < cafObject.length; i++) {
							// 股东类型.
							var url = cafObject[i].url;
							append_Child_Credit_Annex_File_Info(url);
						}
						// 借款人基本信息.
						$("#loan_basic_info_div_id").show();
					}
				},
				error : function(data) {
					alert("程序异常");
				}
			});
		}// --

		// 股东信息-新增 .
		function append_Child_Shareholders_Info(x) {

			// 在新增之前，先将当前存在的元素删除掉.
			var delete_by_div = document.getElementById("shareholders_div_id_" + x);
			if (delete_by_div == null) { // null 判断.
			} else {
				delete_by_div.parentNode.removeChild(delete_by_div);
			}

			// div_1.
			var div_1 = document.createElement("div");
			div_1.setAttribute("class", "control-group");
			div_1.setAttribute("id", "shareholders_div_id_" + x);

			// div_1_1.
			var div_1_1 = document.createElement("div");
			div_1_1.setAttribute("class", "span6");
			// label 股东类型.
			var label_shareholders_type = document.createElement("label");
			label_shareholders_type.setAttribute("class", "control-label");
			label_shareholders_type.innerHTML = "股东类型：";
			div_1_1.appendChild(label_shareholders_type);
			// div_1_1_1 股东类型.
			var div_1_1_1 = document.createElement("div");
			div_1_1_1.setAttribute("class", "controls");
			// input 股东类型.
			var input_shareholders_type = document.createElement("input");
			input_shareholders_type.setAttribute("type", "text");
			input_shareholders_type.setAttribute("id", "shareholders_type_id_" + x);
			input_shareholders_type.setAttribute("readonly", "readonly");
			input_shareholders_type.setAttribute("style", "width:250px");
			div_1_1_1.appendChild(input_shareholders_type);
			div_1_1.appendChild(div_1_1_1);
			div_1.appendChild(div_1_1);

			// div_1_2.
			var div_1_2 = document.createElement("div");
			div_1_2.setAttribute("class", "span6");
			// label 股东证件类型.
			var label_shareholders_cert_type = document.createElement("label");
			label_shareholders_cert_type.setAttribute("class", "control-label");
			label_shareholders_cert_type.innerHTML = "股东证件类型：";
			div_1_2.appendChild(label_shareholders_cert_type);
			// div_1_2_1.
			var div_1_2_1 = document.createElement("div");
			div_1_2_1.setAttribute("class", "controls");
			// input 股东证件类型.
			var input_shareholders_cert_type = document.createElement("input");
			input_shareholders_cert_type.setAttribute("type", "text");
			input_shareholders_cert_type.setAttribute("id", "shareholders_cert_type_id_" + x);
			input_shareholders_cert_type.setAttribute("readonly", "readonly");
			input_shareholders_cert_type.setAttribute("style", "width:250px");
			div_1_2_1.appendChild(input_shareholders_cert_type);
			div_1_2.appendChild(div_1_2_1);
			div_1.appendChild(div_1_2);

			// div_1_3.
			var div_1_3 = document.createElement("div");
			div_1_3.setAttribute("class", "span6");
			// label 股东证件类型.
			var label_shareholders_name = document.createElement("label");
			label_shareholders_name.setAttribute("class", "control-label");
			label_shareholders_name.innerHTML = "股东名称：";
			div_1_3.appendChild(label_shareholders_name);
			// div_1_3_1.
			var div_1_3_1 = document.createElement("div");
			div_1_3_1.setAttribute("class", "controls");
			// input 股东证件类型.
			var input_shareholders_name = document.createElement("input");
			input_shareholders_name.setAttribute("type", "text");
			input_shareholders_name.setAttribute("id", "shareholders_name_id_" + x);
			input_shareholders_name.setAttribute("readonly", "readonly");
			input_shareholders_name.setAttribute("style", "width:250px");
			div_1_3_1.appendChild(input_shareholders_name);
			div_1_3.appendChild(div_1_3_1);
			div_1.appendChild(div_1_3);

			// 原始股东信息-div.
			var shareholders_div = document.getElementById("shareholders_div_id_1");
			shareholders_div.parentNode.appendChild(div_1);

		} // -- .
		
		// 征信报告-新增 .
		function append_Child_Credit_Annex_File_Info(url) {
			// 借款人基本信息-div.
			var loan_basic_info_div = document.getElementById("loan_basic_info_div_id");

			// div_1.
			var div_1 = document.createElement("div");
			div_1.setAttribute("class", "control-group");

			// div_2.
			var div_2 = document.createElement("div");
			div_2.setAttribute("class", "span10");

			// label.
			var label = document.createElement("label");
			label.setAttribute("class", "control-label");
			label.innerHTML = "征信报告：";
			div_2.appendChild(label);
			// div_3.
			var div_3 = document.createElement("div");
			div_3.setAttribute("class", "controls");

			// a 标签.
			var a = document.createElement("a");
			a.setAttribute("class", "example-image-link");
			a.setAttribute("href", "${downpath}/upload/image/" + url);
			a.setAttribute("data-lightbox", "example-1");
			
			// image
			var img = document.createElement("img");
			img.setAttribute("class", "example-image");
			img.setAttribute("style", "max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;");
			img.setAttribute("src", "${downpath}/upload/image/" + url);
			a.appendChild(img);
			div_3.appendChild(a);
			div_2.appendChild(div_3);
			div_1.appendChild(div_2);

			loan_basic_info_div.appendChild(div_1);
			
		} // -- .

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/wloanproject/wloanTermProject/">安心投列表</a></li>
		<li class="active">
			<a href="${ctx}/wloanproject/wloanTermProject/check?id=${wloanTermProject.id}">
				<c:if test="${wloanTermProject.state == '0' || wloanTermProject.state == '1' || wloanTermProject.state == '2'}">
	   				安心投审核
				</c:if>
				<c:if test="${wloanTermProject.state == '3' }">
	   				安心投发布
				</c:if>
				<c:if test="${wloanTermProject.state == '4' }">
	   				安心投上线
				</c:if>
				<c:if test="${wloanTermProject.state == '5' || wloanTermProject.state == '6' || wloanTermProject.state == '7' || wloanTermProject.state == '8'}">
	   				安心投查看
				</c:if>
			</a>
		</li>
	</ul><br/>
	
	<form:form id="inputForm" modelAttribute="wloanTermProject" action="${ctx}/wloanproject/wloanTermProject/toBeCheck" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">项目编号：</label>
				<div class="controls">
					<form:input path="sn" readonly="true" htmlEscape="false" maxlength="32" class="input-xlarge required" style="width:250px"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">项目名称：</label>
				<div class="controls">
					<form:input path="name" readonly="true" htmlEscape="false" maxlength="55" class="input-xlarge required" style="width:250px"/>&nbsp;
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<!-- 受托支付标识-联动-融资主体. -->
			<div class="span6">
				<label class="control-label">受托支付标识：</label>
				<div class="controls">
					<form:select path="isEntrustedPay" disabled="true" class="input-xlarge required" style="width:264px">
						<form:option value="0" label="否"/>
						<form:option value="1" label="是"/>
					</form:select>
					<form:hidden path="isEntrustedWithdraw" />
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">项目还款计划类型：</label>
				<div class="controls">
					<form:select path="projectRepayPlanType" disabled="true" class="input-xlarge required" style="width:264px">
						<form:option value="0" label="旧版"/>
						<form:option value="1" label="新版"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">是否代偿还款：</label>
				<div class="controls">
					<form:select path="isReplaceRepay" disabled="true" class="input-xlarge required" style="width:264px">
						<form:option value="0" label="否"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">融资主体：</label>
				<div class="controls">
					<a href="${ctx}/wloan_subject/wloanSubject/cgbProjectWloanSubjectViewForm?id=${wloanTermProject.wloanSubject.id}">${wloanTermProject.wloanSubject.companyName }</a>
				</div>
				<input id="hidden_wloan_subject_id" type="hidden" value="${wloanTermProject.wloanSubject.id}" />
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">融资档案：</label>
				<div class="controls">
					<a href="${ctx}/wloanproject/wloanTermProject/docViewForm?id=${wloanTermProject.id}">${wloanTermProject.wloanTermDoc.name }</a>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">担保机构：</label>
				<div class="controls">
					<a href="${ctx}/wloanproject/wloanTermProject/guarViewForm?id=${wloanTermProject.id}">${wloanTermProject.wgCompany.name }</a>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">期限：</label>
				<div class="controls">
					<form:select path="span" disabled="true" class="input-large required" style="width:229px">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('regular_wloan_span')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<span class="help-inline">&nbsp;&nbsp;天<font color="red">&nbsp;&nbsp;*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">所在地：</label>
				<div class="controls">
					<form:input path="locus" htmlEscape="false" class="input-large number required" id="locus" type="hidden"/>
					<sys:treeselect disabled="disabled" id="area" name="area.id" value="${wloanTermProject.area.id}" labelName="area.name" labelValue="${wloanTermProject.area.name}"
					title="区域" url="/sys/area/treeData" cssClass="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">融资金额：</label>
				<div class="controls">
					<form:input path="amount" readonly="true" htmlEscape="false" class="input-large number required"/>
					<span class="help-inline">&nbsp;&nbsp;元<font color="red">&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">保证金：</label>
				<div class="controls">
					<input type="text" readonly="readonly" value="0" class="input-large  number required">
					<form:hidden path="marginPercentage" value="0"/>
					<span class="help-inline">&nbsp;&nbsp;元<font color="red">&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">起投金额：</label>
				<div class="controls">
					<form:input path="minAmount" readonly="true" htmlEscape="false" class="input-large  number required"/>
					<span class="help-inline">&nbsp;&nbsp;元<font color="red">&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">最大投资金额：</label>
				<div class="controls">
					<form:input path="maxAmount" readonly="true" htmlEscape="false" class="input-large  number required"/>
					<span class="help-inline">&nbsp;&nbsp;元<font color="red">&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
		</div>	
			
		<div class="control-group">
			<div class="span6">
				<label class="control-label">递增金额：</label>
				<div class="controls">
					<form:input path="stepAmount" readonly="true" htmlEscape="false" class="input-large  number required"/>
					<span class="help-inline">&nbsp;&nbsp;元<font color="red">&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">手续费：</label>
				<div class="controls">
					<input type="text" readonly="readonly" value="0" class="input-large  number required">
					<form:hidden path="feeRate" value="0"/>
					<span class="help-inline">&nbsp;&nbsp;元<font color="red">&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">发布日期：</label>
				<div class="controls">
					<input name="publishDate" disabled="disabled" type="text" maxlength="20" class="input-medium Wdate required" style="width:250px" readonly="readonly"
						value="<fmt:formatDate value="${wloanTermProject.publishDate}" pattern="yyyy-MM-dd"/>"
						onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
						<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">上线日期：</label>
				<div class="controls">
					<input name="onlineDate" disabled="disabled" type="text" maxlength="20" class="input-medium Wdate required" style="width:251px" readonly="readonly"
						value="<fmt:formatDate value="${wloanTermProject.onlineDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
						onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
						<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>

		<!-- 流标/满标日期. -->
		<div class="control-group">
			<div class="span6">
				<label class="control-label">流标日期：</label>
				<div class="controls">
					<input name="loanDate" disabled="disabled" type="text" maxlength="20" class="input-medium Wdate required" style="width:250px" readonly="readonly"
						value="<fmt:formatDate value="${wloanTermProject.loanDate}" pattern="yyyy-MM-dd"/>"
						onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
						<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">满标日期：</label>
				<div class="controls">
					<input name="fullDate" type="text" readonly="readonly" maxlength="20" class="input-medium" style="width:250px"
						value="<fmt:formatDate value="${wloanTermProject.fullDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
				</div>
			</div>
		</div>

		<!-- 实际放款/标的结束日期. -->
		<div class="control-group">
			<div class="span6">
				<label class="control-label">标的结束日期：</label>
				<div class="controls">
					<input name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium" style="width:250px"
						value="<fmt:formatDate value="${wloanTermProject.endDate}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">实际放款日期：</label>
				<div class="controls">
					<input name="realLoanDate" type="text" readonly="readonly" maxlength="20" class="input-medium" style="width:250px"
						value="<fmt:formatDate value="${wloanTermProject.realLoanDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
				</div>
			</div>
		</div>

		<!-- 创建时间/更新时间. -->
		<div class="control-group">
			<div class="span6">
				<label class="control-label">创建日期：</label>
				<div class="controls">
					<input name="createDate" type="text" readonly="readonly" maxlength="20" class="input-medium" style="width:250px"
						value="<fmt:formatDate value="${wloanTermProject.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">更新日期：</label>
				<div class="controls">
					<input name="updateDate" type="text" readonly="readonly" maxlength="20" class="input-medium" style="width:250px"
						value="<fmt:formatDate value="${wloanTermProject.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
				</div>
			</div>
		</div>
		
		<!-- 年化利率和加息利率. -->
		<div class="control-group">
			<div class="span6">
				<label class="control-label">年化合计利率：</label>
				<div class="controls">
					<form:input path="annualRate" readonly="true" htmlEscape="false" class="input-xlarge number required" style="width:230px"/>
					<span class="help-inline">%</span> <span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">加息利率：</label>
				<div class="controls">
					<form:input path="interestRateIncrease" readonly="true" htmlEscape="false" class="input-xlarge number required" style="width:230px"/>
					<span class="help-inline">%</span>
				</div>
			</div>
		</div>
		

		<div class="control-group">
			<div class="span6">
				<label class="control-label">担保函编号：</label>
				<div class="controls">
					<form:input path="guaranteeSn" readonly="true" htmlEscape="false" maxlength="32" class="input-xlarge " style="width:250px"/>
				</div>
			</div>
		</div>

		<div class="control-group">
			<div class="span6">
				<label class="control-label">还款方式：</label>
				<div class="controls">
					<!-- <input type="text" readonly="readonly" value="按月付息到期还款" style="width:250px"> -->
					<input type="text" readonly="readonly" value="30天付息1次，到期还本" style="width:250px">
					<form:hidden path="repayType" value="2"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">流转状态 ：</label>
				<div class="controls">
					<c:if test="${not empty wloanTermProject.state }">
						<c:forEach items="${fns:getDictList('wloan_term_state')}" var="proState">
							<c:if test="${wloanTermProject.state == proState.value }">
								<input type="text" readonly="readonly" value="${proState.label}" style="width:250px">
								<form:hidden path="state" value="${proState.value}" id="state"/>
							</c:if>
						</c:forEach>
					</c:if>
				</div>
			</div>
		</div>

		<div class="control-group">
			<div class="span6">
				<label class="control-label">标的类型：</label>
				<div class="controls">
					<form:select path="projectType" disabled="true" class="input-xlarge required" style="width:264px">
						<form:option value="1" label="其它"/>
						<form:option value="2" label="新手标的"/>
						<form:option value="3" label="推荐标的"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">标的产品类型：</label>
				<div class="controls">
					<form:select path="projectProductType" disabled="true" class="input-xlarge required" style="width:264px">
						<form:option value="1" label="安心投类"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">是否可用抵用券：</label>
				<div class="controls">
					<form:select path="isCanUseCoupon" disabled="true" class="input-xlarge required" style="width:264px" id="wloan_guarant_select">
						<form:option value="0" label="是"/>
						<form:option value="1" label="否"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">是否可用加息券：</label>
				<div class="controls">
					<form:select path="isCanUsePlusCoupon" disabled="true" class="input-xlarge required" style="width:264px" id="wloan_guarant_select">
						<form:option value="0" label="是"/>
						<form:option value="1" label="否"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<!-- 标签. -->
			<div class="span6">
				<label class="control-label">标签：</label>
				<div class="controls">
					<form:input path="label" htmlEscape="false" maxlength="32" class="input-xlarge " style="width:250px" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">经营状况及财务状况：</label>
				<div class="controls">
					<form:input path="businessFinancialSituation" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" readonly="true"/>
				</div>
			</div>
		</div>
		
		<!-- 信息披露5字段. -->
		<div class="control-group">
			<div class="span6">
				<label class="control-label">还款能力变化情况：</label>
				<div class="controls">
					<form:input path="abilityToRepaySituation" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">平台的逾期情况：</label>
				<div class="controls">
					<form:input path="platformOverdueSituation" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">涉诉情况：</label>
				<div class="controls">
					<form:input path="litigationSituation" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">受行政处罚情况：</label>
				<div class="controls">
					<form:input path="administrativePunishmentSituation" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" readonly="true"/>
				</div>
			</div>
		</div>

		<!-- 借款人基本信息. -->
		<div style="display: none;" id="loan_basic_info_div_id">
			<form:hidden path="ztmgLoanBasicInfo.id"/>
			<form:hidden path="ztmgLoanBasicInfo.creditUserId"/>
			<div class="control-group">
				<label class="label">————————————————————————借款人基本信息————————————————————————</label>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">省份：</label>
					<div class="controls">
						<input type="text" id="province_id" readonly="readonly" style="width:250px">
						<form:hidden path="ztmgLoanBasicInfo.province" id="province_id"/>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">地级市：</label>
					<div class="controls">
						<input type="text" id="city_id" readonly="readonly" style="width:250px">
						<form:hidden path="ztmgLoanBasicInfo.city" id="city_id"/>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">市、县级市：</label>
					<div class="controls">
						<input type="text" id="county_id" readonly="readonly" style="width:250px">
						<form:hidden path="ztmgLoanBasicInfo.county" id="county_id"/>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span10">
					<label class="control-label">街道：</label>
					<div class="controls">
						<input type="text" id="street_id" readonly="readonly" style="width:550px">
						<form:hidden path="ztmgLoanBasicInfo.street" id="street_id"/>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">征信信息：</label>
					<div class="controls">
						<input type="text" id="credit_information_id" readonly="readonly" style="width:250px">
						<form:hidden path="ztmgLoanBasicInfo.creditInformation" id="credit_information_id"/>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">所属行业：</label>
					<div class="controls">
						<form:select path="ztmgLoanBasicInfo.industry" id="industry_id" class="input-xlarge" style="width:264px" disabled="true">
							<option value="请选择">请选择</option>
							<option value="INDUSTRY_01">农林牧渔业</option>
							<option value="INDUSTRY_02">采矿业</option>
							<option value="INDUSTRY_03">制造业</option>
							<option value="INDUSTRY_04">电力热力燃气及水生产</option>
							<option value="INDUSTRY_05">供应业</option>
							<option value="INDUSTRY_06">建筑业</option>
							<option value="INDUSTRY_07">批发和零售业</option>
							<option value="INDUSTRY_08">交通运输仓储业</option>
							<option value="INDUSTRY_09">住宿和餐饮业</option>
							<option value="INDUSTRY_10">信息传输软件和信息技术服务业</option>
							<option value="INDUSTRY_11">金融业</option>
							<option value="INDUSTRY_12">房地产业</option>
							<option value="INDUSTRY_13">租赁和商务服务业</option>
							<option value="INDUSTRY_14">科研和技术服务业</option>
							<option value="INDUSTRY_15">水利环境和公共设施管理业</option>
							<option value="INDUSTRY_16">居民服务修理和其他服务业</option>
							<option value="INDUSTRY_17">教育</option>
							<option value="INDUSTRY_18">卫生和社会工作</option>
							<option value="INDUSTRY_19">文化体育和娱乐业</option>
							<option value="INDUSTRY_20">公共管理</option>
							<option value="INDUSTRY_21">社会保障和社会组织</option>
						</form:select>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span6">
					<label class="control-label">实缴资本（元）：</label>
					<div class="controls">
						<form:input path="ztmgLoanBasicInfo.contributedCapital" onkeyup="value=value.replace(/[^\d]/g,'')" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" disabled="true"/>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">年营业收入（元）：</label>
					<div class="controls">
						<form:input path="ztmgLoanBasicInfo.annualRevenue" onkeyup="value=value.replace(/[^\d]/g,'')" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" disabled="true"/>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">负债（元）：</label>
					<div class="controls">
						<form:input path="ztmgLoanBasicInfo.liabilities" onkeyup="value=value.replace(/[^\d]/g,'')" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" disabled="true"/>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span10">
					<label class="control-label">其他借款信息：</label>
					<div class="controls">
						<form:textarea path="ztmgLoanBasicInfo.otherCreditInformation" htmlEscape="false" class="input-xxlarge" rows="2" disabled="true"/>
					</div>
				</div>
			</div>
			<div class="control-group" id="shareholders_div_id_1">
				<div class="span6">
					<label class="control-label">股东类型：</label>
					<div class="controls">
						<input type="text" id="shareholders_type_id_1" readonly="readonly" style="width:250px">
					</div>
				</div>
				<div class="span6">
					<label class="control-label">股东证件类型：</label>
					<div class="controls">
						<input type="text" id="shareholders_cert_type_id_1" readonly="readonly" style="width:250px">
					</div>
				</div>
				<div class="span6">
					<label class="control-label">股东名称：</label>
					<div class="controls">
						<input type="text" id="shareholders_name_id_1" readonly="readonly" style="width:250px">
					</div>
				</div>
			</div>
		</div>

		<div class="control-group">
			<label class="label">———————————————————————————————————————————————————————</label>
		</div>

		<!-- 供应商住所及电子签章. -->
		<div>
			<div class="control-group">
				<div class="span10">
					<label class="control-label">供应商住所：</label>
					<div class="controls">
						<form:textarea path="borrowerResidence" htmlEscape="false" class="input-xxlarge required" rows="2" readonly="true" />
					</div>
				</div>
			</div>
			<!-- 
			<div class="control-group">
				<div class="span6">
					<label class="control-label">供应商电子签章：</label>
					<div class="controls">
						<a class="example-image-link" href="${wloanTermProject.borrowerElectronicSignUrl}" data-lightbox="example-1"><img class="example-image" alt="" src="${wloanTermProject.borrowerElectronicSignUrl}"/></a>
					</div>
				</div>
			</div>
			<div class="control-group">
				<label class="label">借款方电子签章：影响《应收账款转让协议》电子签章，项目审核时注意是否上传电子签章。</label>
			</div>
			 -->		
		</div>			
		
		<div class="control-group">
			<div class="span10">
				<!-- <label class="control-label">担保方案：</label> -->
				<label class="control-label">风控措施：</label>
				<div class="controls">
					<form:textarea path="guaranteeScheme" readonly="true" htmlEscape="false" class="input-xxlarge" rows="4"/>
				</div>
			</div>
		</div>
			
		<div class="control-group">
			<div class="span10">
				<label class="control-label">项目情况：</label>
				<div class="controls">
					<form:textarea path="projectCase" readonly="true" htmlEscape="false"  class="input-xxlarge" rows="4"/>
				</div>
			</div>
		</div>	
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">资金用途：</label>
				<div class="controls">
					<form:textarea path="purpose" readonly="true" htmlEscape="false" class="input-xxlarge required" rows="4" />
					<span class="help-inline"><font color="red">&nbsp;&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
		</div>
		
		<!-- 还款来源. -->
		<div class="control-group">
			<div class="span10">
				<label class="control-label">还款来源：</label>
				<div class="controls">
					<form:textarea path="sourceOfRepayment" readonly="true" htmlEscape="false" class="input-xxlarge required" rows="4" />
					<span class="help-inline"><font color="red">&nbsp;&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
		</div>

		<!-- 还款还款保障措施. -->
		<div class="control-group">
			<div class="span10">
				<label class="control-label">还款保障措施：</label>
				<div class="controls">
					<form:textarea path="repaymentGuaranteeMeasures" htmlEscape="false" class="input-xxlarge" rows="4" readonly="true" />
				</div>
			</div>
		</div>

		<div class="control-group">
			<div class="span10">
				<label class="control-label">备注：</label>
				<div class="controls">
					<form:textarea path="remark" readonly="true" htmlEscape="false"  class="input-xxlarge" rows="4"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="form-actions">
				<shiro:hasPermission name="wloanproject:wloanTermProject:edit">
					<c:if test="${wloanTermProject.state == '1' }">
	    				<input id="btnSubmit" class="btn btn-primary" type="submit" value="提交审核" />&nbsp;&nbsp;
					</c:if>
					<c:if test="${wloanTermProject.state == '2' && usertype == '9' }">
	    				<input id="p2pTradeBidCreate" class="btn btn-primary" type="button" value="懒猫创建标的" />&nbsp;&nbsp;
	    				<input id="cancleCheck" class="btn btn-primary" type="button" value="撤 销"/>&nbsp;&nbsp;
					</c:if>
					<c:if test="${wloanTermProject.state == '3' }">
	    				<input id="btnSubmit" class="btn btn-primary" type="submit" value="上 线" />&nbsp;&nbsp;
					</c:if>
<%-- 					<c:if test="${wloanTermProject.state == '4' }">
	    				<input id="btnSubmit" class="btn btn-primary" type="submit" value="切 标" />&nbsp;&nbsp;
					</c:if> --%>
				</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</div>
	</form:form>
</body>
</html>