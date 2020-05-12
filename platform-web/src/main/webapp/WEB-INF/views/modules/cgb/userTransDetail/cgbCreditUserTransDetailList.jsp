<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>银行托管-流水管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//
			$("#messageBox").show();
			// 导出.
			$("#btnExportBorrowersTransDetail").click(function(){
				top.$.jBox.confirm("确认要执行【导出】操作吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#transDetailRadioType").val($('input[name="trans_Detail_type"]:checked').val());
						$("#searchForm").attr("action","${ctx}/cgb/cgbUserTransDetail/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			}); // --.
		});
		
		// 回车键上抬.	
		$(document).keyup(function(event){
			if(event.keyCode ==13){
				page();
			}
		});// --.
		
		// 查询.
		function page(n,s){
			
			// 单选按钮赋值.
			$("#transDetailRadioType").val($('input[name="trans_Detail_type"]:checked').val());
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			// 分页时，重新定义action.
			$("#searchForm").attr("action","${ctx}/cgb/cgbUserTransDetail/");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/cgb/cgbUserTransDetail">流水列表</a></li>
		<!-- 
		<shiro:hasPermission name="cgb:cgbUserTransDetail:edit"><li><a href="${ctx}/cgb/cgbUserTransDetail/form">流水添加</a></li></shiro:hasPermission>
		 -->
	</ul>
	<form:form id="searchForm" modelAttribute="cgbUserTransDetail" action="${ctx}/cgb/cgbUserTransDetail" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label class="label">帐号：</label>
				<form:input path="creditUserInfo.phone" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label class="label">姓名：</label>
				<form:input path="creditUserInfo.name" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label class="label">交易日期：</label>
				<input name="beginTransDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${cgbUserTransDetail.beginTransDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="endTransDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${cgbUserTransDetail.endTransDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label class="label">交易类型：</label>
				<form:select path="trustType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:option value="0" label="充值"/>
					<form:option value="1" label="提现"/>
					<form:option value="2" label="活期投资"/>
					<form:option value="3" label="定期投资"/>
					<form:option value="4" label="付息"/>
					<form:option value="5" label="还本"/>
					<form:option value="6" label="活期赎回"/>
					<form:option value="7" label="活动返现"/>
					<form:option value="8" label="活期收益"/>
					<form:option value="9" label="佣金"/>
					<form:option value="10" label="抵用券"/>
					<form:option value="11" label="放款"/>
					<form:option value="12" label="受托支付提现"/>
					<form:option value="13" label="代偿还款"/>
				</form:select>
			</li>
			<li><label class="label">收支类型：</label>
				<form:select path="inOutType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:option value="1" label="收入"/>
					<form:option value="2" label="支出"/>
				</form:select>
			</li>
			<li class="clearfix"></li>
			<li><label class="label">状态：</label>
				<form:select path="state" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:option value="1" label="处理中"/>
					<form:option value="2" label="成功"/>
					<form:option value="3" label="失败"/>
				</form:select>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();" />
				<input id="btnExportBorrowersTransDetail" class="btn btn-primary" type="button" value="导出" />
			</li>
			<li>
				<form:hidden path="transDetailRadioType"/>
				<label class="btn">出借人~<input id="lenders_id" type="radio" name="trans_Detail_type" value="1" onclick="return page();" /></label>
				<label class="btn">借款人~<input id="borrowers_id" type="radio" name="trans_Detail_type" value="2" checked="checked" onclick="return page();" /></label>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>订单号</th>
				<th>帐号</th>
				<th>姓名</th>
				<th>交易日期</th>
				<th>交易类型</th>
				<th>收支类型</th>
				<th>金额</th>
				<th>可用余额</th>
				<th>状态</th>
				<th>备注</th>
				<!-- 
				<shiro:hasPermission name="cgb:cgbUserTransDetail:edit"><th>操作</th></shiro:hasPermission>
				 -->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="cgbUserTransDetail">
			<tr>
				<td>
					${cgbUserTransDetail.transId}
				</td>
				<td>
					${cgbUserTransDetail.creditUserInfo.phone}
				</td>
				<td>
					${cgbUserTransDetail.creditUserInfo.name}
				</td>
				<td>
					<fmt:formatDate value="${cgbUserTransDetail.transDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<b>${cgbUserTransDetail.trustTypeStr}</b>
				</td>
				<td>
					<c:if test="${cgbUserTransDetail.inOutType == '1'}">
						<b>收入</b>
					</c:if>
					<c:if test="${cgbUserTransDetail.inOutType == '2'}">
						<b>支出</b>
					</c:if>
				</td>
				<td>
					${cgbUserTransDetail.amountStr}
				</td>
				<td>
					${cgbUserTransDetail.avaliableAmountStr}
				</td>
				<td>
					<b>${cgbUserTransDetail.stateStr}</b>
				</td>
				<td>
					<b>${cgbUserTransDetail.remarks}</b>
				</td>
				<!-- 
				<shiro:hasPermission name="cgb:cgbUserTransDetail:edit"><td>
    				<a href="${ctx}/cgb/cgbUserTransDetail/form?id=${cgbUserTransDetail.id}">修改</a>
					<a href="${ctx}/cgb/cgbUserTransDetail/delete?id=${cgbUserTransDetail.id}" onclick="return confirmx('确认要删除该银行托管-流水吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
				 -->
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>