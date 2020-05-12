<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=9;IE=8;IE=7;IE=EDGE" />
		<title>中投摩根—领先的互联网借贷信息交互平台-关于我们</title>
		<meta content="中投摩根|网络理财|互联网金融|互联网理财|P2P|P2P理财|散标出借|网络投融资平台|互联网金融理财平台|互联网财富管理|互联网财富|互联网财富管理平台|财富出借管理|理财|理财平台|中投摩根财富管理|中投摩根理财" name="keywords" />
		<meta content="中投摩根财富管理平台，中国互联网金融安全理财领航者，出借者首选互联网理财平台，中投摩根在健全的风险管控体系基础上,为出借者提供可信赖的互联网金融理财产品，实现您的金融财富增值！" name="description" />
		<link rel="stylesheet" type="text/css" href="https://v1.cicmorgan.com/css/reset.css">
		<link rel="stylesheet" type="text/css" href="https://v1.cicmorgan.com/css/style.css">

	</head>

	<body>
		<!--页头-->
		<div class="header">
			<div class="header_con">
				<span>欢迎致电：  400-666-9068（9:00－21:00）</span>
				<ul class="header_r">
					<li>
						<a target="_blank" href="http://weibo.com/win11licai?is_all=1"><i class="common_icon sina"></i>官方微博<em></em></a>
					</li>
					<li class="slide_01">
						<a href="javascript:;"><i class="common_icon wechat"></i>官方微信<em></em></a><span class="wechat_slide"></span></li>
					<li>
						<a href="https://www.cicmorgan.com/download.html"><i class="phone"></i>手机客户端</a>
					</li>
				</ul>
			</div>
		</div>
		<!-- nav -->
		<div class="logo">
			<div class="main clearfix">
				<div class="login-pic">
					<a href="https://v1.cicmorgan.com/index.html"> <img src="https://v1.cicmorgan.com/images/icon/logo.png" alt="中投摩根">
					</a>
				</div>
				<div class="login_list" id="divdhcontent">
					<ul id="nav" class="clearfix">
						<li class="index">
							<a href="https://v1.cicmorgan.com/index.html">首页</a>
						</li>
						<li class="lift">
							<a href="https://v1.cicmorgan.com/list.html">出借</a>
						</li>
						<li class="service">
							<a href="https://v1.cicmorgan.com/help.html">帮助中心</a>
						</li>

						<li class="contantli cur">
							<a href="https://v1.cicmorgan.com/disclosure_msg.html" class="">信息披露</a>
						</li>
						
						<li class="new_version"><a href="https://www.cicmorgan.com/index.html" class="">新版</a>
						</li>
						
					</ul>
				</div>
				<div class="Tlogin" id="tlogin">
					<a href="javascript:void(0)" class="active register_open_btn" onclick="loginWindow()">登录</a>
					<a href="https://v1.cicmorgan.com/regist.html">注册</a>
				</div>
				<div class="TRest">
					<a href="https://v1.cicmorgan.com/home_page.html" class="active register_open_btn"></a>
					<a href="javascript:logout();" id="logout"></a>
				</div>
				<span class="sign">签到</span>
			</div>
		</div>

		<!--登录模板 -start -->
		<div class="wrap_gray"></div>
		<div class="register_wrap">
			<div class="register_logo"></div>
			<div class="register_one">
				<label for="">账号<span>*</span></label>
				<input type="text" placeholder="请输入手机号" id="mobile" name="mobile">
			</div>
			<div class="register_one">
				<label for="">密码<span>*</span></label>
				<input type="password" placeholder="请输入密码" id="pwd" name="pwd">
			</div>
			<div class="TBLgNameRem">
				<div style="display: none;margin-left: 9.5rem;" id="checkLogin">
					<!--<div class="TBLgNameRemicon"></div>-->
					<a class="error_hidden" style="color: #a14058;">手机号码格式错误</a>
					<div class="clear"></div>
				</div>
			</div>
			<div class="register_one">
				<a class="register_btn" onclick="login()">登录</a>
			</div>
			<div class="register_other">
				<a href="regist.html">注册新账号</a>
				<a href="forget_password.html">忘记密码？</a>
			</div>
		</div>
		<!--登录模板 -end -->
		<div class="service_nav">
			<ul>
				<li class="service_chat">
					<a target="_blank" href="https://v2.live800.com/live800/chatClient/chatbox.jsp?companyID=462135&amp;configID=124467&amp;jid=2666583706&amp;s=1"></a>
				</li>
				<div class="service_bar"></div>
				<li class="service_phone">
					<a href="#"></a>
					<p class="service_position service_phone_msg">客服电话：400-666-9068</p>
				</li>
				<div class="service_bar"></div>
				<li class="service_code">
					<a href="#"></a>
					<div class="service_position code_tab">
						<span>下载app</span><span>关于微信</span>
						<div class="service_code_msg">
							<dl>
								<dt><img src="https://v1.cicmorgan.com/images/link_photo/service_code01.png" alt=""></dt>

							</dl>
							<dl>
								<dt><img src="https://v1.cicmorgan.com/images/link_photo/service_code01.png" alt=""></dt>

							</dl>

						</div>
					</div>

				</li>
				<div class="service_bar"></div>
				<li class="service_scrolltop" onclick="goTop()">
					<a href="#"></a>
				</li>
			</ul>
		</div>

		<!--页头结束->

   <!-- 团队管理 strat-->

		<div class="contact-boxbg">
			<div align="center" id="aboutdetail" class="aboutdetail">
				<div class="top">${notice.id }</div>
				<div class="db">媒体来源：${notice.sources }&nbsp;&nbsp;${notice.createDate }</div>
				<div class="down">${notice.text }</div>
			</div>
			<div class="clear"></div>
		</div>

		<!-- 团队管理 end-->

		<!--页脚-->
		<div class="footerbg">
			<div class="sitemap">
				<div class="main">
					<ul class="clearfix">
						<li class="us">
							<ul>
								<li class="title">
									<a href="https://v1.cicmorgan.com/aboutus_companyintroduce.html">中投摩根简介</a>
								</li>

								<li class="mtd">
									<a href="https://v1.cicmorgan.com/disclosure_about_contact.html">联系我们</a>
								</li>
								<li class="mtd">
									<a href="https://v1.cicmorgan.com/aboutus_investor_relations.html">媒体报道</a>
								</li>
								<li class="mtd">
									<a href="https://v1.cicmorgan.com/aboutus_investor_relations.html">平台公告</a>
								</li>
							</ul>
						</li>
						<li style="float: left; margin-right: 40px"><img rc="https://v1.cicmorgan.com/images3/line.png"></li>
						<li class="safe">
							<ul>
								<li class="title">
									<a href="https://v1.cicmorgan.com/help.html">帮助中心</a>
								</li>
								<li class="mt">
									<a href="https://v1.cicmorgan.com/disclosure_about_introduce.html">公司简介</a>
								</li>
								<li class="mtd">
									<a href="https://v1.cicmorgan.com/help_item.html?login_jump">注册与登录</a>
								</li>
								<li class="mtd">
									<a href="https://v1.cicmorgan.com/help_item.html?recharge_jump">充值与提现</a>
								</li>
							</ul>
						</li>
						<li class="help">
							<ul>
								<li class="title">
									<a href="https://v1.cicmorgan.com/help.html">新手帮助</a>
								</li>
								<li class="mt">
									<a href="https://v1.cicmorgan.com/investment_protocol.html">服务协议</a>
								</li>
								<li class="mtd">
									<a href="https://v1.cicmorgan.com/help_item.html">常见问题</a>
								</li>
							</ul>
						</li>
						<li class="sor">
							<ul>
								<li class="title">客服电话：</li>
								<li class="sorphone">400-666-9068</li>
								<li class="sortime">工作日：9:00 - 21：00 &nbsp;&nbsp;<br>&nbsp;周末：9:00 - 18:00</li>
								<li class="fuwu_ma clearfix">
									<div class="erweima fl">
										<img height="94" src="https://v1.cicmorgan.com/images/win_pc/server.png">
									</div>
									<div class="ertxt fl">立即关注<br>中投摩根服务号</div>
								</li>
							</ul>
						</li>
						<li class="contant">
							<div class="email">
								<div class="emailname">客服邮箱</div>
								<div class="emailtext">Kefu@cicmorgan.com</div>
							</div>
							<div class="qqC">
								<div class="qqCname">QQ官方群</div>
								<div class="qqCS">中投摩根-出借队 416337029</div>
								<div class="qqCS" style="margin-right: 0px">中投摩根-预备队 109027820
								</div>
								<div style="clear: both"></div>
							</div>
							<div class="fuwu_ma clearfix">
								<div class="erweima fl">
									<img height="94" src="https://v1.cicmorgan.com/images/win_pc/ding.png">
								</div>
								<div class="ertxt fl">立即关注<br>中投摩根订阅号</div>
							</div>
						</li>
					</ul>
				</div>
			</div>
			<div class="invest_btn_div_msg">
				* 预期收益并非平台承诺收益，市场有风险，出借需谨慎
			</div>
			<div class="copyright">

				<div class="cen" style="padding-top:30px;">
					<div class="DTTEXT">京ICP备14046134号</div>
					<div class="DTTEXT">Copyright © 2014-2015 中投摩根信息技术（北京）有限责任公司 All Rights Reserved</div>
				</div>
				<div style="height: 8px; width: 100%"></div>
			</div>
		</div>
	</body>
	<script type="text/javascript" src="https://v1.cicmorgan.com/js/jquery.js"></script>
	<script type="text/javascript" src="https://v1.cicmorgan.com/js/jquery.cookie.js"></script>
	<script type="text/javascript" src="https://v1.cicmorgan.com/js/common.js"></script>
	<script type="text/javascript" src="https://v1.cicmorgan.com/js/aboutus_detail.js"></script>

</html>