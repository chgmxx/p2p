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
		var step = "${step}";
		$(document).ready(function() {
			//判断正常申请还是显示数据
			if(step==""){//正常申请
				$("#confirm").show();
			}else{//显示数据
				$("#confirm").hide();
				$("#bizType").attr("readonly",true)
				
				var creditSupplyId = '${creditUserApply.creditSupplyId}';
				var creditApplyName = '${creditUserApply.creditApplyName}'
				var bizTypeObj = document.getElementById("bizType");
				for (var i = 0; i < bizTypeObj.length; i++) {
					if (bizTypeObj.options[i].value == creditSupplyId) {
						$(".select2-chosen:eq(0)").text(bizTypeObj.options[i].text);
						bizTypeObj.options[i].selected=true;
						break;
					}
				}
			}
		});
		function toSave(){
			errHide();
			if($("#bizType").val()==00){
				errMsg("请选择供应商！");
				return false;
			}else{
				return true;
			}
			
		}
		function errMsg(str){
			$("#errMsg").html(str).show();
		}
		function errHide(){
			$("#errMsg").hide();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/user/list">借款申请</a></li>
	</ul>
		<form:form id="searchForm" modelAttribute="creditUserApply" onsubmit="return toSave()" action="${ctx}/apply/creditUserApply/applyMoney3?id=${creditUserApply.id}&creditUserType=11" method="post" class="breadcrumb form-search">
		
			<div class="loan_apply">
				<div class="la_tip">温馨提示:以下各项为必填项，在协议签订完成后方可提交申请!</div>
				<div class="la_tab">
					<div class="clear">
						<ul>
							<li class="" id="tab-1"><i>1</i><span>融资类型</span></li>
							<li class="cur" id="tab-2"><i>2</i><span>选择供应商</span></li>
							<li class="" id="tab-3"><i>3</i><span>基础交易</span></li>
							<li class="" id="tab-4"><i>4</i><span>上传资料</span></li>
							<li class="" id="tab-5"><i>5</i><span>融资金额</span></li>
							<li class="" id="tab-6"><i>6</i><span>签订协议</span></li>
						</ul>
					</div>
				</div>
				<div class="la_con la_new_con">
					<div class="la_step la_step_one cur form-horizontal clear">
					<div class="control-group">
						<label class="control-label">我方:</label>
						<div class="controls">
							<input class="text" type="text" value="${creditUser.enterpriseFullName}" disabled="dsisabled" >
						</div>
					</div>
						<ul class="ul-form">
							<li>
								<label>对方：</label>
								<form:select path="creditSupplyId" id="bizType" class="form-horizontal clear">
									<form:option value="00" label="请选择" />
									<c:forEach items="${creditSupplierToMiddlemenList}" var="creditSupplierToMiddlemen">
										<form:option value="${creditSupplierToMiddlemen.supplierUser.id}" label="${creditSupplierToMiddlemen.supplierUser.enterpriseFullName}" />
									</c:forEach>
								</form:select>
								
							</li>
						</ul>
						<button class="btn clear group_btn" id="confirm" type="submit" onclick="toSave();">下一步</button>
					    <div class="errMsg" id="errMsg"></div>
					</div>
				</div>
			</div>
		
		
	</form:form>
</body>
</html>