<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>分配题目</title>
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
		<li><a href="${ctx}/questionnaire/">问卷列表</a></li>
		<shiro:hasPermission name="topic:edit">
			<li class="active"><a href="${ctx}/questionnaire/topic/assign?id=${questionnaire.id}">题目分配</a></li>
		</shiro:hasPermission>
	</ul>
	<div class="container-fluid breadcrumb">
		<div class="row-fluid span12">
			<span class="span4">问卷名称: <b>${questionnaireName}</b></span>
		</div>
	</div>
	<sys:message content="${message}" />
	<div class="breadcrumb">
		<form id="assignRoleForm" action="${ctx}/questionnaire/topic/confirmTopicAssign" method="post" class="hide">
			<input type="hidden" name="id" value="${questionnaire.id}"/>
			<input id="idsArr" type="hidden" name="idsArr" value=""/>
		</form>
		<input id="assignButton" class="btn btn-primary" type="submit" value="分配题目" />
		<script type="text/javascript">
			$("#assignButton").click(function(){
				top.$.jBox.open("iframe:${ctx}/questionnaire/topic/assignTopic?id=${questionnaire.id}", "分配题目",810,$(top.document).height()-240,{
					buttons:{"确定分配":"ok", "清除已选":"clear", "关闭":true}, bottomText:"通过选择问卷，然后为列出的问卷分配题目。",submit:function(v, h, f){
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
					    		idsArr = (idsArr + ids[i]) + (((i + 1)== ids.length) ? '':',');
					    	}
					    	$('#idsArr').val(idsArr);
					    	$('#assignRoleForm').submit();
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
				<th>问卷名称</th>
				<th>题目名称</th>
				<th>创建时间</th>
				<shiro:hasPermission name="topic:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${topicList}" var="topic">
				<tr>
					<td>${topic.questionnaire.name}</td>
					<td>${topic.name}</td>
					<td><fmt:formatDate value="${topic.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<shiro:hasPermission name="topic:edit">
						<td><a href="${ctx}/questionnaire/topic/deleteQuestionnaireTopic?questionnaireId=${topic.questionnaire.id}&topicId=${topic.id}" onclick="return confirmx('确认要移除该题目吗？', this.href)">移除</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>