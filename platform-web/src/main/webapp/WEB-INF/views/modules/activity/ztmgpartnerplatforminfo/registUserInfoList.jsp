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
						$("#searchForm").attr("action","${ctx}/userinfo/userInfo/exportUserInfo");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/partner/ztmgPartnerPlatform/list2">渠道用户信息列表</a></li>
		<li class="active"><a href="${ctx}/partner/ztmgPartnerPlatform/listForRegist">注册用户信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="ztmgPartnerPlatform" action="${ctx}/partner/ztmgPartnerPlatform/listForRegist?id=${ztmgPartnerPlatform.id }" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
<%-- 		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>姓名：</label>
				<form:input path="realName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>证件号码：</label>
				<form:input path="certificateNo" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
		</ul> --%>
	<%-- 	<ul class="ul-form">
			<li>
				<label>性别：</label>
				<form:select path="sex" style="width:177px">
					<form:option value="" label="请选择" />
					<form:option value="1" label="男" />
					<form:option value="2" label="女" />
				</form:select>
			</li>
			<li><label>注册日期：</label>
				<input name="beginRegisterDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="开始日期"
					value="<fmt:formatDate value="${userInfo.beginRegisterDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>至：</label>
				<input name="endRegisterDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="结束日期"
					value="<fmt:formatDate value="${userInfo.endRegisterDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns">
				<label></label>
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<input id="btnUserInfoExport" class="btn btn-primary" type="button" value="导出" />
			</li>
			<li class="clearfix"></li>
		</ul> --%>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号码</th>
				<th>姓名</th>
				<th>证件号码</th>
				<th>状态</th>
				<th>注册日期</th>
				<th>注册来源</th>
				<th>最后登录日期</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userInfo">
			<tr>
				<td><a href="${ctx}/userinfo/userInfo/form?id=${userInfo.id}">
					${userInfo.name}
				</a></td>
				<td>
					${userInfo.realName}
				</td>
				<td>
					${userInfo.certificateNo}
				</td>
				<td>
					${fns:getDictLabel(userInfo.state, 'user_state', '')}
				</td>
				<td>
					<fmt:formatDate value="${userInfo.registerDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(userInfo.registerFrom, 'user_register_from', '')}
				</td>
				<td>
					<fmt:formatDate value="${userInfo.lastLoginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>