<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>


<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/reset.css" />	
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/apply.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
	<script src="${ctxStatic}/js/CheckUtils.js" type="text/javascript"></script>
<script type="text/javascript">
	var step = "${step}";
	var fileConfirm = '${creditUserApply.fileConfirm}';
	var financingStep = "${creditUserApply.financingStep}";
		$(document).ready(function() {
			
			//判断正常申请还是显示数据
			if(step==""){//正常申请
				
			}else{//显示数据
				//判断页面是否可编辑
				if(fileConfirm=='1'){//不可编辑
					$("#packType").attr("readonly",true);
					$("#packName").attr("readonly",true);
					$("#packNo").attr("readonly",true);
					$("#packMoney").attr("readonly",true);
					$("#packType").attr("readonly",true);
					$("#packUsedDate").attr("readonly",true);
					$("#packOnDate").attr("readonly",true);
					$("#confirm1").hide();
					$("#confirm2").hide();
				}else if(financingStep>3){
					$("#confirm2").hide();
				}else{
					//判断用户类型
					$("#confirm1").show();
					$("#confirm2").show();
				}
				var packType = "${creditPack.type}";
				var packTypeObj = document.getElementById("packType");
				for (var i = 0; i < packTypeObj.length; i++) {
					if (packTypeObj.options[i].value == packType) {
						$(".select2-chosen:eq(0)").text(packTypeObj.options[i].text);
						packTypeObj.options[i].selected=true;
						break;
					}
				}
				
			}
			//申请流程点击
			$(".step").click(function(){
				var step1 = $(this).children("i").html();
				
				if(step1-1>financingStep){
					alert("跳转页面尚未完成！");
					return false;
				}else if(step1-1==financingStep){
				}else{
					window.location.href = "${ctx}/apply/orderApply/applyMoney"+step1+"?id= ${creditUserApply.id}&step="+step1;
				}
			});
			//修改信息提交
			$("#confirm1").click(function(){
				$("#searchForm").attr("action","${ctx}/apply/orderApply/applyMoney4?id= ${creditUserApply.id}&saveInfo=yes");
				$("#searchForm").submit();
			});
			$("#confirm2").click(function(){
				$("#searchForm").attr("action","${ctx}/apply/orderApply/applyMoney4?id= ${creditUserApply.id}");
				$("#searchForm").submit();
			});
		});

		function toSave(){
			errHide();
			var packName=$("#packName").val();
			var packNo=$("#packNo").val();
			var packMoney=$("#packMoney").val();
			var packType=$("#packType").val();
			var packUsedDate=$("#packUsedDate").val();
			var packOnDate=$("#packOnDate").val();
			if(packName==null || packName.trim()==""){
				errMsg("请填写合同名称！");
				return false;
			}
			
			if(packNo==null || packNo.trim()==""){
				errMsg("请填写合同编号！");
				return false;
			}
			if(packMoney==null || packMoney.trim()==""){
				errMsg("请填写合同金额！");
				return false;
			}
			if(!checkAmount(packMoney)){
				errMsg("请正确填写合同金额！");
				return false;
			}
			if(packType=="00"){
				errMsg("请选择合同类型！");
				return false;
			}
			if(packUsedDate==null || packUsedDate.trim()==""){
				errMsg("请填写合同有效期！");
				return false;
			}
			if(packOnDate==null || packOnDate.trim()==""){
				errMsg("请填写合同签订日期！");
				return false;
			}
			if(compareToDate(packUsedDate)){
				errMsg("请填写正确合同有效期！");
				return false;
			}
			return true;

		}

		//日期大小比较
		function compareToDate(packUsedDate){
			
			var d1 = new Date(packUsedDate.replace(/\-/g, "\/"));    
			var d2 = new Date();
			if(d1!=""&&d2!=""&&d1 >=d2){    
// 			  alert("开始时间不能大于结束时间！");    
			  return false;    
			} else{
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
		<form:form id="searchForm" modelAttribute="creditUserApply" onsubmit="return toSave()" action="${ctx}/apply/orderApply/applyMoney4?id=${creditUserApply.id}" method="post" class="breadcrumb form-search">
			<div class="loan_apply" id="unlook">
				<div class="la_tab">
					<div class="clear">
						<ul>
							<li class="" id="tab-1"><i>1</i><span>融资类型</span></li>
							<li class="" id="tab-2"><i>2</i><span>选择采购方</span></li>
							<li class="cur step" id="tab-3"><i>3</i><span>基础交易</span></li>
							<li class="step" id="tab-4"><i>4</i><span>上传资料</span></li>
							<li class="step" id="tab-5"><i>5</i><span>融资申请</span></li>
							<li class="step" id="tab-6"><i>6</i><span>担保函</span></li>
							<li class="" id="tab-7"><i>7</i><span>签订协议</span></li>
						</ul>
					</div>
				</div>
				<div class="la_new_con la_con">
					<div class="la_step la_step_one cur form-horizontal clear">
				<div class="control-group">
					<label class="control-label">*我方</label>
					<div class="controls">
						<form:input path="" htmlEscape="false" maxlength="32" value="${creditUser.enterpriseFullName}" class="input-xlarge" readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
				
					<label class="control-label">*对方</label>
					<div class="controls">
						<form:input path="" htmlEscape="false" maxlength="32" class="input-xlarge required" value="${supplyUser.enterpriseFullName}" readonly="true"/>
					</div>
				</div>
				<div class="control-group ">
					<label class="control-label">*合同名称</label>
					<div class="controls">
						<form:input path="creditPack.name" id="packName" htmlEscape="false" value="${creditPack.name }"  maxlength="32" class="input-xlarge required"  readonly="false"/>
					</div>
				</div>
				
				<div class="control-group ">
					<label class="control-label">*合同编号</label>
					<div class="controls">
						<form:input path="creditPack.no" htmlEscape="false" value="${creditPack.no }" id="packNo" maxlength="32" class="input-xlarge required"  readonly="false"/>
					</div>
				</div>
				
				<div class="control-group ">
					<label class="control-label">*合同金额</label>
					<div class="controls">
						<form:input path="creditPack.money" htmlEscape="false" value="${creditPack.money }" id="packMoney" maxlength="32" class="input-xlarge required"  readonly="false"/>
					</div>
				</div>
				
				<div class="control-group ">
					<label class="control-label">*合同类型</label>
					<div class="controls">
						<form:select path="creditPack.type" id="packType" style="width:177px">
							<form:option value="00" label="请选择" />
							<form:option value="1" label="贸易合同" />
							<form:option value="2" label="联营合同" />
							<form:option value="3" label="购销合同" />
						</form:select>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label">合同有效期：</label>
				    <div class="controls la_data_input">
						<input name="creditPack.userdDate" value="<fmt:formatDate value='${creditPack.userdDate}' pattern='yyyy-MM-dd HH:mm:ss'/>"  type="text" id="packUsedDate" readonly="readonly" class="input-medium Wdate" placeholder="合同有效期"
						
						onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				    </div>
				</div> 
				<div class="control-group ">
					<label class="control-label">签订日期：</label>
					  <div class="controls la_data_input">
						<input name="creditPack.signDate" type="text" id="packOnDate" readonly="readonly" class="input-medium Wdate" placeholder="签订日期"
							
							onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" value="<fmt:formatDate value='${creditPack.signDate}' pattern='yyyy-MM-dd HH:mm:ss'/>" pattern="yyyy-MM-dd"/>
				       </div>
					
				</div>
				<button class="btn clear group_btn" id="confirm1" type="submit">保存</button>
				<button class="btn clear group_btn" id="confirm2" type="submit">下一步</button>
		        <div class="errMsg" id="errMsg"></div>
		</div>
		</div>
		</div>
	</form:form>
<!-- 	<button class="btn clear group_btn" id="confirm1" style="display:none">保存</button> -->
</body>
</html>