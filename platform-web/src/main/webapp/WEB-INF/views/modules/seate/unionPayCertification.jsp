<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>学历信息查询</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		// 
		var flag = $("#verifyTypeId").children('option:selected').val();
		if(flag == "0020"){
			$("#acctNoId").show(); // 卡号显示.
			$("#acctNameId").show(); // 户名显示.
			$("#certNoId").hide(); // 证件号隐藏.
			$("#phoneId").hide(); // 手机号码隐藏.
		} else if(flag == "0021"){
			$("#acctNoId").show(); // 卡号显示.
			$("#certNoId").show(); // 证件号显示.
			$("#acctNameId").hide(); // 户名隐藏.
			$("#phoneId").hide(); // 手机号码隐藏.
		} else if(flag == "0022"){
			$("#acctNoId").show(); // 卡号显示.
			$("#phoneId").show(); // 手机号码显示.
			$("#certNoId").hide(); // 证件号隐藏.
			$("#acctNameId").hide(); // 户名隐藏.
		} else if(flag == "0030"){
			$("#acctNoId").show(); // 卡号显示.
			$("#acctNameId").show(); // 户名显示.
			$("#certNoId").show(); // 证件号显示.
			$("#phoneId").hide(); // 手机号码隐藏.
		} else if(flag == "0040"){
			$("#acctNoId").show(); // 卡号显示.
			$("#acctNameId").show(); // 户名显示.
			$("#certNoId").show(); // 证件号显示.
			$("#phoneId").show(); // 手机号码显示.
		}
		// 下拉选择器.
		$("#verifyTypeId").change(function() {
			var verifyType = $(this).children('option:selected').val();
			if (verifyType == "0020") {
				$("#acctNoId").show(); // 卡号显示.
				$("#acctNameId").show(); // 户名显示.
				$("#certNoId").hide(); // 证件号隐藏.
				$("#phoneId").hide(); // 手机号码隐藏.
			} else if(verifyType == "0021"){
				$("#acctNoId").show(); // 卡号显示.
				$("#certNoId").show(); // 证件号显示.
				$("#acctNameId").hide(); // 户名隐藏.
				$("#phoneId").hide(); // 手机号码隐藏.
			} else if(verifyType == "0022"){
				$("#acctNoId").show(); // 卡号显示.
				$("#phoneId").show(); // 手机号码显示.
				$("#certNoId").hide(); // 证件号隐藏.
				$("#acctNameId").hide(); // 户名隐藏.
			} else if(verifyType == "0030"){
				$("#acctNoId").show(); // 卡号显示.
				$("#acctNameId").show(); // 户名显示.
				$("#certNoId").show(); // 证件号显示.
				$("#phoneId").hide(); // 手机号码隐藏.
			} else if(verifyType == "0040"){
				$("#acctNoId").show(); // 卡号显示.
				$("#acctNameId").show(); // 户名显示.
				$("#certNoId").show(); // 证件号显示.
				$("#phoneId").show(); // 手机号码显示.
			}
		});
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/union/pay/certification/form">银联信息</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="seateEntity" action="${ctx}/union/pay/certification/query" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li><label>认证类型：</label> <form:select path="verifyType" class="input-large" id="verifyTypeId">
					<form:option value="" label="请选择" />
					<form:option value="0020" label="卡号+户名" />
					<form:option value="0021" label="卡号+证件" />
					<form:option value="0022" label="卡号+手机号" />
					<form:option value="0030" label="卡号+户名+证件" />
					<form:option value="0040" label="卡号+户名+证件+手机号" />
				</form:select></li>
			<li class="btns"><label></label> <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
		<ul class="ul-form" id="acctNoId">
			<li><label>卡号：</label> <form:input path="acctNo" htmlEscape="false" maxlength="32" class="input-medium required" /></li>
			<label style="color: red;">注：银联银行卡号</label>
		</ul>
		<ul class="ul-form" id="acctNameId">
			<li><label>户名：</label> <form:input path="acctName" htmlEscape="false" maxlength="32" class="input-medium required" /></li>
			<label style="color: red;">注：银行卡开户户名</label>
		</ul>
		<ul class="ul-form"  id="certNoId">
			<li><label>身份证：</label> <form:input path="certNo" htmlEscape="false" maxlength="32" class="input-medium required" /></li>
			<label style="color: red;">注：如果证件号码是身份证，且最后一位如果是X，则应该大写，小写无效</label>
		</ul>
		<ul class="ul-form" id="phoneId">
			<li><label>手机号：</label> <form:input path="phone" htmlEscape="false" maxlength="32" class="input-medium required" /></li>
			<label style="color: red;">注：银行卡绑定的手机号</label>
		</ul>
	</form:form>
	<br />
	<sys:message content="${message}" />
	<br />
	<ul class="nav nav-tabs form-horizontal">
		<div class="control-group">
			<div class="span6">
				<label class="control-label">结果代码：</label>
				<div class="controls">${result.statCode}</div>
			</div>
			<div class="span6">
				<label class="control-label">结果代码说明：</label>
				<div class="controls">${result.statMsg}</div>
			</div>
		</div>
	</ul>
</body>
</html>