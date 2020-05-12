<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>中登网登记信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
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
		<li class="active"><a href="${ctx}/zdw/register/zdwRegistrationInfo/">散标登记</a></li>
		<%-- <shiro:hasPermission name="zdw:register:zdwRegistrationInfo:edit"><li><a href="${ctx}/zdw/register/zdwRegistrationInfo/form">中登网登记信息添加</a></li></shiro:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="zdwRegistrationInfo" action="${ctx}/zdw/register/zdwRegistrationInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li>
				<label class="label">登记编号：</label>
				<form:input path="checkInNo" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li>
				<label class="label">修改码：</label>
				<form:input path="modifyCode" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li>
				<label class="label">状态：</label>
				<form:select path="status" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:option value="00" label="登记成功"/>
					<form:option value="01" label="等待登记"/>
					<form:option value="02" label="登记失败"/>
				</form:select>
			</li>
			<li class="clearfix"></li>
			<li>
				<label class="label">创建时间：</label>
				<input name="beginCreateDateTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="开始创建日期"
					value="<fmt:formatDate value="${zdwRegistrationInfo.beginCreateDateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> -
				<input name="endCreateDateTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="结束创建日期"
					value="<fmt:formatDate value="${zdwRegistrationInfo.endCreateDateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>登记编号</th>
				<th>修改码</th>
				<th>状态</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<th>登记证明文件</th>
				<th>备注信息</th>
				<%-- <shiro:hasPermission name="zdw:register:zdwRegistrationInfo:edit"><th>操作</th></shiro:hasPermission> --%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="zdwRegistrationInfo">
			<tr>
				<td>
					${zdwRegistrationInfo.checkInNo}
				</td>
				<td>
					${zdwRegistrationInfo.modifyCode}
				</td>
				<td>
					<c:if test="${zdwRegistrationInfo.status == '00'}">
						<b>登记成功</b>
					</c:if>
					<c:if test="${zdwRegistrationInfo.status == '01'}">
						<b>等待登记</b>
					</c:if>
					<c:if test="${zdwRegistrationInfo.status == '02'}">
						<b>登记失败</b>
					</c:if>
				</td>
				<td>
					<fmt:formatDate value="${zdwRegistrationInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${zdwRegistrationInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<a href="${mainPath}${zdwRegistrationInfo.proveFilePath}" target="_blank">登记证明文件</a>
				</td>
				<td>
					${zdwRegistrationInfo.remarks}
				</td>
				<%-- <shiro:hasPermission name="zdw:register:zdwRegistrationInfo:edit"><td>
    				<a href="${ctx}/zdw/register/zdwRegistrationInfo/form?id=${zdwRegistrationInfo.id}">修改</a>
					<a href="${ctx}/zdw/register/zdwRegistrationInfo/delete?id=${zdwRegistrationInfo.id}" onclick="return confirmx('确认要删除该中登网登记信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission> --%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>