<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户信息管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		
		$(function(){
			$(".control-group").css("display", "none");
			var group = $(".control-group");
			group[0].style.display = "block";
			group[7].style.display = "block";
		})
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }

        function changeSelectType(){
            $(".table").css("display", "none");
        	var type = $("#queryType").val();
			if(type == "2"){
				$(".control-group").css("display", "block");
			} else {
				$(".control-group").css("display", "none");
				var group = $(".control-group");
				group[0].style.display = "block";
				group[7].style.display = "block";
			}
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/seate/traf/form">车辆违章信息查询</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="seateEntity" action="${ctx}/seate/traf/query" method="post" class="form-horizontal">
		<div class="control-group">
			<div class="span10">
				<label class="control-label">查询类型：</label>
				<div class="controls">
					<form:select path="query_Type" class="input-xlarge required" onchange="changeSelectType()" id="queryType">
						<form:option value="0" label="车辆号牌代码查询"/>
						<form:option value="1" label="交管局信息查询"/>
						<form:option value="2" label="车辆违章记录查询"/>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span10">
				<label class="control-label">管局名称：</label>
				<div class="controls">
					<form:input path="carorg" htmlEscape="false" maxlength="32" class="input-xlarge required" style="width:250px"/>
					<span class="help-inline"><font color="red">*</font>&nbsp;参看交管局信息返回的管局名称值</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span10">
				<label class="control-label">车牌前缀：</label>
				<div class="controls">
					<form:input path="lsprefix" htmlEscape="false" maxlength="55" class="input-xlarge required" style="width:250px"/>&nbsp;
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span10">
				<label class="control-label">车牌剩余部分：</label>
				<div class="controls">
					<form:input path="lsnum" htmlEscape="false" maxlength="55" class="input-xlarge required" style="width:250px"/>&nbsp;
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span10">
				<label class="control-label">号牌类型：</label>
				<div class="controls">
					<form:input path="lstype" htmlEscape="false" maxlength="55" class="input-xlarge required" style="width:250px"/>&nbsp;
					<span class="help-inline"><font color="red">*</font>&nbsp; 参看车辆号牌代码返回的代码值</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span20">
				<label class="control-label">发动机号：</label>
				<div class="controls">
					<form:input path="engineno" htmlEscape="false" maxlength="55" class="input-xlarge required" style="width:250px"/>&nbsp;
					<span class="help-inline"><font color="red">*</font>&nbsp; 根据管局需要输入,参看交管局信息返回的发动机号值，0为可以不输入，100为全部输入，其他值表示发动机号的后N位。</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<div class="span10">
				<label class="control-label">车架号：</label>
				<div class="controls">
					<form:input path="frameno" htmlEscape="false" maxlength="55" class="input-xlarge required" style="width:250px"/>&nbsp;
					<span class="help-inline"><font color="red">*</font>&nbsp; 同上</span>
				</div>
			</div>
		</div>
			
		<div class="control-group">
			<div class="form-actions">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查  询" />
			</div>
		</div>	
	</form:form>
	
	<sys:message content="${message }"/>
	
	<!-- 车辆号牌代码查询结果展示 -->
	<c:if test="${seateEntity.result.queryType == '0' }">
		<c:choose>
			<c:when test="${ seateEntity.result.code == '0000' }">
				<table class="table table-striped table-bordered table-condensed" style="max-width: 60%">
					<thead>
						<tr>
							<th colspan="2" style="text-align:center;">车辆号牌代码</th>
						</tr>
				    	<c:forEach items="${seateEntity.result.list}" var="map">
				        	<tr>
								<th style="text-align:center;">${map.key}</th>
								<th>${map.value}</th>
							</tr>
				    	</c:forEach>
					</thead>
				</table>
			</c:when>
				
			<c:otherwise>
				<c:if test="${seateEntity.result.code != null && seateEntity.result.code != '' }">
					<table class="table table-striped table-bordered table-condensed" style="max-width: 60%">
						<tbody>
							<tr>
								<td>
									查询失败,代码 ${seateEntity.result.code }
								</td>
							</tr>
						</tbody>
					</table>
				</c:if>
			</c:otherwise>
		</c:choose>
	</c:if>

	
	<!-- 交管局信息查询结果展示 -->
	<c:if test="${seateEntity.result.queryType == '1' }">
		<c:choose>
			<c:when test="${ seateEntity.result.code == '0000' }">
				<table class="table table-striped table-bordered table-condensed" style="max-width: 60%">
					<thead>
						<tr>
							<th>省份</th>
							<th>车牌前缀</th>
							<th colspan="3">详细信息</th>
						</tr>
				    	<c:forEach items="${seateEntity.result.data}" var="data">
							<c:forEach items="${data }" var="map">
								<c:if test="${map.key == 'list' }">
									<tr>
										<th rowspan="${fn:length(map.value) * 6 + 1 }">${data.province }</th>
										<th rowspan="${fn:length(map.value) * 6 + 1 }">${data.lsprefix }</th>
									</tr>
								
									<c:forEach items="${map.value }" var="list">
										<c:forEach items="${list }" var="cityMap">
											<c:if test="${cityMap.key == 'city' }">
												<tr>
													<th rowspan="6">${cityMap.value }</th>
												</tr>
											</c:if>	
										</c:forEach>
										<c:forEach items="${list }" var="cityMap">
											<c:if test="${cityMap.key != 'city' }">
												<tr>
													<th>
														<c:if test="${cityMap.key == 'frameno' }">
															${cityMap.key  }&nbsp;(车架号)
														</c:if>
														<c:if test="${cityMap.key == 'carorg' }">
															${cityMap.key  }&nbsp;(管局名称)
														</c:if>
														<c:if test="${cityMap.key == 'engineno' }">
															${cityMap.key  }&nbsp;(发动机号)
														</c:if>
														<c:if test="${cityMap.key == 'lsnum' }">
															${cityMap.key  }&nbsp;(车牌首字母)
														</c:if>
														<c:if test="${cityMap.key == 'lsprefix' }">
															${cityMap.key  }&nbsp;(车牌前缀)
														</c:if>
													</th>
													<th>${cityMap.value }</th>
												</tr>
											</c:if>	
										</c:forEach>
									</c:forEach>
								</c:if>
							</c:forEach>
							
				    	</c:forEach>
					</thead>
				</table>
			</c:when>
				
			<c:otherwise>
				<c:if test="${seateEntity.result.code != null && seateEntity.result.code != '' }">
					<table class="table table-striped table-bordered table-condensed" style="max-width: 60%">
						<tbody>
							<tr>
								<td>
									查询失败,代码 ${seateEntity.result.code }
								</td>
							</tr>
						</tbody>
					</table>
				</c:if>
			</c:otherwise>
		</c:choose>
	</c:if>
	
	
	<!-- 车辆违章记录查询 -->
	<c:if test="${seateEntity.result.queryType == '2' }">
		<c:choose>
			<c:when test="${ seateEntity.result.code == '0000' }">
				<c:if test="${ seateEntity.result.result.statcode == '2000' }">
					<table class="table table-striped table-bordered table-condensed" style="max-width: 60%">
						<thead>
							<c:forEach items="${seateEntity.result.result.list }" var="list"  varStatus="id">
								<tr>
									<th rowspan="${fn:length(list) * 14 + 1 }"> ${id.index + 1 }</th>
								</tr>
								<c:forEach items="${list }" var="trafMap">
									<tr>
										<th>${trafMap.key }</th>
										<th>${trafMap.value }</th>
									</tr>
								</c:forEach>
							</c:forEach>
						</thead>
					</table>
				</c:if>
				<c:if test="${ seateEntity.result.result.statcode == '2001' }">
					<table class="table table-striped table-bordered table-condensed" style="max-width: 60%">
						<thead>
							<tr>
								<th>查询结果</th>
								<th>查无数据</th>
							</tr>
						</thead>
					</table>
				</c:if>
			</c:when>
				
			<c:otherwise>
				<c:if test="${seateEntity.result.code != null && seateEntity.result.code != '' }">
					<table class="table table-striped table-bordered table-condensed" style="max-width: 60%">
						<tbody>
							<tr>
								<td>
									查询失败,代码 ${seateEntity.result.code }
								</td>
							</tr>
						</tbody>
					</table>
				</c:if>
			</c:otherwise>
		</c:choose>
	</c:if>
</body>
</html>