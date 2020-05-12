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
		var isModify = '${creditUserApply.modify}';
		$(document).ready(function() {
			//判断正常申请还是显示数据
			if(step==""){//正常申请
				$("#confirm1").hide();
				$("#confirm2").show();
			}else{//显示数据
				//判断页面是否可编辑
				if(isModify=='1'){//不可编辑
					$("#confirm1").hide();
					$("#confirm2").hide();
					$("#bizType").attr("readonly",true)
				}else{
					$("#confirm1").show();
					$("#confirm2").hide();
				}
				
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
			//申请流程点击
			$(".step").click(function(){
				var step1 = $(this).children("i").html();
				var financingStep = "${creditUserApply.financingStep}";
				if(step1-1>financingStep){
					alert("跳转页面尚未完成！");
					return false;
				}else if(step1-1==financingStep){
// 						window.location.href = "${ctx}/apply/orderApply/applyMoney2?id= ${creditUserApply.id}";
				}else{
					window.location.href = "${ctx}/apply/orderApply/applyMoney"+step1+"?id= ${creditUserApply.id}&step="+step1;
				}
			});
			//修改信息提交
			$("#confirm1").click(function(){
				$("#searchForm").attr("action","${ctx}/apply/orderApply/applyMoney2ToSave?id= ${creditUserApply.id}&step=${step}");
				$("#searchForm").submit();
			});
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
		<form:form id="searchForm" modelAttribute="creditUserApply" onsubmit="return toSave()" action="${ctx}/apply/orderApply/applyMoney3?id=${creditUserApply.id}" method="post" class="breadcrumb form-search">
		
			<div class="loan_apply">
				<div class="la_tip">温馨提示:以下各项为必填项，在协议签订完成后方可提交申请!</div>
				<div class="la_tab">
					<div class="clear">
						<ul>
							<li class="" id="tab-1"><i>1</i><span>融资类型</span></li>
							<li class="cur" id="tab-2"><i>2</i><span>选择采购方</span></li>
							<li class="step" id="tab-3"><i>3</i><span>基础交易</span></li>
							<li class="step" id="tab-4"><i>4</i><span>上传资料</span></li>
							<li class="step" id="tab-5"><i>5</i><span>融资申请</span></li>
							<li class="step" id="tab-6"><i>6</i><span>担保函</span></li>
							<li class="" id="tab-7"><i>7</i><span>签订协议</span></li>
						</ul>
					</div>
				</div>
				<div class="la_con la_new_con">
					<div class="la_step la_step_one cur form-horizontal clear" >
					<div class="control-group">
						<label class="control-label">我方:</label>
						<div class="controls">
							<input class="text" type="text" value="${creditUser.enterpriseFullName}" disabled="dsisabled" >
						</div>
					</div>
					
						<ul class="ul-form" >
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
						<button class="btn clear group_btn" type="submit" id="confirm2" onclick="toSave();">下一步</button>
					    <div class="errMsg" id="errMsg"></div>
					</div>
					
				</div>
			</div>
	</form:form>
	<button class="btn clear group_btn" id="confirm1" style="display:none">保存</button>
</body>
</html>