<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>

<body>
	<center>
		<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>

<div class="bag">
		<s:form action="InnerInfo_execute" method="post" cssClass="myform">
			<s:textfield label="id" value="%{inner.id}" readonly="true"/>

			<s:textfield label="name" name="inner.name"
				value="%{inner.name}" />

			<s:password label="new password(if necessary)" showPassword="true"
				name="inner.password" value="" />

			<s:select label="office" disabled="true" 
				listKey="name" listValue="name"
				list="list_office"
				value="%{inner.office.name}"
				/> <!-- name="inner.office"-->

			<s:select label="school" disabled="true"
				listKey="name" listValue="name"
				list="list_school"
				value="%{inner.school.name}"
				/> <!-- name="inner.school"-->

			<s:textfield label="phone" name="inner.phone"
				value="%{inner.phone}" />

			<s:textfield label="mobile" name="inner.mobile"
				value="%{inner.mobile}" />

			<s:textfield label="email" name="inner.email"
				value="%{inner.email}" />
		
			<s:submit value="提交修改" cssClass="button"/>
			
			<s:token />
		</s:form>
</div>

		<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
	</center>
</body>
</html>