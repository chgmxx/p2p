<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户流水记录管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			// 查询.
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/transdetail/userTransDetailCash/");
				$("#searchForm").submit();
			});
			// 导出.
			$("#btnExport").click(function(){
				top.$.jBox.confirm("客户提现流水数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/transdetail/userTransDetailCash/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/transdetail/userTransDetailCash/");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/transdetail/userTransDetailCash/">客户流水提现记录列表</a></li>
		<shiro:hasPermission name="transdetail:userTransDetailCash:edit"><li><a href="${ctx}/transdetail/userTransDetailCash/form">客户流水记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userTransDetail" action="${ctx}/transdetail/userTransDetailCash/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>交易时间：</label>
				<input name="beginTransDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="开始日期"
					value="<fmt:formatDate value="${userTransDetail.beginTransDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label> 至 ：</label>
				<input name="endTransDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="结束日期"
					value="<fmt:formatDate value="${userTransDetail.endTransDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			
		</ul>
		<ul class="ul-form"><li><label>筛选条件：</label>
			<form:select path="state" class="input-large" id="">
				<form:option value="" label="请选择" />
				<form:options items="${fns:getDictList('user_trans_detail_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select></li>
			
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
			</li>
			<li class="clearfix"></li>
		</ul>
			
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号</th>
				<th>姓名</th>
				<th>交易时间</th>
				<th>交易类型</th>
				<th>交易金额</th>
				<th>当前可用余额</th>
				<th>当前代收本金</th>
				<th>收支类型</th>
				<th>备注</th>
				<th>交易状态</th>
				<shiro:hasPermission name="transdetail:userTransDetail:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userTransDetail">
			<tr>
				<td><a href="${ctx}/transdetail/userTransDetail/form?id=${userTransDetail.id}">
					${userTransDetail.userInfo.name}
				</td>
				<td>
					${userTransDetail.userInfo.realName}
				</td>
				<td>
					<fmt:formatDate value="${userTransDetail.transDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(userTransDetail.trustType, 'trans_detail_type', '')}
				</td>
				<td>
					${userTransDetail.amount}
				</td>
				<td>
					<fmt:formatNumber type="number" value="${userTransDetail.avaliableAmount}" minFractionDigits="2" maxFractionDigits="2" />
				</td>
				<td>
					<fmt:formatNumber type="number" value="${userTransDetail.userAccountInfo.regularDuePrincipal}" minFractionDigits="2" maxFractionDigits="2" />
				</td>
				<td>
					${fns:getDictLabel(userTransDetail.inOutType, 'trans_detail_inout_type', '')}
				</td>
				<td>
					${userTransDetail.remarks}
				</td>
				<td>
					${fns:getDictLabel(userTransDetail.state, 'trans_detail_inout_state', '')}
				</td>
				<shiro:hasPermission name="transdetail:userTransDetail:edit"><td>
    				<a href="${ctx}/transdetail/userTransDetail/form?id=${userTransDetail.id}">修改</a>
					<a href="${ctx}/transdetail/userTransDetail/delete?id=${userTransDetail.id}" onclick="return confirmx('确认要删除该客户流水记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>