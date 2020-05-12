<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnUserInfoExport").click(function(){
				top.$.jBox.confirm("确认要导出客户信息数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/userlog/exportLogList");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});

			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action","${ctx}/userlog/");
				$("#searchForm").submit();
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/userlog/");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/userlog/">用户信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="userLog" action="${ctx}/userlog/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="userName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>			
			<li><label>操作日期：</label>
				<input name="beginDate" type="text" id="d4311" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="开始日期"
					value="<fmt:formatDate value="${userLog.beginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,maxDate:'#F{$dp.$D(\'d4312\',{d:-1})}'});"/>-
				<input name="endDate" type="text" id="d4312" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="结束日期"
					value="<fmt:formatDate value="${userLog.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,minDate:'#F{$dp.$D(\'d4311\',{d:1})}'});"/>
			</li>
			<li class="btns">
				<label></label>
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<input id="btnUserInfoExport" class="btn btn-primary" type="button" value="导出" />
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号码</th>
				<th>类型</th>
				<th>简介</th>
				<th>操作时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userLog">
			<tr>
				<td>
					${userLog.userName}
				</td>
				<c:if test="${userLog.type == '1'}">
					<td>登录</td>
				</c:if>
				<c:if test="${userLog.type == '2'}">
					<td>开户</td>
				</c:if>
				<c:if test="${userLog.type == '3'}">
					<td>充值(转账)</td>
				</c:if>
				<c:if test="${userLog.type == '4'}">
					<td>充值（网银）</td>
				</c:if>
				<c:if test="${userLog.type == '5'}">
					<td>提现</td>
				</c:if>
				<td>
					${userLog.remark}
				</td>
				<td>
					<fmt:formatDate value="${userLog.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>

			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>