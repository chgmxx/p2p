<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>平台商户对账文件管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			// --.
			$("#messageBox").show();
			
			// --.
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"仅允许导入“txt”格式的存管方充值对账文件！"});
			});
			
			// --.
			$("#btn_check_t_1_file").click(function(){
				$.jBox($("#check_t_1_file_box").html(), {title:"充值对账", buttons:{"关闭":true}, 
					bottomText:"请点击【t-1日对账】按钮以核对充值文件数据！"});
			});
			
			// --.
			$("#btn_check_t_t_file").click(function(){
				$.jBox($("#check_t_t_file_box").html(), {title:"充值对账", buttons:{"关闭":true}, 
					bottomText:"请点击【时间区间对账】按钮以核对充值文件数据！"});
			});
		});
		
		// --.
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/bill/merchantRecharge/import" method="post" enctype="multipart/form-data"
			class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="导入"/>
		</form>
	</div>
	<!-- [t-1日]对账 -->
	<div id="check_t_1_file_box" class="hide">
		<form id="check_t_1_file_form" action="${ctx}/bill/merchantRecharge/check_t_1_file" class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在对账，请稍等...');"><br/>
			<input id="btn_check_t_1_file_submit" class="btn btn-primary" type="submit" value="t-1日对账"/>
		</form>
	</div>
	<!-- [时间区间]对账. -->
	<div id="check_t_t_file_box" class="hide">
		<form id="check_t_t_file_form" action="${ctx}/bill/merchantRecharge/check_t_t_file" class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在对账，请稍等...');"><br/>
			<ul class="ul-form">
				<label class="label">开始时间：</label>
				<input name="beginCompletionTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
				value="<fmt:formatDate value="${merchantRecharge.beginCompletionTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				<label class="label">结束时间：</label>
				<input name="endCompletionTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
				value="<fmt:formatDate value="${merchantRecharge.endCompletionTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>	
			</ul>
			<input id="btn_check_t_t_file_submit" class="btn btn-primary" type="submit" value="时间区间对账"/>
		</form>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/bill/merchantRecharge/">充值文件</a></li>
		<!-- 
		<shiro:hasPermission name="bill:merchantRecharge:edit"><li><a href="${ctx}/bill/merchantRecharge/form">平台商户对账文件添加</a></li></shiro:hasPermission>
		 -->
	</ul>
	<form:form id="searchForm" modelAttribute="merchantRecharge" action="${ctx}/bill/merchantRecharge/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label class="label">完成时间：</label>
				<input name="beginCompletionTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${merchantRecharge.beginCompletionTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="endCompletionTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${merchantRecharge.endCompletionTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<input id="btnImport" class="btn btn-primary" type="button" value="导入"/>	
				<input id="btn_check_t_1_file" class="btn btn-primary" type="button" value="t-1日对账"/>	
				<input id="btn_check_t_t_file" class="btn btn-primary" type="button" value="时间区间对账"/>	
			</li>
			<li class="clearfix"></li>
		</ul>
		<label class="label">[t-1日]对账：针对昨（t-1）天的充值文件，进行账单核对。</label>
		<label class="label">[时间区间]对账：针对完成时间的时间区间，进行账单核对（为避免大数据量，导致查询缓慢，请尽量缩小时间区间的范围）。</label>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>商户订单号</th>
				<th>存管订单号</th>
				<th>交易类型</th>
				<th>交易金额</th>
				<th>交易状态</th>
				<th>完成时间</th>
				<th>支付公司代码</th>
				<th>平台用户ID</th>
				<th>业务来源</th>
				<th>备注</th>
				<!-- 
				<shiro:hasPermission name="bill:merchantRecharge:edit"><th>操作</th></shiro:hasPermission>
				 -->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="merchantRecharge">
			<tr>
				<td>
					${merchantRecharge.id}
				</td>
				<td>
					${merchantRecharge.cgbOrderId}
				</td>
				<td>
					<c:if test="${merchantRecharge.tradingType == 1001}">
						<b>网银充值</b>
					</c:if>
					<c:if test="${merchantRecharge.tradingType == 1002}">
						<b>快捷充值</b>
					</c:if>
					<c:if test="${merchantRecharge.tradingType == 1004}">
						<b>线下充值</b>
					</c:if>
					<c:if test="${merchantRecharge.tradingType == 1005}">
						<b>自动充值</b>
					</c:if>
				</td>
				<td>
					${merchantRecharge.tradingAmount}
				</td>
				<td>
					<c:choose>
						<c:when test="${merchantRecharge.tradingStatus == 'S'}">
							<b>成功</b>
						</c:when>
						<c:otherwise>
							<b>失败</b>
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<fmt:formatDate value="${merchantRecharge.completionTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${merchantRecharge.payCode}
				</td>
				<td>
					${merchantRecharge.platformUserId}
				</td>
				<td>
					<c:if test="${merchantRecharge.businessSource == 1}">
						<b>PC</b>
					</c:if>
					<c:if test="${merchantRecharge.businessSource == 2}">
						<b>APP</b>
					</c:if>
				</td>
				<td>
					${merchantRecharge.remarks}
				</td>
				<!-- 
				<shiro:hasPermission name="bill:merchantRecharge:edit"><td>
    				<a href="${ctx}/bill/merchantRecharge/form?id=${merchantRecharge.id}">修改</a>
					<a href="${ctx}/bill/merchantRecharge/delete?id=${merchantRecharge.id}" onclick="return confirmx('确认要删除该平台商户对账文件吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
				 -->
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>