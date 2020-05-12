<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分配角色</title>
	<meta name="decorator" content="blank"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<script type="text/javascript">
	
		var questionnaireTree; // 问卷列表对象.
		var selectedTree;//zTree已选择对象
		
		// 初始化
		$(document).ready(function(){
			questionnaireTree = $.fn.zTree.init($("#questionnaireTree"), setting, questionnaireNodes);
			selectedTree = $.fn.zTree.init($("#selectedTree"), setting, selectedNodes);
		});

		var setting = {view: {selectedMulti:false,nameIsHTML:true,showTitle:false,dblClickExpand:false},
				data: {simpleData: {enable: true}},
				callback: {onClick: treeOnClick}};
		
		var questionnaireNodes=[
	            {
	            	id:"${questionnaire.id}",
	             	name:"${questionnaire.name}"
	            }
	    ];
	
		var pre_selectedNodes =[
   		        <c:forEach items="${topicList}" var="topic">
   		        {id:"${topic.id}",
   		         pId:"0",
   		         name:"<font color='red' style='font-weight:bold;'>${topic.name}</font>"},
   		        </c:forEach>];
		
		var selectedNodes =[
		        <c:forEach items="${topicList}" var="topic">
		        {id:"${topic.id}",
		         pId:"0",
		         name:"<font color='red' style='font-weight:bold;'>${topic.name}</font>"},
		        </c:forEach>];
		
		var pre_ids = "${selectIds}".split(",");
		var ids = "${selectIds}".split(",");
		
		//点击选择项回调
		function treeOnClick(event, treeId, treeNode, clickFlag){
			$.fn.zTree.getZTreeObj(treeId).expandNode(treeNode);
			if("questionnaireTree"==treeId){
				$.get("${ctx}/questionnaire/topic/topicTree?questionnaireId=" + treeNode.id, function(topicNodes){
					$.fn.zTree.init($("#topicTree"), setting, topicNodes);
				});
			}
			if("topicTree"==treeId){
				//alert(treeNode.id + " | " + ids);
				//alert(typeof ids[0] + " | " +  typeof treeNode.id);
				if($.inArray(String(treeNode.id), ids)<0){
					selectedTree.addNodes(null, treeNode);
					ids.push(String(treeNode.id));
				}
			};
			if("selectedTree"==treeId){
				if($.inArray(String(treeNode.id), pre_ids)<0){
					selectedTree.removeNode(treeNode);
					ids.splice($.inArray(String(treeNode.id), ids), 1);
				}else{
					top.$.jBox.tip("问卷原有题目不能清除！", 'info');
				}
			}
		};
		function clearAssign(){
			var submit = function (v, h, f) {
			    if (v == 'ok'){
					var tips="";
					if(pre_ids.sort().toString() == ids.sort().toString()){
						tips = "未给问卷【${questionnaire.name}】分配新新题目！";
					}else{
						tips = "已选题目清除成功！";
					}
					ids=pre_ids.slice(0);
					selectedNodes=pre_selectedNodes;
					$.fn.zTree.init($("#selectedTree"), setting, selectedNodes);
			    	top.$.jBox.tip(tips, 'info');
			    } else if (v == 'cancel'){
			    	// 取消
			    	top.$.jBox.tip("取消清除操作！", 'info');
			    }
			    return true;
			};
			tips="确定清除问卷【${questionnaire.name}】下的已选题目？";
			top.$.jBox.confirm(tips, "清除确认", submit);
		};
	</script>
</head>
<body>
	<div id="assignRole" class="row-fluid span12">
		<div class="span4" style="border-right: 1px solid #A8A8A8;">
			<p>当前问卷：</p>
			<div id="questionnaireTree" class="ztree"></div>
		</div>
		<div class="span3">
			<p>待选题目：</p>
			<div id="topicTree" class="ztree"></div>
		</div>
		<div class="span3" style="padding-left:16px;border-left: 1px solid #A8A8A8;">
			<p>已选题目：</p>
			<div id="selectedTree" class="ztree"></div>
		</div>
	</div>
</body>
</html>
