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
<script type="text/javascript">
	var ctxpath = '${ctxpath}';
	var userInfoId = '${creditUserApply.creditSupplyId}';//供应商
	// 	var middlemenId;//代偿户
	var creditInfoId = "${creditUserApply.projectDataId}";//生成的借款信息id
	var businessLicenseType;
	var supplyName;//融资方
	var financingSpan;//融资期限
	var creditSupplyId;//供应商id
	var creditSupplyName;//供应商名称
	var packNo = "${packNo}";//合同编号
	var financingMoney;//融资金额
	var voucherSum = '${voucherSum}';//发票总金额
	var creditApplyName;//申请名称
	var step = "${step}";
	var fileConfirm = '${creditUserApply.fileConfirm}';
	var error = "${error}";//资料不齐错误提示
	var creditOrder = '${creditOrder}';//订单
	var annexAgreement = "${annexAgreement}";//借款合同
	var annexAgreementUrl = "${annexAgreement.url}";//借款合同
	$(function() {
		if(error!=null && error!=""){
			alert(error);
		}
		//申请流程点击
		$(".step").click(function(){
			var step1 = $(this).children("i").html();
			var financingStep = "${creditUserApply.financingStep}";
			if(step1-1>financingStep){
				alert("跳转页面尚未完成！");
				return false;
			}else if(step1-1==financingStep){
			}else{
				window.location.href = "${ctx}/apply/orderApply/applyMoney"+step1+"?id= ${creditUserApply.id}&step="+step1;
			}
		});
		
		$(".flie_wrap .div_imgfile").click(function() {
			closeMessage();
			$(this).siblings(".file").click();

		});
		//判断正常申请还是显示数据
		if(step==""){//正常申请
			
		}else{//显示数据
			//判断页面是否可编辑
			if(fileConfirm=='1'){//不可编辑
				$(".lookimg_delBtn").hide().css("visibility","hidden");
				$(".btn_four").hide();
				$(".voucherDel").hide();
				$(".picture").hide();
				$("#orderNew").hide();
			}else{
				if(creditOrder!=null && creditOrder!=""){
					$("#orderOld").show();
					$("#orderNew").hide();
				}else{
					$("#orderOld").hide();
					$("#orderNew").show();
				}
			}
			
			$("#step4Apply").hide();
			$("#step4Show").show();
		}

		$(".lookimg_delBtn").click(function(){
			var $this = $(this);
			var id = $this.attr("id");
			deleteCreditInfo($this, id);
		})
		
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
									// 							img_wrap.append(input);
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
		;

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

		
		// 上传订单_单击事件
		$(".fileo").on("click", function() {
			closeMessage();
			var file = this.files[0];
			var $this = $(this);
			var orderNo = $this.parent().parent().siblings().children(".voucherNo").val();
			var orderMoney = $this.parent().parent().siblings().children(".voucherMoney").val();
			if (orderNo == null || orderNo.trim() == "") {
				errMessage("请填写订单编号！");
				return false;
			}
			if (orderMoney == null || orderMoney.trim() == "") {
				errMessage("请填写订单金额！");
				return false;
			}
			if (!validatorVoucherMoney(orderMoney)) {
				errMessage("订单金额，只允许正整数和两位小数位的小数！");
				return false;
			}
		});
		
		// 上传订单_改变事件
		$(".fileo").on("change", function() {
			closeMessage();
			var file = this.files[0];
			var $this = $(this);
			var orderNo = $this.parent().parent().siblings().children(".voucherNo").val();
			var orderMoney = $this.parent().parent().siblings().children(".voucherMoney").val();

			// 非undefined判断
			if(typeof(file) != "undefined"){
				document.getElementById('textfieldo').innerHTML=this.value;

				// $this.parent().siblings(".info_error_msg").hide();
				var formData = new FormData();
				var type = $this.attr("id");
				// var count = $this.parent().siblings(".div_imglook").children(".lookimg_wrap").length;

				formData.append("type", type);
				formData.append("creditInfoId", creditInfoId);
				formData.append("packNo", packNo);
				formData.append("orderNo", orderNo);
				formData.append("orderMoney", orderMoney);
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
							$this.parent().parent().siblings().children(".voucherMoney").attr("disabled","disabled");
							$this.parent().parent().siblings(".voucherEnd").show();
							$this.parent().parent().hide();
							readFilef(file, $this.parent().parent().siblings(".voucherEnd"));
						} else {
							errMessage(result.message);
						}
					}
				});
			} else {
				errMessage("请选择上传文件 . . . ");
			}
		});

		$(".voucherDel")
		.click(
				function() {
					closeMessage();
					var $this = $(this);
					var voucherId = $this.parent().siblings(
							".voucherId").val();
					var annexFileId = $this.parent().siblings(
							".annexFileId").val();
					var type = $this.parent().siblings(".voucherFile").children()
							.children("input").attr("id");
					deleteCreditInfo6($this, annexFileId, type);
					
// 					$("#orderNew").show();
				});
		
	});
	//保存
	function toSave() {
		alert("您已保存成功，请下次登陆完善资料");
		window.location.href = "${ctx}/apply/orderApply/applyMoney5?id= ${creditUserApply.id}&saveInfo=yes";
		return false;
	}
	//提交
	function toSubmit() {
		if(confirm("请确认您上传的资料是否完整有效，点击确认将短信及邮件通知供应商填写融资申请。")){
			window.location.href = "${ctx}/apply/orderApply/applyMoney5?id= ${creditUserApply.id}&&sendEmail=1";
		}
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
				+ '<td class="voucherEnd" style="display: none"><a href="" class="example-image-link" data-lightbox="example-6" data-title="" style="padding-right:20px"><img class="example-image" src="" style="display: none;">查看</a><span class="voucherDel">删除</span></td>'
				+ '<input type="hidden" class="voucherId"/>'
				+ '<input type="hidden" class="annexFileId"/>' + '</tr>';
		if(step!=""){
			$("#voucherTable2").append(str);
		}else{
			$("#voucherTable").append(str);
		}
		

		$(".voucherDel")
				.click(
						function() {
							closeMessage();
							var $this = $(this);
							var voucherId = $this.parent().siblings(
									".voucherId").val();
							var annexFileId = $this.parent().siblings(
									".annexFileId").val();
							var type = $this.parent().siblings(".voucherFile").children()
									.children("input").attr("id");
							deleteCreditInfo6($this, annexFileId, type);
// 							$("#orderNew").show();
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
	function deletePayment() {
		closeMessage();
		var id = $("#paymentId").val();
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
					$("#paymentFile").hide();
					$(".file7").show();
					$("#textfield_02").html("上传承诺函").show();
					
				} else {
					errMessage(result.message);
				}

			}
		});
	}

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
// 					$this.parent().parent().hide();
					$this.parent().hide();
					$this.parent().siblings(".voucherFile").show();
					$this.parent().siblings(".voucherId").val("");
					$this.parent().siblings(".annexFileId").val("");
					$this.parent().siblings(".voucherNoTd").children(".voucherNo").removeAttr("disabled").val("");
					$this.parent().siblings(".voucherMoneyTd").children(".voucherMoney").removeAttr("disabled").val("");
					document.getElementById('textfieldo').innerHTML="上传文件";
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

		<div class="loan_apply loan_apply_wrap_01">
			<div class="loan_apply_wrap">
				<div class="la_tip">温馨提示:以下各项为必填项，在协议签订完成后方可提交申请!</div>
				<div class="la_tab">
					<div class="clear">
						<ul>
							<li class="" id="tab-1"><i>1</i><span>融资类型</span></li>
							<li class="" id="tab-2"><i>2</i><span>选择采购方</span></li>
							<li class="step" id="tab-3"><i>3</i><span>基础交易</span></li>
							<li class="cur step" id="tab-4"><i>4</i><span>上传资料</span></li>
							<li class="step" id="tab-5"><i>5</i><span>融资申请</span></li>
							<li class="step" id="tab-6"><i>6</i><span>担保函</span></li>
							<li class="" id="tab-7"><i>7</i><span>签订协议</span></li>
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
									<div class="div_imglook" id="div_imglook_1">
										<c:if test="${not empty annexAgreement.url}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-1" href=""><img class="example-image" src="${staticPath}/upload/image/${annexAgreement.url}"><input class="delete" type="hidden" value="${annexAgreement.id}"></a><div class="lookimg_delBtn" id="${annexAgreement.id}">移除</div></div>
										</c:if>
									</div>

									<!--错误提示-->
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
									<div class="invoice_wrap">
										<table border="" cellspacing="" cellpadding="">
											<tr>
												<th>订单号</th>
												<th>订单金额</th>
												<th>订单影印件</th>
											</tr>
											<tr class="invoice_group">
												<td class="voucherNoTd"><input type="number" name="" class="voucherNo"
													value="" placeholder="请输入订单号" /></td>
												<td class="voucherMoneyTd"><input type="text" name="" class="voucherMoney" value="" placeholder="请输入订单金额" /></td>
												<td class="voucherFile">
													<div class="file_box">
													    
													    <div class="file_button"  id='textfieldo'>上传文件</div>
													    <input type="file" name="fileField" class="file_input fileo" id="2" />
													</div>
												</td>
												<td class="voucherEnd" style="display: none"><a href=""
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
									<b class="pull-left">3.历史交易发票*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/fapiao.jpg"
										class="example-image-link" data-lightbox="example-60"><img
											src="${ctxStatic}/images/fapiao.jpg" class="example-image"
											style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap">
									<div class="flie_wrap" id="flie_wrap_6">
										<div class="div_imgfile" id="div_imgfile_6"></div>
										<input type="file" accept="image/png,image/jpg" name="file"
											id="6" class="file" multiple="multiple">
									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_6"></div>

									<div class="info_error_msg">图片大小不符</div>
								</div>
								
								<dd class="even">
									<b class="pull-left">4.定金转账凭证*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
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
									<div class="flie_wrap picture" id="flie_wrap_1">

										<div class="div_imgfile" id="div_imgfile_1"></div>

										<input type="file" accept="image/png,image/jpg" name="file"
											id="1" multiple="multiple" class="file">

									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_1">
										<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
											<c:if test="${creditAnnexFile.type == '1'}">
												<div><a class="lookimg_wrap example-image-link" data-lightbox="example-1" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
											</c:if>
										</c:forEach>
									</div>

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
									<div class="invoice_wrap">
										<table border="" cellspacing="" cellpadding="">
											<tr>
												<th>订单号</th>
												<th>订单金额</th>
												<th>订单影印件</th>
											</tr>
												<tr class="invoice_group" id="orderOld">
													<td class="voucherNoTd"><input type="number" name="" class="voucherNo"
														value="${creditOrder.no }" /></td>
													<td class="voucherMoneyTd"><input type="text" name="" class="voucherMoney" value="${creditOrder.money }" /></td>
													<td class="voucherFile" style="display:none">
														<div class="file_box">
														    
														    <div class="file_button"  id='textfieldo'>上传文件</div>
														    <input type="file" name="fileField" class="file_input fileo" id="2" />
														</div>
													</td>
													<td class="voucherEnd"><a href="${staticPath}/upload/image/${creditOrder.url }"
														class="example-image-link" data-lightbox="example-6"
														data-title="" style="padding-right: 20px"><img
															class="example-image" src="${staticPath}/upload/image/${creditOrder.url }" style="display: none;">查看</a><span
														class="voucherDel">删除</span></td>
													<input type="hidden" class="voucherId" value="${creditOrder.id }" />
													<input type="hidden" class="annexFileId" value="${creditOrder.annexId }" />
												</tr>
												<tr class="invoice_group" id="orderNew">
													<td><input type="number" name="" class="voucherNo"
														value="" placeholder="请输入订单号" /></td>
													<td><input type="text" name="" class="voucherMoney" value="" placeholder="请输入订单金额" /></td>
													<td class="voucherFile">
														<div class="file_box">
														    
														    <div class="file_button"  id='textfieldo'>上传文件</div>
														    <input type="file" name="fileField" class="file_input fileo" id="2" />
														</div>
													</td>	
													<td class="voucherEnd"><a href=""
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
									<b class="pull-left">3.历史交易发票*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/fapiao.jpg"
										class="example-image-link" data-lightbox="example-60"><img
											src="${ctxStatic}/images/fapiao.jpg" class="example-image"
											style="display: none"></a>查看实例</span>
								</dd>
								<div class="imgfile_wrap">
									<div class="flie_wrap picture" id="flie_wrap_6">
										<div class="div_imgfile" id="div_imgfile_6"></div>
										<input type="file" accept="image/png,image/jpg" name="file"
											id="6" class="file" multiple="multiple">
									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_6">
										<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
											<c:if test="${creditAnnexFile.type == '6'}">
												<div><a class="lookimg_wrap example-image-link" data-lightbox="example-5" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
											</c:if>
										</c:forEach>
									</div>

									<div class="info_error_msg">图片大小不符</div>
								</div>
								
								<dd class="even">
									<b class="pull-left">4.定金转账凭证*</b><span class="pull-left">若为建设工程合同，必须包含合同封面、盖章页、包含工程造价、支付方式的页面。</span>
									<span class="pull-right"><a
										href="${ctxStatic}/images/duizhangdan.png"
										class="example-image-link" data-lightbox="example-50"><img
											src="${ctxStatic}/images/duizhangdan.png"
											class="example-image" style="display: none"></a>查看实例</span>

								</dd>
								<div class="imgfile_wrap">
									<div class="flie_wrap picture" id="flie_wrap_5">
										<div class="div_imgfile" id="div_imgfile_5"></div>
										<input type="file" accept="image/png,image/jpg" name="file"
											id="5" class="file" multiple="multiple">
									</div>

									<!--图片预览容器-->
									<div class="div_imglook" id="div_imglook_5">
										<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
											<c:if test="${creditAnnexFile.type == '5'}">
												<div><a class="lookimg_wrap example-image-link" data-lightbox="example-5" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
											</c:if>
										</c:forEach>
									</div>

									<div class="info_error_msg">图片大小不符</div>
								</div>

							</dl>

						</div>
						<button class="btn btn_four" onclick="toSave();">保存</button>
						<button class="btn btn_four" onclick="toSubmit();">提交</button> 
						<div id="messageBox" class="alert" style="display: none;color: red;">缺少必要参数</div>
					</div>
				</div>
			</div>
		</div>

</body>

</html>