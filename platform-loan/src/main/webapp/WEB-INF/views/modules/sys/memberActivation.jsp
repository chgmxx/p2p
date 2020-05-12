<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>会员激活</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var ctxpath = "${ctxpath}"; 
		var cgbpath = "${cgbpath}";
		function memberActivation(){
			var token = document.getElementById("token").value;
			$.ajax({
				url: ctxpath + "/lanmaoAccount/memberActivation",
				type: "post",
				dataType: "json",
				data: {
					from: '2',//1:表示为出借人解绑 2：表示为借款人解绑
					token: token
				},
				success: function(result1) {
					if(result1.state == 0) {
						var obj =  result1.data;
						openPostWindow(cgbpath, obj);
						//执行完跳转到首页
						window.location.href = './login';
					} else {
						window.location.href = './login';
						//$.cookie("token", '');
						//console.log(result1.message);
						//$(".error_msg").show().html(result1.message);
//						logout();
					}
				}
			});
	    }
		
		// 用window.open()方法跳转至新页面并且用post方式传参
		function openPostWindow(url, result){
			var tempForm = document.createElement("form");
		    tempForm.id = "tempForm1";
		    tempForm.method = "post";
		    tempForm.action = url;
		    tempForm.target="_blank"; //打开新页面
		    // hideInput1
		    var hideInput1 = document.createElement("input");
		    hideInput1.type = "hidden";
		    hideInput1.name="keySerial"; // 后台要接受这个参数来取值
		    hideInput1.value = result.keySerial; // 后台实际取到的值
		    tempForm.appendChild(hideInput1);
		    // hideInput2
		    var hideInput2 = document.createElement("input");
		    hideInput2.type = "hidden";
		    hideInput2.name="serviceName"; // 后台要接受这个参数来取值
		    hideInput2.value = result.serviceName; // 后台实际取到的值
		    tempForm.appendChild(hideInput2);
		    // hideInput3
		    var hideInput3 = document.createElement("input");
		    hideInput3.type = "hidden";
		    hideInput3.name="reqData"; // 后台要接受这个参数来取值
		    hideInput3.value = result.reqData; // 后台实际取到的值
		    tempForm.appendChild(hideInput3);
		    // hideInput4
		    var hideInput4 = document.createElement("input");
		    hideInput4.type = "hidden";
		    hideInput4.name="sign"; // 后台要接受这个参数来取值
		    hideInput4.value = result.sign; // 后台实际取到的值
		    tempForm.appendChild(hideInput4);
		    // hideInput5
		    var hideInput5 = document.createElement("input");
		    hideInput5.type = "hidden";
		    hideInput5.name="platformNo"; // 后台要接受这个参数来取值
		    hideInput5.value = result.platformNo; // 后台实际取到的值
		    tempForm.appendChild(hideInput5);
		    if(document.all){
		        tempForm.attachEvent("onsubmit",function(){});        //IE
		    }else{
		        var subObj = tempForm.addEventListener("submit",function(){},false);    //firefox
		    }
		    document.body.appendChild(tempForm);
		    if(document.all){
		        tempForm.fireEvent("onsubmit");
		    }else{
		        tempForm.dispatchEvent(new Event("submit"));
		    }
		    tempForm.submit();
		    document.body.removeChild(tempForm);
		}
	</script>
</head>
	<body>
		<input id="from"  type="hidden" value="2"/>
		<input id="token"  type="hidden" value="${token}"/>
		<input id="btnSubmit" class="btn btn-primary" type="buttom" value="激活" onclick="memberActivation()"/>
	</body>
</html>