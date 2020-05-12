<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
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

			// 拒绝按钮事件
			$("#refuseSubmit").click(function(){
				$("#inputForm").attr("action","${ctx}/approval/proinfo/refuse");
				$("#inputForm").submit();
			});

			
			var amount = "${projectApproval.wloanTermProject.amount}";
			
			var feeRate = "${projectApproval.wloanTermProject.feeRate}";
			
			var marginPercentage = "${projectApproval.wloanTermProject.marginPercentage}";

			var kouchuAmount = parseFloat(feeRate) + parseFloat(marginPercentage);

			var repayAmount = parseFloat(amount) - parseFloat(kouchuAmount);

			$("#kouchuAmount").val( kouchuAmount.toFixed(2) );

			$("#realRepayAmount").val( repayAmount.toFixed(2) );
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/approval/proinfo/">放款申请信息列表</a></li>
		<li class="active"><a href="${ctx}/approval/proinfo/form?id=">放款信息财务审批</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="projectApproval" action="${ctx}/approval/proinfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="wloanTermProject.id"/>
		<sys:message content="${message}"/>		
		
		<input type="hidden" value="${projectApproval.wloanTermProject.amount }" id="amount"/>
		<input type="hidden" value="${projectApproval.wloanTermProject.span }" id="span"/>
		
		<!-- 风控专员信息begin -->
		<div class="control-group">
			<div class="span10">
				<label class="control-label">放款条件：</label>
				<div class="controls">
					<form:textarea path="rcerLoanTerms" htmlEscape="false" class="input-xxlarge" rows="4" value="${projectApproval.rcerLoanTerms }" readonly="true"/>
				</div>
			</div>
		</div>	
			
		<div class="control-group">
			<div class="span10">
				<label class="control-label">落实情况：</label>
				<div class="controls">
					<form:textarea path="rcerImplement" htmlEscape="false"  class="input-xxlarge" rows="4" value="${projectApproval.rcerImplement }" readonly="true"/>
				</div>
			</div>
		</div>
			
		<div class="control-group">
			<div class="span10">
				<label class="control-label">风控专员：</label>
				<div class="controls">
					<form:input path="rcerUser" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" type="hidden"/>
					<input type="text" value="${rcerUserName }" readonly="readonly">
					<fmt:formatDate value="${projectApproval.createDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</div>
			</div>
		</div>
		<!-- 风控专员信息end -->
	
		<!-- 风控文员信息begin -->
		<div class="control-group">
			<div class="span6">
				<label class="control-label">服务费金额：</label>
				<div class="controls">
					<form:input path="wloanTermProject.feeRate" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">保证金金额：</label>
				<div class="controls">
					<form:input path="wloanTermProject.marginPercentage" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" readonly="true"/>
				</div>
			</div>
		</div>	
			
		<div class="control-group">
			<div class="span6">
				<label class="control-label">应扣除金额金额：</label>
				<div class="controls">
					<input type="text" value="" readonly="readonly" id="kouchuAmount">
				</div>
			</div>
			<div class="span6">
				<label class="control-label">实际放款金额：</label>
				<div class="controls">
					<input type="text" value="" readonly="readonly" id="realRepayAmount">
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">风控文员：</label>
				<div class="controls">
					<form:input path="rclerkUser" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" type="hidden"/>
					<input type="text" value="${rclerkUserName }" readonly="readonly">
					<fmt:formatDate value="${projectApproval.rclerkUpdateDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</div>
			</div>
		</div>
		<!-- 风控文员信息end -->
		
		
		<!-- 财务信息begin -->
		<div class="control-group">
			<div class="span10">
				<label class="control-label">财务人员意见：</label>
				<div class="controls">
					<form:textarea path="financeOption" htmlEscape="false" class="input-xxlarge" rows="4"/>
				</div>
			</div>
		</div>
		<!-- 财务信息end -->
		<div class="control-group">
			<div class="form-actions">
				<c:if test="${ projectApproval.state == '2' &&  usertype == '7' }">
    				<shiro:hasPermission name="projectApproval:projectApproval:edit">
						<input id="btnSubmit" class="btn btn-primary" type="submit" value="通  过"/>&nbsp;
						<input id="refuseSubmit" class="btn btn-primary" type="button" value="拒  绝"/>&nbsp;
					</shiro:hasPermission>
   				</c:if>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</div>	
	</form:form>
</body>
</html>