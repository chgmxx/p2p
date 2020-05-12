<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/reset.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/apply.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />

	<script type="text/javascript">
		var step = '${step}';
		$(document).ready(function() {
			//判断正常申请还是显示数据
			if(step==""){//正常申请
				$("#confirm").show();
			}else{//显示数据
				$("#confirm").hide();
				       var financingType = '${creditUserApply.financingType}';
				var financingTypeObj = document.getElementById("financingType");
				for (var i = 0; i < financingTypeObj.length; i++) {
					if (financingTypeObj.options[i].value == financingType) {
						$(".select2-chosen:eq(0)").text(financingTypeObj.options[i].text);
						financingTypeObj.options[i].selected=true;
						break;
					}
				}
				$("#financingType").attr("readonly",true);
			}
			//申请流程点击
			$(".step").click(function(){
				var step1 = $(this).children("i").html();
				var financingStep = "${creditUserApply.financingStep}";
				if(step1>financingStep){
					alert("跳转页面尚未完成！");
					return false;
				}else{
					window.location.href = "${ctx}/apply/orderApply/applyMoney"+step1+"?id= ${creditUserApply.id}&step="+step1;
				}
			});

		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/user/list">借款申请</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="creditUserApply" action="${ctx}/apply/orderApply/applyMoney2?replaceUserId=${creditUser.id}" method="post" class="form-horizontal">
		<%-- <form:hidden path="id"/> --%>
		<div class="loan_apply">
			<div class="loan_apply_wrap">
				<div class="la_tip">温馨提示:以下各项为必填项，在协议签订完成后方可提交申请!</div>
				<div class="la_tab">
					<div class="clear">
						<ul>
							<li class="cur step" id="tab-1"><i>1</i><span>融资类型</span></li>
							<li class="step" id="tab-2"><i>2</i><span>选择采购方</span></li>
							<li class="step" id="tab-3"><i>3</i><span>基础交易</span></li>
							<li class="step" id="tab-4"><i>4</i><span>上传资料</span></li>
							<li class="step" id="tab-5"><i>5</i><span>融资申请</span></li>
							<li class="step" id="tab-6"><i>6</i><span>担保函</span></li>
							<li class="" id="tab-7"><i>7</i><span>签订协议</span></li>
						</ul>
					</div>
				</div>
				<div class="la_con">
					<div class="la_step la_step_one cur">
					   <div class="clear mt_20">
						<form:select path="financingType" id="financingType" class="form-horizontal clear">
							<form:option value="00" label="请选择融资类型" />
							<form:option value="1" label="应收账款转让" />
							<form:option value="2" label="订单融资" />
						</form:select>
						</div>
						<button class="btn clear" id="confirm" type="submit">下一步</button>
					</div>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>