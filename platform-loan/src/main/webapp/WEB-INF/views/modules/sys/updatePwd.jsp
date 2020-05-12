<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${fns:getConfig('productName')} 登录</title>
	<meta name="decorator" content="blank"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
	<script type="text/javascript" src="${ctxStatic}/js/jquery.js"></script>
	<script type="text/javascript" src="${ctxStatic}/js/CheckUtils.js?v=1"></script>
	<style type="text/css">
    html, body, table {
    background-color: #fff;
    width: 100%;
    text-align: center;
	}

.form-signin-heading {
    font-family: "'Microsoft YaHei', 'Hiragino Sans GB', Helvetica, Arial, 'Lucida Grande', sans-serif!important";
    font-size: 24px;
    color: #40a2fb;
    padding: 50px 0 30px
}

.form-signin {
    position: relative;
    text-align: left;
    width: 360px;
    margin: 0 auto 20px;
    background-color: #fff;
}

.form-signin .checkbox {
    margin-bottom: 10px;
    color: #0663a2;
}

.form-signin .input-label {
    font-size: 16px;
    line-height: 23px;
    color: #999;
}

.form-signin .input-block-level {
    font-size: 16px;
    margin-bottom: 30px;
    height: 46px;
    padding: 7px 7px 7px 50px;
    _padding: 7px 7px 7px 50px;
}

.form-signin .btn.btn-large {
    font-size: 20px;
    margin: 35px auto;
    width: 100%;
    background: #40a2fb;
}

.form-signin #themeSwitch {
    position: absolute;
    right: 15px;
    bottom: 10px;
}

.form-signin div.validateCode {
    padding-bottom: 15px;
}

.mid {
    vertical-align: middle;
}

.header {
    height: 80px;
    padding-top: 20px;
}

.alert {
    position: relative;
    width: 300px;
    margin: 0 auto;
    *padding-bottom: 0px;
}

label.error {
    background: none;
    width: 270px;
    font-weight: normal;
    color: inherit;
    margin: 0;
}

.login_wrap {
    width: 450px;
    margin: 0 auto 27px;
    border: 1px solid #eeeeee;
    border-radius: 5px;
    padding-bottom: 10px;
}

.login_logo {
    background: #40a2fb;
    padding: 15px 0;
}

.login_group {
    position: relative;
}

.input-label {
    position: absolute;
    top: 12px;
    left: 10px;
    width: 24px;
    height: 23px;
    background: url(${ctxStatic}/images/icon_01.png) no-repeat;
}

.form-signin .login_group:nth-of-type(2) label {
    background: url(${ctxStatic}/images/icon_02.png) no-repeat;
}

.clear_overflow{
overflow:hidden;
}
  .mask_model_repd .setting_phone_group{
  width:100%;
  } 
  .setting_phone_group{
      clear: both;
    overflow: hidden;
    margin-bottom:10px;
    position: relative;
  } 
  .setting_phone_group label{
      width: 80px;
    text-align: right;
    padding-right: 15px;
    box-sizing: border-box;
    line-height:30px;
    
  }
  .phone_submit_btn input{
      margin-left: 135px;
    height: 30px;
    width: 80px;
  
  }
   .setting_phone_group #btnSendCode{
   right: 76px;
   }
   .code_num {
    position: absolute;
    background: rgba(0,0,0,0.4);
    left: 0;
    height: 121%;
    top: -21%;
    z-index: 3;
    width: 100%;
    width: 100%;
    color: #fff;
    display: none;
    padding: 25px 8% 19px;
    box-sizing: border-box;
	}
	.code_num_b input{
	  margin-left:0;
	}
	.error_msg {
    font-size: 14px;
    color: #f40;
    text-align: left;
    padding-top: 0;
    display: none;
    padding-left: 80px;
}
.tip_g{
font-size: 16px;
    color: #40a2fb;
    margin-bottom: 20px;
}
.mask_repd{
overflow:auto;
max-height:80%
}
    </style>
	<script type="text/javascript">
	   
	    var isCode = false;
	    var applyFile = false;
	     
		$(document).ready(function() {
			
			// 页面初始化，默认为false
			$("#registAgreement").prop("checked",false);
			$("#electronicSign").prop("checked",false);
			$("#riskAgreement").prop("checked",false);

			$(".file").on("change", function() {
				var _self = $(this);
				for(var i = 0; i < this.files.length; i++) {
					var file = this.files[i];
// 					readFile(file, _self.parent().siblings(".div_imglook"));

				}
				
				var file = this.files[0];
			     var $this=$(this);
// 			     $this.parent().siblings(".info_error_msg").hide();
				var formData = new FormData();
				var type = $this.attr("id");
				var creditUserId = "${id}";
				var count = $this.parent().siblings(".div_imglook").children(".lookimg_wrap").length;
				formData.append("type", "11");
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
							applyFile = true;
// 					 		readFile(file, $this.parent().siblings(".div_imglook"), annexFileId,type);
						}else{
							applyFile = false;
// 							errMessage(result.message);
						} 

					}
				});

			});
			
			
		});
		
	</script>
</head>
<body>
	<!--[if lte IE 6]><br/><div class='alert alert-block' style="text-align:left;padding-bottom:10px;"><a class="close" data-dismiss="alert">x</a><h4>温馨提示：</h4><p>你使用的浏览器版本过低。为了获得更好的浏览体验，我们强烈建议您 <a href="http://browsehappy.com" target="_blank">升级</a> 到最新版本的IE浏览器，或者使用较新版本的 Chrome、Firefox、Safari 等。</p></div><![endif]-->
	<div class="header">
		<div id="messageBox" class="alert alert-error ${empty message ? 'hide' : ''}"><button data-dismiss="alert" class="close">×</button>
			<label id="loginError" class="error">${message}</label>
		</div>
	</div>
	<div class="login_wrap">
	<div class="login_logo"><img src="${ctxStatic}/images/logo.png" /></div>
	<h1 class="form-signin-heading">${fns:getConfig('productName')}</h1>
	<div class="tip_g">首次登录请修改密码</div>
	<form id="loginForm" class="form-signin" action="" method="post">
		
		<div class="clear_overflow">
			
			<div class="setting_phone_group">
				<label for="" class="fl">手机号</label>
				<input type="text" name="" id="mobileTwo" value="${creditUser.phone }" class="fl" readonly="true">
			</div>
			<div class="setting_phone_group ">
				<label for="" class="fl">手机验证码</label>
				<input type="text" name="" id="messageCode" value="" class="fl" placeholder="请输入手机验证码">
				<span id="btnSendCode" onclick="sendMessage()">获取验证码</span>
			</div>
			<div class="setting_phone_group">
				<label for="" class="fl">设置密码</label>
				<input type="text" name="" id="pwd" value="" class="fl" placeholder="请输入密码" onfocus="this.type='password'">
	
			</div>			
			<div class="setting_phone_group" style="margin-bottom:0">
				<label for="" class="fl" style="width:52%"><a href="${ctxStatic}/file/supplyBook.docx" downloan="${ctxStatic}/file/supplyBook.docx">下载授权书模板填写后上传</a></label>
				
			     <input type="file" class="file" value="上传授权书"  style="width:48%"/>
		
			</div>
			
			<div class="setting_phone_group">
					<div class="agreement fl agreement_01">
						<span class=""><input type="checkbox" id="registAgreement" ><i></i></span>
						<em class="fl">注册协议</em>
					</div>
					<div class="agreement fl agreement_02">
						<span class=""><input type="checkbox" id="electronicSign"><i></i></span>
						<em class="fl">电子签章委托授权书</em>
					</div>
					<div class="agreement fl agreement_03" style="margin-top: -20px;">
						<span class=""><input type="checkbox" id="riskAgreement"><i></i></span>
						<em class="fl">借款人网络借贷风险和禁止性行为提示</em>
					</div>
			<!-- 	<tr>
					<td><input type="checkbox" value="注册协议" id="registAgreement" >注册协议</input></td>
					<td><input type="checkbox" value="生成电子签章" id="electronicSign">生成电子签章</input></td>
				</tr> -->
	
			</div>
			<div class="setting_phone_group">
			</div>
			<div class="setting_phone_group phone_submit_btn">
				<input type="button" name="" onclick="updatePwd()" value="确定" class="fl">
			</div>
			<div class="error_msg clear">手机号码有误,请重新输入</div>
			<div class="code_num">
				<div class="code_num_t">
					<input type="text" name="" readonly="" id="pictureCodeR" value="3456" class="fl">
					<input type="hidden" id="codeKey" value="">
					<span class="fr" onclick="getPictureCode()">看不清,换一张</span>
				</div>
				<div class="code_num_b clear">
					<input type="text" name="" id="pictureCode2R" placeholder="请输入验证码" class="fl" value="">
					<span class="fr" id="" onclick="checkPictureCode()">确定</span>
				</div>
				<div class="error_msg_02 fl">验证码错误</div>
			</div>
	
		</div>
		<!-- 注册协议 摊款-->
		<div class="mask_wrap" ></div>
		<div class="mask_repd mask_protocol">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
				<h4 class="modal-title" id="myModalLabel">注册协议</h4>
			</div>
			<div class="mask_model_repd">
					<div class="protocol_group">
						<h3>中投摩根平台服务协议</h3>
						<p>中投摩根信息技术（北京）有限公司（以下称“乙方”）依据本协议的规定为中投摩根平台注册用户（以下称“甲方”）提供服务，为明确双方权利义务关系，维护各方合法权益，双方就平台服务事宜达成本协议，以资信守。</p>
					</div>
					<div class="protocol_group">
						<h4>第一章&nbsp;&nbsp;&nbsp;注册提示</h4>
						<p>第一条 乙方平台是由乙方合法运营的官网（www.cicmorgan.com）、手机应用程序、微信服务号等信息中介服务平台。乙方服务是由乙方通过乙方平台的各种方式向甲方提供的服务，服务具体详情以乙方当时提供的服务内容为准。</p>
					   <p>第二条 在甲方注册成为平台用户并接受平台提供的相关服务之前，已仔细阅读、充分理解并接受本协议的全部内容及平台已经发布的各类规则，特别是其中所涉及的免除及限制双方责任、对双方权利限制条款等，乙方已提请甲方审慎阅读并选择接受或不接受。除非甲方接受本协议所有条款，否则甲方无权使用本协议下所提供的服务。甲方一经注册或使用平台提供的任何一项服务即视为对本协议全部条款已充分理解并完全接受，如有违反而导致任何法律后果的发生，甲方将以自己的名义独立承担相应的法律责任。</p>
					   <p>第三条 在本协议履行过程中，乙方可根据情况对本服务协议进行修改。一旦本服务协议的内容发生变动，乙方将通过乙方平台公布最新的服务协议，不再向甲方作个别通知。如果甲方不同意乙方对本服务协议所做的修改，甲方有权停止使用乙方服务。如果甲方继续使用乙方服务，则视为甲方接受乙方对本服务协议所做的修改，并应遵照修改后的协议执行。</p>
					   <p>第四条 乙方对于甲方的通知及任何其他的协议、告示或其他关于甲方使用账户及服务的通知，甲方同意乙方可通过乙方平台公告、站内信、电子邮件、手机短信、无线通讯装置等电子方式进行，该类通知于发送之日视为已送达收件人。因不可归责于乙方的原因（包括但不限于电子邮件地址、手机号码、联系地址等不准确或无效、信息传输故障等）导致甲方未在前述通知视为送达之日收到该等通知的，乙方不承担任何责任。</p>
					   <p>第五条 乙方可以依其判断暂时停止向甲方提供服务，只要甲方仍然使用乙方服务，即表示甲方仍然同意本服务协议。</p>
					</div>
					<div class="protocol_group">
						<h4>第二章&nbsp;&nbsp;&nbsp;服务内容</h4>
						<p>第六条 乙方服务的部分内容需要甲方根据乙方要求完成身份认证及银行卡认证，未进行身份认证及/或银行卡认证的甲方将无法使用该部分乙方服务。因未能完成认证而无法享受乙方服务造成的损失，乙方不承担任何责任。</p>
						<p>第七条 乙方将为甲方提供以下交易管理服务。</p>
						<p>1、甲方账户：甲方在乙方平台进行注册时将生成甲方账户，甲方账户将记载甲方在乙方平台的活动，上述甲方账户是甲方登陆乙方平台的唯一账户。</p>
						<p>2、交易状态更新：甲方确认，甲方在乙方平台上按乙方服务流程所确认的交易状态，将成为乙方为甲方进行相关交易或操作（包括但不限于支付或收取款项、冻结资金、订立合同等）不可撤销的指令。甲方同意相关指令的执行时间以乙方在乙方系统中进行实际操作的时间为准。甲方同意乙方有权依据本协议及/或乙方相关纠纷处理规则等约定对相关事项进行处理。</p>
						<p>甲方未能及时对交易状态进行修改、确认或未能提交相关申请所引起的任何纠纷或损失由甲方自行负责，乙方不承担任何责任。</p>
						<p>3、支付指令传递：甲方了解乙方并不是银行或支付机构，按照中国法律规定，乙方不提供资金转账服务，甲方同意乙方对其资金到账延迟不承担任何责任。</p>
						<p>甲方通过乙方平台进行各项交易或接受交易款项时，若甲方未遵从本服务协议条款或乙方公布的交易规则中的操作指示，乙方不承担任何责任。若发生上述状况而款项已先行拨入甲方账户下，甲方同意乙方有权直接从甲方相关账户中扣回款项，乙方保留拒绝甲方要求支付此笔款项之权利。此款项若已汇入甲方的银行账户，甲方同意乙方有向甲方事后索回的权利，由此产生的追索费用由甲方承担。</p>
						<p>4、交易指令传递：甲方理解并同意，乙方向符合条件的甲方提供信息中介平台服务。乙方对在乙方平台上进行的出借、借款等交易行为不承担任何责任，所有的出借、借款等交易行为均依赖于甲方的独立判断，由甲方自行决定，乙方无法也没有义务保证甲方在发出借款要约或出借意向后，能够实际获得借款或出借成功，甲方因前述原因导致的损失（包括但不限于利息、手续费等损失）由甲方自行承担，乙方不承担责任。</p>
						<p>5、交易安全设置：乙方有权基于交易安全等方面的考虑不时设定涉及交易的相关事项，包括但不限于交易限额、交易次数等，甲方了解乙方的前述设定可能会对交易造成一定不便，甲方对此没有异议。</p>
						<p>如果乙方发现了因系统故障或其他任何原因导致的处理错误，无论有利于乙方还是有利于甲方，乙方都有权纠正该错误。如果该错误导致甲方实际收到的款项多于应获得的金额，则无论错误的性质和原因为何，乙方保留纠正不当执行交易的权利，甲方应根据乙方向甲方发出的有关纠正错误的通知的具体要求返还多收的款项或进行其他操作。甲方理解并同意，甲方因前述处理错误而多付或少付的款项均不计利息，乙方不承担因前述处理错误而导致的任何损失或责任（包括甲方可能因前述错误导致的利息、汇率等损失）。</p>
						<p>第八条 乙方将为甲方提供以下客户服务：</p>
						<p>乙方服务内容主要包括根据甲方需求发布交易信息、提供交易管理等信息中介服务，具体详情以乙方平台当时提供的服务内容为准。</p>
						<p>1、银行卡认证：为使用乙方或乙方委托的第三方机构提供的充值、取现、代扣等服务，甲方应按照乙方平台规定的流程提交以甲方本人名义登记的有效银行借记卡等信息，经由乙方审核通过后，乙方会将甲方的账户与前述银行账户进行绑定。如甲方未按照乙方规定提交相关信息或提交的信息错误、虚假、过时或不完整，或者乙方有合理的理由怀疑甲方提交的信息为错误、虚假、过时或不完整，乙方有权拒绝为甲方提供银行卡认证服务，甲方因此未能使用充值、取现、代扣等服务而产生的损失自行承担。</p>
						<p>2、充值：甲方可以使用乙方指定的方式向甲方账户充入资金，用于通过乙方平台进行交易。甲方账户内的资金不计收利息。</p>
						<p>3、取现：甲方可以通过乙方平台当时开放的取现功能将甲方账户中的资金转入经过认证的银行卡账户中。乙方将于收到甲方的前述指示后，尽快通过第三方机构将相应的款项汇入甲方经过认证的银行卡账户（根据甲方提供的银行不同，会产生汇入时间上的差异）。</p>
						<p>4、查询：乙方将对甲方在乙方平台的所有操作进行记录，不论该操作之目的最终是否实现。甲方可以通过甲方账户实时查询甲方账户名下的交易记录。甲方理解并同意甲方最终收到款项的服务是由甲方经过认证的银行卡对应的银行提供的，需向该银行请求查证。甲方理解并同意通过乙方平台查询的任何信息仅作为参考，不作为相关操作或交易的证据或依据。</p>
						<p>甲方了解，上述充值、支付指令传递及取现服务涉及乙方与银行、担保公司、第三方支付机构等第三方的合作。甲方同意：（1）受银行、担保公司、第三方支付机构等第三方仅在工作日进行资金代扣及划转的现状等各种原因所限，乙方不对前述服务的资金到账时间做任何承诺，也不承担与此相关的责任，包括但不限于由此产生的利息、货币贬值等损失；（2）一经甲方使用前述服务，即表示甲方不可撤销地授权乙方进行相关操作，且该等操作是不可逆转的，甲方不能以任何理由拒绝付款或要求取消交易。就前述服务，乙方暂不会向甲方收取费用，但甲方应按照第三方的规定向第三方支付费用，具体请见第三方网站的相关信息。与第三方之间就费用支付事项产生的争议或纠纷，与乙方无关。</p>
						<p>5、甲方同意，乙方有权在提供乙方服务过程中以各种方式投放各种商业性广告或其他任何类型的商业信息（包括但不限于在乙方平台的任何页面上投放广告），并且，甲方同意接受乙方通过电子邮件或其他方式向甲方发送商品促销或其他相关商业信息。</p>
						<p>第九条 乙方将为甲方提供以下合同管理服务</p>
						<p>1、在乙方平台交易需订立的合同采用电子合同方式。甲方使用甲方账户登录乙方平台后，根据乙方的相关规则，以甲方账户用户名在乙方平台通过点击确认或类似方式签署的电子合同即视为甲方本人真实意愿并以甲方本人名义签署的合同，具有法律效力。甲方应妥善保管自己的账户密码等账户信息，甲方通过前述方式订立的电子合同对合同各方具有法律约束力，甲方不得以其账户密码等账户信息被盗用或其他理由否认已订立的合同的效力或不按照该等合同履行相关义务。</p>
						<p>2、甲方根据本协议以及乙方的相关规则签署电子合同后，不得擅自修改该合同。乙方向甲方提供电子合同的备案、查看、核对服务，如对电子合同真伪或电子合同的内容有任何疑问，甲方可通过使用乙方的“查阅合同”功能进行核对。如对此有任何争议，应以乙方记录的合同为准。</p>
						<p>3、甲方不得私自仿制、伪造在乙方平台上签订的电子合同或印章，不得用伪造的合同进行招摇撞骗或进行其他非法使用，否则由甲方自行承担责任。</p>
						<p>4、在特别列明的情形下，乙方提供纸质合同。</p>
						<p>第十条 第三方责任</p>
						<p>1、在任何情况下，对于甲方使用乙方服务过程中涉及由第三方提供相关服务的责任由该第三方承担，乙方不承担该等责任。乙方不承担责任的情形包括但不限于：</p>
						<p>（1）因银行、第三方支付机构等第三方未按照甲方和/或乙方指令进行操作引起的任何损失或责任；</p>
						<p>（2）因银行、第三方支付机构等第三方原因导致资金未能及时到账或未能到账引起的任何损失或责任；</p>
						<p>（3）因银行、第三方支付机构等第三方对交易限额或次数等方面的限制而引起的任何损失或责任；</p>
						<p>（4）因其他第三方的行为或原因导致的任何损失或责任。</p>
						<p>2、因甲方自身原因导致的任何损失或责任，由甲方自行负责，乙方不承担责任。包括但不限于：</p>
						<p>（1）甲方未按照本协议或乙方平台不时公布的任何规则进行操作导致的任何损失或责任；</p>
						<p>（2）因甲方使用的银行卡的原因导致的损失或责任，包括甲方使用未经认证的银行卡或使用非甲方本人的银行卡或使用信用卡，甲方的银行卡被冻结、挂失等导致的任何损失或责任；</p>
						<p>（3）甲方向乙方发送的指令信息不明确、或存在歧义、不完整等导致的任何损失或责任；</p>
						<p>（4）甲方账户内余额不足导致的任何损失或责任；</p>
						<p>（5）其他因甲方原因导致的任何损失或责任。</p>
					</div>
					<div class="protocol_group">
						<h4>第三章&nbsp;&nbsp;&nbsp;服务费用及其他费用</h4>
						<p>第十一条 当甲方使用乙方服务时，乙方会向甲方收取相关平台服务费用。各项平台服务费用详见甲方使用乙方服务时，乙方平台上所列之收费说明及收费标准。乙方保留单方面制定及调整平台服务费用收费标准的权利。</p>
						<p>第十二条 甲方在使用乙方服务过程中可能需要向第三方（如银行、第三方支付公司或提供技术服务的第三方等）支付一定的第三方服务费用，具体收费标准详见第三方网站相关页面，或乙方平台的提示及收费标准。甲方同意将根据上述收费标准自行或委托乙方或乙方指定的第三方代为向第三方支付该等服务费。</p>
						

					</div>
					<div class="protocol_group">
						<h4>第四章&nbsp;&nbsp;&nbsp;服务中断或故障</h4>
						<p>第十三条 甲方同意，基于互联网的特殊性，乙方不担保服务不会中断，也不担保服务的及时性和/或安全性。系统因相关状况无法正常运作，使甲方无法使用任何乙方服务或使用任何乙方服务时受到任何影响时，乙方对甲方或第三方不负任何责任，前述状况包括但不限于：</p>
						<p>1、乙方系统停机维护期间。</p>
						<p>2、电信设备出现故障不能进行数据传输的。</p>
						<p>3、由于黑客攻击、网络供应商技术调整或故障、网站升级、银行方面的问题等原因而造成的乙方服务中断或延迟。</p>
						<p>4、因台风、地震、海啸、洪水、停电、战争、恐怖袭击等不可抗力之因素，造成乙方系统障碍不能执行业务的。</p>
						
					</div>
					<div class="protocol_group">
						<h4>第五章&nbsp;&nbsp;&nbsp;账户安全及管理</h4>
						<p>第十四条 甲方了解并同意，确保甲方账户及密码的机密安全是甲方的责任。甲方将对利用该甲方账户及密码所进行的一切行动及言论，负完全的责任，并同意以下事项：</p>
						<p>（1） 甲方不对其他任何人泄露账户或密码，亦不可使用其他任何人的账户或密码。因黑客、病毒或甲方的保管疏忽等非乙方原因导致甲方的甲方账户遭他人非法使用的，乙方不承担任何责任。</p>
						<p>（2）乙方通过甲方的甲方账户及密码来识别甲方的指令，甲方确认，使用甲方账户和密码登陆后在乙方的一切行为均代表甲方本人。甲方账户操作所产生的电子信息记录均为甲方行为的有效凭据，并由甲方本人承担由此产生的全部责任。</p>
						<p>（3）冒用他人账户及密码的，乙方及其合法授权主体保留追究实际使用人连带责任的权利。</p>
						<p>（4）甲方应根据乙方的相关规则以及乙方平台的相关提示创建一个安全密码，应避免选择过于明显的单词或日期，比如甲方的姓名、昵称或者生日等。
第十五条 甲方如发现有第三人冒用或盗用甲方账户及密码，或其他任何未经合法授权的情形，应立即以有效方式通知乙方，要求乙方暂停相关服务，否则由此产生的一切责任由甲方本人承担。同时，甲方理解乙方对甲方的请求采取行动需要合理期限，在此之前，乙方对第三人使用该服务所导致的损失不承担任何责任。</p>
						<p>第十六条 甲方决定不再使用甲方账户时，应首先清偿所有应付款项（包括但不限于借款本金、利息、罚息、违约金、服务费、管理费等），再将甲方账户中的可用款项（如有）全部取现或者向乙方发出其它合法的支付指令，并向乙方申请冻结该甲方账户，经乙方审核同意后可正式注销甲方账户。</p>
						<p>甲方死亡或被宣告死亡的，其在本协议项下的各项权利义务由其继承人承担。若甲方丧失全部或部分民事权利能力或民事行为能力，乙方或其授权的主体有权根据有效法律文书（包括但不限于生效的法院判决等）或其法定监护人的指示处置与甲方账户相关的款项。</p>
						<p>第十七条 乙方有权基于单方独立判断，在其认为可能发生危害交易安全等情形时，不经通知而先行暂停、中断或终止向甲方提供本协议项下的全部或部分甲方服务（包括收费服务），并将注册资料移除或删除，且无需对甲方或任何第三方承担任何责任。前述情形包括但不限于：</p>
						<p>1、乙方认为甲方提供的个人资料不具有真实性、有效性或完整性；</p>
						<p>2、乙方发现异常交易或有疑义或有违法之虞时；</p>
						<p>3、乙方认为甲方账户涉嫌洗钱、套现、传销、被冒用或其他乙方认为有风险之情形；</p>
						<p>4、乙方认为甲方已经违反本协议中规定的各类规则及精神；</p>
						<p>5、甲方在使用乙方收费服务时未按规定向乙方支付相应的平台服务费用或第三方服务费用；</p>
						<p>6、甲方账户已连续三年内未实际使用且账户中余额为零；</p>
						<p>7、乙方基于交易安全等原因，根据其单独判断需先行暂停、中断或终止向甲方提供本协议项下的全部或部分甲方服务（包括收费服务），并将注册资料移除或删除的其他情形。</p>
						<p>第十八条 甲方同意，如其甲方账户未完成银行卡认证、且已经连续一年未登陆，乙方无需进行事先通知即有权终止提供甲方账户服务，并可能立即暂停、关闭或删除甲方账户及该甲方账户中所有相关资料及档案。</p>
						<p>第十九条 甲方同意，甲方账户的暂停、中断或终止不代表甲方责任的终止，甲方仍应对使用乙方服务期间的行为承担可能的违约或损害赔偿责任，同时乙方仍可保有甲方的相关信息。</p>
						

					</div>
					<div class="protocol_group">
						<h4>第六章&nbsp;&nbsp;&nbsp;甲方承诺</h4>
						<p>第二十条 甲方承诺以下事项：</p>
						<p>1、 甲方是符合中华人民共和国法律规定的具有完全民事权利和民事行为能力，能够独立承担民事责任的自然人或系中国境内注册成立的具有独立法人资格的组织。</p>
						<p>2、甲方通过平台开展出借、借款等交易行为应当遵循诚实信用原则，遵守法律法规、金融监管部门发布的规范性文件、与平台达成的合作协议、相关业务规则等。</p>
						<p>3、依乙方要求提示提供真实、最新、有效及完整的资料。</p>
						<p>4、保证并承诺通过乙方平台进行交易的资金来源合法。</p>
						<p>5、有义务维持并更新甲方的资料，确保其为真实、最新、有效及完整。若提供任何错误、虚假、过时或不完整的资料，或者乙方依其独立判断怀疑资料为错误、虚假、过时或不完整，乙方有权包括但不限于停用乙方帐户、拒绝甲方使用乙方服务的部分或全部功能。在此情况下，乙方不承担任何责任，并且甲方同意负担因此所产生的直接或间接的任何支出或损失。</p>
						<p>6、如因甲方未及时更新基本资料，导致乙方服务无法提供或提供时发生任何错误，甲方不得将此作为取消交易或拒绝付款的理由，乙方亦不承担任何责任，所有后果应由甲方承担。</p>
						<p>第二十一条 甲方承诺绝不为任何非法目的或以任何非法方式使用乙方服务，并承诺遵守中国相关法律、法规及一切使用互联网之国际惯例，遵守所有与乙方服务有关的网络协议、规则和程序。</p>
						<p>第二十二条 甲方同意并保证不得利用乙方服务从事侵害他人权益或违法之行为，若有违反者应负所有法律责任。上述行为包括但不限于：</p>
						<p>1、反对宪法所确定的基本原则，危害国家安全、泄漏国家秘密、颠覆国家政权、破坏国家统一的。</p>
						<p>2、侵害他人名誉、隐私权、商业秘密、商标权、著作权、专利权、其他知识产权及其他权益。</p>
						<p>3、违反依法律或合约所应负之保密义务。</p>
						<p>4、冒用他人名义使用乙方服务。</p>
						<p>5、从事任何不法交易行为，如贩卖枪支、毒品、禁药、盗版软件或其他违禁物。</p>
						<p>6、提供赌博资讯或以任何方式引诱他人参与赌博。</p>
						<p>7、涉嫌洗钱、套现或进行传销活动的。</p>
						<p>8、从事任何可能含有电脑病毒或是可能侵害乙方服务系統、资料等行为。</p>
						<p>9、利用乙方服务系统进行可能对互联网或移动网正常运转造成不利影响之行为。</p>
						<p>10、侵犯乙方的商业利益，包括但不限于发布非经乙方许可的商业广告。</p>
						<p>11、利用乙方服务上传、展示或传播虚假的、骚扰性的、中伤他人的、辱骂性的、恐吓性的、庸俗淫秽的或其他任何非法的信息资料。</p>
						<p>12、其他乙方有正当理由认为不适当之行为。/p>
						<p>第二十三条 乙方保有依其单独判断删除乙方平台内各类不符合法律政策或不真实或不适当的信息内容而无须通知甲方的权利，并无需承担任何责任。若甲方未遵守以上规定的，乙方有权作出独立判断并采取暂停或关闭账户等措施，而无需承担任何责任。</p>
						<p>第二十四条 甲方同意，由于甲方违反本协议，或违反通过援引并入本协议并成为本协议一部分的文件，或由于甲方使用乙方服务违反了任何法律或第三方的权利而造成任何第三方进行或发起的任何补偿申请或要求（包括律师费用），甲方应对乙方及其关联方、合作伙伴、董事以及雇员给予全额补偿并使之不受损害。</p>
						<p>第二十五条 甲方承诺，其通过乙方平台上传或发布的信息均真实有效，其向乙方提交的任何资料均真实、有效、完整、详细、准确。如因违背上述承诺，造成乙方或乙方其他使用方损失的，甲方将承担相应责任。</p>
						<p>第二十六条 除本协议以外，甲方应同时遵守乙方平台不时发布及更新的全部规则，包括但不限于公告、产品流程说明、平台项目说明、风险提示等。</p>
						
					</div>
					<div class="protocol_group">
						<h4>第七章&nbsp;&nbsp;&nbsp;责任限制</h4>
						<p>第二十七条 乙方未对任何乙方服务提供任何形式的保证，不提供保证的服务包括但不限于以下服务事项：</p>
						<p>1、乙方服务将符合甲方的收益需求。</p>
						<p>2、乙方服务将不受干扰、及时提供或免于出错。</p>
						<p>3、甲方经由乙方服务购买或取得之任何产品之收益、服务、资讯或其他资料将符合甲方的期望。</p>
						<p>第二十八条 乙方服务的合作单位所提供的服务品质及内容由该合作单位自行负责。乙方平台的内容可能涉及由第三方所有、控制或者运营的其他网站（以下简称“第三方网站”）。乙方无法保证也没有义务保证第三方网站上任何信息的真实性和有效性。甲方确认按照第三方网站的服务协议使用第三方网站，而不是按照本协议。第三方网站不是乙方推荐或者介绍的，第三方网站的内容、产品、广告和其他任何信息均由甲方自行判断并承担风险，而与乙方无关。甲方经由乙方服务的使用下载或取得任何资料，应由甲方自行考量且自负风险，因资料的下载而导致的任何损坏由甲方自行承担。</p>
						<p>第二十九条 甲方自乙方及乙方工作人员或经由乙方服务取得的建议或资讯，无论其为书面或口头，均不构成乙方对乙方服务的任何保证。甲方在此了解并认可，以上建议或资讯均为乙方为完成信息平台中介服务功能而提供的建议或者资讯，以上建议或资讯不是出借建议或资讯。</p>
						<p>第三十条 乙方不保证为向甲方提供便利而设置的外部链接的准确性、有效性、安全性和完整性，同时，对于该等外部链接指向的不由乙方实际控制的任何网页上的内容，乙方不承担任何责任。</p>
						<p>第三十一条 在法律允许的情况下，乙方对于与本协议有关或由本协议引起的，或者，由于使用乙方平台、或由于其所包含的或以其它方式通过乙方平台提供给甲方的全部信息、内容、材料、产品（包括软件）和服务、或购买和使用产品引起的任何间接的、惩罚性的、特殊的、派生的损失（包括但不限于业务损失、收益损失、利润损失、使用数据或其他经济利益的损失），不论是如何产生的，也不论是由对本协议的违约（包括违反保证）还是由侵权造成的，均不负有任何责任，即使其事先已被告知此等损失的可能性。另外即使本协议规定的排他性救济没有达到其基本目的，也应排除乙方对上述损失的责任。</p>
						<p>第三十二条 除本协议另有规定外，在任何情况下，乙方对本协议所承担的违约赔偿责任总额不超过向甲方收取的当次乙方平台服务费用总额。</p>
						
					</div>
					<div class="protocol_group">
						<h4>第八章&nbsp;&nbsp;&nbsp;风险提示</h4>
						<p>第三十三条 甲方了解并认可，任何通过乙方平台进行的交易并不能避免以下风险的产生，乙方不能也没有义务为如下风险负责：</p>
						<p>1.法律及监管风险：因网贷相关法律及监管政策不完善、法律和监管政策发生变动、法律和监管政策解释和执行存在不确定性等因素引起的风险，该等风险可能导致网贷平台业务模式受影响、相关产品合法性存在不确定、相关产品或服务无法继续提供等后果。</p>
						<p>2.市场风险：因市场资金面紧张或利率波动、行业不景气、企业效益下滑等因素引起的风险。</p>
						<p>3.借款人信用风险：当借款人因财务状况发生变化、人身出现意外、发生疾病、死亡等情况，短期或者长期丧失还款能力，或者借款人的还款意愿发生变化时，您的出借资金可能无法按时回收甚至无法回收，您的预期收益可能无法实现。本平台没有义务对逾期的本息以及费用进行垫付或未经委托对借款人进行追索。</p>
						<p>4.不可抗力风险：由于无法控制和不可预测的系统故障、设备故障、通讯故障、电力故障、网络故障、黑客或计算机病毒攻击、以及战争、动乱、自然灾害等其它因素，可能导致平台出现非正常运行或者瘫痪，由此导致您无法及时进行查询、充值、出借、提现等操作。</p>
						<p>5.流动性风险：在进行债权转让时，您转让的债权可能无法及时找到受让方，从而需要您长期持有，本平台没有义务受让或兑付该等债权。</p>
						<p> 6.自身过错导致的任何损失：该过错包括但不限于：决策失误、操作不当、遗忘或泄露密码、密码被他人破解、您使用的计算机系统被第三方侵入、您委托他人代理交易时他人恶意或不当操作而造成的损失。</p>
						<p>第三十四条 乙方不对任何甲方及/或任何交易提供任何担保或条件，无论是明示、默示或法定的。乙方不能也不试图对甲方发布的信息进行控制，对该等信息，乙方不承担任何形式的证明、鉴定服务。乙方不能完全保证平台内容的真实性、充分性、可靠性、准确性、完整性和有效性，并且无需承担任何由此引起的法律责任。甲方应依赖于甲方的独立判断进行交易，甲方应对其作出的判断承担全部责任。</p>
						<p>第三十五条 以上并不能揭示甲方通过乙方进行交易的全部风险及市场的全部情形。甲方在做出交易决策前，应全面了解相关交易，根据自身的交易目标、风险承受能力和资产状况等谨慎决策，独立判断，并自行承担全部风险。</p>
						
					</div>
					<div class="protocol_group">
						<h4>第九章&nbsp;&nbsp;&nbsp;隐私权保护及授权条款</h4>
						<p>第三十六条 乙方对于甲方提供的、乙方自行收集的、经认证的个人信息将按照本协议予以保护、使用或者披露。乙方无需甲方同意即可向乙方关联实体转让与乙方平台有关的全部或部分权利和义务。未经乙方事先书面同意，甲方不得转让其在本协议项下的任何权利和义务。</p>
						<p>第三十七条 乙方可能自公开及私人资料来源收集甲方的额外资料，以更好地掌握甲方情况，并为甲方度身订造乙方服务、解决争议并有助确保在乙方平台进行安全交易。</p>
						<p>第三十八条 乙方按照甲方在乙方平台上的行为自动追踪关于甲方的某些资料。在不透露甲方的隐私资料的前提下，乙方有权对整个甲方数据库进行分析并对甲方数据库进行商业上的利用。</p>
						<p>第三十九条 甲方同意，乙方可在乙方平台的某些网页上使用诸如“Cookies”的资料收集装置。</p>
						<p>第四十条 甲方同意乙方可使用关于甲方的相关资料（包括但不限于乙方持有的有关甲方的档案中的资料，乙方从甲方目前及以前在乙方平台上的活动所获取的其他资料以及乙方通过其他方式自行收集的资料）以解决争议、对纠纷进行调停。甲方同意乙方可通过人工或自动程序对甲方的资料进行评价。</p>
						<p>第四十一条 乙方采用行业标准惯例以保护甲方的资料。甲方因履行本协议提供给乙方的信息，乙方不会恶意出售或免费共享给任何第三方，以下情况除外：</p>
						<p>1、提供独立服务且仅要求服务相关的必要信息的供应商，如印刷厂、邮递公司等；</p>
						<p>2、具有合法调阅信息权限并从合法渠道调阅信息的政府部门或其他机构，如公安机关、法院；</p>
						<p>3、乙方的关联实体；</p>
						<p>4、经平台使用方或平台使用方授权代表同意的第三方。</p>
						<p>第四十二条 乙方有义务根据有关法律要求向司法机关和政府部门提供甲方的个人资料。在甲方未能按照与乙方签订的服务协议或者与乙方其他甲方签订的协议等其他法律文本的约定履行自己应尽的义务时，乙方有权根据自己的判断，或者与该笔交易有关的其他甲方的请求披露甲方的个人信息和资料，并做出评论。甲方严重违反乙方的相关规则（包括但不限于您的借款逾期超过[30]天等）的，乙方有权对甲方提供的及乙方自行收集的甲方的个人信息和资料编辑入网站黑名单，并将该黑名单对第三方披露，且乙方有权将甲方提交或乙方自行收集的甲方的个人资料和信息与任何第三方进行数据共享，以便网站和第三方催收逾期借款及对您的其他申请进行审核之用，由此可能造成的甲方的任何损失，乙方不承担法律责任。</p>
						<p>第四十三条 乙方根据相关法律法规制定及不时修改的《隐私条款》是本协议的一部分，本协议与该《隐私条款》不一致的部分以《隐私条款》为准。</p>
						
					</div>
					<div class="protocol_group">
						<h4>第十章&nbsp;&nbsp;&nbsp;知识产权保护</h4>
						<p>第四十四条 乙方平台上所有内容，包括但不限于著作、图片、档案、资讯、资料、平台架构、平台画面的安排、网页设计，均由乙方或其他权利人依法拥有其知识产权，包括但不限于商标权、专利权、著作权、商业秘密以及其它可以归结于知识产权的其它权利等。</p>
						<p>第四十五条 非经乙方或其他权利人书面同意，任何人不得擅自使用、修改、复制、公开传播、改变、散布、发行或公开发表乙方平台程序或内容。</p>
						<p>第四十六条 甲方未经乙方的明确书面同意不许下载（除了页面缓存）或修改平台或其任何部分。甲方不得对乙方平台或其内容进行转售或商业利用；不得收集和利用产品目录、说明和价格；不得对乙方平台或其内容进行任何衍生利用；不得为其他商业利益而下载或拷贝账户信息或使用任何数据采集、Robots或类似的数据收集和摘录工具。未经乙方的书面许可，严禁对乙方平台的内容进行系统获取以直接或间接创建或编辑文集、汇编、数据库或人名地址录（无论是否通过Robots、Spiders、自动仪器或手工操作）。另外，严禁为任何未经本使用条件明确允许的目的而使用乙方平台上的内容和材料。</p>
						<p>第四十七条 未经乙方明确书面同意，不得以任何商业目的对乙方网站或其任何部分进行复制、复印、仿造、出售、转售、访问、或以其他方式加以利用。未经乙方明确书面同意，甲方不得用frame或运用frame技巧把乙方或其关联公司的商标、标识或其他专有信息（包括图像、文字、网页设计或形式）据为己有。未经乙方明确书面同意，甲方不得以Meta Tags或任何其他"隐藏文本"方式使用乙方或其关联公司的名字和商标。任何未经授权的使用都会终止乙方所授予的允许或许可。</p>
						<p>第四十八条 尊重知识产权是甲方应尽的义务，如有违反，甲方应对乙方承担损害赔偿等法律责任。</p>
						

					</div>					
					<div class="protocol_group">
						<h4>第十一章&nbsp;&nbsp;&nbsp;条款解释</h4>
						<p>第四十九条 本协议是由甲方与乙方共同签订的，适用于甲方在乙方的全部活动。本协议内容包括但不限于协议正文条款及已经发布的或将来可能发布的各类规则，所有条款和规则为协议不可分割的一部分，与协议正文具有同等法律效力。</p>
						<p>第五十条 本协议不涉及甲方与乙方的其他甲方之间，因网上交易而产生的法律关系及法律纠纷。但甲方在此同意将全面接受并履行与乙方其他甲方在乙方签订的任何电子法律文本，并承诺按照该法律文本享有和（或）放弃相应的权利、承担和（或）豁免相应的义务。</p>
						<p>第五十一条 如本协议中的任何条款无论因何种原因完全或部分无效或不具有执行力，则应认为该条款可与本协议相分割，并可被尽可能接近各方意图的、能够保留本协议要求的经济目的的、有效的新条款所取代，而且，在此情况下，本协议的其他条款仍然完全有效并具有约束力。</p>
					</div>
					<div class="protocol_group">
						<h4>第十二章&nbsp;&nbsp;&nbsp;法律适用及争端解决</h4>
						<p>第五十三条 本协议签订地为中国北京市。因本协议所引起的甲方与乙方的任何纠纷或争议，首先应友好协商解决，协商不成的，甲方在此完全同意将纠纷或争议提交北京仲裁委员会仲裁。</p>
					</div>	
					<!-- <div class="protocol_group">
						<h4>第十三条&nbsp;&nbsp;&nbsp;通用条款</h4>
						<p>1.不得转让。未经中投摩根的事先书面同意，平台用户不得转让本协议项下的权利或义务。</p>
						<p>2.标题。段落标题仅作方便阅读之用，不影响本协议的含义或解释。</p>
						<p>3.协议的可分性。如本协议的任一部分被依法认定为违法或不具有强制执行力，不影响协议其他部分的效力。</p>
						<p>4.不构成代理。双方均为独立的协议双方而不构成对方的代理或代表，任何一方均不得以对方的名义承担义务或责任。任何情况下，任何一方均不得表明其是另一方的员工、代表、雇员或代理。</p>
					</div>	 -->
					<div class="read_btn">
					    <span class="read_agreen">同意</span>
						<span class="read_close">取消</span>
					</div>				
			</div>
		</div>
	    <!-- 点在签章 -->
	    <div class="mask_repd mask_protocol_signature">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
				<h4 class="modal-title" id="myModalLabel">电子签章委托授权书</h4>
			</div>
			<div class="mask_model_repd">
					<div class="protocol_group">
						<h3>电子签章委托授权书</h3>
						<p>重要提示：</p>
						<p>中投摩根信息技术（北京）有限责任公司（以下简称“乙方”）依据本协议的规定通过乙方运营的平台（目前包括中投摩根网站（www.cicmorgan.com）、移动客户端包含APP和Android、微信公众号、微博等，以下统称“平台”）为乙方的注册用户（以下简称“甲方”含出借客户、核心企业及供应商、借款客户等）提供服务。为明确甲方与乙方之间的权利义务关系，维护双方的合法权益，本着平等互利的原则，双方就平台服务之相关事宜达成本协议。（本协议凡提及“平台”，即同时代表运营平台的乙方）甲方确认，在甲方注册成为平台用户并接受平台提供的相关服务之前，已仔细阅读、充分理解并接受本协议的全部内容及平台已经发布的各类规则，特别是其中所涉及的免除及限制双方责任、对双方权利限制条款等，乙方已提请甲方审慎阅读并选择接受或不接受。甲方一经注册即视为对本协议全部条款已充分理解并完全接受，甲方即有权使用本协议项下所提供的服务，如有违反而导致任何法律后果的发生，甲方将以自己的名义独立承担相应的法律责任。</p>
					</div>
					<div class="protocol_group">
						<p>尊敬的用户：</p>	
						<p>电子签名数字证书是您授权中投摩根信息技术（北京）有限责任公司（以下简称“本公司”）向电子认证服务提供者申请的用于识别您身份的数字证书，为确保电子签名数字证书的正确使用、更好地为您提供规范的服务、保障您的权益，请您于申请电子签名数字证书前，仔细阅读下列使用须知：</p>
					    <p>1. 电子签名数字证书是指电子签名个人或企业数字证书（以下简称“数字证书”）。</p>
						<p>2. 您已同意本公司向您注册平台（指中投摩根网站www.cicmorgan.com、移动客户端包含APP和Android客户端、微信公众号、微博等，亦称为“注册平台”)、有关部门和个人核实您的信息。本公司有权合法收集、处理、传递及应用您的信息，并按照国家有关规定及本协议的约定对您的信息保密。</p>
						<p>3. 请确保您在本公司平台提供信息的真实、完整、准确，因信息的不真实、不完整或者不准确，由此给本公司或第三方造成损失的，您应当向本公司或第三方承担赔偿责任。</p>
						<p>4.为确保交易安全、合法，甲方同意使用第三方数字认证机构提供的数字证书完成交易文件的签署及交易行为的确认，甲方知悉并授权乙方将甲方的相关信息提交至依法设立的第三方数字认证机构代为申请专属于甲方的数字证书，并以短信、站内信、邮件等合理方式通知甲方数字证书的申请结果及用途，该数字证书将会被作为完成甲方在平台在线签署电子合同等法律文本之目的使用。在平台交易需订立的协议采用电子合同或乙方认可的其他方式，可以有一份或者多份并且每一份均具有同等法律效力。以甲方会员账户在平台根据有关协议及平台的相关规则通过点击确认或类似方式签署的电子合同即视为甲方本人或本企业真实意愿并以甲方本人或本企业名义签署的合同，具有法律效力，并不可撤销的委托并授权平台处理以下事项，</p>
						<p>（1）甲方应妥善保管自己的账户密码等账户信息，甲方通过前述方式订立的电子合同对合同各方具有法律约束力，甲方不得以账户密码等账户信息被盗用或其他理由否认已订立的合同的效力或不按照该等合同履行相关义务。 </p>
						<p>（2）甲方根据本协议以及平台的相关规则签署电子合同后，不得擅自修改该合同。平台向甲方提供电子合同的保管查询、核对等服务，如对电子合同真伪或电子合同的内容有任何疑问，甲方可通过平台的相关系统板块查阅有关合同并进行核对。如对此有任何争议，应以平台记录的合同为准。 </p>
						<p>（3）甲方不得私自仿制、伪造在平台上签订的电子合同或印章，不得以伪造、虚假的合同或印章进行招摇撞骗或进行任何其他非法行为。未经乙方许可，亦不得擅自使用平台的任何商标、电子合同和印章，否则由甲方自行承担责任，与乙方无关，且乙方保留就前述行为向甲方进行追责的权利。</p>
						<p>5. 代表甲方本人或本企业向电子签名服务机构申请数字证书和电子签章，管理用于签署本人出借行为或本企业融资相关行为、付款承诺相关行为、应收账款转让业务中相关行为系列法律文件的数字证书及数字证书的更新。</p>
						<p>6.使用甲方本人或本企业数字证书和电子签章，在满标后以甲方本人或本企业名义按《借款合同》、《应收账款转让协议》或《应收账款转让合作框架协议》、《应收账款转让申请书》、《付款承诺书》范本结合具体情况签署正式的《借款合同》《应收账款转让协议》或《应收账款转让合作框架协议》、《应收账款转让申请书》、《付款承诺书》的法律文件。甲方本人或本企业已仔细阅读并理解《借款合同》《应收账款转让协议》或《应收账款转让合作框架协议》、《应收账款转让申请书》、《付款承诺书》的各项约定。</p>
						<p>7. 保管甲方本人或本企业在贵司注册平台出借过程中签署的全部系列法律文件。</p>			
						<p>8. 甲方同意，平台有权在提供平台服务过程中以各种方式投放各种商业性广告或其他任何类型的商业信息（包括但不限于在平台的任何页面上投放广告），并且，甲方同意接受平台通过电子邮件、手机短信或其他甲方预留的联系方式向甲方发送产品信息或其他相关商业信息。</p>			
						<p>甲方本人或本企业已仔细阅读并理解本确认书及附件的全部内容，甲方本人或本企业认可通过乙方平台页面点击相应按钮的方式直接确认本确认书内容或生成电子签章的形式签署本确认书的法律效力。本确认书一经签署，即对甲方本人或本企业产生约束力。</p>
					</div>
					
					<div class="read_btn">
					    <span class="read_agreen">同意</span>
						<span class="read_close">取消</span>
					</div>				
			</div>
		</div>
		 <!-- 借款人网络借贷风险、禁止性行为及有关事项提示书 -->
	    <div class="mask_repd mask_protocol_risk_agreement">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
				<h4 class="modal-title" id="myModalLabel">借款人网络借贷风险、禁止性行为及有关事项提示书</h4>
			</div>
			<div class="mask_model_repd">
					<div class="protocol_group">
						<h3>借款人网络借贷风险</h3>
						<p>尊敬的中投摩根借款人：</p>
						<p>为了确保贵方充分知悉网络借贷风险及有关事项，防范借贷风险，更好的为贵方服务，现向贵方作以下提示，请务必仔细阅读：</p>
					</div>
					<div class="protocol_group">
						<p>一、就贵方在中投摩根平台的借款项目，贵方须按以下标准和方式支付利息及融资服务费：</p>	
						<p>利息及融资服务费合计为年化8%-16%。利息支付方式为按月付息，到期还本；平台服务费支付方式为借款申请审核通过后，放款前支付。</p>
						<p>二、如果贵方出现逾期，按照《借款合同》及有关合同约定，贵方应承担以下违约后果：</p>
						<p>1.借款人未按合同约定期限及金额归还本金和利息，则自逾期之日起计收逾期违约金；每逾期一日借款人应按未偿还本金的万分之三向出借人支付逾期违约金直至清偿之日止。</p>
						<p>2.借款人未按约定向平台方支付平台服务费，则自逾期之日起计收逾期违约金；每逾期一日借款人应按逾期金额的万分之三向平台方支付逾期违约金直至清偿之日止。</p>
						<p>3.如借款人逾期，本平台有权根据法律、法规、部门规章，及监管机关和行业自律组织（包括但不限于中国银行保险监督管理委员会、中国人民银行、地方金融监管部门、中国互联网金融协会、北京市互联网金融行业协会等）的相关规定或要求向监管机关和行业自律组织及其指定的监管信息系统报送、提供借款人的逾期信息，从而影响借款人的信用记录。</p>
						<p>4.出借人、本平台或不良债权受让方有权向贵方及担保人催收、提起诉讼或仲裁，相关诉讼仲裁费用及实现债权的费用由贵方承担。 </p>
						<p>5.法律法规及合同规定的其他相关不利后果。 </p>
						<p>三、以上提示内容与《借款合同》及相关合同约定不一致的，以相关合同约定为准。</p>
					</div>
					<div class="protocol_group">
						<h3>网络借贷禁止性行为提示</h3>
						<p>请仔细阅读并充分理解《网络借贷信息中介机构业务活动管理暂行办法》及有关监管法律、政策规定的如下网络借贷禁止性行为：</p>
					</div>
					<div class="protocol_group">
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
					</div>
					<div class="read_btn">
						<span class="read_agreen">同意</span>
						<span class="read_close">取消</span>
					</div>
			</div>
		</div>
	 
	</form>
	</div>
	<div class="footer">
		Copyright &copy; ${fns:getConfig('copyrightYear')} ${fns:getConfig('productName')} - Powered By <a href="https://www.cicmorgan.com" target="_blank">cicmorgan</a> ${fns:getConfig('version')} 
	</div>
	<script>
	
		/*关闭弹框*/
		$(".close").click(function() {
			$(".mask_wrap,.mask_protocol,.mask_protocol_signature,.mask_protocol_risk_agreement").hide();
		});
		/*注册协议弹框  */
		$(".agreement_01").click(function(){
			$(".mask_wrap,.mask_protocol").show();	
			
			
		});		
		/* 电子签章 */
		
		$(".agreement_02").click(function(){
			$(".mask_wrap,.mask_protocol_signature").show();	
		});
		/*借款人网络借贷风险和禁止性行为提示 */
		$(".agreement_03").click(function(){
			$(".mask_wrap,.mask_protocol_risk_agreement").show();	
		});
		
		$(".mask_protocol .read_agreen").click(function(){
			$(".mask_wrap,.mask_protocol").hide();
			$(".agreement_01 span").toggleClass("cur");
			$("#registAgreement").prop("checked",true);
			// console.log($("#registAgreement").prop("checked"));
		});		
		$(".mask_protocol_signature .read_agreen").click(function(){
			$(".mask_wrap,.mask_protocol_signature").hide();
			$(".agreement_02 span").toggleClass("cur");
			$("#electronicSign").prop("checked",true);
		});
		$(".mask_protocol_risk_agreement .read_agreen").click(function(){
			$(".mask_wrap,.mask_protocol_risk_agreement").hide();
			$(".agreement_03 span").toggleClass("cur");
			$("#riskAgreement").prop("checked",true);
		});
		
		$(".mask_protocol .read_close").click(function(){
			$(".mask_wrap,.mask_protocol").hide();
			$(".agreement_01 span").removeClass("cur");
			$("#registAgreement").prop("checked",false);
		});			
		$(".mask_protocol_signature .read_close").click(function(){
			$(".mask_wrap,.mask_protocol_signature").hide();
			$(".agreement_02 span").removeClass("cur");
			$("#electronicSign").prop("checked",false);
		});	
		$(".mask_protocol_risk_agreement .read_close").click(function(){
			$(".mask_wrap,.mask_protocol_risk_agreement").hide();
			$(".agreement_03 span").removeClass("cur");
			$("#riskAgreement").prop("checked",false);
		});	
		
		//点击获取验证码
		$("#btnSendCode").click(function() {
			getPictureCode();
			$(".code_num").show();
			$(".mask_drop").show();
		})
		
		/*获取验证码*/
		$("#mobileTwo").blur(function(){
			var mobile = $("#mobileTwo").val();
			if(!checkPhone(mobile)){
				console.log(mobile);
				$(".error_msg").show();
				return false;
			}
			console.log(1);
		})
		/*获取验证码倒计时*/
		var InterValObj; //timer变量，控制时间
		var count = 60; //间隔函数，1秒执行
		var curCount; //当前剩余秒数
		var ctxpath = '${ctxpath}';

		function sendMessage() {
			var mobile = $("#mobileTwo").val();
			if(!checkPhone(mobile)){
				$(".error_msg").show();
				return false;
			}
			
			
			var pictureCode = $("#pictureCodeR").val();
			var key = $("#codeKey").val();
			console.log(mobile);
			$(".error_msg").hide();
			curCount = count;
			//设置button效果，开始计时
			$("#btnSendCode").attr("disabled", "true");
			$("#btnSendCode").html("倒计时" + curCount + "S");
			InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
			//向后台发送处理数据
			$.ajax({
				url: ctxpath + "/sm/newSendSmsCode",
				type: "post",
				dataType: "json",
				data: {
					mobilePhone: mobile,
					type: '1',
					from: '2',
					key: key,
					picturCode: pictureCode
				},
				success: function(result) {
					if(result.state == "0") {
						console.log("验证码已发送！");
					}
				}
			});

		}
		
		//timer处理函数
		function SetRemainTime() {
			if(curCount == 0) {
				window.clearInterval(InterValObj); //停止计时器
				$("#btnSendCode").removeAttr("disabled"); //启用按钮
				$("#btnSendCode").html("重新获取");
			} else {
				curCount--;
				$("#btnSendCode , #btnSendCode02 ,#btnSendCode03").html("倒计时" + curCount + "S");
			}
		}
		
		
		/**
		 * 获取图形验证码
		 */
		function getPictureCode() {
			$(".error_msg").hide();
			$.ajax({
				url: ctxpath + "/sm/getPictureCode",
				type: "post",
				dataType: "json",
				data: {
					from: '2'
				},
				success: function(result) {
					if(result.state == "0") {
						$("#pictureCodeR").val(result.pictureCode);
						$("#codeKey").val(result.key);
					}
				}
			});
		}
		
		/**
		 * 检查图形验证码 并发送短信验证码
		 */
		function checkPictureCode() {
			$(".error_msg").hide();
			$(".error_msg_02").hide();
			var code = $("#pictureCode2R").val(); //输入的图片验证码
			var pictureCode = $("#pictureCodeR").val(); //显示的图片验证码
			var key = $("#codeKey").val();
			$(".error_msg").html("");
			$.ajax({
				url: ctxpath + "/sm/checkPictureCode",
				type: "post",
				dataType: "json",
				data: {
					from: '2',
					key: key,
					pictureCode: code
				},
				success: function(result) {
					if(result.state == "0") {
						$(".code_num").hide();
						$(".mask_drop").hide();
						sendMessage();
					} else {
						$(".error_msg_02").html(result.message);
						$(".error_msg_02").show();
					}
				}
			});

		}
		
		/*
		 * 确认修改密码
		*/
		function updatePwd() {
			$(".error_msg").hide();
			var mobile = $("#mobileTwo").val();
			var code = $("#messageCode").val();
			
			var pwd = $.trim($("#pwd").val());

			//密码校验
			if(pwd == null || pwd == "") {
				$(".error_msg").html("请输入密码");
				$(".error_msg").show();
				return false;
			}
			if(pwd.length < 6) {
				$(".error_msg").html("密码至少大于等于6位");
				$(".error_msg").show();
				return false;
			} 
			if(!applyFile){
				$(".error_msg").html("请上传申请书！");
				$(".error_msg").show();
				return false;
			}
			if($("#registAgreement").prop("checked")){
			} else {
				$(".error_msg").html("请同意注册协议！");
				$(".error_msg").show();
				return false;
			}
			if($("#electronicSign").prop("checked") != true){
				$(".error_msg").html("请同意生成电子签章！");
				$(".error_msg").show();
				return false;
			}
			if($("#riskAgreement").prop("checked") != true){
				$(".error_msg").html("请同意借款人网络借贷风险、禁止性行为及有关事项提示书！");
				$(".error_msg").show();
				return false;
			}

			$.ajax({
				url: ctxpath + "/sm/verifySmsCode",
				type: "post",
				dataType: "json",
				data: {
					mobilePhone: mobile,
					smsCode: code,
					from: '1'
				},
				success: function(result) {
					if(result.state == "0") {
						//注册
						$.ajax({
							url: ctxpath + "/credit/creditForgetPwd",
							type: "post",
							dataType: "json",
							data: {
								phone: mobile,
								pwd: pwd
							},
							success: function(result) {
								if(result.state == "0") {
									window.location.href="${ctx}/login";
								}else{
									$(".error_msg").html(result.message);
									$(".error_msg").show();	
								}
							}
						});
					} else {
						$(".error_msg").html(result.message);
						$(".error_msg").show();
						return false;
					}
				}
			});

		}
	</script>
	
</body>
</html>