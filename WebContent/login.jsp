<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教育实习管理系统</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>

<body>
<center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
<div class="bag">
	<s:form action="login"  method="post" cssClass="myform">
		<s:textfield name="inner.id" label="学工号"></s:textfield>
		<s:password name="inner.password" label="密码"></s:password>
		<s:hidden name="back" value="1" theme="simple" />
		<s:submit value="登录" cssClass="button" />
	</s:form>
</div>
	
	<jsp:include page="/model/common_bottom.jsp" flush="true">
		<jsp:param name="noBackButton" value="true"/>
	</jsp:include>
</center>
</body>
</html>