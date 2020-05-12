<%@page import="javax.validation.ConstraintViolation"%>
<%@page import="javax.validation.ConstraintViolationException"%>
<%@page import="org.springframework.validation.BindException"%>
<%@page import="org.springframework.validation.ObjectError"%>
<%@page import="org.springframework.validation.FieldError"%>
<%@page import="org.slf4j.Logger,org.slf4j.LoggerFactory"%>
<%@page import="com.power.platform.common.web.Servlets"%>
<%@page import="com.power.platform.common.utils.Exceptions"%>
<%@page import="com.power.platform.common.utils.StringUtils"%>
<%@page contentType="text/html;charset=UTF-8" isErrorPage="true"%>
<%@include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>资金存管响应</title>
</head>
<body>
	<div class="container-fluid">
		<div class="page-header">
			<h3>《海口联合农商银行》资金存管</h3>
			<%@include file="/WEB-INF/views/include/head.jsp"%>
		</div>
		<div class="errorMessage form-horizontal">
			<div class="control-group">
				<label class="control-label">接口响应信息:</label>
				<div class="controls">
					<c:if test="${respCode == '00' }">
						<b style="color: red;">成功</b>
					</c:if>
					<c:if test="${respCode == '01' }">
						<b style="color: red;">处理中</b>
					</c:if>
					<c:if test="${respCode == '02' }">
						<b style="color: red;">失败</b>
					</c:if>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">返回详细信息:</label>
				<div class="controls">
					<b style="color: red;">${respMsg}</b>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">返回码说明:</label>
				<div class="controls">
					<c:if test="${respSubCode == '000000' }">
						<b style="color: red;">${respSubCode} = {成功}</b>
					</c:if>
					<c:if test="${respSubCode == '000100' }">
						<b style="color: red;">${respSubCode} = {受理成功}</b>
					</c:if>
					<c:if test="${respSubCode == '000001' }">
						<b style="color: red;">${respSubCode} = {处理中}</b>
					</c:if>
					<c:if test="${respSubCode == '000002' }">
						<b style="color: red;">${respSubCode} = {失败}</b>
					</c:if>
					<c:if test="${respSubCode == '100000' }">
						<b style="color: red;">${respSubCode} = {参数校验失败}</b>
					</c:if>
					<c:if test="${respSubCode == '100001' }">
						<b style="color: red;">${respSubCode} = {参数超限}</b>
					</c:if>
					<c:if test="${respSubCode == '200000' }">
						<b style="color: red;">${respSubCode} = {不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200001' }">
						<b style="color: red;">${respSubCode} = {用户不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200002' }">
						<b style="color: red;">${respSubCode} = {商户不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200003' }">
						<b style="color: red;">${respSubCode} = {账户号不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200004' }">
						<b style="color: red;">${respSubCode} = {标的不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200005' }">
						<b style="color: red;">${respSubCode} = {订单不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200006' }">
						<b style="color: red;">${respSubCode} = {银行卡不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200007' }">
						<b style="color: red;">${respSubCode} = {服务不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200008' }">
						<b style="color: red;">${respSubCode} = {交易密码不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200009' }">
						<b style="color: red;">${respSubCode} = {配置参数不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200901' }">
						<b style="color: red;">${respSubCode} = {BOSS用户不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200902' }">
						<b style="color: red;">${respSubCode} = {BOSS角色不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200903' }">
						<b style="color: red;">${respSubCode} = {BOSS菜单不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200904' }">
						<b style="color: red;">${respSubCode} = {BOSS功能不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200905' }">
						<b style="color: red;">${respSubCode} = {BOSS用户信息重复}</b>
					</c:if>
					<c:if test="${respSubCode == '200906' }">
						<b style="color: red;">${respSubCode} = {商户BOSS用户不存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200907' }">
						<b style="color: red;">${respSubCode} = {商户BOSS用户已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200012' }">
						<b style="color: red;">${respSubCode} = {批次明细为空}</b>
					</c:if>
					<c:if test="${respSubCode == '200013' }">
						<b style="color: red;">${respSubCode} = {还款订单集合为空}</b>
					</c:if>
					<c:if test="${respSubCode == '200014' }">
						<b style="color: red;">${respSubCode} = {还款订单数量超出上限}</b>
					</c:if>
					<c:if test="${respSubCode == '200015' }">
						<b style="color: red;">${respSubCode} = {总金额总笔数与订单明细汇总不符}</b>
					</c:if>
					<c:if test="${respSubCode == '200016' }">
						<b style="color: red;">${respSubCode} = {支付key未配置}</b>
					</c:if>
					<c:if test="${respSubCode == '200017' }">
						<b style="color: red;">${respSubCode} = {网银充值失败}</b>
					</c:if>
					<c:if test="${respSubCode == '200019' }">
						<b style="color: red;">${respSubCode} = {网银充值订单已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200020' }">
						<b style="color: red;">${respSubCode} = {网银充值订单回调金额与充值金额不符}</b>
					</c:if>
					<c:if test="${respSubCode == '200021' }">
						<b style="color: red;">${respSubCode} = {当前标的状态不允许修改}</b>
					</c:if>
					<c:if test="${respSubCode == '200022' }">
						<b style="color: red;">${respSubCode} = {充值订单回调金额与充值金额不符}</b>
					</c:if>
					<c:if test="${respSubCode == '200023' }">
						<b style="color: red;">${respSubCode} = {线下充值账务充值失败}</b>
					</c:if>
					<c:if test="${respSubCode == '200024' }">
						<b style="color: red;">${respSubCode} = {活期还款冻结金额不足}</b>
					</c:if>
					<c:if test="${respSubCode == '200025' }">
						<b style="color: red;">${respSubCode} = {代偿原借款人和标的报备借款人不符}</b>
					</c:if>
					<c:if test="${respSubCode == '200100' }">
						<b style="color: red;">${respSubCode} = {存在该类型账户}</b>
					</c:if>
					<c:if test="${respSubCode == '200101' }">
						<b style="color: red;">${respSubCode} = {用户已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200102' }">
						<b style="color: red;">${respSubCode} = {商户已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200103' }">
						<b style="color: red;">${respSubCode} = {商户流水已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200126' }">
						<b style="color: red;">${respSubCode} = {商户子订单流水已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200104' }">
						<b style="color: red;">${respSubCode} = {交易流水已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200105' }">
						<b style="color: red;">${respSubCode} = {账户号已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200106' }">
						<b style="color: red;">${respSubCode} = {账务流水已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200107' }">
						<b style="color: red;">${respSubCode} = {标的已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200108' }">
						<b style="color: red;">${respSubCode} = {银行卡已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200109' }">
						<b style="color: red;">${respSubCode} = {交易密码已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200110' }">
						<b style="color: red;">${respSubCode} = {服务已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200111' }">
						<b style="color: red;">${respSubCode} = {转账批次已存在}</b>
					</c:if>
					<c:if test="${respSubCode == '200112' }">
						<b style="color: red;">${respSubCode} = {标的状态非投资中,不允许流标}</b>
					</c:if>
				</div>
			</div>
		</div>
		<a href="javascript:" onclick="history.go(-1);" class="btn">返回上一页</a> &nbsp; <br /> <br />
		<script>
			try {
				top.$.jBox.closeTip();
			} catch (e) {
			}
		</script>
	</div>
</body>
</html>