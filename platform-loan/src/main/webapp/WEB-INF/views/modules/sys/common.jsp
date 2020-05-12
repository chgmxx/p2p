<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta name="renderer" content="webkit">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<title>中投摩根—领先的互联网借贷信息交互平台</title>
		<meta content="P2P理财,投资理财,个人理财,网上投资,供应链金融,互联网金融,网贷平台,中投摩根" name="keywords" />
		<meta content="中投摩根财富管理平台,中国互联网金融安全出借领航者,出借者首选互联网出借平台,中投摩根在健全的风险管控体系基础上,为出借者提供可信赖的互联网金融出借产品,实现您的金融财富增值." name="description" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/reset.css" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/bootstrap.2min.css" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
		<script type="text/javascript" src="${ctxStatic}/js/CheckUtils.js"></script>
		<style>
		.menu_tab01 .panel-title em,.menu_tab03 .panel-title em{
	    	color:#797878;
		font-style:normal;
		font-size:14px;
		    margin-left: 4px;
		}
		.list-group{
		  margin-left:0!important;
		}
		.list-group-item{
		 padding-left:46px;
		 background:#f8f8f8;
		}
		.menu_con02 .list-group-item{
		 padding-left:0;
		}
		#left .panel-heading:before{
	    content: "";
	    width: 20px;
	    height: 20px;
	    float: left;
	    background: url(${ctxStatic}/images/test.png) no-repeat;
	    margin-top: 4px;
	    margin-right: 8px;
	 }
	 
	 #left .panel-heading:after {
	 	    content: "";
		    width: 8px;
		    height: 14px;
		    float: right;
		    background: url(${ctxStatic}/images/icon_left.png) no-repeat;
		    margin-top: 6px;
	 }
	  #left .list-group-item:after{
	    content: "";
		    width: 8px;
		    height: 14px;
		    float: right;
		    background: url(${ctxStatic}/images/icon_left.png) no-repeat;
		    margin-top: 3px;
	 }
	 #left .menu_tab:after{
	  	    content: "";
		    width: 14px;
		    height:8px;
		    float: right;
		    background: url(${ctxStatic}/images/icon_up.png) no-repeat;
		    margin-top: 6px;
	 }
	 #left .panel-heading.icon_01:before{
	    background: url(${ctxStatic}/images/1_cur.png) no-repeat;
	 }
	 #left .panel-heading.icon_02:before{
	    background: url(${ctxStatic}/images/2_cur.png) no-repeat;
	 }
	  #left .panel-heading.icon_03:before{
	    background: url(${ctxStatic}/images/3_cur.png) no-repeat;
	 }
	 #left .panel-heading.icon_05:before{
	    background: url(${ctxStatic}/images/4_cur.png) no-repeat;
	 }
	 #left .panel-heading.icon_04:before{
	    background: url(${ctxStatic}/images/5_cur.png) no-repeat;
	 }
	 
	 #left .panel-heading:hover:after,#left .panel-group .panel.cur .panel-heading:after{
	   background:none;
	 }
	 #left .panel-heading:hover a,#left .panel-group .panel.cur .panel-heading a{
 	   color:#fff;
 	 }
 	  #left .panel-heading:hover,#left .panel-group .panel.cur .panel-heading{
 	     background:#40a2fb;
 	       padding-left: 45px;
 	        transition: all 0.3S ease 0s;
 	        padding-right:0;
 	 }
 	 #left .menu_tab:hover{
		 padding-left: 15px;
		 padding-right: 15px;
	 }
	 #left .menu_tab:hover:after, #left .menu_tab.cur:after{
	    background: url(${ctxStatic}/images/icon_down.png) no-repeat;
	 }
	 /*-----------------------------  */

	 #left .panel-heading.icon_01:hover:before,#left .panel-group .panel.cur .panel-heading.icon_01:before{
	    background: url(${ctxStatic}/images/1.png) no-repeat;
	 }
 	#left .panel-heading.icon_02:hover:before,#left .panel-group .panel.cur .panel-heading.icon_02:before{
	    background: url(${ctxStatic}/images/2.png) no-repeat;
	 }
	  #left .panel-heading.icon_03:hover:before,#left .panel-group .panel.cur .panel-heading.icon_03:before{
	    background: url(${ctxStatic}/images/3.png) no-repeat;
	 }
	 #left .panel-heading.icon_05:hover:before,#left .panel-group .panel.cur .panel-heading.icon_05:before{
	    background: url(${ctxStatic}/images/4.png) no-repeat;
	 }
	 #left .panel-heading.icon_04:hover:before,#left .panel-group .panel.cur .panel-heading.icon_04:before{
	    background: url(${ctxStatic}/images/5.png) no-repeat;
	 }

	 /*-------------------------------  */ 
	 iframe#mainFrame{
	  min-height:500px
	 }
	 body{
	background:#f7f7f7;
	}
	.menu_con03 li.cur a{
	 color: #40a2fb!important;
	}
	#userCandle {
	 position:relative;
	}
	#userCandle i{
	position: absolute;
    top: 2px;
    right: 0;
    width: 16px;
    height: 13px;
    transition-duration: .2s;
    -moz-transition-duration: .2s;
    -webkit-transition-duration: .2s;
    -ms-transition-duration: .2s;
    -o-transition-duration: .2s;
    transition-timing-function: linear;
    -moz-transition-timing-function: linear;
    -webkit-transition-timing-function: linear;
    -ms-transition-timing-function: linear;
    -moz-transition-timing-function: linear;
    transition-property: transform;
    -moz-transition-property: -moz-transform;
    -webkit-transition-property: -webkit-transform;
    -ms-transition-property: -ms-transform;
    -o-transition-property: -o-transform;
    }
 	#userCandle i:before {
    content: "";
    display: block;
    width: 0;
    height: 0;
    border-width: 8px 6px 0 6px;
    border-style: solid;
    border-color: #fff transparent transparent;
    position: absolute;
    top: 2px;
    left: 0;
}
#commonHeade{
position:relative;
}
.userli{
position: absolute;
    top: 29px;
    right: 6px;
    width: 178px;
    height: 69px;
    z-index: 2;
    background: #fff;
    box-shadow: 1px 0px 4px #ccc;
    display:none;

}.userli li a{
 font-size:16px;
 color:#333
}
.userli li:nth-of-type(2) {
    width: 100%;
    padding-left: 0;
    text-align:left;
}
.userli li:nth-of-type(2) a{     text-align: left;}
#myModalPhone,#myModalPWD{
width:100%;
margin-left:0!important;
    opacity: 1;
  
    background: rgba(0,0,0,0.6);
}
.modal.in .modal-dialog{
 z-index:9999;
 -webkit-transform:translate(0, 21%);
    -ms-transform: translate(0, 21%);
    -o-transform: translate(0, 21%);
    transform: translate(0, 21%);
    top: 25%;
}
#myModalPhone input,#myModalPWD input{
 height:40px;
}
#myModalPhone .modal-footer,#myModalPWD .modal-footer{
       text-align: center;
}
#myModalPhone .recharge_input,#myModalPWD .recharge_input {
 position:relative;
}
.modal-backdrop, .modal-backdrop.fade.in {
    opacity: .8;
    filter: alpha(opacity=80);
    background: transparent;
}
#myModalPhone .recharge_input span,#myModalPWD .recharge_input span{
    cursor:pointer;
    line-height: 40px;
    position: absolute;
    top: 0;
    padding-right: 10px;
    background: #40a2fb;
    right: 0;
    color: #fff;
    padding-left: 10px;

}
#myModalPhone .recharge_input span.default,#myModalPWD .recharge_input span.default{
    background: #ccc;
}
.btn-primary {
    color: #fff;
    background-color: #40a2fb;
    border-color: #40a2fb;
}
.modal-backdrop{

}
.error_msg{
    position: absolute;
    bottom: 0;
    line-height: 30px;
    color: #f40;
    z-index: 11;
    left: 35%;
    text-align: left;
}
.modal-body{
position:relative;
}
/* 遮盖. */
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
/* 弹窗提示. */
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
</style>
	</head>

	<body>
		<div class="header_wrap" >
			<div class="nav_con">
					<div class="nav_l">
						<div class="pull-left"><img src="${ctxStatic}/images/logo_big.png" /></div>
						<a class="navbar-brand " href="index.html">中投摩根借款后台管理系统</a>
					</div>
					<div class="nav_r pull-right" >
						<ul>
							<li class="fl" id="commonHeade">
								<a href="javascript:;" id="userCandle">您好,${userName}
								<c:if test="${limit != '3' and limit != '5' }">
									<i></i>
								</c:if>
								</a>
								<c:if test="${limit != '3' and limit != '5' }">
									<div class="userli">
							           <ul>
							             <li><a href="javascript:;"  data-target="#myModalPhone" id="modifyMobile" data-toggle="modal">更换手机号</a></li>
							              <li><a href="javascript:;" data-target="#myModalPWD" id="modifyPwd" data-toggle="modal">更改密码</a></li>
							           </ul>
							        </div>
						        </c:if>
							</li>
							<li class="fl">
								<a href="${ctx}/login">退出</a>
							</li>
						</ul>
				  </div>
			</div>
			
		</div>
		<div id="content_wrap">
			
				<div id="left">

					<div class="panel-group" id="accordion">
						<div class="panel panel-default cur" id="companyInfo">
							<div class="panel-heading icon_01 menu_tab_00" >
								<a style="text-decoration:blink;" href="javascript:;" target="mainFrame" jerichotabindex="1"><i class=""></i>&nbsp;企业信息</a>
							</div>
						
							<div class="panel-collapse menu_con03">
								<ul class="list-group">
									<li class="list-group-item cur" id="start">
										<a style="text-decoration:blink;" data-href=".menu3-f3c01cae865445e3aac6f29578719041" href="${ctx}/credit/userinfo/creditUserInfo/creditUserCompanyInfo?id=${id}" target="mainFrame" jerichotabindex="1">开户信息</a>
									</li>
									<c:if test="${isOpeningAnAccount != '2' }">
										<li class="list-group-item menu_tab02" id="tab_basicinfo">
											<a style="text-decoration:blink;" data-href=".menu3-f3c01cae865445e3aac6f29578719041"  href="${ctx}/loan/basicinfo/ztmgLoanBasicInfo/ztmgLoanBasicInfoForm?creditUserId=${id}" target="mainFrame" jerichotabindex="9">基本信息</a>
										</li>
									</c:if>
								</ul>
							</div>
						</div>
						<div class="panel panel-default">
							<div class="panel-heading icon_02">
								<a style="text-decoration:blink;" data-href=".menu3-f3c01cae865445e3aac6f29578719041" href="${ctx}/sys/user/account?id=${id}" target="mainFrame" jerichotabindex="2"><i class=""></i>&nbsp;账户管理</a>
							</div>
						</div>
						<c:if test="${limit != '3' and limit != '5' }">
						<div class="panel panel-default">
							<div class="panel-heading icon_03">
								<a style="text-decoration:blink;" data-href=".menu3-f3c01cae865445e3aac6f29578719041" href="${ctx}/credit/cuoperator/creditUserOperator/list?creditUserId=${id}" target="mainFrame" jerichotabindex="3"><i class=""></i>&nbsp;操作人管理</a>
							</div>
						</div>
						</c:if>
						<c:if test="${limit == '1' || limit == '3' || limit == '2'}">
						<div class="panel panel-default" >
							<div class="panel-heading menu_tab01 icon_04">
								<div class="panel-title fl"><em>借款管理</em><span class=""></span></div>
							</div>
							<div class="panel-collapse menu_con01">
							    <ul class="list-group">
							    	
							    	<c:if test="${limit == '1' || limit == '3'}">
							    		<li class="list-group-item">
							    			 <a style="text-decoration:blink;" data-href=".menu3-f3c01cae865445e3aac6f29578719041" href="${ctx}/sys/user/supplier?middlemenId=${id}" target="mainFrame" jerichotabindex="4">供应商管理</a>
							    		 </li>
							    	 </c:if>	
							    								    	
							    	<li class="list-group-item menu_tab02">
							    	<c:if test="${limit == '2'}">
							    		<a style="text-decoration:blink;" data-href=".menu3-f3c01cae865445e3aac6f29578719041" href="${ctx}/sys/user/project?creditSupplyId=${id}" target="mainFrame" jerichotabindex="5">申请借款</a>
							    	</c:if>	
							    	 <c:if test="${limit == '1' || limit == '3'}">
							    		<a style="text-decoration:blink;" data-href=".menu3-f3c01cae865445e3aac6f29578719041" href="${ctx}/sys/user/project?replaceUserId=${id}" target="mainFrame" jerichotabindex="5">申请借款</a>
							    	</c:if>	 
							    	 
							    	</li>
							    </ul>
							</div>
						</div>						
						<div class="panel panel-default">
							<div class="panel-heading icon_05">
							<%-- 供应商 --%>			
							<c:if test="${limit == '2'}">
					    		<a style="text-decoration:blink;" data-href=".menu3-f3c01cae865445e3aac6f29578719041" href="${ctx}/apply/creditUserApply/loanCreditUserApplyList?creditSupplyId=${id}" target="mainFrame" jerichotabindex="7">项目管理</a>
					    	</c:if>	
					    	<%-- 核心企业 --%>	
					    	 <c:if test="${limit == '1' || limit == '3'}">
					    		<a style="text-decoration:blink;" data-href=".menu3-f3c01cae865445e3aac6f29578719041" href="${ctx}/apply/creditUserApply/loanCreditUserApplyList?creditUserId=${id}" target="mainFrame" jerichotabindex="7">项目管理</a>
					    	</c:if>	
							</div>
						</div>
						<div class="panel panel-default">
							<div class="panel-heading icon_06">
								<%-- 供应商 --%>			
								<c:if test="${limit == '2'}">
						    		<a style="text-decoration:blink;" data-href=".menu3-f3c01cae865445e3aac6f29578719041" href="${ctx}/apply/creditUserApply/repaymentList?creditSupplyId=${id}" target="mainFrame" jerichotabindex="8">还款计划</a>
						    	</c:if>	
						    	<%-- 核心企业 --%>	
						    	 <c:if test="${limit == '1' || limit == '3'}">
						    		<a style="text-decoration:blink;" data-href=".menu3-f3c01cae865445e3aac6f29578719041" href="${ctx}/apply/creditUserApply/repaymentList?creditUserId=${id}" target="mainFrame" jerichotabindex="8">还款计划</a>
						    	</c:if>
								
							</div>
						</div>
						
						
						</c:if>
					</div>
				</div>
				<div id="openClose" class="close" style="display:none">&nbsp;</div>
				<div id="right">
					<iframe id="mainFrame" name="mainFrame"  src=""  scrolling="auto" frameborder="no" width="100%" height="120" onload="this.height=mainFrame.document.body.scrollHeight" ></iframe>
				</div>
		
		   <div id="footer" class="clear">
	                            京ICP备14046134号&nbsp;&nbsp;&nbsp;&nbsp; Copyright © 2018 中投摩根信息技术（北京）有限责任公司 &nbsp;&nbsp;&nbsp;&nbsp;   All Rights Reserved V1.0
			</div>
		</div>
		<!--phone -->
		<div class="modal fade" id="myModalPhone" tabindex="-1" role="dialog" aria-labelledby="phoneLabel">
			<div class="modal-dialog" role="document">
				<div class="modal-content" id="modifyMobile1">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title" id="phoneLabel">修改手机号</h4>
					</div>
					<div class="modal-body recharge_msg">
						<form class="form-horizontal">
							<div class="form-group">
								<label class="control-label pull-left">原手机号</label>
								<div class="recharge_input pull-left">
									<input type="text" class="form-control" value="${creditUserInfo.phone }" disabled="disabled">
								</div>
							</div>							
							<div class="form-group">
								<label class="control-label pull-left">验证码</label>
								<div class="recharge_input pull-left">
								      <span id="btnSendCode" onclick="sendMessage(1)">发送验证码</span>
									<input type="text" class="form-control" id="modifyPhoneCode1">
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary recharge_btn" id="modifyPhoneNext">下一步</button>
					</div>
				</div>
				<div class="modal-content" style="display:none" id="modifyMobile2">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title" id="phoneLabel">修改手机号</h4>
					</div>
					<div class="modal-body recharge_msg">
						<form class="form-horizontal">
							<div class="form-group">
								<label class="control-label pull-left">新手机号</label>
								<div class="recharge_input pull-left">
									<input type="text" class="form-control" id="newMobile" placeholder="请输入手机号码">
								</div>
							</div>							
							<div class="form-group">
								<label class="control-label pull-left">验证码</label>
								<div class="recharge_input pull-left">
								 <span id="btnSendCode02" onclick="sendMessage(2)">发送验证码</span>
								<input type="text" class="form-control" id="modifyPhoneCode2" >
								</div>
							</div>
						</form>
							<div class="error_msg">test!</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary recharge_btn" id="modifyPhoneSubmit">完成</button>
					</div>
				</div>
			</div>
		</div>
		<!--phone-->
		<!--password -->
		<div class="modal fade" id="myModalPWD" tabindex="-1" role="dialog" aria-labelledby="pwdLabel">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title" id="pwdLabel">修改密码</h4>
					</div>
					<div class="modal-body recharge_msg">
						<form class="form-horizontal">
							<div class="form-group">
								<label class="control-label pull-left">原手机号</label>
								<div class="recharge_input pull-left">
									<input type="text" class="form-control" value="${creditUserInfo.phone }" disabled="disabled">
								</div>
							</div>							
							<div class="form-group">
								<label class="control-label pull-left">验证码</label>
								<div class="recharge_input pull-left">
								      <span id="btnSendCode03" onclick="sendMessage(3)">发送验证码</span>
									<input type="text" class="form-control" id="modifyPwdCode">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label pull-left">原密码</label>
								<div class="recharge_input pull-left">
									<input type="password" class="form-control" id="oldPwd"  placeholder="请输入原密码">
								</div>
							</div>							
							<div class="form-group">
								<label class="control-label pull-left">新密码</label>
								<div class="recharge_input pull-left">
								    
									<input type="password" class="form-control" id="newPwd" placeholder="请输入新密码">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label pull-left">再次输入新密码</label>
								<div class="recharge_input pull-left">
									<input type="password" class="form-control" id="newPwd2" placeholder="请再次输入新密码">
								</div>
							</div>
						</form>
						<div class="error_msg">test!</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary recharge_btn" id="modifyPwdSubmit">确定</button>
					</div>
				</div>

			</div>
		</div>
		<!--password-->
		<!-- 遮盖. -->
		<div class="mask_gray"></div>
		<!-- 弹窗提示. -->
		<div class="mask_tip_popup_window">
			<p>温馨提示</p>
			<p>温馨提示请立即完善基本信息，如不填写核心企业则无法进行借款申请，并且无法进行提现操作。</p>
			<div class="mask_li">
			<ul>
				<li onclick="popup_window_cancel();">取消</li>
			<li onclick="popup_window_perfect();">立即完善</li>
			</ul>
			</div>
		</div>

	</body>
	<script type="text/javascript" src="${ctxStatic}/js/jquery.js"></script>
	<script type="text/javascript" src="${ctxStatic}/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/js/jquery.jerichotab.js"></script>
	<script src="${ctxStatic}/jquery-jbox/2.3/jquery.jBox-2.3.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
	<script src="${ctxStatic}/common/mustache.min.js" type="text/javascript"></script>
	<script>
	 $(".menu_tab01").click(function(){
		 $(".menu_con01").slideToggle(200);
	 });	
	 $(".menu_tab_00").click(function(){
		 $(".menu_con03").slideToggle(200);
	 });
	 /*tab切换*/
	 $("#accordion .panel").click(function(){
		 $(this).addClass("cur").siblings().removeClass("cur");

	 });	
	 $(".menu_con01 li,.menu_con03 li").click(function(){
		 $(this).addClass("cur").siblings().removeClass("cur");	
	 });	 

	 var iframe = document.getElementById("mainFrame");
	 var iframeHeight = function() {    
	     var hash = window.location.hash.slice(1), h;
	     if (hash && /height=/.test(hash)) {
	         h = hash.replace("height=", "");
	         iframe.height = h;
	     }
	     setTimeout(iframeHeight, 200);
	 };
	 iframeHeight();

	// 取消.
	function popup_window_cancel() {
		$(".mask_gray").hide();
		$(".mask_tip_popup_window").hide();
	}

	// 立即完善.
	function popup_window_perfect() {
		// iframe.
		$("#mainFrame").attr("src", "${ctx}/loan/basicinfo/ztmgLoanBasicInfo/ztmgLoanBasicInfoForm?creditUserId=${id}");
		// 遮盖隐藏.
		$(".mask_gray").hide();
		// 弹窗隐藏.
		$(".mask_tip_popup_window").hide();
		// 菜单切换，高亮显示.
		var tab_basicinfo=window.document.getElementById("tab_basicinfo");
		$(tab_basicinfo).addClass("cur").siblings().removeClass("cur");
	}

	 $(function(){

		// 开户成功后，弹窗提示，完善基本信息.
		var isState = '${isOpeningAnAccount}';
		// console.log("银行卡认证状态：" + isState);
		var isCreateBasicInfo = '${creditUserInfo.isCreateBasicInfo}';
		if (isState == '1') { // 银行卡，已认证.
			if (isCreateBasicInfo != '1') {
				$(".mask_gray").show();
				$(".mask_tip_popup_window").show();
				/* setTimeout(function() {
					$(".mask_gray").hide();
					$(".mask_tip").hide();
				}, 2000); */
				// console.log("-是否完善基本信息：" + isCreateBasicInfo);
			}
		}
		 
		 
		var url= "${ctx}/credit/userinfo/creditUserInfo/creditUserCompanyInfo?id=${id}";
		var nowType;
		$("#mainFrame").attr("src", url);


		
		//修改手机号
		
		$("#modifyMobile").click(function(){
			$("#modifyMobile1").show();
			$("#modifyMobile2").hide();
			$("#modifyPhoneCode1").val("");
			
		});
		
		$("#modifyPhoneNext").click(function(){
			var mobile = '${creditUserInfo.phone }';
			var smsCode = $("#modifyPhoneCode1").val();
			if(smsCode==""){
				alert("请输入验证码");
				return false;
			}
			//向后台发送处理数据
			$.ajax({
				url: "${ctx}/sys/user/verifyMessage",
				type: "post",
				dataType: "json",
				data: {
					phone: mobile,
					smsCode:smsCode
				},
				success: function(result) {
					if(result.state == "0") {
						
						$("#modifyMobile1").hide();
						$("#modifyMobile2").show();

						window.clearInterval(InterValObj); //停止计时器
						$("#btnSendCode ").attr("onclick","sendMessage(1)").html("获取验证码");
						$("#btnSendCode02").attr("onclick","sendMessage(2)").html("获取验证码");
						$("#btnSendCode03").attr("onclick","sendMessage(3)").html("获取验证码");
					}else{
						alert("请输入正确的验证码");
						return false;
					}
				}
			});
		});
		//修改手机号提交
		$("#modifyPhoneSubmit").click(function(){
			var mobile = $("#newMobile").val();
			var smsCode = $("#modifyPhoneCode2").val();
			if(mobile==""){
				alert("手机号不能为空");
				return false;
			}
			if(!checkPhone(mobile)){
				alert("请输入11位手机号码");
				return false;
			}
			if(smsCode==""){
				alert("请输入验证码");
				return false;
			}
			//向后台发送处理数据
			$.ajax({
				url: "${ctx}/sys/user/verifyMessage",
				type: "post",
				dataType: "json",
				data: {
					phone: mobile,
					smsCode:smsCode
				},
				success: function(result) {
					if(result.state == "0") {
						console.log("修改成功！");
// 						$("#modifyMobile1").hide();
						$("#myModalPhone").hide();
						var phone = ${creditUserInfo.phone };
						$.ajax({
							url: "${ctx}/sys/user/modifyPhone",
							type: "post",
							dataType: "json",
							data: {
								phone: phone,
								newPhone:mobile
							},
							success: function(result) {
								if(result.state == "0") {
									console.log("修改成功！");
//			 						$("#modifyMobile1").hide();
									$("#myModalPhone").hide();
									window.location.href = '${ctx}/login';
								}else{
									alert("请输入正确的验证码");
									return false;
								}
							}
						});
					}else{
						alert("请输入正确的验证码");
						window.clearInterval(InterValObj); //停止计时器
						$("#btnSendCode ").attr("onclick","sendMessage(1)").html("获取验证码");
						$("#btnSendCode02").attr("onclick","sendMessage(2)").html("获取验证码");
						$("#btnSendCode03").attr("onclick","sendMessage(3)").html("获取验证码");
						return false;
					}
				}
			});
		});

		//修改密码
		$("#modifyPwd").click(function(){
			$("#modifyPwdCode").val("");
			$("#oldPwd").val("");
			$("#newPwd").val("");
			$("#newPwd2").val("");
		});
		
		//修改密码提交
		$("#modifyPwdSubmit").click(function(){
			var mobile = ${creditUserInfo.phone };
			var smsCode = $("#modifyPwdCode").val();
			var oldPwd = $("#oldPwd").val();
			var newPwd = $("#newPwd").val();
			var newPwd2 = $("#newPwd2").val();
			//验证码不能为空
			if(smsCode==""){
				alert("验证码不能为空");
				return false;
			}
			//原密码是否为空
			if(oldPwd==""){
				alert("原密码不能为空");
				return false;
			}
			//新密码是否为空
			if(newPwd==""){
				alert("新密码不能为空");
				return false;
			}
			//两次新密码是否一致
			if(newPwd!=newPwd2){
				alert("两次输入密码不一致");
				return false;
			}
			//向后台发送处理数据
			$.ajax({
				url: "${ctx}/sys/user/verifyMessage",
				type: "post",
				dataType: "json",
				data: {
					phone: mobile,
					smsCode:smsCode
				},
				success: function(result) {
					if(result.state == "0") {
						console.log("验证码确认！");
						$.ajax({
							url: "${ctx}/sys/user/modifyPwd",
							type: "post",
							dataType: "json",
							data: {
								phone: mobile,
								oldPwd:oldPwd,
								newPwd:newPwd,
							},
							success: function(result) {
								if(result.state == "0") {
									console.log("修改成功！");
//			 						$("#modifyMobile1").hide();
									$("#myModalPhone").hide();
									window.location.href = '${ctx}/login';
								}else if(result.state == "3"){
									alert("请输入正确的原密码");
									return false;
								}else if(result.state == "2"){
									alert("缺少必要参数");
									return false;
								}else{
									alert("系统错误");
									return false;
								}
							}
						});
					}else{
						alert("请输入正确的验证码");
						window.clearInterval(InterValObj); //停止计时器
						$("#btnSendCode ").attr("onclick","sendMessage(1)").html("获取验证码");
						$("#btnSendCode02").attr("onclick","sendMessage(2)").html("获取验证码");
						$("#btnSendCode03").attr("onclick","sendMessage(3)").html("获取验证码");
						return false;
					}
				}
			});
		});
		
	 $("#commonHeade").hover(function(){
		 $(".userli").toggle();
	});
	 });

	 /*获取验证码倒计时*/
	var InterValObj; //timer变量，控制时间
	var count = 60; //间隔函数，1秒执行
	var curCount; //当前剩余秒数
	 //发送短信验证码
	function sendMessage(type){
		nowType = type
		var mobile;
		curCount = count;
		if(nowType==1){
			mobile = ${creditUserInfo.phone };
		}
		if(nowType==2){
			
			mobile = $("#newMobile").val();
			
		}
		if(nowType==3){
			mobile = ${creditUserInfo.phone };
		}
		if(mobile==""){
			alert("手机号不能为空");
			return false;
		}
		if(!checkPhone(mobile)){
			alert("请输入11位手机号码");
			return false;
		}
		//设置button效果，开始计时
		$("#btnSendCode , #btnSendCode02 ,#btnSendCode03").removeAttr("onclick").html("倒计时" + curCount + "S");
		
	
		//向后台发送处理数据
		$.ajax({
			url: "${ctx}/sys/user/sendMessage",
			type: "post",
			dataType: "json",
			data: {
				phone: mobile
			},
			success: function(result) {
				if(result.state == "0") {
					console.log("验证码已发送！");
					interval(); 
				}else{
					$("#btnSendCode ").attr("onclick","sendMessage(1)").html("获取验证码");
					$("#btnSendCode02").attr("onclick","sendMessage(2)").html("获取验证码");
					$("#btnSendCode03").attr("onclick","sendMessage(3)").html("获取验证码");
				}
			}
		});
	}
	function interval() {
		curCount = count;
		// 设置button效果，开始计时
		$("#btnSendCode , #btnSendCode02 ,#btnSendCode03").removeAttr("onclick").html("倒计时" + curCount + "S");
		InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
	}

	//timer处理函数
	function SetRemainTime() {
		if(curCount == 0) {
			window.clearInterval(InterValObj); //停止计时器
			$("#btnSendCode ").attr("onclick","sendMessage(1)").html("获取验证码");
			$("#btnSendCode02").attr("onclick","sendMessage(2)").html("获取验证码");
			$("#btnSendCode03").attr("onclick","sendMessage(3)").html("获取验证码");
		} else {
			curCount--;
			$("#btnSendCode , #btnSendCode02 ,#btnSendCode03").html("倒计时" + curCount + "S");
		}
	}

	
	</script>

</html>