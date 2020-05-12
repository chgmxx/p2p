<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>客户到期还款计划</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		// 
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action","${ctx}/report/userRepayPlan");
			$("#searchForm").submit();
		});// --.
		// 
		$("#btnExport").click(function(){
			top.$.jBox.confirm("确认要执行导出操作吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/report/userRepayPlan/export");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});// --.
	});
	
	// 回车键上抬.	
	$(document).keyup(function(event){
		if(event.keyCode ==13){
			page();
		}
	});// --.
	
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		// 分页时，重新定义action.
		$("#searchForm").attr("action","${ctx}/report/userRepayPlan");
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/report/userRepayPlan">还款列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermUserPlan" action="${ctx}/report/userRepayPlan" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<li><label class="label">还款日期：</label> <input placeholder="开始日期" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermUserPlan.beginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${wloanTermUserPlan.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
		<li><label class="label">手机号码：</label> <form:input path="userInfo.name" placeholder="手机号码" htmlEscape="false" maxlength="11" class="input-medium" /></li>
		<li>
			<label class="label">还款类型：</label>
			<form:select path="principal" class="input-large" id="">
				<form:option value="" label="请选择" />
				<form:options items="${fns:getDictList('wloan_term_user_plan_principal')}" itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</li>
		<li>
			<label class="label">还款状态：</label>
			<form:select path="state" class="input-large" id="">
				<form:option value="" label="请选择" />
				<form:options items="${fns:getDictList('wloan_term_user_plan_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</li>
		<li class="btns">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" />
			<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
		</li>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目名称</th>
				<th>项目编号</th>
				<c:if test="${fns:getUser().loginName == 'admin'}">	
				<th>客户姓名</th>
				</c:if>
				<th>出借客户</th>
				<th>还款金额</th>
				<th>还款类型</th>
				<th>还款日期</th>
				<th>还款状态</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="wloanTermUserPlan">
				<tr>
					<td>
						${wloanTermUserPlan.wloanTermProject.name}
					</td>
					<td>
						${wloanTermUserPlan.wloanTermProject.sn}
					</td>
					<c:if test="${fns:getUser().loginName == 'admin'}">			
						<td>
							${wloanTermUserPlan.userInfo.realName }
						</td>
					</c:if>
					<td>
						${wloanTermUserPlan.userInfo.name }
					</td>
					<td>
						${wloanTermUserPlan.interest}
					</td>
					<td>
						${wloanTermUserPlan.principal == '2' ? '利息' : '本息'}
					</td>
					<td>
						<fmt:formatDate value="${wloanTermUserPlan.repaymentDate}" pattern="yyyy-MM-dd"/>
					</td>
					<td>${fns:getDictLabel(wloanTermUserPlan.state, 'wloan_term_user_plan_state', '')}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>