/**
 * 发票列表，上传完成后可以继续新增新的发票记录.
 */

$(function() {

	// 上传发票资料文件-点击事件.
	$(".invoice_file_id").on("click", function() {
		closeMessage();
		var file = this.files[0];
		var $this = $(this);
		var no = $this.parent().parent().siblings().children("#creVoucherNo").val();
		var money = $this.parent().parent().siblings().children("#creVoucherMoney").val();
		var code = $this.parent().parent().siblings().children("#creVoucherCode").val();
		var issueDate = $this.parent().parent().siblings().children("#creVoucherIssueDate").val();
		if (no == null || no.trim() == "") {
			errMessage("请填写发票编号！");
			return false;
		}
		if (money == null || money.trim() == "") {
			errMessage("请填写发票金额！");
			return false;
		}
		if (!validatorVoucherMoney(money)) {
			errMessage("发票金额，只允许正整数和两位小数位的小数！");
			return false;
		}
		if (code == null || code.trim() == "") {
			errMessage("请填写发票代码！");
			return false;
		}
		if (issueDate == null || issueDate.trim() == "") {
			errMessage("请填写开票日期！");
			return false;
		}
	});

	// 上传文件-改变事件.
	$(".invoice_file_id").on("change", function() {
		closeMessage();
		var file = this.files[0];
		var $this = $(this);
		var no = $this.parent().parent().siblings().children("#creVoucherNo").val();
		var money = $this.parent().parent().siblings().children("#creVoucherMoney").val();
		var code = $this.parent().parent().siblings().children("#creVoucherCode").val();
		var issueDate = $this.parent().parent().siblings().children("#creVoucherIssueDate").val();

		// 非undefined判断
		if (typeof (file) != "undefined") {
			var fileName = this.value;
			fileName = fileName.split("\\")[fileName.split("\\").length - 1];
			$this.parent().parent().siblings(".voucherFileView").children("#creVoucherFileName").html(fileName);

			var formData = new FormData();
			var type = $this.attr("id");

			formData.append("type", type);
			formData.append("creditInfoId", creditInfoId);
			formData.append("packNo", packNo);
			formData.append("voucherNo", no);
			formData.append("voucherMoney", money);
			formData.append("voucherCode", code);
			formData.append("voucherIssueDate", issueDate);
			formData.append("creditApplyId", creditApplyId);
			formData.append("file1", file);

			$.ajax({
				url : ctxpath + "/creditInfo/uploadCreditInfo",
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
						$this.parent().parent().siblings("#creVoucherId").val(result.creditVoucherId);
						$this.parent().parent().siblings("#creVoucherAnnexFileId").val(annexFileId);
						$this.parent().parent().siblings().children("#creVoucherNo").attr("disabled", "disabled");
						$this.parent().parent().siblings().children("#creVoucherMoney").attr("disabled", "disabled");
						$this.parent().parent().siblings().children("#creVoucherCode").attr("disabled", "disabled");
						$this.parent().parent().siblings().children("#creVoucherIssueDate").attr("disabled", "disabled");
						$this.parent().parent().siblings(".voucherFileView").show();
						$this.parent().parent().hide();
						readFilef(file, $this.parent().parent().siblings(".voucherFileView"));
						appendVoucherHtml(money);
					} else {
						errMessage(result.message);
					}
				}
			});
		} else {
			errMessage("请选择上传文件 . . . ");
		}
	});

	// 删除发票信息
	$(".voucherDelete").click(function() {
		// errMessage("删除发票信息中 ...");
		closeMessage();
		var $this = $(this);
		var voucherId = $this.parent().siblings("#creVoucherId").val();
		var annexFileId = $this.parent().siblings("#creVoucherAnnexFileId").val();
		var type = "6"; // 资料类型：发票.
		deleteCreditInfo6($this, annexFileId, type);
	});
});

//消息提示 .
function message_prompt(message) {
	$(".mask_investNo_tip").html(message);
	$(".mask_investNo_tip").show();
	setTimeout(function() {
		$(".mask_investNo_tip").hide();
	}, 2000);
} // --

function appendVoucherHtml(money){
	voucherSum = parseFloat(voucherSum) + parseFloat(money);
	var str = '<tr>'
		+ '<td><input type="text" id="creVoucherNo" name="" maxlength="8" placeholder="发票编号" style="width: 138px;" /></td>'
		+ '<td><input type="text" id="creVoucherMoney" name="" maxlength="10" placeholder="发票金额" style="width: 138px;" /></td>'
		+ '<td><input type="text" id="creVoucherCode" name="" maxlength="12" placeholder="发票代码" style="width: 138px;" /></td>'
		+ '<td><input type="text" id="creVoucherIssueDate" name="" value="" readonly="readonly" class="input-medium Wdate" placeholder="开票日期" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\',isShowClear:false});" style="width: 140px;" /></td>'
		+ '<td style="display:block;"><div class="file_box"><a href="#">上传文件</a><input type="file" name="fileField" class="file_input invoice_file_id" id="6" /></div></td>'
		+ '<td class="voucherFileView" style="display:none;"><a href="${staticPath}/upload/image/${creditVoucher.url}" class="example-image-link" data-lightbox="example-6" data-title="发票" style="padding-right: 20px"><img class="example-image" src="${staticPath}/upload/image/${creditVoucher.url}" style="display: none;">查看</a><span class="voucherDelete" style="color: red;">删除</span></td>'
		+ '<input type="hidden" id="creVoucherId" />'
		+ '<input type="hidden" id="creVoucherAnnexFileId" />'
		+ '</tr>';
	$("#my_voucher_tbody_id").append(str);
	
	// 上传发票资料文件-点击事件.
	$(".invoice_file_id").on("click", function() {
		closeMessage();
		var file = this.files[0];
		var $this = $(this);
		var no = $this.parent().parent().siblings().children("#creVoucherNo").val();
		var money = $this.parent().parent().siblings().children("#creVoucherMoney").val();
		var code = $this.parent().parent().siblings().children("#creVoucherCode").val();
		var issueDate = $this.parent().parent().siblings().children("#creVoucherIssueDate").val();
		if (no == null || no.trim() == "") {
			errMessage("请填写发票编号！");
			return false;
		}
		if (money == null || money.trim() == "") {
			errMessage("请填写发票金额！");
			return false;
		}
		if (!validatorVoucherMoney(money)) {
			errMessage("发票金额，只允许正整数和两位小数位的小数！");
			return false;
		}
		if (code == null || code.trim() == "") {
			errMessage("请填写发票代码！");
			return false;
		}
		if (issueDate == null || issueDate.trim() == "") {
			errMessage("请填写开票日期！");
			return false;
		}
	});

	// 上传文件-改变事件.
	$(".invoice_file_id").on("change", function() {
		closeMessage();
		var file = this.files[0];
		var $this = $(this);
		var no = $this.parent().parent().siblings().children("#creVoucherNo").val();
		var money = $this.parent().parent().siblings().children("#creVoucherMoney").val();
		var code = $this.parent().parent().siblings().children("#creVoucherCode").val();
		var issueDate = $this.parent().parent().siblings().children("#creVoucherIssueDate").val();

		// 非undefined判断
		if (typeof (file) != "undefined") {
			var fileName = this.value;
			fileName = fileName.split("\\")[fileName.split("\\").length - 1];
			$this.parent().parent().siblings(".voucherFileView").children("#creVoucherFileName").html(fileName);

			var formData = new FormData();
			var type = $this.attr("id");

			formData.append("type", type);
			formData.append("creditInfoId", creditInfoId);
			formData.append("packNo", packNo);
			formData.append("voucherNo", no);
			formData.append("voucherMoney", money);
			formData.append("voucherCode", code);
			formData.append("voucherIssueDate", issueDate);
			formData.append("creditApplyId", creditApplyId);
			formData.append("file1", file);

			$.ajax({
				url : ctxpath + "/creditInfo/uploadCreditInfo",
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
						$this.parent().parent().siblings("#creVoucherId").val(result.creditVoucherId);
						$this.parent().parent().siblings("#creVoucherAnnexFileId").val(annexFileId);
						$this.parent().parent().siblings().children("#creVoucherNo").attr("disabled", "disabled");
						$this.parent().parent().siblings().children("#creVoucherMoney").attr("disabled", "disabled");
						$this.parent().parent().siblings().children("#creVoucherCode").attr("disabled", "disabled");
						$this.parent().parent().siblings().children("#creVoucherIssueDate").attr("disabled", "disabled");
						$this.parent().parent().siblings(".voucherFileView").show();
						$this.parent().parent().hide();
						readFilef(file, $this.parent().parent().siblings(".voucherFileView"));
						appendVoucherHtml(money);
					} else {
						errMessage(result.message);
					}
				}
			});
		} else {
			errMessage("请选择上传文件 . . . ");
		}
	});

	// 删除发票信息
	$(".voucherDelete").click(function() {
		// errMessage("删除发票信息中 ...");
		closeMessage();
		var $this = $(this);
		var voucherId = $this.parent().siblings("#creVoucherId").val();
		var annexFileId = $this.parent().siblings("#creVoucherAnnexFileId").val();
		var type = "6"; // 资料类型：发票.
		deleteCreditInfo6($this, annexFileId, type);
	});
}
