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
		$(document).ready(function() {
			$("#no").focus();
			$("#inputForm").validate({
				rules: {
					loginName: {remote: "${ctx}/sys/user/checkLoginName?oldLoginName=" + encodeURIComponent('${user.loginName}')}
				},
				messages: {
					loginName: {remote: "用户登录名已存在"},
					confirmNewPassword: {equalTo: "输入与上面相同的密码"}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
		});
		function save(){
			var financingType = $("#financingType").val();
			if(financingType=="1"){//应收账款转让
				$("#inputForm").attr("action","${ctx}/apply/creditUserApply/applyMoney2?replaceUserId=${creditUser.id}");
			}else if(financingType=="2"){//订单融资
				$("#inputForm").attr("action","${ctx}/apply/orderApply/applyMoney2?replaceUserId=${creditUser.id}");
			}else{
				alert("请选择融资类型！");
				return false;
			}
			return true;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/user/list">借款申请</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="creditUserApply" onsubmit="return save()" action="${ctx}/apply/creditUserApply/applyMoney2" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<div class="loan_apply">
			<div class="loan_apply_wrap">
				<div class="la_tip">温馨提示:以下各项为必填项，在协议签订完成后方可提交申请!</div>
				<div class="la_tab">
					<div class="clear">
						<ul>
							<li class="cur" id="tab-1"><i>1</i><span>融资类型</span></li>
							<li class="" id="tab-2"><i>2</i><span>选择供应商</span></li>
							<li class="" id="tab-3"><i>3</i><span>基础交易</span></li>
							<li class="" id="tab-4"><i>4</i><span>上传资料</span></li>
							<li class="" id="tab-5"><i>5</i><span>融资金额</span></li>
							<li class="" id="tab-6"><i>6</i><span>签订协议</span></li>
						</ul>
					</div>
				</div>
				<div class="la_con">
					<div class="la_step la_step_one cur">
					   <div class="clear mt_20">
						<form class="form-horizontal clear">
							<form:select path="financingType" id="financingType" class="form-horizontal clear">
								<form:option value="00" label="请选择融资类型" />
								<form:option value="1" label="应收账款质押" />
								<form:option value="2" label="订单融资" />
							</form:select>
						</form>
						</div>
						<button class="btn clear" type="submit">下一步</button>
					</div>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>