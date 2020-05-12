<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>


<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/reset.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/apply.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
	

<script type="text/javascript">
		var financingSpan ;//融资期限
		var financingMoney;//融资金额
		var serviceRate = "${serviceRate}";//服务费率
		var financingRate ;//融资利率
		var serviceMoney;//平台服务费
		var registMoney;//登记服务费
		var interestMoney;//融资利息
		var orderSum = "${orderSum}";//订单总金额
		var financingStep = "${creditUserApply.financingStep}";
		var shareRate = "${creditUserApply.shareRate}";//是否分摊 100 表示不分摊
		var creditUserId = "${creditUserApply.replaceUserId}";//核心企业id
		var sumMoney = "${sumMoney}";//供应商在贷总金额
		var sumMoneyNow = 1000000 - sumMoney ;//可融资金额
		var creditMiddlemenRateList = "${creditMiddlemenRateList}";
		var step = "${step}";
		var creditUserType = "${creditUserType}";//登录用户类型
		var modify = "${creditUserApply.modify}";//核心企业确认提交申请
		
		function init_financing_span(){
			errHide();
			$("#financingOk").show();
			financingSpan = $("#financingSpan").children("option:selected").text();
			var financingRates = $("#financingSpan").val().split(",");
			financingRate = financingRates[1];
			serviceRate = financingRates[2];//服务费率
			$("#financingRate").val(financingRate);
			$("#serviceRate").val(serviceRate);
			financingMoney = $("#financingMoney").val();
			financingMoney = parseInt(financingMoney);
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
		}
		
		$(document).ready(function() {

			//申请流程点击
			$(".step").click(function(){
				var step1 = $(this).children("i").html();
				if(step1-1>financingStep){
					alert("跳转页面尚未完成！");
					return false;
				}else if(step1-1==financingStep){
					if(creditUserType=="02"){

					}else if(creditUserType=="11"){
// 						window.location.href = "${ctx}/apply/orderApply/applyMoney6ToShow?id= ${creditUserApply.id}";
					}
					
				}else{
					window.location.href = "${ctx}/apply/orderApply/applyMoney"+step1+"?id= ${creditUserApply.id}&step="+step1;
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
				$("#confirm").hide();
				$("#confirm2").hide();
				$("#confirm1").hide();
				showServiceMoney();
				//不可更改
				if(modify == "1"){
					$("#financingMoney").attr("readonly",true);
					$("#financingSpan").attr("readonly",true);
				}else if(creditUserType=="02"){
					$(".credit").hide();
					$("#confirm").show();
					$("#confirm1").hide();
					$("#confirm2").hide();
					
				}else if(creditUserType=="11"){
					$("#financingMoney").attr("readonly",true);
					$("#financingSpan").attr("readonly",true);
					$(".credit").show();
					$("#confirm2").show();
					$("#confirm1").show();
					$("#confirm").hide();
				}
				
				financingSpan = "${creditUserApply.span}";
				financingRate = "${creditUserApply.lenderRate}";
				financingMoney = "${creditUserApply.amount}";
				if(financingSpan<=180){
					registMoney = "30";
				}else{
					registMoney = "60";
				}
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
				//融资利息
				interestMoney = formatCurrency((financingMoney*financingRate/36500)*financingSpan);
				$("#financingServices").val(interestMoney);
				//平台服务费
				serviceMoney = formatCurrency((financingMoney*serviceRate/36500)*financingSpan);
				$("#ztmgMoney").val(serviceMoney);
				//费用分摊
				if(shareRate!=100 && shareRate!=""){
					//分摊
					$("#selectYse").attr("checked","checked");
					$("#moneyShare").show();
					$("#creditPercent").val(shareRate);
					
					//融资利息
					interestMoney = formatCurrency((financingMoney*financingRate/36500)*financingSpan);
					//平台服务费
					serviceMoney = formatCurrency((financingMoney*serviceRate/36500)*financingSpan);
					
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
				
				
			}else{//正常申请
				$("#confirm").show();
				$("#confirm1").hide();
				$("#confirm2").hide();
			}
			/* //修改信息
			$(".modify").click(function(){
 				$(this).siblings().attr("readonly","false");
				$(this).hide();
				$("#confirm1").show();
				return false;
			}); */

			$("#financingSpan").on("change", function() {
				errHide();
				$("#financingOk").show();
				financingSpan = $("#financingSpan").children("option:selected").text();
				var financingRates = $("#financingSpan").val().split(",");
				financingRate = financingRates[1];
				serviceRate = financingRates[2];//服务费率
				$("#financingRate").val(financingRate);
				$("#serviceRate").val(serviceRate);
				financingMoney = $("#financingMoney").val();
				financingMoney = parseInt(financingMoney);
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
// 				showServiceMoney();
			});
			$("#financingMoney").on("change", function() {
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
					$("#financingRate").val(financingRate);
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

			//供应商提交申请
			$("#confirm").click(function(){
				if(confirm("请确认您填写的融资申请信息，提交申请后不可再编辑。点击确定将短信加邮件通知核心企业确认，并生成担保函")){
					$("#searchForm").attr("action","${ctx}/apply/orderApply/applyMoney6?id= ${creditUserApply.id}");
					$("#searchForm").submit();
				}else{
					return false;
				}
				
			});
			//核心企业保存
			$("#confirm1").click(function(){
				$("#searchForm").attr("action","${ctx}/apply/orderApply/applyMoney6?id= ${creditUserApply.id}&saveInfo=yes");
				$("#searchForm").submit();
			});
			//核心企业提交申请
			$("#confirm2").click(function(){
				if(confirm("请确认当前平台费用分摊比例正确，确认后将邮件、短信通知供应商。")){
					$("#searchForm").attr("action","${ctx}/apply/orderApply/applyMoney6?id= ${creditUserApply.id}&confirm=1");
					$("#searchForm").submit();
				}else{
					return false;
				}
			});

			// 初始化融资期限.
			init_financing_span();

		});

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
			financingSpan = $("#financingSpan").children("option:selected").text();
			var financingRates = $("#financingSpan").val().split(",");
			var financingRate = financingRates[1];
			$("#financingRate").val(financingRate);
			financingMoney = $("#financingMoney").val();
			
			if(financingSpan=="30"){
				$("#serviceMoney").val("30元");
			}
			if(financingSpan=="60"){
				$("#serviceMoney").val("30元");
			}
			if(financingSpan=="90"){
				$("#serviceMoney").val("30元");
			}
			if(financingSpan=="120"){
				$("#serviceMoney").val("30元");
			}
			if(financingSpan=="360"){
				$("#serviceMoney").val("60元");
			}
			if(financingMoney!=null){
				$("#financingServices").val(formatCurrency((financingMoney*financingRate/36500)*financingSpan));
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
		//供应商提交申请
		function toSave(){
			errHide();
			financingMoney = $("#financingMoney").val();
			financingSpan = $("#financingSpan").children("option:selected").text();
			if(financingMoney==null ||financingMoney=="00"){
				errMsg("请填写融资金额！");
				return false;
			}
			if(financingMoney>sumMoneyNow){
				errMsg("该供应商剩余融资额度为:"+sumMoneyNow);
				return false;
			}
			if(parseFloat(financingMoney)>parseFloat(orderSum)){
				errMsg("融资金额需小于订单金额！");
				return false;
			}
			if(financingSpan==null ||financingSpan=="00"){
				errMsg("请选择融资期限！");
				return false;
			}
		}
		function errMsg(str){
			$("#errMsg").html(str).show();
		}
		function errHide(){
			$("#errMsg").hide();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/user/list">借款申请</a></li>
	</ul>
	
		<form:form id="searchForm" modelAttribute="creditUserApply" onsubmit="return toSave()" action="${ctx}/apply/orderApply/applyMoney6?id=${creditUserApply.id}" method="post" class="breadcrumb form-search">
				<div class="loan_apply">
				<div class="la_tab">
					<div class="clear">
						<ul>
							<li class="" id="tab-1"><i>1</i><span>融资类型</span></li>
							<li class="" id="tab-2"><i>2</i><span>选择采购方</span></li>
							<li class="step" id="tab-3"><i>3</i><span>基础交易</span></li>
							<li class="step" id="tab-4"><i>4</i><span>上传资料</span></li>
							<li class="cur step" id="tab-5"><i>5</i><span>融资申请</span></li>
							<li class="step" id="tab-6"><i>6</i><span>担保函</span></li>
							<li class="" id="tab-7"><i>7</i><span>签订协议</span></li>
						</ul>
					</div>
				</div>
				<div class="la_new_con">
<!-- 				<a href="javascript:;" onclick="history.go(-1);" class="back_icon">返回上一步</a> -->
				<div class="form-horizontal clear">
				<div>
					<b style="color:red">温馨提示：融资申请页面的资料需要供货商填写</b>
				</div>
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
						<form:input path="lenderRate" htmlEscape="false" id="financingRate" value="${creditUserApply.lenderRate }" maxlength="32" class="input-xlarge required"  readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*服务费利率</label>
					<div class="controls">
						<form:input path="" htmlEscape="false" id="serviceRate" maxlength="32" class="input-xlarge required"  readonly="true"/>
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
				<div class="control-group credit" style="display:none">
					<label class="control-label">*平台费用是否分摊</label>
					<div class="controls">
						<input path="" type="radio" name="selectShare" value="1"   class="required" id="selectYse"  readonly="true"/>是
						<input path="" type="radio" name="selectShare" value="0"   class="required" id="selectNo" checked="checked"  readonly="true"/>否
					</div>
				</div>
				<div class=" credit" style="display:none">
					<label class="">（温馨提示：平台费用=服务费+利息+登记费）</label>
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
				<button class="btn clear group_btn group_btn_new" type="submit" id="confirm">提交申请</button>
				<button class="btn clear group_btn group_btn_new"  id="confirm1" onclick="toConfirm();">保存</button>
				<button class="btn clear group_btn group_btn_new"  id="confirm2" onclick="toConfirm();">确认申请</button>
		        <div class="errMsg" id="errMsg"></div>
		</div>
		</div>
		</div>
	</form:form>
		
		</div>
		
</body>
</html>