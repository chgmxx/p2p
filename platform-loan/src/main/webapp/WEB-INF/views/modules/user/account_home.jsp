<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta name="renderer" content="webkit">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<title>中投摩根—领先的互联网借贷信息交互平台-账户管理</title>
		<meta content="P2P理财,投资理财,个人理财,网上投资,供应链金融,互联网金融,网贷平台,中投摩根" name="keywords" />
		<meta content="中投摩根财富管理平台,中国互联网金融安全出借领航者,出借者首选互联网出借平台,中投摩根在健全的风险管控体系基础上,为出借者提供可信赖的互联网金融出借产品,实现您的金融财富增值." name="description" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/reset.css" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
		<style>
		 .account_con .container-fluid .form_group:nth-of-type(2){
		  background:#f2f2f2;
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
		#parent{background-color:#ffcc66;display:none;}
		#child {line-height: 30px;padding: 5px; color:red;font-size:14px;}
		.alert {
			padding: 0 !important
		}
		</style>
<body>
	<div class="nav_head">企业信息</div>	

	<form:form id="inputForm" modelAttribute="userInfo" action="${ctx}/sys/user/save" method="post" class="form-horizontal">
		<input type="hidden" id="userId" name="userId" value="${userInfo.id}"> 
		<input type="hidden" id="creditUserType" name="creditUserType" value="${userInfo.creditUserType}">
		<input type="hidden" id="availableAmountStr" name="availableAmountStr" value="${userInfo.creditUserAccount.availableAmountStr}">
					<div id="parent" class="serror_msg">
					  <div id="child">Content here</div>
					</div>
					<!--账户管理-->
					<div class="account_wrap">
						<div class="account_con">
							<div class="clear">
								<div class="form_group">
									<dl>
										<dt class="pull-left"><b>账户名称：</b></dt>
										<dd class="pull-left">${userInfo.enterpriseFullName}</dd>
									</dl>
								</div>
								<div class="form_group">
									<dl>
										<dt class="pull-left"><b>账户余额：</b></dt>
										<dd class="pull-left">${userInfo.creditUserAccount.availableAmountStr}</dd>
									</dl>
								</div>
								<!-- 用户成功授权. -->
								<c:if test="${ not empty ztmgUserAuthorization }">
									<div class="form_group">
										<dl>
											<dt class="pull-left"><b>授权状态：</b></dt>
											<dd class="pull-left">${ztmgUserAuthorization.status}</dd>
											<dt class="pull-left" style="padding-left:90px;"><b>授权列表：</b></dt>
											<dd class="pull-left">${ztmgUserAuthorization.grantList}</dd>
											
										</dl>
									</div>
									<div class="form_group">
										<dl>
											<dt class="pull-left"><b>授权金额：</b></dt>
											<dd class="pull-left">${ztmgUserAuthorization.grantAmountList}</dd>
											<dt class="pull-left" style="padding-left:90px;"><b>授权截至期限：</b></dt>
											<dd class="pull-left">${ztmgUserAuthorization.grantTimeList}</dd>
										</dl>
									</div>
								</c:if>
								<!-- 用户未授权. -->
								<c:if test="${empty ztmgUserAuthorization}">
									<div class="form_group">
										<dl>
											<dt class="pull-left"><b>授权状态：</b></dt>
											<dd class="pull-left"><b>未开通授权还款</b></dd>
											<!-- <dd class="pull-left"><b>未开通免密相关授权</b></dd> -->
										</dl>
									</div>
								</c:if>

								<div class="form_group">
									<div class="form_group_bank">
										<h4><span><b>绑定银行卡：</b></span></h4>
										<div class="bankcard_msg">
											<h4>${userInfo.cgbUserBankCard.bankName}</h4>
											<h5>${userInfo.cgbUserBankCard.bankAccountNo}</h5>
											<h6>${userInfo.enterpriseFullName}</h6>
										</div>
										<div class="form_group">
											<dl>
												<dt class="pull-left"><b>绑卡状态：</b></dt>
												<c:if test="${userInfo.cgbUserBankCard.state == 0}">
													<dd class="pull-left">未绑卡</dd>
												</c:if>
												<c:if test="${userInfo.cgbUserBankCard.state == 1}">
													<dd class="pull-left">已绑卡</dd>
												</c:if>
											</dl>
										</div>
										<div class="bankcard_tip">
											<p>温馨提示：建议使用建设银行、工商银行</p>
											<div class="bankcard_btn">
												<!-- button type="button" class="btn btn-lg" data-toggle="modal" data-target="#myswiftModal">快捷充值</button -->
												<c:if test="${userInfo.cgbUserBankCard.state == 1}">
													<button type="button" class="btn btn-lg" data-toggle="modal" data-target="#myModal">网银充值</button>
													<button type="button" class="btn btn-lg" data-toggle="modal" data-target="#myModal_withdraw">提现</button>
												</c:if>
												<c:if test="${userInfo.openAccountState == '1'}">
													<button type="button" class="btn btn-lg" onclick="lanMaoUserAuthorization('${userInfo.id}');">用户授权</button>
												</c:if>
												<li class="clearfix"></li>
												<button type="button" class="btn btn-lg" data-toggle="modal" data-target="#myOfflineRechargeModal">线下转账充值</button>
												<li class="clearfix"></li>
												<c:if test="${userInfo.openAccountState == '1'}">
													<button type="button" class="btn btn-lg" onclick="activateStockedUser('${userInfo.id}');">会员激活</button>
													<button type="button" class="btn btn-lg" onclick="borrowersUnbindBankcard('${userInfo.id}');">解绑银行卡</button>
													<button type="button" class="btn btn-lg" data-toggle="modal" data-target="#myEnterpriseBindBankCard">企业绑卡</button>
												</c:if>
												<!--modal enterpriseBindBankCard-->
												<div class="modal fade modal_recharge" id="myEnterpriseBindBankCard" tabindex="-1" role="dialog" aria-labelledby="myEnterpriseBindBankCardLabel">
													<div class="modal-dialog" role="document">
														<div class="modal-content">
															<div class="modal-header">
																<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
																<h4 class="modal-title" id="myEnterpriseBindBankCardLabel">企业绑卡</h4>
															</div>
															<div class="modal-body recharge_msg">
																<table class="table table-striped table-bordered table-condensed" >
																	<thead>
																	</thead>
																	<tbody>
																		<tr>
																			<td>银行账户</td>
																			<td>
																				<input type="text" style="width: 388px;" name="lmBankcardNo" value="" id="lmBankcardNo">
																			</td>
																		</tr>
																		<tr>
																			<td>开户银行</td>
																			<td>
																				<select style="width: 388px;" id="lmBankName">
																					<option value="00">请选择</option>
																					<!-- 懒猫-企业绑卡/提现，银行支持列表 -->
																					<option value="f26081b05dc84b36bbd5f3cde25ebc04">中国银行</option>
																					<option value="d16c6f66f8bd4e1b83d4f372f8f75ee0">中国农业银行</option>
																					<option value="5abaa0eea76342cdb754e200e5058f1c">中国工商银行</option>
																					<option value="2ad51191131547b9ac2008f5c4d53fb0">中国建设银行</option>
																					<option value="6efc25e492d140b1b5b59c2902c1df6b">兴业银行</option>
																					<option value="3847e7df9189453b9e5c0ea93ceb03c9">北京银行</option>
																					<option value="22f106b435db4dbe9bc984ed2be710f6">平安银行</option>
																					<option value="7a0b1457eaba4d7b8e58286084a92f74">中国民生银行</option>
																					<option value="8382dc8ce04042f9aaa7ce88c6b0e2ac">招商银行</option>
																					<option value="fe702856af2741bcad6fd10eacccb67d">中信银行</option>
																					<option value="2cfcffd577cf4070a63d770882187528">广发银行</option>
																					<option value="a52b130bf25142988afde957d8e4d252">华夏银行</option>
																					<option value="6d6ca8d987f94b6895d064f6dacf895b">成都银行</option>
																					<option value="fe2953bde67d4bf8acc496ff3683b5db">太原市城区农村信用合作联社</option>
																					<option value="6ffed42de17649ef9e8585f7a320156d">交通银行</option>
																					<option value="cf3a1469831046ddb1804c246f895ec3">晋城银行</option>
																					<option value="da2abb875ed84903971280ff5e374417">晋商银行</option>
																					<option value="b5a9991ae6f6435cbf5d68b3fa886016">上海浦东发展银行</option>
																					<option value="36493c9a7e284413839f4a3743969dd7">江西芦溪农村商业银行股份有限公司</option>
																					<option value="57d5a80d87b54e64adf73eea22a2c2a7">遵化市农村信用合作社联合社</option>
																					<option value="2b720b614c6345169b9297e6880dff28">中国光大银行</option>
																					<option value="182bed0bac6e4c6d94331518e2ad7bc7">江西省奉新农村商业银行</option>
																					<option value="edb46cd19a1f4d679cd9af6425ad5be7">东莞农村商业银行</option>
																					<option value="7f9a3177ddd344bf89f4d2b85a1d1fbe">晋中银行</option>
																					<option value="00b2488c21b04b83b1547993b53acbcf">广州银行</option>
																					<option value="50b42f8a469849ea9d2099d420bd744e">新疆天山农村商业银行</option>
																					<option value="b9d3a57b36dc484095c12b1f9bc5f3ce">重庆银行</option>
																					<option value="005f06ba8dda437586167991531c6489">北京农村商业银行</option>
																					<option value="991509626e6840979dc2a6ed9912f747">东营农村商业银行</option>
																				</select>
																			</td>
																		</tr>
																	</tbody>
																</table>
																<div id="messageBox" class="alert alert-success " style="display: none;">缺少必要参数</div>
															</div>
															<div class="modal-footer">
																<button type="button" class="btn btn-primary recharge_btn" id="enterpriseBindBankCard">立即绑卡</button>
															</div>
														</div>
													</div>
												</div>
												<!--modal enterpriseBindBankCard-->
												<!--modal web rechrge-->
												<div class="modal fade modal_recharge" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
													<div class="modal-dialog" role="document">
														<div class="modal-content">
															<div class="modal-header">
																<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
																<h4 class="modal-title" id="myModalLabel">网银充值：</h4>
															</div>
															<div class="modal-body recharge_msg">
																<form class="form-horizontal">
																	<div class="form-group">
																		<label class="control-label pull-left">充值金额</label>
																		<div class="recharge_input pull-left">
																			<input type="number" class="form-control" id="rechargeAmount">
																		</div>
																	</div>
																</form>
															</div>
															<div class="modal-footer">
																<button type="button" class="btn btn-primary recharge_btn" id="recharge">立即充值</button>
															</div>
														</div>
													</div>
												</div>
												<!--modal web rechrge-->
												<!--modal swift rechrge-->
												<div class="modal fade modal_recharge" id="myswiftModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
													<div class="modal-dialog" role="document">
														<div class="modal-content">
															<div class="modal-header">
																<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
																<h4 class="modal-title" id="myModalLabel">快捷充值：</h4>
															</div>
															<div class="modal-body recharge_msg">
																<form class="form-horizontal">
																	<div class="form-group">
																		<label class="control-label pull-left">充值金额</label>
																		<div class="recharge_input pull-left">
																			<input type="number" class="form-control" id="swiftRechargeAmount">
																		</div>
																	</div>
																</form>
															</div>
															<div class="modal-footer">
																<button type="button" class="btn btn-primary recharge_btn" id="swiftRecharge">立即充值</button>
															</div>
														</div>
													</div>
												</div>
												<!--modal swift rechrge-->
												<!-- modal offline recharge start. -->
												<div class="modal fade modal_recharge" id="myOfflineRechargeModal" tabindex="-1" role="dialog" aria-labelledby="myOfflineRechargeModalLabel">
													<div class="modal-dialog" role="document">
														<div class="modal-content">
															<div class="modal-header">
																<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
																<h4 class="modal-title" id="myOfflineRechargeModal"><b>请打开银行APP或网银向以下账户转账完成充值</b></h4>
															</div>
															<div class="modal-body recharge_msg">
																<div class="form-group">
																	<label class="modal-title pull-left"><b>收款户名：</b></label>
																	<div class="pull-left">中投摩根信息技术（北京）有限责任公司存管专户</div>
																</div>
																<div class="form-group">
																	<label class="modal-title pull-left"><b>收款账号：</b></label>
																	<div class="recharge_input pull-left">8981214000000019400</div>
																</div>
																<div class="form-group">
																	<label class="modal-title pull-left"><b>收款银行：</b></label>
																	<div class="recharge_input pull-left">海口联合农村商业银行</div>
																</div>
																<div class="form-group">
																	<label class="modal-title pull-left"><b>支行网点：</b></label>
																	<div class="recharge_input pull-left">海口联合农商银行总行营业部</div>
																</div>
																<div class="form-group">
																	<label class="modal-title pull-left"><b>收款行地址：</b></label>
																	<div class="recharge_input pull-left">海南省海口市</div>
																</div>
																<!-- <form class="form-horizontal">
																	<div class="form-group">
																		<label class="control-label pull-left">充值金额</label>
																		<div class="recharge_input pull-left">
																			<input type="number" class="form-control" id="offlineRechargeAmount">
																		</div>
																	</div>
																</form> -->
															</div>
															<div class="modal-footer">
																<!-- <button type="button" class="btn btn-primary recharge_btn" id="offlineRecharge">立即充值</button> -->
															</div>
														</div>
													</div>
												</div>
												<!-- modal offline recharge end. -->
												<!--modal withdraw-->
												<div class="modal fade modal_recharge" id="myModal_withdraw" tabindex="-1" role="dialog" aria-labelledby="myModalLabel_wh">
													<div class="modal-dialog" role="document">
														<div class="modal-content">
															<div class="modal-header">
																<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
																<h4 class="modal-title" id="myModalLabel_wh">提现：</h4>
															</div>
															<div class="modal-body recharge_msg">
																<form class="form-horizontal">
																	<div class="form-group">
																		<label class="control-label pull-left">可提现金额</label>
																		<div class="recharge_input pull-left">
																			<input type="number" readonly="readonly" class="form-control" value="${userInfo.creditUserAccount.availableAmountStr}">
																		</div>
																	</div>							
																	<div class="form-group">
																		<label class="control-label pull-left">提现金额</label>
																		<div class="recharge_input pull-left">
																			<input type="number" class="form-control"  id="cashAmount">
																		</div>
																	</div>
																</form>
															</div>
															<div class="modal-footer">

																<button type="button" class="btn btn-primary withdraw_btn" id="cash">确认提现</button>
															</div>
														</div>
													</div>
												</div>	
												<!--modal withdraw-->
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
			</form:form>
			<div class="mask_investNo_tip"></div>
</body>
	<script type="text/javascript" src="${ctxStatic}/js/jquery.js"></script>
	<script type="text/javascript" src="${ctxStatic}/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/js/jquery.jerichotab.js"></script>
<script type="text/javascript">
var ctxpath = '${ctxpath}';
var url = "${cgbpath}";
$("#lanmaoform").hide();
$("#lanmaoform").attr('action',url);
var openAccountState = ${userInfo.openAccountState};
/**
 * description: 会员激活 <br>
 * author: Roy <br>
 */
function activateStockedUser(userId){
	 $.ajax({
		url : ctxpath + "/lanmaoAccount/activateStockedUser", 
		type : "post",
		async: false,
		dataType: 'json',
		data: {
			from: "PC",
			platformUserNo: userId,
			userRole: "BORROWERS"
		},
		success : function(result) {
			var state = result.state;
			if(state == '0'){
				// console.log("用户授权参数封装成功了...");
				// 跳转至存管页面进行用户授权
				openPostWindow(url, result.data);
			} else if(state == '2'){
				message_prompt(result.message);
			} else {
				$(".error_msg").html("系统异常，请您联系客服或者技术小哥哥...");
				$(".error_msg").show();
			}
		},
		error : function(data) {
			console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
		}
	});
}

/**
 * description: 借款人解绑银行卡 <br>
 * author: Roy <br>
 */
function borrowersUnbindBankcard(userId){
	 $.ajax({
		url : ctxpath + "/lanmaoAccount/borrowersUnbindBankcard", 
		type : "post",
		dataType: 'json',
		data: {
			from: "PC",
			creditUserId: userId
		},
		success : function(result) {
			var state = result.state;
			if(state == '0'){
				// console.log("用户授权参数封装成功了...");
				// 跳转至存管页面进行用户授权
				openPostWindow(url, result.data);
			} else if(state == '2'){
				message_prompt(result.message);
			} else {
				$(".error_msg").html("系统异常，请您联系客服或者技术小哥哥...");
				$(".error_msg").show();
			}
		},
		error : function(data) {
			console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
		}
	});
}

/**
 * description: 懒猫用户授权 <br>
 * author: Roy <br>
 */
function lanMaoUserAuthorization(userId){
	/**
	 * 用户授权列表
	 * TENDER:授权出借
	 * REPAYMENT:授权还款
	 * CREDIT_ASSIGNMENT:授权债权认购
	 */
	// console.log("用户ID：" + userId);
	 if(openAccountState != '1'){
		alert("您的账户未开户或在审核中，请先开户成功或审核通过之后再进行本次操作。");
		return false;
	 }
	$.ajax({
		url : "${ctxpath}/lanMaoTrade/userAuthorization", 
		type : "post",
		async: false,
		dataType: 'json',
		data: {
			userDevice: "PC",
			platformUserNo: userId,
			authList: "REPAYMENT"
		},
		success : function(data) {
			var state = data.state;
			if(state == 0){
				// console.log("用户授权参数封装成功了...");
				// 跳转至存管页面进行用户授权
				openPostWindow(url, data);
			} else {
				$(".error_msg").html("系统异常，请您联系客服或者技术小哥哥...");
				$(".error_msg").show();
			}
		},
		error : function(data) {
			console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
		}
	});
}

//用window.open()方法跳转至新页面并且用post方式传参
function openPostWindow(url, result){
	
	var tempForm = document.createElement("form");
	tempForm.id = "tempForm1";
	tempForm.method = "post";
	tempForm.action = url;
	//tempForm.target="_blank"; //打开新页面
	// hideInput1
	var hideInput1 = document.createElement("input");
	hideInput1.type = "hidden";
	hideInput1.name="keySerial"; // 后台要接受这个参数来取值
	hideInput1.value = result.keySerial; // 后台实际取到的值
	tempForm.appendChild(hideInput1);
	// hideInput2
	var hideInput2 = document.createElement("input");
	hideInput2.type = "hidden";
	hideInput2.name="serviceName"; // 后台要接受这个参数来取值
	hideInput2.value = result.serviceName; // 后台实际取到的值
	tempForm.appendChild(hideInput2);
	// hideInput3
	var hideInput3 = document.createElement("input");
	hideInput3.type = "hidden";
	hideInput3.name="reqData"; // 后台要接受这个参数来取值
	hideInput3.value = result.reqData; // 后台实际取到的值
	tempForm.appendChild(hideInput3);
	// hideInput4
	var hideInput4 = document.createElement("input");
	hideInput4.type = "hidden";
	hideInput4.name="sign"; // 后台要接受这个参数来取值
	hideInput4.value = result.sign; // 后台实际取到的值
	tempForm.appendChild(hideInput4);
	// hideInput5
	var hideInput5 = document.createElement("input");
	hideInput5.type = "hidden";
	hideInput5.name="platformNo"; // 后台要接受这个参数来取值
	hideInput5.value = result.platformNo; // 后台实际取到的值
	tempForm.appendChild(hideInput5);
	// hideInput6
	var hideInput6 = document.createElement("input");
	hideInput6.type = "hidden";
	hideInput6.name="userDevice"; // 后台要接受这个参数来取值
	hideInput6.value = "PC"; // 后台实际取到的值
	tempForm.appendChild(hideInput6);
	if(document.all){
		tempForm.attachEvent("onsubmit",function(){});        //IE
	}else{
		var subObj = tempForm.addEventListener("submit",function(){},false);    //firefox
	}
	document.body.appendChild(tempForm);
	if(document.all){
		tempForm.fireEvent("onsubmit");
	}else{
		tempForm.dispatchEvent(new Event("submit"));
	}
	tempForm.submit();
	document.body.removeChild(tempForm);
}
//用window.open()方法跳转至新页面并且用post方式传参
function openPostWindowRecharge(url, result){
	
	var tempForm = document.createElement("form");
	tempForm.id = "tempForm1";
	tempForm.method = "post";
	tempForm.action = url;
	tempForm.target="_blank"; //打开新页面
	// hideInput1
	var hideInput1 = document.createElement("input");
	hideInput1.type = "hidden";
	hideInput1.name="keySerial"; // 后台要接受这个参数来取值
	hideInput1.value = result.keySerial; // 后台实际取到的值
	tempForm.appendChild(hideInput1);
	// hideInput2
	var hideInput2 = document.createElement("input");
	hideInput2.type = "hidden";
	hideInput2.name="serviceName"; // 后台要接受这个参数来取值
	hideInput2.value = result.serviceName; // 后台实际取到的值
	tempForm.appendChild(hideInput2);
	// hideInput3
	var hideInput3 = document.createElement("input");
	hideInput3.type = "hidden";
	hideInput3.name="reqData"; // 后台要接受这个参数来取值
	hideInput3.value = result.reqData; // 后台实际取到的值
	tempForm.appendChild(hideInput3);
	// hideInput4
	var hideInput4 = document.createElement("input");
	hideInput4.type = "hidden";
	hideInput4.name="sign"; // 后台要接受这个参数来取值
	hideInput4.value = result.sign; // 后台实际取到的值
	tempForm.appendChild(hideInput4);
	// hideInput5
	var hideInput5 = document.createElement("input");
	hideInput5.type = "hidden";
	hideInput5.name="platformNo"; // 后台要接受这个参数来取值
	hideInput5.value = result.platformNo; // 后台实际取到的值
	tempForm.appendChild(hideInput5);
	// hideInput6
	var hideInput6 = document.createElement("input");
	hideInput6.type = "hidden";
	hideInput6.name="userDevice"; // 后台要接受这个参数来取值
	hideInput6.value = "PC"; // 后台实际取到的值
	tempForm.appendChild(hideInput6);
	if(document.all){
		tempForm.attachEvent("onsubmit",function(){});        //IE
	}else{
		var subObj = tempForm.addEventListener("submit",function(){},false);    //firefox
	}
	document.body.appendChild(tempForm);
	if(document.all){
		tempForm.fireEvent("onsubmit");
	}else{
		tempForm.dispatchEvent(new Event("submit"));
	}
	tempForm.submit();
	document.body.removeChild(tempForm);
}

/**
 * 1）用户授权
 * 2）REPAY,SHARE_PAYMENT（免密还款，免密缴费）
 */
function webMemberAuthorizationCreate(userId){
	// 用户ID
	console.log("用户ID：" + userId);
	$.ajax({
		url : "${ctxpath}/authorization/webMemberAuthorizationCreate", 
		type : "post",
		async: false,
		dataType: 'json',
		data: {
			from: "PC",
			userId: userId,
			grant: "REPAY,SHARE_PAYMENT"
		},
		success : function(data) {
			var encryptRet = data.encryptRet;
			// console.log("tm\t" + encryptRet.tm);
			// console.log("data\t" + encryptRet.data);
			// console.log("merchantId\t" + encryptRet.merchantId);
			// GET请求存管页面，完成用户授权操作.
			window.location.href = url + "?data=" + encryptRet.data + "&tm=" + encryptRet.tm + "&merchantId=" + encryptRet.merchantId;
		},
		error : function(data) {
			console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
		}
	});

}

/**
 * 1）企业绑卡
 */
$("#enterpriseBindBankCard").click(function(){
	var creditUserId = $("#userId").val();
	var bankcardNo = $("#lmBankcardNo").val().trim();
	var bankcardNoLength = bankcardNo.length;
	var pattern = /^[0-9]+$/;
	var str = bankcardNo.replace(/\s*/g, "");
	if (bankcardNoLength < 29 && pattern.test(str)) {
	} else {
		$("#messageBox").show().html("请输入合法的银行账户");
		setTimeout(function(){ $("#messageBox").hide();}, 3000);
		return false;
	}
	// console.log("bankcardNo = " + bankcardNo);
	// 开户银行
	var bankName = $("#lmBankName").children('option:selected').html().trim();
	// console.log("bankName = " + bankName);
	var bankcardId = $("#lmBankName").val().trim();
	// console.log("bankcardId = " + bankcardId);
	if (bankName == "请选择") { // 银行名称
		$("#messageBox").show().html("请选择开户银行");
		setTimeout(function(){ $("#messageBox").hide();}, 3000);
		return false;
	} 
	$.ajax({
		url: ctxpath + "/lanmaoAccount/enterpriseBindBankCard",
		type: "post",
		async: false,
		dataType: 'json',
		data: {
			from: 'PC',
			creditUserId: creditUserId,
			bankcardNo: bankcardNo,
			bankName: bankName,
			bankcardId: bankcardId
		},
		success: function(result) {
			if(result.state == '0') {
				openPostWindow(url, result.data);
			} else if(result.state == '2'){
				message_prompt(result.message);
			} else {
				$(".error_msg").html("系统异常，请您联系客服或者技术小哥哥...");
				$(".error_msg").show();
			}
		},
		error: function(e) {
			console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
		}
	});
});

/**lanmao 2.0 网银充值 */
$("#recharge").click(function(){
	if(openAccountState != '1'){
		alert("您的账户未开户或在审核中，请先开户成功或审核通过之后再进行本次操作。");
		return ;
	}
 	var userId = $("#userId").val();
 	var rechargeAmount = $("#rechargeAmount").val();
 	if(!(/^\d+(\.\d{1,2})?$/.test(rechargeAmount ) || /^\d$/.test(rechargeAmount) )){
 		alert("您输入的金额格式不对,最高精确到分,并且不能为负数!");
 		return ;
 	}
	$.ajax({
		url: ctxpath + "/lmpay/lanmaoWebRecharge",
		type: "post",
		async: false,
		dataType: 'json',
		data: { //  平台营销账户充值   05 
			from: "5",
			token: userId,
			amount: rechargeAmount,
			isbankcode: "0"
		},
		success: function(json) {
			if(json.state == 4) {
				logout();
			} else { 
				if(json.state == 0) {
					var data = json.data;
					openPostWindowRecharge(url, json.data);
				} else if (json.state == '5'){
					message_prompt("未绑定银行卡，请先进行绑卡注册操作");
				} else if (json.state == '9'){
					$(".serror_msg").show();
			  		$("#child").html("<h5>充值失败， 充值流水记录失败！</h5>");
			  	  	setTimeout(
			  	    	  	function(){ $(".serror_msg").hide();}, 3000);
				} else {
					$(".error_msg").html("网络出现异常，请您稍后再试。");
					$(".error_msg").show();
				}
			}
		},
		error: function(e) {
			$(".error_msg").html("网络出现异常，请您稍后再试。");
			$(".error_msg").show();
		}
	});
 });

/**lanmao 2.0 快捷充值 */
$("#swiftRecharge").click(function(){
	if(openAccountState != '1'){
		alert("您的账户未开户或在审核中，请先开户成功或审核通过之后再进行本次操作。");
		return false;
	 }
	//alert(userId);
 	var userId = $("#userId").val();
 	var rechargeAmount = $("#swiftRechargeAmount").val();
 	if(!(/^\d+(\.\d{1,2})?$/.test(rechargeAmount ) || /^\d$/.test(rechargeAmount) )){
 		alert("您输入的金额格式不对,最高精确到分,并且不能为负数!");
 		return ;
 	}
	$.ajax({
		url: ctxpath + "/lmpay/lanmaoSwiftRecharge",
		type: "post",
		async: false,
		dataType: 'json',
		data: { //  平台营销账户充值   05 
			from: "5",
			token: userId,
			amount: rechargeAmount
		},
		success: function(json) {
			if(json.state == 4) {
				logout();
			} else { 
				if(json.state == 0) {
					var data = json.data;
					openPostWindowRecharge(url, json.data);
				} else if (json.state == '5'){
					message_prompt("未绑定银行卡，请先进行绑卡注册操作");
				} else if (json.state == '9'){
					$(".serror_msg").show();
			  		$("#child").html("<h5>充值失败， 充值流水记录失败！</h5>");
			  	  	setTimeout(
			  	    	  	function(){ $(".serror_msg").hide();}, 3000);
				} else {
					$(".error_msg").html("网络出现异常，请您稍后再试。");
					$(".error_msg").show();
				}
			}
		},
		error: function(e) {
			$(".error_msg").html("网络出现异常，请您稍后再试。");
			$(".error_msg").show();
		}
	});
 });
/**
 $("#recharge").click(function(){
 	var userId = $("#userId").val();
 	var rechargeAmount = $("#rechargeAmount").val();
	$.ajax({
		url: ctxpath + "/newpay/creditAuthRechargeWeb",
		type: "post",
		async: false,
		dataType: 'json',
		data: {
			from: "2",
			userId: userId,
			amount: rechargeAmount,
			bizType: '01'
		},
		success: function(json) {
			if(json.state == 4) {
				logout();
			} else {
				if(json.state == 0) {
					var data = json.data;
					var tm = data.tm;
					var merchantId = data.merchantId;
					// window.location.href = url + "?data=" + data.data + "&tm=" + tm + "&merchantId=" + merchantId;
					// 浏览器，打开新的窗口，进行充值操作（页面嵌入操作，有些银行会限制，也就是在跳转银行界面的时候必须重新开启一个新的窗口或者完全跳转至银行的界面）.
					window.open(url + "?data=" + data.data + "&tm=" + tm + "&merchantId=" + merchantId, "pc recharge",false);
				} else {
					$(".error_msg").html("网络出现异常，请您稍后再试。");
					$(".error_msg").show();
				}
			}
		},
		error: function(e) {
			$(".error_msg").html("网络出现异常，请您稍后再试。");
			$(".error_msg").show();
		}
	});
 }); */

// 转账充值，
$("#offlineRecharge").click(function(){
 	var userId = $("#userId").val();
 	var offlineRechargeAmount = $("#offlineRechargeAmount").val();
 	if(!(/^\d+(\.\d{1,2})?$/.test(offlineRechargeAmount ) || /^\d$/.test(offlineRechargeAmount) )){
 		alert("您输入的金额格式不对,最高精确到分,并且不能为负数!");
 		return ;
 	}
	$.ajax({
		url : "${ctxpath}/newpay/creditOfflineRechargeWeb", 
		type : "post",
		async: false,
		dataType: 'json',
		data: {
			from: "2",
			userId: userId,
			amount: offlineRechargeAmount
		},
		success : function(data) {
			if(data.state == 0){
				var encryptRet = data.encryptRet;
				// console.log("tm\t" + encryptRet.tm);
				// console.log("data\t" + encryptRet.data);
				// console.log("merchantId\t" + encryptRet.merchantId);
				// GET请求存管页面，完成用户转账充值操作.
				window.location.href = url + "?data=" + encryptRet.data + "&tm=" + encryptRet.tm + "&merchantId=" + encryptRet.merchantId;
			} else {
				$(".error_msg").html(data.message);
				$(".error_msg").show();
			}
		},
		error : function(data) {
			console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
		}
	});

});

// 懒猫 2.0 提现
 $("#cash").click(function(){
	var userId = $("#userId").val();
  	var cashAmount = $("#cashAmount").val();
  	var availableAmountStr = $("#availableAmountStr").val();
  	// console.log(userId+" >> "+ cashAmount + " >> " + availableAmountStr)
  	if(isRealNum(availableAmountStr) && availableAmountStr <= 0.0) {
		$(".serror_msg").show();
  		$("#child").html("<h5>可用余额为零！！</h5>");
  	  	setTimeout(function(){ $(".serror_msg").hide();}, 3000);
  	  	$("#cashAmount").val("");
  	  	return 
  	}
  	if(isRealNum(availableAmountStr) && isRealNum(cashAmount) && cashAmount <= 0.0) {
		$(".serror_msg").show();
  		$("#child").html("<h5>请输入有效的提现金额！！</h5>");
  	  	setTimeout(function(){ $(".serror_msg").hide();}, 3000);
  	  	$("#cashAmount").val("");
  	  	return 
  	}
  	// alert($("#creditUserType").val())
  	//console.log(isRealNum(availableAmountStr))
  	//console.log(isRealNum(cashAmount))
  	//console.log(cashAmount >= 1)
  	//console.log(cashAmount)
  	//console.log(availableAmountStr)
  	//console.log(parseInt(cashAmount) <= parseInt(availableAmountStr))
  	if(isRealNum(availableAmountStr) && isRealNum(cashAmount)  && (parseInt(cashAmount) > 1) &&  (parseInt(cashAmount) <= parseInt(availableAmountStr)) ) {
  		$.ajax({
  	 		url: ctxpath + "/lanmaowithdraw/CreditAuthwithdraw",
  	 		type: 'post',
  	 		dataType: 'json',
  	 		data: {
  	 			from: $("#creditUserType").val(),
  	 			userId: userId,
  	 			amount: cashAmount,
  	 			bizType: '01'
  	 		},
  	 		success: function(json) {
  	 			if(json.state == 4) {
  	 				logout();
  	 			} else if(json.state == 9) {
  	 				$(".serror_msg").show();
  	 		  		$("#child").html("<h3>提现失败，插件提现流水失败！</h3>");
  	 		  	  	$("#cashAmount").val("");
  	 		  	  	setTimeout(
  	 		  	    	  	function(){ $(".serror_msg").hide();}, 3000);
  	 			} else if(json.state == 0) {
  	 				openPostWindow(url, json.data);
  	 			} else if(json.state == 5) {
  	 				$(".error_msg").html("您输入的提现金额有误，请您核实后重新输入。");
  	 				$(".error_msg").show();
  	 			} else if(json.state == 7) {
  	 				window.location.href = "account_setting_bankcard.html";
  	 			}
  	 		}
  	 	});
  	}else {
		$(".serror_msg").show();
  		$("#child").html("<h5>请输入有效的提现金额!!</h5>");
  	  	$("#cashAmount").val("");
  	  	setTimeout(function(){ $(".serror_msg").hide();}, 3000);
  	}
  });



 /**
 *判断是否是数字
 *
 **/

 function isRealNum(val){
     // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除，
 　　if(val === "" || val ==null){
         return false;
 　　}
    if(!isNaN(val)){　　　　
 　　//对于空数组和只有一个数值成员的数组或全是数字组成的字符串，isNaN返回false，例如：'123'、[]、[2]、['123'],isNaN返回false,
    //所以如果不需要val包含这些特殊情况，则这个判断改写为if(!isNaN(val) && typeof val === 'number' )
 　　　 return true; 
 　　}
 　else{ 
 　　　　return false; 
 　　} 
 }
 /**
$("#cash").click(function(){
 	var userId = $("#userId").val();
 	var cashAmount = $("#cashAmount").val();
	$.ajax({
		url: ctxpath + "/newwithdraw/creditWithdrawWeb",
		type: 'post',
		dataType: 'json',
		data: {
			from: '2',
			userId: userId,
			amount: cashAmount,
			bizType: '01'
		},
		success: function(json) {
			if(json.state == 4) {
				logout();
			} else if(json.state == 0) {
				var data = json.data;
				var tm = data.tm;
				var merchantId = data.merchantId;
				window.location.href = url + "?data=" + data.data + "&tm=" + tm + "&merchantId=" + merchantId;
			} else if(json.state == 5) {
				$(".error_msg").html("您输入的提现金额有误，请您核实后重新输入。");
				$(".error_msg").show();
			} else if(json.state == 7) {
				window.location.href = "account_setting_bankcard.html";
			}
		}
	});
 }); */
 $(function() {
	 if(parseInt($("#creditUserType").val()) == 5){
		$(".bankcard_btn button[data-target='#myModal_withdraw']").hide()
	}else{
		$(".bankcard_btn button[data-target='#myModal_withdraw']").show()
	}
});

/*关闭动态模态框*/
$(".recharge_btn").click(function(){
	$('#myModal').modal('hide');
});

$(".withdraw_btn").click(function(){
	$('#myModal_withdraw').modal('hide');
});

// 消息提示 .
function message_prompt(message) {
	$(".mask_investNo_tip").html(message);
	$(".mask_investNo_tip").show();
	setTimeout(function() {
		$(".mask_investNo_tip").hide();
	}, 3000);
} // --
</script>
</html>