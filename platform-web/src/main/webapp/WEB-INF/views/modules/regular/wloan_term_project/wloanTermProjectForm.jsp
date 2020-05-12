<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>定期项目信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			jQuery.validator.addMethod("loanDate",function(value, element) {
				return this.optional(element) || loanDateOnChange(value);
			},"请输入正确的流标日期(顺延20个工作日)");

			// 初始化项目表单页面.
			Init_Wloan_Project_Choice();
			
			// 受托支付标识-联动-融资主体.
			$('#isEntrustedPay').change(function(){
				// console.log("isEntrustedPay：" + $(this).children('option:selected').val());
				var isEntrustedPay = $(this).children('option:selected').val();
				// console.log("isEntrustedPay：" + isEntrustedPay);
				WloanSubjectListByisEntrustedPay(isEntrustedPay);
			});// -- 

			// 融资主体-联动-借款申请.
			$('#wloan_subject_id').change(function(){
				// console.log("isEntrustedPay：" + $(this).children('option:selected').val());
				var wloanSubjectId = $(this).children('option:selected').val();
				// console.log("isEntrustedPay：" + isEntrustedPay);
				CreditUserApplysByWloanSubjectId(wloanSubjectId);
				// CreditInfosByWloanSubjectId(wloanSubjectId);
			});// --

			//
			$("#inputForm").validate({
					rules: {
						sn: {remote: "${ctx}/wloanproject/wloanTermProject/findProSnExist?" + encodeURIComponent('${wloanTermProject.sn}')}
					},
					messages: {
						sn: {remote: "该项目编号已存在，请重新输入"}
					},
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
		});
		
		// 校验融资金额不能小于起投金额
		function checkAmount(){
			var amount		= parseInt($("#amount").val());
			var minAmount	= parseInt($("#minAmount").val());
			if(amount < minAmount){
				alert("融资金额不能小于起投金额");
				return;
			}
		} // --
		
		// 受托支付标识-联动-融资主体.
		function WloanSubjectListByisEntrustedPay(isEntrustedPay){
			$.ajax({
				url : "${ctx}/wloanproject/wloanTermProject/isEntrustedPay?isEntrustedPay=" + isEntrustedPay, 
				type : "post", 
				success : function(data) {
					var table = data.wSubjects;
					var $subjectId = $("#wloan_subject_id");
					$subjectId.empty();// 首先清空select现在有的内容.
					$subjectId.append('<option selected="selected" value="">请选择</option>');
					// console.log("value = " + $subjectId.val());
					// console.log("value = " + $subjectId.text());
					// console.log("option:selected = " + $("#subjectId").find("option:selected").text());
					for (var i = 0; i < table.length; i++) {
						var item = table[i];
						$subjectId.append("<option  value=" + item.id + ">" + item.companyName + "</option>");
					}
					// 上次选中的值重新赋值.
					$("span.select2-chosen:eq(3)").text("请选择");
					// console.log($("option:selected").text());
					// console.log($("span.select2-chosen:eq(1)").text());
				},
				error : function(data) {
					alert("程序异常");
				}
			});
		}// --
		
		// 初始化项目表单页面.
		function Init_Wloan_Project_Choice() {
			
			var isEntrustedPay = $('#isEntrustedPay').children('option:selected').val(); // 受托支付标识.
			//  受托支付提现标识，默认0.
			$('#isEntrustedWithdraw').val(0);
			var subjectId = $('#hidden_wloan_subjecct_id').val(); // 融资主体ID.
			// console.log("subjectId：" + subjectId);
			InitWloanSubjectListByisEntrustedPay(isEntrustedPay, subjectId); // 初始化融资主体.

			// console.log("isEntrustedPay：" + isEntrustedPay);
			CreditUserApplysByWloanSubjectId(subjectId);
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
		 * FN:CreditUserApplysByWloanSubjectId.
		 * DESC:初始化，融资主体-联动-借款申请.
		 */
		function CreditUserApplysByWloanSubjectId(wloanSubjectId){
			$.ajax({
				url : "${ctx}/wloanproject/wloanTermProject/creditUserApplysByWloanSubjectId?wloanSubjectId=" + wloanSubjectId, 
				type : "post", 
				success : function(data) {
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
								$("span.select2-chosen:eq(11)").text(industryObj.options[i].text);
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
							append_Child_Credit_Annex_File_Info(url, i + 1);
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
		function append_Child_Credit_Annex_File_Info(url, x) {

			if (x > 1) { // 当元素大于一个时.
				var credit_info_a_id = document.getElementById("credit_info_a_id_" + (x - 1));
				// a 标签.
				var a = document.createElement("a");
				a.setAttribute("id", "credit_info_a_id_" + x);
				a.setAttribute("class", "example-image-link");
				a.setAttribute("href", "${downpath}/upload/image/" + url);
				a.setAttribute("data-lightbox", "example-1");
				
				// image
				var img = document.createElement("img");
				img.setAttribute("class", "example-image");
				img.setAttribute("style", "max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;");
				img.setAttribute("src", "${downpath}/upload/image/" + url);
				a.appendChild(img);
				credit_info_a_id.parentNode.appendChild(a);
			} else {

				// 在新增之前，先将当前存在的元素删除掉.
				var delete_by_div = document.getElementById("credit_info_div_id");
				if (delete_by_div == null) { // null 判断.
				} else {
					delete_by_div.parentNode.removeChild(delete_by_div);
				} 

				// 借款人基本信息-div.
				var loan_basic_info_div = document.getElementById("loan_basic_info_div_id");

				// div_1.
				var div_1 = document.createElement("div");
				div_1.setAttribute("id", "credit_info_div_id");
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
				a.setAttribute("id", "credit_info_a_id_" + x);
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
			}

		} // -- .

		// 
		function num(obj) {
			obj.value = obj.value.replace(/[^\d.]/g, ""); //清除"数字"和"."以外的字符
			obj.value = obj.value.replace(/^\./g, ""); //验证第一个字符是数字
			obj.value = obj.value.replace(/\.{2,}/g, "."); //只保留第一个, 清除多余的
			obj.value = obj.value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
			obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3'); //只能输入两个小数
		}

		// 对Date的扩展，将 Date 转化为指定格式的String
		// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
		// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
		// 例子： 
		// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
		// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
		Date.prototype.format = function(fmt) { //author: meizz 
			var o = {
				"M+" : this.getMonth() + 1, //月份 
				"d+" : this.getDate(), //日 
				"h+" : this.getHours(), //小时 
				"m+" : this.getMinutes(), //分 
				"s+" : this.getSeconds(), //秒 
				"q+" : Math.floor((this.getMonth() + 3) / 3), //季度 
				"S" : this.getMilliseconds()
			//毫秒 
			};
			if (/(y+)/.test(fmt))
				fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
			for ( var k in o)
				if (new RegExp("(" + k + ")").test(fmt))
					fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
			return fmt;
		}

		// 流标日期
		function loanDateOnChange(loanDate) {
			// console.log("流标日期：\t" + loanDate);
			//起始日期，/pattern/是正则表达式的界定符，pattern是要匹配的内容，只用于第一个符号的匹配，g为全局匹配标志
			var beginDateStr = new Date().format('yyyy-MM-dd');
			var beginDate = new Date(beginDateStr.replace(/-/g, "/"));
			// console.log("当期日期：\t" + beginDate);
			//结束日期
			var endDate = new Date(loanDate.replace(/-/g, "/"));
			// console.log("流标日期：\t" + endDate);
			//日期差值,即包含周六日、以天为单位的工时，86400000=1000*60*60*24.
			var workDayVal = (endDate - beginDate)/86400000 + 1;
			// console.log("工时：\t" + workDayVal);
			//工时的余数
			var remainder = workDayVal % 7;
			// console.log("工时余数：\t" + remainder);
			//工时向下取整的除数
			var divisor = Math.floor(workDayVal / 7);
			// console.log("工时向下取整的除数：\t" + divisor);
			var weekendDay = 2 * divisor;
			// console.log("工时向下取整的除数：\t" + weekendDay);

			//起始日期的星期，星期取值有（1,2,3,4,5,6,0）
			var nextDay = beginDate.getDay();
			//从起始日期的星期开始 遍历remainder天
			for(var tempDay = remainder; tempDay>=1; tempDay--) {
				//第一天不用加1
				if(tempDay == remainder) {
					nextDay = nextDay + 0;
				} else if(tempDay != remainder) {
					nextDay = nextDay + 1;
				}
				//周日，变更为0
				if(nextDay == 7) {
					nextDay = 0;
				}
				//周六日
				if(nextDay == 0 || nextDay == 6) {
					weekendDay = weekendDay + 1;
				}
			}
			//实际工时（天） = 起止日期差 - 周六日数目。
			// console.log("起止日期差：\t" + workDayVal);
			// console.log("周六日数目：\t" + weekendDay);
			workDayVal = workDayVal - weekendDay;
			// console.log("实际工时（天）：\t" + workDayVal);
			if(workDayVal <= 20){
				// console.log("实际工时（天）：\t" + workDayVal);
				return true;
			} else {
				// console.log("实际工时（天）：\t" + workDayVal);
				return false;
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/wloanproject/wloanTermProject/">安心投列表</a></li>
		<li class="active">
			<a href="${ctx}/wloanproject/wloanTermProject/form?id=${wloanTermProject.id}">
				安心投${not empty wloanTermProject.id?'修改':'创建'}
			</a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="wloanTermProject" action="${ctx}/wloanproject/wloanTermProject/save" method="post" class="form-horizontal">
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
			<!-- 受托支付标识-联动-融资主体. -->
			<div class="span6">
				<label class="control-label">受托支付标识：</label>
				<div class="controls">
					<form:select path="isEntrustedPay" class="input-xlarge required" style="width:264px">
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
					<form:select path="projectRepayPlanType" class="input-xlarge required" style="width:264px">
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
					<form:select path="isReplaceRepay" class="input-xlarge required" style="width:264px">
						<form:option value="0" label="否"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">融资主体：</label>
				<div class="controls">
					<form:select path="subjectId" class="input-xlarge required" style="width:264px" id="wloan_subject_id">
						<form:option value="" label="请选择"/>
						<c:forEach items="${wSubjects}" var="wSubjects">
							<form:option value="${wSubjects.id }" label="${wSubjects.companyName }" htmlEscape="false" />
						</c:forEach>
					</form:select>
					<input id="hidden_wloan_subjecct_id" type="hidden" value="${wloanTermProject.subjectId}" />
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">融资档案：</label>
				<div class="controls">
					<form:select path="docId" class="input-xlarge required" style="width:264px" id="wloan_doc_select">
						<form:option value="" label="请选择"/>
						<c:forEach items="${wloanDocs}" var="wloanDocs">
							<form:option value="${wloanDocs.id }" label="${wloanDocs.name }"/>
						</c:forEach>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">担保机构：</label>
				<div class="controls">
					<form:select path="guaranteeId" class="input-xlarge required" style="width:264px" id="wloan_guarant_select">
						<form:option value="" label="请选择"/>
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
				<label class="control-label">期限：</label>
				<div class="controls">
					<form:select path="span" class="input-large required" style="width:229px">
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
					<sys:treeselect id="area" name="area.id" value="${wloanTermProject.area.id}" labelName="area.name" labelValue="${wloanTermProject.area.name}"
					title="区域" url="/sys/area/treeData" cssClass="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">融资金额：</label>
				<div class="controls">
					<form:input path="amount" htmlEscape="false" class="input-large number required" onkeyup="num(this)"/>
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
					<form:input path="minAmount" htmlEscape="false" class="input-large  number required"/>
					<span class="help-inline">&nbsp;&nbsp;元<font color="red">&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">最大投资金额：</label>
				<div class="controls">
					<form:input path="maxAmount" htmlEscape="false" class="input-large  number required" onkeyup="num(this)"/>
					<span class="help-inline">&nbsp;&nbsp;元<font color="red">&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
		</div>	
			
		<div class="control-group">
			<div class="span6">
				<label class="control-label">递增金额：</label>
				<div class="controls">
					<form:input path="stepAmount" htmlEscape="false" class="input-large  number required"/>
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
					<input name="publishDate" type="text" maxlength="20" class="input-medium Wdate required" style="width:250px" readonly="readonly"
						value="<fmt:formatDate value="${wloanTermProject.publishDate}" pattern="yyyy-MM-dd"/>"
						onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
						<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">上线日期：</label>
				<div class="controls">
					<input name="onlineDate" type="text" maxlength="20" class="input-medium Wdate required" style="width:251px" readonly="readonly"
						value="<fmt:formatDate value="${wloanTermProject.onlineDate}" pattern="yyyy-MM-dd HH:mm"/>"
						onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',isShowClear:false});"/>
						<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">流标日期：</label>
				<div class="controls">
					<input name="loanDate" type="text" maxlength="20" class="input-medium Wdate required loanDate" style="width:250px" readonly="readonly"
						value="<fmt:formatDate value="${wloanTermProject.loanDate}" pattern="yyyy-MM-dd"/>"
						onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
						<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">年化合计利率：</label>
				<div class="controls">
					<form:input path="annualRate" htmlEscape="false" class="input-xlarge number required" style="width:230px"/>
					<span class="help-inline">%</span> <span class="help-inline"><font color="red">*</font> </span>
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
				<label class="control-label">加息利率：</label>
				<div class="controls">
					<form:input path="interestRateIncrease" htmlEscape="false" class="input-xlarge number required" style="width:230px"/>
					<span class="help-inline">%</span> <span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">标的类型：</label>
				<div class="controls">
					<form:select path="projectType" class="input-xlarge required" style="width:264px">
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
					<form:select path="projectProductType" class="input-xlarge required" style="width:264px">
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
					<form:select path="isCanUseCoupon" class="input-xlarge required" style="width:264px" id="wloan_guarant_select">
						<form:option value="0" label="是"/>
						<form:option value="1" label="否"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">是否可用加息券：</label>
				<div class="controls">
					<form:select path="isCanUsePlusCoupon" class="input-xlarge required" style="width:264px" id="wloan_guarant_select">
						<form:option value="0" label="是"/>
						<form:option value="1" label="否"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">标签：</label>
				<div class="controls">
					<form:input path="label" htmlEscape="false" maxlength="32" class="input-xlarge required" style="width:250px"/>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">担保函编号：</label>
				<div class="controls">
					<form:input path="guaranteeSn" htmlEscape="false" maxlength="32" class="input-xlarge " style="width:250px"/>
				</div>
			</div>
		</div>

		<!-- 信息披露5字段. -->
		<div class="control-group">
			<div class="span6">
				<label class="control-label">经营状况及财务状况：</label>
				<div class="controls">
					<form:input path="businessFinancialSituation" htmlEscape="false" maxlength="32" class="input-xlarge required" style="width:250px"/>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">还款能力变化情况：</label>
				<div class="controls">
					<form:input path="abilityToRepaySituation" htmlEscape="false" maxlength="32" class="input-xlarge required" style="width:250px"/>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">平台的逾期情况：</label>
				<div class="controls">
					<form:input path="platformOverdueSituation" htmlEscape="false" maxlength="32" class="input-xlarge required" style="width:250px"/>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">涉诉情况：</label>
				<div class="controls">
					<form:input path="litigationSituation" htmlEscape="false" maxlength="32" class="input-xlarge required" style="width:250px"/>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">受行政处罚情况：</label>
				<div class="controls">
					<form:input path="administrativePunishmentSituation" htmlEscape="false" maxlength="32" class="input-xlarge required" style="width:250px"/>
					<span class="help-inline"><font color="red">*</font></span>
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
						<form:input path="ztmgLoanBasicInfo.creditInformation" id="credit_information_id"/>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">所属行业：</label>
					<div class="controls">
						<form:select path="ztmgLoanBasicInfo.industry" id="industry_id" class="input-xlarge" style="width:264px">
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
						<form:input path="ztmgLoanBasicInfo.contributedCapital" onkeyup="value=value.replace(/[^\d]/g,'')" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px"/>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">年营业收入（元）：</label>
					<div class="controls">
						<form:input path="ztmgLoanBasicInfo.annualRevenue" onkeyup="value=value.replace(/[^\d]/g,'')" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px"/>
					</div>
				</div>
				<div class="span6">
					<label class="control-label">负债（元）：</label>
					<div class="controls">
						<form:input path="ztmgLoanBasicInfo.liabilities" onkeyup="value=value.replace(/[^\d]/g,'')" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px"/>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="span10">
					<label class="control-label">其他借款信息：</label>
					<div class="controls">
						<form:textarea path="ztmgLoanBasicInfo.otherCreditInformation" htmlEscape="false" class="input-xxlarge" rows="2" />
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
						<form:textarea path="borrowerResidence" htmlEscape="false" class="input-xxlarge required" rows="2" />
						<span class="help-inline"><font color="red">&nbsp;&nbsp;&nbsp;*</font> </span>
					</div>
				</div>
			</div>
		</div>	
		
		<div class="control-group">
			<div class="span10">
				<!-- <label class="control-label">担保方案：</label> -->
				<label class="control-label">风控措施：</label>
				<div class="controls">
					<form:textarea path="guaranteeScheme" htmlEscape="false" class="input-xxlarge" rows="4"/>
				</div>
			</div>
		</div>
			
		<div class="control-group">
			<div class="span10">
				<label class="control-label">项目情况：</label>
				<div class="controls">
					<form:textarea path="projectCase" htmlEscape="false"  class="input-xxlarge" rows="4"/>
				</div>
			</div>
		</div>	
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">资金用途：</label>
				<div class="controls">
					<form:textarea path="purpose" htmlEscape="false" class="input-xxlarge required" rows="4" />
					<span class="help-inline"><font color="red">&nbsp;&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
		</div>
		
		<!-- 还款来源. -->
		<div class="control-group">
			<div class="span10">
				<label class="control-label">还款来源：</label>
				<div class="controls">
					<form:textarea path="sourceOfRepayment" htmlEscape="false" class="input-xxlarge required" rows="4" />
					<span class="help-inline"><font color="red">&nbsp;&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
		</div>

		<!-- 还款保障措施. -->
		<div class="control-group">
			<div class="span10">
				<label class="control-label">还款保障措施：</label>
				<div class="controls">
					<form:textarea path="repaymentGuaranteeMeasures" htmlEscape="false" class="input-xxlarge required" rows="4" />
					<span class="help-inline"><font color="red">&nbsp;&nbsp;&nbsp;*</font> </span>
				</div>
			</div>
		</div>

		<div class="control-group">
			<div class="span10">
				<label class="control-label">备注：</label>
				<div class="controls">
					<form:textarea path="remark" htmlEscape="false"  class="input-xxlarge" rows="4"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="form-actions">
				<shiro:hasPermission name="wloanproject:wloanTermProject:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" onclick="checkAmount();"/>&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</div>	
		
	</form:form>
</body>
</html>