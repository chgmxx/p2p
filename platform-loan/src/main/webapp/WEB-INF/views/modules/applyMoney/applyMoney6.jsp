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
</head>
<body>
<div class="bg_height">
	<div class="nav_head">借款申请</div>


		<div id="messageBox" class="alert alert-success " style="display: none;">缺少必要参数</div>
		<!--账户管理-->
		<div class="loan_apply">
			<!-- <h1>
				借款申请<span>需求编号:保存后自动生成</span><span>创建者:张三</span><span>创建时间:2018-03-12</span>
			</h1> -->
			<div class="loan_apply_wrap">
				<div class="la_tip">温馨提示：协议各方签订完成，融资申请状态流转至审核中！</div>
				<div class="la_tab">
					<div class="clear">
						<ul>
							<li class="" id="tab-1"><i>1</i><span>融资类型</span></li>
							<li class="" id="tab-2"><i>2</i><span>选择供应商</span></li>
							<li class="" id="tab-3"><i>3</i><span>基础交易</span></li>
							<li class="" id="tab-4"><i>4</i><span>上传资料</span></li>
							<li class="" id="tab-5"><i>5</i><span>融资金额</span></li>
							<li class="cur" id="tab-6"><i>6</i><span>签订协议</span></li>
						</ul>
					</div>
				</div>
				<div class="la_con la_new_con">
				
					<div class="la_step_four clear la_step_six">
						<div class="la_step_six_p">签订协议列表（以下协议最终以各方盖章的为准）</div>
						<table summary="为自由而努力" class="imageTable">
							<thead>
								<tr>
									<th scope="col">协议名称</th>
									<th scope="col">各方</th>
									<th scope="col">操作</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>供应链融资合作框架协议/融资申请书（应收账款质押）</td>
									<td>已签订</td>
									<td><button class="btn clear" ><a href="${srcPdfFile}" target="_blank" download="${srcPdfFile}" id="downloanPack">下载合同</a></button></td>
								</tr>
								<tr>
									<td>借款人网络借贷风险、禁止性行为及有关事项提示书</td>
									<td>已签订</td>
									<td><button class="btn clear" ><a href="${declarationFilePath}" target="_blank" download="${declarationFilePath}" id="downloanPack">下载合同</a></button></td>
								</tr>
								<tr>
									<td>应收账款质押登记协议</td>
									<td>已签订</td>
									<td><button class="btn clear" ><a href="${zdFilePath}" target="_blank" download="${zdFilePath}" id="downloanPack">下载合同</a></button></td>
								</tr>
								<tr>
									<td>授权函</td>
									<td>已签订</td>
									<td><button class="btn clear" ><a href="${shCisFilePath}" target="_blank" download="${shCisFilePath}" id="downloanPack">下载合同</a></button></td>
								</tr>
							</tbody>
						</table>
						<%-- <table border="" cellspacing="" cellpadding="">
							<tr>
								<th>协议名称</th>
								<th>甲方</th>
								<th>乙方</th>
								<th>丙方</th>
								<th>操作</th>
							</tr>
							<tr>
								<td>应收账款转让申请书</td>
								<td>已签订</td>
								<td>已签订</td>
								<td></td>
								<td><button class="btn clear" ><a href="${srcPdfFile}" target="_blank" download="${srcPdfFile}" id="downloanPack">下载合同</a></button></td>
							</tr>
						</table> --%>
					</div>
				</div>
			</div>
		</div>
		<!--确认签订 弹框 -->
		</div>
</body>

</html>