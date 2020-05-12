<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户兑换奖品管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action","${ctx}/useraward/userAward/");
				$("#searchForm").submit();
			});
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出奖品兑换数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/useraward/userAward/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/useraward/userAward/");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/useraward/userAward/">用户兑换奖品列表</a></li>
		<shiro:hasPermission name="useraward:userAward:edit"><li><!--<a href="${ctx}/useraward/userAward/form">用户兑换奖品添加</a>--></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userAward" action="${ctx}/useraward/userAward/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户：</label>
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="64" class="input-medium" />
			</li>
			<li><label>手机号：</label>
				<form:input path="userInfo.name" htmlEscape="false" maxlength="64" class="input-medium" />
			</li>
			<li><label>快递单号：</label>  
				<form:input path="expressNo" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
		</ul>
		<ul class="ul-form">
			<li>
				<label>创建时间：</label>
				<input placeholder="开始日期" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${userAward.beginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input placeholder="结束日期" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${userAward.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" />
			</li>
		</ul>
		<ul class="ul-form">
			<li><label>奖品类型：</label>
				<form:select path="awardInfo.isTrue" class="input-medium">
					<form:option value="" label="请选择" />
					<form:option value="0" label="实体奖品" />
					<form:option value="1" label="虚拟奖品" />
				</form:select>
			</li>
			<li><label>奖品状态：</label>
				<form:select path="state" class="input-medium">
					<form:option value="" label="请选择" />
					<form:option value="0" label="待下单" />
					<form:option value="1" label="已下单" />
					<form:option value="2" label="已发货" />
					<form:option value="3" label="已结束" />
					<form:option value="4" label="已兑现" />
					<form:option value="5" label="已失效" />
				</form:select>
			</li>
			<li><label>兑奖类型：</label>
				<form:select path="awardGetType" class="input-medium">
					<form:option value="" label="请选择" />
					<form:option value="0" label="抽奖" />
					<form:option value="1" label="兑奖" />
				</form:select>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
				<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户</th>
				<th>手机号</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<th>奖品</th>
				<th>状态</th>
				<th>快递单号</th>
				<th>快递名称</th>
				<shiro:hasPermission name="useraward:userAward:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userAward">
			<tr>
				<td><a href="${ctx}/useraward/userAward/form?id=${userAward.id}">
					${userAward.userInfo.realName}
				</a></td>
				<td>
					${userAward.userInfo.name}
				</a></td>
				<td>
					<fmt:formatDate value="${userAward.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${userAward.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${userAward.awardInfo.name}
				</td>
				<td>
					<c:if test="${userAward.state == '0'}">待下单</c:if>
					<c:if test="${userAward.state == '1'}">已下单</c:if>
					<c:if test="${userAward.state == '2'}">已发货</c:if>
					<c:if test="${userAward.state == '3'}">已结束</c:if>
					<c:if test="${userAward.state == '4'}">已兑现</c:if>
					<c:if test="${userAward.state == '5'}">已失效</c:if>
				</td>
				<td>
					${userAward.expressNo}
				</td>
				<td>
					${userAward.expressName}
				</td>
				<shiro:hasPermission name="useraward:userAward:edit"><td>
    				<a href="${ctx}/useraward/userAward/form?id=${userAward.id}">修改</a>
					<!-- <a href="${ctx}/useraward/userAward/delete?id=${userAward.id}" onclick="return confirmx('确认要删除该用户兑换奖品吗？', this.href)">删除</a>-->
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>