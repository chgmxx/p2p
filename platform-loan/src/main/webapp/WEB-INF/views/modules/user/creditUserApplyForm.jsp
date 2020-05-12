<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta name="renderer" content="webkit">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<title>中投摩根—领先的互联网借贷信息交互平台-账户管理</title>
		<meta content="P2P理财,投资理财,个人理财,网上投资,供应链金融,互联网金融,网贷平台,中投摩根" name="keywords" />
		<meta content="中投摩根财富管理平台,中国互联网金融安全出借领航者,出借者首选互联网出借平台,中投摩根在健全的风险管控体系基础上,为出借者提供可信赖的互联网金融出借产品,实现您的金融财富增值." name="description" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/reset.css" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/lightbox.css" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
		<script type="text/javascript" src="${ctxStatic}/js/jquery.js"></script>
		<script src="${ctxStatic}/js/lightbox.js" type="text/javascript"></script>
		<script type="text/javascript">
		var serviceRate = "${serviceRate}";
		$(document).ready(function() {
			if(serviceRate!=""){
				serviceRate = parseFloat(serviceRate);
				var num = ${(creditUserApply.amount *serviceRate/36500)*creditUserApply.span};
				var numT = formatCurrency(num);
				$("#interest").val(numT);
			}
		});
		
		
		function formatCurrency(num) {
			num = num.toString().replace(/\$|\,/g, '');
			if(isNaN(num))
				num = "0";
			sign = (num == (num = Math.abs(num)));
			num = Math.floor(num * 100 + 0.50000000001);
			cents = num % 100;
			num = Math.floor(num / 100).toString();
			if(cents < 10)
				cents = "0" + cents;
			for(var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ',' +
				num.substring(num.length - (4 * i + 3));
			return(((sign) ? '' : '-') + num + '.' + cents);
		}
		</script>
	<style>
			.la_step {
				padding: 0;
			}
			
			button[disabled],
			html input[disabled] {
				background: #fff;
				border: 1px solid #ccc;
			}
			
			.la_step>div:nth-of-type(1) {
				padding-right:40px;
				border-right: 1px dashed #ccc;
				color: #999;
			}
			
			.la_step>div:nth-of-type(2) {
				padding-left: 25px;
			}
			
			.la_step {
				padding: 20px 50px;
				border-bottom: 10px solid #f2f2f2;
				overflow: hidden;
			}
			
			.la_form a {
				line-height: 40px;
				padding-left: 20px;
				font-size: 16px;
			}
			
			.la_table {
				border-color: #ccc;
				margin-top: 50px;
				margin-bottom: 50px;
			}
			
			.la_table table {
			
				border-color: #ccc;
				text-align: center;
			    width:100%;
				border: 0;
				font-size: 16px;
			}
			
			.la_table table th {
				border-color: #ccc;
				text-align: center;
				padding: 10px 0;
			}
			
			.la_table table td {
				border-color: #ccc;
				padding: 10px 0;
			}
			
			.la_table table td span {
				color: #36a7e7;
				cursor: pointer;
			}
			.la_border{
				border-top:10px solid #f2f2f2;
			}
			.label_ip{
			    background: #fff;
			    border: 1px solid #ccc;
			    width: 300px;
			    padding-left: 10px;
			    line-height: 40px;
			    height: 40px;
			    color: #666;
			    border-radius: 6px;
			    float: left;
			    display: block;
			    font-size: 16px;
			}
			.la_table table td img{
			width:100px;
			height:100px;
			}
			.loan_apply_wrap{
			  padding-left:0;
			  margin-bottom:0;
			  padding-bottom:0;
			}
			.la_step,.loan_apply{
			padding:0;
			}
			.la_form input,.label_ip{
			width:220px;}
			.la_step_three label{
			 width:82px;
			}
			.la_step_three {
			padding-bottom:30px;
			}
		</style>
<body>
	<div class="nav_head">借款申请</div>
					<!--账户管理-->
					<div class="loan_apply">
						<b class="nav_head">融资申请信息</b>
						<div class="loan_apply_wrap">
							<div class="la_con">
								<div class="la_step la_step_three cur">
									<div class="pull-left">
										<div class="la_form">
											<label for="" class="pull-left">融资类型</label>
											<c:if test="${creditUserApply.financingType != '2'}">
												<input type="text" class="pull-left" value="应收账款质押" disabled="disabled"/> 
											</c:if>
											<c:if test="${creditUserApply.financingType == '2'}">
												<input type="text" class="pull-left" value="订单融资" disabled="disabled"/> 
											</c:if>
										</div>
										<div class="la_form">
											<label for="" class="pull-left">还款方</label>
											<input type="text" class="pull-left" value="${creditUserApply.creditPack.loanName}" disabled="disabled" />
										</div>
										<div class="la_form">
											<label for="" class="pull-left">融资方</label>
											<input type="text" name="" id="" value="${creditUserApply.creditPack.loanName}" disabled="disabled" />
										</div>
										<div class="la_form">
											<label for="" class="pull-left">合同名称</label>
											<input type="text" name="" id="" value="${creditUserApply.creditPack.name}" disabled="disabled" />
										</div>
										<div class="la_form">
											<label for="" class="pull-left">合同编号</label>
											<input type="text" name="" id="" value="${creditUserApply.creditPack.no}" disabled="disabled" />
										</div>
										<div class="la_form">
											<label for="" class="pull-left">合同金额</label>
											<input type="number" name="" id="" value="${creditUserApply.creditPack.money}" disabled="disabled" />
										</div>
										<div class="la_form">
											<label for="" class="pull-left">合同类型</label>
											<c:if test="${creditUserApply.creditPack.type == '1'}">
												<input type="text" name="" id="" value="贸易合同" disabled="disabled" />
											</c:if>
											<c:if test="${creditUserApply.creditPack.type == '2'}">
												<input type="text" name="" id="" value="联营合同" disabled="disabled" />
											</c:if>
											<c:if test="${creditUserApply.creditPack.type == '3'}">
												<input type="text" name="" id="" value="购销合同" disabled="disabled" />
											</c:if>
											
										</div>
										<div class="la_form">
											<label for="" class="pull-left">合同有效期</label>
											<div class="label_ip"><fmt:formatDate value="${creditUserApply.creditPack.userdDate}" pattern="yyyy-MM-dd"/></div>
										</div>
									</div>
									<div class="pull-left">
										<div class="la_form">
											<label for="" class="pull-left">应收账款</label>
											<input type="text" class="pull-left" value="${creditUserApply.amount}" disabled="disabled" />
										</div>
										<div class="la_form">
											<label for="" class="pull-left">融资期限</label>
											<input type="text" class="pull-left" value="${creditUserApply.span}" disabled="disabled" />
										</div>
										<div class="la_form">
											<label for="" class="pull-left">融资利率</label>
											<input type="text" class="pull-left" value="${creditUserApply.lenderRate}" disabled="disabled" />
										</div>
										<div class="la_form">
											<label for="" class="pull-left">还款方式</label>
											<input type="text" class="pull-left" value="到期还本付息" disabled="disabled" />
										</div>
										<div class="la_form">
											<label for="" class="pull-left">计息方式</label>
											<input type="text" class="pull-left" value="放款计息" disabled="disabled" />
										</div>
										<div class="la_form">
											<label for="" class="pull-left">服务费</label>
											<input type="text" class="pull-left" value="" disabled="disabled" id="interest"/>
										</div>
										<div class="la_form">
											<label for="" class="pull-left">状态</label>
											<c:if test="${creditUserApply.state == '0' }">
												<input type="text" class="pull-left" value="草稿" disabled="disabled" />
											</c:if>
											<c:if test="${creditUserApply.state == '1' }">
												<input type="text" class="pull-left" value="审核中" disabled="disabled" />
											</c:if>
											<c:if test="${creditUserApply.state == '2' }">
												<input type="text" class="pull-left" value="审核通过" disabled="disabled" />
											</c:if>
											<c:if test="${creditUserApply.state == '3' }">
												<input type="text" class="pull-left" value="审核驳回" disabled="disabled" />
											</c:if>
											<c:if test="${creditUserApply.state == '4' }">
												<input type="text" class="pull-left" value="融资中" disabled="disabled" />
											</c:if>
											<c:if test="${creditUserApply.state == '5' }">
												<input type="text" class="pull-left" value="还款中" disabled="disabled" />
											</c:if>
											<c:if test="${creditUserApply.state == '6' }">
												<input type="text" class="pull-left" value="结束" disabled="disabled" />
											</c:if>
										</div>
										<div class="la_form">
											<label for="" class="pull-left">签订日期</label>
											<div class="label_ip"><fmt:formatDate value="${creditUserApply.creditPack.signDate}" pattern="yyyy-MM-dd"/></div>
										</div>
									</div>
								</div>
								<c:if test="${creditUserApply.financingType != '2' }">
									<b class="nav_head">签订协议信息</b>
									<div class="la_table">
										<table border="" cellspacing="" cellpadding="">
											<tr>
												<th>协议名称</th>
												<th>下载地址</th>
											</tr>
											<tr>
												<td>供应链融资合作框架协议/融资申请书（应收账款质押）</td>
												<td>
													<c:if test="${creditUserApply.borrPurpose != '' and  creditUserApply.borrPurpose !=null}">
														<a href="${downpath}${creditUserApply.borrPurpose}" target="_blank" download="${downpath}${creditUserApply.borrPurpose}">下载</a> 
													</c:if>
												</td>
											</tr>
											<tr>
												<td>借款人网络借贷风险、禁止性行为及有关事项提示书</td>
												<td>
													<c:if test="${creditUserApply.declarationFilePath != '' and  creditUserApply.declarationFilePath !=null}">
														<a href="${downpath}${creditUserApply.declarationFilePath}" target="_blank" download="${downpath}${creditUserApply.declarationFilePath}">下载</a> 
													</c:if>
												</td>
											</tr>
											<tr>
												<td>应收账款质押登记协议</td>
												<td>
													<c:if test="${creditUserApply.zdFilePath != '' and  creditUserApply.zdFilePath !=null}">
														<a href="${downpath}${creditUserApply.zdFilePath}" target="_blank" download="${downpath}${creditUserApply.zdFilePath}">下载</a> 
													</c:if>
												</td>
											</tr>
											<tr>
												<td>授权函</td>
												<td>
													<c:if test="${creditUserApply.shCisFilePath != '' and  creditUserApply.shCisFilePath !=null}">
														<a href="${downpath}${creditUserApply.shCisFilePath}" target="_blank" download="${downpath}${creditUserApply.shCisFilePath}">下载</a> 
													</c:if>
												</td>
											</tr>
										</table>
									</div>
								</c:if>
								<c:if test="${creditUserApply.financingType == '2' }">
									<c:if test="${creditOrder != '' and creditOrder!=null }">
										<b class="nav_head">上传订单信息</b>
										<div class="la_table">
											<table border="" cellspacing="" cellpadding="">
												<tr>
													<th>订单编号</th>
													<th>订单金额</th>
													<th>订单图片</th>
												</tr>
												<tr>
													<td>${creditOrder.no}</td>
													<td>${creditOrder.money}</td>
													<td><a href="${staticPath}/upload/image/${creditOrder.url}" class="example-image-link" data-lightbox="example-${status.index}"><img alt="" src="${staticPath}/upload/image/${creditOrder.url}" class="example-image"></a></td>
												</tr>
											</table>
										</div>
									</c:if>
								</c:if>
								<c:if test="${creditUserApply.financingType != '2' }">
									<b class="nav_head">上传发票信息</b>
									<div class="la_table">
										<table border="" cellspacing="" cellpadding="">
											<tr>
												<th>发票编号</th>
												<th>发票金额</th>
												<th>发票图片</th>
											</tr>
											<c:forEach items="${voucherList}" var="voucher" varStatus="status">
											<tr>
												<td>${voucher.no}</td>
												<td>${voucher.money}</td>
												<td><a href="${staticPath}/upload/image/${voucher.annexFile.url}" class="example-image-link" data-lightbox="example-${status.index}"><img alt="" src="${staticPath}/upload/image/${voucher.annexFile.url}" class="example-image"></a></td>
											</tr>
											</c:forEach>
										</table>
									</div>
								</c:if>
							</div>
							<div class="la_border">
								<b class="nav_head">上传资料信息</b>
								<div class="la_table">
									<table border="" cellspacing="" cellpadding="">
										<tr>
											<th>类型</th>
											<th>预览</th>
										</tr>
										<c:forEach items="${annexFileList}" var="annexFile" varStatus="statu">
										<tr>
											<td>${annexFile.remark}</td>
											<td>
												<c:choose>
													<c:when test="${annexFile.type == '7'}">
														<a href="${staticPath}${annexFile.url}" target="_blank">查看</a>
													</c:when>
													<c:otherwise>
														<a href="${staticPath}/upload/image/${annexFile.url}" class="example-image-link" data-lightbox="example-${statu.index}"><img alt="" src="${staticPath}/upload/image/${annexFile.url}"  class="example-image"></a>
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
										</c:forEach>
									</table>
								</div>
							</div>
					</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
</body>
</html>