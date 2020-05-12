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
<c:set var="staticPath" value="https://www.cicmorgan.com"/>


<!-- LanMao test -->
<!-- <c:set var="mainPath" value="http://182.92.114.130:8088/data"/>
<c:set var="ctxpath" value="http://www.cicmorgan.com/svc/services"/>
<c:set var="cgbpath" value="https://hk.lanmaoly.com/bha-neo-app/lanmaotech/gateway"/>
<c:set var="downpath" value="http://182.92.114.130:8088/data"/>
<c:set var="downpathD" value="http://182.92.114.130:8088"/>
<c:set var="ctxURL" value="http://www.cicmorgan.com"/>
<c:set var="staticPath" value="http://182.92.114.130:8088/data"/> -->
<!-- test -->
<%-- <c:set var="mainPath" value="http://182.92.114.130:8088/data"/>
<c:set var="ctxpath" value="http://182.92.114.130:8082/svc/services"/>
<c:set var="cgbpath" value="http://sandbox.firstpay.com/hk-fsgw/gateway"/>
<c:set var="downpath" value="http://182.92.114.130:8088/data"/>
<c:set var="downpathD" value="http://182.92.114.130:8088"/>
<c:set var="ctxURL" value="http://182.92.114.130:8082"/>
<c:set var="staticPath" value="http://182.92.114.130:8088/data"/> --%>
<!-- online -->
<c:set var="mainPath" value="https://www.cicmorgan.com"/>
<c:set var="ctxpath" value="https://www.cicmorgan.com/svc/services"/>
<c:set var="cgbpath" value="https://cg2.unitedbank.cn/bha-neo-app/lanmaotech/gateway"/>
<c:set var="downpath" value="https://www.cicmorgan.com"/>
<c:set var="ctxURL" value="https://www.cicmorgan.com"/> 
<c:set var="staticPath" value="https://www.cicmorgan.com"/>
<c:set var="downpathD" value="https://www.cicmorgan.com"/>

