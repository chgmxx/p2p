<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta name="renderer" content="webkit">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<title>中投摩根—领先的互联网借贷信息交互平台</title>
		<meta content="P2P理财,投资理财,个人理财,网上投资,供应链金融,互联网金融,网贷平台,中投摩根" name="keywords" />
		<meta content="中投摩根财富管理平台,中国互联网金融安全出借领航者,出借者首选互联网出借平台,中投摩根在健全的风险管控体系基础上,为出借者提供可信赖的互联网金融出借产品,实现您的金融财富增值." name="description" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/reset.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css" />

	</head>

	<body>
		<div class="header">

			<div class="navbar navbar-fixed-top">
				<div class="container-fluid">
					<div class="nav-inner">
						<div class="pull-left"><img src="${pageContext.request.contextPath}/images/logo.png" /></div>
						<a class="navbar-brand " href="index.html">中投摩根信贷平台支持系统</a>

					</div>
					<div class="nav pull-right">
						<ul class="navbar-nav">
							<li class="">
								<a href="javascript:;">用户昵称</a>
							</li>
							<li>
								<a href="javascript:;">退出</a>
							</li>
							<li>
								<a href="javascript:;">帮助中心</a>
							</li>
							<li>
								<a href="javascript:;">设置</a>
							</li>

						</ul>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jerichotab.js"></script>
	<script>
		var leftWidth = 160; // 左侧窗口大小
		var tabTitleHeight = 33; // 页签的高度
		var htmlObj = $("html"),
			mainObj = $("#main");
		var headerObj = $("#header"),
			footerObj = $("#footer");
		var frameObj = $("#left, #openClose, #right, #right iframe");

		function wSize() {
			var minHeight = 500,
				minWidth = 980;
			var strs = getWindowSize().toString().split(",");
			htmlObj.css({
				"overflow-x": strs[1] < minWidth ? "auto" : "hidden",
				"overflow-y": strs[0] < minHeight ? "auto" : "hidden"
			});
			mainObj.css("width", strs[1] < minWidth ? minWidth - 10 : "auto");
			frameObj.height((strs[0] < minHeight ? minHeight : strs[0]) - headerObj.height() - footerObj.height() - (strs[1] < minWidth ? 42 : 28));
			$("#openClose").height($("#openClose").height() - 5); //  
			$(".jericho_tab iframe").height($("#right").height() - tabTitleHeight); // 
			wSizeWidth();
		}

		function wSizeWidth() {
			if(!$("#openClose").is(":hidden")) {
				var leftWidth = ($("#left").width() < 0 ? 0 : $("#left").width());
				$("#right").width($("#content").width() - leftWidth - $("#openClose").width() - 5);
			} else {
				$("#right").width("100%");
			}
		} //  
		function openCloseClickCallBack(b) {
			$.fn.jerichoTab.resize();
		} //

		$(document).ready(function() {
			//  初始化页签
			$.fn.initJerichoTab({
				renderTo: '#right',
				uniqueId: 'jerichotab',
				contentCss: {
					'height': $('#right').height() - tabTitleHeight
				},
				tabs: [],
				loadOnce: true,
				tabWidth: 110,
				titleHeight: tabTitleHeight
			}); //
			// 绑定菜单单击事件
			$("#menu a.menu").click(function() {
				// 一级菜单焦点
				$("#menu li.menu").removeClass("active");
				$(this).parent().addClass("active");
				// 左侧区域隐藏
				if($(this).attr("target") == "mainFrame") {
					$("#left,#openClose").hide();
					wSizeWidth();
					//  隐藏页签
					$(".jericho_tab").hide();
					$("#mainFrame").show(); //
					return true;
				}
				// 左侧区域显示
				$("#left,#openClose").show();
				if(!$("#openClose").hasClass("close")) {
					$("#openClose").click();
				}
				// 显示二级菜单
				var menuId = "#menu-" + $(this).attr("data-id");
				if($(menuId).length > 0) {
					$("#left .accordion").hide();
					$(menuId).show();
					// 初始化点击第一个二级菜单
					if(!$(menuId + " .accordion-body:first").hasClass('in')) {
						$(menuId + " .accordion-heading:first a").click();
					}
					if(!$(menuId + " .accordion-body li:first ul:first").is(":visible")) {
						$(menuId + " .accordion-body a:first i").click();
					}
					// 初始化点击第一个三级菜单
					$(menuId + " .accordion-body li:first li:first a:first i").click();
				} else {
					// 获取二级菜单数据
					$.get($(this).attr("data-href"), function(data) {
						if(data.indexOf("id=\"loginForm\"") != -1) {
							alert('未登录或登录超时。请重新登录，谢谢！');
							top.location = "/erp/a";
							return false;
						}
						$("#left .accordion").hide();
						$("#left").append(data);
						// 链接去掉虚框
						$(menuId + " a").bind("focus", function() {
							if(this.blur) {
								this.blur()
							};
						});
						// 二级标题
						$(menuId + " .accordion-heading a").click(function() {
							$(menuId + " .accordion-toggle i").removeClass('icon-chevron-down').addClass('icon-chevron-right');
							if(!$($(this).attr('data-href')).hasClass('in')) {
								$(this).children("i").removeClass('icon-chevron-right').addClass('icon-chevron-down');
							}
						});
						// 二级内容
						$(menuId + " .accordion-body a").click(function() {
							$(menuId + " li").removeClass("active");
							$(menuId + " li i").removeClass("icon-white");
							$(this).parent().addClass("active");
							$(this).children("i").addClass("icon-white");
						});
						// 展现三级
						$(menuId + " .accordion-inner a").click(function() {
							var href = $(this).attr("data-href");
							if($(href).length > 0) {
								$(href).toggle().parent().toggle();
								return false;
							}
							//  打开显示页签
							return addTab($(this)); // 
						});
						// 默认选中第一个菜单
						$(menuId + " .accordion-body a:first i").click();
						$(menuId + " .accordion-body li:first li:first a:first i").click();
					});
				}
				// 大小宽度调整
				wSizeWidth();
				return false;
			});
			// 初始化点击第一个一级菜单
			$("#menu a.menu:first span").click();
			//  下拉菜单以选项卡方式打开
			$("#userInfo .dropdown-menu a").mouseup(function() {
				return addTab($(this), true);
			}); // 
			// 鼠标移动到边界自动弹出左侧菜单
			$("#openClose").mouseover(function() {
				if($(this).hasClass("open")) {
					$(this).click();
				}
			});
			// 获取通知数目  
			function getNotifyNum() {
				$.get("/erp/a/oa/oaNotify/self/count?updateSession=0&t=" + new Date().getTime(), function(data) {
					var num = parseFloat(data);
					if(num > 0) {
						$("#notifyNum,#notifyNum2").show().html("(" + num + ")");
					} else {
						$("#notifyNum,#notifyNum2").hide()
					}
				});
			}
			getNotifyNum(); //
			setInterval(getNotifyNum, 60000); //
		});
		//  添加一个页签
		function addTab($this, refresh) {
			$(".jericho_tab").show();
			$("#mainFrame").hide();
			$.fn.jerichoTab.addTab({
				tabFirer: $this,
				title: $this.text(),
				closeable: true,
				data: {
					dataType: 'iframe',
					dataLink: $this.attr('href')
				}
			}).loadData(refresh);
			return false;
		} //
		/*左侧导航*/
         $(".menu_tab").click(function(){
         	$(".menu_con01").toggle(200);
         });
         $(".menu_tab02").click(function(){
         	$(".menu_con02").toggle(200);
         	
         });
	</script>
	<script>
		$("#companyInfo").click(function(){
			window.location = "company/companyInfo.jsp";
		});
	</script>
	<script type="text/javascript" src="js/wsize.min.js"></script>

</html>