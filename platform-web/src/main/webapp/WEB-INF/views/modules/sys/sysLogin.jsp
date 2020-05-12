<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${fns:getConfig('productName')} 登录</title>
	<meta name="decorator" content="blank"/>
	<style type="text/css">
      html,body,table{background-color:#f5f5f5;width:100%;text-align:center;}.form-signin-heading{font-family:Helvetica, Georgia, Arial, sans-serif, 黑体;font-size:36px;margin-bottom:20px;color:#0663a2;}
      .form-signin{position:relative;text-align:left;width:300px;padding:25px 29px 29px;margin:0 auto 20px;background-color:#fff;border:1px solid #e5e5e5;
        	-webkit-border-radius:5px;-moz-border-radius:5px;border-radius:5px;-webkit-box-shadow:0 1px 2px rgba(0,0,0,.05);-moz-box-shadow:0 1px 2px rgba(0,0,0,.05);box-shadow:0 1px 2px rgba(0,0,0,.05);}
      .form-signin .checkbox{margin-bottom:10px;color:#0663a2;} .form-signin .input-label{font-size:16px;line-height:23px;color:#999;}
      .form-signin .input-block-level{font-size:16px;height:auto;margin-bottom:15px;padding:7px;*width:283px;*padding-bottom:0;_padding:7px 7px 9px 7px;}
      .form-signin .btn.btn-large{font-size:16px;} .form-signin #themeSwitch{position:absolute;right:15px;bottom:10px;}
      .form-signin div.validateCode {padding-bottom:15px;} .mid{vertical-align:middle;}
      .header{height:80px;padding-top:20px;} .alert{position:relative;width:300px;margin:0 auto;*padding-bottom:0px;}
      label.error{background:none;width:270px;font-weight:normal;color:inherit;margin:0;}
    </style>
	<script type="text/javascript">
	   
	    var isCode = false;
	     
		$(document).ready(function() {
			$("#loginForm").validate({
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
			});
			
			$("#code").blur(function() {
				var mobile = $("#mobile").val();
				var code = $("#code").val();
				$.ajax({
					url: "https://www.cicmorgan.com/svc/services/sm/verifySmsCode",
					type:"post",
					dataType:"json",
					data:{
						mobilePhone:mobile,
						smsCode:code,
						from:'8'
					},
					success:function(result){
						//console.log(result.message);
						if(result.state == "0"){
							//alert(result.message);
							$("#checkCode")[0].style.display = "none";
							isCode = true;
						}else{
							$("#checkCode").html("手机验证码错误");
							$("#checkCode")[0].style.display = "block";
						}
					}
				});
			});
			
			
		});
		
		
		function getMobile(){
			var username = $("#username").val();
			//alert(username);
			$.ajax({
				url: "https://www.cicmorgan.com/svc/services/sys/getSysUserInfo",
				type:"post",
				dataType:"json",
				data:{
					username:username
				},
				success:function(result){
					if(result.state == "0"){
						$("#mobile").val(result.data.mobile);
						$("#checkCode")[0].style.display = "none";
					}else{
						$("#checkCode").html("查找不到该登录名，请核实。");
						$("#checkCode")[0].style.display = "block";
					}
				}
			});
			
		}
		
		
		/*获取验证码倒计时*/
	    var InterValObj; //timer变量，控制时间
	    var count = 60; //间隔函数，1秒执行
	    var curCount;//当前剩余秒数

		function sendMessage() {
			var mobile = $("#mobile").val();
		    curCount = count;
		    //设置button效果，开始计时
		    $("#btnSendCode").attr("disabled", "true");
		    $("#btnSendCode").html("请在" + curCount + "秒内输入验证码");
		    InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
		    //向后台发送处理数据
		    $.ajax({
				url: "https://www.cicmorgan.com/svc/services/sm/newSendSmsCode",
				type:"post",
				dataType:"json",
				data:{
					mobilePhone:mobile,
					type:'1',
					key:'cicmorganabc',
					picturCode:'picturCode',
					ip:'127.0.0.1',
					from:'8'
				},
				success:function(result){

					if(result.state == "0"){
						$("#checkCode")[0].style.display = "none";
					}else{
						$("#checkCode").html(result.message);
						$("#checkCode")[0].style.display = "block";
					}
				}
			});
		}
	    
		//timer处理函数
		function SetRemainTime() {
					if (curCount == 0) {                
						window.clearInterval(InterValObj);//停止计时器
						$("#btnSendCode").removeAttr("disabled");//启用按钮
						$("#btnSendCode").html("重新发送验证码");
					}
					else {
						curCount--;
						$("#btnSendCode").html("请在" + curCount + "秒内输入验证码");
					}
		}
		
		function login(){
			//alert("111");
			/* var code = $.trim($("#code").val()); */
			//alert("222")
			//验证码校验
			/* if(code == null || code == ""){
				$("#checkCode").html("请输入验证码");
				$("#checkCode")[0].style.display = "block";
				return false;
			} */
			//alert(isCode);
			/* if(!isCode){
				return false;
			} */
			//alert("333");
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
	<h1 class="form-signin-heading">${fns:getConfig('productName')}</h1>
	<form id="loginForm" class="form-signin" action="${ctx}/login" method="post">
		<label class="input-label" for="username">登录名</label>
		<input type="text" id="username" name="username" class="input-block-level required" value="${username}" onblur="getMobile()">
		<label class="input-label" for="password">密码</label>
		<input type="password" id="password" name="password" class="input-block-level required">
		<input type="hidden" id="mobile" name="mobile">
		<!-- <input type="text" id="code" name="code" class="input-block-level required" style="width: 156px;"><a href="javascript:;" class="login_code_02" id="btnSendCode" onclick="sendMessage()" >获取验证码</a> -->
		<label style="color: red;width: 227px;display: none;" id="checkCode"></label>
		<c:if test="${isValidateCodeLogin}"><div class="validateCode">
			<label class="input-label mid" for="validateCode">验证码</label>
			<sys:validateCode name="validateCode" inputCssStyle="margin-bottom:0;"/>
		</div></c:if><%--=
		<label for="mobile" title="手机登录"><input type="checkbox" id="mobileLogin" name="mobileLogin" ${mobileLogin ? 'checked' : ''}/></label> --%>
		<input class="btn btn-large btn-primary" type="button" value="登 录" onclick="login();"/>&nbsp;&nbsp;
		<label for="rememberMe" title="下次不需要再登录"><input type="checkbox" id="rememberMe" name="rememberMe" ${rememberMe ? 'checked' : ''}/> 记住我（公共场所慎用）</label>
		<div id="themeSwitch" class="dropdown">
			<a class="dropdown-toggle" data-toggle="dropdown" href="#">${fns:getDictLabel(cookie.theme.value,'theme','默认主题')}<b class="caret"></b></a>
			<ul class="dropdown-menu">
			</ul>
			<!--[if lte IE 6]><script type="text/javascript">$('#themeSwitch').hide();</script><![endif]-->
		</div>
	</form>
	<div class="footer">
		Copyright &copy; ${fns:getConfig('copyrightYear')} ${fns:getConfig('productName')} - Powered By <a href="http://www.win11.com" target="_blank">win11</a> ${fns:getConfig('version')} 
	</div>
	<script src="${ctxStatic}/flash/zoom.min.js" type="text/javascript"></script>
</body>
</html>