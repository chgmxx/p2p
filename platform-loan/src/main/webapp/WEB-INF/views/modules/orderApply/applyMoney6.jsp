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
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
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
	var creditApplyId = "${creditUserApply.id}";//借款申请id
	var step = "${step}";
	$(function() {

		/**
		 * 借款人网络借贷风险、禁止性行为及有关事项提示书.
		 */
		// 融资方.
		$("#company_name").text("${supplyUser.enterpriseFullName}");
		// 借款申请编号.
		$("#credit_user_apply_id").text("${creditUserApply.id}");
		// 年化利率.
		$("#credit_user_rate_id").text("${creditUserApply.lenderRate}" + "%");
		// 服务费率.
		$("#credit_user_services_rate_id").text("${serviceRate}" + "%");
		// 声明文件.
		$(".agreement_01").click(function() {
			$(".mask_protocol_signature").show();
		});
		// 关闭弹框.
		$(".close").click(function() {
			$(".mask_protocol_signature").hide();
		});
		// 声明文件，同意.
		$(".mask_protocol_signature .read_agreen").click(function() {
			$(".mask_protocol_signature").hide();
			$(".agreement_01 span").addClass("cur");
			$("input[id='prompt_book_id']").attr("checked", "true");
			$(".mask_gray").show();
			$(".mask_tip").show();
		});
		// 声明文件，取消.
		$(".mask_protocol_signature .read_close").click(function() {
			$(".mask_protocol_signature").hide();
			$(".agreement_01 span").removeClass("cur");
			$("input[id='prompt_book_id']").removeAttr("checked");
		});

		//判断正常申请还是显示数据
		if(step==""){//正常申请
			
		}else{//显示数据
			var path = '${creditAnnexFile.url}';
			var creditAnnexFileId = '${creditAnnexFile.id}';
			if(path!=''){
// 				$(".file7").hide();
// 				$("#paymentFile").children("a").attr("href", path);
// 				$("#paymentFile").children("a").attr("download", path);
// 				$("#paymentFile").show();
// 				$("#paymentId").val(creditAnnexFileId);
// 				$("#createTemplate").hide();
			}			
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
		
		//担保函
		$(".file7").on("change", function() {
			closeMessage();
// 			document.getElementById('textfield_02').innerHTML=this.value;
			var file = this.files[0];
			var $this = $(this);
			var val=$this.val();
			// 			     $this.parent().siblings(".info_error_msg").hide();
			var formData = new FormData();
			var type = $this.attr("id");
			// 				var count = $this.parent().siblings(".div_imglook").children(".lookimg_wrap").length;

			formData.append("type", type);
			formData.append("creditInfoId", creditInfoId);
			formData.append("file1", file);
			formData.append("creditApplyId", creditApplyId);
			var element7 = $("#flie_wrap_7");
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
						var path = result.path;
// 						$(".file7").hide();
// 						$("#paymentFile").children("a").attr("href", path);
// 						$("#paymentFile").children("a").attr("download", path);
// 						$("#paymentFile").show();
// 						$("#paymentId").val(annexFileId);

						var str='<div class="" id="'+annexFileId+'">'+
								'<div>'+val+'</div>'+
								'<a href="'+path+'" download="'+path+'" class="" style="padding-right: 20px">下载</a>'+
								'<span onclick="deletePayment(\''+annexFileId+'\')">删除</span>'+
								'<input type="text" style="display: none" value='+annexFileId+' id="paymentId" />'+
								'</div>'
						element7.append(str);
					} else {
// 						$("#paymentFile").hide();
// 						$(".file7").show();
						errMessage(result.message);
					}

				}
			});

		});


	});

	//生成承诺函
	function createTemplate(){
		$.ajax({
			url : ctxpath + "/creditInfo/createTemplate",
			type : 'post',
			dataType : 'json',
			data : {
				creditInfoId: creditInfoId,
				supplyUserId:userInfoId,
				creditApplyId:creditApplyId
			},
			success : function(result) {
				if (result.state == 0) {
					$("#createTemplate").hide();
					$("#downTemplate").show();
					$("#downTemplate").attr("href","${downpath}"+result.pdfStr);
					$("#downTemplate").attr("download","${downpath}"+result.pdfStr);
				} else{
					errMessage(result.message);
				}

			}
		});
	}

	//删除承诺函
	function deletePayment(id) {
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
					$("#"+id).hide();
// 					$(".file7").show();
// 					$("#textfield_02").html("上传担保函").show();
					$("#createTemplate").show();
				} else {
					errMessage(result.message);
				}

			}
		});
	}

	//提交
	function toSubmit() {
		
		// 借款人网络借贷风险、禁止性行为及有关事项提示书.
		var isChecked = $("#prompt_book_id").attr("checked") == "checked";
		if (!isChecked) {
			errMessage("请授权同意，借款人网络借贷风险、禁止性行为及有关事项提示书！");
			return false;
		}
		$(".mask_wrap,.mask_repd").show();
		window.location.href = "${ctx}/apply/orderApply/applyMoney7?id=${creditUserApply.id}";
	}


	function closeMessage() {
		$("#messageBox").hide();
	}
	function errMessage(str) {
		$("#messageBox").show().html(str);
	}
</script>
<style type="text/css">
.mask_protocol, .mask_protocol_signature {
	max-height: 600px;
	overflow: auto;
	width: 728px;
	z-index: 1;
}
.setting_phone_group {
	overflow: hidden;
}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/user/list">借款申请</a></li>
	</ul>

<%-- 	<form:form id="searchForm" modelAttribute="creditUserApply"
		action="${ctx}/apply/creditUserApply/applyMoney4" method="post"
		class="breadcrumb form-search"> --%>

		<div class="loan_apply loan_apply_wrap_01">
			<div class="loan_apply_wrap">
				<div class="la_tip">温馨提示:以下各项为必填项，在协议签订完成后方可提交申请!</div>
				<div class="la_tab">
					<div class="clear">
						<ul>
							<li class="" id="tab-1"><i>1</i><span>融资类型</span></li>
							<li class="" id="tab-2"><i>2</i><span>选择采购方</span></li>
							<li class="step" id="tab-3"><i>3</i><span>基础交易</span></li>
							<li class="step" id="tab-4"><i>4</i><span>上传资料</span></li>
							<li class="step" id="tab-5"><i>5</i><span>融资申请</span></li>
							<li class="cur step" id="tab-6"><i>6</i><span>担保函</span></li>
							<li class="" id="tab-7"><i>7</i><span>签订协议</span></li>
						</ul>
					</div>
				</div>
				<div class="la_con">
					<div class="la_step_four ">

						<div class="info_basic_wrap">
							<dl class="font_size18">
								<dt>
									<span class="pull-left">仅支持bmp、jpg、jpeg、png格式图片，单张图片大小不超过10M且越清晰越好，每张图片只能包含一项内容，每个类别不超过30张。加*为必填项</span>
									</spab>
								</dt>
								
								
								<dd class="even">
									<b class="pull-left">担保函*</b><span class="pull-left">1.请下载已自动生成的担保函，打印并加盖公司公章。2.拍照或扫描上传盖章承诺函彩色版，确保公章为红色</span>
									<span class="pull-right">
									<a href="" download="" target="_blank" id="downTemplate" style="color: #fff;display: none"  >下载担保函模板</a>
									<a href="javascript:;"  id="createTemplate" onclick="createTemplate();" style="color: #fff">生成担保函模板</a>
									</span>
								</dd> 
								<div class="imgfile_wrap">
									<div class="" id="flie_wrap_7">
										<div class="" id="div_imgfile_7" style="visibility: hidden"></div>
										<div class="file_box">
										    
										    <div class="file_button"  id='textfield_02'>上传担保函</div>
										    <input type="file" name="fileField" class="file_input file7" id="7" />
										</div>
										<%-- <c:forEach items="${creditAnnexFileList}" var="creditAnnexFile" >
											<c:if test="${creditAnnexFile.type == '7'}">
												<div class="" id="${creditAnnexFile.id}">
													<div>'${creditAnnexFile.id}'</div>
													<a href="${creditAnnexFile.url}" download="${creditAnnexFile.url}" class="" style="padding-right: 20px">下载</a>
													<span onclick="deletePayment('${creditAnnexFile.id}')">删除</span>
													<input type="text" style="display: none" value="${creditAnnexFile.id}" id="paymentId2" />
												</div>
											</c:if>
										</c:forEach> --%>
										
									</div>

									<div class="info_error_msg">图片大小不符</div>
								</div>

							</dl>

						</div>
					<!-- 借款人网络借贷风险、禁止性行为及有关事项提示书. -->
					<div class="setting_phone_group">
						<div class="agreement fl agreement_01">
							<span class=""><input id="prompt_book_id" type="checkbox"><i></i></span>
							<em class="fl">借款人网络借贷风险、禁止性行为及有关事项提示书</em>
						</div>
					</div>
					<!-- 声明文件，阅读声明文件并同意授权，生成声明文件. -->
					<div class="mask_repd mask_protocol_signature">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
							<h4 class="modal-title" id="myModalLabel">借款人网络借贷风险、禁止性行为及有关事项提示书</h4>
						</div>
						<div class="mask_model_repd">
							<div class="protocol_group">
								<h3>借款人网络借贷风险、禁止性行为及有关事项提示书</h3>
								<p><b id="company_name"></b>：</p>
								<p>为了确保贵方充分知悉网络借贷风险及有关事项，防范借贷风险，更好的为贵方服务，现向贵方作以下提示，请务必仔细阅读：</p>
								<p>一、就借款编号为_<b id="credit_user_apply_id"></b>_的借款项目，贵方须按以下标准和方式支付利息及融资服务费：</p>
								<p>利息支付标准为年利率_<b id="credit_user_rate_id"></b>_，支付方式为：按月付息，到期还本。</p>
								<p>平台服务费支付标准为_<b id="credit_user_services_rate_id"></b>_，支付方式为：一次性收取。</p>
								<p>二、如果贵方出现逾期，按照《借款合同》及有关合同约定，贵方应承担以下违约后果：</p>
								<p>1.借款人未按合同约定期限及金额归还本金和利息，则自逾期之日起计收逾期违约金；每逾期一日借款人应按未偿还本金的万分之三向出借人支付逾期违约金直至清偿之日止。</p>
								<p>2.借款人未按约定向平台方支付平台服务费，则自逾期之日起计收逾期违约金；每逾期一日借款人应按逾期金额的万分之三向平台方支付逾期违约金直至清偿之日止。</p>
								<p>3.如借款人逾期，本平台有权根据法律、法规、部门规章，及监管机关和行业自律组织（包括但不限于中国银行保险监督管理委员会、中国人民银行、地方金融监管部门、中国互联网金融协会、北京市互联网金融行业协会等）的相关规定或要求向监管机关和行业自律组织及其指定的监管信息系统报送、提供借款人的逾期信息，从而影响借款人的信用记录。</p>
								<p>4.出借人、本平台或不良债权受让方有权向贵方及担保人催收、提起诉讼或仲裁，相关诉讼仲裁费用及实现债权的费用由贵方承担。</p>
								<p>5.法律法规及合同规定的其他相关不利后果。</p>
								<p>三、以上提示内容与《借款合同》及相关合同约定不一致的，以相关合同约定为准。</p>
								<h3>网络借贷禁止性行为提示</h3>
								<p>请仔细阅读并充分理解《网络借贷信息中介机构业务活动管理暂行办法》及有关监管法律、政策规定的如下网络借贷禁止性行为：</p>
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
								<div class="clear"><span class="fr"></span></div>
							</div>
							<div class="read_btn">
								<span class="read_agreen">同意</span>
								<span class="read_close">取消</span>
							</div>
						</div>
					</div>
					<button class="btn btn_four" onclick="toSubmit();">下一步</button> 
						<div id="messageBox" class="alert alert-success " style="display: none;">缺少必要参数</div>
					</div>
				</div>
			</div>
				<!--确认签订 弹框 -->
		<div class="mask_wrap" ></div>
		<div class="mask_repd" >
			<h2>正在签订 请耐心等待...</h2>
		</div>
		</div>


<%-- 	</form:form> --%>


</body>

</html>