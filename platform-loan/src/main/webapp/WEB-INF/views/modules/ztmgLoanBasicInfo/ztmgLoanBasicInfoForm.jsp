<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>借款人基本信息管理</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/reset.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/bootstrap.min.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/style.css" />
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/lightbox.css" />
	<script type="text/javascript" src="${ctxStatic}/js/jquery.js"></script>
	<script type="text/javascript" src="${ctxStatic}/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/js/jquery.jerichotab.js"></script>
	<script src="${ctxStatic}/jquery-jbox/2.3/jquery.jBox-2.3.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
	<script src="${ctxStatic}/common/mustache.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/js/lightbox.js" type="text/javascript"></script>
	<script src="${ctxStatic}/js/jquery.cookie.js" type="text/javascript"></script>
	<script src="${ctxStatic}/js/addressArea.js?v=3" type="text/javascript"></script>
<style type="text/css">
	.lookimg_delBtn {
		z-index: 10
	}

	.div_imglook>div:hover .lookimg_delBtn {
		display: block !important;
	}

	.loan_apply_wrap {
		padding: 0 0 20px 0px;
	}

	.loan_apply {
		padding: 0 20px 0 0;
	}
</style>
<style type="text/css">
table.table td {
	padding: 10px 5px !important;
	vertical-align: middle !important;
}

table.table td input {
	height: 30px;
	width: 200px;
	margin-left: 8px;
}

table.table td select {
	width: 175px;
}

.alert {
	padding: 0 !important
}

table {
	border-collapse: collapse;
	border: none
}

.table-bordered>thead>tr>th, .table-bordered>thead>tr>td {
	border-bottom-width: 0;
}

.loan_apply {
	padding: 0
}

.loan_apply_wrap {
	padding-top: 0
}

.info_basic_wrap dl dd {
	padding-left: 0
}

table.table th {
	padding: 10px 3px !important;
	vertical-align: middle !important;
	font-weight: normal;
	position: relative;
}

.table-condensed th input {
	border: 0;
	background: none;
	color: #36a7e7;
}

.loan_apply .nav_head {
	padding-top: 15px
}

.province select {
	float: left;
}

.provice_group {
	float: left;
}

.provice_group label {
	float: left;
	margin: 0 15px;
}

.base_info {
	border: 1px solid #fdfdfd;
}

.base_info .base_group {
	border-bottom: 1px solid #ccc;
}

.base_info .base_group:last {
	border-bottom: 0;
}

.fl {
	float: left
}

.base_info .base_group label {
	
}

.base_info_group {
	padding: 15px;
}

.area_group {
	width: 50%;
	float: left;
	margin-bottom: 10px
}

.area_group:last-child {
	margin-bottom: 0;
}

.area_group label {
	padding: 0 3px
}

.add_info_wrap {
	position: relative;
	overflow: hidden;
	clear: both
}

.add_info_wrap .btn_info {
	position: absolute;
	top: 44px;
	right: 9px;
	width: 47px;
	height: 24px;
	line-height: 24px;
	text-align: center;
	color: #fff;
	background: #c9c9c9;
	cursor: pointer;
}

input.btn_submit {
	width: 47px;
	height: 24px;
	line-height: 24px;
	text-align: center;
	color: #fff !important;
	background: #40a2fb !important;
	/* margin-left: 10%; */
	cursor: pointer;
	float: right;
	margin-right: 9px;
}

.ml_15 {
	margin-left: 15px
}

table td:nth-of-type(1) {
	width: 105px;
	text-align: center;
}

.area_group_01 {
	width: 33.3333%
}

table.table .area_group_01 select {
	width: 150px
}

table.table .area_group_01:nth-of-type(3) label {
	padding: 0 5px 0 0;
}

.area_group_02 {
	width: 394px;
}

table.table td .area_group_02 input {
	width: 314px;
}

table.table td  .add_info_wrap input {
	margin: 0;
}

.mask_protocol, .mask_protocol_signature {
	max-height: 600px;
	overflow: auto;
	width: 728px;
	z-index: 1;
}

.mask_gray {
	position: fixed;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	z-index: 12;
	background-color: rgba(0, 0, 0, .8);
	width: 100%;
	height: 100%;
	display: none;
}

.mask_tip {
	position: fixed;
	top: 50%;
	left: 50%;
	transform: translateX(-50%) translateY(-50%);
	-webkit-transform: translateX(-50%) translateY(-50%);
	background: #fff;
	width: 500px;
	height: 224px;
	z-index: 19;
	border-radius: 20px;
	line-height: 224px;
	box-sizing: border-box;
	display: none;
	font-size: 24px;
	text-align: center;
}
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
<script type="text/javascript">
	// 页面自加载.
	$(function() {
		// -- .
		init_ztmg_Loan_Basic_Info();
		// 图片上传，文件控制器方法.
		fileController();
		// 声明文件.
		$(".agreement_01").click(function() {
			$(".mask_repd,.mask_protocol_signature").show();
		});
		// 关闭弹框.
		$(".close").click(function() {
			$(".mask_protocol_signature").hide();
		});
		// 声明文件，同意.
		$(".mask_protocol_signature .read_agreen").click(function() {
			$(".mask_protocol_signature").hide();
			$(".agreement_01 span").addClass("cur");
			$("input[id='declaration_file_id']").attr("checked", "true");
			$(".mask_gray").show();
			$(".mask_tip").show();
			// 借款人基本信息ID.
			var ztmg_loan_basic_info_id = $("#ztmg_loan_basic_info_id").val();
			// 借款人ID.
			var credit_user_id = $("#credit_user_id").val();
			$.ajax({
				url : "${ctx}/loan/basicinfo/ztmgLoanBasicInfo/declarationFileSign",
				type : "post",
				dataType : "json",
				data : {
					id : ztmg_loan_basic_info_id,
					creditUserId : credit_user_id
				},
				success : function(data) {
					$(".mask_gray").hide();
					$(".mask_tip").hide();
					var ztmgLoanBasicInfo = data.ztmgLoanBasicInfo;
					if (ztmgLoanBasicInfo.declarationFilePath == "") {
						$('#credit_pledge_a_id').removeAttr('href');
					} else {
						$("#credit_pledge_a_id").attr("href", "${mainPath}" + ztmgLoanBasicInfo.declarationFilePath);
					}
					message_prompt(data.message);
					// console.log(data.message);
				},
				error : function(data) {
					console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
					$(".mask_gray").hide();
					$(".mask_tip").hide();
				}
			});

		});
		// 声明文件，取消.
		$(".mask_protocol_signature .read_close").click(function() {
			$(".mask_protocol_signature").hide();
			$(".agreement_01 span").removeClass("cur");
			$("input[id='declaration_file_id']").removeAttr("checked");
		});
	});

	// 图片上传，文件控制器方法.
	function fileController() {

		// 添加图片-click事件.
		$(".flie_wrap .div_imgfile").click(function() {
			// closeMessage();
			$(this).siblings(".file").click();
		});

		// 添加图片-change事件.
		$(".file").on("change", function() {
			// closeMessage();
			// 借款人ID.
			var credit_user_id = $("#credit_user_id").val();
			var file = this.files[0];
			var $this = $(this);
			$this.parent().siblings(".info_error_msg").hide();
			var formData = new FormData();
			var type = $this.attr("id");
			var count = $this.parent().siblings(".div_imglook").children(".lookimg_wrap").length;
			// -- .
			formData.append("type", type);
			formData.append("creditInfoId", credit_user_id);
			formData.append("file1", file);
			$.ajax({
				url : "${ctxpath}/creditInfo/uploadCreditInfo",
				type : 'post',
				dataType : 'json',
				data : formData,
				// 告诉jQuery不要去处理发送的数据
				processData : false,
				// 告诉jQuery不要去设置Content-Type请求头
				contentType : false,
				success : function(result) {
					if (result.state == 0) {
						var annexFileId = result.annexFileId;
						readFile(file, $this.parent().siblings(".div_imglook"), annexFileId, type);
					}
				}
			});
		});

		// 移除图片-click事件 .
		$(".lookimg_delBtn").click(function() {
			// closeMessage();
			// 借款人ID.
			var credit_user_id = $("#credit_user_id").val();
			var $this = $(this);
			var annexFileId = $this.attr("id");
			deleteCreditInfo($this, annexFileId, credit_user_id);
		});
	}

	// 需要把阅读的文件传进来file element是把读取到的内容放入的容器.
	function readFile(file, element, id, type) {
		// 借款人ID.
		var credit_user_id = $("#credit_user_id").val();
		var reader = new FileReader();
		// 根据文件类型选择阅读方式.
		switch (file.type) {
		case 'image/jpg':
		case 'image/png':
		case 'image/jpeg':
		case 'image/gif':
			reader.readAsDataURL(file);
			break;
		}

		// 当文件阅读结束后执行的方法.
		reader.addEventListener('load', function() {
			//	如果说让读取的文件显示的话 还是需要通过文件的类型创建不同的标签.
			switch (file.type) {
			case 'image/jpg':
			case 'image/png':
			case 'image/jpeg':
			case 'image/gif':
				var img = document.createElement('img');
				var wrap = document.createElement('div');
				var input = document.createElement('input');
				input.setAttribute("class", "delete");
				input.setAttribute("type", "hidden");
				input.setAttribute("value", id)
				wrap.setAttribute("class", "lookimg_delBtn");
				wrap.setAttribute("id", id);
				wrap.innerHTML = "移除";
				img.src = reader.result;
				img.className = "example-image";
				var img_wrap = $("<div><a class='lookimg_wrap example-image-link' data-lightbox='example-"+type+"' href='"+reader.result+"'><img class='example-image' src='"+reader.result+"'/><input class='delete' type='hidden' value='"+id+"'/></a><div class='lookimg_delBtn' id="+id+">移除</div><div class='tit_pic'>" + file.name + "</div>WW</div>");
				element.append(img_wrap);
				element.show();
				$("#" + id).click(function() {
					var $this = $(this);
					// console.log("id = " + id );
					deleteCreditInfo($this, id, credit_user_id);
				});
				break;
			}
		});
	}

	// 删除征信报告附件.
	function deleteCreditInfo($this, id, creditUserId) {
		// closeMessage();
		$.ajax({
			url : "${ctxpath}/creditInfo/deleteCredit",
			type : 'post',
			dataType : 'json',
			data : {
				id : creditUserId,
				annexFileId : id
			},
			success : function(result) {
				if (result.state == 0) {
					$this.parent().remove();
				} else {
					$("#messageBox").show().html(result.message);
				}
			}
		});
	}

	// 初始化基本信息页面.
	function init_ztmg_Loan_Basic_Info() {

		// 核心企业不展示附加信息.
		var creditUserType = '${creditUserInfo.creditUserType}';
		if(creditUserType == '11'){
			$('.loan_apply').hide();
		}

		// 信用承诺书.
		var declarationFilePath = '${ztmgLoanBasicInfo.declarationFilePath}';
		if (declarationFilePath == "") {
			$('#credit_pledge_a_id').removeAttr('href');
		} else {
			$("#credit_pledge_a_id").attr("href", "${mainPath}" + declarationFilePath);
		}

		// 省份.
		var province = '${ztmgLoanBasicInfo.province}';
		var provinceObj = document.getElementById("l_address_province");
		for (var i = 0; i < provinceObj.options.length; i++) {
			if (provinceObj.options[i].value == province) {
				provinceObj.options[i].selected = true;
				change(1); // 联动地级市.
				break;
			}
		}
		// 地级市.
		var city = '${ztmgLoanBasicInfo.city}';
		var cityObj = document.getElementById("l_address_city");
		for (var i = 0; i < cityObj.options.length; i++) {
			if (cityObj.options[i].value == city) {
				cityObj.options[i].selected = true;
				change(2); // 联动市、县级市.
				break;
			}
		}
		// 市、县级市.
		var county = '${ztmgLoanBasicInfo.county}';
		var countyObj = document.getElementById("l_address_county");
		for (var i = 0; i < countyObj.options.length; i++) {
			if (countyObj.options[i].value == county) {
				countyObj.options[i].selected = true;
				break;
			}
		}
		// 所属行业.
		var industry = '${ztmgLoanBasicInfo.industry}';
		var industryObj = document.getElementById("industry_id");
		for (var i = 0; i < industryObj.options.length; i++) {
			if (industryObj.options[i].value == industry) {
				industryObj.options[i].selected = true;
				break;
			}
		}

		// 股东编号数组.
		// console.log(shareholders_array);

		// JSON数组.
		var shareholdersInfoPojoJsonArrayStr = '${shareholdersInfoPojoJsons}';
		// console.log("股东集合  = " + shareholdersInfoPojoJsonArrayStr);
		var siaObject = JSON.parse(shareholdersInfoPojoJsonArrayStr);
		// console.log(siaObject);
		// 遍历JSON数组.
		for (var i = 0; i < siaObject.length; i++) {
			// 股东类型.
			var shareholdersType = siaObject[i].shareholdersType;
			// 股东证件类型.
			var shareholdersCertType = siaObject[i].shareholdersCertType;
			// 股东名称.
			var shareholdersName = siaObject[i].shareholdersName;
			if (i == 0) { // 默认股东信息.
				// 股东类型.
				var shareholdersTypeObj = document.getElementById("shareholders_type_id_" + (i + 1));
				for (var j = 0; j < shareholdersTypeObj.options.length; j++) {
					if (shareholdersTypeObj.options[j].value == shareholdersType) {
						shareholdersTypeObj.options[j].selected = true;
						break;
					}
				}
				// 股东证件类型.
				var shareholdersCertTypeObj = document.getElementById("shareholders_cert_type_id_" + (i + 1));
				for (var j = 0; j < shareholdersCertTypeObj.options.length; j++) {
					if (shareholdersCertTypeObj.options[j].value == shareholdersCertType) {
						shareholdersCertTypeObj.options[j].selected = true;
						break;
					}
				}
				// 股东名称
				var shareholdersNameObj = document.getElementById("shareholders_name_id_" + (i + 1));
				shareholdersNameObj.value = shareholdersName;
			}
			if (i >= 1) { // 新增股东信息元素，并赋值.
				append_Child_Shareholders_Info();
				// 股东类型.
				var shareholdersTypeObj = document.getElementById("shareholders_type_id_" + (i + 1));
				for (var j = 0; j < shareholdersTypeObj.options.length; j++) {
					if (shareholdersTypeObj.options[j].value == shareholdersType) {
						shareholdersTypeObj.options[j].selected = true;
						break;
					}
				}
				// 股东证件类型.
				var shareholdersCertTypeObj = document.getElementById("shareholders_cert_type_id_" + (i + 1));
				for (var j = 0; j < shareholdersCertTypeObj.options.length; j++) {
					if (shareholdersCertTypeObj.options[j].value == shareholdersCertType) {
						shareholdersCertTypeObj.options[j].selected = true;
						break;
					}
				}
				// 股东名称
				var shareholdersNameObj = document.getElementById("shareholders_name_id_" + (i + 1));
				shareholdersNameObj.value = shareholdersName;
			}
		}
	}

	// 变量x，用于新增股东信息，记录股东，为股东进行编号.
	var x = 1;
	// 股东编号数组，记录当前股东的编号组，因为删除时编号不可控是无序的.
	var shareholders_array = new Array();
	shareholders_array[0] = 1;

	// 新增/修改-借款人基本信息-- .
	function ztmg_Loan_Basic_Info_Save() {
		// console.log("保存借款人基本信息.");

		// 借款人基本信息ID.
		var ztmg_loan_basic_info_id = $("#ztmg_loan_basic_info_id").val();
		// 借款人ID.
		var credit_user_id = $("#credit_user_id").val();
		// 省份.
		var l_address_province = $("#l_address_province").val();
		// 地级市.
		var l_address_city = $("#l_address_city").val();
		// 市、县级市.
		var l_address_county = $("#l_address_county").val();
		// 街道.
		var l_address_street = $("#l_address_street").val();
		// 公司名称.
		var company_name_val = $("#company_name_id").val();
		// 公司法人代表.
		var oper_name_val = $("#oper_name_id").val();
		// 注册地址.
		var registered_address_val = $("#registered_address_id").val();
		// 注册资本(元).
		var registered_capital_val = $("#registered_capital_id").val();
		// 实缴资本(元).
		var contributed_capital_val = $("#contributed_capital_id").val();
		// 成立时间.
		var set_up_time_val = $("#set_up_time_id").val();
		// 所属行业.
		var industry_val = $("#industry_id").val();
		// 经营区域.
		var scope_val = $("#scope_id").val();
		// 年营业收入(元).
		var annual_revenue_val = $("#annual_revenue_id").val();
		// 负债(元).
		var liabilities_val = $("#liabilities_id").val();
		// 征信信息.
		var credit_information_val = $("#credit_information_id").val();
		// 其它借款信息.
		var other_credit_information_val = $("#other_credit_information_id").val();

		// 必填项判断.
		if (l_address_province == "省份") { // 省份.
			$("#messageBox").show().html("请选择省份");
			return false;
		} else if (l_address_city == "地级市") { // 地级市.
			$("#messageBox").show().html("请选择地级市");
			return false;
		} else if (l_address_county == "市、县级市") { // 市、县级市.
			$("#messageBox").show().html("请选择市、县级市");
			return false;
		} else if (l_address_street.trim() == "") { // 街道.
			$("#messageBox").show().html("请填写街道信息");
			return false;
		} else if (company_name_val.trim() == "") { // 公司名称.
			$("#messageBox").show().html("请填写公司名称信息");
			return false;
		} else if (oper_name_val.trim() == "") { // 公司法人代表.
			$("#messageBox").show().html("请填写公司法人代表信息");
			return false;
		} else if (registered_address_val.trim() == "") { // 注册地址.
			$("#messageBox").show().html("请填写注册地址信息");
			return false;
		} else if (registered_capital_val.trim() == "") { // 注册资本.
			$("#messageBox").show().html("请填写注册资本信息");
			return false;
		} else if (contributed_capital_val.trim() == "") { // 实缴资本.
			$("#messageBox").show().html("请填写实缴资本信息");
			return false;
		} else if (set_up_time_val.trim() == "") { // 成立时间.
			$("#messageBox").show().html("请填写成立时间信息");
			return false;
		} else if (industry_val == "请选择") { // 所属行业.
			$("#messageBox").show().html("请选择所属行业信息");
			return false;
		} else if (scope_val.trim() == "") { // 经营区域.
			$("#messageBox").show().html("请填写经营区域信息");
			return false;
		} else if (shareholders_array.length > 0) { // 保存股东编号的数组.
			for (var i = 0; i < shareholders_array.length; i++) {
				// 股东类型.
				var shareholders_type = $("#shareholders_type_id_" + shareholders_array[i]).val();
				// 股东证件类型.
				var shareholders_cert_type = $("#shareholders_cert_type_id_" + shareholders_array[i]).val();
				// 股东名称.
				var shareholders_name = $("#shareholders_name_id_" + shareholders_array[i]).val();
				if (shareholders_type == "请选择") {
					$("#messageBox").show().html("请选择第" + (i + 1) + "位股东类型");
					return false;
				} else if (shareholders_cert_type == "请选择") {
					$("#messageBox").show().html("请选择第" + (i + 1) + "位股东证件类型");
					return false;
				} else if (shareholders_name.trim() == "") {
					$("#messageBox").show().html("请选择第" + (i + 1) + "位股东名称");
					return false;
				} else {
					$("#messageBox").css('display', 'none');
				}
			}
		}

		if (annual_revenue_val.trim() == "") { // 年营业收入(元).
			$("#messageBox").show().html("请填写上年营业收入信息");
			return false;
		} else if (liabilities_val.trim() == "") { // 负债(元).
			$("#messageBox").show().html("请填写年负债信息");
			return false;
		} else if (credit_information_val.trim() == "") { // 征信信息.
			$("#messageBox").show().html("请填写征信信息");
			return false;
		} else if (other_credit_information_val.trim() == "") { // 其它平台借款余额.
			$("#messageBox").show().html("请填写其它平台借款余额信息");
			return false;
		} else {
			$("#messageBox").css('display', 'none');
		}

		var isChecked = $("#declaration_file_id").attr("checked") == "checked";
		if (isChecked) {
			$("#messageBox").css('display', 'none');
		} else {
			// 核心企业不展示信用承诺书.
			var creditUserType = '${creditUserInfo.creditUserType}';
			if(creditUserType == '11'){
				$("#messageBox").css('display', 'none');
			} else {
				$("#messageBox").show().html("请阅读信用承诺书，并同意授权");
				return false;
			}
		}

		// console.log("借款人基本信息ID = " + ztmg_loan_basic_info_id);
		// console.log("股东数量 = " + shareholders_array.length);
		// 股东JSON数组.
		var str_shareholders_arrs = shareholders_Info_Array(shareholders_array.length);
		// console.log(str_shareholders_arrs);
		// str_shareholders_arrs = JSON.parse(str_shareholders_arrs);
		// console.log(str_shareholders_arrs);

		// 是否操作提交按钮.
		var r = false;
		if (ztmg_loan_basic_info_id.trim() == "") {
			r = confirm("确定要执行新增操作吗？")
		} else {
			r = confirm("确定要执行修改操作吗？")
		}
		if (r) { // 确认执行操作.
			$.ajax({
				url : "${ctx}/loan/basicinfo/ztmgLoanBasicInfo/ztmgLoanBasicInfoSave",
				type : "post",
				dataType : "json",
				data : {
					id : ztmg_loan_basic_info_id, // 借款人基本信息主键.
					creditUserId : credit_user_id, // 借款人主键.
					province : l_address_province, // 省份.
					city : l_address_city, // 地级市.
					county : l_address_county, // 市、县级市.
					street : l_address_street, // 街道.
					companyName : company_name_val, // 公司名称.
					operName : oper_name_val, // 公司法人代表.
					registeredAddress : registered_address_val, // 注册地址.
					registeredCapital : registered_capital_val, // 注册资本.
					contributedCapital : contributed_capital_val, // 实缴资本(元).
					setUpTime : set_up_time_val, // 成立时间.
					industry : industry_val, // 所属行业.
					scope : scope_val, // 经营区域.
					shareholdersJsonArrayStr : str_shareholders_arrs, // 股东JSONArray.
					annualRevenue : annual_revenue_val, // 年营业收入(元).
					liabilities : liabilities_val, // 负债(元).
					creditInformation : credit_information_val, // 征信信息.
					otherCreditInformation : other_credit_information_val
				// 其它借款信息.
				},
				success : function(data) {
					var ztmgLoanBasicInfo = data.ztmgLoanBasicInfo;
					// 借款人基本信息ID.
					$("#ztmg_loan_basic_info_id").val(ztmgLoanBasicInfo.id);
					$("#messageBox").show().html(data.message);
					//console.log(result.message);
					// 信用文件路径.
					if (ztmgLoanBasicInfo.declarationFilePath == "") {
						$('#credit_pledge_a_id').removeAttr('href');
					} else {
						$("#credit_pledge_a_id").attr("href", "${mainPath}" + ztmgLoanBasicInfo.declarationFilePath);
					}
				},
				error : function(data) {
					console.log("Ajax-请求中断-java.net.SocketException:Software caused connection abort:socket write error.");
				}
			});
		} else { // 取消操作.
			return false;
		}

	} // -- .

	// 股东信息-新增 .
	function append_Child_Shareholders_Info() {

		// 变量x = x + 1.
		x = x + 1;
		// console.log("x = " + x);
		// 股东编号组，记录股东编号.
		shareholders_array.push(x);
		// for (var a = 0; a < shareholders_array.length; a++) {
		// console.log("hareholders_array[" + a + "] = " + shareholders_array[a]);
		// }

		// div_1.
		var div_1 = document.createElement("div");
		div_1.setAttribute("class", "add_info_wrap");
		div_1.setAttribute("id", "shareholders_div_id_" + x);

		// div_2.
		var div_2 = document.createElement("div");
		div_2.setAttribute("class", "area_group");
		// label 股东类型.
		var label_shareholders_type = document.createElement("label");
		label_shareholders_type.setAttribute("class", "fl");
		label_shareholders_type.innerHTML = "股东类型";
		div_2.appendChild(label_shareholders_type);
		// select 股东类型.
		var select_shareholders_type = document.createElement("select");
		select_shareholders_type.setAttribute("id", "shareholders_type_id_" + x);
		// option.
		for (var i = 1; i <= 3; i++) {
			if (i == 1) { // 请选择.
				var select_shareholders_type_option_1 = document.createElement("option");
				select_shareholders_type_option_1.setAttribute("value", "请选择");
				select_shareholders_type_option_1.innerHTML = "请选择";
				select_shareholders_type.appendChild(select_shareholders_type_option_1);
			}
			if (i == 2) { // 自然人.
				var select_shareholders_type_option_2 = document.createElement("option");
				select_shareholders_type_option_2.setAttribute("value", "SHAREHOLDERS_TYPE_01");
				select_shareholders_type_option_2.innerHTML = "自然人";
				select_shareholders_type.appendChild(select_shareholders_type_option_2);
			}
			if (i == 3) { // 法人.
				var select_shareholders_type_option_3 = document.createElement("option");
				select_shareholders_type_option_3.setAttribute("value", "SHAREHOLDERS_TYPE_02");
				select_shareholders_type_option_3.innerHTML = "法人";
				select_shareholders_type.appendChild(select_shareholders_type_option_3);
			}
		}
		div_2.appendChild(select_shareholders_type);
		// div_1 + div_2.
		div_1.appendChild(div_2);

		// div_3.
		var div_3 = document.createElement("div");
		div_3.setAttribute("class", "area_group");
		// label 股东证件类型.
		var label_shareholders_cert_type = document.createElement("label");
		label_shareholders_cert_type.setAttribute("class", "fl");
		label_shareholders_cert_type.innerHTML = "股东证件类型";
		div_3.appendChild(label_shareholders_cert_type);
		// select 股东证件类型.
		var select_shareholders_cert_type = document.createElement("select");
		select_shareholders_cert_type.setAttribute("id", "shareholders_cert_type_id_" + x);
		// option.
		for (var i = 1; i <= 3; i++) {
			if (i == 1) { // 请选择.
				var select_shareholders_cert_type_option_1 = document.createElement("option");
				select_shareholders_cert_type_option_1.setAttribute("value", "请选择");
				select_shareholders_cert_type_option_1.innerHTML = "请选择";
				select_shareholders_cert_type.appendChild(select_shareholders_cert_type_option_1);
			}
			if (i == 2) { // 自然人.
				var select_shareholders_cert_type_option_2 = document.createElement("option");
				select_shareholders_cert_type_option_2.setAttribute("value", "SHAREHOLDERS_CERT_TYPE_01");
				select_shareholders_cert_type_option_2.innerHTML = "居民身份证";
				select_shareholders_cert_type.appendChild(select_shareholders_cert_type_option_2);
			}
			if (i == 3) { // 法人.
				var select_shareholders_cert_type_option_3 = document.createElement("option");
				select_shareholders_cert_type_option_3.setAttribute("value", "SHAREHOLDERS_CERT_TYPE_02");
				select_shareholders_cert_type_option_3.innerHTML = "营业执照";
				select_shareholders_cert_type.appendChild(select_shareholders_cert_type_option_3);
			}
		}
		div_3.appendChild(select_shareholders_cert_type);
		// div_1 + div_3.
		div_1.appendChild(div_3);

		// div_4.
		var div_4 = document.createElement("div");
		div_4.setAttribute("class", "area_group");
		// label 股东名称.
		var label_shareholders_name = document.createElement("label");
		label_shareholders_name.setAttribute("class", "fl");
		label_shareholders_name.innerHTML = "股东名称";
		div_4.appendChild(label_shareholders_name);
		// input 股东名称.
		var input_shareholders_name = document.createElement("input");
		input_shareholders_name.setAttribute("type", "text");
		input_shareholders_name.setAttribute("id", "shareholders_name_id_" + x);
		input_shareholders_name.setAttribute("maxlength", "32");
		input_shareholders_name.setAttribute("class", "fl");
		div_4.appendChild(input_shareholders_name);
		// div_1 + div_4.
		div_1.appendChild(div_4);

		// input 删除.
		var input_delete = document.createElement("input");
		input_delete.setAttribute("class", "add_info_wrap btn_info");
		input_delete.setAttribute("onclick", "delete_Child_Shareholders_Info(" + x + ");");
		input_delete.setAttribute("type", "button");
		input_delete.setAttribute("value", "删除");
		// div_1 + input-删除.
		div_1.appendChild(input_delete);

		// 原始股东信息-div.
		var shareholders_div = document.getElementById("shareholders_div_id_1");
		shareholders_div.parentNode.appendChild(div_1);

	} // -- .

	// 删除指定股东信息 .
	function delete_Child_Shareholders_Info(d) {

		// console.log("delete d = " + d);

		if (x == 1) {
			return false;
		}

		// 删除指定元素.
		shareholders_array.remove(d);
		// for (var a = 0; a < shareholders_array.length; a++) {
		// console.log("hareholders_array[" + a + "] = " + shareholders_array[a]);
		// }

		var delete_by_div = document.getElementById("shareholders_div_id_" + d);
		delete_by_div.parentNode.removeChild(delete_by_div);

	} // -- .

	// 数字查找指定元素的索引.
	Array.prototype.indexOf = function(val) {
		for (var i = 0; i < this.length; i++) {
			if (this[i] == val)
				return i;
		}
		return -1;
	}

	// 数组删除指点元素.
	Array.prototype.remove = function(val) {
		var index = this.indexOf(val);
		if (index > -1) {
			this.splice(index, 1);
		}
	};

	// 股东信息，数据封装 .
	function shareholders_Info_Array(length) {

		var shareholders_arrs = new Array();
		for (var i = 0; i < length; i++) {
			// JSON数组，第二维.
			var shareholders_arr = new Array();
			// 股东JSONObject.
			shareholders_json = {};
			// 前提知道JSONObject有多少个元素(目前是3个).
			for (var j = 0; j < 3; j++) {
				if (j == 0) {
					// 股东类型.
					var shareholders_type = $("#shareholders_type_id_" + shareholders_array[i]).val();
					shareholders_arr[j] = shareholders_type;
					var shareholdersType = "shareholdersType";
					shareholders_json[shareholdersType] = shareholders_arr[j];
				}
				if (j == 1) {
					// 股东证件类型.
					var shareholders_cert_type = $("#shareholders_cert_type_id_" + shareholders_array[i]).val();
					shareholders_arr[j] = shareholders_cert_type;
					var shareholdersCertType = "shareholdersCertType";
					shareholders_json[shareholdersCertType] = shareholders_arr[j];
				}
				if (j == 2) {
					// 股东名称.
					var shareholders_name = $("#shareholders_name_id_" + shareholders_array[i]).val();
					shareholders_arr[j] = shareholders_name;
					var shareholdersName = "shareholdersName";
					shareholders_json[shareholdersName] = shareholders_arr[j];
				}
			}
			shareholders_arrs[i] = shareholders_json;
		}

		// console.log(shareholders_arrs);
		var str_shareholders_arrs = JSON.stringify(shareholders_arrs);
		// console.log(str_shareholders_arrs);
		return str_shareholders_arrs;
	} // --

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
	<div class="loan_apply_wrap_02">
		<div class="nav_head">基本信息</div>
		<div id="messageBox" class="alert alert-success " style="display: none;">缺少必要参数</div>
		<div>
			<table class="table table-striped table-bordered table-condensed">
				<input type="text" class="error_msg" style="display: none">
				<input type="hidden" id="ztmg_loan_basic_info_id" value="${ztmgLoanBasicInfo.id}">
				<input type="hidden" id="credit_user_id" value="${ztmgLoanBasicInfo.creditUserId}">
				<thead>
					<tr>
						<th></th>
						<th>
							<input type="button" id="ztmg_Loan_Basic_Info_submit" value="提交" onclick="ztmg_Loan_Basic_Info_Save();" class="btn_submit" />
						</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>*办公地点</td>
						<td>
							<div class="add_info_wrap">
								<div class="area_group area_group_01">
									<label>省</label>
									<select id="l_address_province"></select>
								</div>
								<div class="area_group area_group_01">
									<label>市</label>
									<select id="l_address_city"></select>
								</div>
								<div class="area_group area_group_01">
									<label>县(区)</label>
									<select id="l_address_county"></select>
								</div>
								<div class="area_group area_group_02">
									<label class="fl">街道</label>
									<input type="text" maxlength="32" value="${ztmgLoanBasicInfo.street}" id="l_address_street" class="fl">
								</div>
							</div>
						</td>
					</tr>
					<tr>
						<td>*公司名称</td>
						<td>
							<div class="area_group area_group_02">
								<input type="text" value="${ztmgLoanBasicInfo.companyName}" maxlength="64" name="company_name_name" id="company_name_id">
							</div>
						</td>
					</tr>
					<tr>
						<td>*法定代表人</td>
						<td>
							<div class="area_group area_group_02">
								<input type="text" value="${ztmgLoanBasicInfo.operName}" maxlength="64" name="oper_name_name" id="oper_name_id">
							</div>
						</td>
					</tr>
					<tr>
						<td>*注册地址</td>
						<td>
							<div class="area_group area_group_02">
								<input type="text" value="${ztmgLoanBasicInfo.registeredAddress}" maxlength="128" name="registered_address_name" id="registered_address_id">
							</div>
						</td>
					</tr>
					<tr>
						<td>*注册资本(元)</td>
						<td>
							<div class="area_group area_group_02">
								<input type="text" value="${ztmgLoanBasicInfo.registeredCapital}" maxlength="32" name="registered_capital_name" onkeyup="value=value.replace(/[^\d]/g,'')" id="registered_capital_id">
							</div>
						</td>
					</tr>
					<tr>
						<td>*实缴资本(元)</td>
						<td>
							<div class="area_group area_group_02">
								<input type="text" value="${ztmgLoanBasicInfo.contributedCapital}" maxlength="32" name="contributed_capital_name" onkeyup="value=value.replace(/[^\d]/g,'')" id="contributed_capital_id">
							</div>
						</td>
					</tr>
					<tr>
						<td>*成立时间</td>
						<td>
						<input name="set_up_time_name" value="<fmt:formatDate value='${ztmgLoanBasicInfo.setUpTime}' pattern='yyyy-MM-dd'/>"  type="text" id="set_up_time_id" readonly="readonly" class="input-medium Wdate" placeholder="成立时间" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
						</td>
					</tr>
					<tr>
						<td>*所属行业</td>
						<td>
							<select name="industry_name" id="industry_id" class="ml_15">
									<option value="请选择">请选择</option>
									<option value="INDUSTRY_01">农林牧渔业</option>
									<option value="INDUSTRY_02">采矿业</option>
									<option value="INDUSTRY_03">制造业</option>
									<option value="INDUSTRY_04">电力热力燃气及水生产</option>
									<option value="INDUSTRY_05">供应业</option>
									<option value="INDUSTRY_06">建筑业</option>
									<option value="INDUSTRY_07">批发和零售业</option>
									<option value="INDUSTRY_08">交通运输仓储业</option>
									<option value="INDUSTRY_09">住宿和餐饮业</option>
									<option value="INDUSTRY_10">信息传输软件和信息技术服务业</option>
									<option value="INDUSTRY_11">金融业</option>
									<option value="INDUSTRY_12">房地产业</option>
									<option value="INDUSTRY_13">租赁和商务服务业</option>
									<option value="INDUSTRY_14">科研和技术服务业</option>
									<option value="INDUSTRY_15">水利环境和公共设施管理业</option>
									<option value="INDUSTRY_16">居民服务修理和其他服务业</option>
									<option value="INDUSTRY_17">教育</option>
									<option value="INDUSTRY_18">卫生和社会工作</option>
									<option value="INDUSTRY_19">文化体育和娱乐业</option>
									<option value="INDUSTRY_20">公共管理</option>
									<option value="INDUSTRY_21">社会保障和社会组织</option>
							</select>
						</td>
					</tr>
						<tr>
						<td>*经营区域</td>
						<td>
							<div class="area_group area_group_02">
								<input type="text" value="${ztmgLoanBasicInfo.scope}" maxlength="255" id="scope_id">
							</div>
						</td>
					</tr>
					<tr>
						<td>*股东信息</td>
						<td>
							<div class="add_info_wrap" id="shareholders_div_id_1">
								<div class="area_group">
									<label class="fl">股东类型</label>
									<select id="shareholders_type_id_1">
										<option value="请选择">请选择</option>
										<option value="SHAREHOLDERS_TYPE_01">自然人</option>
										<option value="SHAREHOLDERS_TYPE_02">法人</option>
									</select>
								</div>
								<div class="area_group">
									<label class="fl">股东证件类型</label>
									<select id="shareholders_cert_type_id_1">
										<option value="请选择">请选择</option>
										<option value="SHAREHOLDERS_CERT_TYPE_01">居民身份证</option>
										<option value="SHAREHOLDERS_CERT_TYPE_02">营业执照</option>
									</select>
								</div>
								<div class="area_group">
									<label class="fl">股东名称</label> <input type="text" id="shareholders_name_id_1" maxlength="32" class="fl">
								</div>
								<input class="add_info_wrap btn_info" onclick="append_Child_Shareholders_Info();" type="button" value="新增"> 
							</div>
						</td>
					</tr>
					<tr>
						<td>*年营业收入(元)</td>
						<td>
							<div class="area_group area_group_02">
								<input type="text" value="${ztmgLoanBasicInfo.annualRevenue}" maxlength="32" onkeyup="value=value.replace(/[^\d]/g,'')" id="annual_revenue_id">
							</div>
						</td>
					</tr>
					<tr>
						<td>*负债(元)</td>
						<td>
							<div class="area_group area_group_02">
								<input type="text" value="${ztmgLoanBasicInfo.liabilities}" maxlength="32" onkeyup="value=value.replace(/[^\d]/g,'')" id="liabilities_id">
							</div>
						</td>
					</tr>
					<tr>
						<td>*征信信息</td>
						<td>
							<div class="area_group area_group_02">
								<input type="text" value="${ztmgLoanBasicInfo.creditInformation}" id="credit_information_id">
							</div>
						</td>
					</tr>
					<tr>
						<td>*其它平台借款余额(元)</td>
						<td>
							<div class="area_group area_group_02">
								<input type="text" value="${ztmgLoanBasicInfo.otherCreditInformation}" onkeyup="value=value.replace(/[^\d]/g,'')" id="other_credit_information_id">
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<div class="loan_apply">
				<div class="loan_apply_wrap">
					<div class="nav_head">附加信息</div>
					<div class="la_con">
						<div class="la_step la_step_four cur">
							<div class="info_basic_wrap">
								<dl class="font_size18">
									<dd class="even">
										<b class="pull-left">征信报告</b>
										<span class="pull-left">支持图片格式</span>
										<span class="pull-right"><a href="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image-link" data-lightbox="example-10">
										<img src="${ctxStatic}/images/yingyezhizhao.jpg" class="example-image" style="display: none"></a>查看实例</span>
									</dd>

									<div class="imgfile_wrap">
										<div class="flie_wrap" id="flie_wrap_1">
											<div class="div_imgfile" id="div_imgfile_1"></div>
											<input type="file" accept="image/png,image/jpg" name="file" id="18" multiple="multiple" class="file">
										</div>
										<!--图片预览容器-->
										<div class="div_imglook" id="div_imglook_8">
											<c:forEach items="${creditAnnexFileList}"
												var="creditAnnexFile">
												<c:if test="${creditAnnexFile.type == '18'}">
													<div>
														<a class="lookimg_wrap example-image-link" data-lightbox="example-1" href="${staticPath}/upload/image/${creditAnnexFile.url}">
															<img class="example-image" src="${staticPath}/upload/image/${creditAnnexFile.url}">
															<input class="delete" type="hidden" value="${creditAnnexFile.id}">
														</a>
														<div class="lookimg_delBtn" id="${creditAnnexFile.id}">移除</div>
													</div>
												</c:if>
											</c:forEach>
										</div>
										<!--确定上传按钮-->
										<div class="info_error_msg" id="info_error_msg_1">图片大小不符</div>
									</div>
									</dl>
								</div>
						</div>
					</div>
					<!-- 声明文件，阅读声明文件并同意授权，生成声明文件. -->
					<div class="la_con">
						<div class="la_step la_step_four cur">
							<div class="info_basic_wrap">
								<dl class="font_size18">
									<dd class="even">
										<b class="pull-left">信用承诺书</b>
										<span class="pull-left">阅读信用承诺书并同意授权，生成信用承诺书</span>
									</dd>
								</dl>
							</div>
						</div>
					</div>
					<div class="setting_phone_group">
						<div class="agreement fl agreement_01">
							<span class=""><input type="checkbox" id="declaration_file_id" ><i></i></span>
							<em class="fl">信用承诺书</em>
						</div>
					</div>
					<div class="la_con">
						<div class="la_step la_step_four cur">
							<div class="info_basic_wrap">
								<dl class="font_size18">
									<dd class="even">
										<a id="credit_pledge_a_id" class="pull-left" href="#" target="_Blank">信用承诺书</a>
									</dd>
								</dl>
							</div>
						</div>
					</div>
				</div>
				<!-- 声明文件，阅读声明文件并同意授权，生成声明文件. -->
				<div class="mask_repd mask_protocol_signature">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
						<h4 class="modal-title" id="myModalLabel">信用承诺书</h4>
					</div>
					<div class="mask_model_repd">
						<div class="protocol_group">
							<h3>信用承诺书</h3>
							<p>中投摩根信息技术(北京)有限责任公司：</p>	
							<p>鉴于本单位现正在向贵公司申请P2P网络借款， 本单位向出借人及贵公司郑重承诺：</p>
							<p>本单位保证向贵公司提供的包括但不限于借款人及担保人基本信息、信用信息、资金用途、还款来源等全部借款资料、信息均为真实、准确、完整、有效的，如提供资料、信息存在虚假不实，本单位将承担相应的法律责任。</p>
							<p>特此承诺。</p>
							<div class="clear"><span class="fr">承诺人：xxx</span></div>
							<div class="clear"><span class="fr">法定代表人（签字）：xxx</span></div>
							<div class="clear"><span class="fr">日期：xxxx年-xx月-xx日</span></div> 
						</div>
						<div class="read_btn">
							<span class="read_agreen">同意</span>
							<span class="read_close">取消</span>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="mask_gray"></div>
		<div class="mask_tip">正在创建信用承诺书，请您耐心等待. . .</div>
		<div class="mask_investNo_tip"></div>
<!-- 三级联动初始化. -->
<script type="text/javascript">

// 三级联动初始化.
_init_area();

</script>
</body>
</html>