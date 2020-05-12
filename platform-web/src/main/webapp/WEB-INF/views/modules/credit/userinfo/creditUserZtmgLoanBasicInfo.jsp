<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>信贷用户管理</title>
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
<style type="text/css">
.font_style {
	font-family:宋体;
	font-size: 16px;
	padding-top: 4px;
}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/credit/userinfo/creditUserInfo/">信贷用户列表</a></li>
		<li class="active"><a href="${ctx}/credit/userinfo/creditUserInfo/creditUserZtmgLoanBasicInfo?id=${creditUserId}">基本信息</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="" action="" method="post" class="form-horizontal">
		<sys:message content="${message}"/>		
		<!-- 公司名称，公司法人代表，注册地址. -->
		<div class="control-group">
			<div class="span7">
				<label class="control-label font_style">公司名称：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.companyName}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style">${ztmgLoanBasicInfo.companyName}</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="span7">
				<label class="control-label">公司法人代表：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.operName}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style">${ztmgLoanBasicInfo.operName}</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="span7">
				<label class="control-label">注册地址：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.registeredAddress}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style">${ztmgLoanBasicInfo.registeredAddress}</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		<!-- 注册资本（元），实缴资本（元），成立时间. -->
		<div class="control-group">
			<div class="span7">
				<label class="control-label">注册资本（元）：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.registeredCapital}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style">${ztmgLoanBasicInfo.registeredCapital}</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="span7">
				<label class="control-label">实缴资本（元）：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.contributedCapital}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style">${ztmgLoanBasicInfo.contributedCapital}</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="span7">
				<label class="control-label">成立时间：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.setUpTime}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style"><fmt:formatDate value="${ztmgLoanBasicInfo.setUpTime}" pattern="yyyy-MM-dd"/></span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		<!-- 办公地点，所属行业，经营区域. -->
		<div class="control-group">
			<div class="span7">
				<label class="control-label">办公地点：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.province}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style">${ztmgLoanBasicInfo.province}${ztmgLoanBasicInfo.city}${ztmgLoanBasicInfo.county}${ztmgLoanBasicInfo.street}</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="span7">
				<label class="control-label">所属行业：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.industry}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_01'}">
						<span class="help-inline font_style">农林牧渔业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_02'}">
								<span class="help-inline font_style">采矿业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_03'}">
								<span class="help-inline font_style">制造业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_04'}">
								<span class="help-inline font_style">电力热力燃气及水生产</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_05'}">
								<span class="help-inline font_style">供应业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_06'}">
								<span class="help-inline font_style">建筑业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_07'}">
								<span class="help-inline font_style">批发和零售业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_08'}">
								<span class="help-inline font_style">交通运输仓储业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_09'}">
								<span class="help-inline font_style">住宿和餐饮业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_10'}">
								<span class="help-inline font_style">信息传输软件和信息技术服务业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_11'}">
								<span class="help-inline font_style">金融业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_12'}">
								<span class="help-inline font_style">房地产业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_13'}">
								<span class="help-inline font_style">租赁和商务服务业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_14'}">
								<span class="help-inline font_style">科研和技术服务业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_15'}">
								<span class="help-inline font_style">水利环境和公共设施管理业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_16'}">
								<span class="help-inline font_style">居民服务修理和其他服务业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_17'}">
								<span class="help-inline font_style">教育</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_18'}">
								<span class="help-inline font_style">卫生和社会工作</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_19'}">
								<span class="help-inline font_style">文化体育和娱乐业</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_20'}">
								<span class="help-inline font_style">公共管理</span>
							</c:if>
							<c:if test="${ztmgLoanBasicInfo.industry == 'INDUSTRY_21'}">
								<span class="help-inline font_style">社会保障和社会组织</span>
							</c:if>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="span7">
				<label class="control-label">经营区域：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.scope}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style">${ztmgLoanBasicInfo.scope}</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		<!-- 年营业收入（元），负债（元），征信信息. -->
		<div class="control-group">
			<div class="span7">
				<label class="control-label">年营业收入（元）：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.annualRevenue}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style">${ztmgLoanBasicInfo.annualRevenue}</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="span7">
				<label class="control-label">负债（元）：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.liabilities}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style">${ztmgLoanBasicInfo.liabilities}</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="span7">
				<label class="control-label">征信信息：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.creditInformation}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style">${ztmgLoanBasicInfo.creditInformation}</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		<!-- 其它平台借贷余额. -->
		<div class="control-group">
			<div class="span7">
				<label class="control-label">其它平台借贷余额：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.otherCreditInformation}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<span class="help-inline font_style">${ztmgLoanBasicInfo.otherCreditInformation}</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="span7">
				<label class="control-label">信用承诺书：</label>
				<div class="controls">
					<c:choose>
						<c:when test="${empty ztmgLoanBasicInfo.declarationFilePath}">
							<span class="help-inline font_style">-----------------</span>
						</c:when>
						<c:otherwise>
							<a id="credit_pledge_a_id" class="pull-left" href="${mainPath}${ztmgLoanBasicInfo.declarationFilePath}" target="_Blank">信用承诺书</a>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		<c:forEach items="${ztmgLoanShareholdersInfos}" var="ztmgLoanShareholdersInfo">
			<div class="control-group">
				<div class="span7">
					<label class="control-label">股东类型：</label>
					<div class="controls">
						<c:if test="${ztmgLoanShareholdersInfo.shareholdersType == 'SHAREHOLDERS_TYPE_01'}">
							<span class="help-inline font_style">自然人</span>
						</c:if>
						<c:if test="${ztmgLoanShareholdersInfo.shareholdersType == 'SHAREHOLDERS_TYPE_02'}">
							<span class="help-inline font_style">法人</span>
						</c:if>
					</div>
				</div>
				<div class="span7">
					<label class="control-label">股东证件类型：</label>
					<div class="controls">
						<c:if test="${ztmgLoanShareholdersInfo.shareholdersCertType == 'SHAREHOLDERS_CERT_TYPE_01'}">
							<span class="help-inline font_style">居民身份证</span>
						</c:if>
						<c:if test="${ztmgLoanShareholdersInfo.shareholdersCertType == 'SHAREHOLDERS_CERT_TYPE_02'}">
							<span class="help-inline font_style">营业执照</span>
						</c:if>
					</div>
				</div>
				<div class="span7">
					<label class="control-label">股东名称：</label>
					<div class="controls">
						<span class="help-inline font_style">${ztmgLoanShareholdersInfo.shareholdersName}</span>
					</div>
				</div>
			</div>
		</c:forEach>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>