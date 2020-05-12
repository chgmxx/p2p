<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>加息券字典数据管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		//$("#name").focus();
		$("#inputForm").validate({
			submitHandler : function(form) {
				loading('正在提交，请稍等...');
				form.submit();
			},
			errorContainer : "#messageBox",
			errorPlacement : function(error, element) {
				$("#messageBox").text("输入有误，请先更正。");
				if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
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
		<li><a href="${ctx}/activity/aRateCouponDic/">加息券字典数据列表</a></li>
		<li class="active"><a href="${ctx}/activity/aRateCouponDic/addForm?id=${aRateCouponDic.id}">加息券字典数据<shiro:hasPermission name="activity:aRateCouponDic:edit">${not empty aRateCouponDic.id?'修改':'添加'}</shiro:hasPermission> <shiro:lacksPermission name="activity:aRateCouponDic:edit">查看</shiro:lacksPermission></a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="aRateCouponDic" action="${ctx}/activity/aRateCouponDic/addSave" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="state" class="input-xlarge ">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('a_rate_coupon_dic_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">逾期天数(天)：</label>
			<div class="controls">
				<form:input path="overdueDays" htmlEscape="false" maxlength="11" class="input-xlarge  digits" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">利率(加息)：</label>
			<div class="controls">
				<form:input path="rate" htmlEscape="false" class="input-xlarge  number" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">起投金额：</label>
			<div class="controls">
				<form:input path="limitAmount" htmlEscape="false" class="input-xlarge  number" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge " />
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="activity:aRateCouponDic:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" />&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>