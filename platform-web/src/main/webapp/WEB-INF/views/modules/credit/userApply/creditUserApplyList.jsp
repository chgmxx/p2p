<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>借款申请管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#messageBox").show();

		// 核心企业为空串时，展示请选择.
		if($("#replaceUserId").val() == ""){
			$("span.select2-chosen:eq(2)").text("请选择");
		}
		
	});

	// 回车键上抬.	
	$(document).keyup(function(event){
		if(event.keyCode ==13){
			page();
		}
	});// --.

	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		// 分页时，重新定义action.
		$("#searchForm").attr("action","${ctx}/apply/creditUserApply/");
		$("#searchForm").submit();
		return false;
	}

	function pass(applyId, status) {
		// console.log("借款申请ID:\t" + applyId);
		top.$.jBox.open("iframe:${ctx}/loan/audit/creditAuditInfo/auditForm?id=" + applyId + "&status=" + status, "借款申请审批", 810, $(top.document).height() - 240, {
			buttons : {
				// "确定分配" : "ok",
				// "清除已选" : "clear",
				"关闭" : true
			},
			bottomText : "通过：上传压缩包；驳回：上传压缩包，填写驳回原因;",
			submit : function(v, h, f) {
				var pre_ids = h.find("iframe")[0].contentWindow.pre_ids;
				var ids = h.find("iframe")[0].contentWindow.ids;
				//nodes = selectedTree.getSelectedNodes();
				/* if (v == "ok") {
					// 删除''的元素
					if (ids[0] == '') {
						ids.shift();
						pre_ids.shift();
					}
					// 执行保存
					loading('正在提交，请稍等...');
					var idsArr = "";
					for (var i = 0; i < ids.length; i++) {
						idsArr = (idsArr + ids[i]) + (((i + 1) == ids.length) ? '' : ',');
					}
					$('#idsArr').val(idsArr);
					$('#assignRoleForm').submit();
					return true;
				} else if (v == "clear") {
					h.find("iframe")[0].contentWindow.clearAssign();
					return false;
				} */
			},
			loaded : function(h) {
				// $(".jbox-content", top.document).css("overflow-y", "hidden");
			},
			closed : function() {
				location.reload();
			}
		});
	}
	// --
	function zdwSearch(guarantorCompanyName) {
		// console.log("中登网查询");
		var height = window.screen.height;
		// console.log("高 = " + height);
		var width = window.screen.width;
		// console.log("宽 = " + width);
		// console.log("担保人企业名称：" + guarantorCompanyName);
		window.open("${ctx}/zdw/register/zdwSearchRegisterInfo?guarantorCompanyName=" + guarantorCompanyName, "", "height=" + height + ", width=" + width, false);
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a id="credit_user_apply_id" href="${ctx}/apply/creditUserApply/">申请列表</a></li>
		<!-- 
		<shiro:hasPermission name="apply:creditUserApply:edit">
			<li><a href="${ctx}/apply/creditUserApply/form">借款申请添加</a></li>
		</shiro:hasPermission>
		-->
	</ul>
	<form:form id="searchForm" modelAttribute="creditUserApply" action="${ctx}/apply/creditUserApply/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<ul class="ul-form">
			<li><label class="label">申请日期：</label> <input name="beginCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${creditUserApply.beginCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /> - <input name="endCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate" value="<fmt:formatDate value="${creditUserApply.endCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" /></li>
			<li>
				<label class="label">期限：</label>
				<form:select path="span" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:option value="30" label="30天"/>
					<form:option value="60" label="60天"/>
					<form:option value="90" label="90天"/>
					<form:option value="120" label="120天"/>
					<form:option value="180" label="180天"/>
					<form:option value="360" label="360天"/>
				</form:select>
			</li>
			<li>
				<label class="label">状态：</label>
				<form:select path="state" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:option value="0" label="草稿"/>
					<form:option value="1" label="审核中"/>
					<form:option value="2" label="审核通过"/>
					<form:option value="3" label="审核驳回"/>
					<form:option value="4" label="融资中"/>
					<form:option value="5" label="还款中"/>
					<form:option value="6" label="结束"/>
				</form:select>
			</li>
			<li class="clearfix"></li>
			<li>
				<label class="label">核心企业：</label>
				<form:select path="replaceUserId" class="input-xxlarge">
					<form:option value="" label="请选择"/>
					<c:forEach var="middlemen" items="${middlemenList}">
						<form:option value="${middlemen.id}" label="${middlemen.enterpriseFullName}" />
					</c:forEach>
				</form:select>
			</li>
			<li>
				<label class="label">融资主体：</label>
				<form:input path="loanUserEnterpriseFullName" htmlEscape="false" class="input-xxlarge" />
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>资料名称</th>
				<th>担保人/核心企业</th>
				<th>核心企业电话</th>
				<th>借款人/供应商</th>
				<th>借款人电话</th>
				<th>金额(元)</th>
				<th>期限(天)</th>
				<th>状态</th>
				<th>申请时间</th>
				<th>修改时间</th>
				<th>融资合同</th>
				<shiro:hasPermission name="apply:creditUserApply:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="creditUserApply">
				<tr>
					<td><a href="${ctx}/credit/annexFile/list?otherId=${creditUserApply.projectDataInfo.id}&&remark=${creditUserApply.projectDataInfo.name}&creditUserApplyId=${creditUserApply.id}">${creditUserApply.projectDataInfo.name}</a></td>
					<td>${creditUserApply.replaceUserEnterpriseFullName}</td><!-- ${creditUserApply.replaceUserInfo.name} -->
					<td>${creditUserApply.replaceUserInfo.phone}</td>
					<td>${creditUserApply.loanUserEnterpriseFullName}</td><!-- ${creditUserApply.loanUserName} -->
					<td>${creditUserApply.loanUserPhone}</td>
					<td>${creditUserApply.amount}</td>
					<td>${creditUserApply.span}</td>
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
					<td><fmt:formatDate value="${creditUserApply.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${creditUserApply.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>
						<c:if test="${creditUserApply.borrPurpose == '' and creditUserApply.borrPurpose ==null}">
							暂无
						</c:if>
						<c:if test="${creditUserApply.borrPurpose != '' and creditUserApply.borrPurpose != null }">
							<a href="${imgUrl}${creditUserApply.borrPurpose}" target="_blank"><b>查看</b></a>
						</c:if>
					</td>
					<shiro:hasPermission name="apply:creditUserApply:edit">
						<td>
							<c:if test="${creditUserApply.state == '1' }">
								<%-- <a href="${ctx}/apply/creditUserApply/audit?id=${creditUserApply.id}&status=2" style="padding-right:9px;" onclick="return confirmx('确认要执行【通过】操作吗？', this.href)"><label class="label">通过</label></a> --%>
								<a onclick="zdwSearch('${creditUserApply.loanUserEnterpriseFullName}');" style="padding-right:9px;"><label class="label">中登查询</label></a>
								<a onclick="pass('${creditUserApply.id}', '2');" style="padding-right:9px;"><label class="label">通过</label></a>
								<a onclick="pass('${creditUserApply.id}', '3');" style="padding-right:9px;"><label class="label">驳回</label></a>
								<%-- <a href="${ctx}/apply/creditUserApply/audit?id=${creditUserApply.id}&status=3" onclick="return confirmx('确认要执行【驳回】操作吗？', this.href)"><label class="label">驳回</label></a> --%>
							</c:if>
							<c:if test="${creditUserApply.state == '2' }">
								<a href="${ctx}/apply/creditUserApply/audit?id=${creditUserApply.id}&status=4" style="padding-right:9px;" onclick="return confirmx('确认要执行【融资】操作吗？', this.href)"><label class="label">融资</label></a>
							</c:if>
							<c:if test="${creditUserApply.state == '4' }">
								<a href="${ctx}/apply/creditUserApply/audit?id=${creditUserApply.id}&status=5" style="padding-right:9px;" onclick="return confirmx('确认要执行【还款】操作吗？', this.href)"><label class="label">还款</label></a>
							</c:if>
							<c:if test="${creditUserApply.state == '5' }">
								<a href="${ctx}/apply/creditUserApply/audit?id=${creditUserApply.id}&status=6" style="padding-right:9px;" onclick="return confirmx('确认要执行【结束】操作吗？', this.href)"><label class="label">结束</label></a>
							</c:if>
						</td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>