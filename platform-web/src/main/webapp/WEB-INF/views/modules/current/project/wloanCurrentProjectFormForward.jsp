<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>活期融资项目管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					var feeRate = "${wloanCurrentProject.feeRate / 100 }";
					var maiginRate = "${wloanCurrentProject.marginPercentage / 100 }";
					
					var ableExper = "${wloanCurrentProject.amount - (wloanCurrentProject.currentRealAmount == null ? 0 : wloanCurrentProject.currentRealAmount) }";
					var waitExper = "${waitAmountDouble }";
					
					if(ableExper <= 0){
						alert("可转入份额为0,不能转入");
						return;
					}
					
					if(waitExper <= 0){
						alert("待转入份额为0,不能转入");
						return;
					}
					
					if( parseFloat(ableExper) >= parseFloat(waitExper) ){
						if(!confirm("确认转入？本次待转入份额[" + parseFloat(waitExper) + "] 手续费将扣除（" + parseFloat(waitExper) * feeRate + "元），保证金将扣除（" +  parseFloat(waitExper) * maiginRate + "元）。")){
							return;
						}
					} else {
						if(!confirm("确认转入？本次将转入份额（" + parseFloat(ableExper) + "元） 手续费将扣除（" + parseFloat(ableExper) * feeRate + "元），保证金将扣除（" +  parseFloat(ableExper) * maiginRate + "元）。")){
							return;
						}
					}
					
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
		<li><a href="${ctx}/current/project/wloanCurrentProject/">活期融资项目列表</a></li>
		<li class="active"><a href="${ctx}/current/project/wloanCurrentProject/toForwardThis?id=${wloanCurrentProject.id}">活期融资项目
		<c:if test="${wloanCurrentProject.state == '3'}">转入</c:if>
		</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="wloanCurrentProject" action="${ctx}/current/project/wloanCurrentProject/toBeForwardThis" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">项目编号：</label>
				<div class="controls">
					<form:input path="sn" htmlEscape="false" maxlength="32" class="input-xlarge" style="width:250px" readonly="true"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">项目名称：</label>
				<div class="controls">
					<form:input path="name" htmlEscape="false" maxlength="55" class="input-xlarge" style="width:250px" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">融资主体：</label>
				<div class="controls">
					<a href="${ctx}/current/project/wloanCurrentProject/subjectViewForm?id=${wloanCurrentProject.id}">${wloanCurrentProject.wloanSubject.companyName }</a>
					<form:input path="wloanSubject.companyName" type="hidden" htmlEscape="false" maxlength="55" class="input-xlarge" style="width:250px"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">担保机构：</label>
				<div class="controls">
					<a href="${ctx}/current/project/wloanCurrentProject/guarViewForm?id=${wloanCurrentProject.id}">${wloanCurrentProject.wgCompany.name }</a>
					<form:input path="wgCompany.name" type="hidden" htmlEscape="false" maxlength="55" class="input-xlarge" style="width:250px"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">融资档案：</label>
				<div class="controls">
					<a href="${ctx}/current/project/wloanCurrentProject/docViewForm?id=${wloanCurrentProject.id}">${wloanCurrentProject.wloanTermDoc.name }</a>
					<form:input path="wloanTermDoc.name" type="hidden" htmlEscape="false" maxlength="55" class="input-xlarge" style="width:250px"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">融资金额：</label>
				<div class="controls">
					<form:input path="amount" htmlEscape="false" class="input-large number" style="width:216px" readonly="true"/>
					<span class="help-inline">RMB</span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">融资进度：</label>
				<div class="controls">
					<form:input path="currentRealAmount" htmlEscape="false" class="input-large number" style="width:216px" readonly="true"/>
					<span class="help-inline">RMB</span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">期限：</label>
				<div class="controls">
					<c:if test="${not empty wloanCurrentProject.span }">
						<c:forEach items="${fns:getDictList('regular_wloan_span')}" var="proSpan">
							<c:if test="${wloanCurrentProject.span == proSpan.value }">
								<form:input path="span" htmlEscape="false" maxlength="32" class="input-large" readonly="true" value="${proSpan.label }" style="width:215px"/>
							</c:if>
						</c:forEach>
					</c:if>
					<span class="help-inline">&nbsp;&nbsp;天</span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">手续费：</label>
				<div class="controls">
					<form:input path="feeRate" htmlEscape="false" class="input-xlarge number" style="width:230px" readonly="true"/>
					<span class="help-inline">%</span>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">保证金：</label>
				<div class="controls">
					<form:input path="marginPercentage" htmlEscape="false" class="input-large number" style="width:216px" readonly="true"/>
					<span class="help-inline">&nbsp;&nbsp;&nbsp;%</span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">状态：</label>
				<div class="controls">
					<c:if test="${not empty wloanCurrentProject.state }">
						<c:forEach items="${fns:getDictList('current_project_state')}" var="currstate">
							<c:if test="${wloanCurrentProject.state == currstate.value }">
								<input type="text" readonly="readonly" value="${currstate.label}" style="width:250px">
								<form:hidden path="state" value="${currstate.value}" id="state"/>
							</c:if>
						</c:forEach>
					</c:if>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">担保函编号：</label>
				<div class="controls">
					<form:input path="guaranteeSn" htmlEscape="false" maxlength="32" class="input-xlarge " style="width:250px" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span6">
				<label class="control-label">上线时间：</label>
				<div class="controls">
					<input name="onlineDate" type="text" readonly="readonly" maxlength="20" class="input-medium" style="width:250px"
						value="<fmt:formatDate value="${wloanCurrentProject.onlineDate}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			<div class="span6">
				<label class="control-label">结束日期：</label>
				<div class="controls">
					<input name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium" style="width:250px"
						value="<fmt:formatDate value="${wloanCurrentProject.endDate}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">备注：</label>
				<div class="controls">
					<form:textarea path="remark" htmlEscape="false" maxlength="255" class="input-xxlarge" rows="4" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">担保方案：</label>
				<div class="controls">
					<form:textarea path="guaranteeScheme" htmlEscape="false" maxlength="255" class="input-xxlarge" rows="4" readonly="true"/>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<div class="span10">
				<label class="control-label">资金用途：</label>
				<div class="controls">
					<form:textarea path="purpose" htmlEscape="false" class="input-xxlarge" rows="4" maxlength="500" readonly="true"/>
				</div>
			</div>
		</div>	
		
		<div class="form-actions">
			<button type="button" data-toggle="modal" data-target="#myModal" class="btn btn-primary"> 转 入</button>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
		
		
		
		<!-- 放款确认信息 -->
		<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				<h3 id="myModalLabel">转入信息</h3>
			</div>
			<div class="modal-body">
				<div class="control-group">
					<label class="control-label">融资金额：</label>
					<div class="controls">
						${wloanCurrentProject.amount }元
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">可转入金额：</label>
					<div class="controls">
						${wloanCurrentProject.amount - (wloanCurrentProject.currentRealAmount == null ? 0 : wloanCurrentProject.currentRealAmount) }元
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">待转入份额：</label>
					<div class="controls">
						${waitAmountDouble }元
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn" data-dismiss="modal" aria-hidden="true">取消</button>
				<button class="btn btn-primary">转入</button>
			</div>
		</div>
	</form:form>
</body>
</html>