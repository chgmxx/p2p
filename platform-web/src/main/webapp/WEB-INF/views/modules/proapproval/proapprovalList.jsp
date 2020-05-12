<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnprojectApprovalInfoExport").click(function(){
				top.$.jBox.confirm("确认要导出项目审批信息吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/approval/proinfo/exportProjectApprovalInfo");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/approval/proinfo/">放款申请列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="projectApproval" action="${ctx}/approval/proinfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>项目名称：</label>
				<form:input path="wloanTermProject.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>借款人：</label>
				<form:input path="wloanTermProject.wloanSubject.companyName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>借款期限：</label>
				<form:input path="wloanTermProject.span" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns">
				<label></label>
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<input id="btnprojectApprovalInfoExport" class="btn btn-primary" type="button" value="项目审批信息导出" />
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目名称</th>
				<th>项目编号</th>
				<th>借款人</th>
				<th>金额</th>
				<th>投资期限</th>
				<th>状态</th>
				<shiro:hasPermission name="projectApproval:projectApproval:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="projectApproval">
			<tr>
				<td>
					<a href="${ctx}/approval/proinfo/form?id=${projectApproval.id}">${projectApproval.wloanTermProject.name }</a>					
				</td>
				<td>
					${projectApproval.wloanTermProject.sn }
				</td>
				<td>
					${projectApproval.wloanTermProject.wloanSubject.companyName }
				</td>
				<td>
					${projectApproval.wloanTermProject.amount }
				</td>
				<td>
					${projectApproval.wloanTermProject.span }
				</td>
				<td>
					${fns:getDictLabel(projectApproval.state, 'appro_state', '拒绝')}
				</td>
				<shiro:hasPermission name="projectApproval:projectApproval:edit"><td>
					<c:if test="${ projectApproval.state == '' &&  usertype == '5' }">
    					<a href="${ctx}/approval/proinfo/form?id=${projectApproval.id}">修改</a>
    				</c:if>
					<c:if test="${ projectApproval.state == '1' &&  usertype == '6' }">
    					<a href="${ctx}/approval/proinfo/form?id=${projectApproval.id}">审批</a>
    				</c:if>
    				<c:if test="${ projectApproval.state == '2' &&  usertype == '7' }">
    					<a href="${ctx}/approval/proinfo/form?id=${projectApproval.id}">审批</a>
    				</c:if>
    				<c:if test="${ projectApproval.state == '6' &&  usertype == '9' }">
    					<a href="${ctx}/approval/proinfo/form?id=${projectApproval.id}">审批</a>
    				</c:if>
    				<c:if test="${ projectApproval.state == '3' &&  usertype == '1' }">
    					<a href="${ctx}/approval/proinfo/form?id=${projectApproval.id}">审批</a>
    				</c:if>
					<c:if test="${ projectApproval.state == '4' &&  usertype == '7' }">
    					<a href="${ctx}/approval/proinfo/form?id=${projectApproval.id}">放款</a>
    				</c:if>  				
    					<a href="${ctx}/approval/proinfo/form?id=${projectApproval.id}">查看审批信息</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>