<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>

<body>
<center>
	<jsp:include page="common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>

	<h2>欢迎<s:property value="#session.id"></s:property>来到免费师范生教育实习管理系统!</h2>
	<s:form action="submit"  method="post">
		<s:textarea name="command" label="命令" cols="20" rows="5"></s:textarea>
		<s:submit value="运行"></s:submit>
	</s:form>
	
	<jsp:include page="common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center>
</body>
</html>