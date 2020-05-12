<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>信贷用户管理</title>
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

	// "两位编码：\t" + grant.respCode + "<br/>" + "六位编码：\t" + grant.respSubCode + "<br/>" + "编码信息：\t" + grant.respMsg + "<br/>"
	// 用户授权查询.
	function memberAuthorizationSearch(userId){
		console.log("借款人ID：" + userId);
		$.ajax({
			url : "${ctx}/cgb/p2p/member/authorization/search?userId=" + userId, 
			type : "post", 
			success : function(data) {
				var grant = data.grant;
				console.log("respCode\t" + grant.respCode);
				$.jBox.alert(
								"<b>授权列表：</b>\t" + grant.grantList + "<br/>"
								+ "<b>授权金额列表：</b>\t" + grant.grantAmountList + "<br/>"
								+ "<b>授权期限列表：</b>\t" + grant.grantTimeList + "<br/>",
								'用户授权信息',
								{ top: '200px', width: 400, height: 200 });
			},
			error : function(data) {
				console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
			}
		});
	}
</script>
<style type="text/css">
	.select2-container {
		width: 300px;
	}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/credit/userinfo/creditUserInfo/">信贷用户列表</a></li>
		<c:if test="${creditUserType == 11 }">
		 <shiro:hasPermission name="credit:userinfo:creditUserInfo:edit"><li><a href="${ctx}/credit/userinfo/creditUserInfo//add">核心企业添加</a></li></shiro:hasPermission>
		</c:if>
	</ul>
	<form:form id="searchForm" modelAttribute="creditUserInfo" action="${ctx}/credit/userinfo/creditUserInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label class="label">项目类型：</label>
				<form:select path="accountType">
					<form:option value="" label="请选择" />
					<form:option value="1" label="安心投" />
					<form:option value="2" label="供应链" />
				</form:select>
			</li>
			<li><label class="label">项目名称：</label>
				<form:select path="ownedCompany">
				    <form:option value="" label="全部" />
					<c:forEach var="middlemen" items="${middlemenList}">
						<form:option value="${middlemen.enterpriseFullName}" label="${middlemen.enterpriseFullName}" />
					</c:forEach>
				</form:select>
			</li>
			<li><label class="label">公司名称：</label>
				<form:input path="enterpriseFullName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="clearfix"></li>
			<li><label class="label">手机号：</label>
				<form:input path="phone" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label class="label">姓名：</label>
				<form:input path="name" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>移动电话</th>
				<th>借款人ID</th>
				<th>借款人</th>
				<th>公司名称</th>
				<th>项目类型</th>
				<th>项目名称</th>
				<th>业务类型</th>
				<shiro:hasPermission name="cgb:memberAuthorization:view">
					<th>授权查询</th>
				</shiro:hasPermission>
				<th>资料清单</th>
				<th>基本信息</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditUserInfo">
			<tr>
				<td>
					<a href="${ctx}/credit/userinfo/creditUserInfo/form?id=${creditUserInfo.id}">${creditUserInfo.phone}</a>
				</td>
				<td>
					${creditUserInfo.id}
				</td>
				<td>
					${creditUserInfo.enterpriseFullName}<!-- ${creditUserInfo.name} -->
				</td>
				<td>
					${creditUserInfo.enterpriseFullName}
				</td>
				<td>
					<c:if test="${creditUserInfo.accountType == '1'}">
						安心投
					</c:if>
					<c:if test="${creditUserInfo.accountType == '2'}">
						供应链
					</c:if>
				</td>
				<td>
					${creditUserInfo.ownedCompany}
				</td>
				<td>
					<c:if test="${creditUserInfo.creditUserType == '01'}">
						投资户
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '02'}">
						借款户
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '03'}">
						担保户
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '04'}">
						咨询户
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '05'}">
						p2p平台户
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '08'}">
						营销户
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '10'}">
						收费户
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '11'}">
						代偿户
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '12'}">
						第三方营销账户
					</c:if>
					<c:if test="${creditUserInfo.creditUserType == '13'}">
						垫资账户
					</c:if>
				</td>
				<shiro:hasPermission name="cgb:memberAuthorization:view">
					<td><a href="#" onclick="memberAuthorizationSearch('${creditUserInfo.id}');">查询</a></td>
				</shiro:hasPermission>
				<td><a href="${ctx}/credit/annexFile/list?otherId=${creditUserInfo.id}">查看</a></td>
				<td><a href="${ctx}/credit/userinfo/creditUserInfo/creditUserZtmgLoanBasicInfo?id=${creditUserInfo.id}">查看</a></td>
				<%-- <td><a href="${ctx}/credit/creditinfo/creditInfo/list?creditUserId=${creditUserInfo.id}">查看</a></td> --%>
				<%-- <td><a href="${ctx}/credit/userinfo/creditUserInfo/form?id=${creditUserInfo.id}">修改</a></td> --%>
				<td><a href="${ctx}/credit/userinfo/creditUserInfo/deleteSupplierBank?id=${creditUserInfo.id}" onclick="if(confirm('确定销户?')==false)return false;">销户</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>