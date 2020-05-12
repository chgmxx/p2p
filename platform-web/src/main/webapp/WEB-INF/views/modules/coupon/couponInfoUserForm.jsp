<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		function selectAll(){  
		    if ($("#SelectAll").attr("checked")) {  
		        $(":checkbox").attr("checked", true);  
		    } else {  
		        $(":checkbox").attr("checked", false);  
		    }  
		}  
		//子复选框的事件  
		function setSelectAll(){  
		    //当没有选中某个子复选框时，SelectAll取消选中  
		    if (!$("#userInfoId").checked) {  
		        $("#SelectAll").attr("checked", false);  
		    }  
		    var chsub = $("input[type='checkbox'][id='userInfoId']").length; //获取subcheck的个数  
		    var checkedsub = $("input[type='checkbox'][id='userInfoId']:checked").length; //获取选中的subcheck的个数  
		    if (checkedsub == chsub) {  
		        $("#SelectAll").attr("checked", true);  
		    }  
		}  
	
		function jqchk(){
			var chk_value =[]; 
			$('input[name="userInfoId"]:checked').each(function(){ 
				chk_value.push($(this).val()); 
			}); 
				if(chk_value.length==0){
					alert('你还没有选择任何要发送优惠券的人！');
				}else{
					window.location.href='${ctx}/coupon/couponInfoUser/save?userInfoIds='+chk_value+'&couponInfoId=${couponInfo.id}';
				}
			} 
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/coupon/couponInfoUser/form?couponInfoId=${couponInfo.id}">发送优惠券</a></li>
	</ul>
		<ul class="ul-form">
			<li><label>${fns:getDictLabel(couponInfo.type, 'coupon_info_type', '-')}</label>
				${couponInfo.amount }元
			</li>	
		</ul>
	<form:form id="searchForm" modelAttribute="couponInfoUser" action="${ctx}/coupon/couponInfoUser/form" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input type="hidden" name="couponInfo.id" value="${couponInfo.id}" >
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="userInfo.name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>姓名：</label>
				<form:input path="userInfo.realName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /></li>
			<li class="btns"><input type="button" id="send" class="btn btn-primary" value="发送" onclick="jqchk()"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>

	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>
					<input type="checkbox" id="SelectAll" value="全选" name="SelectAll" onclick="selectAll()"/>
					全选
				</th>
				<th>手机号码</th>
				<th>姓名</th>
				<th>证件号码</th>
				<th>性别</th>
				<th>状态</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userInfo">
			<tr>
				<td>
					<input type="checkbox" id="userInfoId" name="userInfoId" value="${userInfo.id}" onclick="setSelectAll();">
				</td>
				<td>
					${userInfo.name}
				</td>
				<td>
					${userInfo.realName}
				</td>
				<td>
					${userInfo.certificateNo}
				</td>
				<td>
					${fns:getDictLabel(userInfo.sex, 'sex', '')}
				</td>
				<td>
					${fns:getDictLabel(userInfo.state, 'user_state', '')}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>