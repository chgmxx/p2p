<%@ taglib prefix="shiro" uri="/WEB-INF/tlds/shiros.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fnc" uri="/WEB-INF/tlds/fnc.tld" %>
<%@ taglib prefix="fns" uri="/WEB-INF/tlds/fns.tld" %>
<%@ taglib prefix="sys" tagdir="/WEB-INF/tags/sys" %>
<%@ taglib prefix="act" tagdir="/WEB-INF/tags/act" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/cms" %>
<c:set var="ctx" value="${pageContext.request.contextPath}${fns:getAdminPath()}"/>
<c:set var="ctxStatic" value="${pageContext.request.contextPath}/static"/>
<c:set var="imgUrl" value="${fns:getConfig('imgUrl')}"/>
<c:set var="creditImgUrl" value="${fns:getConfig('credit_file_path')}"/>


<!-- test. -->
<%-- <c:set var="mainPath" value="http://182.92.114.130:8088/data"/>
<c:set var="downpath" value="http://182.92.114.130:8088/data"/>
<c:set var="downpath2" value="http://182.92.114.130:8088/data/upload/image/annexFileZip/"/> --%>

<!-- online. -->
<c:set var="mainPath" value="https://www.cicmorgan.com"/>
<c:set var="downpath" value="https://www.cicmorgan.com"/>
<c:set var="downpath2" value="https://www.cicmorgan.com/upload/image/annexFileZip/"/>
