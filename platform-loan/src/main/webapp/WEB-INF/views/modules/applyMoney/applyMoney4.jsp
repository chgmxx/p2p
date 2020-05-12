<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0);
	response.flushBuffer();
%>
<html>
<head>
<title>用户信息管理管理</title>
<meta name="decorator" content="default" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/reset.css" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/lightbox.css" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/apply.css" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/imgTab.css" />
<script type="text/javascript" src="${ctxStatic}/js/jquery.js"></script>
<script type="text/javascript" src="${ctxStatic}/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctxStatic}/js/jquery.jerichotab.js"></script>
<script src="${ctxStatic}/js/jquery.cookie.js" type="text/javascript"></script>
<script src="${ctxStatic}/js/lightbox.js" type="text/javascript"></script>
<script src="${ctxStatic}/js/CheckUtils.js" type="text/javascript"></script>
<script src="${ctxStatic}/js/applyMoney/applyMoney4.js?v=0.6" type="text/javascript"></script>
<style type="text/css">
.mask_protocol, .mask_protocol_signature {
	max-height: 600px;
	overflow: auto;
	width: 728px;
	z-index: 1;
	padding:0;
}

.mask_protocol_signature p,.protocol_group span,.read_btn span{
font-size:14px;
}
.div_imglook>div.default:hover .lookimg_delBtn {
	display: none !important;
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
	z-index: 2;
	font-size: 20px;
	line-height: 1.4;
	display: none;
	border-radius: 10px;
}

</style>
<script type="text/javascript">
	var ctxpath = '${ctxpath}';
	var financingStep = "${creditUserApply.financingStep}";
	var userInfoId = '${supplyUser.id}';
	var creditInfoId = "${creditUserApply.projectDataId}";//生成的借款信息id
	var letter = "${letter}";//是否有承诺函
	var businessLicenseType;
	var supplyName;//融资方
	var financingSpan;//融资期限
	var creditSupplyId;//供应商id
	var creditSupplyName;//供应商名称
	var packNo = "${packNo}";//合同编号
	var financingMoney;//融资金额
	var voucherSum;//发票总金额
	var creditApplyName;//申请名称
	var step = "${step}";
	var isModify = '${creditUserApply.modify}';
	var error = "${error}";//资料不齐错误提示
	var annexAgreement = "${annexAgreement}";//借款合同
	var annexAgreementUrl = "${annexAgreement.url}";//借款合同
	var creditApplyId = "${creditUserApply.id}";
	var creditUserType = "${loginCreditUserInfo.creditUserType}"; // 确认登录用户的角色
	$(function() {
		if(error!=null && error!=""){
			alert(error);
		}
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
		$(".flie_wrap .div_imgfile").click(function() {
			closeMessage();
			$(this).siblings(".file").click();

		});

		//判断正常申请还是显示数据
		if(step==""){//正常申请
			voucherSum = 0;
		}else{//显示数据
			voucherSum = '${voucherSum}';
			//判断页面是否可编辑
		 	if(isModify=='1'){//不可编辑
				$(".lookimg_delBtn").hide().css("visibility","hidden");
				$(".voucherDel").hide();
				$("#confirm1").hide();
				$("#confirm2").hide();
			}else{
				$("#confirm1").show();
				$("#confirm2").show();
			} 
			
			$("#step4Apply").hide();
			$("#step4Show").show();

			/* if(letter!=""){//有承诺函
				$("#letter").hide();
			} */
			if(financingStep>4){
				$("#confirm2").hide();
			}
			if(creditUserType == '02'){
				$("#confirm1").hide();
				$("#flie_wrap_2_1_id").hide();
				$("#flie_wrap_2_2_id").hide();
				$("#flie_wrap_2_3_id").hide();
				$("#flie_wrap_2_4_id").hide();
				$("#flie_wrap_2_5_id").hide();
				$(".div_imglook>div").addClass("default");
				$(".voucherDelete").hide();
				$(".file_box").hide();
			}
			// 添加图片-隐藏
			// $("#flie_wrap_1").hide();
			// $("#flie_wrap_2").hide();
			// $("#flie_wrap_3").hide();
			// $(".div_imglook>div").addClass("default");
		}

		var on = document.querySelector(".div_imglook");
		//    需要把阅读的文件传进来file element是把读取到的内容放入的容器
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
			reader.addEventListener(
							'load',
							function() {
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
									wrap
											.setAttribute("class",
													"lookimg_delBtn");
									wrap.setAttribute("id", id);
									wrap.innerHTML = "移除";
									img.src = reader.result;
									img.className = "example-image";
									var img_wrap = $("<div><a class='lookimg_wrap example-image-link' data-lightbox='example-"+type+"' href='"+reader.result+"'><img class='example-image' src='"+reader.result+"'/><input class='delete' type='hidden' value='"+id+"'/></a><div class='lookimg_delBtn' id="+id+">移除</div><div class='tit_pic'>"
											+ file.name + "</div></div>");
									element.append(img_wrap);
									element.show();
									$("#" + id).click(function() {
										closeMessage();
										var $this = $(this);
										console.log("id = " + id);
										deleteCreditInfo($this, id);
									});
									break;
								}
							});
		}

		$(".file").on("change",function() {
				closeMessage();
				var _self = $(this);
				for (var i = 0; i < this.files.length; i++) {
					var file = this.files[i];
					// 					readFile(file, _self.parent().siblings(".div_imglook"));

				}

				var file = this.files[0];
				var $this = $(this);
				$this.parent().siblings(".info_error_msg").hide();
				var formData = new FormData();
				var type = $this.attr("id");
				var count = $this.parent().siblings(".div_imglook")
						.children(".lookimg_wrap").length;
				formData.append("type", type);
				formData.append("creditInfoId", creditInfoId);
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
							readFile(file, $this.parent().siblings(
									".div_imglook"), annexFileId, type);
						} else {
							errMessage(result.message);
						}

					}
				});

		});
		//付款承诺书
		$(".file7").on("change", function() {
			closeMessage();
// 			document.getElementById('textfield_02').innerHTML=this.value;
			var file = this.files[0];
			var $this = $(this);
			var val=$this.val();
			// 			     $this.parent().siblings(".info_error_msg").hide();
			var formData = new FormData();
			var type = $this.attr("id");
			// 				var count = $this.parent().siblings(".div_imglook").children(".lookimg_wrap").length;

			formData.append("type", type);
			formData.append("creditInfoId", creditInfoId);
			formData.append("file1", file);
			var element7 = $("#flie_wrap_7");
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
						var path = result.path;
// 						$(".file7").hide();
// 						$("#paymentFile").children("a").attr("href", path);
// 						$("#paymentFile").children("a").attr("download", path);
// 						$("#paymentFile").show();
// 						$("#paymentId").val(annexFileId);

						var str='<div class="" id="'+annexFileId+'">'+
						         '<div>'+val+'</div>'+
								'<a href="'+path+'" download="'+path+'" class="" style="padding-right: 20px">下载</a>'+
								'<span onclick="deletePayment(\''+annexFileId+'\')">删除</span>'+
								'<input type="text" style="display: none" value="'+annexFileId+'"/>'+
								'</div>';
						element7.append(str);
						
						
					} else {
// 						$("#paymentFile").hide();
// 						$(".file7").show();
						errMessage(result.message);
					}

				}
			});

		});

		//付款承诺书（显示数据）
		$(".file72").on("change", function() {
			closeMessage();
// 			document.getElementById('textfield_022').innerHTML=this.value;
			var file = this.files[0];
			var $this = $(this);
			var val2=$this.val();
			// 			     $this.parent().siblings(".info_error_msg").hide();
			var formData = new FormData();
			var type = $this.attr("id");
			// 				var count = $this.parent().siblings(".div_imglook").children(".lookimg_wrap").length;

			formData.append("type", type);
			formData.append("creditInfoId", creditInfoId);
			formData.append("file1", file);
			var element72 = $("#flie_wrap_72");
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
						var path = result.path;
// 						$(".file72").hide();
// 						$("#paymentFile2").children("a").attr("href", path);
// 						$("#paymentFile2").children("a").attr("download", path);
// 						$("#paymentFile2").show();
// 						$("#paymentId2").val(annexFileId);
						var str2 = '<div class="" id="'+annexFileId+'">'+
									'<div>'+val2+'</div>'+
									'<a href="'+path+'" download="'+path+'" class="" style="padding-right: 20px">下载</a>'+
									'<span	onclick="deletePayment(\''+annexFileId+'\')">删除</span>'+
									'<input type="text" style="display: none" value="'+annexFileId+'" id="paymentId2" />'+
									'</div>'
						element72.append(str2);
						
					} else {
						$("#paymentFile2").hide();
						$(".file72").show();
						errMessage(result.message);
					}

				}
			});

		});

		// 上传文件-点击事件.
		$(".filef").on("click", function() {
			closeMessage();
			var file = this.files[0];
			var $this = $(this);
			var voucherNo = $this.parent().parent().siblings().children(".voucherNo").val();
			var voucherMoney = $this.parent().parent().siblings().children(".voucherMoney").val();
			if (voucherNo == null || voucherNo.trim() == "") {
				errMessage("请填写发票编号！");
				return false;
			}
			if (voucherMoney == null || voucherMoney.trim() == "") {
				errMessage("请填写发票金额！");
				return false;
			}
			if (!validatorVoucherMoney(voucherMoney)) {
				errMessage("发票金额，只允许正整数和两位小数位的小数！");
				return false;
			}
		});

		// 上传文件-改变事件.
		$(".filef").on("change", function() {
			closeMessage();
			var file = this.files[0];
			var $this = $(this);
			var voucherNo = $this.parent().parent().siblings().children(".voucherNo").val(); // 发票号
			var voucherMoney = $this.parent().parent().siblings().children(".voucherMoney").val(); // 发票金额

			// 非undefined判断
			if(typeof(file) != "undefined"){
				// document.getElementById('textfield').innerHTML=this.value;
				// document.getElementById('textfield').innerHTML=this.value;
				var fileName = this.value;
				fileName = fileName.split("\\")[fileName.split("\\").length-1];
				$this.parent().parent().siblings(".voucherEnd").children("#textfield2").html(fileName);
				
				// $this.parent().siblings(".info_error_msg").hide();
				var formData = new FormData();
				var type = $this.attr("id");
				// var count = $this.parent().siblings(".div_imglook").children(".lookimg_wrap").length;

				formData.append("type", type);
				formData.append("creditInfoId", creditInfoId);
				formData.append("packNo", packNo);
				formData.append("voucherNo", voucherNo);
				formData.append("voucherMoney", voucherMoney);
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
							$this.parent().parent().siblings(".voucherId").val(result.creditVoucherId);
							$this.parent().parent().siblings(".annexFileId").val(annexFileId);
							$this.parent().parent().siblings().children(".voucherNo").attr("disabled", "disabled");
							$this.parent().parent().siblings().children(".voucherMoney").attr("disabled", "disabled");
							$this.parent().parent().siblings(".voucherEnd").show();
							$this.parent().parent().hide();
							readFilef(file, $this.parent().parent().siblings(".voucherEnd"));
							appendVoucher(voucherMoney);
						} else {
							errMessage(result.message);
						}
					}
				});
			} else {
				errMessage("请选择上传文件 . . . ");
			}
		});

		$(".voucherDel").click(function() {
			closeMessage();
			var $this = $(this);
			var voucherId = $this.parent().siblings(
					".voucherId").val();
			var annexFileId = $this.parent().siblings(
					".annexFileId").val();
			var type = $this.parent().siblings(".voucherFile").children()
					.children("input").attr("id");
			deleteCreditInfo6($this, annexFileId, type);

		});

		$(".lookimg_delBtn").click(function(){
			var $this = $(this);
			var id = $this.attr("id");
			deleteCreditInfo($this, id);
		})
		
		// 声明文件.
		$(".agreement_01").click(function() {
			$(".mask_repd,.mask_protocol_signature").show();
		});
		// 关闭弹框.
		$(".close").click(function() {
			$(".mask_protocol_signature").hide();
		});
		// 声明文件，同意.
		$(".mask_protocol_signature .read_agreen").click(function() {
			$(".mask_protocol_signature").hide();
			$(".agreement_01 span").addClass("cur");
			$("input[id='declaration_file_id']").attr("checked", "true");
			var flag = $("input[id='declaration_file_id']").is(':checked');
			// console.log("同意ajax请求前勾选：" + flag);
			$(".mask_gray").show();
			$(".mask_tip").show();
			$.ajax({
				url : ctxpath + "/creditInfo/createTemplate",
				type : 'post',
				dataType : 'json',
				data : {
					creditInfoId: creditInfoId,
					supplyUserId:userInfoId,
					creditApplyId:creditApplyId
				},
				success : function(result) {
					$(".mask_gray").hide();
					$(".mask_tip").hide();
					var flag = $("input[id='declaration_file_id']").is(':checked');
					// console.log("同意ajax请求后勾选：" + flag);
					if (result.state == 0) {
						if (result.pdfStr == "") {
							$('#credit_pledge_a_id').removeAttr('href');
						} else {
							$("#credit_pledge_a_id").attr("href", "${mainPath}" + result.pdfStr);
						}
						closeMessage();
						message_prompt(result.message);
					} else if (result.state == 1) {
						if (result.pdfStr == "") {
							$('#credit_pledge_a_id').removeAttr('href');
						} else {
							$("#credit_pledge_a_id").attr("href", "${mainPath}" + result.pdfStr);
						}
						$("input[id='declaration_file_id']").attr("checked", "false");
						message_prompt(result.message);
					} else if (result.state == 2) {
						if (result.pdfStr == "") {
							$('#credit_pledge_a_id').removeAttr('href');
						} else {
							$("#credit_pledge_a_id").attr("href", "${mainPath}" + result.pdfStr);
						}
						$("input[id='declaration_file_id']").attr("checked", "false");
						message_prompt(result.message);
					}
					
					
				},
				error : function(data) {
					console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
					$(".mask_gray").hide();
					$(".mask_tip").hide();
				}
			});

		});
		// 声明文件，取消.
		$(".mask_protocol_signature .read_close").click(function() {
			$(".mask_protocol_signature").hide();
			$(".agreement_01 span").removeClass("cur");
			$("input[id='declaration_file_id']").removeAttr("checked");
		});

	});

	function createTemplate(){
		
		$.ajax({
			url : ctxpath + "/creditInfo/createTemplate",
			type : 'post',
			dataType : 'json',
			data : {
				creditInfoId: creditInfoId,
				supplyUserId:userInfoId,
				creditApplyId:creditApplyId
			},
			success : function(result) {
				if (result.state == 0) {
					$("#createTemplate").hide();
					$("#downTemplate").show();
					$("#downTemplate").attr("href","${downpath}"+result.pdfStr);
					$("#downTemplate").attr("download","${downpath}"+result.pdfStr);
				} else{
					errMessage(result.message);
				}

			}
		});
	}
	function createTemplate2(){
		
		$.ajax({
			url : ctxpath + "/creditInfo/createTemplate",
			type : 'post',
			dataType : 'json',
			data : {
				creditInfoId: creditInfoId,
				supplyUserId:userInfoId,
				creditApplyId:creditApplyId
			},
			success : function(result) {
				if (result.state == 0) {
					$("#createTemplate2").hide();
					$("#downTemplate2").show();
					$("#downTemplate2").attr("href","${downpath}"+result.pdfStr);
					$("#downTemplate2").attr("download","${downpath}"+result.pdfStr);
				} else{
					errMessage(result.message);
				}

			}
		});
	}

	//保存
	function toSave() {
		alert("您已保存成功，请下次登陆完善资料");
		window.location.href = "${ctx}/apply/creditUserApply/applyMoney5?id= ${creditUserApply.id}&saveInfo=yes" + "&creditUserType="+creditUserType;
		return false;
	}
	function stepFour() {
		if(voucherSum==0){
			errMessage("请上传至少一张发票！");
			return;
		}
		var flag = $("input[id='declaration_file_id']").is(':checked');
		// console.log("下一步勾选：" + flag);
		if(!flag){
			errMessage("请授权同意付款承诺书，并签订！");
			return;
		}
		window.location.href = "${ctx}/apply/creditUserApply/applyMoney5?id= ${creditUserApply.id}" + "&creditUserType="+creditUserType;
	}
	
	function appendVoucher(voucherMoney) {
		voucherSum = parseFloat(voucherSum) + parseFloat(voucherMoney);
		var str = '<tr class="invoice_group">'
				+ '<td><input type="text" name="" class="voucherNo" value="" placeholder="请输入发票号" /></td>'
				+ '<td><input type="text" name="" class="voucherMoney" value="" placeholder="请输入发票金额"/></td>'
				+ '<td class="voucherFile">'
				+'<div class="file_box">'
				+'<div class="file_button"  id="textfield">上传文件</div>'
			    +'<input type="file" name="fileField" class="file_input filef" id="6"/></div>'
				+'</td>'
				+ '<td class="voucherEnd" style="display: none"><div class="file_button"  id="textfield2">上传文件</div><a href="" class="example-image-link" data-lightbox="example-6" data-title="" style="padding-right:20px"><img class="example-image" src="" style="display: none;">查看</a><span class="voucherDel">删除</span></td>'
				+ '<input type="hidden" class="voucherId"/>'
				+ '<input type="hidden" class="annexFileId"/>' + '</tr>';
		$("#voucherTable").append(str);
		$("#voucherTable2").append(str);

		$(".voucherDel")
				.click(
						function() {
							closeMessage();
							var $this = $(this);
							var voucherId = $this.parent().siblings(
									".voucherId").val();
							var annexFileId = $this.parent().siblings(
									".annexFileId").val();
							var type = $this.parent().siblings("voucherFile")
									.children("input").attr("id");
							deleteCreditInfo6($this, annexFileId, type);

						});

		// 上传文件_点击事件
		$(".filef").on("click", function() {
			closeMessage();
			var file = this.files[0];
			var $this = $(this);
			var voucherNo = $this.parent().parent().siblings().children(".voucherNo").val();
			var voucherMoney = $this.parent().parent().siblings().children(".voucherMoney").val();
			if (voucherNo == null || voucherNo.trim() == "") {
				errMessage("请填写发票编号！");
				return false;
			}
			if (voucherMoney == null || voucherMoney.trim() == "") {
				errMessage("请填写发票金额！");
				return false;
			}
			if (!validatorVoucherMoney(voucherMoney)) {
				errMessage("发票金额，只允许正整数和两位小数位的小数！");
				return false;
			}
		});

		// 上传文件_改变事件
		$(".filef").on("change", function() {
			closeMessage();
			var file = this.files[0];
			var $this = $(this);
			var voucherNo = $this.parent().parent().siblings().children(".voucherNo").val();
			var voucherMoney = $this.parent().parent().siblings().children(".voucherMoney").val();

			// 非undefined判断
			if(typeof(file) != "undefined"){
				// document.getElementById('textfield').innerHTML=this.value;
				// document.getElementById('textfield2').innerHTML=this.value;
				var fileName = this.value;
				fileName = fileName.split("\\")[fileName.split("\\").length-1];
				$this.parent().parent().siblings(".voucherEnd").children("#textfield2").html(fileName);

				// $this.parent().siblings(".info_error_msg").hide();
				var formData = new FormData();
				var type = $this.attr("id");
				// var count = $this.parent().siblings(".div_imglook").children(".lookimg_wrap").length;

				formData.append("type", type);
				formData.append("creditInfoId", creditInfoId);
				formData.append("packNo", packNo);
				formData.append("voucherNo", voucherNo);
				formData.append("voucherMoney", voucherMoney);
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
							$this.parent().parent().siblings(".voucherId").val(result.creditVoucherId);
							$this.parent().parent().siblings(".annexFileId").val(annexFileId);
							$this.parent().parent().siblings().children(".voucherNo").attr("disabled", "disabled");
							$this.parent().parent().siblings().children(".voucherMoney").attr("disabled", "disabled");
							$this.parent().parent().siblings(".voucherEnd").show();
							$this.parent().parent().hide();
							readFilef(file, $this.parent().parent().siblings(".voucherEnd"));
							appendVoucher(voucherMoney);
						} else {
							errMessage(result.message);
						}
					}
				});
			} else {
				errMessage("请选择上传文件 . . . ");
			}
		});

	}

	function readFilef(file, $this) {
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
				$this.children("a").attr("href", reader.result);
				$this.children("a").children("img").attr("src", reader.result);

			}
		});
	};

	//格式化金额
	function formatCurrency(num) {
		num = num.toString().replace(/\$|\,/g, '');
		if (isNaN(num))
			num = "0";
		sign = (num == (num = Math.abs(num)));
		num = Math.floor(num * 100 + 0.50000000001);
		cents = num % 100;
		num = Math.floor(num / 100).toString();
		if (cents < 10)
			cents = "0" + cents;
		for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
			num = num.substring(0, num.length - (4 * i + 3)) + ','
					+ num.substring(num.length - (4 * i + 3));
		return (((sign) ? '' : '-') + num + '.' + cents);
	}

	//删除付款承诺书
	function deletePayment(id) {
		closeMessage();
// 		var id = $("#paymentId").val();
		$.ajax({
			url : ctxpath + "/creditInfo/deleteCredit",
			type : 'post',
			dataType : 'json',
			data : {
				id : userInfoId,
				annexFileId : id
			},
			success : function(result) {
				if (result.state == 0) {
// 					$("#paymentFile").hide();
// 					$(".file7").show();
					$("#"+id).hide();
// 					$("#textfield_02").html("上传承诺函").show();
					
				} else {
					errMessage(result.message);
				}

			}
		});
	}

	//删除付款承诺书(显示数据)
// 	function deletePayment2() {
// 		closeMessage();
// 		var id = $("#paymentId2").val();
// 		$.ajax({
// 			url : ctxpath + "/creditInfo/deleteCredit",
// 			type : 'post',
// 			dataType : 'json',
// 			data : {
// 				id : userInfoId,
// 				annexFileId : id
// 			},
// 			success : function(result) {
// 				if (result.state == 0) {
// 					$("#paymentFile2").hide();
// 					$(".file72").show();
// 					$("#textfield_022").html("上传承诺函").show();
					
// 				} else {
// 					errMessage(result.message);
// 				}

// 			}
// 		});
// 	}

	function deleteCreditInfo6($this, id, type) {
		closeMessage();
		$.ajax({
			url : ctxpath + "/creditInfo/deleteCredit",
			type : 'post',
			dataType : 'json',
			data : {
				id : userInfoId,
				annexFileId : id
			},
			success : function(result) {
				if (result.state == 0) {
					$this.parent().parent().hide();
					message_prompt("发票，删除成功！");
				} else {
					errMessage(result.message);
				}

			}
		});
	}

	function deleteCreditInfo($this, id) {
		closeMessage();
		$.ajax({
			url : ctxpath + "/creditInfo/deleteCredit",
			type : 'post',
			dataType : 'json',
			data : {
				id : userInfoId,
				annexFileId : id
			},
			success : function(result) {
				if (result.state == 0) {
					//					$("#flie_wrap_2").hide();
					//					$("#btn_ImgUpStart_2").hide();

					// 						$this.parent().hide();
					$this.parent().remove();
				} else {
					errMessage(result.message);
				}
			}
		});
	}

	function closeMessage() {
		$("#messageBox").hide();
	}
	function errMessage(str) {
		$("#messageBox").show().html(str);
	}
	
	// 消息提示 .
	function message_prompt(message) {
		$(".mask_investNo_tip").html(message);
		$(".mask_investNo_tip").show();
		setTimeout(function() {
			$(".mask_investNo_tip").hide();
		}, 2000);
	} // --
$(".loan_apply").height($(window).height());

	// 校验发票金额(发票金额，允许正整数或带小数位后两位的小数)
	function validatorVoucherMoney(money){
		return /^[1-9]+\d*(\.\d{0,2})?$|^0?\.\d{0,2}$/.test( money + "");
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/user/list">借款申请</a></li>
	</ul>

<%-- 	<form:form id="searchForm" modelAttribute="creditUserApply"
		action="${ctx}/apply/creditUserApply/applyMoney4" method="post"
		class="breadcrumb form-search"> --%>

		<div class="loan_apply loan_apply_wrap_01">
			<div class="loan_apply_wrap">
				<div class="la_tip">温馨提示:以下各项为必填项，在协议签订完成后方可提交申请!</div>
				<div class="la_tab">
					<div class="clear">
						<ul>
							<li class="" id="tab-1"><i>1</i><span>融资类型</span></li>
							<li class="" id="tab-2"><i>2</i><span>选择供应商</span></li>
							<li class="step" id="tab-3"><i>3</i><span>基础交易</span></li>
							<li class="cur step" id="tab-4"><i>4</i><span>上传资料</span></li>
							<li class="step" id="tab-5"><i>5</i><span>融资金额</span></li>
							<li class="" id="tab-6"><i>6</i><span>签订协议</span></li>
						</ul>
					</div>
				</div>
				<div class="la_con">
					<div class="la_step_four ">

						<div class="info_basic_wrap" id="step4Apply">
							<dl class="font_size18">
								<dt>
									<span class="pull-left">仅支持bmp、jpg、jpeg、png格式图片，单张图片大小不超过10M且越清晰越好，每张图片只能包含一项内容，每个类别不超过30张。加*为必填项</span>
									</spab>
								</dt>
								<dd class="even">
									<b class="pull-left">1.合同影印件*</b> <span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/hetong.jpg"
										class="example-image-link" data-lightbox="example-10"><img
											src="${ctxStatic}/images/hetong.jpg" class="example-image"
											style="display: none"></a>查看实例</span>
								</dd>

								<div class="imgfile_wrap">
									<div class="flie_wrap" id="flie_wrap_1">

										<div class="div_imgfile" id="div_imgfile_1"></div>

										<input type="file" accept="image/png,image/jpg" name="file"
											id="1" multiple="multiple" class="file">

									</div>

									<!--图片预览容器-->
									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_1">
										<c:if test="${not empty annexAgreement.url}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-1" href="${staticPath}/upload/image/${annexAgreement.url}"><img class="example-image" src="${staticPath}/upload/image/${annexAgreement.url}"><input class="delete" type="hidden" value="${annexAgreement.id}"></a><div class="lookimg_delBtn" id="${annexAgreement.id}">移除</div></div>
										</c:if>
									</div>

									<!--确定上传按钮-->
									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_1" class="fr submit_btn" />-->
									<div class="info_error_msg" id="info_error_msg_1">图片大小不符</div>
								</div>
								<dd class="even">
									<b class="pull-left">2.订单(ERP系统截图)</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/fukuandan.jpg"
										class="example-image-link" data-lightbox="example-20"><img
											src="${ctxStatic}/images/fukuandan.jpg" class="example-image"
											style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap">
									<div class="flie_wrap" id="flie_wrap_2">
										<div class="div_imgfile" id="div_imgfile_2"></div>
										<input type="file" accept="image/png,image/jpg" name="file"
											id="2" class="file">
									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_2"></div>

									<!--确定上传按钮-->
									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_2" class="fr submit_btn" />-->
									<div class="info_error_msg">图片大小不符</div>
								</div>

								<dd class="even">
									<b class="pull-left">3.发货单*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/fahuodan.png"
										class="example-image-link" data-lightbox="example-30"><img
											src="${ctxStatic}/images/fahuodan.png" class="example-image"
											style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap">
									<div class="flie_wrap" id="flie_wrap_3">
										<div class="div_imgfile" id="div_imgfile_3"></div>
										<input type="file" accept="image/png,image/jpg" name="file"
											id="3" class="file">
									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_3"></div>

									<!--确定上传按钮-->
									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_3" class="fr submit_btn" />-->
									<div class="info_error_msg">图片大小不符</div>
								</div>
								<dd class="even">
									<b class="pull-left">4.验收单*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/yanshoudan.jpg"
										class="example-image-link" data-lightbox="example-40"><img
											src="${ctxStatic}/images/yanshoudan.jpg"
											class="example-image" style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap">
									<div class="flie_wrap" id="flie_wrap_4">
										<div class="div_imgfile" id="div_imgfile_4"></div>
										<input type="file" accept="image/png,image/jpg" name="file"
											id="4" class="file">
									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_4"></div>

									<!--确定上传按钮-->
									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_4" class="fr submit_btn" />-->
									<div class="info_error_msg">图片大小不符</div>
								</div>
								<dd class="even">
									<b class="pull-left">5.对账单*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/duizhangdan.png"
										class="example-image-link" data-lightbox="example-50"><img
											src="${ctxStatic}/images/duizhangdan.png"
											class="example-image" style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap">
									<div class="flie_wrap" id="flie_wrap_5">
										<div class="div_imgfile" id="div_imgfile_5"></div>
										<input type="file" accept="image/png,image/jpg" name="file"
											id="5" class="file" multiple="multiple">
									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_5"></div>

									<!--确定上传按钮-->
									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_5" class="fr submit_btn" />-->
									<div class="info_error_msg">图片大小不符</div>
								</div>
								<dd class="even">
									<b class="pull-left">6.发票*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/fapiao.jpg"
										class="example-image-link" data-lightbox="example-60"><img
											src="${ctxStatic}/images/fapiao.jpg" class="example-image"
											style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap">
									<div class="invoice_wrap">
										<table border="" cellspacing="" cellpadding=""
											id="voucherTable">
											<tr>
												<th>发票号</th>
												<th>发票金额</th>
												<th>发票影印件</th>
											</tr>

											<tr class="invoice_group">
												<td><input type="number" name="" class="voucherNo"
													value="" placeholder="请输入发票号" /></td>
												<td><input type="text" name="" class="voucherMoney" value="" placeholder="请正确输入发票实际金额" /></td>
												<td class="voucherFile">
												<!--      <input type="file" class="filef" name="" id="6" value="" /> -->
													<div class="file_box">
													    
													    <div class="file_button"  id='textfield'>上传文件</div>
													    <input type="file" name="fileField" class="file_input filef" id="6" />
													    
													   
													</div>
												</td>
												<td class="voucherEnd" style="display: none">
													<div class="file_button"  id='textfield2'>上传文件</div>
													<a href=""
													class="example-image-link" data-lightbox="example-6"
													data-title="" style="padding-right: 20px"><img
														class="example-image" src="" style="display: none;">查看</a><span
													class="voucherDel">删除</span></td>
												<input type="hidden" class="voucherId" />
												<input type="hidden" class="annexFileId" />
											</tr>
										</table>
									</div>
									<div class="info_error_msg">图片大小不符</div>
								</div>
								<dd class="even">
									<b class="pull-left">7.核心企业承诺函*</b><span class="pull-left">1.请下载已自动生成的承诺函，打印并加盖公司公章。2.拍照或扫描上传盖章承诺函彩色版，确保公章为红色</span>
									<b class="pull-left" style="color:red">*内容修改后需要重新上传承诺函</b>
									<span class="pull-right">
									<a href="" download="" target="_blank" id="downTemplate" style="color: #fff;display: none"  >下载模板</a>
									<a href="javascript:;"  id="createTemplate" onclick="createTemplate();" style="color: #fff">生成模板</a>
									</span>
								</dd>
								<div class="imgfile_wrap">
									<div class="" id="flie_wrap_7">
										<div class="" id="div_imgfile_7" style="visibility: hidden"></div>
										<div class="file_box">
										    <div class="file_button"  id='textfield_02'>上传承诺函</div>
										    <input type="file" name="fileField" class="file_input file7" id="7" />
										</div>
										<!-- <div class="" id="paymentFile" style="display: none">
											<a href="" download="" class="" style="padding-right: 20px">下载</a><span
												onclick="deletePayment()">删除</span>
											<input type="text" style="display: none" id="paymentId" />
										</div> -->
										
									</div>

									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_6" class="fr submit_btn" />-->
									<div class="info_error_msg">图片大小不符</div>
								</div>

							</dl>

						</div>
						<div class="info_basic_wrap" id="step4Show" style="display:none">
							<dl class="font_size18">
								<dt>
									<span class="pull-left">仅支持bmp、jpg、jpeg、png格式图片，单张图片大小不超过10M且越清晰越好，每张图片只能包含一项内容，每个类别不超过30张。加*为必填项</span>
									</spab>
								</dt>
								<dd class="even">
									<b class="pull-left">1.合同影印件*</b> <span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/hetong.jpg"
										class="example-image-link" data-lightbox="example-10"><img
											src="${ctxStatic}/images/hetong.jpg" class="example-image"
											style="display: none"></a>查看实例</span>
								</dd>

								<div class="imgfile_wrap">
									<div class="flie_wrap" id="flie_wrap_2_1_id">

										<div class="div_imgfile" id="div_imgfile_1"></div>

										<input type="file" accept="image/png,image/jpg" name="file"
											id="1" multiple="multiple" class="file">

									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_1">
										<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
											<c:if test="${creditAnnexFile.type == '1'}">
												<div><a class="lookimg_wrap example-image-link" data-lightbox="example-1" href="${staticPath}/upload/image/${creditAnnexFile.url}"><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
											</c:if>
										</c:forEach>
									</div>

									<!--确定上传按钮-->
									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_1" class="fr submit_btn" />-->
									<div class="info_error_msg" id="info_error_msg_1">图片大小不符</div>
								</div>
								<dd class="even">
									<b class="pull-left">2.订单(ERP系统截图)</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/fukuandan.jpg"
										class="example-image-link" data-lightbox="example-20"><img
											src="${ctxStatic}/images/fukuandan.jpg" class="example-image"
											style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap">
									<div class="flie_wrap" id="flie_wrap_2_2_id">
										<div class="div_imgfile" id="div_imgfile_2"></div>
										<input type="file" accept="image/png,image/jpg" name="file"
											id="2" class="file">
									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_2">
										<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
											<c:if test="${creditAnnexFile.type == '2'}">
												<div><a class="lookimg_wrap example-image-link" data-lightbox="example-2" href="${staticPath}/upload/image/${creditAnnexFile.url}"><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
											</c:if>
										</c:forEach>
									</div>

									<!--确定上传按钮-->
									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_2" class="fr submit_btn" />-->
									<div class="info_error_msg">图片大小不符</div>
								</div>

								<dd class="even">
									<b class="pull-left">3.发货单*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/fahuodan.png"
										class="example-image-link" data-lightbox="example-30"><img
											src="${ctxStatic}/images/fahuodan.png" class="example-image"
											style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap">
									<div class="flie_wrap" id="flie_wrap_2_3_id">
										<div class="div_imgfile" id="div_imgfile_3"></div>
										<input type="file" accept="image/png,image/jpg" name="file"
											id="3" class="file">
									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_3">
										<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
											<c:if test="${creditAnnexFile.type == '3'}">
												<div><a class="lookimg_wrap example-image-link" data-lightbox="example-3" href="${staticPath}/upload/image/${creditAnnexFile.url}"><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
											</c:if>
										</c:forEach>
									</div>

									<!--确定上传按钮-->
									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_3" class="fr submit_btn" />-->
									<div class="info_error_msg">图片大小不符</div>
								</div>
								<dd class="even">
									<b class="pull-left">4.验收单*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/yanshoudan.jpg"
										class="example-image-link" data-lightbox="example-40"><img
											src="${ctxStatic}/images/yanshoudan.jpg"
											class="example-image" style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap">
									<div class="flie_wrap" id="flie_wrap_2_4_id">
										<div class="div_imgfile" id="div_imgfile_4"></div>
										<input type="file" accept="image/png,image/jpg" name="file"
											id="4" class="file">
									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_4">
										<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
											<c:if test="${creditAnnexFile.type == '4'}">
												<div><a class="lookimg_wrap example-image-link" data-lightbox="example-4" href="${staticPath}/upload/image/${creditAnnexFile.url}"><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
											</c:if>
										</c:forEach>
									</div>

									<!--确定上传按钮-->
									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_4" class="fr submit_btn" />-->
									<div class="info_error_msg">图片大小不符</div>
								</div>
								<dd class="even">
									<b class="pull-left">5.对账单*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/duizhangdan.png"
										class="example-image-link" data-lightbox="example-50"><img
											src="${ctxStatic}/images/duizhangdan.png"
											class="example-image" style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap">
									<div class="flie_wrap" id="flie_wrap_2_5_id">
										<div class="div_imgfile" id="div_imgfile_5"></div>
										<input type="file" accept="image/png,image/jpg" name="file"
											id="5" class="file" multiple="multiple">
									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_5">
										<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
											<c:if test="${creditAnnexFile.type == '5'}">
												<div><a class="lookimg_wrap example-image-link" data-lightbox="example-5" href="${staticPath}/upload/image/${creditAnnexFile.url}"><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
											</c:if>
										</c:forEach>
									</div>

									<!--确定上传按钮-->
									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_5" class="fr submit_btn" />-->
									<div class="info_error_msg">图片大小不符</div>
								</div>
								<dd class="even">
									<b class="pull-left">6.发票*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/fapiao.jpg"
										class="example-image-link" data-lightbox="example-60"><img
											src="${ctxStatic}/images/fapiao.jpg" class="example-image"
											style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap" style="display: none;">
									<div class="invoice_wrap">
										<table border="" cellspacing="" cellpadding=""
											id="voucherTable2">
											<tr>
												<th>发票号</th>
												<th>发票金额</th>
												<th>发票影印件</th>
											</tr>
												<c:forEach items="${creditVoucherList}" var="creditVoucher" >
													<tr class="invoice_group" id="voucherOld">
														<td><input type="number" name="" class="voucherNo"
															value="${creditVoucher.no }" /></td>
														<td><input type="text" name="" class="voucherMoney" value="${creditVoucher.money }" /></td>
														<td class="voucherFile" style="display:none">
															<div class="file_box">
															    
															    <div class="file_button"  id='textfiel'>上传文件</div>
															    <input type="file" name="fileField" class="file_input filef" id="6" />
															</div>
														</td>
														<td class="voucherEnd"><a href="${staticPath}/upload/image/${creditVoucher.url }"
															class="example-image-link" data-lightbox="example-6"
															data-title="" style="padding-right: 20px"><img
																class="example-image" src="${staticPath}/upload/image/${creditVoucher.url }" style="display: none;">查看</a><span
															class="voucherDel">删除</span></td>
														<input type="hidden" class="voucherId" value="${creditVoucher.id }" />
														<input type="hidden" class="annexFileId" value="${creditVoucher.annexId }" />
													</tr>
												</c:forEach>
												<tr class="invoice_group" id="voucherNew">
													<td><input type="number" name="" class="voucherNo"
														value="" placeholder="请输入发票号" /></td>
													<td><input type="text" name="" class="voucherMoney" value="" placeholder="请正确输入发票实际金额" /></td>
													<td class="voucherFile">
														<div class="file_box">
														    
														    <div class="file_button"  id='textfiel'>上传文件</div>
														    <input type="file" name="fileField" class="file_input filef" id="6" />
														</div>
													</td>	
													<td class="voucherEnd">
													<div class="file_button"  id='textfield2'>上传文件</div>
													<a href=""
														class="example-image-link" data-lightbox="example-6"
														data-title="" style="padding-right: 20px"><img
															class="example-image" src="" style="display: none;">查看</a><span
														class="voucherDel">删除</span></td>
													<input type="hidden" class="voucherId" />
													<input type="hidden" class="annexFileId" />
												</tr>

										</table>
									</div>
									<div class="info_error_msg">图片大小不符</div>
								</div>
								<!-- 改版发票页面展示. -->
								<div>
									<table id="my_voucher_tab_id" summary="为自由而努力" class="imageTable">
										<thead>
											<tr>
												<th scope="col">发票号</th>
												<th scope="col">发票金额</th>
												<th scope="col">发票代码</th>
												<th scope="col">发票日期</th>
												<th scope="col">发票资料</th>
											</tr>
										</thead>
										<tbody id="my_voucher_tbody_id">
											<c:forEach items="${creditVoucherList}" var="creditVoucher" >
												<tr>
													<td>
														<input value="${creditVoucher.no}" type="text" name="" placeholder="发票号" style="width: 138px;" disabled="disabled" />
													</td>
													<td>
														<input value="${creditVoucher.money}" type="text" name="" maxlength="10" placeholder="发票金额" style="width: 138px;" disabled="disabled" />
													</td>
													<td>
														<input value="${creditVoucher.code}" type="text" name="" placeholder="发票代码" style="width: 138px;" disabled="disabled" />
													</td>
													<td>
														<input name="" type="text" value="<fmt:formatDate value='${creditVoucher.issueDate}' pattern='yyyy-MM-dd'/>" readonly="readonly" class="input-medium Wdate" placeholder="发票日期" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" style="width: 140px;" disabled="disabled" />
													</td>
													<td>
														<a href="${staticPath}/upload/image/${creditVoucher.url}" class="example-image-link" data-lightbox="example-6" data-title="发票" style="padding-right: 20px"><img class="example-image" src="${staticPath}/upload/image/${creditVoucher.url}" style="display: none;">查看</a>
														<span class="voucherDelete" style="color: red;">删除</span>
													</td>
													<input type="hidden" id="creVoucherId" value="${creditVoucher.id }" />
													<input type="hidden" id="creVoucherAnnexFileId" value="${creditVoucher.annexId }" />
												</tr>
											</c:forEach>
											<tr>
												<td>
													<input type="text" id="creVoucherNo" name="" maxlength="8" placeholder="发票编号" style="width: 138px;" />
												</td>
												<td>
													<input type="text" id="creVoucherMoney" name="" maxlength="10" placeholder="发票金额" style="width: 138px;" />
												</td>
												<td>
													<input type="text" id="creVoucherCode" name="" maxlength="12" placeholder="发票代码" style="width: 138px;" />
												</td>
												<td>
													<input type="text" id="creVoucherIssueDate" name="" value="<fmt:formatDate value='${creditVoucher.issueDate}' pattern='yyyy-MM-dd'/>" readonly="readonly" class="input-medium Wdate" placeholder="开票日期" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" style="width: 140px;" />
												</td>
												<td style="display:block;">
													<div class="file_box">
														<a href="#">上传文件</a>
														<input type="file" name="fileField" class="file_input invoice_file_id" id="6" />
													</div>
												</td>
												<td class="voucherFileView" style="display:none;">
													<a href="${staticPath}/upload/image/${creditVoucher.url}" class="example-image-link" data-lightbox="example-6" data-title="发票" style="padding-right: 20px"><img class="example-image" src="${staticPath}/upload/image/${creditVoucher.url}" style="display: none;">查看</a>
													<span class="voucherDelete" style="color: red;" >删除</span>
												</td>
												<input type="hidden" id="creVoucherId" />
												<input type="hidden" id="creVoucherAnnexFileId" />
											</tr>
										</tbody>
									</table>
								</div>
								<div id="messageBox" class="alert" style="display: none;color: red;">缺少必要参数</div>
								<!-- 声明文件，阅读声明文件并同意授权，生成声明文件. -->
								<dd class="even">
									<b class="pull-left">7.核心企业付款承诺书*</b>
									<!-- <span class="pull-left">1.请下载已自动生成的承诺函，打印并加盖公司公章。2.拍照或扫描上传盖章承诺函彩色版，确保公章为红色</span> -->
									<b class="pull-left" style="color:red">*阅读付款承诺书并同意授权，生成付款承诺书*</b>
								</dd>
								<c:if test="${creditUserApply.creditUserType == '11'}">
									<div class="setting_phone_group">
										<div class="agreement fl agreement_01">
											<span class=""><input type="checkbox" id="declaration_file_id" ><i></i></span>
											<em class="fl">付款承诺书</em>
										</div>
									</div>
								</c:if>
								<div class="la_con">
									<div class="la_step la_step_four cur">
										<div class="info_basic_wrap">
											<dl class="font_size18">
												<dd class="even">
													<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
														<c:if test="${creditAnnexFile.type == '7'}">
															<a id="credit_pledge_a_id" class="pull-left" href="${staticPath}${creditAnnexFile.url}" target="_Blank">付款承诺书</a>
														</c:if>
													</c:forEach>
												</dd>
											</dl>
										</div>
									</div>
								</div>

								<!-- 声明文件，阅读声明文件并同意授权，生成声明文件. -->
								<div class="mask_repd mask_protocol_signature">
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
										<h4 class="modal-title" id="myModalLabel">付款承诺书</h4>
									</div>
									<div class="mask_model_repd">
										<div class="protocol_group">
											<h3>付款承诺书</h3>
											<p>致: 中投摩根信息技术（北京）有限责任公司</p>	
											<p>我公司已收到（供应商）应收账款质押通知，该公司将其对我公司享有的以下应收账款出质给贵公司网络借贷信息中介服务平台（域名：www.cicmorgan.com）上的出借人），我公司确认以下账款系我公司应付账款:</p>
											<p>（见附表一）</p>
											<p>我公司承诺如下：</p>
											<p>1. 我公司对上述应付账款（含应收账款本金及产生的利息、服务费、违约金等全部相关费用）无任何异议并承诺按期（即《供应链融资合作框架协议》或相应付款通知书约定的付款时间）付款。我公司同时就《借款协议（应收账款质押）》、《供应链融资合作框架协议》项下借款人的全部债务（包括但不限于应收账款本金、借款本金、利息、服务费、罚息、违约金等全部相关应付款项）承担不可撤销的连带保证责任。保证期限为自主合同（即《借款协议（应收账款质押）》、《供应链融资合作框架协议》）付款义务到期之日起3 年。</p>
											<p>2. 如我公司未根据主合同约定在应付款日及时足额支付相应价款的，每逾期一日，我公司愿向中投摩根平台关于此融资项目的出借人支付应付款总额的0.03%作为违约金，直至付清本息为止。</p>
											<p>3. 管辖及争议解决方式：各方因履行本协议或与本协议有关的所有纠纷均应以友好协商的方式解决，协商不成的，提请北京仲裁委员会按照该会现行有效的仲裁规则进行裁决，仲裁裁决实行一裁终局制，对双方均具有约束力，仲裁地点在北京。本承诺书适用中华人民共和国法律，在争议持续期间，除争议部分外，我公司承诺继续履行本承诺书其余条款。</p>
											<p>特此承诺。</p>
											<div class="clear"><span class="fr">承诺人：核心企业</span></div>
											<div class="clear"><span class="fr">日期：xxxx年-xx月-xx日</span></div> 
										</div>
										<div class="read_btn">
											<span class="read_agreen">同意</span>
											<span class="read_close">取消</span>
										</div>
									</div>
								</div>
								<!-- <dd class="even">
									<b class="pull-left">7.核心企业承诺函*</b><span class="pull-left">1.请下载已自动生成的承诺函，打印并加盖公司公章。2.拍照或扫描上传盖章承诺函彩色版，确保公章为红色</span>
									<b class="pull-left" style="color:red">*内容修改后需要重新上传承诺函</b>
									<span class="pull-right">
									<a href="" download="" target="_blank" id="downTemplate2" style="color: #fff;display: none"  >下载模板</a>
									<a href="javascript:;"  id="createTemplate2" onclick="createTemplate2();" style="color: #fff">生成模板</a>
									</span>
								</dd> -->
								<%-- <div class="imgfile_wrap">
									<div class="" id="flie_wrap_72">
										<div class="" id="div_imgfile_7" style="visibility: hidden"></div>
										<div class="file_box" id="letter">
										    
										    <div class="file_button"  id='textfield_022'>上传承诺函</div>
										    <input type="file" name="fileField" class="file_input file72" id="7" />
										    
										   
										</div>
										<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
											<c:if test="${creditAnnexFile.type == '7'}">
												<div class="" id="${creditAnnexFile.id}">
													<div>'${creditAnnexFile.id}'</div>
													<a href="${creditAnnexFile.url}" download="${creditAnnexFile.url}" class="" style="padding-right: 20px">下载</a>
													<span onclick="deletePayment('${creditAnnexFile.id}')">删除</span>
													<input type="text" style="display: none" value="${creditAnnexFile.id}" id="paymentId2" />
												</div>
											</c:if>
										</c:forEach>
										
									</div>

									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_6" class="fr submit_btn" />-->
									<div class="info_error_msg">图片大小不符</div>
								</div> --%>

							</dl>

						</div>
						<button class="btn btn_four" id="confirm1" onclick="toSave();">保存</button> 
						<button class="btn btn_four" id="confirm2" onclick="stepFour();">下一步</button> 
						<div id="messageBox" class="alert alert-success " style="display: none;">缺少必要参数</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="mask_gray"></div>
		<div class="mask_tip">正在创建创建承诺书，请您耐心等待. . .</div>
		<div class="mask_investNo_tip"></div>


<%-- 	</form:form> --%>


</body>

</html>