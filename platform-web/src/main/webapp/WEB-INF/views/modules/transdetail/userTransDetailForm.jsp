<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户流水记录管理</title>
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/transdetail/userTransDetail/">客户流水记录列表</a></li>
		<li class="active"><a href="${ctx}/transdetail/userTransDetail/form?id=${userTransDetail.id}">客户流水记录<shiro:hasPermission name="transdetail:userTransDetail:edit">${not empty userTransDetail.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="transdetail:userTransDetail:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="userTransDetail" action="${ctx}/transdetail/userTransDetail/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">姓名：</label>
				<div class="controls">
					<form:input path="userInfo.realName" htmlEscape="false" class="input-xlarge required number" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">手机号：</label>
				<div class="controls">
					<form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-xlarge required" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">交易金额：</label>
				<div class="controls">
					<form:input path="amount" htmlEscape="false" class="input-xlarge required" readonly="true"/>
				</div>
			</div>
			<div class="span6 ">
				<label class="control-label">当前可用余额：</label>
				<div class="controls">
					<input name="avaliableAmount" type="text" class="input-xlarge required" readonly="readonly"
						value="<fmt:formatNumber type="number" value="${userTransDetail.avaliableAmount}" minFractionDigits="2" maxFractionDigits="2" />"/>
					
					
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">交易时间：</label>
				<div class="controls">
					<input name="transDate" type="text" readonly="readonly" maxlength="20" class="input-xlarge required"
						value="<fmt:formatDate value="${userTransDetail.transDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
				</div>
			</div>
			<div class="span6 ">
				<label class="control-label">交易类型：</label>
				<div class="controls">
					<c:if test="${not empty userTransDetail.trustType }">
						<c:forEach items="${fns:getDictList('trans_detail_type')}" var="transType">
							<c:if test="${userTransDetail.trustType == transType.value }">
								<form:input path="trustType" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true" value="${transType.label }"/>
							</c:if>
						</c:forEach>
					</c:if>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6 ">
				<label class="control-label">收支类型：</label>
				<div class="controls">
					<c:if test="${not empty userTransDetail.inOutType }">
						<c:forEach items="${fns:getDictList('trans_detail_inout_type')}" var="outType">
							<c:if test="${userTransDetail.inOutType == outType.value }">
								<form:input path="inOutType" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true" value="${outType.label }"/>
							</c:if>
						</c:forEach>
					</c:if>
				</div>
			</div>
			<div class="span6 ">
				<label class="control-label">交易状态：</label>
				<div class="controls">
					<c:if test="${not empty userTransDetail.state }">
						<c:forEach items="${fns:getDictList('trans_detail_inout_state')}" var="transState">
							<c:if test="${userTransDetail.state == transState.value }">
								<form:input path="state" htmlEscape="false" maxlength="32" class="input-xlarge" readonly="true" value="${transState.label }"/>
							</c:if>
						</c:forEach>
					</c:if>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">备注：</label>
				<div class="controls">
					<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="500" class="input-xxlarge " readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="form-actions">
			<div class="span6 ">
				<label class="control-label"></label>
				<div class="controls">
				</div>
			</div>
			<div class="span6">
				<label class="control-label"></label>
				<div class="controls">
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>