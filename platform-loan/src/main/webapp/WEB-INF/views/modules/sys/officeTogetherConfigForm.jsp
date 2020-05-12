<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>加油站站点认证管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
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
		
		$(function(){
			var attestation='${officeTogetherConfig.attestation}';
			var assure='${officeTogetherConfig.assure}';
			var discount='${officeTogetherConfig.discount}';
			var first='${officeTogetherConfig.first}';
			var coupons='${officeTogetherConfig.coupons}';
			if(attestation==1){
				$("#attestation").attr("checked","true");
			}
			if(assure==1){
				$("#assure").attr("checked","checked");
			}
			if(discount==1){
				$("#discount").attr("checked","checked");
			}
			if(first==1){
				$("#first").attr("checked","checked");
			}
			if(coupons==1){
				$("#coupons").attr("checked","checked");
			}
		})
		
		function tochange(obj){
			if($(obj).val()=="1"){
				if($("#attestation").val()=="1"){
					$("#attestation").val("0");
				}else{
					$("#attestation").val("1");
				}
			}else if($(obj).val()=="2"){
				if($("#assure").val()=="1"){
					$("#assure").val("0");
				}else{
					$("#assure").val("1");
				}
			}else if($(obj).val()=="3"){
				if($("#discount").val()=="1"){
					$("#discount").val("0");
				}else{
					$("#discount").val("1");
				}
			}else if($(obj).val()=="4"){
				if($("#first").val()=="1"){
					$("#first").val("0");
				}else{
					$("#first").val("1");
				}
			}else if($(obj).val()=="5"){
				if($("#coupons").val()=="1"){
					$("#coupons").val("0");
				}else{
					$("#coupons").val("1");
				}
			}
		}

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/jiayou/sitevalidate/officeTogetherConfig/form?id=${officeTogetherConfig.id}">加油站站点认证
		</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="officeTogetherConfig" action="${ctx}/jiayou/sitevalidate/officeTogetherConfig/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" id="officeId" name="officeId" value="${officeId}">
		<input type="hidden" id="attestation" name="attestation"  value="${empty officeTogetherConfig.attestation?0:officeTogetherConfig.attestation}" />
		<input type="hidden" id="assure" name="assure"  value="${empty officeTogetherConfig.assure?0:officeTogetherConfig.assure}" />
		<input type="hidden" id="discount" name="discount"  value="${empty officeTogetherConfig.discount?0:officeTogetherConfig.discount}" />
		<input type="hidden" id="first" name="first"  value="${empty officeTogetherConfig.first?0:officeTogetherConfig.first}" />
		<input type="hidden" id="coupons" name="coupons"  value="${empty officeTogetherConfig.coupons?0:officeTogetherConfig.coupons}" />
		<sys:message content="${message}"/>	
		<div class="control-group">
		<label class="control-label">站点认证:</label>
			<div class="controls">
			<span>
			 <input type="checkbox" id="torgetherFlag" name="torgetherFlag" <c:if test="${not empty officeTogetherConfig.attestation && officeTogetherConfig.attestation=='1'}">checked="checked"</c:if> value="1" onchange="tochange(this);">
			 <label>认证</label>
			 </span>
			 
			 <span>
			 <input type="checkbox" id="torgetherFlag" name=torgetherFlag <c:if test="${not empty officeTogetherConfig.assure && officeTogetherConfig.assure=='1'}">checked="checked"</c:if> value="2" onchange="tochange(this);">
			 <label>担保</label>
			 </span>
			 
			  <span>
			 <input type="checkbox" id="torgetherFlag" name="torgetherFlag" <c:if test="${not empty officeTogetherConfig.discount && officeTogetherConfig.discount=='1'} ">checked="checked"</c:if> value="3" onchange="tochange(this);">
			 <label>折扣</label>
			 </span>
			 
			  <span>
			 <input type="checkbox" id="torgetherFlag" name="torgetherFlag" <c:if test="${not empty officeTogetherConfig.first && officeTogetherConfig.first=='1'}">checked="checked"</c:if> value="4" onchange="tochange(this);">
			 <label>首</label>
			 </span>
			 
			   <span>
			 <input type="checkbox" id="torgetherFlag" name="torgetherFlag" <c:if test="${not empty officeTogetherConfig.coupons && officeTogetherConfig.coupons=='1'}">checked="checked"</c:if> value="5" onchange="tochange(this);">
			 <label>优惠券</label>
			 </span>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>