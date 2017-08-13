<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-错误页面</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>

<body>
<center>
	<jsp:include page="common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
	
<div class="bag">
	<div>
		<s:if test="null==#session.errorTips">
			出错了！
		</s:if>
		<s:else>
			<s:property value="#session.errorTips"/>
		</s:else>
	</div>
	<s:form action="login">
		<s:submit value="登录系统"></s:submit>
	</s:form>
</div>
	
	<jsp:include page="common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center>
</body>
</html>