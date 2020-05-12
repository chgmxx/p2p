<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>借款申请管理</title>
<meta name="decorator" content="default" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/reset.css" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
<style>
.apply_tit h1{    padding-left: 10px;
    font-size: 24px;
    color:#333;
    font-weight:normal;
}
.table-condensed th, .table-condensed td {
  padding: 12px 5px!important;
    text-align: center!important;
}
.breadcrumb{
  padding:0;
}
.table-bordered>thead>tr>th, .table-bordered>thead>tr>td{
  border-bottom:0;
}
.apply_tit{
padding-right:10px
}
.table-bordered>thead>tr>th, .table-bordered>tbody>tr>th, .table-bordered>tfoot>tr>th, .table-bordered>thead>tr>td, .table-bordered>tbody>tr>td, .table-bordered>tfoot>tr>td{
border-bottom: 0;
border-left: 0;
}
.form-search .ul-form li label{
    color: #666;
    font-weight: normal;
}
.ul-form li:nth-of-type(1) input{
    padding: 6px;
    box-sizing: border-box;
    height: 28px;
}
.ul-form li:nth-of-type(2) input{
    padding: 6px;
    width: 233px;
    box-sizing: border-box;
    height: 32px;
}
.modal.fade.in {
    top: 10%;
    left: 50%;
    z-index: 99999;
    border: 0;
    background: transparent;
    -webkit-box-shadow: 0 3px 7px rgba(0,0,0,0);
    -moz-box-shadow: 0 3px 7px rgba(0,0,0,0);
    box-shadow: 0 3px 7px rgba(0,0,0,0);
}
.recharge_input input {
    width: 300px;
    font-size: 14px;
    margin-left: 20px;
    height: 34px;
}.btn-primary,.btn-primary:hover {
    color: #fff;
    background-color: #40a2fb;
    border-color: #40a2fb;
}
</style>
<script type="text/javascript">
var error = "${error}";
var limit = "${limit}";//1,核心企业 2，供应商
	$(document).ready(function() {
		$("#messageBox").show();
		if(error!=""){
			alert(error);
		}
		$("#saveVoucherInfo").click(function(){
			var id = "${creditVoucherInfo.id}";
			var userId = "${userInfo.id}";
			var phone = $("#phone").val();
			var bankName = $("#bankName").val();
			var bankNo = $("#bankNo").val();
			var toName = $("#toName").val();
			var toPhone = $("#toPhone").val();
			var toAddr = $("#toAddr").val();
			$.ajax({
				url:"${ctx}/sys/user/creditVoucherInfo",
				type: 'post',
				dataType: "json",
				data: {
					id:id,
					userId:userId,
					phone:phone,
					bankName: bankName,
					bankNo: bankNo,
					toName: toName,
					toPhone: toPhone,
					toAddr: toAddr
				},
				success: function(result) {
					if(result.state == "0") {
						$('#myModal').modal('hide');
					} 
				},
				error:function(result){
					$('#myModal').modal('hide');
				}
			});
		});

		
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
	<div class="nav_head">申请列表 
		<c:if test="${userInfo.creditUserType == '11'}">
			<a href="${ctx}/apply/creditUserApply/applyMoney1?creditUserId=${id}">
				<span class="pull-right" >申请借款</span>
			</a>
		</c:if>
		<!-- 借款户（供应商），申请借款页面. -->
		<%-- <c:if test="${userInfo.creditUserType == '02'}">
			<a href="${ctx}/apply/creditUserApply/applyMoney1?creditSupplyId=${id}">
				<span class="pull-right" >申请借款</span>
			</a>
		</c:if> --%>
		<!-- 借款户（房产抵押），申请借款页面. -->
		<%-- <c:if test="${userInfo.creditUserType == '15'}">
			<a href="${ctx}/apply/creditUserApply/checkLoanApply?creditSupplyId=${id}">
				<span class="pull-right" >申请借款</span>
			</a>
		</c:if> --%>
		<a href="javaScript:void(0)" data-target="#myModal" data-toggle="modal">
			<c:if test="${userInfo.creditUserType == '11'}">
				<span class="pull-right" >发票信息</span>
			</c:if>
		</a>
	</div>
	<form:form id="searchForm" modelAttribute="creditUserApply" action="${ctx}/sys/user/project?replaceUserId=${id}" method="post" class="breadcrumb form-search">
	<c:if test="${userInfo.creditUserType!='02'}">
		<ul class="ul-form">
			<li><label>申请编号：</label>
				<form:input path="creditApplyName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>融资方：</label>
				<form:input path="loanUserEnterpriseFullName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</c:if>
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>申请编号</th>
				<th>融资类型</th>
				<th>还款方</th>
				<th>融资方</th>
				<th>融资金额(元)</th>
				<th>状态</th>
				<th>操作</th>
				<th>发票申请</th>
				
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="creditUserApply">
				<tr>
					<td><a href="${ctx}/apply/creditUserApply/form?id=${creditUserApply.id}">${creditUserApply.creditApplyName}</a></td>
					<td>
						<c:if test="${creditUserApply.financingType == '1' }">
							应收账款质押
						</c:if>
						<c:if test="${creditUserApply.financingType == '2' }">
							订单融资
						</c:if>
						
					</td>
					<td>
						<c:if test="${creditUserApply.replaceUserEnterpriseFullName == null}">
							暂无
						</c:if>
						<c:if test="${creditUserApply.replaceUserEnterpriseFullName != null}">
							${creditUserApply.replaceUserEnterpriseFullName}
						</c:if>
					</td>
					<td>
						<c:if test="${creditUserApply.loanUserEnterpriseFullName == '' || creditUserApply.loanUserEnterpriseFullName==null}">
							暂无
						</c:if>
						<c:if test="${creditUserApply.loanUserEnterpriseFullName != '' || creditUserApply.loanUserEnterpriseFullName==null}">
							${creditUserApply.loanUserEnterpriseFullName}
						</c:if>
					</td>
					<td>
						<c:if test="${creditUserApply.amount == '' || creditUserApply.amount==null}">
							暂无
						</c:if>
						<c:if test="${creditUserApply.amount != '' and creditUserApply.amount!=null}">
							${creditUserApply.amount}
						</c:if>
					</td>
					<td>
						<c:if test="${creditUserApply.state == '0' }">
							<b>草稿</b>
						</c:if>
						<c:if test="${creditUserApply.state == '1' }">
							<b>审核中</b>
						</c:if>
						<c:if test="${creditUserApply.state == '2' }">
							<b>审核通过</b>
						</c:if>
						<c:if test="${creditUserApply.state == '3' }">
							<b>审核驳回</b>
						</c:if>
						<c:if test="${creditUserApply.state == '4' }">
							<b>融资中</b>
						</c:if>
						<c:if test="${creditUserApply.state == '5' }">
							<b>还款中</b>
						</c:if>
						<c:if test="${creditUserApply.state == '6' }">
							<b>结束</b>
						</c:if>
					</td>
					<td>
						<!-- 供应商第5步进行授权 -->
						<c:if test="${userInfo.creditUserType == '02' and creditUserApply.financingStep == '5'}">
							<a href="${ctx}/apply/creditUserApply/applyMoney${creditUserApply.financingStep}?id=${creditUserApply.id}&step=${creditUserApply.financingStep}&creditUserType=02">授权</a>
						</c:if>
						<!-- 供应商大于第5步进行查看 -->
						<c:if test="${userInfo.creditUserType == '02' and creditUserApply.financingStep > '5'}">
							<a href="${ctx}/apply/creditUserApply/form?id=${creditUserApply.id}">查看</a>
						</c:if>
						<!-- 核心企业查看 -->
						<c:if test="${userInfo.creditUserType == '11'}">
							<a href="${ctx}/apply/creditUserApply/form?id=${creditUserApply.id}">查看</a>
						</c:if>
						<c:if test="${creditUserApply.financingType == '1' and creditUserApply.financingStep<6 and userInfo.creditUserType == '11'}">
							<!-- 正常申请 -->
							<c:if test="${creditUserApply.state == '0' }">
								<a href="${ctx}/apply/creditUserApply/applyMoney${creditUserApply.financingStep}?id=${creditUserApply.id}&step=${creditUserApply.financingStep}&creditUserType=11">编辑</a>
								<a href="${ctx}/apply/creditUserApply/deleteApply?id=${creditUserApply.id}&limit=${limit}" onclick="if(confirm('确认删除此条草稿记录')==false)return false;">删除</a>
							</c:if>
							<!-- 申请驳回 -->
							<c:if test="${creditUserApply.state == '3' }">
								<a href="${ctx}/apply/creditUserApply/applyMoney3?id=${creditUserApply.id}&step=3&creditUserType=11">编辑</a>
							</c:if>
							
						</c:if>
						<c:if test="${creditUserApply.financingType == '2' and creditUserApply.financingStep<7}">
							<!-- 正常申请 -->
							<c:if test="${creditUserApply.state == '0' }">
								<c:if test="${creditUserApply.financingStep == '4' and userInfo.creditUserType=='02' and creditUserApply.fileConfirm=='1' }">
									<a href="${ctx}/apply/orderApply/applyMoney5?id=${creditUserApply.id}&step=5&creditUserType=02">编辑</a>
								</c:if>
								<c:if test="${creditUserApply.financingStep == '5' and userInfo.creditUserType=='02' and creditUserApply.fileConfirm=='1' }">
									<a href="${ctx}/apply/orderApply/applyMoney5?id=${creditUserApply.id}&step=5&creditUserType=02">编辑</a>
								</c:if>
								<c:if test="${userInfo.creditUserType=='11' }">
									<c:if test="${creditUserApply.financingStep!='7' and creditUserApply.modify!='1' }">
										<a href="${ctx}/apply/orderApply/applyMoney${creditUserApply.financingStep}?id=${creditUserApply.id}&step=${creditUserApply.financingStep}&creditUserType=11">编辑</a>
									</c:if>
									<c:if test="${creditUserApply.modify=='1' }">
										<a href="${ctx}/apply/orderApply/applyMoney6?id=${creditUserApply.id}&step=6&creditUserType=11">编辑</a>
									</c:if>
								</c:if>
								<a href="${ctx}/apply/creditUserApply/deleteApply?id=${creditUserApply.id}&limit=${limit}" onclick="if(confirm('确认删除此条草稿记录')==false)return false;">删除</a>
							</c:if>
							<!-- 申请驳回 -->
							<c:if test="${creditUserApply.state == '3' }">
								<a href="${ctx}/apply/orderApply/applyMoney3?id=${creditUserApply.id}&step=3">编辑</a>
							</c:if>
							
						</c:if>
						
					</td>
					<td>
						<c:if test="${creditUserApply.voucherState == null or creditUserApply.voucherState == ''}">
							<c:if test="${ userInfo.creditUserType=='11' and creditUserApply.state>=4}">
								<a href="${ctx}/sys/user/creditVoucherApply?id=${creditUserApply.id}" id="applyVoucher">申请</a>
							</c:if>
						</c:if>
						<c:if test="${creditUserApply.voucherState == '1' }">
							<b>申请中</b>
						</c:if>
						<c:if test="${creditUserApply.voucherState == '2' }">
							<b>已开票</b>
						</c:if>
						<c:if test="${creditUserApply.voucherState == '3' }">
							<b>已过期</b>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
		<!--modal rechrge-->
	<div class="modal fade modal_recharge" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title" id="myModalLabel">发票信息</h4>
				</div>
				<div class="modal-body recharge_msg">
					<form class="form-horizontal">
						<div>
							<b style="color: red">温馨提示：请在服务费支付完成后的一个月内进行发票申请，三个月后不进行发票申请将过期。</b>
						</div>
						<div class="form-group">
							<label class="control-label pull-left">*抬头</label>
							<div class="recharge_input pull-left">
								<input type="text" class="form-control" value="${wloanSubject.companyName }" id="title" disabled="disabled" />
							</div>
						</div>
						<div class="form-group">
							<label class="control-label pull-left">*税号</label>
							<div class="recharge_input pull-left">
								<input type="text" class="form-control" value="${wloanSubject.businessNo }" id="number" disabled="disabled"/>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label pull-left">*地址</label>
							<div class="recharge_input pull-left">
								<input type="text" class="form-control" value="${wloanSubject.registAddress }" id="addr" disabled="disabled"/>
							</div>
						</div>						
						<div class="form-group">
							<label class="control-label pull-left">*电话</label>
							<div class="recharge_input pull-left">
								<input type="number" class="form-control" value="${creditVoucherInfo.phone }" id="phone" >
							</div>
						</div>
						<div class="form-group">
							<label class="control-label pull-left">*开户行</label>
							<div class="recharge_input pull-left">
								<input type="text" class="form-control" value="${creditVoucherInfo.bankName }" id="bankName">
							</div>
						</div>
						<div class="form-group">
							<label class="control-label pull-left">*开户账号</label>
							<div class="recharge_input pull-left">
								<input type="text" class="form-control" value="${creditVoucherInfo.bankNo }" id="bankNo">
							</div>
						</div>
						<div class="form-group">
							<label class="control-label pull-left">*收件人姓名</label>
							<div class="recharge_input pull-left">
								<input type="text" class="form-control" value="${creditVoucherInfo.toName }" id="toName">
							</div>
						</div>
						<div class="form-group">
							<label class="control-label pull-left">*收件人电话</label>
							<div class="recharge_input pull-left">
								<input type="number" class="form-control" value="${creditVoucherInfo.toPhone }" id="toPhone">
							</div>
						</div>
						<div class="form-group">
							<label class="control-label pull-left">*收件人地址</label>
							<div class="recharge_input pull-left">
								<input type="text" class="form-control" value="${creditVoucherInfo.toAddr }" id="toAddr">
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary recharge_btn" id="saveVoucherInfo">保存</button>
				</div>
			</div>
		</div>
	</div>
</body>
<script>
/*关闭动态模态框*/
$(".recharge_btn").click(function(){
	$('#myModal').modal('hide');
});

</script>
</html>