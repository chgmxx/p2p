<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>银行托管-流水管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(
			function() {
				//$("#name").focus();
				$("#inputForm")
						.validate(
								{
									submitHandler : function(form) {
										loading('正在提交，请稍等...');
										form.submit();
									},
									errorContainer : "#messageBox",
									errorPlacement : function(error, element) {
										$("#messageBox").text("输入有误，请先更正。");
										if (element.is(":checkbox")
												|| element.is(":radio")
												|| element.parent().is(
														".input-append")) {
											error.appendTo(element.parent()
													.parent());
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
		<li><a href="${ctx}/cgb/cgbUserTransDetail/">流水列表</a></li>
		<li class="active"><a href="${ctx}/cgb/cgbUserTransDetail/form?id=${cgbUserTransDetail.id}">流水<shiro:hasPermission name="cgb:cgbUserTransDetail:edit">${not empty cgbUserTransDetail.id?'修改':'添加'}</shiro:hasPermission>
				<shiro:lacksPermission name="cgb:cgbUserTransDetail:edit">查看</shiro:lacksPermission></a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="cgbUserTransDetail" action="${ctx}/cgb/cgbUserTransDetail/save" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">帐号：</label>
			<div class="controls">
				<form:input path="userInfo.name" htmlEscape="false" maxlength="64" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">姓名：</label>
			<div class="controls">
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="64" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交易日期：</label>
			<div class="controls">
				<input name="transDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required" value="<fmt:formatDate value="${cgbUserTransDetail.transDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交易类型：</label>
			<div class="controls">
				<form:select path="trustType" htmlEscape="false" class="input-xlarge required">
					<form:option value="" label="请选择" />
					<form:option value="0" label="充值" />
					<form:option value="1" label="提现" />
					<form:option value="2" label="活期投资" />
					<form:option value="3" label="投资" />
					<form:option value="4" label="还利息" />
					<form:option value="5" label="还本金" />
					<form:option value="6" label="活期赎回" />
					<form:option value="7" label="活动返现" />
					<form:option value="8" label="活期收益" />
					<form:option value="9" label="邀请奖励" />
					<form:option value="10" label="优惠券" />
					<form:option value="11" label="体验金" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">金额：</label>
			<div class="controls">
				<form:input path="amount" htmlEscape="false" class="input-xlarge required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">可用余额：</label>
			<div class="controls">
				<form:input path="avaliableAmount" htmlEscape="false" class="input-xlarge required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">收支类型：</label>
			<div class="controls">
				<form:select path="inOutType" class="input-xlarge required">
					<form:option value="" label="请选择" />
					<form:option value="1" label="收入" />
					<form:option value="2" label="支出" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="500" class="input-xxlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="state" class="input-xlarge required">
					<form:option value="" label="请选择" />
					<form:option value="1" label="处理中" />
					<form:option value="2" label="成功" />
					<form:option value="3" label="失败" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="cgb:cgbUserTransDetail:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" />&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>