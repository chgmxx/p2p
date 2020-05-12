<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
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
	<script src="${ctxStatic}/js/ProJson.js" type="text/javascript"></script>
	<script src="${ctxStatic}/js/CityJson.js" type="text/javascript"></script>
	<style>
		.lookimg_delBtn{
		   z-index:10
		}
		   

		.div_imglook >div:hover .lookimg_delBtn {
		display: block!important;
		}
		.loan_apply_wrap{
		    padding: 0 0 20px 0px;
		}
		.loan_apply{
		    padding: 0 20px 0 0;
		 
		}
		.mask_gray{
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
		.mask_tip{
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
	</style>
	<script type="text/javascript">
  		var ctxpath = '${ctxpath}';
		var middlemenId ;//代偿户
		var creditUserId = '${creditUserInfo.id}';
		var businessLicenseType;
		$(function(){
								
			//核心企业id
			/* $("#coreCompanyList").change(function(){
				middlemenId = $("#coreCompanyList").val();
			}); */

			//抵押业务处理
/* 			$("#bizType").change(function(){
				$bizType=$("#bizType").find("option:selected").textla_step();
				if($bizType=='抵押业务' || $bizType=='核心企业'){
					$("#coreCompany").hide();
					middlemenId = ${creditUserInfo.id};
				}else{
					$("#coreCompany").show();
					middlemenId = $("#coreCompanyList").val();
				}
			}); */
			$("#businessLicenseType").change(function(){
				closeMessage();
				businessLicenseType = $("#businessLicenseType").val();
				if(businessLicenseType == "USCC"){
					$("#taxRegCertNo").parent().hide();
					$("#taxRegCertNoA").hide();
					$("#orgCode").parent().hide();
					$("#orgCodeA").hide();
				}else{
					$("#taxRegCertNoA").show();
					$("#taxRegCertNo").parent().show();
					$("#orgCodeA").show();
					$("#orgCode").parent().show();
				}
			})
			
			$("#businessLicenseType2").change(function(){
				closeMessage();
				businessLicenseType = $("#businessLicenseType2").val();
				if(businessLicenseType == "USCC"){
					$("#taxRegCertNo2").parent().hide();
					$("#taxRegCertNoA2").hide();
					$("#orgCode2").parent().hide();
					$("#orgCodeA2").hide();
				}else{
					$("#taxRegCertNoA2").show();
					$("#taxRegCertNo2").parent().show();
					$("#orgCodeA2").show();
					$("#orgCode2").parent().show();
				}
			})
				
			//-----------------------------省市二级联动-------------------------------------------------
			$.each(province, function (k, p) { 
		        var option = "<option value='" + p.code + "'>" + p.name + "</option>";
		        $("#bankProvince").append(option);
		        $("#bankProvince2").append(option);
		    });
			var selValue = $("#bankProvince").val();
		    $.each(city, function (k, p) { 
		        if (p.ProID == selValue) {
		            var option = "<option value='" + p.name + "'>" + p.name + "</option>";
		            $("#bankCity").append(option);
		            $("#bankCity2").append(option);
		        }
		    });
		    $("#bankProvince").change(function () {
		    	closeMessage();
		        var selValue = $(this).val(); 
		        $("#bankCity option").remove();         
		        $.each(city, function (k, p) { 
		            if (p.ProID == selValue) {
		                var option = "<option value='" + p.name + "'>" + p.name + "</option>";
		                $("#bankCity").append(option);
		            }
		        });         
		    });  
		    $("#bankProvince2").change(function () {
		    	closeMessage();
		        var selValue = $(this).val(); 
		        $("#bankCity2 option").remove();         
		        $.each(city, function (k, p) { 
		            if (p.ProID == selValue) {
		                var option = "<option value='" + p.name + "'>" + p.name + "</option>";
		                $("#bankCity2").append(option);
		            }
		        });         
		    });
			//-----------------------------省市二级联动结束------------------------------------------------	
			
			$(".flie_wrap .div_imgfile").click(function() {
				closeMessage();
				$(this).siblings(".file").click();
	
			});
				
			var on = document.querySelector(".div_imglook");
//	    需要把阅读的文件传进来file element是把读取到的内容放入的容器
			function readFile(file, element, id,type) {
				var reader = new FileReader();
				//        根据文件类型选择阅读方式
				switch(file.type) {
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
					switch(file.type) {
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
							var img_wrap = $("<div><a class='lookimg_wrap example-image-link' data-lightbox='example-"+type+"' href='"+reader.result+"'><img class='example-image' src='"+reader.result+"'/><input class='delete' type='hidden' value='"+id+"'/></a><div class='lookimg_delBtn' id="+id+">移除</div><div class='tit_pic'>"
									+ file.name + "</div>WW</div>");
// 							img_wrap.append(input);
							element.append(img_wrap);
							element.show();
							$("#" + id).click(function() {
								var $this = $(this);
								console.log("id = " + id );
								deleteCreditInfo($this, id);
							});
							break;
					}
				});
			}

			$(".lookimg_delBtn").click(function(){
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
				if(type==8){
					if(count==1){
						$this.parent().siblings(".info_error_msg").show().html("最多只能上传一张图片！");
						return;
					}
				}
				if(type==9){
					if(count==1){
						$this.parent().siblings(".info_error_msg").show().html("最多只能上传一张图片！");
						return;
					}
				}
				if(type==10){
					if(count==2){
						$this.parent().siblings(".info_error_msg").show().html("最多只能上传两张图片！");
						return;
					}
				}
				
				//	var file = document.getElementById("file1").files;
				formData.append("type", type);
				formData.append("creditInfoId", creditUserId);
				formData.append("file1", file);
				$.ajax({
					url: ctxpath + "/creditInfo/uploadCreditInfo",
					type: 'post',
					dataType: 'json',
					data: formData,
					// 告诉jQuery不要去处理发送的数据
					processData: false,
					// 告诉jQuery不要去设置Content-Type请求头
					contentType: false,
					success: function(result) {
						if(result.state == 0) {
							var annexFileId = result.annexFileId;
	
							readFile(file, $this.parent().siblings(".div_imglook"), annexFileId,type);
							
	
	
						} 
	
					}
				});
	
			})

			$("#modifyBankCard").click(function(){
				closeMessage();
				$("#creditBankCard").hide();
				$("#creditBankCard2").hide();
				$("#modifyBankCardView").show();
			})
			
		});


	function deleteCreditInfo($this, id) {
		closeMessage();
		$.ajax({
			url: ctxpath + "/creditInfo/deleteCredit",
			type: 'post',
			dataType: 'json',
			data: {
				id:creditUserId,
				annexFileId:id
			},
			success: function(result) {
				if(result.state == 0) {
// 					$this.parent().parent().siblings(".info_error_msg").hide();
					$this.parent().remove();

				}else{
					$("#messageBox").show().html(result.message);
				} 

			}
		});
	}

	function checkEmail(myemail){
		var myReg=/^[a-zA-Z0-9_-]+@([a-zA-Z0-9]+\.)+(com|cn|net|org)$/;
		 
		if(myReg.test(myemail)){
			return true;
		}else{
			$("#messageBox").show().html("邮箱格式不对!");
			return false;
		}
	}

		function openAccount(type){
			closeMessage();
			if(type == 1){
				$("#openAccount").hide();
				businessLicenseType = $("#businessLicenseType").val();
				var creditUserId = "${creditUserInfo.id}";
				var bizType = $("#bizType").val();
				var enterpriseFullName = $("#enterpriseFullName").val() ;
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
				var bankCardName = enterpriseFullName ;
				var bankProvince = $("#bankProvince").val();
				var bankCity = $("#bankCity").val();
				var issuerName = $("#issuerName").val();
				var issuer = $("#issuer").val();
				var email = $("#email").val();
				var registAddress = $("#registAddress").val();

				if(bizType==null || bizType.trim()=="" || bizType==00){
					$("#messageBox").show().html("请选择账户类型");
					$("#openAccount").show();
					return;
				}
				if(enterpriseFullName==null || enterpriseFullName.trim()==""){
					$("#messageBox").show().html("请填写企业全称");
					$("#openAccount").show();
					return;
				}
				if(businessLicenseType==null || businessLicenseType.trim()==""){
					$("#messageBox").show().html("请选择证照类型");
					$("#openAccount").show();
					return;
				}
				if(businessLicense==null || businessLicense.trim()==""){
					$("#messageBox").show().html("请填写证照号");
					$("#openAccount").show();
					return;
				}
				if(businessLicenseType=="BLC"){
					if(taxRegCertNo==null || taxRegCertNo.trim()==""){
						$("#messageBox").show().html("请填写税务登记证");
						$("#openAccount").show();
						return;
					}
					if(orgCode==null || orgCode.trim()==""){
						$("#messageBox").show().html("请填写组织机构代码");
						$("#openAccount").show();
						return;
					}
				}

				if(agentPersonName==null || agentPersonName.trim()==""){
					$("#messageBox").show().html("请填写联系人姓名");
					$("#openAccount").show();
					return;
				}
				if(agentPersonCertType==null || agentPersonCertType.trim()==""){
					$("#messageBox").show().html("请选择联系人证件类型");
					$("#openAccount").show();
					return;
				}
				if(agentPersonCertNo==null || agentPersonCertNo.trim()==""){
					$("#messageBox").show().html("请填写联系人证件号");
					$("#openAccount").show();
					return;
				}
				
				if(agentPersonPhone==null || agentPersonPhone.trim()==""){
					$("#messageBox").show().html("请填写联系人手机号");
					$("#openAccount").show();
					return;
				}
				
				
				if(bankPermitCertNo==null || bankPermitCertNo.trim()==""){
					$("#messageBox").show().html("请填写银行开户许可证编号");
					$("#openAccount").show();
					return;
				}
				
				if(corporationName==null || corporationName.trim()==""){
					$("#messageBox").show().html("请填写法人姓名");
					$("#openAccount").show();
					return;
				}
				if(corporationCertType==null || corporationCertType.trim()==""){
					$("#messageBox").show().html("请选择法人证件类型");
					$("#openAccount").show();
					return;
				}
				if(corporationCertNo==null || corporationCertNo.trim()==""){
					$("#messageBox").show().html("请填写法人证件号");
					$("#openAccount").show();
					return;
				}
				
				if(bankName==null || bankName.trim()==""){
					$("#messageBox").show().html("请填写银行名称");
					$("#openAccount").show();
					return;
				}
//				if(bankCode==null || bankCode.trim()==""){
//					$(".error_msg").show().html("请填写银行编码");
//					return;
//				}
				if(bankCardNo==null || bankCardNo.trim()==""){
					$("#messageBox").show().html("请填写银行账号");
					$("#openAccount").show();
					return;
				}
//				if(bankCardName==null || bankCardName.trim()==""){
//					$(".error_msg").show().html("请填写银行开户名");
//					return;
//				}
				if(bankProvince==null || bankProvince.trim()==""){
					$("#messageBox").show().html("请选择开户城市（省）");
					$("#openAccount").show();
					return;
				}
				if(bankCity==null || bankCity.trim()==""){
					$("#messageBox").show().html("请选择开户城市（市）");
					$("#openAccount").show();
					return;
				}
				if(issuerName==null || issuerName.trim()==""){
					$("#messageBox").show().html("请填写支行名称");
					$("#openAccount").show();
					return;
				}
				if(issuer==null || issuer.trim()==""){
					$("#messageBox").show().html("请填写支行-联行号");
					$("#openAccount").show();
					return;
				}
				if(email==null || email.trim()==""){
					$("#messageBox").show().html("请填写邮箱");
					$("#openAccount").show();
					return;
				}
				if(registAddress==null || registAddress.trim()==""){
					$("#messageBox").show().html("请填写注册地址");
					$("#openAccount").show();
					return;
				}
				//邮箱有效性验证
				if(email != null && !email == ""){
					if(!checkEmail(email)){
						$("#openAccount").show();
						return ;
					}
				}
			}else{
				$("#openAccount2").hide();
				businessLicenseType = $("#businessLicenseType2").val();
				var creditUserId = "${creditUserInfo.id}";
				var bizType = "${creditUserInfo.creditUserType}";
				var enterpriseFullName = $("#enterpriseFullName2").val() ;
				var businessLicense = $("#businessLicense2").val();
				var taxRegCertNo = $("#taxRegCertNo2").val();
				var orgCode = $("#orgCode2").val();
				var bankPermitCertNo = $("#bankPermitCertNo2").val();
				var agentPersonName = $("#agentPersonName2").val();
				var agentPersonCertType = $("#agentPersonCertType2").val();
				var agentPersonCertNo = $("#agentPersonCertNo2").val();
				var agentPersonPhone = $("#agentPersonPhone2").val();
				var corporationName = $("#corporationName2").val();
				var corporationCertType = $("#corporationCertType2").val();
				var corporationCertNo = $("#corporationCertNo2").val();
				var bankName = $("#bankName2").children('option:selected').html();
				var bankCode = $("#bankName2").val();
				var bankCardNo = $("#bankCardNo2").val();
				var bankCardName = enterpriseFullName ;
				var bankProvince = $("#bankProvince2").val();
				var bankCity = $("#bankCity2").val();
				var issuerName = $("#issuerName2").val();
				var issuer = $("#issuer2").val();
				var email = $("#email2").val();
				var registAddress = $("#registAddress2").val();
				
				if(businessLicenseType=="00"){
					$("#messageBox").show().html("请选择证照类型");
					$("#openAccount2").show();
					return;
				}
				if(corporationCertType=="00"){
					$("#messageBox").show().html("请选择法人证件类型");
					$("#openAccount2").show();
					return;
				}
				if(bankName=="请选择"){
					$("#messageBox").show().html("请选择银行");
					$("#openAccount2").show();
					return;
				}
				if(bankProvince=="00"){
					$("#messageBox").show().html("请选择开户城市（省）");
					$("#openAccount2").show();
					return;
				}
				if(bankCity=="00"){
					$("#messageBox").show().html("请选择开户城市（市）");
					$("#openAccount2").show();
					return;
				}
				if(agentPersonCertType=="00"){
					$("#messageBox").show().html("请选择联系人证件类型");
					$("#openAccount2").show();
					return;
				}
				
				if(businessLicenseType=="${wloanSubject.businessLicenseType}"){
					businessLicenseType=null;
				}
				if(enterpriseFullName=="${wloanSubject.companyName }"){
// 					enterpriseFullName=null;
					bankCardName = null;
				}
				if(businessLicense=="${wloanSubject.businessNo}"){
					businessLicense=null;
				}
				if(taxRegCertNo=="${wloanSubject.taxCode}"){
					taxRegCertNo=null;
				}
				if(orgCode=="${wloanSubject.organNo}"){
					orgCode=null;
				}
				if(bankPermitCertNo=="${wloanSubject.bankPermitCertNo}"){
					bankPermitCertNo=null;
				}
				if(agentPersonName=="${wloanSubject.agentPersonName}"){
					agentPersonName=null;
				}
				if(agentPersonCertType=="${wloanSubject.agentPersonCertType}"){
					agentPersonCertType=null;
				}
				if(agentPersonCertNo=="${wloanSubject.agentPersonCertNo}"){
					agentPersonCertNo=null;
				}
				if(agentPersonPhone=="${wloanSubject.agentPersonPhone}"){
					agentPersonPhone=null;
				}
				if(corporationName=="${wloanSubject.loanUser }"){
					corporationName=null;
				}
				if(corporationCertType=="${wloanSubject.corporationCertType}"){
					corporationCertType=null;
				}
				if(corporationCertNo=="${wloanSubject.corporationCertNo}"){
					corporationCertNo=null;
				}
				if(bankName=="${wloanSubject.loanBankName}"){
					bankName=null;
					issuerName==null;
					issuer==null;
				}else{
					if(issuerName==null || issuerName.trim()==""){
						$("#messageBox").show().html("请填写支行名称");
						$("#openAccount2").show();
						return;
					}
					if(issuer==null || issuer.trim()==""){
						$("#messageBox").show().html("请填写支行-联行号");
						$("#openAccount2").show();
						return;
					}
				}
				if(bankCardNo=="${wloanSubject.loanBankNo}"){
					bankCardNo=null;
				}
				if(email=="${wloanSubject.email}"){
					email=null;
				}
				if(registAddress=="${wloanSubject.registAddress}"){
					registAddress=null;
				}
				//邮箱有效性验证
				if(email != null && !email == ""){
					if(!checkEmail(email)){
						$("#openAccount2").show();
						return ;
					}
				}
				
			}
			
			

			$(".mask_gray").show();
			$(".mask_tip").show();
			
			if(type == 1){
				$.ajax({
					url: ctxpath + "/cgbPay/accountCreateByCompanyForErp",
					type: "post",
					dataType: "json",
					data: {
						id:creditUserId,
						bizType: bizType,
						supplierId:creditUserId,
						enterpriseFullName: enterpriseFullName,
						businessLicenseType: businessLicenseType,
						businessLicense: businessLicense,
						taxRegCertNo: taxRegCertNo,
						orgCode: orgCode,
						bankPermitCertNo: bankPermitCertNo,
						agentPersonName: agentPersonName,
						agentPersonCertType: agentPersonCertType,
						agentPersonCertNo: agentPersonCertNo,
						agentPersonPhone: agentPersonPhone,
						corporationName: corporationName,
						corporationCertType: corporationCertType,
						corporationCertNo: corporationCertNo,
						bankName: bankName,
						bankCode: bankCode,
						bankCardNo: bankCardNo,
						bankCardName: bankCardName,
						bankProvince: bankProvince,
						bankCity: bankCity,
						issuerName: issuerName,
						issuer: issuer,
						email:email,
						registAddress:registAddress
					},
					success: function(result) {
						$(".mask_gray").hide();
						$(".mask_tip").hide();
						console.log(result);
						//console.log(result.message);
						if(result.state == 0) {
							console.log("成功");
							var data = result.data;
							var tm = data.tm;
							var merchantId = data.merchantId;

							window.location.href = "${cgbpath}?data=" + data.data + "&tm=" + tm + "&merchantId=" + merchantId;
						}else{
							$("#messageBox").show().html(result.message);
							$("#openAccount").show();
						}
					}
				});
			}else{
				$.ajax({
					url: ctxpath + "/app/updateenterprise",
					type: "post",
					dataType: "json",
					data: {
						id:creditUserId,
						bizType: bizType,
						supplierId:creditUserId,
						enterpriseFullName: enterpriseFullName,
						businessLicenseType: businessLicenseType,
						businessLicense: businessLicense,
						taxRegCertNo: taxRegCertNo,
						orgCode: orgCode,
						bankPermitCertNo: bankPermitCertNo,
						agentPersonName: agentPersonName,
						agentPersonCertType: agentPersonCertType,
						agentPersonCertNo: agentPersonCertNo,
						agentPersonPhone: agentPersonPhone,
						corporationName: corporationName,
						corporationCertType: corporationCertType,
						corporationCertNo: corporationCertNo,
						bankName: bankName,
						bankCode: bankCode,
						bankCardNo: bankCardNo,
						bankCardName: bankCardName,
						bankProvince: bankProvince,
						bankCity: bankCity,
						issuerName: issuerName,
						issuer: issuer,
						email:email,
						registAddress:registAddress
					},
					success: function(result) {
						$(".mask_gray").hide();
						$(".mask_tip").hide();
						console.log(result);
						//console.log(result.message);
						if(result.state == 0) {
							console.log("成功");
							location.reload();
						}else{
							$("#messageBox").show().html(result.message);
							$("#openAccount2").show();
						}
					}
				});
			}			
		}

		function closeMessage(){
			$("#messageBox").hide();
		}
		
		
		
	</script>
	<style type="text/css">
	   table.table td{
	    padding:10px 5px!important;
	        vertical-align: middle!important;
	   }
	      table.table td input{
	      height:30px;
	      width:200px;
	      }
	       table.table td select{ width:200px; }
	       .alert{
	       padding:0!important}

	       table{
			  border-collapse:collapse;
		    border:none
			  }


	.table-bordered>thead>tr>th, .table-bordered>thead>tr>td{
	border-bottom-width: 0;
	}
	.loan_apply{
	padding:0}

	.loan_apply_wrap{
	padding-top:0
	}
	.info_basic_wrap dl dd{
	 padding-left:0
	}

   table.table th{
    padding:10px 3px!important;
        vertical-align: middle!important;
        font-weight:normal;
   }
.table-condensed th input{
    border: 0;
    background: none;
        color: #36a7e7;
}


.loan_apply .nav_head{
  padding-top:15px	
}
	</style>
</head>
<body>
<div class="loan_apply_wrap_02">
   <div class="nav_head">开户信息</div>	
   
	<div id="messageBox" class="alert alert-success " style="display: none;">缺少必要参数</div>
	<c:if test="${userBankCard.state == '2'}">
		<div>
		<table class="table table-striped table-bordered table-condensed" >
			<input type="text" class="error_msg" style="display: none">
			<thead><tr><th>开户信息</th><th>当前状态：开户中...</th><th><input type="button" id="openAccount" value="提交" onclick="openAccount(1);"/></th><th></th></tr></thead>
			<tbody>
		
				<tr>
					<td>账户类型</td>
					<td>
							<select name="bizType" id="bizType">
								<option value="00">请选择</option>
								<option value="02">供应商</option>
								<option value="11">核心企业</option>
								<option value="15">抵押业务</option>
							</select>
					</td>
					<td>联系人证件号</td>
					<td>
							<input type="text" name="" value=""  id="agentPersonCertNo">
					</td>
				</tr>
				<tr>
					<%-- <td>核心企业</td>
					<td>
						<div id="coreCompany">
							
	
							<select name="coreCompanyList" id="coreCompanyList">
								<option value="00">请选择</option>
								<!--<option value="01">美特好</option>-->
								<c:forEach items="${returnList}" var="creditUser">
									<option value="${creditUser.middlemenId}">${creditUser.middlemenName}</option>-->
								</c:forEach>
							</select>
						</div>
					</td> --%>
					<td>联系人手机号</td>
					<td>
							<input type="text" name="" value="" id="agentPersonPhone">
					</td>
				</tr>
				<tr>
					<td>企业全称</td>
					<td>
						
							<input type="text" id="enterpriseFullName" name="" value="" >
					
					</td>
					<td>法人姓名</td>
					<td>
						
							<input type="text" name="" value="" id="corporationName">
					
					</td>
				</tr>
				<tr>
					<td>证照类型</td>
					<td>
						
							<select name="" id="businessLicenseType">
								<option value="BLC">BLC-营业执照</option>
								<option value="USCC">USCC-统一社会信用代码</option>
							</select>
					</td>
					<td>法人证件类型</td>
					<td>
						
							<select name="" id="corporationCertType">
								<option value="IDC">IDC-身份证</option>
								<option value="GAT">GAT-港澳台身份证</option>
								<option value="MILIARY">MILIARY-军官证</option>
								<option value="PASS_PORT">PASS_PORT-护照</option>
							</select>
						
					</td>
				</tr>
				<tr>
					<td>证照号</td>
					<td>
					
					
							<input type="text" name="" value=""  id="businessLicense">
					
					</td>
					<td>法人证件号</td>
					<td>
						
							<input type="text" name="" value=""  id="corporationCertNo">
					
					</td>
				</tr>
				<tr>
					<td id="taxRegCertNoA">税务登记证</td>
					<td>
						
							<input type="text" id="taxRegCertNo" name="" value="" >
					
					</td>
					<td>银行名称</td>
					<td>
						
				
							 <select id="bankName" >
							 	<option value="b7f3cf26bb2e4fe99bc6237f529968a3">海口联合农商银行</option>
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
							 </select>  
							<!--<input type="text" name="" value="" class="fr" id="bankName">-->
					
					</td>
				</tr>
				<tr>
					<td id="orgCodeA">组织机构代码</td>
					<td>
						
							<input type="text" id="orgCode" name="" value="" >
					
					</td>
					<td>银行账号</td>
					<td>
						
							<input type="text" name="" value=""  id="bankCardNo">
						
					</td>
				</tr>
				<tr>
					<td>银行开户许可证编号</td>
					<td>
							<input type="text" name="" id="bankPermitCertNo" value="">
					
					</td>
					<td>开户城市</td>
					<td>
						
							
							     <select id="bankProvince" >
							     	<option value="">请选择</option>
							     </select>      
	                                   <select id="bankCity">
	                                   	<option value="">请选择</option>
	                                   </select>
	                                  </div>
					
					</td>
				</tr>
				<tr>
					<td>联系人姓名</td>
					<td>
						
							
							<input type="text" name="" id="agentPersonName" value="">
					
					</td>
					<td>支行名称</td>
					<td>
						
						<input type="text" name="" value="" id="issuerName">
				
				</td>
			</tr>
			<tr>
				<td>联系人证件类型</td>
				<td>
					
						<select name="" id="agentPersonCertType">
							<option value="IDC">IDC-身份证</option>
							<option value="GAT">GAT-港澳台身份证</option>
							<option value="MILIARY">MILIARY-军官证</option>
							<option value="PASS_PORT">PASS_PORT-护照</option>
						</select>
				
				</td>
				<td>支行-联行号</td>
				<td>
						<input type="text" name="" value=""  id="issuer">
				</td>
			</tr>
			<tr>
				<td>邮箱</td>
				<td>
					
						<input type="text" name="" value=""  id="email"/>
				
				</td>
				<td>注册地址</td>
				<td>
						<input type="text" name="" value=""  id="registAddress"/>
				</td>
			</tr>
			
			
		</tbody>
		</table>
		
	</div>
	<div class="loan_apply">
		<!-- <h1>附加信息<span>创建时间:2018-03-12</span></h1> -->
		<div class="loan_apply_wrap">
 			<div class="nav_head">附加信息</div>	
			<div class="la_con">

				<div class="la_step la_step_four cur">

					<div class="info_basic_wrap">
						<dl class="font_size18">

							<dd class="even"><b class="pull-left">1.营业执照</b> <span class="pull-left">支持图片格式</span><span class="pull-right"><a href="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image-link" data-lightbox="example-10"><img src="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>

							<div class="imgfile_wrap">
								<div class="flie_wrap" id="flie_wrap_1">

									<div class="div_imgfile" id="div_imgfile_1"></div>

									<input type="file" accept="image/png,image/jpg" name="file" id="8" multiple="multiple" class="file">

								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_8">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '8'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-8" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
										</c:if>
									</c:forEach>
								</div>

								<!--确定上传按钮-->
								<!--<input type="button" value="确定上传" id="btn_ImgUpStart_1" class="fr submit_btn" />-->
								<div class="info_error_msg" id="info_error_msg_1">图片大小不符</div>
							</div>
							<dd class="even"><b class="pull-left">2.银行开户许可证(一张)</b><span class="pull-left"></span><span class="pull-right"><a href="${ctxStatic}/images/openPermit.jpg" class="example-image-link" data-lightbox="example-20"><img src="${ctxStatic}/images/openPermit.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>
							<div class="imgfile_wrap">
								<div class="flie_wrap">
									<div class="div_imgfile"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="9" class="file" />
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_9">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '9'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-9" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
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
								<div class="flie_wrap" id="flie_wrap_2">
									<div class="div_imgfile" id="div_imgfile_2"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="10" class="file">
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_10">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '10'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-10" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
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
	<%-- <div class="loan_apply">
		<h1>附加信息<span>创建时间:2018-03-12</span></h1>
		<div class="loan_apply_wrap">

			<div class="la_con">

				<div class="la_step la_step_four cur">

					<div class="info_basic_wrap">
						<dl class="font_size18">

							<dd class="even"><b class="pull-left">1.营业执照</b> <span class="pull-left">支持上传jpg或png格式</span><span class="pull-right" data-toggle="modal" data-target="#myModal01">查看实例</span></dd>

							<div class="imgfile_wrap">
								<div class="flie_wrap" id="flie_wrap_1">

									<div class="div_imgfile" id="div_imgfile_1"></div>

									<input type="file" accept="image/png,image/jpg" name="file" id="8" multiple="multiple" class="file">

								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_8">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '8'}">
											<div class="lookimg_wrap"><div class="lookimg_delBtn">移除</div><img src="http://cicmorgan.com/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></div>
										</c:if>
									</c:forEach>
								</div>

								<!--确定上传按钮-->
								<!--<input type="button" value="确定上传" id="btn_ImgUpStart_1" class="fr submit_btn" />-->
								<div class="info_error_msg" id="info_error_msg_1">图片大小不符</div>
							</div>
							<dd class="even"><b class="pull-left">2.法人身份证</b><span class="pull-left">上传带有公章的正反两面，支持上传jpg或png格式</span><span class="pull-right" data-toggle="modal" data-target="#myModal02">查看实例</span></dd>
							<div class="imgfile_wrap">
								<div class="flie_wrap" id="flie_wrap_2">
									<div class="div_imgfile" id="div_imgfile_2"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="10" class="file">
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_10">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '10'}">
											<div class="lookimg_wrap"><div class="lookimg_delBtn">移除</div><img src="http://cicmorgan.com/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></div>
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

					<button class="btn clear">保存</button>

				</div>

			</div>
		</div>
	</div> --%>
	</c:if>
	<c:if test="${userBankCard.state == '0'}">
		<form:form id="" modelAttribute="creditUserInfo" action="" method="post" class="form-horizontal">
		<table class="table table-striped table-bordered table-condensed" >
			<thead><tr><th>企业基本信息</th><th>当前状态：开户审核中</th></tr></thead>
			<tbody>
				<tr>
					<td>账户类型</td>
					<td>
						
							
							<c:if test="${creditUserInfo.creditUserType == '11'}">
								<label for="" class="fl">核心企业</label>
							</c:if>
							<c:if test="${creditUserInfo.creditUserType == '02'}">
								<label for="" class="fl">供应商</label>
							</c:if>
					
					</td>
					<td >联系人证件号</td>
					<td>
						
							<label for="" class="fl">${wloanSubject.agentPersonCertNo }</label>
						
					</td>
				</tr>
				<tr>
					
					
					<td>核心企业</td>
					<td>
					<div id="coreCompany">
						<label for="" class="fl">${creditUserInfo2.enterpriseFullName }</label>
					</div>
					</td>
					<td>联系人手机号</td>
					<td>
						<div >
							<label for="" class="fl">${wloanSubject.agentPersonPhone }</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>企业全称</td>
					<td>
						<div >
							<label for="" class="fl">${wloanSubject.companyName }</label>
						</div>
					</td>
					<td>法人姓名</td>
					<td>
						<div >
							<label for="" class="fl">${wloanSubject.loanUser }</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>证照类型</td>
					<td>
						<div>
							<c:if test="${wloanSubject.businessLicenseType == 'BLC'}">
								<label for="" class="fl">BLC-营业执照</label>
							</c:if>
							<c:if test="${wloanSubject.businessLicenseType == 'USCC'}">
								<label for="" class="fl">USCC-统一社会信用代码</label>
							</c:if>
						</div>
					</td>
					<td>法人证件类型</td>
					<td>
						<div >
							<c:if test="${wloanSubject.corporationCertType == 'IDC'}">
								<label for="" class="fl">IDC-身份证</label>
							</c:if>
							<c:if test="${wloanSubject.corporationCertType == 'GAT'}">
								<label for="" class="fl">GAT-港澳台身份证</label>
							</c:if>
							<c:if test="${wloanSubject.corporationCertType == 'MILIARY'}">
								<label for="" class="fl">MILIARY-军官证</label>
							</c:if>
							<c:if test="${wloanSubject.corporationCertType == 'PASS_PORT'}">
								<label for="" class="fl">PASS_PORT-护照</label>
							</c:if>
						</div>
					</td>
				</tr>
				<tr>
					<td>证照号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.businessNo}</label>
						</div>
					</td>
					<td>法人证件号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.corporationCertNo}</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>税务登记证</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.taxCode}</label>
						</div>
					</td>
					<td>银行名称</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.loanBankName}</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>组织机构代码</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.organNo}</label>
						</div>
					</td>
					<td>银行账号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.loanBankNo}</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>银行开户许可证编号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.bankPermitCertNo}</label>
						</div>
					</td>
					<td>联系人姓名</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.agentPersonName}</label>
						</div>
					</td>
					
				</tr>
			
			<tr>
				<td>联系人证件类型</td>
				<td>
					<div>
						<c:if test="${wloanSubject.agentPersonCertType == 'IDC'}">
								<label for="" class="fl">IDC-身份证</label>
							</c:if>
							<c:if test="${wloanSubject.agentPersonCertType == 'GAT'}">
								<label for="" class="fl">GAT-港澳台身份证</label>
							</c:if>
							<c:if test="${wloanSubject.agentPersonCertType == 'MILIARY'}">
								<label for="" class="fl">MILIARY-军官证</label>
							</c:if>
							<c:if test="${wloanSubject.agentPersonCertType == 'PASS_PORT'}">
								<label for="" class="fl">PASS_PORT-护照</label>
							</c:if>
					</div>
				</td>
				<td></td>
				<td>
					<div>
						<label for="" class="fl"></label>
					</div>
				</td> 
			</tr>
			<tr>
				<td>邮箱</td>
				<td>
					
						<label for="" class="fl">${wloanSubject.email}</label>
				
				</td>
				<td>注册地址</td>
				<td>
						<label for="" class="fl">${wloanSubject.registAddress}</label>
				</td>
			</tr>
			
		</tbody>
		</table>	
	</form:form>
	<div class="loan_apply">
		<!-- <h1>附加信息<span>创建时间:2018-03-12</span></h1> -->
		<div class="loan_apply_wrap">
 			<div class="nav_head">附加信息</div>	
			<div class="la_con">

				<div class="la_step la_step_four cur">

					<div class="info_basic_wrap">
						<dl class="font_size18">

							<dd class="even"><b class="pull-left">1.营业执照</b> <span class="pull-left">支持图片格式</span><span class="pull-right"><a href="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image-link" data-lightbox="example-10"><img src="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>

							<div class="imgfile_wrap">
								<div class="flie_wrap" id="flie_wrap_1">

									<div class="div_imgfile" id="div_imgfile_1"></div>

									<input type="file" accept="image/png,image/jpg" name="file" id="8" multiple="multiple" class="file">

								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_8">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '8'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-8" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
										</c:if>
									</c:forEach>
								</div>

								<!--确定上传按钮-->
								<!--<input type="button" value="确定上传" id="btn_ImgUpStart_1" class="fr submit_btn" />-->
								<div class="info_error_msg" id="info_error_msg_1">图片大小不符</div>
							</div>
							<dd class="even"><b class="pull-left">2.银行开户许可证(一张)</b><span class="pull-left"></span><span class="pull-right"><a href="${ctxStatic}/images/openPermit.jpg" class="example-image-link" data-lightbox="example-20"><img src="${ctxStatic}/images/openPermit.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>
							<div class="imgfile_wrap">
								<div class="flie_wrap">
									<div class="div_imgfile"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="9" class="file" />
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_9">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '9'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-9" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
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
								<div class="flie_wrap" id="flie_wrap_2">
									<div class="div_imgfile" id="div_imgfile_2"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="10" class="file">
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_10">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '10'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-10" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
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
	</c:if>
	<c:if test="${userBankCard.state == '1'}">
		<form:form id="creditBankCard" modelAttribute="creditUserInfo" action="" method="post" class="form-horizontal">
		<table class="table table-striped table-bordered table-condensed" >
			<thead><tr><th>企业基本信息</th><th>当前状态：已开户</th><th><input type="button" id="modifyBankCard" value="修改账户"></input></th><th></th></tr></thead>
			<tbody>
				<tr>
					<td>账户类型</td>
					<td>
						<div >
							
							<c:if test="${creditUserInfo.creditUserType == '11'}">
								<label for="" class="fl">核心企业</label>
							</c:if>
							<c:if test="${creditUserInfo.creditUserType == '02'}">
								<label for="" class="fl">供应商</label>
							</c:if>
						</div>
					</td>
					<td >联系人证件号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.agentPersonCertNo }</label>
						</div>
					</td>
				</tr>
				<tr>
					
					
					<td>核心企业</td>
					<td>
					<div id="coreCompany">
						<label for="" class="fl">${creditUserInfo2.enterpriseFullName }</label>
					</div>
					</td>
					<td>联系人手机号</td>
					<td>
						<div >
							<label for="" class="fl">${wloanSubject.agentPersonPhone }</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>企业全称</td>
					<td>
						<div >
							<label for="" class="fl">${wloanSubject.companyName }</label>
						</div>
					</td>
					<td>法人姓名</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.loanUser }</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>证照类型</td>
					<td>
						<div>
							<c:if test="${wloanSubject.businessLicenseType == 'BLC'}">
								<label for="" class="fl">BLC-营业执照</label>
							</c:if>
							<c:if test="${wloanSubject.businessLicenseType == 'USCC'}">
								<label for="" class="fl">USCC-统一社会信用代码</label>
							</c:if>
						</div>
					</td>
					<td>法人证件类型</td>
					<td>
						<div>
							<c:if test="${wloanSubject.corporationCertType == 'IDC'}">
								<label for="" class="fl">IDC-身份证</label>
							</c:if>
							<c:if test="${wloanSubject.corporationCertType == 'GAT'}">
								<label for="" class="fl">GAT-港澳台身份证</label>
							</c:if>
							<c:if test="${wloanSubject.corporationCertType == 'MILIARY'}">
								<label for="" class="fl">MILIARY-军官证</label>
							</c:if>
							<c:if test="${wloanSubject.corporationCertType == 'PASS_PORT'}">
								<label for="" class="fl">PASS_PORT-护照</label>
							</c:if>
						</div>
					</td>
				</tr>
				<tr>
					<td>证照号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.businessNo}</label>
						</div>
					</td>
					<td>法人证件号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.corporationCertNo}</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>税务登记证</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.taxCode}</label>
						</div>
					</td>
					<td>银行名称</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.loanBankName}</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>组织机构代码</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.organNo}</label>
						</div>
					</td>
					<td>银行账号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.loanBankNo}</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>银行开户许可证编号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.bankPermitCertNo}</label>
						</div>
					</td>
					<td>联系人姓名</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.agentPersonName}</label>
						</div>
					</td>
					
				</tr>
			
			<tr>
				<td>联系人证件类型</td>
				<td>
					<div>
						<c:if test="${wloanSubject.agentPersonCertType == 'IDC'}">
								<label for="" class="fl">IDC-身份证</label>
							</c:if>
							<c:if test="${wloanSubject.agentPersonCertType == 'GAT'}">
								<label for="" class="fl">GAT-港澳台身份证</label>
							</c:if>
							<c:if test="${wloanSubject.agentPersonCertType == 'MILIARY'}">
								<label for="" class="fl">MILIARY-军官证</label>
							</c:if>
							<c:if test="${wloanSubject.agentPersonCertType == 'PASS_PORT'}">
								<label for="" class="fl">PASS_PORT-护照</label>
							</c:if>
					</div>
				</td>
				<td></td>
				<td>
					<div>
						<label for="" class="fl"></label>
					</div>
				</td> 
			</tr>
			<tr>
				<td>邮箱</td>
				<td>
					
						<label for="" class="fl">${wloanSubject.email}</label>
				
				</td>
				<td>注册地址</td>
				<td>
						<label for="" class="fl">${wloanSubject.registAddress}</label>
				</td>
			</tr>
			
		</tbody>
		</table>	
	</form:form>
	<!--账户管理-->
	<div class="loan_apply" id="creditBankCard2">
		
		<div class="loan_apply_wrap">
<div class="nav_head">附加信息</div>	
			<div class="la_con">

				<div class="la_step la_step_four cur">

					<div class="info_basic_wrap">
						<dl class="font_size18">

						<dd class="even"><b class="pull-left">1.营业执照</b> <span class="pull-left">支持上传图片格式</span><span class="pull-right"><a href="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image-link" data-lightbox="example-10"><img src="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>

							<div class="imgfile_wrap">
								<div class="flie_wrap" id="flie_wrap_1">

									<div class="div_imgfile" id="div_imgfile_1"></div>

									<input type="file" accept="image/png,image/jpg" name="file" id="8" multiple="multiple" class="file">

								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_8">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '8'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-8" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
										</c:if>
									</c:forEach>
								</div>

								<!--确定上传按钮-->
								<!--<input type="button" value="确定上传" id="btn_ImgUpStart_1" class="fr submit_btn" />-->
								<div class="info_error_msg" id="info_error_msg_1">图片大小不符</div>
							</div>
							<dd class="even"><b class="pull-left">2.银行开户许可证(一张)</b><span class="pull-left"></span><span class="pull-right"><a href="${ctxStatic}/images/openPermit.jpg" class="example-image-link" data-lightbox="example-20"><img src="${ctxStatic}/images/openPermit.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>
							<div class="imgfile_wrap">
								<div class="flie_wrap">
									<div class="div_imgfile"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="9" class="file" />
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_9">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '9'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-9" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
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
								<div class="flie_wrap" id="flie_wrap_2">
									<div class="div_imgfile" id="div_imgfile_2"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="10" class="file">
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_10">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '10'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-10" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
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
	<div id="modifyBankCardView" style="display:none">
		<table class="table table-striped table-bordered table-condensed" >
			<input type="text" class="error_msg" style="display: none">
			<thead><tr><th>企业基本信息</th><th>当前状态：开户中...</th><th><input type="button" id="openAccount2" value="提交" onclick="openAccount(2);"/></th><th></th></tr></thead>
			<tbody>
		
				<tr>
					<td>联系人证件号</td>
					<td>
							<input type="text" name="" value="${wloanSubject.agentPersonCertNo}"  id="agentPersonCertNo2">
					</td>
					<td>联系人手机号</td>
					<td>
							<input type="text" name="" value="${wloanSubject.agentPersonPhone}" id="agentPersonPhone2">
					</td>
				</tr>
				<tr>
					<td>企业全称</td>
					<td>
						
							<input type="text" id="enterpriseFullName2" name="" value="${wloanSubject.companyName }" >
					
					</td>
					<td>法人姓名</td>
					<td>
						
							<input type="text" name="" value="${wloanSubject.loanUser }" id="corporationName2">
					
					</td>
				</tr>
				<tr>
					<td>证照类型</td>
					<td>
						
							<select name="" id="businessLicenseType2">
								<option value="00">请选择</option>
								<option value="BLC">BLC-营业执照</option>
								<option value="USCC">USCC-统一社会信用代码</option>
							</select>
					</td>
					<td>法人证件类型</td>
					<td>
						
							<select name="" id="corporationCertType2">
								<option value="00">请选择</option>
								<option value="IDC">IDC-身份证</option>
								<option value="GAT">GAT-港澳台身份证</option>
								<option value="MILIARY">MILIARY-军官证</option>
								<option value="PASS_PORT">PASS_PORT-护照</option>
							</select>
						
					</td>
				</tr>
				<tr>
					<td>证照号</td>
					<td>
					
					
							<input type="text" name="" value="${wloanSubject.businessNo}"  id="businessLicense2">
					
					</td>
					<td>法人证件号</td>
					<td>
						
							<input type="text" name="" value="${wloanSubject.corporationCertNo}"  id="corporationCertNo2">
					
					</td>
				</tr>
				<tr>
					<td id="taxRegCertNoA2">税务登记证</td>
					<td>
						
							<input type="text" id="taxRegCertNo2" name="" value="${wloanSubject.taxCode}" >
					
					</td>
					<td>银行名称</td>
					<td>
						
				
							 <select id="bankName2" >
							 	<option value="00">请选择</option>
							 	<option value="b7f3cf26bb2e4fe99bc6237f529968a3">海口联合农商银行</option>
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
							 </select>  
							<!--<input type="text" name="" value="" class="fr" id="bankName">-->
					
					</td>
				</tr>
				<tr>
					<td id="orgCodeA2">组织机构代码</td>
					<td>
						
							<input type="text" id="orgCode2" name="" value="${wloanSubject.organNo}" >
					
					</td>
					<td>银行账号</td>
					<td>
						
							<input type="text" name="" value="${wloanSubject.loanBankNo}"  id="bankCardNo2">
						
					</td>
				</tr>
				<tr>
					<td>银行开户许可证编号</td>
					<td>
							<input type="text" name="" id="bankPermitCertNo2" value="${wloanSubject.bankPermitCertNo}">
					
					</td>
					<td>开户城市</td>
					<td>
						
							
							     <select id="bankProvince2" >
							     	<option value="00">请选择</option>
							     </select>      
	                                   <select id="bankCity2">
	                                   	<option value="00">请选择</option>
	                                   </select>
	                                  </div>
					
					</td>
				</tr>
				<tr>
					<td>联系人姓名</td>
					<td>
						
							
							<input type="text" name="" id="agentPersonName2" value="${wloanSubject.agentPersonName}">
					
					</td>
					<td>支行名称</td>
					<td>
						
						<input type="text" name="" value="" id="issuerName2">
				
				</td>
			</tr>
			<tr>
				<td>联系人证件类型</td>
				<td>
					
						<select name="" id="agentPersonCertType2">
							<option value="00">请选择</option>
							<option value="IDC">IDC-身份证</option>
							<option value="GAT">GAT-港澳台身份证</option>
							<option value="MILIARY">MILIARY-军官证</option>
							<option value="PASS_PORT">PASS_PORT-护照</option>
						</select>
				
				</td>
				<td>支行-联行号</td>
				<td>
						<input type="text" name="" value=""  id="issuer2">
				</td>
			</tr>
			<tr>
				<td>邮箱</td>
				<td>
					
						<input type="text" name="" value="${wloanSubject.email}"  id="email2"/>
				
				</td>
				<td>注册地址</td>
				<td>
						<input type="text" name="" value="${wloanSubject.registAddress}"  id="registAddress2"/>
				</td>
			</tr>

			
		</tbody>
		</table>
			<!--账户管理-->
	<div class="loan_apply" id="creditBankCard2">
		
		<div class="loan_apply_wrap">
<div class="nav_head">附加信息</div>	
			<div class="la_con">

				<div class="la_step la_step_four cur">

					<div class="info_basic_wrap">
						<dl class="font_size18">

						<dd class="even"><b class="pull-left">1.营业执照</b> <span class="pull-left">支持上传图片格式</span><span class="pull-right"><a href="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image-link" data-lightbox="example-10"><img src="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>

							<div class="imgfile_wrap">
								<div class="flie_wrap" id="flie_wrap_1">

									<div class="div_imgfile" id="div_imgfile_1"></div>

									<input type="file" accept="image/png,image/jpg" name="file" id="8" multiple="multiple" class="file">

								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_8">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '8'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-8" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
										</c:if>
									</c:forEach>
								</div>

								<!--确定上传按钮-->
								<!--<input type="button" value="确定上传" id="btn_ImgUpStart_1" class="fr submit_btn" />-->
								<div class="info_error_msg" id="info_error_msg_1">图片大小不符</div>
							</div>
							<dd class="even"><b class="pull-left">2.银行开户许可证(一张)</b><span class="pull-left"></span><span class="pull-right"><a href="${ctxStatic}/images/openPermit.jpg" class="example-image-link" data-lightbox="example-20"><img src="${ctxStatic}/images/openPermit.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>
							<div class="imgfile_wrap">
								<div class="flie_wrap">
									<div class="div_imgfile"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="9" class="file" />
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_9">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '9'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-9" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
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
								<div class="flie_wrap" id="flie_wrap_2">
									<div class="div_imgfile" id="div_imgfile_2"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="10" class="file">
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_10">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '10'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-10" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
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
	</div>
	</c:if>
		<c:if test="${userBankCard.state == '3'}">
		<form:form id="creditBankCard" modelAttribute="creditUserInfo" action="" method="post" class="form-horizontal">
		<table class="table table-striped table-bordered table-condensed" >
			<thead><tr><th>企业基本信息</th><th>当前状态：银行未审核</th><th><input type="button" id="modifyBankCard" value="修改账户"></input></th><th></th></tr></thead>
			<tbody>
				<tr>
					<td>账户类型</td>
					<td>
						<div >
							
							<c:if test="${creditUserInfo.creditUserType == '11'}">
								<label for="" class="fl">核心企业</label>
							</c:if>
							<c:if test="${creditUserInfo.creditUserType == '02'}">
								<label for="" class="fl">供应商</label>
							</c:if>
						</div>
					</td>
					<td >联系人证件号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.agentPersonCertNo }</label>
						</div>
					</td>
				</tr>
				<tr>
					
					
					<td>核心企业</td>
					<td>
					<div id="coreCompany">
						<label for="" class="fl">${creditUserInfo2.enterpriseFullName }</label>
					</div>
					</td>
					<td>联系人手机号</td>
					<td>
						<div >
							<label for="" class="fl">${wloanSubject.agentPersonPhone }</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>企业全称</td>
					<td>
						<div >
							<label for="" class="fl">${wloanSubject.companyName }</label>
						</div>
					</td>
					<td>法人姓名</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.loanUser }</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>证照类型</td>
					<td>
						<div>
							<c:if test="${wloanSubject.businessLicenseType == 'BLC'}">
								<label for="" class="fl">BLC-营业执照</label>
							</c:if>
							<c:if test="${wloanSubject.businessLicenseType == 'USCC'}">
								<label for="" class="fl">USCC-统一社会信用代码</label>
							</c:if>
						</div>
					</td>
					<td>法人证件类型</td>
					<td>
						<div>
							<c:if test="${wloanSubject.corporationCertType == 'IDC'}">
								<label for="" class="fl">IDC-身份证</label>
							</c:if>
							<c:if test="${wloanSubject.corporationCertType == 'GAT'}">
								<label for="" class="fl">GAT-港澳台身份证</label>
							</c:if>
							<c:if test="${wloanSubject.corporationCertType == 'MILIARY'}">
								<label for="" class="fl">MILIARY-军官证</label>
							</c:if>
							<c:if test="${wloanSubject.corporationCertType == 'PASS_PORT'}">
								<label for="" class="fl">PASS_PORT-护照</label>
							</c:if>
						</div>
					</td>
				</tr>
				<tr>
					<td>证照号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.businessNo}</label>
						</div>
					</td>
					<td>法人证件号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.corporationCertNo}</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>税务登记证</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.taxCode}</label>
						</div>
					</td>
					<td>银行名称</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.loanBankName}</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>组织机构代码</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.organNo}</label>
						</div>
					</td>
					<td>银行账号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.loanBankNo}</label>
						</div>
					</td>
				</tr>
				<tr>
					<td>银行开户许可证编号</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.bankPermitCertNo}</label>
						</div>
					</td>
					<td>联系人姓名</td>
					<td>
						<div>
							<label for="" class="fl">${wloanSubject.agentPersonName}</label>
						</div>
					</td>
					
				</tr>
			
			<tr>
				<td>联系人证件类型</td>
				<td>
					<div>
						<c:if test="${wloanSubject.agentPersonCertType == 'IDC'}">
								<label for="" class="fl">IDC-身份证</label>
							</c:if>
							<c:if test="${wloanSubject.agentPersonCertType == 'GAT'}">
								<label for="" class="fl">GAT-港澳台身份证</label>
							</c:if>
							<c:if test="${wloanSubject.agentPersonCertType == 'MILIARY'}">
								<label for="" class="fl">MILIARY-军官证</label>
							</c:if>
							<c:if test="${wloanSubject.agentPersonCertType == 'PASS_PORT'}">
								<label for="" class="fl">PASS_PORT-护照</label>
							</c:if>
					</div>
				</td>
				<td></td>
				<td>
					<div>
						<label for="" class="fl"></label>
					</div>
				</td> 
			</tr>
			<tr>
				<td>邮箱</td>
				<td>
					
						<label for="" class="fl">${wloanSubject.email}</label>
				
				</td>
				<td>注册地址</td>
				<td>
						<label for="" class="fl">${wloanSubject.registAddress}</label>
				</td>
			</tr>
			
		</tbody>
		</table>	
	</form:form>
	<!--账户管理-->
	<div class="loan_apply" id="creditBankCard2">
		
		<div class="loan_apply_wrap">
<div class="nav_head">附加信息</div>	
			<div class="la_con">

				<div class="la_step la_step_four cur">

					<div class="info_basic_wrap">
						<dl class="font_size18">

						<dd class="even"><b class="pull-left">1.营业执照</b> <span class="pull-left">支持上传图片格式</span><span class="pull-right"><a href="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image-link" data-lightbox="example-10"><img src="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>

							<div class="imgfile_wrap">
								<div class="flie_wrap" id="flie_wrap_1">

									<div class="div_imgfile" id="div_imgfile_1"></div>

									<input type="file" accept="image/png,image/jpg" name="file" id="8" multiple="multiple" class="file">

								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_8">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '8'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-8" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
										</c:if>
									</c:forEach>
								</div>

								<!--确定上传按钮-->
								<!--<input type="button" value="确定上传" id="btn_ImgUpStart_1" class="fr submit_btn" />-->
								<div class="info_error_msg" id="info_error_msg_1">图片大小不符</div>
							</div>
							<dd class="even"><b class="pull-left">2.银行开户许可证(一张)</b><span class="pull-left"></span><span class="pull-right"><a href="${ctxStatic}/images/openPermit.jpg" class="example-image-link" data-lightbox="example-20"><img src="${ctxStatic}/images/openPermit.jpg" class="example-image" style="display:none"></a>查看实例</span></dd>
							<div class="imgfile_wrap">
								<div class="flie_wrap">
									<div class="div_imgfile"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="9" class="file" />
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_9">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '9'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-9" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
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
								<div class="flie_wrap" id="flie_wrap_2">
									<div class="div_imgfile" id="div_imgfile_2"></div>
									<input type="file" accept="image/png,image/jpg" name="file" id="10" class="file">
								</div>

								<!--图片预览容器-->
								<div class="div_imglook" id="div_imglook_10">
									<c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
										<c:if test="${creditAnnexFile.type == '10'}">
											<div><a class="lookimg_wrap example-image-link" data-lightbox="example-10" href=""><img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}"><input class="delete" type="hidden" value="${creditAnnexFile.id}"></a><div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div></div>
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
	<div id="modifyBankCardView" style="display:none">
		<table class="table table-striped table-bordered table-condensed" >
			<input type="text" class="error_msg" style="display: none">
			<thead><tr><th>企业基本信息</th><th>当前状态：开户中...</th><th><input type="button" id="openAccount2" value="提交" onclick="openAccount(2);"/></th><th></th></tr></thead>
			<tbody>
		
				<tr>
					<td>联系人证件号</td>
					<td>
							<input type="text" name="" value="${wloanSubject.agentPersonCertNo}"  id="agentPersonCertNo2">
					</td>
					<td>联系人手机号</td>
					<td>
							<input type="text" name="" value="${wloanSubject.agentPersonPhone}" id="agentPersonPhone2">
					</td>
				</tr>
				<tr>
					<td>企业全称</td>
					<td>
						
							<input type="text" id="enterpriseFullName2" name="" value="${wloanSubject.companyName }" >
					
					</td>
					<td>法人姓名</td>
					<td>
						
							<input type="text" name="" value="${wloanSubject.loanUser }" id="corporationName2">
					
					</td>
				</tr>
				<tr>
					<td>证照类型</td>
					<td>
						
							<select name="" id="businessLicenseType2">
								<option value="00">请选择</option>
								<option value="BLC">BLC-营业执照</option>
								<option value="USCC">USCC-统一社会信用代码</option>
							</select>
					</td>
					<td>法人证件类型</td>
					<td>
						
							<select name="" id="corporationCertType2">
								<option value="00">请选择</option>
								<option value="IDC">IDC-身份证</option>
								<option value="GAT">GAT-港澳台身份证</option>
								<option value="MILIARY">MILIARY-军官证</option>
								<option value="PASS_PORT">PASS_PORT-护照</option>
							</select>
						
					</td>
				</tr>
				<tr>
					<td>证照号</td>
					<td>
					
					
							<input type="text" name="" value="${wloanSubject.businessNo}"  id="businessLicense2">
					
					</td>
					<td>法人证件号</td>
					<td>
						
							<input type="text" name="" value="${wloanSubject.corporationCertNo}"  id="corporationCertNo2">
					
					</td>
				</tr>
				<tr>
					<td id="taxRegCertNoA2">税务登记证</td>
					<td>
						
							<input type="text" id="taxRegCertNo2" name="" value="${wloanSubject.taxCode}" >
					
					</td>
					<td>银行名称</td>
					<td>
						
				
							 <select id="bankName2" >
							 	<option value="00">请选择</option>
							 	<option value="b7f3cf26bb2e4fe99bc6237f529968a3">海口联合农商银行</option>
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
							 </select>  
							<!--<input type="text" name="" value="" class="fr" id="bankName">-->
					
					</td>
				</tr>
				<tr>
					<td id="orgCodeA2">组织机构代码</td>
					<td>
						
							<input type="text" id="orgCode2" name="" value="${wloanSubject.organNo}" >
					
					</td>
					<td>银行账号</td>
					<td>
						
							<input type="text" name="" value="${wloanSubject.loanBankNo}"  id="bankCardNo2">
						
					</td>
				</tr>
				<tr>
					<td>银行开户许可证编号</td>
					<td>
							<input type="text" name="" id="bankPermitCertNo2" value="${wloanSubject.bankPermitCertNo}">
					
					</td>
					<td>开户城市</td>
					<td>
						
							
							     <select id="bankProvince2" >
							     	<option value="00">请选择</option>
							     </select>      
	                                   <select id="bankCity2">
	                                   	<option value="00">请选择</option>
	                                   </select>
	                                  </div>
					
					</td>
				</tr>
				<tr>
					<td>联系人姓名</td>
					<td>
						
							
							<input type="text" name="" id="agentPersonName2" value="${wloanSubject.agentPersonName}">
					
					</td>
					<td>支行名称</td>
					<td>
						
						<input type="text" name="" value="" id="issuerName2">
				
				</td>
			</tr>
			<tr>
				<td>联系人证件类型</td>
				<td>
					
						<select name="" id="agentPersonCertType2">
							<option value="00">请选择</option>
							<option value="IDC">IDC-身份证</option>
							<option value="GAT">GAT-港澳台身份证</option>
							<option value="MILIARY">MILIARY-军官证</option>
							<option value="PASS_PORT">PASS_PORT-护照</option>
						</select>
				
				</td>
				<td>支行-联行号</td>
				<td>
						<input type="text" name="" value=""  id="issuer2">
				</td>
			</tr>
			<tr>
				<td>邮箱</td>
				<td>
					
						<input type="text" name="" value="${wloanSubject.email}"  id="email2"/>
				
				</td>
				<td>注册地址</td>
				<td>
						<input type="text" name="" value="${wloanSubject.registAddress}"  id="registAddress2"/>
				</td>
			</tr>

			
		</tbody>
		</table>
		
	</div>
	</c:if>
	</div>
<div class="mask_gray"></div>
<div class="mask_tip">正在跳转银行,请您耐心等待...</div>
</body>
</html>