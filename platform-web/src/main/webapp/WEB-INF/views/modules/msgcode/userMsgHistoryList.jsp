<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/usersms/">用户验证码信息</a></li>
		<li ><a href="${ctx}/usersms/rejectList">屏蔽信息</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="smsMsgHistory" action="${ctx}/usersms/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="phone" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><label></label><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号码</th>
				<th>验证码</th>
				<th>发送时间</th>
				<th>发送类别</th>
				<th>内容</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="smsMsgHistory">
			<tr>
				<td>
					${smsMsgHistory.phone}
				</td>
				<td>
					${smsMsgHistory.validateCode}
				</td>
				<td>
					<fmt:formatDate value="${smsMsgHistory.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${smsMsgHistory.type=="1"?"注册":(smsMsgHistory.type=="2"?"找回密码":(smsMsgHistory.type=="3"?"修改登录密码":(smsMsgHistory.type=="4"?"设置交易密码":(smsMsgHistory.type=="5"?"找回交易密码":(smsMsgHistory.type=="6"?"修改交易密码":(smsMsgHistory.type=="7"?"绑卡":"更换手机"))))))}
				</td>
				<td>
					${smsMsgHistory.msgContent}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>