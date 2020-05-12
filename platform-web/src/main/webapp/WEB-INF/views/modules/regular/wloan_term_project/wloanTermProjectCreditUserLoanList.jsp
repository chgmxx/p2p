<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>定期项目信息管理</title>
<meta name="decorator" content="default" />
<style type="text/css">
.input-medium-select {width:177px;}
</style>
<script type="text/javascript">
	$(document).ready(function() {
			//
			$("#messageBox").show();
			//
			$("#btnProjectInfoExport").click(function(){
				top.$.jBox.confirm("确认要执行【项目导出】操作吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/wloanproject/wloanTermProject/exportProjectInfo");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#btnRepayPlanInfoExport").click(function(){
				top.$.jBox.confirm("确认要执行【还款导出】操作吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/wloanproject/wloanTermProject/exportRepayPlanInfo");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
	});
	
	// 回车键上抬.	
	$(document).keyup(function(event){
		if(event.keyCode ==13){
			page();
		}
	});// --.
	
	// 分页查询.
	function page(n, s) {
		
		// 单选按钮赋值.
		$("#projectProductType").val($('input[name="project_product_type"]:checked').val());
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").attr("action","${ctx}/wloanproject/wloanTermProject/creditUserLoanList");
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/wloanproject/wloanTermProject/creditUserLoanList/">供应链列表</a></li>
		<shiro:hasPermission name="wloanproject:wloanTermProject:edit">
			<li><a href="${ctx}/wloanproject/wloanTermProject/creditUserLoanForm">供应链创建</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermProject" action="${ctx}/wloanproject/wloanTermProject/creditUserLoanList/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li>
				<label class="label">期限：</label> 
				<form:select path="span" class="input-medium-select">
					<form:option value="" label="请选择" />
					<form:option value="30" label="30" />
					<form:option value="90" label="90" />
					<form:option value="180" label="180" />
					<form:option value="360" label="360" />
				</form:select>
			</li>
			<li>
				<label class="label">状态：</label>
				<form:select path="state" class="input-medium-select">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('wloan_term_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</li>
			<li class="clearfix"></li>
			<li><label class="label">项目编号：</label> <form:input path="sn" htmlEscape="false" maxlength="32" class="input-medium" /></li>
			<li><label class="label">项目名称：</label> <form:input path="name" htmlEscape="false" maxlength="55" class="input-medium" /></li>
			<li><label class="label">融资主体：</label> <form:input path="wloanSubject.companyName" htmlEscape="false" maxlength="55" class="input-medium" /></li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();" />
				<input id="btnProjectInfoExport" class="btn btn-primary" type="button" value="项目导出" />
				<input id="btnRepayPlanInfoExport" class="btn btn-primary" type="button" value="还款导出" />
			</li>
			<li>
				<!-- 标的产品类型. -->
				<form:hidden path="projectProductType"/>
				<label class="btn">供应链<input id="project_product_type_2" type="radio" name="project_product_type" value="2" checked="checked" /></label>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目编号</th>
				<th>项目ID</th>
				<th>项目名称</th>
				<th>融资主体</th>
				<th>借款户</th>
				<th>借款人</th>
				<th>法定代表人</th>
				<th>融资金额</th>
				<th>年化利率(%)</th>
				<th>上线日期</th>
				<th>期限(天)</th>
				<!-- <th>还款方式</th> -->
				<th>放款金额</th>
				<th>产品类型</th>
				<th>状态</th>
				<th>还款计划</th>
				<th>受托支付</th>
				<!-- 
				<shiro:hasPermission name="cgb:cancel:edit">
					<th>流标</th>
				</shiro:hasPermission>
				 -->
				<shiro:hasPermission name="cgb:grant:edit">
					<th>放款</th>
				</shiro:hasPermission>
				<shiro:hasPermission name="cgb:grant:edit">
					<th>受托支付提现</th>
				</shiro:hasPermission>
				<shiro:hasPermission name="wloanproject:wloanTermProject:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanTermProject">
				<tr>
					<td>
						<a href="${ctx}/wloanproject/wloanTermProject/creditUserLoanCheck?id=${wloanTermProject.id}">${wloanTermProject.sn}</a>	
					</td>
					<td>
						${wloanTermProject.id}
					</td>
					<td>${wloanTermProject.name}</td>
					<td>${wloanTermProject.wloanSubject.companyName}</td>
					<td>${wloanTermProject.wloanSubject.loanPhone}</td>
					<td>${wloanTermProject.wloanSubject.companyName}</td><!-- ${wloanTermProject.loanUserName} -->
					<td>${wloanTermProject.wloanSubject.loanUser}</td>
					<td>${wloanTermProject.amount}</td>
					<td>${wloanTermProject.annualRate}</td>
					<td><fmt:formatDate value="${wloanTermProject.onlineDate}" pattern="yyyy-MM-dd" /></td>
					<td>${fns:getDictLabel(wloanTermProject.span, 'regular_wloan_span', '')}</td>
					<%-- <td>${fns:getDictLabel(wloanTermProject.repayType, 'wloan_repay_type', '')}</td> --%>
					<td>${wloanTermProject.currentRealAmount}</td>
					<td>
						<c:if test="${wloanTermProject.projectProductType == '1'}">
							<b>安心投</b>
						</c:if>
						<c:if test="${wloanTermProject.projectProductType == '2'}">
							<b>供应链</b>
						</c:if>
					</td>
					<td><b>${fns:getDictLabel(wloanTermProject.state, 'wloan_term_state', '')}</b></td>
					<td>
						<c:if test="${wloanTermProject.projectRepayPlanType == '0'}">
							<b>旧版</b>
						</c:if>
						<c:if test="${wloanTermProject.projectRepayPlanType == '1'}">
							<b>新版</b>
						</c:if>
					</td>
					<td>
						<c:if test="${wloanTermProject.isEntrustedPay == '0'}">
							<b>否</b>
						</c:if>
						<c:if test="${wloanTermProject.isEntrustedPay == '1'}">
							<b>是</b>
						</c:if>
					</td>
					<!-- 
					<shiro:hasPermission name="cgb:cancel:edit">
						<td>
							<c:choose>
								<c:when test="${wloanTermProject.state == '4' }">  
							   		<a href="${ctx}/cgb/p2p/trade/bid/cancel?id=${wloanTermProject.id}" onclick="return confirmx('确认要执行【流标】吗？', this.href)">流标</a>
							   	</c:when>
							   	<c:otherwise>
							   	</c:otherwise>
							</c:choose>
						</td>
					</shiro:hasPermission>
					 -->
					<!-- 放款. -->
					<shiro:hasPermission name="cgb:grant:edit">
						<td>
							<c:choose>
								<c:when test="${wloanTermProject.state == '5' }">  
									<a href="${ctx}/cgb/p2p/trade/bid/grant?id=${wloanTermProject.id}" onclick="return confirmx('确认要执行【放款】操作吗？', this.href)"><label class="label">放款</label></a>
								</c:when>
								<c:otherwise>
								</c:otherwise>
							</c:choose>
						</td>
					</shiro:hasPermission>
					<!-- 受托支付提现. -->
					<shiro:hasPermission name="cgb:entrustedWithdraw:edit">
						<td>
							<c:if test="${wloanTermProject.state == '6' && wloanTermProject.isEntrustedPay == '1' && wloanTermProject.isEntrustedWithdraw == '0'}">
								<a href="${ctx}/cgb/p2p/trade/bid/entrustedWithdraw?id=${wloanTermProject.id}" onclick="return confirmx('确认要执行【受托支付提现】操作吗？', this.href)"><label class="label">受托支付提现</label></a>
							</c:if>
							<c:if test="${wloanTermProject.state == '6' && wloanTermProject.isEntrustedPay == '1' && wloanTermProject.isEntrustedWithdraw == '1'}">
								<b>支付提现成功</b>
							</c:if>
						</td>
					</shiro:hasPermission>
					<!-- 项目操作. -->
					<shiro:hasPermission name="wloanproject:wloanTermProject:edit">
						<td>
							<!-- 撤销. -->
							<c:if test="${wloanTermProject.state == '0' }">
								<a href="${ctx}/wloanproject/wloanTermProject/creditUserLoanForm?id=${wloanTermProject.id}" onclick="return confirmx('确认要执行【修改】操作吗？', this.href)" style="padding-right:6px;"><label class="label">修改</label></a>
								<a href="${ctx}/wloanproject/wloanTermProject/delete?id=${wloanTermProject.id}" onclick="return confirmx('确认要执行【删除】操作吗？', this.href)"><label class="label">删除</label></a>
							</c:if> 
							<!-- 草稿. -->
							<c:if test="${wloanTermProject.state == '1' }">
								<a href="${ctx}/wloanproject/wloanTermProject/creditUserLoanForm?id=${wloanTermProject.id}" onclick="return confirmx('确认要执行【修改】操作吗？', this.href)" style="padding-right:6px;"><label class="label">修改</label></a>
								<a href="${ctx}/wloanproject/wloanTermProject/creditUserLoanCheck?id=${wloanTermProject.id}" onclick="return confirmx('确认要执行【提交审核】操作吗？', this.href)" style="padding-right:6px;"><label class="label">提交审核</label></a>
								<a href="${ctx}/wloanproject/wloanTermProject/delete?id=${wloanTermProject.id}" onclick="return confirmx('确认要执行【删除】操作吗？', this.href)"><label class="label">删除</label></a>
							</c:if> 
							<!-- 审核（审核 + 用户类型为风控经理.）. -->
							<c:if test="${wloanTermProject.state == '2' && usertype == '9' }">
								<a href="${ctx}/wloanproject/wloanTermProject/creditUserLoanCheck?id=${wloanTermProject.id}" onclick="return confirmx('确认要执行【审核】操作吗？', this.href)"><label class="label">审核</label></a>
							</c:if> 
							<!-- 发布. -->
							<c:if test="${wloanTermProject.state == '3'  }">
								<a href="${ctx}/wloanproject/wloanTermProject/creditUserLoanCheck?id=${wloanTermProject.id}" style="padding-right:6px;" onclick="return confirmx('确认要执行【上线】操作吗？', this.href)"><label class="label">上线</label></a>
								<%-- <a href="${ctx}/wloanproject/wloanTermProjectPlan/findByProId?proid=${wloanTermProject.id}"><b>还款计划</b></a> --%>
							</c:if> 
							<!-- 投标中. -->
							<c:if test="${wloanTermProject.state == '4'  }">
								<%-- <a href="${ctx}/wloanproject/wloanTermProject/creditUserLoanCheck?id=${wloanTermProject.id}" style="padding-right:6px;" onclick="return confirmx('确认要执行【切标】操作吗？', this.href)"><label class="label">切标</label></a> --%>
								<a href="${ctx}/lm/p2p/trade/miscarry?id=${wloanTermProject.id}" onclick="return confirmx('确认要执行【流标】操作吗？', this.href)"><label class="label">流标</label></a>
								<a href="${ctx}/wloanproject/wloanTermProjectPlan/findByProId?proid=${wloanTermProject.id}"><b>还款计划</b></a>
								<a href="${ctx}/wloan_term_invest/wloanTermInvest/findInvestByProId?projectId=${wloanTermProject.id}" style="padding-right:6px;"><b>出借详情</b></a>
							</c:if> 
							<!-- 放款申请（满标 + 用户类型为风控专员.）. -->
							<c:if test="${wloanTermProject.state == '5' && usertype == '5' }">
								<%-- <a href="${ctx}/approval/proinfo/form?projectid=${wloanTermProject.id}">放款申请</a> --%>
							</c:if>
							<!-- 满标. -->
							<c:if test="${wloanTermProject.state == '5'}">
								<a href="${ctx}/wloanproject/wloanTermProjectPlan/findByProId?proid=${wloanTermProject.id}" style="padding-right:6px;"><b>还款计划</b></a>
								<a href="${ctx}/wloan_term_invest/wloanTermInvest/findInvestByProId?projectId=${wloanTermProject.id}" style="padding-right:6px;"><b>出借详情</b></a>
							</c:if> 
							<!-- 还款中. -->
							<c:if test="${wloanTermProject.state == '6' }">
								<a href="${ctx}/wloanproject/wloanTermProjectPlan/findByProId?proid=${wloanTermProject.id}" style="padding-right:6px;"><b>还款计划</b></a>
								<a href="${ctx}/wloan_term_invest/wloanTermInvest/findInvestByProId?projectId=${wloanTermProject.id}" style="padding-right:6px;"><b>出借详情</b></a>
								<%-- <a href="${imgUrl}${wloanTermProject.contractUrl}" target="_blank"><b>项目合同</b></a> --%>
							</c:if>
							<!-- 已结束. -->
							<c:if test="${wloanTermProject.state == '7' }">
								<a href="${ctx}/wloanproject/wloanTermProjectPlan/findByProId?proid=${wloanTermProject.id}" style="padding-right:6px;"><b>还款计划</b></a>
								<a href="${ctx}/wloan_term_invest/wloanTermInvest/findInvestByProId?projectId=${wloanTermProject.id}" style="padding-right:6px;"><b>出借详情</b></a>
							</c:if>
							<!-- 流标. -->
							<c:if test="${wloanTermProject.state == '8' }">
								<a href="${ctx}/wloanproject/wloanTermProjectPlan/findByProId?proid=${wloanTermProject.id}" style="padding-right:6px;"><b>还款计划</b></a>
								<a href="${ctx}/wloan_term_invest/wloanTermInvest/findInvestByProId?projectId=${wloanTermProject.id}" style="padding-right:6px;"><b>出借详情</b></a>
							</c:if>
						</td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>