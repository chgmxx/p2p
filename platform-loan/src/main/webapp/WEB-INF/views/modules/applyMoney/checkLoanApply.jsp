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
<script type="text/javascript" src="${ctxStatic}/js/jquery.js"></script>
<script type="text/javascript" src="${ctxStatic}/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctxStatic}/js/jquery.jerichotab.js"></script>
<script src="${ctxStatic}/js/jquery.cookie.js" type="text/javascript"></script>
<script src="${ctxStatic}/js/lightbox.js" type="text/javascript"></script>
<script src="${ctxStatic}/js/CheckUtils.js" type="text/javascript"></script>
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
				window.location.href = "${ctx}/apply/creditUserApply/applyMoney"+step1+"?id= ${creditUserApply.id}&step="+step1;
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
		window.location.href = "${ctx}/apply/creditUserApply/applyMoney5?id= ${creditUserApply.id}&saveInfo=yes";
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
		window.location.href = "${ctx}/apply/creditUserApply/applyMoney5?id= ${creditUserApply.id}";
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
							<li class="cur step" id="tab-4"><i>*</i><span>上传资料</span></li>
						</ul>
					</div>
				</div>
				<div class="la_con">
					<div class="la_step_four ">

						<div class="info_basic_wrap" id="step4Apply">
							<dl class="font_size18">
								<dt>
									<span class="pull-left">借款申请名称：<b>${creditUserApply.creditApplyName}</b></span>
									</spab>
								</dt>
								<dd class="even">
									<b class="pull-left">1.借款合同*</b> <span class="pull-left">请上传借款合同。</span>
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
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-1" href=""><img class="example-image" src="${staticPath}/upload/image/${annexAgreement.url}"><input class="delete" type="hidden" value="${annexAgreement.id}"></a><div class="lookimg_delBtn" id="${annexAgreement.id}">移除</div></div>
										</c:if>
									</div>

									<!--确定上传按钮-->
									<!--<input type="button" value="确定上传" id="btn_ImgUpStart_1" class="fr submit_btn" />-->
									<div class="info_error_msg" id="info_error_msg_1">图片大小不符</div>
								</div>
								<dd class="even">
									<b class="pull-left">2.抵押物资料</b><span class="pull-left">请上传抵押物资料。</span>
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
							</dl>
						</div>
						<button class="btn btn_four" id="confirm1" onclick="alert('保存成功，等待借款申请被确认！');">保存</button> 
						<button class="btn btn_four" id="confirm2" onclick="alert('保存成功，等待借款申请被确认！');">下一步</button> 
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