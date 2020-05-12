<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>项目还款详情</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" >
		$(document).ready(function() {
			$("#btnRepayPlanInfoExport").click(function(){
				top.$.jBox.confirm("确认要导出还款计划【还款成功】数据吗？","系统提示",function(v,h,f){
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
		<li><a href="${ctx}/wloanproject/wloanTermProject/">定期项目信息列表</a></li>
		<shiro:hasPermission name="wloanproject:wloanTermProject:edit"><li><a href="${ctx}/wloanproject/wloanTermProject/form">定期项目信息添加</a></li></shiro:hasPermission>
		<li><a href="${ctx}/wloanproject/wloanTermProjectPlan/proid?proid=${proid}">项目还款计划</a></li>
		<li class="active"><a href="${ctx}/wloanproject/wloanTermProjectPlan/fromWloanTermProjectPlanDetail?projectPlanId=${wloanTermProjectPlan.id}&proid=${proid}">项目还款详情</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="wloanTermUserPlan" action="#" method="post" class="breadcrumb form-search">
			<input id="projectPlanId" name="projectPlanId" type="hidden" value="${wloanTermProjectPlan.id}" />
			<label>项目名称：</label><span class="help-inline">${wloanTermProject.name}</span>
			<label>还款类型：</label>
			<c:choose>
				<c:when test="${wloanTermProjectPlan.principal==1}">
					<span class="help-inline">本息</span>
				</c:when>
				<c:otherwise>
					<span class="help-inline">利息</span>
				</c:otherwise>
			</c:choose>
			<label>还款金额：</label><span class="help-inline">${wloanTermProjectPlan.interest}</span>
			<label>还款日期：</label><span class="help-inline"><fmt:formatDate value="${wloanTermProjectPlan.repaymentDate}" pattern="yyyy-MM-dd"/></span>
			<label></label>
			<input id="btnRepayPlanInfoExport" class="btn btn-primary" type="button" value="导出" />	
	</form:form>
	
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>项目名称</th>
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
					<c:if test="${wloanTermUserPlan.state==2}">
						未还款
					</c:if>
					<c:if test="${wloanTermUserPlan.state==3}">
						<label style="color:green ">还款成功</label>
					</c:if>
					<c:if test="${wloanTermUserPlan.state==4}">
						<label style="color:red">还款失败</label>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<div class="pagination">${page}</div>
	<div align="right">
			<c:choose>
				<c:when test="${viewType!=1}">
					<shiro:hasPermission name="wloanproject:wloanTermProjectPlan:edit">
						<input id="btnSubmit" class="btn btn-primary" type="button" value="还款" onclick="returnWloanTermProjectPlan('${wloanTermProjectPlan.id}','${repaymentDate}','${proid}')" />&nbsp;&nbsp;&nbsp;
					</shiro:hasPermission>
				</c:when>
				<c:otherwise>
						<input id="btnCancel" class="btn btn-primary" type="button" value="返 回" onclick="history.go(-1)"/>
				</c:otherwise>
			</c:choose> 
			
	</div>
	 <script type="text/javascript">
    	function returnWloanTermProjectPlan(projectPlanId,repaymentDate,proid){		
    		$('#btnSubmit').attr("disabled","disabled");		
    		repaymentDate = repaymentDate.replace(/-/g,"/");
    		var repaydate = new Date(repaymentDate );
    		
    		// 还款可以换今天以后7天内的还款
    		var now = new Date();
    		now = now.valueOf();
    		now = now + 7 * 24 * 60 * 60 * 1000;
    		now = new Date(now);
    		
    		if(repaydate > now){
    			alert("该项目未到还款日期，不能还款");
    			return;
    		}
    		
    		var ips = "127.0.0.1".substring(0, 9);
    		// location.href="${ctx}/cgb/p2p/trade/bid/repay?projectPlanId="+projectPlanId+"&repaymentDate="+repaymentDate+"&ip="+ips+"&proid="+proid;
			location.href="${ctx}/wloanproject/wloanTermProjectPlan/returnWloanTermProjectPlan?projectPlanId="+projectPlanId+"&repaymentDate="+repaymentDate+"&ip="+ips+"&proid="+proid;		
    	}
    </script>
</body>
</html>