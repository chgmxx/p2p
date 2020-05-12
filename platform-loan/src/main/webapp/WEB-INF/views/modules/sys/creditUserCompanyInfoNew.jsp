<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>企业开户信息</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/reset.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/bootstrap.min.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/lightbox.css" />
	<script type="text/javascript" src="${ctxStatic}/js/jquery.js"></script>
	<script type="text/javascript" src="${ctxStatic}/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/js/jquery.jerichotab.js"></script>
	<script src="${ctxStatic}/jquery-jbox/2.3/jquery.jBox-2.3.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
	<script src="${ctxStatic}/common/mustache.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/js/lightbox.js" type="text/javascript"></script>
	<script src="${ctxStatic}/js/jquery.cookie.js" type="text/javascript"></script>
	<script src="${ctxStatic}/js/addressArea.js?v=3" type="text/javascript"></script>
	<style>
.lookimg_delBtn {
	z-index: 10
}

.div_imglook>div:hover .lookimg_delBtn {
	display: block !important;
}

.div_imglook>div.default:hover .lookimg_delBtn {
	display: none !important;
}

.loan_apply_wrap {
	padding: 0 0 20px 0px;
}

.loan_apply {
	padding: 0 20px 0 0;
}

.mask_gray {
	position: fixed;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	z-index: 12;
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
.fullTip{
  min-height:100px;
  padding:30px 30px;
  line-height:50px;
  overflow:hidden;
  font-size:18px;
  color:#333;
}

.mask_tip_popup_window {
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
	box-sizing: border-box;
	display: none;
	text-align: center;
	line-height: 2;
	font-size: 20px;
	text-align: center;
	color: #333;
	padding: 30px;
}

.mask_tip_popup_window p:nth-of-type(2) {
	font-size: 14px;
	color: #666;
	text-align: left;
}
.word_lh{
	line-height:30px;
}
.word_lh b{
	font-weight:bold!important;
}
.close_tip{
position: absolute;
	top: 20px;
	right: 20px;
	font-size:20px;
	color:#666;
	cursor:pointer;
}
</style>
	<script type="text/javascript">
		var ctxpath = '${ctxpath}';
		var cgbpath = '${cgbpath}';
		var middlemenId;//代偿户
		var creditUserId = '${userBankCard.creditUserInfo.id}';
		var businessLicenseType;
		var state = '${userBankCard.state}';
		var userBankCard = '${userBankCard}';
		var creditUserInfo = '${userBankCard.creditUserInfo}';
		var creditUserType = '${userBankCard.creditUserInfo.creditUserType}';

		// 企业借款用户开户状态
		var openAccountState = '${creUserInfo.openAccountState}';

		$(function() {

			// 初始化
			init_companyInfo();

			$(".flie_wrap .div_imgfile").click(function() {
				closeMessage();
				$(this).siblings(".file").click();

			});

			var on = document.querySelector(".div_imglook");
			//	    需要把阅读的文件传进来file element是把读取到的内容放入的容器
			function readFile(file, element, id, type) {
				var reader = new FileReader();
				//        根据文件类型选择阅读方式
				switch (file.type) {
				case 'image/jpg':
				case 'image/png':
				case 'image/jpeg':
				case 'image/gif':
					reader.readAsDataURL(file);
					break;
				}
				//        当文件阅读结束后执行的方法
				reader.addEventListener('load', function() {
					//         如果说让读取的文件显示的话 还是需要通过文件的类型创建不同的标签
					switch (file.type) {
					case 'image/jpg':
					case 'image/png':
					case 'image/jpeg':
					case 'image/gif':
						var img = document.createElement('img');
						var wrap = document.createElement('div');
						var input = document.createElement('input');
						input.setAttribute("class", "delete");
						input.setAttribute("type", "hidden");
						input.setAttribute("value", id)
						wrap.setAttribute("class", "lookimg_delBtn");
						wrap.setAttribute("id", id);
						wrap.innerHTML = "移除";
						img.src = reader.result;
						img.className = "example-image";
						var img_wrap = $("<div><a class='lookimg_wrap example-image-link' data-lightbox='example-"+type+"' href='"+reader.result+"'><img class='example-image' src='"+reader.result+"'/><input class='delete' type='hidden' value='"+id+"'/></a><div class='lookimg_delBtn' id="+id+">移除</div><div class='tit_pic'>" + file.name + "</div>WW</div>");
						// 							img_wrap.append(input);
						element.append(img_wrap);
						element.show();
						$("#" + id).click(function() {
							var $this = $(this);
							console.log("id = " + id);
							deleteCreditInfo($this, id);
						});
						break;
					}
				});
			}

			$(".lookimg_delBtn").click(function() {
				closeMessage();
				var $this = $(this);
				var annexFileId = $this.attr("id");
				deleteCreditInfo($this, annexFileId);
			});

			$(".file").on("change", function() {
				closeMessage();
				var file = this.files[0];
				var $this = $(this);
				$this.parent().siblings(".info_error_msg").hide();
				var formData = new FormData();
				var type = $this.attr("id");
				var count = $this.parent().siblings(".div_imglook").children(".lookimg_wrap").length;
				if (type == 8) {
					if (count == 1) {
						$this.parent().siblings(".info_error_msg").show().html("最多只能上传一张图片！");
						return;
					}
				}
				if (type == 9) {
					if (count == 1) {
						$this.parent().siblings(".info_error_msg").show().html("最多只能上传一张图片！");
						return;
					}
				}
				if (type == 10) {
					if (count == 2) {
						$this.parent().siblings(".info_error_msg").show().html("最多只能上传两张图片！");
						return;
					}
				}

				//	var file = document.getElementById("file1").files;
				formData.append("type", type);
				formData.append("creditInfoId", creditUserId);
				formData.append("file1", file);
				$.ajax({
					url : ctxpath + "/creditInfo/uploadCreditInfo",
					type : 'post',
					dataType : 'json',
					data : formData,
					// 告诉jQuery不要去处理发送的数据
					processData : false,
					// 告诉jQuery不要去设置Content-Type请求头
					contentType : false,
					success : function(result) {
						if (result.state == 0) {
							var annexFileId = result.annexFileId;

							readFile(file, $this.parent().siblings(".div_imglook"), annexFileId, type);

						}

					}
				});

			})

			$("#modifyBankCard").click(function() {
				closeMessage();
				$("#creditBankCard").hide();
				// 				$("#creditBankCard2").hide();
				$("#modifyBankCardView").show();
			})

		});

		function deleteCreditInfo($this, id) {
			closeMessage();
			$.ajax({
				url : ctxpath + "/creditInfo/deleteCredit",
				type : 'post',
				dataType : 'json',
				data : {
					id : creditUserId,
					annexFileId : id
				},
				success : function(result) {
					if (result.state == 0) {
						// 					$this.parent().parent().siblings(".info_error_msg").hide();
						$this.parent().remove();

					} else {
						$("#messageBox").show().html(result.message);
					}

				}
			});
		}

		function checkEmail(myemail) {
			var myReg = /^[a-zA-Z0-9_-]+@([a-zA-Z0-9]+\.)+(com|cn|net|org)$/;

			if (myReg.test(myemail)) {
				return true;
			} else {
				$("#messageBox").show().html("邮箱格式不对!");
				return false;
			}
		}

		function openAccount(type) {
			closeMessage();
			$("#openAccount").hide();
			businessLicenseType = $("#businessLicenseType").val();
			var creditUserId = "${userBankCard.creditUserInfo.id}";
			var bizType = $("#bizType").val();
			var enterpriseFullName = $("#enterpriseFullName").val();
			var businessLicense = $("#businessLicense").val();
			var taxRegCertNo = $("#taxRegCertNo").val();
			var orgCode = $("#orgCode").val();
			var bankPermitCertNo = $("#bankPermitCertNo").val();
			var agentPersonName = $("#agentPersonName").val();
			var agentPersonCertType = $("#agentPersonCertType").val();
			var agentPersonCertNo = $("#agentPersonCertNo").val();
			var agentPersonPhone = $("#agentPersonPhone").val();
			var corporationName = $("#corporationName").val();
			var corporationCertType = $("#corporationCertType").val();
			var corporationCertNo = $("#corporationCertNo").val();
			var bankName = $("#bankName").children('option:selected').html();
			var bankCode = $("#bankName").val();
			var bankCardNo = $("#bankCardNo").val();
			var bankCardName = enterpriseFullName;
			var bankProvince = $("#bankProvince").val();
			var bankCity = $("#bankCity").val();
			var issuerName = $("#issuerName").val();
			var issuer = $("#issuer").val();
			var email = $("#email").val();
			var registAddress = $("#registAddress").val();
			if (bizType == null || bizType.trim() == "" || bizType == '00') {
				$("#messageBox").show().html("请选择账户类型");
				$("#openAccount").show();
				return;
			}
			if (enterpriseFullName == null || enterpriseFullName.trim() == "") {
				$("#messageBox").show().html("请填写企业全称");
				$("#openAccount").show();
				return;
			}
			if (businessLicenseType == null || businessLicenseType.trim() == "" || businessLicenseType == "00") {
				$("#messageBox").show().html("请选择证照类型");
				$("#openAccount").show();
				return;
			}
			if (businessLicense == null || businessLicense.trim() == "") {
				$("#messageBox").show().html("请填写证照号");
				$("#openAccount").show();
				return;
			}
			if (businessLicenseType == "BLC") {
				if (taxRegCertNo == null || taxRegCertNo.trim() == "") {
					$("#messageBox").show().html("请填写税务登记证");
					$("#openAccount").show();
					return;
				}
				if (orgCode == null || orgCode.trim() == "") {
					$("#messageBox").show().html("请填写组织机构代码");
					$("#openAccount").show();
					return;
				}
			}

			if (agentPersonName == null || agentPersonName.trim() == "") {
				$("#messageBox").show().html("请填写联系人姓名");
				$("#openAccount").show();
				return;
			}
			if (agentPersonCertType == null || agentPersonCertType.trim() == "") {
				$("#messageBox").show().html("请选择联系人证件类型");
				$("#openAccount").show();
				return;
			}
			if (agentPersonCertNo == null || agentPersonCertNo.trim() == "") {
				$("#messageBox").show().html("请填写联系人证件号");
				$("#openAccount").show();
				return;
			}

			if (agentPersonPhone == null || agentPersonPhone.trim() == "") {
				$("#messageBox").show().html("请填写联系人手机号");
				$("#openAccount").show();
				return;
			}

			if (bankPermitCertNo == null || bankPermitCertNo.trim() == "") {
				$("#messageBox").show().html("请填写核准号");
				$("#openAccount").show();
				return;
			}

			if (corporationName == null || corporationName.trim() == "") {
				$("#messageBox").show().html("请填写法人姓名");
				$("#openAccount").show();
				return;
			}
			if (corporationCertType == null || corporationCertType.trim() == "") {
				$("#messageBox").show().html("请选择法人证件类型");
				$("#openAccount").show();
				return;
			}
			if (corporationCertNo == null || corporationCertNo.trim() == "") {
				$("#messageBox").show().html("请填写法人证件号");
				$("#openAccount").show();
				return;
			}

			if (bankName == null || bankName.trim() == "") {
				$("#messageBox").show().html("请填写银行名称");
				$("#openAccount").show();
				return;
			}
			if (bankCardNo == null || bankCardNo.trim() == "") {
				$("#messageBox").show().html("请填写银行账号");
				$("#openAccount").show();
				return;
			}
			if (bankProvince == null || bankProvince.trim() == "") {
				$("#messageBox").show().html("请选择开户城市（省）");
				$("#openAccount").show();
				return;
			}
			if (bankCity == null || bankCity.trim() == "") {
				$("#messageBox").show().html("请选择开户城市（市）");
				$("#openAccount").show();
				return;
			}
			if (issuerName == null || issuerName.trim() == "") {
				$("#messageBox").show().html("请填写支行名称");
				$("#openAccount").show();
				return;
			}
			if (issuer == null || issuer.trim() == "") {
				$("#messageBox").show().html("请填写支行-联行号");
				$("#openAccount").show();
				return;
			}
			if (email == null || email.trim() == "") {
				$("#messageBox").show().html("请填写邮箱");
				$("#openAccount").show();
				return;
			}
			if (registAddress == null || registAddress.trim() == "") {
				$("#messageBox").show().html("请填写注册地址");
				$("#openAccount").show();
				return;
			}
			//邮箱有效性验证
			if (email != null && !email == "") {
				if (!checkEmail(email)) {
					$("#openAccount").show();
					return;
				}
			}
			if (type == 2) {
				if (businessLicenseType == "${userBankCard.creditUserInfo.wloanSubject.businessLicenseType}") {
					businessLicenseType = null;
				}
				if (enterpriseFullName == "${userBankCard.creditUserInfo.wloanSubject.companyName }") {
					// 					enterpriseFullName=null;
					bankCardName = null;
				}
				if (businessLicense == "${userBankCard.creditUserInfo.wloanSubject.businessNo}") {
					businessLicense = null;
				}
				if (taxRegCertNo == "${userBankCard.creditUserInfo.wloanSubject.taxCode}") {
					taxRegCertNo = null;
				}
				if (orgCode == "${userBankCard.creditUserInfo.wloanSubject.organNo}") {
					orgCode = null;
				}
				if (bankPermitCertNo == "${userBankCard.creditUserInfo.wloanSubject.bankPermitCertNo}") {
					bankPermitCertNo = null;
				}
				if (agentPersonName == "${userBankCard.creditUserInfo.wloanSubject.agentPersonName}") {
					agentPersonName = null;
				}
				if (agentPersonCertType == "${userBankCard.creditUserInfo.wloanSubject.agentPersonCertType}") {
					agentPersonCertType = null;
				}
				if (agentPersonCertNo == "${userBankCard.creditUserInfo.wloanSubject.agentPersonCertNo}") {
					agentPersonCertNo = null;
				}
				if (agentPersonPhone == "${userBankCard.creditUserInfo.wloanSubject.agentPersonPhone}") {
					agentPersonPhone = null;
				}
				if (corporationName == "${userBankCard.creditUserInfo.wloanSubject.loanUser }") {
					corporationName = null;
				}
				if (corporationCertType == "${userBankCard.creditUserInfo.wloanSubject.corporationCertType}") {
					corporationCertType = null;
				}
				if (corporationCertNo == "${userBankCard.creditUserInfo.wloanSubject.corporationCertNo}") {
					corporationCertNo = null;
				}
				if (bankName == "${userBankCard.creditUserInfo.wloanSubject.loanBankName}") {
					bankName = null;
					issuerName = null;
					issuer = null;
				} else {
					if (issuerName == null || issuerName.trim() == "") {
						$("#messageBox").show().html("请填写支行名称");
						$("#openAccount").show();
						return;
					}
					if (issuer == null || issuer.trim() == "") {
						$("#messageBox").show().html("请填写支行-联行号");
						$("#openAccount").show();
						return;
					}
				}
				if (bankCardNo == "${userBankCard.creditUserInfo.wloanSubject.loanBankNo}") {
					bankCardNo = null;
				}
				if (email == "${userBankCard.creditUserInfo.wloanSubject.email}") {
					email = null;
				}
				if (registAddress == "${userBankCard.creditUserInfo.wloanSubject.registAddress}") {
					registAddress = null;
				}
			}

			$(".mask_gray").show();
			$(".mask_tip").show();

			if (type == 1) {
				$.ajax({
					url : ctxpath + "/cgbPay/accountCreateByCompanyForErp",
					type : "post",
					dataType : "json",
					data : {
						id : creditUserId,
						bizType : bizType,
						supplierId : creditUserId,
						enterpriseFullName : enterpriseFullName,
						businessLicenseType : businessLicenseType,
						businessLicense : businessLicense,
						taxRegCertNo : taxRegCertNo,
						orgCode : orgCode,
						bankPermitCertNo : bankPermitCertNo,
						agentPersonName : agentPersonName,
						agentPersonCertType : agentPersonCertType,
						agentPersonCertNo : agentPersonCertNo,
						agentPersonPhone : agentPersonPhone,
						corporationName : corporationName,
						corporationCertType : corporationCertType,
						corporationCertNo : corporationCertNo,
						bankName : bankName,
						bankCode : bankCode,
						bankCardNo : bankCardNo,
						bankCardName : bankCardName,
						bankProvince : bankProvince,
						bankCity : bankCity,
						issuerName : issuerName,
						issuer : issuer,
						email : email,
						registAddress : registAddress
					},
					success : function(result) {
						$(".mask_gray").hide();
						$(".mask_tip").hide();
						console.log(result);
						//console.log(result.message);
						if (result.state == 0) {
							console.log("成功");
							var data = result.data;
							var tm = data.tm;
							var merchantId = data.merchantId;

							window.location.href = cgbpath + "?data=" + data.data + "&tm=" + tm + "&merchantId=" + merchantId;
						} else {
							$("#messageBox").show().html(result.message);
							$("#openAccount").show();
						}
					}
				});
			} else {
				$.ajax({
					url : ctxpath + "/app/updateenterprise",
					type : "post",
					dataType : "json",
					data : {
						id : creditUserId,
						bizType : bizType,
						supplierId : creditUserId,
						enterpriseFullName : enterpriseFullName,
						businessLicenseType : businessLicenseType,
						businessLicense : businessLicense,
						taxRegCertNo : taxRegCertNo,
						orgCode : orgCode,
						bankPermitCertNo : bankPermitCertNo,
						agentPersonName : agentPersonName,
						agentPersonCertType : agentPersonCertType,
						agentPersonCertNo : agentPersonCertNo,
						agentPersonPhone : agentPersonPhone,
						corporationName : corporationName,
						corporationCertType : corporationCertType,
						corporationCertNo : corporationCertNo,
						bankName : bankName,
						bankCode : bankCode,
						bankCardNo : bankCardNo,
						bankCardName : bankCardName,
						bankProvince : bankProvince,
						bankCity : bankCity,
						issuerName : issuerName,
						issuer : issuer,
						email : email,
						registAddress : registAddress
					},
					success : function(result) {
						$(".mask_gray").hide();
						$(".mask_tip").hide();
						console.log(result);
						//console.log(result.message);
						if (result.state == 0) {
							console.log("成功");
							location.reload();
						} else {
							$("#messageBox").show().html(result.message);
							$("#openAccount").show();
						}
					}
				});
			}
		}

		function closeMessage() {
			$("#messageBox").hide();
		}

		// 取消.
		function popup_window_cancel() {
			$(".mask_gray").hide();
			$(".mask_tip_popup_window").hide();
		}

		// 立即完善.
		function popup_window_perfect() {
			window.location.href = "${ctx}/loan/basicinfo/ztmgLoanBasicInfo/ztmgLoanBasicInfoForm?creditUserId=" + creditUserId;
			var tab_basicinfo = window.parent.document.getElementById("tab_basicinfo");
			$(tab_basicinfo).addClass("cur").siblings().removeClass("cur");
		}
		
		
		/**
		 * 初始化企业信息方法
		 */
		function init_companyInfo(){

			// console.log(state);
			// 根据状态state来判断按钮列表的展示
			if(openAccountState == "0"){ // 未开户
				$("#update_id").hide(); // 修改
				$("#save_id").show(); // 保存
				$("#insert_submit_id").show(); // 企业绑卡注册
				$("#update_submit_id").hide(); // 企业信息修改
			} else if(openAccountState == "1"){ // 已开户
				// 输入框加锁
				// 企业资料信息
				$("#bizType").attr("disabled", "disabled"); // 账户类型
				$("#enterpriseFullName").attr("disabled", "disabled"); // 企业全称
				$("#corporationName").attr("disabled", "disabled"); // 法人姓名
				$("#corporationCertType").attr("disabled", "disabled"); // 法人证件类型
				$("#corporationCertNo").attr("disabled", "disabled"); // 法人证件号
				$("#registAddress").attr("disabled", "disabled"); // 注册地址
				$("#businessLicenseType").attr("disabled", "disabled"); // 证照类型
				$("#businessLicense").attr("disabled", "disabled"); // 证照号
				$("#taxRegCertNo").attr("disabled", "disabled"); // 税务登记证
				$("#orgCode").attr("disabled", "disabled"); // 组织机构代码
				// 企业账户信息
				/**
				// 加锁
				$("#bankName").removeAttr("disabled"); // 银行名称
				$("#bankCardNo").removeAttr("disabled"); // 银行账号
				$("#l_address_province").removeAttr("disabled"); // 开户省
				$("#l_address_city").removeAttr("disabled"); // 开户市
				$("#l_address_county").removeAttr("disabled"); // 开户县(区)
				$("#issuerName").removeAttr("disabled"); // 支行名称
				$("#bankPermitCertNo").removeAttr("disabled"); // 核准号
				$("#issuer").removeAttr("disabled"); // 支行-联行号
				*/
				// 解锁
				$("#bankName").attr("disabled", "disabled"); // 银行名称
				$("#bankCardNo").attr("disabled", "disabled"); // 银行账号
				$("#l_address_province").attr("disabled", "disabled"); // 开户省
				$("#l_address_city").attr("disabled", "disabled"); // 开户市
				$("#l_address_county").attr("disabled", "disabled"); // 开户县(区)
				$("#issuerName").attr("disabled", "disabled"); // 支行名称
				$("#bankPermitCertNo").attr("disabled", "disabled"); // 核准号
				$("#issuer").attr("disabled", "disabled"); // 支行-联行号
				// 企业联系人信息
				$("#agentPersonName").attr("disabled", "disabled"); // 姓名
				$("#agentPersonPhone").attr("disabled", "disabled"); // 手机号码
				$("#agentPersonCertType").attr("disabled", "disabled"); // 证件类型
				$("#agentPersonCertNo").attr("disabled", "disabled"); // 证件号
				$("#email").attr("disabled", "disabled"); // 邮箱

				$("#save_id").hide(); // 保存
				$("#update_id").show(); // 修改按钮隐藏
				$("#insert_submit_id").hide(); // 企业绑卡注册
				$("#update_submit_id").show(); // 企业信息修改

				// 添加图片-隐藏
				$("#flie_wrap_1").hide();
				$("#flie_wrap_2").hide();
				$("#flie_wrap_3").hide();
				$(".div_imglook>div").addClass("default");
				// 图片移除-隐藏
				/**
				$(".div_imglook>div").hover(function () { // 鼠标指针浮动在上面触发事件
					$(this).addClass("div_none_hover");
				}).mouseout(function () { // 鼠标指针浮动移除触发事件
					$(this).removeClass("div_none_hover");
				});
				*/
			} else if(openAccountState == "2"){ // 审核中
				// 输入框加锁
				// 企业资料信息
				$("#bizType").attr("disabled", "disabled"); // 账户类型
				$("#enterpriseFullName").attr("disabled", "disabled"); // 企业全称
				$("#corporationName").attr("disabled", "disabled"); // 法人姓名
				$("#corporationCertType").attr("disabled", "disabled"); // 法人证件类型
				$("#corporationCertNo").attr("disabled", "disabled"); // 法人证件号
				$("#registAddress").attr("disabled", "disabled"); // 注册地址
				$("#businessLicenseType").attr("disabled", "disabled"); // 证照类型
				$("#businessLicense").attr("disabled", "disabled"); // 证照号
				$("#taxRegCertNo").attr("disabled", "disabled"); // 税务登记证
				$("#orgCode").attr("disabled", "disabled"); // 组织机构代码
				// 企业账户信息
				$("#bankName").attr("disabled", "disabled"); // 银行名称
				$("#bankCardNo").attr("disabled", "disabled"); // 银行账号
				$("#l_address_province").attr("disabled", "disabled"); // 开户省
				$("#l_address_city").attr("disabled", "disabled"); // 开户市
				$("#l_address_county").attr("disabled", "disabled"); // 开户县(区)
				$("#issuerName").attr("disabled", "disabled"); // 支行名称
				$("#bankPermitCertNo").attr("disabled", "disabled"); // 核准号
				$("#issuer").attr("disabled", "disabled"); // 支行-联行号
				// 企业联系人信息
				$("#agentPersonName").attr("disabled", "disabled"); // 姓名
				$("#agentPersonPhone").attr("disabled", "disabled"); // 手机号码
				$("#agentPersonCertType").attr("disabled", "disabled"); // 证件类型
				$("#agentPersonCertNo").attr("disabled", "disabled"); // 证件号
				$("#email").attr("disabled", "disabled"); // 邮箱

				$("#keep_one_eyes_open_id").hide(); // 注意事项
				$("#update_id").hide(); // 修改
				$("#save_id").hide(); // 保存
				$("#insert_submit_id").hide(); // 企业绑卡注册
				$("#update_submit_id").hide(); // 企业信息更新
				// 添加图片-隐藏
				$("#flie_wrap_1").hide();
				$("#flie_wrap_2").hide();
				$("#flie_wrap_3").hide();
				$(".div_imglook>div").addClass("default");
			} else if(openAccountState == "3"){ // 审核回退
				// 输入框激活
				// 企业资料信息
				$("#bizType").removeAttr("disabled"); // 账户类型
				$("#enterpriseFullName").removeAttr("disabled"); // 企业全称
				$("#corporationName").removeAttr("disabled"); // 法人姓名
				$("#corporationCertType").removeAttr("disabled"); // 法人证件类型
				$("#corporationCertNo").removeAttr("disabled"); // 法人证件号
				$("#registAddress").removeAttr("disabled"); // 注册地址
				$("#businessLicenseType").removeAttr("disabled"); // 证照类型
				$("#businessLicense").removeAttr("disabled"); // 证照号
				$("#taxRegCertNo").removeAttr("disabled"); // 税务登记证
				$("#orgCode").removeAttr("disabled"); // 组织机构代码
				// 企业账户信息
				$("#bankName").removeAttr("disabled"); // 银行名称
				$("#bankCardNo").removeAttr("disabled"); // 银行账号
				$("#l_address_province").removeAttr("disabled"); // 开户省
				$("#l_address_city").removeAttr("disabled"); // 开户市
				$("#l_address_county").removeAttr("disabled"); // 开户县(区)
				$("#issuerName").removeAttr("disabled"); // 支行名称
				$("#bankPermitCertNo").removeAttr("disabled"); // 核准号
				$("#issuer").removeAttr("disabled"); // 支行-联行号
				// 企业联系人信息
				$("#agentPersonName").removeAttr("disabled"); // 姓名
				$("#agentPersonPhone").removeAttr("disabled"); // 手机号码
				$("#agentPersonCertType").removeAttr("disabled"); // 证件类型
				$("#agentPersonCertNo").removeAttr("disabled"); // 证件号
				$("#email").removeAttr("disabled"); // 邮箱

				$("#keep_one_eyes_open_id").hide(); // 注意事项
				$("#update_id").hide(); // 修改
				$("#save_id").hide(); // 保存
				$("#insert_submit_id").hide(); // 企业绑卡注册
				$("#update_submit_id").hide(); // 企业信息修改

				// 添加图片-隐藏
				$("#flie_wrap_1").hide();
				$("#flie_wrap_2").hide();
				$("#flie_wrap_3").hide();
				// $(".div_imglook>div").addClass("default");
				// 图片移除-隐藏
				$(".div_imglook>div").hover(function () { // 鼠标指针浮动在上面触发事件
					$(this).addClass("div_none_hover");
				}).mouseout(function () { // 鼠标指针浮动移除触发事件
					$(this).removeClass("div_none_hover");
				});
			} else if(openAccountState == "4"){ // 审核拒绝
				// 输入框加锁
				// 企业资料信息
				$("#bizType").attr("disabled", "disabled"); // 账户类型
				$("#enterpriseFullName").attr("disabled", "disabled"); // 企业全称
				$("#corporationName").attr("disabled", "disabled"); // 法人姓名
				$("#corporationCertType").attr("disabled", "disabled"); // 法人证件类型
				$("#corporationCertNo").attr("disabled", "disabled"); // 法人证件号
				$("#registAddress").attr("disabled", "disabled"); // 注册地址
				$("#businessLicenseType").attr("disabled", "disabled"); // 证照类型
				$("#businessLicense").attr("disabled", "disabled"); // 证照号
				$("#taxRegCertNo").attr("disabled", "disabled"); // 税务登记证
				$("#orgCode").attr("disabled", "disabled"); // 组织机构代码
				// 企业账户信息
				$("#bankName").attr("disabled", "disabled"); // 银行名称
				$("#bankCardNo").attr("disabled", "disabled"); // 银行账号
				$("#l_address_province").attr("disabled", "disabled"); // 开户省
				$("#l_address_city").attr("disabled", "disabled"); // 开户市
				$("#l_address_county").attr("disabled", "disabled"); // 开户县(区)
				$("#issuerName").attr("disabled", "disabled"); // 支行名称
				$("#bankPermitCertNo").attr("disabled", "disabled"); // 核准号
				$("#issuer").attr("disabled", "disabled"); // 支行-联行号
				// 企业联系人信息
				$("#agentPersonName").attr("disabled", "disabled"); // 姓名
				$("#agentPersonPhone").attr("disabled", "disabled"); // 手机号码
				$("#agentPersonCertType").attr("disabled", "disabled"); // 证件类型
				$("#agentPersonCertNo").attr("disabled", "disabled"); // 证件号
				$("#email").attr("disabled", "disabled"); // 邮箱

				$("#keep_one_eyes_open_id").hide(); // 注意事项
				$("#update_id").hide(); // 修改
				$("#save_id").hide(); // 保存
				$("#insert_submit_id").hide(); // 企业绑卡注册
				// $("#update_submit_id").hide(); // 企业信息更新
				$("#update_submit_id").hide(); // 企业信息修改
				// 添加图片-隐藏
				$("#flie_wrap_1").hide();
				$("#flie_wrap_2").hide();
				$("#flie_wrap_3").hide();
				$(".div_imglook>div").addClass("default");
			}

			// 省份.
			var province = '${wloanSubject.loanBankProvince}';
			var provinceObj = document.getElementById("l_address_province");
			for (var i = 0; i < provinceObj.options.length; i++) {
				if (provinceObj.options[i].value == province) {
					provinceObj.options[i].selected = true;
					change(1); // 联动地级市.
					break;
				}
			}
			// 地级市.
			var city = '${wloanSubject.loanBankCity}';
			var cityObj = document.getElementById("l_address_city");
			for (var i = 0; i < cityObj.options.length; i++) {
				if (cityObj.options[i].value == city) {
					cityObj.options[i].selected = true;
					change(2); // 联动市、县级市.
					break;
				}
			}
			// 市、县级市.
			var county = "${wloanSubject.loanBankCounty}";
			var countyObj = document.getElementById("l_address_county");
			for (var i = 0; i < countyObj.options.length; i++) {
				if (countyObj.options[i].value == county) {
					countyObj.options[i].selected = true;
					break;
				}
			}

			// 账户类型
			var bizTypes = document.getElementById("bizType");
			// console.log("账户类型：\t" + creditUserType);
			for (var i = 0; i < bizTypes.options.length; i++) {
				if (bizTypes.options[i].value == creditUserType) {
					bizTypes.options[i].selected = true;
					break;
				}
			}

			//证照类型
			var businessLicenseType = '${userBankCard.creditUserInfo.wloanSubject.businessLicenseType}';
			var businessLicenseTypes = document.getElementById("businessLicenseType");
			for (var i = 0; i < businessLicenseTypes.options.length; i++) {
				if (businessLicenseTypes.options[i].value == businessLicenseType) {
					businessLicenseTypes.options[i].selected = true;
					break;
				}
			}
			// 融资主体不存在时进行初始化
			var blType = $("#businessLicenseType").val().trim();
			if (blType == "USCC") {
				$("#taxRegCertNo").parent().hide();
				$("#taxRegCertNoA").hide();
				$("#orgCode").parent().hide();
				$("#orgCodeA").hide();
			} else {
				$("#taxRegCertNoA").show();
				$("#taxRegCertNo").parent().show();
				$("#orgCodeA").show();
				$("#orgCode").parent().show();
			}

			//法人证件类型
			var corporationCertType = '${userBankCard.creditUserInfo.wloanSubject.corporationCertType}';
			var corporationCertTypes = document.getElementById("corporationCertType");
			for (var i = 0; i < corporationCertTypes.options.length; i++) {
				if (corporationCertTypes.options[i].value == corporationCertType) {
					corporationCertTypes.options[i].selected = true;
					break;
				}
			}

			//银行名称
			var bankName = '${userBankCard.creditUserInfo.wloanSubject.loanBankName}';
			var bankNames = document.getElementById("bankName");
			for (var i = 0; i < bankNames.options.length; i++) {
				if (bankNames.options[i].text == bankName) {
					bankNames.options[i].selected = true;
					break;
				}
			}

			//联系人证件类型
			var agentPersonCertType = '${userBankCard.creditUserInfo.wloanSubject.agentPersonCertType}';
			var agentPersonCertTypes = document.getElementById("agentPersonCertType");
			for (var i = 0; i < agentPersonCertTypes.options.length; i++) {
				if (agentPersonCertTypes.options[i].value == agentPersonCertType) {
					agentPersonCertTypes.options[i].selected = true;
					break;
				}
			}

			// 证照类型隐藏及展示
			$("#businessLicenseType").change(function() {
				closeMessage();
				businessLicenseType = $("#businessLicenseType").val();
				if (businessLicenseType == "USCC") {
					$("#taxRegCertNo").parent().hide();
					$("#taxRegCertNoA").hide();
					$("#orgCode").parent().hide();
					$("#orgCodeA").hide();
				} else {
					$("#taxRegCertNoA").show();
					$("#taxRegCertNo").parent().show();
					$("#orgCodeA").show();
					$("#orgCode").parent().show();
				}
			});
		}
		
		/**
		 * 企业信息修改（1.激活输入框）
		 */
		function companyInfo_update(){
			// console.log("执行修改操作 ...")
			if(openAccountState == "4"){
				// 输入框加锁
				// 企业资料信息
				$("#bizType").attr("disabled", "disabled"); // 账户类型
				$("#enterpriseFullName").attr("disabled", "disabled"); // 企业全称
				$("#corporationName").attr("disabled", "disabled"); // 法人姓名
				$("#corporationCertType").attr("disabled", "disabled"); // 法人证件类型
				$("#corporationCertNo").attr("disabled", "disabled"); // 法人证件号
				$("#registAddress").attr("disabled", "disabled"); // 注册地址
				$("#businessLicenseType").attr("disabled", "disabled"); // 证照类型
				$("#businessLicense").attr("disabled", "disabled"); // 证照号
				$("#taxRegCertNo").attr("disabled", "disabled"); // 税务登记证
				$("#orgCode").attr("disabled", "disabled"); // 组织机构代码
				// 企业账户信息
				$("#bankName").attr("disabled", "disabled"); // 银行名称
				$("#bankCardNo").attr("disabled", "disabled"); // 银行账号
				$("#l_address_province").attr("disabled", "disabled"); // 开户省
				$("#l_address_city").attr("disabled", "disabled"); // 开户市
				$("#l_address_county").attr("disabled", "disabled"); // 开户县(区)
				$("#issuerName").attr("disabled", "disabled"); // 支行名称
				$("#bankPermitCertNo").attr("disabled", "disabled"); // 核准号
				$("#issuer").attr("disabled", "disabled"); // 支行-联行号
				// 企业联系人信息
				$("#agentPersonName").attr("disabled", "disabled"); // 姓名
				$("#agentPersonPhone").attr("disabled", "disabled"); // 手机号码
				$("#agentPersonCertType").attr("disabled", "disabled"); // 证件类型
				$("#agentPersonCertNo").attr("disabled", "disabled"); // 证件号
				$("#email").attr("disabled", "disabled"); // 邮箱
			} else {
				// 输入框激活
				// 企业资料信息
				$("#bizType").removeAttr("disabled"); // 账户类型
				$("#enterpriseFullName").removeAttr("disabled"); // 企业全称
				$("#corporationName").removeAttr("disabled"); // 法人姓名
				$("#corporationCertType").removeAttr("disabled"); // 法人证件类型
				$("#corporationCertNo").removeAttr("disabled"); // 法人证件号
				$("#registAddress").removeAttr("disabled"); // 注册地址
				$("#businessLicenseType").removeAttr("disabled"); // 证照类型
				$("#businessLicense").removeAttr("disabled"); // 证照号
				$("#taxRegCertNo").removeAttr("disabled"); // 税务登记证
				$("#orgCode").removeAttr("disabled"); // 组织机构代码
				// 企业账户信息
				$("#bankName").removeAttr("disabled"); // 银行名称
				$("#bankCardNo").removeAttr("disabled"); // 银行账号
				$("#l_address_province").removeAttr("disabled"); // 开户省
				$("#l_address_city").removeAttr("disabled"); // 开户市
				$("#l_address_county").removeAttr("disabled"); // 开户县(区)
				$("#issuerName").removeAttr("disabled"); // 支行名称
				$("#bankPermitCertNo").removeAttr("disabled"); // 核准号
				$("#issuer").removeAttr("disabled"); // 支行-联行号
				// 企业联系人信息
				$("#agentPersonName").removeAttr("disabled"); // 姓名
				$("#agentPersonPhone").removeAttr("disabled"); // 手机号码
				$("#agentPersonCertType").removeAttr("disabled"); // 证件类型
				$("#agentPersonCertNo").removeAttr("disabled"); // 证件号
				$("#email").removeAttr("disabled"); // 邮箱
			}
			
			// 修改按钮隐藏
			$("#update_id").hide();
			// 保存于提交按钮展示
			$("#save_id").show();
			// 提交按钮逻辑判断
			if (openAccountState == "0") { // 未开户
				$("#insert_submit_id").show();
				$("#update_submit_id").hide();
			} else if (openAccountState == "1") { // 已开户 
				$("#insert_submit_id").hide();
				$("#update_submit_id").show();
			} else if (openAccountState == "2") { // 审核中
				$("#insert_submit_id").show();
				$("#update_submit_id").hide();
			} else if (openAccountState == "3") { // 审核回退 
				$("#insert_submit_id").show();
				$("#update_submit_id").hide();
			} else if (openAccountState == "4") { // 审核拒绝 
				$("#insert_submit_id").hide();
				$("#update_submit_id").hide();
			}

			// 添加图片-展示
			$("#flie_wrap_1").show();
			$("#flie_wrap_2").show();
			$("#flie_wrap_3").show();
			// 移除按钮-展示
			$(".div_imglook>div").removeClass("default");
		}

		//用window.open()方法跳转至新页面并且用post方式传参
		function openPostWindow(url, result){
			
			var tempForm = document.createElement("form");
			tempForm.id = "tempForm1";
			tempForm.method = "post";
			tempForm.action = url;
			// tempForm.target="_blank"; //打开新页面
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
		 * 企业开户信息提交（1.输入框加锁）
		 */
		function companyInfo_submit(type) {
			/* if(type == "2"){
				message_prompt("企业信息修改，暂不开放");
				return false;
			} */
			// console.log("企业信息修改 ...")
			// return false;
			// console.log("执行提交操作 ...")
			// 保存操作，持久化数据库
			// 企业资料信息
			var creditUserType = $("#bizType").val().trim();
			var enterpriseFullName = $("#enterpriseFullName").val().trim();
			var corporationName = $("#corporationName").val().trim();
			var registAddress = $("#registAddress").val().trim();
			var corporationCertType = $("#corporationCertType").val().trim();
			var corporationCertNo = $("#corporationCertNo").val().trim();
			var businessLicenseType = $("#businessLicenseType").val().trim();
			var businessLicense = $("#businessLicense").val().trim();
			var taxRegCertNo = $("#taxRegCertNo").val().trim();
			var orgCode = $("#orgCode").val().trim();

			// 企业账户信息
			var bankName = $("#bankName").children('option:selected').html().trim();
			var bankCode = $("#bankName").val().trim();
			var bankCardNo = $("#bankCardNo").val().trim();
			var l_address_province = $("#l_address_province").val().trim();
			var l_address_city = $("#l_address_city").val().trim();
			var l_address_county = $("#l_address_county").val().trim();
			var issuerName = $("#issuerName").val().trim();
			var bankPermitCertNo = $("#bankPermitCertNo").val().trim();
			var issuer = $("#issuer").val().trim();

			// 企业联系人信息
			var agentPersonName = $("#agentPersonName").val().trim();
			var agentPersonPhone = $("#agentPersonPhone").val().trim();
			var agentPersonCertType = $("#agentPersonCertType").val().trim();
			var agentPersonCertNo = $("#agentPersonCertNo").val().trim();
			var email = $("#email").val().trim();

			// 页面输入框校验
			if (creditUserType == "00") {
				$("#messageBox").show().html("请选择账户类型");
				return false;
			} else if (enterpriseFullName.trim() == "") {
				$("#messageBox").show().html("请填写企业全称");
				return false;
			} else if (registAddress.trim() == "") {
				$("#messageBox").show().html("请填写注册地址");
				return false;
			} else if (corporationName.trim() == "") {
				$("#messageBox").show().html("请填写法人姓名");
				return false;
			} else if (corporationCertType == "00") {
				$("#messageBox").show().html("请选择法人证件类型");
				return false;
			} else if (corporationCertNo.trim() == "") {
				$("#messageBox").show().html("请填写法人证件号");
				return false;
			} else if (businessLicense.trim() == "") {
				$("#messageBox").show().html("请填写证照号");
				return false;
			} else if (bankName == "请选择") { // 银行名称
				$("#messageBox").show().html("请选择银行名称");
				return false;
			} else if (bankCardNo == "") {
				$("#messageBox").show().html("请选择银行账号");
				return false;
			} else if (l_address_province == "省份") {
				$("#messageBox").show().html("请选择开户省");
				return false;
			} else if (l_address_city == "地级市") {
				$("#messageBox").show().html("请选择开户市");
				return false;
			} else if (l_address_county == "市、县级市") {
				$("#messageBox").show().html("请选择开户县(区)");
				return false;
			} else if (issuerName == "") {
				$("#messageBox").show().html("请填写支行名称");
				return false;
			} else if (bankPermitCertNo == "") {
				$("#messageBox").show().html("请填写核准号");
				return false;
			} else if (issuer == "") {
				$("#messageBox").show().html("请填写支行-联行号");
				return false;
			} else if (businessLicenseType == "BLC") { // 营业执照
				if (taxRegCertNo.trim() == "") {
					$("#messageBox").show().html("请填写税务登记证");
					return false;
				} else if (orgCode.trim() == "") {
					$("#messageBox").show().html("请填写组织机构代码");
					return false;
				}
			}

			// 企业联系人
			if (agentPersonName == "") {
				$("#messageBox").show().html("请填写联系人姓名");
				return false;
			} else if (agentPersonPhone == "") {
				$("#messageBox").show().html("请填写联系人手机号码");
				return false;
			} else if (agentPersonCertType == "00") {
				$("#messageBox").show().html("请选择联系人证件类型");
				return false;
			} else if (agentPersonCertNo == "") {
				$("#messageBox").show().html("请填写联系人证件号码");
				return false;
			} else if (email == "") {
				$("#messageBox").show().html("请填写联系人邮箱");
				return false;
			} else {
				var myReg = /^[a-zA-Z0-9_-]+@([a-zA-Z0-9]+\.)+(com|cn|net|org)$/;
				if (myReg.test(email)) {
				} else {
					$("#messageBox").show().html("请确认联系人邮箱格式");
					return false;
				}
			}

			// 银行卡号码
			if(!checkBankNo(bankCardNo)){
				$("#messageBox").show().html("请输入合法的银行账号");
				return false;
			}
			bankCardNo = bankCardNo.replace(/\s*/g, "");

			//企业联系人信息
			// 单独if判断，移动电话
			if(!validatorMobilePhone(agentPersonPhone)){
				$("#messageBox").show().html("请输入合法的手机号码");
				return false;
			}

			//单独if判断，身份证号
			var options=$("#agentPersonCertType option:selected"); 
			if(options.val()=="IDC" || options.val()=="PASS_PORT"){
				if(!checkCertificateNumber(agentPersonCertNo)){
					$("#messageBox").show().html("请输入合法的证件号码");
					return false;
				}
			}

			//单独if判断，银行开户许可证(核准号)
			if(!checkBankNumber(bankPermitCertNo)){
				$("#messageBox").show().html("请输入合法的核准号");
				return false;
			}

			//企业资料信息
			//单独if判断，身份证号和护照号
			var options=$("#corporationCertType option:selected"); 
			if(options.val()=="IDC" || options.val()=="PASS_PORT"){
				if(!checkCertNumber(corporationCertNo)){
					$("#messageBox").show().html("请输入合法的证件号码");
					return false;
				}
			}

			//单独if判断，统一社会信用代码
			var options=$("#businessLicenseType option:selected"); 
			if(options.val()=="USCC"){
				if(!checkLicenseNumber(businessLicense)){
					$("#messageBox").show().html("请输入合法的证件号码");
					return false;
				}
			}

			// 消息提示隐藏
			$("#messageBox").hide();

			// 输入框加锁
			// 企业资料信息
			$("#bizType").attr("disabled", "disabled"); // 账户类型
			$("#enterpriseFullName").attr("disabled", "disabled"); // 企业全称
			$("#corporationName").attr("disabled", "disabled"); // 法人姓名
			$("#registAddress").attr("disabled", "disabled"); // 注册地址
			$("#corporationCertType").attr("disabled", "disabled"); // 法人证件类型
			$("#corporationCertNo").attr("disabled", "disabled"); // 法人证件号
			$("#businessLicenseType").attr("disabled", "disabled"); // 证照类型
			$("#businessLicense").attr("disabled", "disabled"); // 证照号
			$("#taxRegCertNo").attr("disabled", "disabled"); // 税务登记证
			$("#orgCode").attr("disabled", "disabled"); // 组织机构代码
			// 企业账户信息
			$("#bankName").attr("disabled", "disabled"); // 银行名称
			$("#bankCardNo").attr("disabled", "disabled"); // 银行账号
			$("#l_address_province").attr("disabled", "disabled"); // 开户省
			$("#l_address_city").attr("disabled", "disabled"); // 开户市
			$("#l_address_county").attr("disabled", "disabled"); // 开户县(区)
			$("#issuerName").attr("disabled", "disabled"); // 支行名称
			$("#bankPermitCertNo").attr("disabled", "disabled"); // 核准号
			$("#issuer").attr("disabled", "disabled"); // 支行-联行号
			// 企业联系人信息
			$("#agentPersonName").attr("disabled", "disabled"); // 姓名
			$("#agentPersonPhone").attr("disabled", "disabled"); // 手机号码
			$("#agentPersonCertType").attr("disabled", "disabled"); // 证件类型
			$("#agentPersonCertNo").attr("disabled", "disabled"); // 证件号
			$("#email").attr("disabled", "disabled"); // 邮箱
			// 修改按钮展示
			$("#update_id").show();
			// 保存和提交按钮隐藏
			$("#save_id").hide();
			$("#insert_submit_id").hide();
			$("#update_submit_id").hide();

			// 借款人帐号信息
			var creditUserInfo = {};
			creditUserInfo.id = creditUserId;
			creditUserInfo.creditUserType = creditUserType;
			creditUserInfo.enterpriseFullName = enterpriseFullName;
			// 借款人融资主体
			var wloanSubject = {};
			wloanSubject.loanUser = corporationName;
			wloanSubject.registAddress = registAddress;
			wloanSubject.corporationCertType = corporationCertType;
			wloanSubject.corporationCertNo = corporationCertNo;
			wloanSubject.businessLicenseType = businessLicenseType;
			wloanSubject.businessNo = businessLicense;
			wloanSubject.taxCode = taxRegCertNo;
			wloanSubject.organNo = orgCode;
			wloanSubject.loanBankName = bankName;
			wloanSubject.loanBankCode = bankCode;
			wloanSubject.loanBankNo = bankCardNo;
			wloanSubject.loanBankProvince = l_address_province;
			wloanSubject.loanBankCity = l_address_city;
			wloanSubject.loanBankCounty = l_address_county;
			wloanSubject.loanIssuerName = issuerName;
			wloanSubject.bankPermitCertNo = bankPermitCertNo;
			wloanSubject.loanIssuer = issuer;
			wloanSubject.agentPersonName = agentPersonName;
			wloanSubject.agentPersonPhone = agentPersonPhone;
			wloanSubject.agentPersonCertType = agentPersonCertType;
			wloanSubject.agentPersonCertNo = agentPersonCertNo;
			wloanSubject.email = email;
			creditUserInfo.wloanSubject = wloanSubject;

			// 是否操作提交按钮.
			var r = false;
			if(type == "1"){
				r = confirm("确定要执行企业绑卡注册操作吗？")
				if (r) { // 确认执行操作.
					// 消息提示展示
					$(".mask_gray").show();
					$(".mask_tip").html("<b>【企业绑卡注册】正在跳转至存管行，请您耐心等待 ......</b>").addClass("fullTip");
					$(".mask_tip").show();
					$.ajax({
						url : "${ctxpath}/lanmaoAccount/enterpriseRegister",
						type : "post",
						dataType : "json",
						data : JSON.stringify(creditUserInfo), //将对象序列化成JSON字符串
						contentType : 'application/json;charset=utf-8', //设置请求头信息
						success : function(result) {
							// 成功的状态，跳转至银行存管页面进行开户流程
							if(result.state == "0") {
								var data = result.data;
								openPostWindow(cgbpath, data);
							} else if (result.state == "1") {
								message_prompt(result.message);
							} else if (result.state == "2") {
								message_prompt(result.message);
							}
						},
						error : function(data) {
							console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
						}
					});
				} else { // 取消操作.

					// 输入框解锁
					// 企业资料信息
					$("#bizType").removeAttr("disabled"); // 账户类型
					$("#enterpriseFullName").removeAttr("disabled"); // 企业全称
					$("#corporationName").removeAttr("disabled"); // 法人姓名
					$("#registAddress").removeAttr("disabled"); // 注册地址
					$("#corporationCertType").removeAttr("disabled"); // 法人证件类型
					$("#corporationCertNo").removeAttr("disabled"); // 法人证件号
					$("#businessLicenseType").removeAttr("disabled"); // 证照类型
					$("#businessLicense").removeAttr("disabled"); // 证照号
					$("#taxRegCertNo").removeAttr("disabled"); // 税务登记证
					$("#orgCode").removeAttr("disabled"); // 组织机构代码
					// 企业账户信息
					$("#bankName").removeAttr("disabled"); // 银行名称
					$("#bankCardNo").removeAttr("disabled"); // 银行账号
					$("#l_address_province").removeAttr("disabled"); // 开户省
					$("#l_address_city").removeAttr("disabled"); // 开户市
					$("#l_address_county").removeAttr("disabled"); // 开户县(区)
					$("#issuerName").removeAttr("disabled"); // 支行名称
					$("#bankPermitCertNo").removeAttr("disabled"); // 核准号
					$("#issuer").removeAttr("disabled"); // 支行-联行号
					// 企业联系人信息
					$("#agentPersonName").removeAttr("disabled"); // 姓名
					$("#agentPersonPhone").removeAttr("disabled"); // 手机号码
					$("#agentPersonCertType").removeAttr("disabled"); // 证件类型
					$("#agentPersonCertNo").removeAttr("disabled"); // 证件号
					$("#email").removeAttr("disabled"); // 邮箱 

					// 修改按钮展示
					$("#update_id").hide();
					// 保存和提交按钮隐藏
					$("#save_id").show();
					// 提交按钮逻辑判断
					if (state == "0") { // 开户申请中
						$("#insert_submit_id").hide();
						$("#update_submit_id").show();
					} else if (state == "1") { // 已开户 
						$("#insert_submit_id").hide();
						$("#update_submit_id").show();
					} else if (state == "2") { // 未开户 
						$("#insert_submit_id").show();
						$("#update_submit_id").hide();
					}
					return false;
				}
			} else if (type == "2"){
				r = confirm("确定要执行企业信息修改操作吗？")
				if (r) { // 确认执行操作.
					// 消息提示展示
					$(".mask_gray").show();
					$(".mask_tip").html("<b>【企业信息修改】正在跳转至存管行，请您耐心等待 ......</b>").addClass("fullTip");
					$(".mask_tip").show();
					$.ajax({
						url : "${ctxpath}/lanmaoAccount/enterpriseInformationUpdate",
						type : "post",
						dataType : "json",
						data : JSON.stringify(creditUserInfo), //将对象序列化成JSON字符串
						contentType : 'application/json;charset=utf-8', //设置请求头信息
						success : function(result) {
							// 成功的状态，跳转至银行存管页面
							if(result.state == "0") {
								var data = result.data;
								openPostWindow(cgbpath, data);
							} else if (result.state == "1") {
								message_prompt(result.message);
							} else if (result.state == "2") {
								message_prompt(result.message);
							}
						},
						error : function(data) {
							console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
						}
					});
				} else { // 取消操作.

					// 输入框解锁
					// 企业资料信息
					$("#bizType").removeAttr("disabled"); // 账户类型
					$("#enterpriseFullName").removeAttr("disabled"); // 企业全称
					$("#corporationName").removeAttr("disabled"); // 法人姓名
					$("#registAddress").removeAttr("disabled"); // 注册地址
					$("#corporationCertType").removeAttr("disabled"); // 法人证件类型
					$("#corporationCertNo").removeAttr("disabled"); // 法人证件号
					$("#businessLicenseType").removeAttr("disabled"); // 证照类型
					$("#businessLicense").removeAttr("disabled"); // 证照号
					$("#taxRegCertNo").removeAttr("disabled"); // 税务登记证
					$("#orgCode").removeAttr("disabled"); // 组织机构代码
					// 企业账户信息
					$("#bankName").removeAttr("disabled"); // 银行名称
					$("#bankCardNo").removeAttr("disabled"); // 银行账号
					$("#l_address_province").removeAttr("disabled"); // 开户省
					$("#l_address_city").removeAttr("disabled"); // 开户市
					$("#l_address_county").removeAttr("disabled"); // 开户县(区)
					$("#issuerName").removeAttr("disabled"); // 支行名称
					$("#bankPermitCertNo").removeAttr("disabled"); // 核准号
					$("#issuer").removeAttr("disabled"); // 支行-联行号
					// 企业联系人信息
					$("#agentPersonName").removeAttr("disabled"); // 姓名
					$("#agentPersonPhone").removeAttr("disabled"); // 手机号码
					$("#agentPersonCertType").removeAttr("disabled"); // 证件类型
					$("#agentPersonCertNo").removeAttr("disabled"); // 证件号
					$("#email").removeAttr("disabled"); // 邮箱 

					// 修改按钮展示
					$("#update_id").hide();
					// 保存和提交按钮隐藏
					$("#save_id").show();
					// 提交按钮逻辑判断
					if (state == "0") { // 开户申请中
						$("#insert_submit_id").hide();
						$("#update_submit_id").show();
					} else if (state == "1") { // 已开户 
						$("#insert_submit_id").hide();
						$("#update_submit_id").show();
					} else if (state == "2") { // 未开户 
						$("#insert_submit_id").show();
						$("#update_submit_id").hide();
					} else if (state == "3") { // 已开户，修改审核失败 
						$("#insert_submit_id").hide();
						$("#update_submit_id").show();
					}
					return false;
				}
			}
		}

		/**
		 * 企业信息保存（1.输入框加锁）
		 */
		function companyInfo_save() {
			// console.log("执行保存操作 ...")
			// 保存操作，持久化数据库
			// 企业资料信息
			var creditUserType = $("#bizType").val().trim();
			var enterpriseFullName = $("#enterpriseFullName").val().trim();
			var corporationName = $("#corporationName").val().trim();
			var registAddress = $("#registAddress").val().trim();
			var corporationCertType = $("#corporationCertType").val().trim();
			var corporationCertNo = $("#corporationCertNo").val().trim();
			var businessLicenseType = $("#businessLicenseType").val().trim();
			var businessLicense = $("#businessLicense").val().trim();
			var taxRegCertNo = $("#taxRegCertNo").val().trim();
			var orgCode = $("#orgCode").val().trim();

			// 企业账户信息
			var bankName = $("#bankName").children('option:selected').html().trim();
			var bankCode = $("#bankName").val().trim();
			var bankCardNo = $("#bankCardNo").val().trim();
			var l_address_province = $("#l_address_province").val().trim();
			var l_address_city = $("#l_address_city").val().trim();
			var l_address_county = $("#l_address_county").val().trim();
			var issuerName = $("#issuerName").val().trim();
			var bankPermitCertNo = $("#bankPermitCertNo").val().trim();
			var issuer = $("#issuer").val().trim();

			// 企业联系人信息
			var agentPersonName = $("#agentPersonName").val().trim();
			var agentPersonPhone = $("#agentPersonPhone").val().trim();
			var agentPersonCertType = $("#agentPersonCertType").val().trim();
			var agentPersonCertNo = $("#agentPersonCertNo").val().trim();
			var email = $("#email").val().trim();

			// 页面输入框校验
			if (creditUserType == "00") {
				$("#messageBox").show().html("请选择账户类型");
				return false;
			} else if (enterpriseFullName.trim() == "") {
				$("#messageBox").show().html("请填写企业全称");
				return false;
			} else if (registAddress.trim() == "") {
				$("#messageBox").show().html("请填写注册地址");
				return false;
			} else if (corporationName.trim() == "") {
				$("#messageBox").show().html("请填写法人姓名");
				return false;
			} else if (corporationCertType == "00") {
				$("#messageBox").show().html("请选择法人证件类型");
				return false;
			} else if (corporationCertNo.trim() == "") {
				$("#messageBox").show().html("请填写法人证件号");
				return false;
			} else if (businessLicense.trim() == "") {
				$("#messageBox").show().html("请填写证照号");
				return false;
			} else if (bankName == "请选择") { // 银行名称
				$("#messageBox").show().html("请选择银行名称");
				return false;
			} else if (bankCardNo == "") {
				$("#messageBox").show().html("请选择银行账号");
				return false;
			} else if (l_address_province == "省份") {
				$("#messageBox").show().html("请选择开户省");
				return false;
			} else if (l_address_city == "地级市") {
				$("#messageBox").show().html("请选择开户市");
				return false;
			} else if (l_address_county == "市、县级市") {
				$("#messageBox").show().html("请选择开户县(区)");
				return false;
			} else if (issuerName == "") {
				$("#messageBox").show().html("请填写支行名称");
				return false;
			} else if (bankPermitCertNo == "") {
				$("#messageBox").show().html("请填写核准号");
				return false;
			} else if (issuer == "") {
				$("#messageBox").show().html("请填写支行-联行号");
				return false;
			} else if (businessLicenseType == "BLC") { // 营业执照
				if (taxRegCertNo.trim() == "") {
					$("#messageBox").show().html("请填写税务登记证");
					return false;
				} else if (orgCode.trim() == "") {
					$("#messageBox").show().html("请填写组织机构代码");
					return false;
				}
			}

			// 企业联系人
			if (agentPersonName == "") {
				$("#messageBox").show().html("请填写联系人姓名");
				return false;
			} else if (agentPersonPhone == "") {
				$("#messageBox").show().html("请填写联系人手机号码");
				return false;
			} else if (agentPersonCertType == "00") {
				$("#messageBox").show().html("请选择联系人证件类型");
				return false;
			} else if (agentPersonCertNo == "") {
				$("#messageBox").show().html("请填写联系人证件号码");
				return false;
			} else if (email == "") {
				$("#messageBox").show().html("请填写联系人邮箱");
				return false;
			} else {
				var myReg = /^[a-zA-Z0-9_-]+@([a-zA-Z0-9]+\.)+(com|cn|net|org)$/;
				if (myReg.test(email)) {
				} else {
					$("#messageBox").show().html("请确认联系人邮箱格式");
					return false;
				}
			}

			// 银行卡号码
			if(!checkBankNo(bankCardNo)){
				$("#messageBox").show().html("请输入合法的银行账号");
				return false;
			}
			bankCardNo = bankCardNo.replace(/\s*/g, "");

			//企业联系人信息
			// 单独if判断，移动电话
			if(!validatorMobilePhone(agentPersonPhone)){
				$("#messageBox").show().html("请输入合法的手机号码");
				return false;
			}

			//单独if判断，身份证号
			var options=$("#agentPersonCertType option:selected"); 
			if(options.val()=="IDC" || options.val()=="PASS_PORT"){
				if(!checkCertificateNumber(agentPersonCertNo)){
					$("#messageBox").show().html("请输入合法的证件号码");
					return false;
				}
			}

			//单独if判断，银行开户许可证(核准号)
			if(!checkBankNumber(bankPermitCertNo)){
				$("#messageBox").show().html("请输入合法的核准号");
				return false;
			}

			//企业资料信息
			//单独if判断，身份证号和护照号
			var options=$("#corporationCertType option:selected"); 
			if(options.val()=="IDC" || options.val()=="PASS_PORT"){
				if(!checkCertNumber(corporationCertNo)){
					$("#messageBox").show().html("请输入合法的证件号码");
					return false;
				}
			}

			//单独if判断，统一社会信用代码
			var options=$("#businessLicenseType option:selected"); 
			if(options.val()=="USCC"){
				if(!checkLicenseNumber(businessLicense)){
					$("#messageBox").show().html("请输入合法的证件号码");
					return false;
				}
			}

			// 消息提示隐藏
			$("#messageBox").hide();

			// 输入框加锁
			// 企业资料信息
			$("#bizType").attr("disabled", "disabled"); // 账户类型
			$("#enterpriseFullName").attr("disabled", "disabled"); // 企业全称
			$("#corporationName").attr("disabled", "disabled"); // 法人姓名
			$("#registAddress").attr("disabled", "disabled"); // 注册地址
			$("#corporationCertType").attr("disabled", "disabled"); // 法人证件类型
			$("#corporationCertNo").attr("disabled", "disabled"); // 法人证件号
			$("#businessLicenseType").attr("disabled", "disabled"); // 证照类型
			$("#businessLicense").attr("disabled", "disabled"); // 证照号
			$("#taxRegCertNo").attr("disabled", "disabled"); // 税务登记证
			$("#orgCode").attr("disabled", "disabled"); // 组织机构代码
			// 企业账户信息
			$("#bankName").attr("disabled", "disabled"); // 银行名称
			$("#bankCardNo").attr("disabled", "disabled"); // 银行账号
			$("#l_address_province").attr("disabled", "disabled"); // 开户省
			$("#l_address_city").attr("disabled", "disabled"); // 开户市
			$("#l_address_county").attr("disabled", "disabled"); // 开户县(区)
			$("#issuerName").attr("disabled", "disabled"); // 支行名称
			$("#bankPermitCertNo").attr("disabled", "disabled"); // 核准号
			$("#issuer").attr("disabled", "disabled"); // 支行-联行号
			// 企业联系人信息
			$("#agentPersonName").attr("disabled", "disabled"); // 姓名
			$("#agentPersonPhone").attr("disabled", "disabled"); // 手机号码
			$("#agentPersonCertType").attr("disabled", "disabled"); // 证件类型
			$("#agentPersonCertNo").attr("disabled", "disabled"); // 证件号
			$("#email").attr("disabled", "disabled"); // 邮箱
			
			if(openAccountState == "0"){// 未开户
				// 修改按钮展示
				$("#update_id").show();
				// 保存按钮隐藏
				$("#save_id").hide();
				// 企业绑卡注册按钮展示
				$("#insert_submit_id").show();
				// 企业信息修改按钮隐藏
				$("#update_submit_id").hide();
			} else if(openAccountState == "1"){ // 已开户
				// 修改按钮展示
				$("#update_id").show();
				// 保存按钮隐藏
				$("#save_id").hide();
				// 企业绑卡注册按钮隐藏
				$("#insert_submit_id").hide();
				// 企业信息修改按钮展示
				$("#update_submit_id").show();
			} else if(openAccountState == "2"){ // 已开户
				// 修改按钮隐藏
				$("#update_id").hide();
				// 保存按钮隐藏
				$("#save_id").hide();
				// 企业绑卡注册按钮隐藏
				$("#insert_submit_id").hide();
				// 企业信息修改按钮隐藏
				$("#update_submit_id").hide();
			}

			// 是否操作提交按钮.
			var r = false;
			r = confirm("确定要执行保存操作吗？")
			if (r) { // 确认执行操作.
				// 消息提示展示
				// $(".mask_gray").show();
				// $(".mask_tip").html("<b>企业用户信息保存至数据库</b>");
				// $(".mask_tip").show();
				$.ajax({
					url : "${ctx}/credit/userinfo/creditUserInfo/companyInfoSave",
					type : "post",
					dataType : "json",
					data : {
						id : creditUserId, // 借款人帐号信息主键.
						creditUserType : creditUserType,
						enterpriseFullName : enterpriseFullName,
						loanUser : corporationName,
						registAddress : registAddress,
						corporationCertType : corporationCertType,
						corporationCertNo : corporationCertNo,
						businessLicenseType : businessLicenseType,
						businessNo : businessLicense,
						taxCode : taxRegCertNo,
						organNo : orgCode,
						loanBankName : bankName, //银行名称
						loanBankCode : bankCode, //银行主键ID
						loanBankNo : bankCardNo,
						loanBankProvince : l_address_province,
						loanBankCity : l_address_city,
						loanBankCounty : l_address_county,
						loanIssuerName : issuerName,
						bankPermitCertNo : bankPermitCertNo,
						loanIssuer : issuer,
						agentPersonName : agentPersonName,
						agentPersonPhone : agentPersonPhone,
						agentPersonCertType : agentPersonCertType,
						agentPersonCertNo : agentPersonCertNo,
						email : email
					},
					success : function(data) {
						// 消息提示隐藏
						// $(".mask_gray").hide();
						// $(".mask_tip").hide();
						// 接口消息提示
						// data.message
						message_prompt_();
						// top.location.reload();
						// 刷新父亲对象（用于框架）
						// parent.location.reload();
					},
					error : function(data) {
						console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
					}
				});
			} else { // 取消操作.

				// 输入框解锁
				// 企业资料信息
				$("#bizType").removeAttr("disabled"); // 账户类型
				$("#enterpriseFullName").removeAttr("disabled"); // 企业全称
				$("#corporationName").removeAttr("disabled"); // 法人姓名
				$("#registAddress").removeAttr("disabled"); // 注册地址
				$("#corporationCertType").removeAttr("disabled"); // 法人证件类型
				$("#corporationCertNo").removeAttr("disabled"); // 法人证件号
				$("#businessLicenseType").removeAttr("disabled"); // 证照类型
				$("#businessLicense").removeAttr("disabled"); // 证照号
				$("#taxRegCertNo").removeAttr("disabled"); // 税务登记证
				$("#orgCode").removeAttr("disabled"); // 组织机构代码
				// 企业账户信息
				$("#bankName").removeAttr("disabled"); // 银行名称
				$("#bankCardNo").removeAttr("disabled"); // 银行账号
				$("#l_address_province").removeAttr("disabled"); // 开户省
				$("#l_address_city").removeAttr("disabled"); // 开户市
				$("#l_address_county").removeAttr("disabled"); // 开户县(区)
				$("#issuerName").removeAttr("disabled"); // 支行名称
				$("#bankPermitCertNo").removeAttr("disabled"); // 核准号
				$("#issuer").removeAttr("disabled"); // 支行-联行号
				// 企业联系人信息
				$("#agentPersonName").removeAttr("disabled"); // 姓名
				$("#agentPersonPhone").removeAttr("disabled"); // 手机号码
				$("#agentPersonCertType").removeAttr("disabled"); // 证件类型
				$("#agentPersonCertNo").removeAttr("disabled"); // 证件号
				$("#email").removeAttr("disabled"); // 邮箱 

				// 修改按钮展示
				$("#update_id").hide();
				// 保存和提交按钮隐藏
				$("#save_id").show();
				// 提交按钮逻辑判断
				if (state == "0") { // 开户申请中
					$("#insert_submit_id").hide();
					$("#update_submit_id").show();
					// $("#update_submit_id").hide();
				} else if (state == "1") { // 已开户 
					$("#insert_submit_id").hide();
					$("#update_submit_id").show();
					// $("#update_submit_id").hide();
				} else if (state == "2") { // 未开户 
					$("#insert_submit_id").show();
					// $("#insert_submit_id").hide();
					$("#update_submit_id").hide();
				} else if (state == "3") { // 已开户，修改审核失败 
					$("#insert_submit_id").hide();
					$("#update_submit_id").show();
					// $("#update_submit_id").hide();
				}
				return false;
			}

		}

		// 双击操作空处理
		function ondblclick_fn() {
			console.log("双击了按钮 .... ");
		}

		// 消息提示 .
		function message_prompt(message) {
			$(".mask_gray").show();
			$(".mask_tip").html(message);
			$(".mask_tip").show();
			setTimeout(function() {
				$(".mask_gray").hide();
				$(".mask_tip").hide();
			}, 2000);
		} // --

		// 消息提示 .
		function message_prompt_() {
			$(".mask_gray").show();
			$(".mask_tip_popup_window").show();
			setTimeout(function() {
				$(".mask_gray").hide();
				$(".mask_tip_popup_window").hide();
			}, 3000);
		} // --

		//企业联系人信息
		// 校验手机号码
		function validatorMobilePhone(value){
			var length = value.length;
			if(length == 11 && /^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/.test(value)){
				$("#messageBox").hide();
				return true;
			} else {
				$("#messageBox").show().html("手机号输入错误");
				return false;
			}
		}
		// 校验身份证号
		function checkCertificateNumber(value){
			var options = $("#agentPersonCertType option:selected"); 
			if(options.val()=="IDC"){
				var length = value.length;
				if(length == 18 && /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(value)){
					$("#messageBox").hide();
					return true;
				} else {
					$("#messageBox").show().html("身份证号码填写有误");
					return false;
				} 
			} else if(options.val()=="PASS_PORT"){
				var length = value.length;
				if(/^[0-9a-zA-Z]{18}/.test(value)){
					$("#messageBox").hide();
					return true;
				} else {
					$("#messageBox").show().html("护照号填写有误");
					return false;
				} 
			} else{
				$("#messageBox").hide();
			}
		}

		//验证银行卡账号
		function checkBankNo(value) {
			var length = value.length;
			var pattern = /^[0-9]+$/;
			str = value.replace(/\s*/g, "");
			if (length < 32 && pattern.test(str)) {
				$("#messageBox").hide();
				return true;
			} else {
				$("#messageBox").show().html("请输入合法的银行账号");
				return false;
			}
		}

		//验证银行卡开户许可证(核准号)
		function checkBankNumber(value) {
			var length = value.length;
			if (length == 14 && /^[J]{1}[0-9]{13}$/.test(value)) {
				$("#messageBox").hide();
				return true;
			} else {
				$("#messageBox").show().html("核准号填写有误");
				return false;
			}
		}

		//企业资料信息
		// 校验身份证号
		function checkCertNumber(value) {
			var options = $("#corporationCertType option:selected");
			if (options.val() == "IDC") {
				var length = value.length;
				if (length <= 18 && /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(value)) {
					$("#messageBox").hide();
					return true;
				} else {
					$("#messageBox").show().html("身份证号码填写有误");
					return false;
				}
			} else if (options.val() == "PASS_PORT") {//检验护照号
				var regEn = /[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im, regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;
				if (regEn.test(value) || regCn.test(value)) {
					$("#messageBox").show().html("护照号填写有误");
					return false;
				}
				var length = value.length;
				if (length <= 18 && /^[0-9a-zA-Z]/.test(value)) {
					$("#messageBox").hide();
					return true;
				} else {
					$("#messageBox").show().html("护照号填写有误");
					return false;
				}
			} else {
				$("#messageBox").hide();
			}
		}

		//检验统一社会信用代码
		function checkLicenseNumber(value) {
			var options = $("#businessLicenseType option:selected");
			if (options.val() == "USCC") {
				var length = value.length;
				if (length <= 18 && /^[0-9a-zA-Z]{18}/.test(value)) {
					$("#messageBox").hide();
					return true;
				} else {
					$("#messageBox").show().html("统一社会信用码输入有误");
					return false;
				}
			} else {
				$("#messageBox").hide();
			}
		}
	</script>
<style type="text/css">
	table.table td {
		padding: 10px 5px !important;
		vertical-align: middle !important;
	}
	
	table.table td input {
		height: 30px;
		width: 200px;
	}
	
	table.table td select {
		width: 200px;
	}
	
	.alert {
		padding: 0 !important
	}
	
	table {
		border-collapse: collapse;
		border: none
	}
	
	.table-bordered>thead>tr>th, .table-bordered>thead>tr>td {
		border-bottom-width: 0;
	}
	
	.loan_apply {
		padding: 0
	}
	
	.loan_apply_wrap {
		padding-top: 0
	}
	
	.info_basic_wrap dl dd {
		padding-left: 0
	}
	
	table.table th {
		padding: 10px 3px !important;
		vertical-align: middle !important;
		font-weight: normal;
	}
	
	.table-condensed th input {
		border: 0;
		background: none;
		color: #36a7e7;
	}
	
	.loan_apply .nav_head {
		padding-top: 15px
	}
	
	.mask_li {
		overflow: hidden;
		margin-top: 30px
	}
	
	.mask_li li {
		display: inline-block;
		width: 100px;
		height: 40px;
		border-radius: 5px;
		background: #40a2fb;
		text-align: center;
		line-height: 40px;
		margin: 0 20px;
		color: #fff;
		font-size: 14px;
		cursor: pointer;
	}
	.spanButton{
		float:right;
	}
	.spanButton span{
		width: 100px;
		height: 30px;
		background: #40a2fb;
		color: #fff;
		line-height: 30px;
		font-size: 14px;
		cursor: pointer;
		text-align: center;
		border-radius: 5px;
		margin-top: 15px;
		margin-left: 20px;
		float:right;
	}
</style>
</head>
<body>
<div class="loan_apply_wrap_02">
	<div class="nav_head"><b>开户信息</b></div>	
			<div>
			<dd class="even"><b class="pull-left nav_head">企业资料信息</b></dd>
			<c:if test="${creUserInfo.openAccountState == '2'}">
				<dd class="even">
					<span class="pull-left word_lh"><b style="color: red;">注意：</b><b>企业绑卡注册，审核中！您的开户信息已提交至存管银行，存管银行运营人员会在1~2个工作日内进行审核，给您带来的不便，敬请谅解！</b></span>
				</dd>
			</c:if>
			<c:if test="${creUserInfo.openAccountState == '3'}">
				<dd class="even">
					<span class="pull-left word_lh"><b style="color: red;">注意：</b><b>企业绑卡注册，审核回退！请您联系平台风控人员或者客户人员，核实审核回退的原因，风控专员会在商户后台修改您的企业信息，给您带来的不便，敬请谅解！</b></span>
				</dd>
			</c:if>
			<c:if test="${creUserInfo.openAccountState == '4'}">
				<dd class="even">
					<span class="pull-left word_lh"><b style="color: red;">注意：</b><b>企业绑卡注册，审核拒绝！请您联系平台风控人员或者客户人员，核实审核拒绝的原因，以便再次进行绑卡注册后进行借款操作，给您带来的不便，敬请谅解！</b></span>
				</dd>
			</c:if>
			<table class="table table-striped table-bordered table-condensed" >
				<input type="text" class="error_msg" style="display: none">
				<thead>
					<tr>
						<!-- 企业绑卡注册 -->
						<th>企业绑卡注册</th>
						<c:if test="${creUserInfo.openAccountState == '0'}">
							<th><b>未开户</b></th>
						</c:if>
						<c:if test="${creUserInfo.openAccountState == '1'}">
							<th><b>已开户</b></th>
						</c:if>
						<c:if test="${creUserInfo.openAccountState == '2'}">
							<th><b>审核中</b></th>
						</c:if>
						<c:if test="${creUserInfo.openAccountState == '3'}">
							<th><b>审核回退</b></th>
						</c:if>
						<c:if test="${creUserInfo.openAccountState == '4'}">
							<th><b>审核拒绝</b></th>
						</c:if>
						<!-- 企业信息修改 -->
						<%-- <th>企业信息修改</th>
						<c:if test="${creUserInfo.openAccountState == '0'}">
							<th><b>未开户</b></th>
						</c:if>
						<c:if test="${creUserInfo.openAccountState == '1'}">
							<th><b>已开户</b></th>
						</c:if>
						<c:if test="${creUserInfo.openAccountState == '2'}">
							<th><b>审核中</b></th>
						</c:if>
						<c:if test="${creUserInfo.openAccountState == '3'}">
							<th><b>审核回退</b></th>
						</c:if>
						<c:if test="${creUserInfo.openAccountState == '4 '}">
							<th><b>审核拒绝</b></th>
						</c:if> --%>
						<!-- 企业信息修改 -->
						<th>企业信息修改</th>
						<c:if test="${creUserInfo.autoState == '0'}">
							<th><b>未变更</b></th>
						</c:if>
						<c:if test="${empty creUserInfo.autoState}">
							<th><b>未变更</b></th>
						</c:if>
						<c:if test="${creUserInfo.autoState == 'AUDIT'}">
							<th><b>审核中</b></th>
						</c:if>
						<c:if test="${creUserInfo.autoState == 'PASSED'}">
							<th><b>审核通过</b></th>
						</c:if>
						<c:if test="${creUserInfo.autoState == 'REFUSED'}">
							<th><b>审核拒绝</b></th>
						</c:if>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>账户类型</td>
						<td>
							<select name="bizType" id="bizType" style="width: 200px;">
								<option value="00">请选择</option>
								<option value="02">供应商（借款户）</option>
								<option value="05">平台户（中投摩根）</option>
								<option value="11">核心企业（代偿户）</option>
								<option value="15">抵押业务（借款户）</option>
							</select>
						</td>
						<td>企业全称</td>
						<td>
							<input type="text" style="width: 300px;" id="enterpriseFullName" name="" value="${userBankCard.creditUserInfo.wloanSubject.companyName }" >
						</td>
					</tr>
					<tr>
						<td>法人姓名</td>
						<td>
							<input type="text" style="width: 200px;" name="" value="${userBankCard.creditUserInfo.wloanSubject.loanUser }" id="corporationName">
						</td>
						<td>注册地址</td>
						<td>
							<input style="width: 300px;" type="text" name="" value="${userBankCard.creditUserInfo.wloanSubject.registAddress}"  id="registAddress"/>
						</td>
					</tr>
					<tr>
						<td>法人证件类型</td>
						<td>
							<select style="width: 200px;" name="" id="corporationCertType">
								<option value="00">请选择</option>
								<option value="IDC">身份证</option>
								<option value="PASS_PORT">护照</option>
								<option value="GAT">港澳台通行证</option>
								<option value="PERMANENT_RESIDENCE">外国人永久居留证</option>
							</select>
						</td>
						<td>法人证件号</td>
						<%-- <td>
							<input type="text" style="width: 300px;" onkeyup="value=value.replace(/[^\d | a-zA-Z]/g,'')" name="" value="${userBankCard.creditUserInfo.wloanSubject.corporationCertNo}"  id="corporationCertNo">
						</td> --%>
						<td>
							<input type="text" style="width: 300px;" onkeyup="checkCertNumber(this.value)" name="" value="${userBankCard.creditUserInfo.wloanSubject.corporationCertNo}"  id="corporationCertNo">
						</td>
					</tr>
					<tr>
						<td>证照类型</td>
						<td>
							<select style="width: 200px;" name="" id="businessLicenseType">
								<option value="USCC">USCC-统一社会信用代码</option>
								<option value="BLC">BLC-营业执照</option>
							</select>
						</td>
						<td>证照号</td>
						<td>
							<input type="text" style="width: 300px;" onkeyup="checkLicenseNumber(this.value)" name="" value="${userBankCard.creditUserInfo.wloanSubject.businessNo}"  id="businessLicense">
						</td>
					</tr>
					<tr>
						<td id="taxRegCertNoA">税务登记证</td>
						<td>
							<input type="text" style="width: 200px;" onkeyup="value=value.replace(/[^\d | a-zA-Z]/g,'')" id="taxRegCertNo" name="" value="${userBankCard.creditUserInfo.wloanSubject.taxCode}" >
						</td>
						<td id="orgCodeA">组织机构代码</td>
						<td>
							<input type="text" style="width: 300px;" onkeyup="value=value.replace(/[^\d | a-zA-Z]/g,'')" id="orgCode" name="" value="${userBankCard.creditUserInfo.wloanSubject.organNo}" >
						</td>
					</tr>
				</tbody>
			</table>

			<dd class="even"><b class="pull-left nav_head">企业账户信息</b></dd>

			<table class="table table-striped table-bordered table-condensed" >
				<input type="text" class="error_msg" style="display: none">
				<thead>
				</thead>
				<tbody>
					<tr>
						<td>银行名称</td>
						<td>
							<select style="width: 388px;" id="bankName" >
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
								<option value="b2111599c1034615b6068d0da03a4171">乐陵市农村合作信用社</option>
								<!-- end -->
								<!-- <option value="b7f3cf26bb2e4fe99bc6237f529968a3">海口联合农商银行</option>
								<option value="da61c6a63a6d47fd8ba12341ed1a504e">广发银行</option>
								<option value="1110a94f482d468a8afd98206c7256dc">恒丰银行</option>
								<option value="bfa989dd3ec74440bc40f162d8b86be2">招商银行</option>
								<option value="51cebebdffd2474580cda99c9e4fe7f7">中国邮政储蓄银行</option>
								<option value="500d05388e574c07871f7b4a33be35de">中国银行</option>
								<option value="a0f037c2ed724b0e96124e6eb4ab7ce0">中信银行</option>
								<option value="46f96e8017b54c28b4471c371838a30d">重庆银行</option>
								<option value="ce6d594fcb444483ad6467cf6120cb7a">中国工商银行</option>
								<option value="3a5a96b180e94ee5adf91d23f60ff93f">中国光大银行</option>
								<option value="56bbd6ee09cc4f7e96500dfd739ef8ff">中国建设银行</option>
								<option value="60eed507ab6a4ff486fb41bac3709f68">中国农业银行</option>
								<option value="3248ad90eb174cb8a85387827f6c7c92">平安银行</option>
								<option value="b5a9991ae6f6435cbf5d68b3fa886016">上海浦东发展银行</option>
								<option value="d12a7cbcbe6f448f803486a38e7796d0">交通银行</option>
								<option value="d49984bceba044f6bf4139c9167b7c2c">晋城银行</option>
								<option value="f90bb76376d9442684f5be6a7f8df1fc">晋商银行</option>
								<option value="70fa27e485d0436d92e90340481528c2">晋中银行</option>
								<option value="53dd939f0bef4471b0c267e5f3d8afd7">中国民生银行</option>
								<option value="5bbbba91fce84b248dc3d7953b6ec312">华夏银行</option>
								<option value="a3cdc09508044c9c87c807b7e2042595">北京银行</option>
								<option value="ca0bd61d088e4cc5aa0848609c1d05d2">渤海银行</option>
								<option value="abf6997bbb9a45f6ac03bec04dd91dc3">兴业银行</option>
								<option value="5ff187519cb5451aa383abb9606fd4ed">太原市城区农村信用合作联社</option>
								<option value="d0c8c59ccf3b4c3f8bb810d32a7ed2d6">北京农村商业银行</option>
								<option value="e9819ef456e549cba0c48de7a8a20bca">遵化市农村信用合作社联合社</option>
								<option value="6cad11ec477d4ff6b156ce4d6735016a">江西省奉新县农村信用合作联社营业部</option>
								<option value="7c329dbf96964f06acef4300456af00c">广东华兴银行</option>
								<option value="08fbb72c2b994cb5b4c68aa3ddaed94a">来安农村商业银行</option>
								<option value="b84a5ff0d9254beb90a2dd5dc5a9d949">西安银行</option>
								<option value="f7321aadc7da4c7a9d8424363ee92d50">贵州省遵义市习水县农村信用合作联社</option>
								<option value="36493c9a7e284413839f4a3743969dd7">江西芦溪农村商业银行股份有限公司</option>
								<option value="c80e2c438f0c40c2b8e83c77289503c9">新疆天山农村商业银行</option>
								<option value="13c73be4ae5a4f26ab1854947cb69a1d">上海银行</option>
								<option value="0bf4d6df56d344c5923b88a934ae456d">广州银行</option>
								<option value="baf65474b93d48a089253ac28b56ca7f">东营市商业银行</option>
								<option value="69f1b68df6d943be8575f3bab8381466">汕头市潮阳农村信用合作联社成田信用社</option>
								<option value="25b906a7b1284f74a9a8821d85f50e86">东莞农村商业银行股份有限公司</option>
								<option value="a1fe69d573d4482e9ece8d3ffdefc2a6">南昌农村商业银行股份有限公司</option>
								<option value="4f96b04518f94cd4813fbc6df8a4b6f8">中山农村商业银行股份有限公司</option>
								<option value="a5ed163dc7a248e9be165fcabfa9d137">成都银行</option> -->
							</select>
						</td>
					</tr>
					<tr>
						<td>银行账号</td>
						<td>
							<input style="width: 388px;" onkeyup="checkBankNo(this.value)" type="text" name="" value="${userBankCard.creditUserInfo.wloanSubject.loanBankNo}"  id="bankCardNo">
						</td> 
					</tr>
					<tr>
						<td>开户省</td>
						<td>
							<div>
								<select id="l_address_province"></select>
							</div>
						</td>
					</tr>
					<tr>
						<td>开户市</td>
						<td>
							<div>
								<select id="l_address_city"></select>
							</div>
						</td>
					</tr>
					<tr>
						<td>开户县(区)</td>
						<td>
							<div>
								<select id="l_address_county"></select>
							</div>
						</td>
					</tr>
					<tr>
						<td>支行名称</td>
						<td>
							<input type="text" style="width: 388px;" name="" value="${wloanSubject.loanIssuerName}" id="issuerName">
						</td>
					</tr>
					<tr>
						<td>银行开户许可证<b>（核准号）</b></td>
						<td>
							<input type="text" style="width: 388px;" onkeyup="checkBankNumber(this.value)" name="" id="bankPermitCertNo" value="${userBankCard.creditUserInfo.wloanSubject.bankPermitCertNo}">
						</td>
					</tr>
					<tr>
						<td>支行-联行号</td>
						<td>
							<input type="text" style="width: 388px;" onkeyup="value=value.replace(/[^\d | a-zA-Z]/g,'')" name="" value="${wloanSubject.loanIssuer}"  id="issuer">
						</td>
					</tr>
				</tbody>
			</table>

			<dd class="even"><b class="pull-left nav_head">企业联系人信息</b></dd>
			<table class="table table-striped table-bordered table-condensed" >
				<input type="text" class="error_msg" style="display: none">
				<thead>
					<tbody>
						<tr>
							<td>姓名</td>
							<td>
								<input style="width: 200px;" type="text" name="" id="agentPersonName" value="${userBankCard.creditUserInfo.wloanSubject.agentPersonName}">
							</td>
							<td>手机号码</td>
							<td>
								<input style="width: 300px;" maxlength="11" onkeyup="validatorMobilePhone(this.value);" type="text" name="" value="${userBankCard.creditUserInfo.wloanSubject.agentPersonPhone }" id="agentPersonPhone">
							</td>
						</tr>
						<tr>
							<td>证件类型</td>
							<td>
								<select style="width: 200px;" name="" id="agentPersonCertType">
									<option value="00">请选择</option>
									<option value="IDC">身份证</option>
									<option value="PASS_PORT">护照</option>
									<option value="GAT">港澳台通行证</option>
									<option value="PERMANENT_RESIDENCE">外国人永久居留证</option>
								</select>
							</td>
							<td>证件号</td>
							<td>
								<input style="width: 300px;" onkeyup="checkCertificateNumber(this.value)" type="text" name="" value="${userBankCard.creditUserInfo.wloanSubject.agentPersonCertNo }"  id="agentPersonCertNo">
							</td>
						</tr>
						<tr>
							<td>邮箱</td>
							<td>
								<input style="width: 200px;" type="text" name="" value="${userBankCard.creditUserInfo.wloanSubject.email}"  id="email"/>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
	<div class="loan_apply">
		<!-- <h1>附加信息<span>创建时间:2018-03-12</span></h1> -->
		<div class="loan_apply_wrap">
 			<div class="nav_head"><b>附加信息</b></div>	
			<div class="la_con">
				<div class="la_step la_step_four cur">
					<div class="info_basic_wrap">
						<dl class="font_size18">

							<dd class="even"><b class="pull-left">1.营业执照</b> <span class="pull-left">上传带有公章的营业执照，支持上传图片格式</span><span class="pull-right"><a href="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image-link" data-lightbox="example-10"><img src="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>

							<div class="imgfile_wrap">
								<div class="flie_wrap" id="flie_wrap_1">

									<div class="div_imgfile" id="div_imgfile_1"></div>

									<input type="file" accept="image/png,image/jpg" name="file" id="8" multiple="multiple" class="file">

								</div>
								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_8">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '8'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-8" href="${staticPath}/upload/image/${creditAnnexFile.url}"><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
										</c:if>
									</c:forEach>
								</div>
								<!--确定上传按钮-->
								<!--<input type="button" value="确定上传" id="btn_ImgUpStart_1" class="fr submit_btn" />-->
								<div class="info_error_msg" id="info_error_msg_1">图片大小不符</div>
							</div>
							<dd class="even">
								<b class="pull-left">2.银行开户许可证</b>
								<span class="pull-left word_lh">上传带有公章的银行开户许可证（一张），支持上传图片格式，<b>注：银行账户为一般户时，请在开户许可证复印件上填写绑定一般户的信息，包括但不限于：户名、账号、开户行、联行号，方便存管行核对信息</b></span>
								<span class="pull-right"><a href="${ctxStatic}/images/openPermit.jpg" class="example-image-link" data-lightbox="example-20"><img src="${ctxStatic}/images/openPermit.jpg" class="example-image" style="display:none"></a>查看实例</span>
							</dd>
							<div class="imgfile_wrap">
								<div class="flie_wrap" id="flie_wrap_2">
									<div class="div_imgfile"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="9" class="file" />
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_9">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '9'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-9" href="${staticPath}/upload/image/${creditAnnexFile.url}"><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
										</c:if>
									</c:forEach>
								</div>
																<!--kk-->
								<div class="div_pic_wrap">
									<img src="images/photo/02.jpg"/>
								</div>
								<!--确定上传按钮-->
								<!--<input type="button" value="确定上传" id="btn_ImgUpStart" class="fr submit_btn" />-->
								<div class="info_error_msg">图片大小不符</div>
							</div>
							<dd class="even"><b class="pull-left">3.法人身份证</b><span class="pull-left">上传带有公章的正反两面，支持上传图片格式</span><span class="pull-right"><a href="${ctxStatic}/images/shenfenzheng.jpg" class="example-image-link" data-lightbox="example-30"><img src="${ctxStatic}/images/shenfenzheng.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>
							<div class="imgfile_wrap">
								<div class="flie_wrap" id="flie_wrap_3">
									<div class="div_imgfile" id="div_imgfile_2"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="10" class="file">
								</div>
								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_10">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '10'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-10" href="${staticPath}/upload/image/${creditAnnexFile.url}"><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
										</c:if>
									</c:forEach>
								</div>
								<!--确定上传按钮-->
								<!--<input type="button" value="确定上传" id="btn_ImgUpStart_2" class="fr submit_btn" />-->
								<div class="info_error_msg">图片大小不符</div>
							</div>

						</dl>

					</div>

					<div class="error_msg">请选择</div>

					<!-- <button class="btn clear">保存</button> -->

				</div>

			</div>
		</div>
	</div>
	<dd class="even" id="keep_one_eyes_open_id">
		<span class="pull-left word_lh"><b style="color: red;">注意：</b>点击<b style="color: red;">存管绑卡注册</b>/<b style="color: red;">存管信息修改</b>之后会跳转至存管银行页面进行<b style="color: red;">银行交易密码</b>及相关信息的设置，设置完毕才算提交信息成功，<b style="color: red;">请耐心等待页面跳转。</b></span>
	</dd>
	<div id="messageBox" class="alert alert-success " style="display: none;">缺少必要参数</div>
	<div class="spanButton">
		<span id="update_id" onclick="companyInfo_update();" ondblclick="ondblclick_fn();">修改</span>
		<span id="save_id" onclick="companyInfo_save();" ondblclick="ondblclick_fn();" style="display: none;" >保存</span>
		<!-- 企业信息开户提交操作 -->
		<span id="insert_submit_id" onclick="companyInfo_submit(1);" ondblclick="ondblclick_fn();" style="display: none;">存管绑卡注册</span>
		<!-- 企业信息修改提交操作 -->
		<span id="update_submit_id" onclick="companyInfo_submit(2);" ondblclick="ondblclick_fn();" style="display: none;">存管信息修改</span>
	</div>
</div>
<div class="mask_gray"></div>
<div class="mask_tip">正在跳转银行,请您耐心等待...</div>
<div class="mask_tip_popup_window">
	<span onclick="popup_window_cancel();" class="close_tip">X</span>
	<p style="color: green;">温馨提示</p>
	<p><b>系统保存成功，可以进行`存管绑卡注册`/`存管信息修改`相关业务......</b></p>
	<div class="mask_li">
	<ul>
		<!-- <li onclick="popup_window_cancel();">取消</li> -->
		<!-- <li onclick="popup_window_perfect();">立即完善</li> -->
	</ul>
	</div>
</div>
<!-- 三级联动初始化. -->
<script type="text/javascript">

// 三级联动初始化.
_init_area();

</script>
</body>
</html>