<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户流水记录管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			//查询
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/transdetail/userTransDetail/");
				$("#searchForm").submit();
			});
			
			//账户对账
			$("#btnSubmit1").click(function(){
				top.$.jBox.confirm("确认要导出账户对账结果数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action", "${ctx}/transdetail/userTransDetail/checkaccount");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			//导出客户交易流水
			$("#btnSubmit2").click(function(){
				top.$.jBox.confirm("确认要导出该客户交易流水数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action", "${ctx}/transdetail/userTransDetail/exportdetail");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			//导出充值提现订单对账
			$("#btnSubmit3").click(function(){
				top.$.jBox.confirm("确认要导出充值提现订单对账结果数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action", "${ctx}/transdetail/userTransDetail/checkorder");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action", "${ctx}/transdetail/userTransDetail/");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/transdetail/userTransDetail/">客户流水记录列表</a></li>
		<shiro:hasPermission name="transdetail:userTransDetail:edit"><li><a href="${ctx}/transdetail/userTransDetail/form">客户流水记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userTransDetail" action="${ctx}/transdetail/userTransDetail/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号：</label>
				<form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>姓名：</label>
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>交易类型：</label>
				<form:select path="trustType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('trans_detail_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>收支类型：</label>
				<form:select path="inOutType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('trans_detail_inout_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
		</ul>
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
			<li><label>交易状态：</label>
				<form:select path="state" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('trans_detail_inout_state')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label></label>
				<label></label>
			</li>
			<li class="btns"><label></label><input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/></li>
			<li class="btns"><label></label><input id="btnSubmit1" class="btn btn-primary" type="button" value="账户对账" style="margin-right: -13px;"/></li>
			<li class="btns"><label></label><input id="btnSubmit2" class="btn btn-primary" type="button" value="导出流水"/></li>
			<li class="btns"><label></label><input id="btnSubmit3" class="btn btn-primary" type="button" value="订单对账"/></li>
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