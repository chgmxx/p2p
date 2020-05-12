<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>用户信息管理管理</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
	<style type="text/css">
		.mask_investNo_tip {
			position: fixed;
			top: 50%;
			left: 50%;
			width: 600px;
			text-align: center;
			padding: 15px 30px;
			color: #fff;
			background: rgba(0, 0, 0, 0.8);
			transform: translate(-50%, -50%);
			-webkit-transform: translate(-50%, -50%);
			z-index: 2;
			font-size: 20px;
			line-height: 1.4;
			display: none;
			border-radius: 10px;
		}
	</style>
	<!--  -->
	<script type="text/javascript">

		// 核心企业ID.
		var creditUserId = "${creditUserId}";
		// 供应商ID.
		var creditSupplyId = "${creditSupplyId}";
		$(document).ready(function() {
			
			var message = "${message}";
			if(message != ""){
				message_prompt("${message}");
			}

			$("#btnUserInfoExport").click(function(){
				var r = confirm("确定要执行导出操作吗？")
				if (r) {
					if(creditSupplyId != ""){
						$("#searchForm").attr("action", "${ctx}/apply/creditUserApply/downloadProjectPlan?creditSupplyId=" + creditSupplyId);
						$("#searchForm").submit();
					} else if (creditUserId != "") {
						$("#searchForm").attr("action", "${ctx}/apply/creditUserApply/downloadProjectPlan?creditUserId=" + creditUserId);
						$("#searchForm").submit();
					}
				}
			});

		});

		function page(n, s) {
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}

		// 消息提示 .
		function message_prompt(message) {
			$(".mask_investNo_tip").html(message);
			$(".mask_investNo_tip").show();
			setTimeout(function() {
				$(".mask_investNo_tip").hide();
			}, 2000);
		} // --
	</script>
</head>
<body>

	<div class="nav_head">导出还款计划</div>
	<form:form id="searchForm" modelAttribute="WloanTermProjectPlan" action="${ctx}/apply/creditUserApply/downloadProjectPlan?creditUserId=${creditUserId}" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li><label>开始日期：</label>
				<input name="beginRepaymentDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="开始日期"
					value="<fmt:formatDate value="${wloanTermProjectPlan.beginRepaymentDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li><label>结束日期：</label>
				<input name="endRepaymentDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" placeholder="结束日期"
					value="<fmt:formatDate value="${wloanTermProjectPlan.endRepaymentDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li class="btns">
				<input id="btnUserInfoExport" class="btn btn-primary" type="button" value="导出" />
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>

	<div class="mask_investNo_tip"></div>
</body>
</html>