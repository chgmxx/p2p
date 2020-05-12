<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>中登网应收账款和转让记录登记列表</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		//
		$("#messageBox").show();
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
		<li class="active"> <a href="#">登记列表</a> </li>
		<%-- <shiro:hasPermission name="zdw:register:zdwSearchRegisterInfo:edit">
			<li><a href="${ctx}/zdw/register/zdwSearchRegisterInfo/form">中登网应收账款和转让记录登记列表添加</a></li>
		</shiro:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="zdwSearchRegisterInfo" action="${ctx}/zdw/register/zdwSearchRegisterInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li>
				<label class="label">查询人：</label>
				<span class="help-inline"><b>中投摩根信息技术（北京）有限责任公司</b></span>
			</li>
			<li class="clearfix"></li>
			<li>
				<label class="label">查询条件：</label>
				<span class="help-inline"><b>担保人名称-${zdwSearchRegisterInfo.guarantorCompanyName}</b></span>
			</li>
			<!-- <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li> -->
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<!-- <th>查询时间</th> -->
				<!-- <th>查询证明编号</th> -->
				<!-- <th>查询证明文件路径</th> -->
				<th>序号</th>
				<th>登记证明编号</th>
				<th>登记时间</th>
				<th>登记到期日</th>
				<th>登记种类</th>
				<th>质权人名称</th>
				<th>登记证明文件</th>
				<%-- <shiro:hasPermission name="zdw:register:zdwSearchRegisterInfo:edit">
					<th>操作</th>
				</shiro:hasPermission> --%>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="zdwSearchRegisterInfo">
				<tr>
					<%-- <td>
						<fmt:formatDate value="${zdwSearchRegisterInfo.queryDateTime}" pattern="yyyy-MM-dd HH:mm:ss" />
					</td> --%>
					<%-- <td>${zdwSearchRegisterInfo.queryProveNo}</td> --%>
					<%-- <td>${zdwSearchRegisterInfo.queryProveFilePath}</td> --%>
					<td>${zdwSearchRegisterInfo.no}</td>
					<td>${zdwSearchRegisterInfo.registerProveNo}</td>
					<td>
						<fmt:formatDate value="${zdwSearchRegisterInfo.registerDateTime}" pattern="yyyy-MM-dd HH:mm:ss" />
					</td>
					<td>
						<fmt:formatDate value="${zdwSearchRegisterInfo.registerExpireDateTime}" pattern="yyyy-MM-dd HH:mm:ss" />
					</td>
					<td>${zdwSearchRegisterInfo.registerType}</td>
					<td>${zdwSearchRegisterInfo.pledgeeName}</td>
					<td>
						<a href="${mainPath}${zdwSearchRegisterInfo.registerProveFilePath}" target="_blank">登记证明文件</a>
					</td>
					<%-- <shiro:hasPermission name="zdw:register:zdwSearchRegisterInfo:edit">
						<td><a
							href="${ctx}/zdw/register/zdwSearchRegisterInfo/form?id=${zdwSearchRegisterInfo.id}">修改</a>
							<a
							href="${ctx}/zdw/register/zdwSearchRegisterInfo/delete?id=${zdwSearchRegisterInfo.id}"
							onclick="return confirmx('确认要删除该中登网应收账款和转让记录登记列表吗？', this.href)">删除</a>
						</td>
					</shiro:hasPermission> --%>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>