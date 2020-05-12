<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>奖品信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

			Init_is_True();

			//$("#name").focus();
			$("#inputForm").validate({
				rules: {
					needAmount: {digits:true}
				},
				messages: {
					needAmount: {digits: "奖品积分必须为数字且不能为负数！"}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});

			// 是否为虚拟奖品-联动-抵用券类型.
			$('#isTrue').change(function(){
				// console.log("isEntrustedPay：" + $(this).children('option:selected').val());
				var is_true = $(this).children('option:selected').val();
				// console.log("isEntrustedPay：" + isEntrustedPay);
				isTrue(is_true);
			});// --

			// 根据抵用券类型的唯一标识，联动抵用券类型的相关属性.
			$('#vouchers_id').change(function(){
				// console.log("isEntrustedPay：" + $(this).children('option:selected').val());
				var vouchers_id = $(this).children('option:selected').val();
				// console.log("isEntrustedPay：" + isEntrustedPay);
				vouchersInfoByVouchersId(vouchers_id);
			});// --

		});

		// 判断是否为虚拟奖品，展示抵用券类型数据下拉选.
		function isTrue(is_true){
			if(is_true == '0'){ // 非虚拟奖品.
				document.getElementById("vouchers_div_id").style.display = "none";
			} else if(is_true == '1') { // 虚拟奖品.
				document.getElementById("vouchers_div_id").style.display = "block";
			} else {
				document.getElementById("vouchers_div_id").style.display = "none";
			}
		}

		// 初始化是否为虚拟奖品.
		function Init_is_True() {
			var is_true = $('#isTrue').val();
			isTrue(is_true);
		}

		function vouchersInfoByVouchersId(vouchers_id){
			$.ajax({
				url : "${ctx}/activity/aVouchersDic/vouchersInfoByVouchersId", 
				type : "post",
				async : false,
				contentType : "application/x-www-form-urlencoded",
				dataType : "json",
				data : {
					"vouchersId" : vouchers_id
				},
				success : function(data) {
					var a_vouchers_dic = data.aVouchersDic;
					// 奖品名称.
					if(typeof(a_vouchers_dic.amountStr) == "undefined"){
						$("#award_name_id").val("");
					} else {
						$("#award_name_id").val(a_vouchers_dic.amountStr + "元抵用券");
					}
				},
				error : function(data) {
					alert("程序异常");
				}
			});
		}// --.

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/award/awardInfo/">奖品信息列表</a></li>
		<li class="active"><a href="${ctx}/award/awardInfo/form?id=${awardInfo.id}">奖品信息<shiro:hasPermission name="award:awardInfo:edit">${not empty awardInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="award:awardInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="awardInfo" action="${ctx}/award/awardInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">奖品积分：</label>
			<div class="controls">
				<form:input path="needAmount" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="state" class="input-xlarge required">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('award_state')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">奖品名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-xlarge required" id="award_name_id"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电脑端图片：</label>
			<div class="controls">
				<form:hidden id="url" path="imgWeb" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="url" type="images" uploadPath="/photo" selectMultiple="true" maxWidth="100" maxHeight="100"/>
			</div>
		</div>
	    <div class="control-group">
			<label class="control-label">移动端图片：</label>
			<div class="controls">
				<form:hidden id="url1" path="imgWap" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="url1" type="images" uploadPath="/photo" selectMultiple="true" maxWidth="100" maxHeight="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否为抽奖奖品：</label>
			<div class="controls">
				<form:select path="isLottery" class="input-xlarge required">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否为虚拟奖品：</label>
			<div class="controls">
				<form:select path="isTrue" class="input-xlarge required">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div id="vouchers_div_id" style="display: none;">
			<div class="control-group">
				<label class="control-label">抵用券：</label>
				<div class="controls">
					<form:select path="vouchersId" class="input-xxlarge required" id="vouchers_id">
						<form:option value="" label="请选择"/>
						<c:forEach var="dic" items="${awardInfo.vouchersDics}">
							<form:option value="${dic.id}" label="${dic.id}，${dic.amountStr}，${dic.remarks}" />
						</c:forEach>
					</form:select>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">中奖概率：</label>
			<div class="controls">
				<form:input path="odds" htmlEscape="false" class="input-xlarge required"/>%
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">奖品说明：</label>
			<div class="controls">
				<form:input path="docs" htmlEscape="false" maxlength="255" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">奖品规格：</label>
			<div class="controls">
				<form:input path="awardStandard" htmlEscape="false" maxlength="255" class="input-xlarge "/>
			</div>
		</div>
				<div class="control-group">
			<label class="control-label">兑换流程：</label>
			<div class="controls">
				<form:input path="exchangeFlow" htmlEscape="false" maxlength="255" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">兑换说明：</label>
			<div class="controls">
				<form:input path="exchangeDocs" htmlEscape="false" maxlength="255" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">兑奖失效时长：</label>
			<div class="controls">
				<form:select path="deadline" class="input-xlarge required">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('award_deadline')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="award:awardInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>