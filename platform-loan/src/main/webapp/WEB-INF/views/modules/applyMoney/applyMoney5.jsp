<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>


<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/reset.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/bootstrap.min.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/apply.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
<style type="text/css">

.fullTip{
  min-height:100px;
  padding:30px 30px;
  line-height:50px;
  overflow:hidden;
  font-size:18px;
  color:#333;
}
.mask_protocol, .mask_protocol_signature {
	max-height: 600px;
	overflow: auto;
	width: 728px;
	z-index: 1;
}

.mask_02_protocol_signature {
	max-height: 600px;
	overflow: auto;
	width: 728px;
	z-index: 1;
}
.mask_03_protocol_signature {
	max-height: 600px;
	overflow: auto;
	width: 728px;
	z-index: 1;
}
.mask_04_protocol_signature {
	max-height: 600px;
	overflow: auto;
	width: 728px;
	z-index: 1;
}

.mask_gray {
	position: fixed;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	z-index: 1002;
	background-color: rgba(0, 0, 0, .8);
	width: 100%;
	height: 100%;
	display: none;
}

.mask_tip {
	position: fixed;
	top: 50%;
	left: 50%;
	transform: translateX(-50%) translateY(-50%);
	-webkit-transform: translateX(-50%) translateY(-50%);
	background: #fff;
	width: 500px;
	height: 224px;
	z-index: 19;
	border-radius: 20px;
	line-height: 224px;
	box-sizing: border-box;
	display: none;
	font-size: 24px;
	text-align: center;
}
.mask_investNo_tip {
	position: fixed;
	top: 50%;
	left: 50%;
	width: 600px;
	text-align: center;
	padding: 15px 30px;
	color: #fff;
	background: rgba(0, 0, 0, 0.8);
	transform: translate(-50%, -50%);
	-webkit-transform: translate(-50%, -50%);
	z-index: 9999;
	font-size: 20px;
	line-height: 1.4;
	display: none;
	border-radius: 10px;
}
.setting_phone_group{
  overflow:hidden;
}
</style>
<script type="text/javascript">
		var financingSpan ;//融资期限
		var financingMoney;//融资金额
		var financingRate ;//融资利率
		var serviceRate = "${serviceRate}";//服务费率
		var serviceMoney;//平台服务费
		var registMoney;//登记服务费
		var interestMoney;//融资利息
		var voucherSum = "${voucherSum}";//发票总金额
		var creditUserId = "${creditUser.id}";//核心企业id
		var sumMoney = "${sumMoney}";//供应商在贷总金额
		var sumMoneyNow = 1000000 - sumMoney ;//可融资金额
		var step = "${step}";
		var financingStep = "${creditUserApply.financingStep}";
		var shareRate = "${creditUserApply.shareRate}";//是否分摊 100 表示不分摊
		var isModify = '${creditUserApply.modify}';
		var saveMessage = true;//是否保存
		var creditUserType = "${loginCreditUserInfo.creditUserType}";

		function init_financing_span(){

			// 页面填空项进行回填
			// $("#credit_pack_name").html("${creditUserApply.pack.name}"); // 合同名称
			// $("#credit_pack_no").html("${creditUserApply.pack.no}"); // 合同编号

			// 核心企业/供应商角色判断
			// console.log("帐号角色:" + creditUserType);
			if(creditUserType == '02'){ // 页面已有的信息不可编辑
				$("#financingMoney").attr("disabled","disabled"); // 融资金额
				$("#financingSpan").attr("disabled","disabled"); // 融资期限
				$("#selectYse").attr("disabled","disabled"); // 是否分摊，是
				$("#selectNo").attr("disabled","disabled"); // 是否分摊，否
				$("#creditPercent").attr("disabled","disabled"); // 承担比例
			}
			errHide();
			$("#financingOk").show();
			financingSpan = $("#financingSpan").children("option:selected").text();
			var financingRates = $("#financingSpan").val().split(",");
			financingRate = financingRates[1];
			serviceRate = financingRates[2];//服务费率
			$("#financingRate").val(financingRate);
			// 保留两位小数，融资金额允许小数
			financingMoney = $("#financingMoney").val();
			// console.log("financingMoney=" + financingMoney);
			// financingMoney = parseInt(financingMoney);
			// console.log("financingMoney=" + financingMoney);
			if(financingSpan<=180){
				registMoney = "30";
			}else{
				registMoney = "60";
			}
			$("#serviceMoney").val(registMoney+"元");
			$("#serviceRate").val(serviceRate);
			if(financingMoney!=null){
				//融资利息
				interestMoney = formatCurrency((financingMoney*financingRate/36500)*financingSpan);
				$("#financingServices").val(interestMoney);
				//平台服务费
				serviceMoney = formatCurrency((financingMoney*serviceRate/36500)*financingSpan);
				$("#ztmgMoney").val(serviceMoney);
				shareServiceMoney();
			}
			// 年化利率.
			$("#credit_user_rate_id").text(financingRate + "%");
			// 服务费率.
			$("#credit_user_services_rate_id").text(serviceRate + "%");
		}

		$(document).ready(function() {

			/**
			 * 借款人网络借贷风险、禁止性行为及有关事项提示书.
			 */
			// 融资方.
			$("#company_name").text("${supplyUser.enterpriseFullName}");
			// 借款申请编号.
			$("#credit_user_apply_id").text("${creditUserApply.id}");
			// 年化利率.
			$("#credit_user_rate_id").text("${creditUserApply.lenderRate}" + "%");
			// 服务费率.
			$("#credit_user_services_rate_id").text("${serviceRate}" + "%");
			// 借款人网络借贷风险、禁止性行为及有关事项提示书.
			$(".agreement_01").click(function() {
				$(".mask_protocol_signature").show();
			});
			// 关闭弹框.
			$(".close").click(function() {
				$(".mask_protocol_signature").hide(); // 借款人网络借贷风险、禁止性行为及有关事项提示书
				$(".mask_02_protocol_signature").hide(); // 贸易真实性及有关事项提示书
				$(".mask_03_protocol_signature").hide(); // 应收账款质押登记协议
				$(".mask_04_protocol_signature").hide(); // 授权函
			});
			// 借款人网络借贷风险、禁止性行为及有关事项提示书，同意.
			$(".mask_protocol_signature .read_agreen").click(function() {
				$(".mask_protocol_signature").hide();
				$(".agreement_01 span").addClass("cur");
				$("input[id='prompt_book_id']").attr("checked", "true");
				$("#errMsg").hide();
			});
			// 借款人网络借贷风险、禁止性行为及有关事项提示书，取消.
			$(".mask_protocol_signature .read_close").click(function() {
				$(".mask_protocol_signature").hide();
				$(".agreement_01 span").removeClass("cur");
				$("input[id='prompt_book_id']").removeAttr("checked");
			});

			/* 贸易真实性及有关事项提示书 */
			// 贸易真实性及有关事项提示书，展示
			$(".agreement_02").click(function() {
				$(".mask_02_protocol_signature").show();
			});
			// 贸易真实性及有关事项提示书，同意
			$(".mask_02_protocol_signature .read_agreen").click(function() {
				$(".mask_02_protocol_signature").hide();
				$(".agreement_02 span").addClass("cur");
				$("input[id='agreement_02_id']").attr("checked", "true");
				$("#errMsg").hide();
			});
			// 贸易真实性及有关事项提示书，取消
			$(".mask_02_protocol_signature .read_close").click(function() {
				$(".mask_02_protocol_signature").hide();
				$(".agreement_02 span").removeClass("cur");
				$("input[id='agreement_02_id']").removeAttr("checked");
			});
			
			/* 应收账款质押登记协议 */
			// 应收账款质押登记协议，展示
			$(".agreement_03").click(function() {
				$(".mask_03_protocol_signature").show();
			});
			// 应收账款质押登记协议，同意
			$(".mask_03_protocol_signature .read_agreen").click(function() {
				$(".mask_03_protocol_signature").hide();
				$(".agreement_03 span").addClass("cur");
				$("input[id='agreement_03_id']").attr("checked", "true");
				$("#errMsg").hide();
			});
			// 应收账款质押登记协议，取消
			$(".mask_03_protocol_signature .read_close").click(function() {
				$(".mask_03_protocol_signature").hide();
				$(".agreement_03 span").removeClass("cur");
				$("input[id='agreement_03_id']").removeAttr("checked");
			});

			/* 授权函 */
			// 授权函，展示
			$(".agreement_04").click(function() {
				$(".mask_04_protocol_signature").show();
			});
			// 应收账款质押登记协议，同意
			$(".mask_04_protocol_signature .read_agreen").click(function() {
				$(".mask_04_protocol_signature").hide();
				$(".agreement_04 span").addClass("cur");
				$("input[id='agreement_04_id']").attr("checked", "true");
				$("#errMsg").hide();
			});
			// 应收账款质押登记协议，取消
			$(".mask_04_protocol_signature .read_close").click(function() {
				$(".mask_04_protocol_signature").hide();
				$(".agreement_04 span").removeClass("cur");
				$("input[id='agreement_04_id']").removeAttr("checked");
			});

			//申请流程点击
			$(".step").click(function(){
				var step1 = $(this).children("i").html();
				if(step1-1>financingStep){
					alert("跳转页面尚未完成！");
					return false;
				}else if(step1-1==financingStep){
				}else{
					window.location.href = "${ctx}/apply/creditUserApply/applyMoney"+step1+"?id= ${creditUserApply.id}&step="+step1+"&creditUserType="+creditUserType;
				}
			});
			var financingType = "${creditUserApply.financingType}";
			if(financingType=="1"){
				financingType="应收账款转账";
			}else if(financingType=="2"){
				financingType="订单融资";
			}
			$("#financingType").val(financingType);

			//判断正常申请还是显示数据
			if(step!=""){//显示数据
				//判断页面是否可编辑
			 	if(isModify=='1'){//不可编辑
					$("#confirm1").hide();
					$("#confirm2").hide();
				}else{
					$("#confirm1").show();
					$("#confirm2").show();
				} 
				//显示数据
				financingSpan = "${creditUserApply.span}";
				
				financingRate = "${creditUserApply.lenderRate}";

				financingMoney = "${creditUserApply.amount}";
				var financingNow;
				if(financingRate.indexOf(".")=='-1'){
					financingNow = financingSpan+","+financingRate+".00"+","+serviceRate;
				}else{
					financingNow = financingSpan+","+financingRate+","+serviceRate;
				}
				var financingSpans = document.getElementById("financingSpan");
				for (var i = 0; i < financingSpans.length; i++) {
					if (financingSpans.options[i].value == financingNow) {
						$(".select2-chosen:eq(0)").text(financingSpans.options[i].text);
						financingSpans.options[i].selected=true;
						break;
					}
				}
				$("#financingRate").val(financingRate);
				$("#serviceRate").val(serviceRate);
				showServiceMoney();
				//费用分摊
				if(shareRate!=100){
					//分摊
					$("#selectYse").attr("checked","checked");
					$("#moneyShare").show();
					$("#creditPercent").val(shareRate);
					
					//融资利息
					interestMoney = formatCurrency((financingMoney*financingRate/36500)*financingSpan);
					//平台服务费
					serviceMoney = formatCurrency((financingMoney*serviceRate/36500)*financingSpan);
					if(financingSpan<=180){
						registMoney = "30";
					}else{
						registMoney = "60";
					}
					var sumFee = parseFloat(interestMoney.replace(",",""))+parseFloat(serviceMoney.replace(",",""))+parseFloat(registMoney);
					var creditPercent = shareRate;
					creditPercent = parseFloat(creditPercent);
					var regP = /^([1-9][0-9]{0,1}|100)$/;
					if(regP.exec(creditPercent)){
						var supplyPercent = 100-creditPercent;
						$("#supplyPercent").html(supplyPercent);
						var creditFee = formatCurrency(sumFee*creditPercent/100);
						var supplyFee = formatCurrency(sumFee*supplyPercent/100);
						$("#creditFee").html(creditFee);
						$("#supplyFee").html(supplyFee);
					}
				}else{
					//不分摊
					$("#selectNo").attr("checked","checked");
					$("#moneyShare").hide();
				}
				if(financingStep>5){
					$("#confirm2").hide();
				}
			}else{//正常申请
				//显示数据
				financingSpan = "${creditUserApply.span}";
				
				financingRate = "${creditUserApply.lenderRate}";

				financingMoney = "${creditUserApply.amount}";
				var financingNow;
				if(financingRate.indexOf(".")=='-1'){
					financingNow = financingSpan+","+financingRate+".00"+","+serviceRate;
				}else{
					financingNow = financingSpan+","+financingRate+","+serviceRate;
				}
				var financingSpans = document.getElementById("financingSpan");
				for (var i = 0; i < financingSpans.length; i++) {
					if (financingSpans.options[i].value == financingNow) {
						$(".select2-chosen:eq(0)").text(financingSpans.options[i].text);
						financingSpans.options[i].selected=true;
						break;
					}
				}
				$("#financingRate").val(financingRate);
				$("#serviceRate").val(serviceRate);
				showServiceMoney();
				//费用分摊
				if(shareRate!=""){
					if(shareRate!=100){
						//分摊
						$("#selectYse").attr("checked","checked");
						$("#moneyShare").show();
						$("#creditPercent").val(shareRate);
						
						//融资利息
						interestMoney = formatCurrency((financingMoney*financingRate/36500)*financingSpan);
						//平台服务费
						serviceMoney = formatCurrency((financingMoney*serviceRate/36500)*financingSpan);
						if(financingSpan<=180){
							registMoney = "30";
						}else{
							registMoney = "60";
						}
						var sumFee = parseFloat(interestMoney.replace(",",""))+parseFloat(serviceMoney.replace(",",""))+parseFloat(registMoney);
						var creditPercent = shareRate;
						creditPercent = parseFloat(creditPercent);
						var regP = /^([1-9][0-9]{0,1}|100)$/;
						if(regP.exec(creditPercent)){
							var supplyPercent = 100-creditPercent;
							$("#supplyPercent").html(supplyPercent);
							var creditFee = formatCurrency(sumFee*creditPercent/100);
							var supplyFee = formatCurrency(sumFee*supplyPercent/100);
							$("#creditFee").html(creditFee);
							$("#supplyFee").html(supplyFee);
						}
					}else{
						//不分摊
						$("#selectNo").attr("checked","checked");
						$("#moneyShare").hide();
					}

				}
				if(financingStep>5){
					$("#confirm2").hide();
				}
			}

			
			$("#financingSpan").on("change", function() {
				// console.log("financingSpan change");
				errHide();
				$("#financingOk").show();
				financingSpan = $("#financingSpan").children("option:selected").text();
				var financingRates = $("#financingSpan").val().split(",");
				financingRate = financingRates[1];
				serviceRate = financingRates[2];//服务费率
				$("#financingRate").val(financingRate);
				financingMoney = $("#financingMoney").val();
				// financingMoney = parseInt(financingMoney);
				if(financingSpan<=180){
					registMoney = "30";
				}else{
					registMoney = "60";
				}
				$("#serviceMoney").val(registMoney+"元");
				$("#serviceRate").val(serviceRate);
				if(financingMoney!=null){
					//融资利息
					interestMoney = formatCurrency((financingMoney*financingRate/36500)*financingSpan);
					$("#financingServices").val(interestMoney);
					//平台服务费
					serviceMoney = formatCurrency((financingMoney*serviceRate/36500)*financingSpan);
					$("#ztmgMoney").val(serviceMoney);
					shareServiceMoney();
				}
				// 年化利率.
				$("#credit_user_rate_id").text(financingRate + "%");
				// 服务费率.
				$("#credit_user_services_rate_id").text(serviceRate + "%");	
			});
			$("#financingMoney").on("change", function() {
				// console.log("financingMoney change");
				errHide();
				financingMoney = $("#financingMoney").val();
				if(financingMoney!=null){
					if(financingMoney>sumMoneyNow){
						errMsg("该供应商剩余融资额度为:"+sumMoneyNow);
						return false;
					}
					financingSpan = $("#financingSpan").children("option:selected").text();
					var financingRates = $("#financingSpan").val().split(",");
					financingRate = financingRates[1];
					serviceRate = financingRates[2];//服务费率
// 					var financingRate = $("#financingSpan").val();
					$("#financingRate").val(financingRate);
					$("#serviceRate").val(serviceRate);
					if(financingSpan!=null){
						//融资利息
						interestMoney = formatCurrency((financingMoney*financingRate/36500)*financingSpan);
						$("#financingServices").val(interestMoney);
						//平台服务费
						serviceMoney = formatCurrency((financingMoney*serviceRate/36500)*financingSpan);
						$("#ztmgMoney").val(serviceMoney);
						shareServiceMoney();
					}
				}
				
			});
			//是否费用分摊
			$("#selectYse").click(function(){
				$("#moneyShare").show();
			});
			//是否费用分摊
			$("#selectNo").click(function(){
				$("#moneyShare").hide();
			});
			
			//平台费用分摊计算
			$("#creditPercent").on("change",function(){
				shareServiceMoney();
			});

			//修改信息提交
			$("#confirm1").click(function(){
				// saveMessage = false;
				// $("#searchForm").attr("action","${ctx}/apply/creditUserApply/applyMoney6?id= ${creditUserApply.id}&saveInfo=yes");
				// $("#searchForm").submit();
				var span = $("#financingSpan").val();
				var shareRate = $("#creditPercent").val();
				var isTrue = toNext();
				// console.log("isTrue = " + isTrue);
				if(isTrue){
					if(confirm("请确认平台费用分摊比例，确认后将以邮件的形式通知供应商进行授权确认...")){
						$('#confirm1').attr("disabled","disabled");
						$.ajax({
							url : "${ctx}/apply/creditUserApply/applyMoney6", 
							type : "post",
							async: false,
							dataType: 'json',
							data: {
								id : "${creditUserApply.id}",
								saveInfo : "yes",
								amount : financingMoney,
								shareRate : shareRate,
								span : span
							},
							success : function(result) {
								if(result.state == '0'){
									// console.log("通知成功...");
									if('TRUE' == result.is_notice){
										message_prompt("已通知供应商进行确认授权，请勿重复操作...");
									} else {
										message_prompt("供应商授权确认，通知成功...");
									}
								}
							},
							error : function(result) {
								console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
							}
						});
					} else {
						$('#confirm1').removeAttr("disabled");
						message_prompt("通知供应商授权已取消操作...");
					}
				} else {
					message_prompt("请确认第5步融资金额页面内容填写完整...");
				}
			});

			// 初始化融资期限.
			init_financing_span();

		});

		//双击操作空处理
		function ondblclick_fn() {
			console.log("双击了按钮 .... ");
		}

		// 供应商授权
		function supplyAgreement(){
			// 贸易真实性及有关事项提示书
			var agreement02IdIsChecked = $("#agreement_02_id").attr("checked") == "checked";
			if (!agreement02IdIsChecked) {
				errMsg("请授权同意，贸易真实性及有关事项提示书！");
				return false;
			}
			// 应收账款质押登记协议
			var agreement03IdIsChecked = $("#agreement_03_id").attr("checked") == "checked";
			if (!agreement03IdIsChecked) {
				errMsg("请授权同意，应收账款质押登记协议！");
				return false;
			}
			// 授权函
			var agreement04IdIsChecked = $("#agreement_04_id").attr("checked") == "checked";
			if (!agreement04IdIsChecked) {
				errMsg("请授权同意，授权函！");
				return false;
			}
			var span = $("#financingSpan").val();
			var shareRate = $("#creditPercent").val();
			if(confirm("请确认授权操作？")){
				$('.mask_gray').show();
				$('.mask_tip').show();
				// 消息提示展示
				$('#confirm2').attr("disabled","disabled");
				$.ajax({
					url : "${ctx}/apply/creditUserApply/applyMoney6", 
					type : "post",
					async: false,
					dataType: 'json',
					data: {
						id : "${creditUserApply.id}",
						amount : financingMoney,
						shareRate : shareRate,
						span : span
					},
					success : function(result) {
						if(result.state == '0'){
							// console.log("通知成功...");
							if('TRUE' == result.is_authorize){
								$('.mask_gray').hide();
								$('.mask_tip').hide();
								message_prompt("确认授权成功，请勿重复操作...");
							} else {
								message_prompt("确认授权成功...");
								// 跳转至第六步骤
								window.location.href = "${ctx}/apply/creditUserApply/skipApplyMoney6?id=${creditUserApply.id}";
							}
						} else if (result.state == '1') {
							$('.mask_gray').hide();
							$('.mask_tip').hide();
							message_prompt("系统出现异常，联系风控或者管理员...");
						}
					},
					error : function(result) {
						console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
					}
				});
			} else {
				$('.mask_gray').hide();
				$('.mask_tip').hide();
				$('#confirm2').removeAttr("disabled");
				message_prompt("已取消授权操作...");
			}
		}

		//服务费分摊
		function shareServiceMoney(){
			var sumFee = parseFloat(interestMoney.replace(",",""))+parseFloat(serviceMoney.replace(",",""))+parseFloat(registMoney);
			var creditPercent = $("#creditPercent").val();
			creditPercent = parseFloat(creditPercent);
			var regP = /^([1-9][0-9]{0,1}|100)$/;
			if(regP.exec(creditPercent)){
				var supplyPercent = 100-creditPercent;
				$("#supplyPercent").html(supplyPercent);
				var creditFee = formatCurrency(sumFee*creditPercent/100);
				var supplyFee = formatCurrency(sumFee*supplyPercent/100);
				$("#creditFee").html(creditFee);
				$("#supplyFee").html(supplyFee);
			}else{
				alert("请输入1-100的正整数");
			}
		}
		
		//显示服务费
		function showServiceMoney(){
			
			if(financingSpan<=180){
				registMoney = "30";
			}else{
				registMoney = "60";
			}
			$("#serviceMoney").val(registMoney+"元");
			if(financingMoney!=null){
				//融资利息
				interestMoney = formatCurrency((financingMoney*financingRate/36500)*financingSpan);
				$("#financingServices").val(interestMoney);
				//平台服务费
				serviceMoney = formatCurrency((financingMoney*serviceRate/36500)*financingSpan);
				$("#ztmgMoney").val(serviceMoney);
			}
		}

		//格式化金额
		function formatCurrency(num) {
			num = num.toString().replace(/\$|\,/g, '');
			if(isNaN(num))
				num = "0";
			sign = (num == (num = Math.abs(num)));
			num = Math.floor(num * 100 + 0.50000000001);
			cents = num % 100;
			num = Math.floor(num / 100).toString();
			if(cents < 10)
				cents = "0" + cents;
			for(var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ',' +
				num.substring(num.length - (4 * i + 3));
			return(((sign) ? '' : '-') + num + '.' + cents);
		}

		//下一步
		function toNext(){

			// console.log("to next.")

			errHide();
			financingMoney = $("#financingMoney").val();
			if(financingMoney!=""){
				financingMoney = parseFloat(financingMoney);
				console.log("financingMoney:" + financingMoney)
				if(isNaN(financingMoney)){
					errMsg("请填写合法的融资金额！");
					return false;
				}
			}
			sumMoneyNow = parseFloat(sumMoneyNow);
			financingSpan = $("#financingSpan").children("option:selected").text();
			if(financingMoney=="" ||financingMoney=="00"){
				errMsg("请填写融资金额！");
				return false;
			}
			if(financingMoney>sumMoneyNow){
				errMsg("该供应商剩余融资额度为:"+sumMoneyNow);
				return false;
			}
			if(parseFloat(financingMoney)>parseFloat(voucherSum)){
				errMsg("融资金额需小于发票金额！");
				return false;
			}
			if(financingSpan==null ||financingSpan=="00"){
				errMsg("请选择融资期限！");
				return false;
			}

			// 借款人网络借贷风险、禁止性行为及有关事项提示书.
			var isChecked = $("#prompt_book_id").attr("checked") == "checked";
			if (!isChecked) {
				errMsg("请授权同意，借款人网络借贷风险、禁止性行为及有关事项提示书！");
				return false;
			}

		return true;
	}
	function errMsg(str) {
		$("#errMsg").html(str).show();
	}
	function errHide() {
		$("#errMsg").hide();
	}
	// 消息提示 .
	function message_prompt(message) {
		$(".mask_investNo_tip").html(message);
		$(".mask_investNo_tip").show();
		setTimeout(function() {
			$(".mask_investNo_tip").hide();
		}, 3000);
	} // --
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/user/list">借款申请</a></li>
	</ul>
	
		<form:form id="searchForm" modelAttribute="creditUserApply" action="" method="post" class="breadcrumb form-search">
				<div class="loan_apply">
				<div class="la_tab">
					<div class="clear">
						<ul>
							<li class="" id="tab-1"><i>1</i><span>融资类型</span></li>
							<li class="" id="tab-2"><i>2</i><span>选择供应商</span></li>
							<li class="step" id="tab-3"><i>3</i><span>基础交易</span></li>
							<li class="step" id="tab-4"><i>4</i><span>上传资料</span></li>
							<li class="cur step" id="tab-5"><i>5</i><span>融资金额</span></li>
							<li class="" id="tab-6"><i>6</i><span>签订协议</span></li>
						</ul>
					</div>
				</div>
				<div class="la_new_con">
				<div class="form-horizontal clear">
				<div class="control-group">
					<label class="control-label">*融资类型</label>
					<div class="controls">
						<form:input path="" htmlEscape="false" maxlength="32" id="financingType" value="应收账款转账" class="input-xlarge" readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*融资方</label>
					<div class="controls">
						<form:input path="supplyUser.enterpriseFullName" htmlEscape="false" maxlength="32" class="input-xlarge required" value="${supplyUser.enterpriseFullName}" readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*还款方</label>
					<div class="controls">
						<form:input path="creditUser.enterpriseFullName" htmlEscape="false" maxlength="32" value="${creditUser.enterpriseFullName }" class="input-xlarge required"  readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*核心企业</label>
					<div class="controls">
						<form:input path="creditUser.enterpriseFullName" htmlEscape="false" maxlength="32" class="input-xlarge required" value="${creditUser.enterpriseFullName }"  readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*融资金额</label>
					<div class="controls">
						<form:input path="amount" htmlEscape="false" maxlength="32" id="financingMoney" value="${creditUserApply.amount }" class="input-xlarge required"  readonly="false"/>
					</div>
				</div>
				
			    <div class="control-group ">
					<label class="control-label">*融资期限</label>
					<div class="controls">
						<form:select path="span" id="financingSpan" style="width:177px">
							<c:forEach var="rateInfo" items="${creditMiddlemenRateList}">
								<form:option value="${rateInfo.span},${rateInfo.rate},${rateInfo.serviceRate}"  label="${rateInfo.span}" />
							</c:forEach>
						</form:select>
					</div>
				</div>
				
				<div class="control-group ">
					<label class="control-label">*融资利率</label>
					<div class="controls">
						<form:input path="lenderRate" htmlEscape="false" id="financingRate" maxlength="32" value="${creditUserApply.lenderRate }" class="input-xlarge required"  readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*服务费利率</label>
					<div class="controls">
						<form:input path="" htmlEscape="false" id="serviceRate" maxlength="32"  class="input-xlarge required"  readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*还款方式</label>
					<div class="controls">
						<form:input path="" htmlEscape="false" maxlength="32" value="到期还本付息" class="input-xlarge required"  readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*计息方式</label>
					<div class="controls">
						<form:input path="" htmlEscape="false" maxlength="32" class="input-xlarge required" value="按月计息"  readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*融资利息</label>
					<div class="controls">
						<form:input path="" htmlEscape="false" maxlength="32" id="financingServices" value="（融资金额*服务费率/365）*融资期限" class="input-xlarge required"  readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*登记服务费</label>
					<div class="controls">
						<form:input path="" htmlEscape="false" maxlength="32" class="input-xlarge required" id="serviceMoney"  readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*平台服务费</label>
					<div class="controls">
						<form:input path="" htmlEscape="false" maxlength="32" class="input-xlarge required" id="ztmgMoney"  readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*平台费用分摊</label>
					<div class="controls">
						<input path="" type="radio" name="selectShare" value="1"   class="required" id="selectYse"  readonly="true"/>是
						<input path="" type="radio" name="selectShare" value="0"   class="required" id="selectNo" checked="checked"  readonly="true"/>否
					</div>
				</div>
				<div class=" ">
					<p>温馨提示：平台费用=平台服务费+融资利息+登记服务费</p>
					<div class="" id="moneyShare" style="display:none">
						<table id="contentTable" class="table table-striped table-bordered table-condensed">
							<thead></thead>
							<tbody>
								<tr>
									<td>企业名称</td>
									<td>${creditUser.enterpriseFullName}</td>
									<td>${supplyUser.enterpriseFullName}</td>
								</tr>
								<tr>
									<td>承担比例</td>
									<td><form:input path="shareRate" htmlEscape="false" id="creditPercent" placeholder="手动填写（0-100％）" type="number" value="100"  maxlength="32"  class="input-xlarge required" /></td>
									<td id="supplyPercent">自动换算</td>
								</tr>
								<tr>
									<td>承担费用</td>
									<td id="creditFee">自动换算</td>
									<td id="supplyFee">自动换算</td>
								</tr>
							</tbody>
						</table>
						
						
					</div>
				</div>

				<c:if test="${creditUserApply.creditUserType == '02'}">
					<!-- 贸易真实性网络借贷风险、禁止性行为及有关事项提示书 -->
					<div class="setting_phone_group">
						<div class="agreement fl agreement_02">
							<span class=""><input id="agreement_02_id" type="checkbox"><i></i></span>
							<em class="fl">贸易真实性及有关事项提示书</em>
						</div>
					</div>
					<div class="setting_phone_group">
						<div class="agreement fl agreement_03">
							<span class=""><input id="agreement_03_id" type="checkbox"><i></i></span>
							<em class="fl">应收账款质押登记协议</em>
						</div>
					</div>
					<div class="setting_phone_group">
						<div class="agreement fl agreement_04">
							<span class=""><input id="agreement_04_id" type="checkbox"><i></i></span>
							<em class="fl">授权函</em>
						</div>
					</div>
					<!-- 授权，按钮 -->
					<div class="bankcard_btn">
						<p style="color: green;">温馨提示：操作授权后，该笔融资申请状态将流转至审核中，授权过程中，各协议会进行电子签章，请耐心等待。</p>
						<button class="btn btn-lg" type="button" id="confirm2" onclick="supplyAgreement();" ondblclick="ondblclick_fn();" >授权</button>
					</div>
				</c:if>

				<c:if test="${creditUserApply.creditUserType == '11'}">
					<!-- 借款人网络借贷风险、禁止性行为及有关事项提示书，复选框 -->
					<div class="setting_phone_group">
						<div class="agreement fl agreement_01">
							<span class=""><input id="prompt_book_id" type="checkbox"><i></i></span>
							<em class="fl">借款人网络借贷风险、禁止性行为及有关事项提示书</em>
						</div>
					</div>
					<!-- 保存并通知供应商授权，按钮 -->
					<div class="bankcard_btn">
						<p>温馨提示：邮件的形式通知供应商，会出现供应商不及时处理的情况，核心企业线下告知对方是有必要的。</p>
						<button class="btn btn-lg" style="width: 200px;" type="button" id="confirm1" >通知供应商授权</button>
					</div>
				</c:if>

				<!-- 声明文件，阅读声明文件并同意授权，生成声明文件. -->
				<div class="mask_repd mask_protocol_signature">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
						<h4 class="modal-title" id="myModalLabel">借款人网络借贷风险、禁止性行为及有关事项提示书</h4>
					</div>
					<div class="mask_model_repd">
						<div class="protocol_group">
							<h3>借款人网络借贷风险、禁止性行为及有关事项提示书</h3>
							<p><b id="company_name"></b>：</p>
							<p>为了确保贵方充分知悉网络借贷风险及有关事项，防范借贷风险，更好的为贵方服务，现向贵方作以下提示，请务必仔细阅读：</p>
							<p>一、就借款编号为_<b id="credit_user_apply_id"></b>_的借款项目，贵方须按以下标准和方式支付利息及融资服务费：</p>
							<p>利息支付标准为年利率_<b id="credit_user_rate_id"></b>_，支付方式为：按月付息，到期还本。</p>
							<p>平台服务费支付标准为_<b id="credit_user_services_rate_id"></b>_，支付方式为：一次性收取。</p>
							<p>二、如果贵方出现逾期，按照《借款合同》及有关合同约定，贵方应承担以下违约后果：</p>
							<p>1.借款人未按合同约定期限及金额归还本金和利息，则自逾期之日起计收逾期违约金；每逾期一日借款人应按未偿还本金的万分之三向出借人支付逾期违约金直至清偿之日止。</p>
							<p>2.借款人未按约定向平台方支付平台服务费，则自逾期之日起计收逾期违约金；每逾期一日借款人应按逾期金额的万分之三向平台方支付逾期违约金直至清偿之日止。</p>
							<p>3.如借款人逾期，本平台有权根据法律、法规、部门规章，及监管机关和行业自律组织（包括但不限于中国银行保险监督管理委员会、中国人民银行、地方金融监管部门、中国互联网金融协会、北京市互联网金融行业协会等）的相关规定或要求向监管机关和行业自律组织及其指定的监管信息系统报送、提供借款人的逾期信息，从而影响借款人的信用记录。</p>
							<p>4.出借人、本平台或不良债权受让方有权向贵方及担保人催收、提起诉讼或仲裁，相关诉讼仲裁费用及实现债权的费用由贵方承担。</p>
							<p>5.法律法规及合同规定的其他相关不利后果。</p>
							<p>三、以上提示内容与《借款合同》及相关合同约定不一致的，以相关合同约定为准。</p>
							<h3>网络借贷禁止性行为提示</h3>
							<p>请仔细阅读并充分理解《网络借贷信息中介机构业务活动管理暂行办法》及有关监管法律、政策规定的如下网络借贷禁止性行为：</p>
							<p>一、《网络借贷信息中介机构业务活动管理暂行办法》第十条规定：</p>
							<p>网络借贷信息中介机构不得从事或者接受委托从事下列活动：</p>
							<p>（一）为自身或变相为自身融资；</p>
							<p>（二）直接或间接接受、归集出借人的资金；</p>
							<p>（三）直接或变相向出借人提供担保或者承诺保本保息；</p>
							<p>（四）自行或委托、授权第三方在互联网、固定电话、移动电话等电子渠道以外的物理场所进行宣传或推介融资项目；</p>
							<p>（五）发放贷款，但法律法规另有规定的除外；</p>
							<p>（六）将融资项目的期限进行拆分；</p>
							<p>（七）自行发售理财等金融产品募集资金，代销银行理财、券商资管、基金、保险或信托产品等金融产品；</p>
							<p>（八）开展类资产证券化业务或实现以打包资产、证券化资产、信托资产、基金份额等形式的债权转让行为；</p>
							<p>（九）除法律法规和网络借贷有关监管规定允许外，与其他机构投资、代理销售、经纪等业务进行任何形式的混合、捆绑、代理；</p>
							<p>（十）虚构、夸大融资项目的真实性、收益前景，隐瞒融资项目的瑕疵及风险，以歧义性语言或其他欺骗性手段等进行虚假片面宣传或促销等，捏造、散布虚假信息或不完整信息损害他人商业信誉，误导出借人或借款人；</p>
							<p>（十一）向借款用途为投资股票、场外配资、期货合约、结构化产品及其他衍生品等高风险的融资提供信息中介服务；</p>
							<p>（十二）从事股权众筹等业务；</p>
							<p>（十三）法律法规、网络借贷有关监管规定禁止的其他活动。</p>
							<p>二、《网络借贷信息中介机构业务活动管理暂行办法》第十三条规定：</p>
							<p>借款人不得从事下列行为：</p>
							<p>（一）通过故意变换身份、虚构融资项目、夸大融资项目收益前景等形式的欺诈借款；</p>
							<p>（二）同时通过多个网络借贷信息中介机构，或者通过变换项目名称、对项目内容进行非实质性变更等方式，就同一融资项目进行重复融资；</p>
							<p>（三）在网络借贷信息中介机构以外的公开场所发布同一融资项目的信息；</p>
							<p>（四）已发现网络借贷信息中介机构提供的服务中含有本办法第十条所列内容，仍进行交易；</p>
							<p>（五）法律法规和网络借贷有关监管规定禁止从事的其他活动。</p>
							<p>借款人确认：本人已知悉并充分理解上述风险提示和禁止性行为规定</p>
							<div class="clear"><span class="fr"></span></div>
						</div>
						<div class="read_btn">
							<span class="read_agreen">同意</span>
							<span class="read_close">取消</span>
						</div>
					</div>
				</div>
				<!-- 贸易真实性及有关事项提示书 -->
				<div class="mask_repd mask_02_protocol_signature">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
						<h4 class="modal-title" id="myModalLabel">贸易真实性及有关事项提示书</h4>
					</div>
					<div class="mask_model_repd">
						<div class="protocol_group">
							<h3>贸易真实性及有关事项提示书</h3>
							<p>1、我公司于<b id="credit_sign_date"><fmt:formatDate value="${creditUserApply.pack.signDate}" pattern="yyyy-MM-dd"/></b>与【<b id="replace_company_name">${creditUserApply.replaceUserInfo.enterpriseFullName}</b>】签署了【合同名称：<b id="credit_pack_name">${creditUserApply.pack.name}</b>，合同编号：<b id="credit_pack_no">${creditUserApply.pack.no}</b>】，并向【<b>${creditUserApply.replaceUserInfo.enterpriseFullName}</b>】供应了【<b>${creditUserApply.pack.money}</b>】元货物，双方的交易已实际发生，货物不存在质量问题，【<b id="replace_company_name">${creditUserApply.replaceUserInfo.enterpriseFullName}</b>】代为提交的申请借款相关资料真实、准确、全面。</p>
							<p>2、我公司保证在清偿出借人全部本金和利息前，未经出借人书面同意，不会放弃任何对外的债权或者通过任何方式转移、减少我公司的资产。</p>
							<div class="clear"><span class="fr"></span></div>
						</div>
						<div class="read_btn">
							<span class="read_agreen">同意</span>
							<span class="read_close">取消</span>
						</div>
					</div>
				</div>
				<!-- 贸易真实性及有关事项提示书 -->
				<div class="mask_repd mask_03_protocol_signature">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
						<h4 class="modal-title" id="myModalLabel">应收账款质押登记协议</h4>
					</div>
					<div class="mask_model_repd">
						<div class="protocol_group">
							<h3>应收账款质押登记协议</h3>
							<p>本应收账款质押登记协议（以下简称“本协议”）由以下双方于<b><fmt:formatDate value="${creditUserApply.nowDate}" pattern="yyyy-MM-dd"/></b>签署</p>
							<p>甲方：<b>${creditUserApply.supplyUser.enterpriseFullName}</b></p>
							<p>统一社会信用代码/组织机构代码：<b>${creditUserApply.supplyLoanSubject.businessNo}</b></p>
							<p>乙方：<b>中投摩根信息技术（北京）有限责任公司</b></p>
							<p>统一社会信用代码/组织机构代码：<b>91110108306541619G</b></p>
							<p>（甲方、乙方在本协议中合称为“双方”，单称为“一方”）经平等协商，双方达成以下协议，以兹守信：</p>
							<p>1.甲方在中投摩根信息技术（北京）有限责任公司设立的中投摩根平台（https://www.cicmorgan.com）向社会出借人借款，为担保债务的履行，甲方将其应收账款出质给社会出借人。</p>
							<p>2.甲方授权乙方为其办理应收账款的质押登记，并承诺积极提供办理登记所需的一切文件及资料。甲方承担办理质押登记的费用。</p>
							<p>3.甲方承诺，其已经告知乙方并已提供甲方的有效证明文件。甲方保证，其在签署本协议前告知乙方的内容及提供的相关文件是真实、准确、完整，且没有误导的。若由于其提供的应收账款信息不真实、不准确、不完整，或有误导而导致乙方对任何第三人承担任何责任，由乙方承担。</p>
							<p>4.双方确认应收账款的登记期限以登记机关的记录为准。如登记期限届满时，应收账款所列款项尚未全部清偿，甲方同意对登记期限进行展期，并给予乙方全部必要的协助。</p>
							<p>5.应收账款已全部清偿且双方在其他相关协议项下的义务已全部履行完毕后，乙方应及时向登记机关注销应收账款的质押登记，相关费用由甲方承担。</p>
							<p>6.因本协议发生的或与本协议有关的任何争议，应由双方通过友好协商解决；如各方无法通过协商解决，则任何一方均可将该争议提交海南仲裁委员会，依据申请仲裁时该委员会有效的仲裁规则在北京进行仲裁。</p>
							<p>7.本协议未约定事宜，以《借款协议》的约定为准。</p>
							<p>8.本协议采用电子文本形式制成，由双方以电子方式签署确认后生效。生效的电子协议以不可修改的格式保存在乙方运营的中投摩根云平台（https://loan.cicmorgan.com），双方均认可该形式协议的法律效力并同意以留存在中投摩根平台的协议为准。</p>
							<p>9.双方对本协议的任何修改、补充均须以平台电子文本形式作出。</p>
							<p>甲方：<b>${creditUserApply.supplyUser.enterpriseFullName}</b></p>
							<p>乙方：<b>中投摩根信息技术（北京）有限责任公司</b></p>
							<div class="clear"><span class="fr"></span></div>
						</div>
						<div class="read_btn">
							<span class="read_agreen">同意</span>
							<span class="read_close">取消</span>
						</div>
					</div>
				</div>
				<!-- 贸易真实性及有关事项提示书 -->
				<div class="mask_repd mask_04_protocol_signature">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
						<h4 class="modal-title" id="myModalLabel">授权函</h4>
					</div>
					<div class="mask_model_repd">
						<div class="protocol_group">
							<h3>授权函</h3>
							<p>本企业声明，【中投摩根信息技术（北京）有限责任公司】已经依法向本企业提示了相关条款，本企业已经完全知悉并理解本授权条款的内容及相应的法律后果，并愿意接受本授权条款的约定。以下为具体授权事项：</p>
							<p>一、数据采集授权</p>
							<p>同意【中投摩根信息技术（北京）有限责任公司】向上海资信有限公司上传本企业在【中投摩根】平台发生的借款事宜的信用信息（包括但不限于本企业主体信息、每笔借款信息、还款情况），并将上述信用信息纳入由于上海资信有限公司建设的征信系统（包括但不限于网络金融征信系统/商业信用信息征信系统）；上述信用信息可由上海资信有限公司进行采集、存储、匹配、检索、分析、处理加工、信用评估、提供和使用，用于形成本企业信用报告、信用评估及其他相关法律、法规、规章和规范性文件规定的用途。</p>
							<p>二、信息查询授权</p>
							<p>同意【中投摩根信息技术（北京）有限责任公司】向上海资信有限公司查询本企业在上海资信有限公司建设的第三方征信系统（包括但不限于网络金融征信系统）的企业信用信息；查询用途如下：</p>
							<p>（一）提供本企业在其他上海资信有限公司合作信贷机构中的融资情况及还款情况；</p>
							<p>（二）为本企业在【中投摩根】平台的授信提供参考；</p>
							<p>三、授权期限</p>
							<p>本授权条款自本企业授权之日起至本企业还清在【中投摩根】平台上申请的全部借款之日止。</p>
							<p>甲方：<b>${creditUserApply.supplyUser.enterpriseFullName}</b></p>
							<p>签订日期：<b><fmt:formatDate value="${creditUserApply.nowDate}" pattern="yyyy-MM-dd"/></b></p>
							<div class="clear"><span class="fr"></span></div>
						</div>
						<div class="read_btn">
							<span class="read_agreen">同意</span>
							<span class="read_close">取消</span>
						</div>
					</div>
				</div>
				<form:hidden path="replaceUserId" htmlEscape="false" maxlength="32" class="input-xlarge required" value="${creditUser.id }"  readonly="true"/>
				<form:hidden path="projectDataId" htmlEscape="false" maxlength="32" class="input-xlarge required" value="${creditInfo.id }"  readonly="true"/>
				<form:hidden path="creditSupplyId" htmlEscape="false" maxlength="32" class="input-xlarge required" value="${supplyUser.id }"  readonly="true"/>
				<form:hidden path="creditApplyName" htmlEscape="false" maxlength="32" class="input-xlarge required" value="${creditInfo.name }"  readonly="true"/>

				<!-- <button class="btn clear group_btn group_btn_new" id="confirm2" >下一步</button> -->
				<div class="errMsg" id="errMsg"></div>
		</div>
		</div>
		</div>
	</form:form>
	<!--确认签订 弹框 -->
	<div class="mask_wrap" ></div>
	<div class="mask_repd" >
		<h2>正在签订 请耐心等待...</h2>
	</div>
	</div>
	<div class="mask_investNo_tip"></div>
	<div class="mask_gray"></div>
	<div class="mask_tip">正在确认授权，请您耐心等待...</div>
</body>
</html>