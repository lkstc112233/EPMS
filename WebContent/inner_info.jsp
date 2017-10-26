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
		<jsp:include page="common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>

<div class="bag">
		<s:form action="inner_info" method="post" cssClass="myform">
			<s:textfield label="id" value="%{#session.inner.id}" readonly="true"/>

			<s:textfield label="name" name="inner.name"
				value="%{#session.inner.name}" />

	<!--	<s:password label="old password" showPassword="true"
				name="inner.password" value="%{#session.inner.password}" />	-->

			<s:password label="new password(if necessary)" showPassword="true"
				name="newPassword" value="" />

			<s:select label="office" disabled="true" 
				list="list_office" value="%{#session.inner.office}" listKey="name" listValue="name"
				/> <!-- name="inner.office"-->

			<s:select label="school" disabled="true"
				list="list_school" value="%{#session.inner.school}" listKey="name" listValue="name"
				/> <!-- name="inner.school"-->

			<s:textfield label="phone" name="inner.phone"
				value="%{#session.inner.phone}" />

			<s:textfield label="mobile" name="inner.mobile"
				value="%{#session.inner.mobile}" />

			<s:textfield label="email" name="inner.email"
				value="%{#session.inner.email}" />
		
			<s:hidden name="executive" value="true" />
			<s:submit value="提交修改" cssClass="button"/>
			
			<s:token />
		</s:form>
</div>

		<jsp:include page="common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
	</center>
</body>
</html>