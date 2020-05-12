<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>资料清单管理</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/lightbox.css" />
    <script type="text/javascript" src="${ctxStatic}/js/lightbox.js"></script>
	<script type="text/javascript">
		var imgs = [];
		var list = '${urlList}';
		var listSize = '${urlListSize}';
		var downLoadAll = '${downLoadAll}';
		list = list.substr(1,list.length-1);
		$(document).ready(function() {
			// 一键下载
			$("#btnSubmit").click(function(){
				var zipName = '${downLoadAll}';
				// console.log("zip name：\t" + zipName);
				if(zipName.trim() == ""){
					$.jBox.messager("由于资料文件没有找到，导致打包没有成功，无法下载 ...", "消息提醒", 3000, { width: 300, showType: 'slide', icon: 'info' });
				} else {
					window.location.href = '${downpath2 }' + zipName;
				}
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>

</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">资料清单</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="creditAnnexFile" action="${ctx}/credit/annexFile/list?otherId=${creditInfoId }" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>资料名称：</label>${creditAnnexFile.remark}</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="一键下载"/></li>
			<li class="clearfix"></li>
			<li><label>合同名称：</label><span class="help-inline">${pack.name}</span></li>
			<li><label>合同编号：</label><span class="help-inline">${pack.no}</span></li>
			<li><label>合同金额：</label><span class="help-inline">${pack.money}</span></li>
			<li>
				<label>合同类型：</label>
				<span class="help-inline">
					<c:if test="${pack.type == '1'}">
						<b>贸易合同</b>
					</c:if>
					<c:if test="${pack.type == '2'}">
						<b>联营合同</b>
					</c:if>
					<c:if test="${pack.type == '3'}">
						<b>购销合同</b>
					</c:if>
				</span>
			</li>
			<li><label>有效日期：</label><span class="help-inline"><fmt:formatDate value="${pack.userdDate}" pattern="yyyy-MM-dd HH:mm:ss"/></span></li>
			<li><label>签订日期：</label><span class="help-inline"><fmt:formatDate value="${pack.signDate}" pattern="yyyy-MM-dd HH:mm:ss"/></span></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>资料类型</th>
				<th>资料照片</th>
				<th>修改时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditAnnexFile">
			<tr>
				<td>
					<c:choose>
						<c:when test="${creditAnnexFile.type == '6' }">
							<c:if test="${empty creditAnnexFile.creditVoucher}">
								<b>发票</b>
							</c:if>
							<c:if test="${not empty creditAnnexFile.creditVoucher}">
								<b>发票，发票号：${creditAnnexFile.creditVoucher.no}，发票金额：${creditAnnexFile.creditVoucher.money}</b>
							</c:if>
						</c:when>
						<c:otherwise>
							${fns:getDictLabel(creditAnnexFile.type, 'credit_info_type', '')}
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:if test="${creditAnnexFile.type == '7' }">
						<c:choose>
							<c:when test="${creditAnnexFile.bookByCommitment_B == true }">
								<a href="${mainPath}${creditAnnexFile.url}" target="_blank"><b>付款承诺书</b></a>
							</c:when>
							<c:otherwise>
								<a class="example-image-link"  download="${downpath }/upload/image/${creditAnnexFile.url}" src="${downpath }/upload/image/${creditAnnexFile.url}" href="${downpath }/upload/image/${creditAnnexFile.url}" data-lightbox="example-1">
									<img alt="${fns:getDictLabel(creditAnnexFile.type, 'credit_info_type', '')}" style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;">
								</a>
								<a class="example-image-link downFile"  download="${downpath }/upload/image/${creditAnnexFile.url}"  href="${downpath }/upload/image/${creditAnnexFile.url}" data-lightbox="example-1"/>
							</c:otherwise>
						</c:choose>
					</c:if>
				    <c:if test="${creditAnnexFile.type != '7' }">
				    	<a class="example-image-link" download="${downpath }/upload/image/${creditAnnexFile.url}" href="${downpath }/upload/image/${creditAnnexFile.url}" data-lightbox="example-1">
					    <img alt="${fns:getDictLabel(creditAnnexFile.type, 'credit_info_type', '')}" src="${downpath }/upload/image/${creditAnnexFile.url}" style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;">
				        </a>
				        <a class="example-image-link downFile"  download="${downpath }/upload/image/${creditAnnexFile.url}"  href="${downpath }/upload/image/${creditAnnexFile.url}" data-lightbox="example-1"/>
				    </c:if>
				</td>
				<td><fmt:formatDate value="${creditAnnexFile.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			</tr>
		</c:forEach>
		<!-- 借贷风险、禁止性行为提示书. -->
		<tr>
			<td>借贷风险、禁止性行为提示书</td>
			<td><a href="${imgUrl}${declarationFilePath}" target="_blank"><b>查看</b></a></td>
			<td><fmt:formatDate value="${creditUserApplyUpdateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
		</tr>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<div class="form-actions">
	    
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</body>
</html>