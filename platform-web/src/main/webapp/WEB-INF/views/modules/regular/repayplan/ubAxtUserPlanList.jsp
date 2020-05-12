<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>UB-还款计划【客户还款计划】</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" >
		$(document).ready(function() {
			//
			$("#messageBox").show();
			//
			$("#btnRepayPlanInfoExport").click(function(){
				top.$.jBox.confirm("确定要执行【导出】操作吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/wloanproject/wloanTermProjectPlan/exportRepayPlanInfo");
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
		<li><a href="${ctx}/wloanproject/wloanTermProjectPlan/findAxtByProId?proid=${proid}">安心投还款【P-A】</a></li>
		<li class="active"><a href="${ctx}/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=${wloanTermProjectPlan.id}&proid=${proid}">安心投还款【C-ALL】</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermUserPlan" action="#" method="post" class="breadcrumb form-search">
		<input id="projectPlanId" name="projectPlanId" type="hidden" value="${wloanTermProjectPlan.id}" />
		<ul class="ul-form">
			<li><label>项目名称：</label><span class="help-inline">${wloanTermProject.name}</span></li>
			<li>
				<label>还款类型：</label>
				<c:choose>
					<c:when test="${wloanTermProjectPlan.principal==1}">
						<span class="help-inline">本息</span>
					</c:when>
					<c:otherwise>
						<span class="help-inline">利息</span>
					</c:otherwise>
				</c:choose>
			</li>
			<li><label>还款金额：</label><span class="help-inline">${wloanTermProjectPlan.interest}</span></li>
			<li><label>还款日期：</label><span class="help-inline"><fmt:formatDate value="${wloanTermProjectPlan.repaymentDate}" pattern="yyyy-MM-dd"/></span></li>
			<li class="btns">
				<input id="btnRepayPlanInfoExport" class="btn btn-primary" type="button" value="导出" />
				<input id="btnCancel" class="btn btn-primary" type="button" value="返 回" onclick="history.go(-1)"/>				
			</li>
			<li class="clearfix"></li>
		</ul>
		<label class="label">【C-ALL】：全部客户的还款计划。</label>
	</form:form>
	
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目名称</th>
				<th>项目编号</th>
				<c:if test="${fns:getUser().loginName == 'admin'}">	
				<th>客户姓名</th>
				</c:if>
				<th>投资用户</th>
				<th>还款金额</th>
				<th>还款类型</th>
				<th>还款日期</th>
				<th>还款状态</th>				
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${wloanTermUserPlanList}" var="wloanTermUserPlan">
			<tr>
				<td>
					${wloanTermUserPlan.wloanTermProject.name}
				</td>
				<td>
					${wloanTermUserPlan.wloanTermProject.sn}
				</td>
				<c:if test="${fns:getUser().loginName == 'admin'}">			
					<td>
						${wloanTermUserPlan.userInfo.realName }
					</td>
				</c:if>
				<td>
					${wloanTermUserPlan.userInfo.name }
				</td>
				<td>
					${wloanTermUserPlan.interest}
				</td>
				<td>
					${wloanTermUserPlan.principal == '2' ? '利息' : '本息'}
				</td>
				<td>
					<fmt:formatDate value="${wloanTermUserPlan.repaymentDate}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					<c:if test="${wloanTermUserPlan.state == '2'}">
						<b>未还款</b>
					</c:if>
					<c:if test="${wloanTermUserPlan.state == '3'}">
						<b style="color:green;">还款成功</b>
					</c:if>
					<c:if test="${wloanTermUserPlan.state == '4'}">
						<b style="color:red;">还款失败</b>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<div class="pagination">${page}</div>
	<div align="right">
		<c:choose>
			<c:when test="${viewType != 1}">
				<shiro:hasPermission name="wloanproject:wloanTermProjectPlan:edit">
					<!-- 是否代偿还款. -->
					<c:if test="${wloanTermProject.isReplaceRepay == '0'}">
						<input id="btnSubmit" class="btn btn-primary" type="button" value="自动授权预处理，还款确认" onclick="returnWloanTermProjectPlan('${wloanTermProjectPlan.id}','${repaymentDate}','${proid}','${wloanTermProject.isReplaceRepay}')" />&nbsp;&nbsp;&nbsp;
						<%-- <input id="btnSubmit" class="btn btn-primary" type="button" value="安心投还款" onclick="returnWloanTermProjectPlan('${wloanTermProjectPlan.id}','${repaymentDate}','${proid}','${wloanTermProject.isReplaceRepay}')" />&nbsp;&nbsp;&nbsp; --%>
					</c:if>
					<c:if test="${wloanTermProject.isReplaceRepay == '1'}">
						<input id="btnSubmit" class="btn btn-primary" type="button" value="供应链还款" onclick="returnWloanTermProjectPlan('${wloanTermProjectPlan.id}','${repaymentDate}','${proid}','${wloanTermProject.isReplaceRepay}')" />&nbsp;&nbsp;&nbsp;
					</c:if>
				</shiro:hasPermission>
			</c:when>
			<c:otherwise>
				<input id="btnCancel" class="btn btn-primary" type="button" value="返 回" onclick="history.go(-1)"/>
			</c:otherwise>
		</c:choose> 
	</div>
	 <script type="text/javascript">
    	function returnWloanTermProjectPlan(projectPlanId, repaymentDate, proid, isReplaceRepay){		
    		$('#btnSubmit').attr("disabled","disabled");		
    		repaymentDate = repaymentDate.replace(/-/g,"/");
    		var repaydate = new Date(repaymentDate); // 还款日期.
    		var now = new Date(); // 当前日期.
    		if(now >= repaydate){
    			var ips = "127.0.0.1".substring(0, 9); // ip.
        		if(isReplaceRepay == 0){ // 安心投还款.
	        		confirmx("确认要执行【还款】操作吗？", "${ctx}/lm/p2p/repayment/repayment?projectPlanId="+projectPlanId+"&repaymentDate="+repaymentDate+"&ip="+ips+"&proid="+proid+"&type=1");
        		} else if (isReplaceRepay == 1) { // 供应链还款.
        			/* confirmx("确认要执行【供应链还款】操作吗？", "${ctx}/cgb/p2p/trade/bid/replaceRepay?projectPlanId="+projectPlanId+"&repaymentDate="+repaymentDate+"&ip="+ips+"&proid="+proid+"&type=1"); */
        		}
    		} else {
    			alertx("未到还款日，不能执行【还款】操作！");
    			return;
    			// console.log("该批次还款未到还款日，不能还款");
    		}
    	}
    </script>
</body>
</html>