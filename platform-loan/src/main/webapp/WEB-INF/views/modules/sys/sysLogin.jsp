<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${fns:getConfig('productName')} 登录</title>
	<meta name="decorator" content="blank"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
	<script type="text/javascript" src="${ctxStatic}/js/jquery.js"></script>
	<script type="text/javascript" src="${ctxStatic}/js/CheckUtils.js"></script>
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
    padding: 50px 0 70px
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

.mask_btn {
    cursor: pointer;
    float: right;
    color: #40a2fb;
}
    
    </style>
	<script type="text/javascript">
	   
	    var isCode = false;
	     
		$(document).ready(function() {
			/* $("#loginForm").validate({
				rules: {
					validateCode: {remote: "${pageContext.request.contextPath}/servlet/validateCodeServlet"}
				},
				messages: {
					username: {required: "请填写用户名."},password: {required: "请填写密码."},
					validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
				},
				errorLabelContainer: "#messageBox",
				errorPlacement: function(error, element) {
					error.appendTo($("#loginError").parent());
				} 
			}); */
		});

		function login(){
			$("#loginForm").submit();
		}

		// 如果在框架或在对话框中，则弹出提示并跳转到首页
		if(self.frameElement && self.frameElement.tagName == "IFRAME" || $('#left').length > 0 || $('.jbox').length > 0){
			alert('未登录或登录超时。请重新登录，谢谢！');
			top.location = "${ctx}";
		}
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
	<form id="loginForm" class="form-signin" action="${ctx}/newlogin" method="post">
	   <div class="login_group">
		<label class="input-label" for="username"></label>
			<input type="text" id="username" name="username" class="input-block-level required" value="${username}" onblur="javascript:void(0);"  placeholder="请输入手机号" />
		</div>
		<div class="login_group">
		<label class="input-label" for="password"></label>
		<input type="password" id="password" name="password" class="input-block-level required" placeholder="请输入密码">
		</div>
		<label style="color: red;width: 227px;display: none;" id="checkCode"></label>
		<input class="btn btn-large btn-primary" type="button" value="登 录" onclick="login();"/>&nbsp;&nbsp;
		<label for="rememberMe" title="下次不需要再登录" class="mask_btn">重置密码</label>
		<!-- 重置密码 -->
		<div class="mask_wrap"></div>
		<div class="mask_repd">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
				<h4 class="modal-title" id="myModalLabel">重置密码</h4>
			</div>
			<div class="mask_model_repd">

				<div class="setting_phone_group">
					<label for="" class="fl">手机号</label>
					<input type="text" name="" id="mobileTwo" value="" class="fl" placeholder="请输入手机号">
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
		</div>
		
	</form>
	</div>
	<div class="footer">
		Copyright &copy; ${fns:getConfig('copyrightYear')} ${fns:getConfig('productName')} - Powered By <a href="https://www.cicmorgan.com" target="_blank">cicmorgan</a> ${fns:getConfig('version')} 
	</div>
	<script>
		$(".mask_btn").click(function() {
			$(".mask_wrap,.mask_repd").show();
		});
		/*关闭弹框*/
		$(".close").click(function() {
			$(".mask_wrap,.mask_repd").hide();
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
// 		var ctxpath = 'https://www.cicmorgan.com/svc/services';
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
									$(".mask_wrap,.mask_repd").hide();
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