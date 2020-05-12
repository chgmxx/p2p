<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>分配答案</title>
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
		<li><a href="${ctx}/questionnaire/topic/">题目列表</a></li>
		<shiro:hasPermission name="topic:edit">
			<li  class="active"><a href="${ctx}/questionnaire/answer/assign?id=${topic.id}">答案分配</a></li>
		</shiro:hasPermission>
	</ul>
	<div class="container-fluid breadcrumb">
		<div class="row-fluid span12">
			<span class="span4">题目名称: <b>${topicName}</b></span>
		</div>
	</div>
	<sys:message content="${message}" />
	<div class="breadcrumb">
		<form id="assignAnswerForm" action="${ctx}/questionnaire/answer/confirmAnswerAssign" method="post" class="hide">
			<input type="hidden" name="id" value="${topic.id}"/>
			<input id="idsArr" type="hidden" name="idsArr" value=""/>
		</form>
		<input id="assignButton" class="btn btn-primary" type="submit" value="分配答案" />
		<script type="text/javascript">
			$("#assignButton").click(function(){
				top.$.jBox.open("iframe:${ctx}/questionnaire/answer/assignAnswer?id=${topic.id}", "分配答案",810,$(top.document).height()-240,{
					buttons:{"确定分配":"ok", "清除已选":"clear", "关闭":true}, bottomText:"通过选择题目，然后为列出的题目分配答案。",submit:function(v, h, f){
						var pre_ids = h.find("iframe")[0].contentWindow.pre_ids;
						var ids = h.find("iframe")[0].contentWindow.ids;
						//nodes = selectedTree.getSelectedNodes();
						if (v=="ok"){
							// 删除''的元素
							if(ids[0]==''){
								ids.shift();
								pre_ids.shift();
							}
					    	// 执行保存
					    	loading('正在提交，请稍等...');
					    	var idsArr = "";
					    	for (var i = 0; i<ids.length; i++) {
					    		// console.log(ids[i]);
					    		idsArr = (idsArr + ids[i]) + (((i + 1)== ids.length) ? '':',');
					    	}
					    	$('#idsArr').val(idsArr);
					    	$('#assignAnswerForm').submit();
					    	return true;
						} else if (v=="clear"){
							h.find("iframe")[0].contentWindow.clearAssign();
							return false;
		                }
					}, loaded:function(h){
						$(".jbox-content", top.document).css("overflow-y","hidden");
					}
				});
			});
		</script>
	</div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>题目名称</th>
				<th>答案名称</th>
				<th>分值(分)</th>
				<th>创建时间</th>
				<shiro:hasPermission name="questionnaire:answer:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${answerList}" var="answer">
				<tr>
					<td>${answer.topic.name}</td>
					<td>${answer.name}</td>
					<td>${answer.score}</td>
					<td><fmt:formatDate value="${answer.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="questionnaire:answer:edit">
						<td><a href="${ctx}/questionnaire/answer/deleteTopicAnswer?topicId=${answer.topic.id}&answerId=${answer.id}" onclick="return confirmx('确认要移除该答案吗？', this.href)">移除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>