<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>奖品信息管理</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/lightbox.css" />
    <script type="text/javascript" src="${ctxStatic}/js/lightbox.js"></script>
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
<style type="text/css">
/**
文本不换行，这样超出一行的部分被截取，显示 ...
*/
.line-limit-length {
	max-width: 180px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/award/awardInfo/">奖品信息列表</a></li>
		<shiro:hasPermission name="award:awardInfo:edit"><li><a href="${ctx}/award/awardInfo/form">奖品信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="awardInfo" action="${ctx}/award/awardInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li>
				<label class="label">奖品积分：</label>
				<form:input path="needAmount" htmlEscape="false" class="input-medium"/>
			</li>
			<li>
				<label class="label">奖品名称：</label>
				<form:input path="name" htmlEscape="false" class="input-medium"/>
			</li>
			<li>
				<label class="label">状态：</label>
				<form:select path="state" class="input-medium">
					<form:option value="" label="请选择" />
					<form:option value="0" label="上架" />
					<form:option value="1" label="下架" />
					<form:option value="2" label="删除" />
				</form:select>
			</li>
			<li>
				<label class="label">抽奖奖品：</label>
				<form:select path="isLottery" class="input-medium">
					<form:option value="" label="请选择" />
					<form:option value="0" label="否" />
					<form:option value="1" label="是" />
				</form:select>
			</li>
			 <li>
			 	<label class="label">虚拟奖品：</label>
				<form:select path="isTrue" class="input-medium">
					<form:option value="" label="请选择" />
					<form:option value="0" label="否" />
					<form:option value="1" label="是" />
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>奖品名称</th>
				<th>所需积分</th>
				<th>奖品描述</th>
				<th>状态</th>
				<th>PC图片</th>
				<th>HS图片</th>
				<th>抽奖奖品</th>
				<th>虚拟奖品</th>
				<th>中奖概率</th>
				<shiro:hasPermission name="award:awardInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="awardInfo">
			<tr>
				<td>
					<a href="${ctx}/award/awardInfo/form?id=${awardInfo.id}">
						${awardInfo.name}
					</a>
				</td>
				<td>
					${awardInfo.needAmount}
				</td>
				<td>
					<p class="line-limit-length">${awardInfo.docs}</p>
				</td>
				<td>
					<c:if test="${awardInfo.state == '0'}">上架</c:if>
					<c:if test="${awardInfo.state == '1'}">下架</c:if>
					<c:if test="${awardInfo.state == '2'}">删除</c:if>
					
				</td>
				<td>
						<c:forEach items="${awardInfo.imgWebList}" var="imgWebList">
							<a class="example-image-link" href="${imgWebList}" data-lightbox="example-1">查看</a>
						</c:forEach>
				</td>
				<td>
						<c:forEach items="${awardInfo.imgWapList}" var="imgWapList">
							<a class="example-image-link" href="${imgWapList}" data-lightbox="example-1">查看</a>
						</c:forEach>
				</td>
				<td>
					<c:if test="${awardInfo.isLottery == '0'}">否</c:if>
					<c:if test="${awardInfo.isLottery == '1'}">是</c:if>
				</td>
				<td>
					<c:if test="${awardInfo.isTrue == '0'}">否</c:if>
					<c:if test="${awardInfo.isTrue == '1'}">是</c:if>
				</td>
				<td>
					${awardInfo.odds}%
				</td>
				<shiro:hasPermission name="award:awardInfo:edit"><td>
    				<a href="${ctx}/award/awardInfo/form?id=${awardInfo.id}">修改</a>
					<a href="${ctx}/award/awardInfo/delete?id=${awardInfo.id}" onclick="return confirmx('确认要删除该奖品信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>