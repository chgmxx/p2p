<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>借款申请管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#messageBox").show();
		$("#export").click(function(){
			$("#searchForm").attr("action","${ctx}/apply/creditUserApply/downloadVoucherInfoList");
			$("#searchForm").submit();
		});
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action","${ctx}/apply/creditUserApply/creditVoucherApplyList");
			$("#searchForm").submit();
		});
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
		<li class="active"><a href="${ctx}/apply/creditUserApply/creditVoucherApplyList">申请列表</a></li>
		<!-- 
		<shiro:hasPermission name="apply:creditUserApply:edit">
			<li><a href="${ctx}/apply/creditUserApply/form">借款申请添加</a></li>
		</shiro:hasPermission>
		-->
	</ul>
	<form:form id="searchForm" modelAttribute="creditUserApply" action="${ctx}/apply/creditUserApply/creditVoucherApplyList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label class="label">申请日期：</label> <input name="beginCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${creditUserApply.beginCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input name="endCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${creditUserApply.endCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
			<li>
				<label class="label">状态：</label>
				<form:select path="voucherState" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:option value="1" label="申请中"/>
					<form:option value="2" label="审核通过"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="btns"><input id="export" class="btn btn-primary" type="submit" value="导出" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>资料名称</th>
				<th>企业名称</th>
				<th>企业类型</th>
				<th>服务费金额</th>
				<th>申请时间</th>
				<th>修改时间</th>
				<th>抬头</th>
				<th>税号</th>
				<th>地址</th>
				<th>电话</th>
				<th>开户行</th>
				<th>开户账号</th>
				<th>发票收件人姓名</th>
				<th>发票收件人电话</th>
				<th>发票收件人地址</th>
				<th>状态</th>
				<shiro:hasPermission name="apply:creditUserApply:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="creditUserApply">
				<tr>
					<td>${creditUserApply.projectDataInfo.name}</td>
					<td>${creditUserApply.creditUser.enterpriseFullName}</td>
					<c:if test="${creditUserApply.creditUser.creditUserType == '11' }">
						<td>核心企业</td>
					</c:if>
					<c:if test="${creditUserApply.creditUser.creditUserType == '02' }">
						<td>供应商</td>
					</c:if>
					
					<td>${creditUserApply.sumFee}</td>
					
					
					<td><fmt:formatDate value="${creditUserApply.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${creditUserApply.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${creditUserApply.creditVoucherInfoDetail.title}</td>
					<td>${creditUserApply.creditVoucherInfoDetail.number}</td>
					<td>${creditUserApply.creditVoucherInfoDetail.addr}</td>
					<td>${creditUserApply.creditVoucherInfoDetail.phone}</td>
					<td>${creditUserApply.creditVoucherInfoDetail.bankName}</td>
					<td>${creditUserApply.creditVoucherInfoDetail.bankNo}</td>
					<td>${creditUserApply.creditVoucherInfoDetail.toName}</td>
					<td>${creditUserApply.creditVoucherInfoDetail.toPhone}</td>
					<td>${creditUserApply.creditVoucherInfoDetail.toAddr}</td>
					<c:if test="${creditUserApply.creditVoucherInfoDetail.state == '1' }">
							<td>未开票</td>
					</c:if>
					<c:if test="${creditUserApply.creditVoucherInfoDetail.state == '2' }">
							<td>已开票</td>
					</c:if>
					
					<shiro:hasPermission name="apply:creditUserApply:edit">
						<td>
							<c:if test="${creditUserApply.voucherState == '1' }">
								<a href="${ctx}/apply/creditUserApply/creditVoucherApplyOK?id=${creditUserApply.id}" style="padding-right:9px;" onclick="return confirmx('确认要执行【开票】操作吗？', this.href)"><label class="label">开票</label></a>
							</c:if>
						</td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>