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
		<li class="active"><a href="${ctx}/approval/proinfo/form">
			<c:if test="${ projectApproval.state == '4' }">放  款	</c:if>
			<c:if test="${ projectApproval.state == '5' }">查看放款信息</c:if>
		</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="projectApproval" action="${ctx}/wloanproject/wloanTermProject/toBeCheck" method="post" class="form-horizontal">
		<form:hidden path="id" value="${projectApproval.wloanTermProject.id }"/>
		<sys:message content="${message}"/>		
		
		
		<!-- 风控专员信息begin -->
		<div class="control-group">
			<div class="span10">
				<label class="control-label">放款条件：</label>
				<div class="controls">
					<form:textarea path="rcerLoanTerms" htmlEscape="false" class="input-xxlarge" rows="4" readonly="true"/>
				</div>
			</div>
		</div>	
			
		<div class="control-group">
			<div class="span10">
				<label class="control-label">落实情况：</label>
				<div class="controls">
					<form:textarea path="rcerImplement" htmlEscape="false"  class="input-xxlarge" rows="4" readonly="true"/>
				</div>
			</div>
		</div>
			
		<div class="control-group">
			<div class="span10">
				<label class="control-label">风控专员签名：</label>
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
				<label class="control-label">风控文员签名：</label>
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
					<form:textarea path="financeOption" htmlEscape="false" class="input-xxlarge" rows="4" readonly="true"/>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span10">
				<label class="control-label">财务人员签名：</label>
				<div class="controls">
					<form:input path="financeUser" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" type="hidden"/>
					<input type="text" value="${financeUserName }" readonly="readonly">
					<fmt:formatDate value="${projectApproval.financeUpdateDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</div>
			</div>
		</div>
		<!-- 财务信息end -->
				<!-- 风控经理信息begin -->
		<div class="control-group">
			<div class="span10">
				<label class="control-label">风控经理意见：</label>
				<div class="controls">
					<form:textarea path="rcerManagerOption" htmlEscape="false" class="input-xxlarge" rows="4" value="${projectApproval.financeOption }" readonly="true"/>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span10">
				<label class="control-label">风控经理：</label>
				<div class="controls">
					<form:input path="rcerManagerUser" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" type="hidden"/>
					<input type="text" value="${rcerManagerUserName }" readonly="readonly">
					<fmt:formatDate value="${projectApproval.rcerManagerUpdateDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</div>
			</div>
		</div>
		<!-- 风控经理信息end -->
		<!-- 总经理信息begin -->
		<div class="control-group">
			<div class="span10">
				<label class="control-label">总经理意见：</label>
				<div class="controls">
					<form:textarea path="adminOption" htmlEscape="false" class="input-xxlarge" rows="4" readonly="true"/>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span10">
				<label class="control-label">总经理签名：</label>
				<div class="controls">
					<form:input path="adminUser" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" type="hidden"/>
					<input type="text" value="${adminUserName }" readonly="readonly">
					<fmt:formatDate value="${projectApproval.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</div>
			</div>
		</div>
		<!-- 总经理信息end -->
		
		<div class="control-group">
			<div class="form-actions">
				<shiro:hasPermission name="projectApproval:projectApproval:edit">
					<c:if test="${ projectApproval.state == '4' && usertype == '7' }">
    					<input id="btnSubmit" class="btn btn-primary" type="submit" value="放  款"/>&nbsp;
    				</c:if>
				</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</div>	
	</form:form>
</body>
</html>