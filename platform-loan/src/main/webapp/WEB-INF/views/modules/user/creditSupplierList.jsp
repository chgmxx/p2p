<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>借款申请管理--供应商管理</title>
<meta name="decorator" content="default" />

<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />

<style>
.form-group {
	clear: both;
	margin-bottom: 20px;
	overflow: hidden;
}

.form-group label {
	width: 100px;
	text-align: right;
	margin-right: 20px;
}

.apply_tit span {
	width: 130px;
	background: #36a7e7;
	height: 40px;
	color: #fff;
	text-align: center;
	display: block;
	font-size: 18px;
	line-height: 40px;
	margin-right: 10px;
	border-radius: 5px;
	cursor: pointer;
}

.table-condensed th, .table-condensed td {
	padding: 12px 5px !important;
	text-align: center !important;
}

.apply_tit {
	padding-left: 5px;
	padding-right: 5px;
}

.apply_tit span {
	width: 100px;
	background: #36a7e7;
	height: 40px;
	color: #fff;
	text-align: center;
	display: block;
	font-size: 15px;
	line-height: 40px;
	margin-right: 10px;
	border-radius: 5px;
	cursor: pointer;
}

.nav, .breadcrumb {
	margin-bottom: 8px;
	background: #fff;
}

.add_msg h5 {
	font-weight: normal;
	color: #40a2fb;
	margin: 10px 0;
}

#myModalAdd .modal-footer .close {
	display: inline-block;
	float: none;
	width: 92px;
	background: #44ade9;
	height: 30px;
	font-size: 15px;
	text-shadow: none;
	opacity: 1;
	font-weight: normal;
	color: #fff;
}

.nav_head a {
	display: block
}

#myModal, #myModalAdd {
	display: none;
}

.modal-footer {
	text-align: center;
}

.file_box {
	position: relative;
	width: 100%;
	height: 40px;
	overflow: hidden;
	text-align: center;
}

.file_box .file_button {
	background-color: #FFF;
	min-width: 120px;
	line-height: 40px;
	cursor: pointer;
	font-size: 14px;
	color: #40a2fb;
}

.file_box .file_input {
	position: absolute;
	top: 0;
	left: 0;
	height: 40px;
	filter: alpha(opacity : 0);
	opacity: 0;
	width: 120px;
	cursor: pointer;
}

/* 消息提示div样式. */
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
<script type="text/javascript" src="${ctxStatic}/js/CheckUtils.js"></script>
<script type="text/javascript">
	var ctxpath = '${ctxpath}';
	var ctx = '${ctx}';
	var file;
	$(document).ready(function() {

		$("#tosave").click(function() {
			$('#tosave').attr("disabled","disabled");
			var phone = $("#phone").val(); // 手机号码.
			var pwd = $("#pwd").val(); // 初始密码.
			var middlemenId = $("#middlemenId").val(); // 核心企业唯一标识.
			var enterpriseFullName = $("#enterpriseFullName").val(); // 供应商企业全称.
			if (!checkPhone(phone)) { // 校验手机号码合法性
				$("#errorMsg").html("**请输入正确的手机号码").show();
				return false;
			} else if (pwd.trim() == "") { // 初始密码必填项.
				$("#errorMsg").html("**请输入初始密码，建议：123456").show();
				return false;
			} else if (enterpriseFullName.trim() == "") { // 供应商企业全称必填项.
				$("#errorMsg").html("**请输入完整的企业全称").show();
				return false;
			}

			$.ajax({
				url : ctx + "/sys/user/addSupplier", // 添加单个供应商.
				type : "post",
				async : false,
				dataType : 'json',
				data : {
					phone : phone,
					pwd : pwd,
					middlemenId : middlemenId,
					enterpriseFullName : enterpriseFullName
				},
				success : function(json) {
					var message = json.message;
					message_prompt(message);
					$("#myModal").hide();
				},
				error : function(e) {
					$(".error_msg").html("网络出现异常，请您稍后再试。");
					$(".error_msg").show();
				}
			});
		});

		$("input").focus(function() {
			$("#errorMsg").hide();
		});

		//批量上传供应商选择文件
		$("#textfield_02").click(function() {
			$(".file_input").click();
		});
		$(".file_input").on("change", function() {
			file = this.files[0];
			// $("#searchForm").attr("action","${ctx}/sys/user/addSupplierList?id=${id}");
			var $this = $(this);
			$this.html(this.value);
			$("#textfield_02").html(file.name);
			// $this.hide();
		});
		//批量添加供应商提交
		$("#addSupplierFile").click(function() {

			var formData = new FormData();
			formData.append("file", file);
			formData.append("middlementId", $("#middlemenId").val()); // 核心企业唯一标识.
			$.ajax({
				url : ctx + "/sys/user/addSupplierList", // 批量添加供应商.
				type : "POST",
				data : formData,
				contentType: false, // 必须false才会自动加上正确的Content-Type.
				processData: false, // 必须false才会避开jQuery对‘formdata’的默认处理，XMLHttpRequest会对‘formdata’进行正确的处理.
				success : function(json) {
					var message = json.message;
					message_prompt(message);
					$("#myModalAdd").hide();
				},
				error : function(e) {
					$(".error_msg").html("网络出现异常，请您稍后再试。");
					$(".error_msg").show();
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

	// 消息提示-Mr.li.
	function message_prompt(message) {
		$(".mask_investNo_tip").html(message);
		$(".mask_investNo_tip").show();
		setTimeout(function() {
			$(".mask_investNo_tip").hide();
			window.history.go(0);
		}, 2000);
	} // --
</script>
</head>
<body>
	<div class="nav_head">供应商管理 <a href="#"  class="pull-right">
	<span class="pull-right" data-toggle="modal" data-target="#myModalAdd">批量添加</span>
	<span class="pull-right" data-toggle="modal" data-target="#myModal">添加供应商</span></a>
	</div>
	<!-- <div id="messageBox" class="alert alert-success "><button data-dismiss="alert" class="close">×</button>用户尚未开户</div> -->
	<sys:message content="${message}" />
	<form:form id="searchForm" modelAttribute="creditSupplierToMiddlemen" action="${ctx}/sys/user/supplier?middlemenId=${id}" method="post" class="breadcrumb form-search" enctype="multipart/form-data">
		<input type="file" name="file" style="display:none" class="file_input">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:message content="${message}" />
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>联系人</th>
					<th>供应商名称</th>
					<th>是否完善基本信息</th>
	<!-- 				<th>组织机构代码</th> -->
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="creditSupplierToMiddlemen">
					<tr>
						<td>${creditSupplierToMiddlemen.supplierUser.name}${creditSupplierToMiddlemen.supplierUser.phone}</td>
						<td>${creditSupplierToMiddlemen.supplierUser.enterpriseFullName}</td>
						<td>
							<c:choose>
								<c:when test="${creditSupplierToMiddlemen.supplierUser.isCreateBasicInfo == '1'}"><i>已完善</i></c:when>
								<c:otherwise><i style="color: red;">未完善</i></c:otherwise>
							</c:choose>
						</td>
	<%-- 					<td>${creditSupplierToMiddlemen.wloanSubject.organNo}</td> --%>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="pagination">${page}</div>
		
		<!-- 添加供应商  -->
		<div class="modal fade " id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title" id="myModalLabel">添加供应商</h4>
					</div>
					<div class="modal-body recharge_msg">
						<form class="form-horizontal">
							<div class="form-group">
								<label class="control-label pull-left">手机号</label>
								<div class="recharge_input pull-left">
									<input type="text" class="form-control" id="phone">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label pull-left">密码</label>
								<div class="recharge_input pull-left">
									<input type="password" class="form-control" id="pwd">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label pull-left">供应商名称</label>
								<div class="recharge_input pull-left">
									<input type="text" class="form-control" id="enterpriseFullName">
									<input type="hidden" id="middlemenId" value="${id}">
								</div>
							</div>							
							<!-- <div class="form-group">
								<label class="control-label pull-left">组织机构代码</label>
								<div class="recharge_input pull-left">
									<input type="text" class="form-control" id="orgCode">
								</div>
							</div> -->
							<div class="form-group">
								<label class="control-label pull-left"></label>
								<div class="recharge_input pull-left" id="errorMsg"style="margin-left: 131px; color: #f40;font-size: 12px; display:none">
									错误
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
	
						<button type="button" class="btn btn-primary withdraw_btn"  id="tosave">确认添加</button>
					</div>
				</div>
			</div>
		</div>
	</form:form>
	
	
	
	<!-- 批量添加 -->
	<div class="modal fade " id="myModalAdd" tabindex="-1" role="dialog" aria-labelledby="myModalLabelAdd">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title" id="myModalLabelAdd">添加供应商</h4>
				</div>
				<div class="modal-body">
					<div class="add_msg">
						<h5>温馨提示：</h5>
						<p>1、请上传excel格式文件。</p> 
						<p>2、请确保上传表格中包含手机号、密码、供应商名称字段，并且对应内容正确。如内容不正确则上传失败</p> 

						<div class="file_box">
							<div class="file_button" id="textfield_02">上传文件</div>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" id="addSupplierFile" class="btn btn-primary withdraw_btn">确认上传</button>	
					<button type="button" class="btn btn-primary withdraw_btn close" data-dismiss="modal" aria-label="Close">取消</button>
				</div>
			</div>
		</div>
	</div>
	<!-- 消息提示div. -->
	<div class="mask_investNo_tip"></div>
</body>
</html>