<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>银行托管-银行卡管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#lenders_id").click(function() { // 出借人列表.
			// 单选按钮事件.
			$("#bankCardRadioType").val("1");
			$("#searchForm").action = "${ctx}/cgb/cgbUserBankCard/";
			$("#searchForm").submit();
		});// --.
		$("#borrowers_id").click(function() { // 借款人列表.
			// 单选按钮事件.
			$("#bankCardRadioType").val("2");
			// console.log("单选按钮事件：" + $("#transDetailRadioType").val());
			$("#searchForm").action = "${ctx}/cgb/cgbUserBankCard/";
			$("#searchForm").submit();
		});// --.
		$("#btnSubmit").click(function() { // 出借人列表查询.
			// console.log("单选按钮值：" + $('input[name="trans_Detail_type"]:checked').val());
			$("#bankCardRadioType").val($('input[name="bank_card_type"]:checked').val());
			$("#searchForm").action = "${ctx}/cgb/cgbUserBankCard/";
			$("#searchForm").submit();
		});// --.
	});
	
	// 回车键上抬.	
	$(document).keyup(function(event){
		if(event.keyCode ==13){
			$("#bankCardRadioType").val($('input[name="bank_card_type"]:checked').val());
			$("#searchForm").action = "${ctx}/cgb/cgbUserBankCard/";
			$("#searchForm").submit();
		}
	});// --.
	
	
	function page(n, s) {
		
		// 单选按钮赋值.
		$("#bankCardRadioType").val($('input[name="bank_card_type"]:checked').val());
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/cgb/cgbUserBankCard/">银行卡列表</a></li>
		<!-- 
		<shiro:hasPermission name="cgb:cgbUserBankCard:edit">
			<li><a href="${ctx}/cgb/cgbUserBankCard/form">银行卡添加</a></li>
		</shiro:hasPermission>
		 -->
	</ul>
	<form:form id="searchForm" modelAttribute="cgbUserBankCard" action="${ctx}/cgb/cgbUserBankCard/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label class="label">银行卡：</label> <form:input path="bankAccountNo" htmlEscape="false" maxlength="50" class="input-medium" /></li>
			<li><label class="label">帐号：</label> <form:input path="userInfo.name" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label class="label">姓名：</label> <form:input path="userInfo.realName" htmlEscape="false" maxlength="64" class="input-medium" /></li>
			<li><label class="label">绑卡时间：</label> <input name="beginBindDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${cgbUserBankCard.beginBindDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input name="endBindDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${cgbUserBankCard.endBindDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
			<li><label class="label">银行预留手机：</label> <form:input path="bankCardPhone" htmlEscape="false" maxlength="11" class="input-medium" /></li>
			<li class="clearfix"></li>
			<li><label class="label">状态：</label>
				<form:select path="state" class="input-medium">
					<form:option value="" label="请选择" />
					<form:option value="0" label="未认证" />
					<form:option value="1" label="已认证" />
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="查询" /></li>
			<li>
				<form:hidden path="bankCardRadioType"/>
				<label class="btn">出借人~<input id="lenders_id" type="radio" name="bank_card_type" value="1" checked="checked" /></label>
				<label class="btn">借款人~<input id="borrowers_id" type="radio" name="bank_card_type" value="2" /></label>
			</li>
			<li class="clearfix"></li>
			<!-- <label><b style="color: windowtext;">帐号：</b></label><b style="color: orange;">159××××0605：投资人银行卡数据，159××××0605【借款人】：借款人银行卡数据，注销的帐号：客户帐号已注销的银行卡数据</b> -->
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>银行卡</th>
				<th>帐号</th>
				<th>姓名</th>
				<th>银行代码</th>
				<th>银行名称</th>
				<th>银行预留手机</th>
				<th>状态</th>
				<th>是否默认银行卡</th>
				<th>绑卡时间</th>
				<th>更新时间</th>
				<th>删除标记</th>
				<!-- 
				<shiro:hasPermission name="cgb:cgbUserBankCard:edit">
					<th>操作</th>
				</shiro:hasPermission>
				 -->
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="cgbUserBankCard">
				<tr>
					<td>${cgbUserBankCard.bankAccountNo}</td>
					<td>${cgbUserBankCard.userInfo.name}</td>
					<td>${cgbUserBankCard.userInfo.realName}</td>
					<td>${cgbUserBankCard.bankNo}</td>
					<td>${cgbUserBankCard.bankName}</td>
					<td>${cgbUserBankCard.bankCardPhone}</td>
					<td>
						<c:if test="${cgbUserBankCard.state == '0'}">
							<b>未认证</b>
						</c:if>
						<c:if test="${cgbUserBankCard.state == '1'}">
							<b>已认证</b>
						</c:if>
					</td>
					<td>
						<c:if test="${cgbUserBankCard.isDefault == '2'}">
							<b>默认</b>
						</c:if>
					</td>
					<td><fmt:formatDate value="${cgbUserBankCard.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${cgbUserBankCard.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><b>${fns:getDictLabel(cgbUserBankCard.delFlag, 'del_flag', '')}</b></td>
					<!-- 
					<shiro:hasPermission name="cgb:cgbUserBankCard:edit">
						<td><a href="${ctx}/cgb/cgbUserBankCard/form?id=${cgbUserBankCard.id}">修改</a> <a href="${ctx}/cgb/cgbUserBankCard/delete?id=${cgbUserBankCard.id}" onclick="return confirmx('确认要删除该银行托管-银行卡吗？', this.href)">删除</a></td>
					</shiro:hasPermission>
					 -->
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>