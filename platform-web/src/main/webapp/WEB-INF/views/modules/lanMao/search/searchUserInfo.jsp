<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息查询</title>
	<meta name="decorator" content="default"/>
<script type="text/javascript">
	$(document).ready(function() {
		
	});
</script>
<style type="text/css">
	.select2-container {
		width: 300px;
	}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active">用户信息查询</li>
		
	</ul>
	<form:form id="searchForm" modelAttribute="lanMaoWhiteList" action="${ctx}/lanMao/search/searchUserInfo/" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			
			<li><label class="label">平台用户编号：</label>
				<form:input path="platformUserNo" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix" style="color:red">${message}</li>
		</ul>
	</form:form> 
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>平台用户编号</th>
				<th>用户类型</th>
				<th>用户角色</th>
				<th>审核状态</th>
				<th>用户状态</th>
				<th>账户余额（元）</th>
				<th>可用余额（元）</th>			
			</tr>
		</thead>
		<tbody>
        <tr>
        <td>${result.platformUserNo}</td>    
        <td>${result.userType}</td> 
        <td>${result.userRole}</td> 
        <td>${result.auditStatus}</td> 
        <td>${result.activeStatus}</td> 
        <td>${result.balance}</td> 
        <td>${result.availableAmount}</td>              
        </tr>
        <tr>
        <th>冻结金额（元）</th>
		<th>已到账资金（元）</th>
		<th>在途金额（元）</th>
		<th>绑定的卡号</th>
        <th>银行代码</th>
		<th>手机号</th>
		<th>用户授权</th>
	
		</tr>
		<tr>
		 <td>${result.freezeAmount}</td> 
        <td>${result.arriveBalance}</td> 
        <td>${result.floatBalance}</td> 
        <td>${result.bankcardNo}</td>  
		<td>${result.bankcode}</td> 
        <td>${result.mobile}</td> 
        <td>${result.authlist}</td>         
         <td></td>
          </tr>
         <tr>
        <th>迁移导入会员状态</th>
		<th>鉴权通过类型</th>
		<th>证件类型</th>
		<th>用户证件号</th>
		<th>开户名称</th>
		<th>授权金额</th>
		<th>授权截止期限</th>
         </tr>
         <tr>
         <td>${result.isImportUserActivate}</td> 
         <td>${result.accessType}</td> 
         <td>${result.idCardType}</td> 
         <td>${result.idCardNo}</td> 
         <td>${result.name}</td> 
         <td>${result.amount}</td> 
         <td>${result.failTime}</td>         
         </tr>
         <tr>
	        <th>白名单银行卡号</th>
	        <th></th>
	        <th></th>
	        <th></th>
	        <th></th>
	        <th></th>
	        <th></th>
         </tr>
         <tr>
	         <td>${result.onlineWhiteBankcards}</td> 
	         <td></td>
	         <td></td>
	         <td></td>
	         <td></td>
	         <td></td>
	         <td></td>
         </tr>
		</tbody>
		
	</table>
</body>
</html>