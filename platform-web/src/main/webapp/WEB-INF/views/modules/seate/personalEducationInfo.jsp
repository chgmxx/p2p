<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>学历信息查询</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {

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
		<li class="active"><a href="${ctx}/personal/education/info/form">学历信息</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="seateEntity" action="${ctx}/personal/education/info/query" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li><label>身份证号：</label> <form:input path="idcardNumber" htmlEscape="false" maxlength="32" class="input-medium required" /></li>
			<li><label>姓名：</label> <form:input path="name" htmlEscape="false" maxlength="32" class="input-medium required" /></li>
			<li class="btns"><label></label> <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<br />
	<sys:message content="${message}" />
	<br />
	<ul class="nav nav-tabs form-horizontal">
		<div class="control-group">
			<div class="span6">
				<label class="control-label">结果代码：</label>
				<div class="controls">${result.statcode}</div>
			</div>
			<div class="span6">
				<label class="control-label">结果代码说明：</label>
				<div class="controls">${result.state}</div>
			</div>
		</div>
		<!-- 分割线. -->
		<div class="control-group"></div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">姓名：</label>
				<div class="controls">${result.name}</div>
			</div>
			<div class="span6">
				<label class="control-label">身份证号码：</label>
				<div class="controls">${result.idcardNumber}</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">毕业院校：</label>
				<div class="controls">${result.degree.college}</div>
			</div>
			<div class="span6">
				<label class="control-label">学历层次：</label>
				<div class="controls">${result.degree.degree}</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">毕业时间：</label>
				<div class="controls">${result.degree.graduateTime}</div>
			</div>
			<div class="span6">
				<label class="control-label">是否国家重点学科：</label>
				<div class="controls">${result.degree.isKeySubject}</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">学历证编号：</label>
				<div class="controls">${result.degree.levelNo}</div>
			</div>
			<div class="span6">
				<label class="control-label">毕业照片：</label>
				<div class="controls">${result.degree.photo}</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">毕业照片格式：</label>
				<div class="controls">${result.degree.photoStyle}</div>
			</div>
			<div class="span6">
				<label class="control-label">所学专业：</label>
				<div class="controls">${result.degree.specialty}</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">入学时间：</label>
				<div class="controls">${result.degree.startTime}</div>
			</div>
			<div class="span6">
				<label class="control-label">毕业结论：</label>
				<div class="controls">${result.degree.studyResult}</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">学习形式：</label>
				<div class="controls">${result.degree.studyStyle}</div>
			</div>
			<div class="span6">
				<label class="control-label">学历类别：</label>
				<div class="controls">${result.degree.studyType}</div>
			</div>
		</div>
		<!-- 分割线. -->
		<div class="control-group"></div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">年龄：</label>
				<div class="controls">${result.personBase.age}</div>
			</div>
			<div class="span6">
				<label class="control-label">出生日期：</label>
				<div class="controls">${result.personBase.birthday}</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">性别：</label>
				<c:if test="${result.personBase.gender == '1'}">
					<div class="controls">男</div>
				</c:if>
				<c:if test="${result.personBase.gender == '2'}">
					<div class="controls">女</div>
				</c:if>
				<c:if test="${result.personBase.gender == '3'}">
					<div class="controls">不详</div>
				</c:if>
			</div>
			<div class="span6">
				<label class="control-label">毕业年份：</label>
				<div class="controls">${result.personBase.graduateYears}</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">原始发证地：</label>
				<div class="controls">${result.personBase.originalAddress}</div>
			</div>
			<div class="span6">
				<label class="control-label">评估建议：</label>
				<div class="controls">${result.personBase.riskAndAdviceInfo}</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span6">
				<label class="control-label">校验结果：</label>
				<c:if test="${result.personBase.verifyResult == '1'}">
					<div class="controls">通过</div>
				</c:if>
				<c:if test="${result.personBase.verifyResult == '2'}">
					<div class="controls">未通过</div>
				</c:if>
			</div>
		</div>
	</ul>
</body>
</html>