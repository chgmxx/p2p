<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>抵用券管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">

	// --.
	$(document).ready(function() {

		//
		$("#messageBox").show();

		// --.
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action","${ctx}/activity/userVouchersHistory/");
			$("#searchForm").submit();
		});// --.

		// --.
		$("#btnExport").click(function(){
			top.$.jBox.confirm("确认要导出客户抵用券数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/activity/userVouchersHistory/export");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});// --.

	});// --.

	// 回车键上抬.	
	$(document).keyup(function(event){
		if(event.keyCode ==13){
			page();
		}
	});// --.

	// --.
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").attr("action","${ctx}/activity/userVouchersHistory/");
		$("#searchForm").submit();
		return false;
	}// --.

</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/activity/userVouchersHistory/">抵用券列表</a></li>
		<shiro:hasPermission name="activity:userVouchersHistory:edit">
			<li><a href="${ctx}/activity/userVouchersHistory/rechargeForm">抵用券充值</a></li>
		</shiro:hasPermission>
		<shiro:hasPermission name="activity:userVouchersHistory:edit">
			<li><a href="${ctx}/activity/userVouchersHistory/rechargeAllForm">抵用券批充</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userVouchersHistory" action="${ctx}/activity/userVouchersHistory/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li>
				<label class="label">移动电话：</label>
				<form:input path="userInfo.name" htmlEscape="false" class="input-medium" />
			</li>
			<li>
				<label class="label">使用状态：</label>
				<form:select path="state" class="input-medium">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('a_user_awards_history_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</li>
			<li>
				<label class="label">备注：</label>
				<form:input path="remark" htmlEscape="false" class="input-medium" />
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" />
				<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>	
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>移动电话</th>
				<th>姓名</th>
				<th>抵用券(元)</th>
				<th>使用状态</th>
				<th>类型</th>
				<th>项目期限范围</th>
				<th>投资项目</th>
				<th>充值原因</th>
				<th>备注</th>
				<th>获取日期</th>
				<th>逾期日期</th>
				<th>修改日期</th>
				<shiro:hasPermission name="activity:userVouchersHistory:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="userVouchersHistory">
				<tr>
					<td><a href="${ctx}/activity/userVouchersHistory/viewForm?id=${userVouchersHistory.id}"> ${userVouchersHistory.userInfo.name}</a></td>
					<td>${userVouchersHistory.userInfo.realName}</td>
					<td>${userVouchersHistory.value}</td>
					<td>${fns:getDictLabel(userVouchersHistory.state, 'a_user_awards_history_state', '')}</td>
					<td>${fns:getDictLabel(userVouchersHistory.type, 'a_user_awards_history_type', '')}</td>
					<td>
						<c:choose>
							<c:when test="${userVouchersHistory.spans == '1'}">
								<b>通用</b>
							</c:when>
							<c:otherwise>
								<b>${userVouchersHistory.spans}</b>
							</c:otherwise>
						</c:choose>
					</td>
					<td>${userVouchersHistory.wloanTermProject.name}</td>
					<td>${userVouchersHistory.rechargeReason}</td>
					<td>${userVouchersHistory.remark}</td>
					<td><fmt:formatDate value="${userVouchersHistory.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${userVouchersHistory.overdueDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${userVouchersHistory.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="activity:userVouchersHistory:edit">
						<td><a href="${ctx}/activity/userVouchersHistory/rechargeForm?id=${userVouchersHistory.id}">修改</a> <a href="${ctx}/activity/userVouchersHistory/delete?id=${userVouchersHistory.id}" onclick="return confirmx('确认要删除该抵用券吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>