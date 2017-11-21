<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" />-确认实习基地联系人（<s:property value="annual.year" />）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>

<div class="bag">
	<table style="width:80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				确认实习基地联系人（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>

	<jsp:include page="/model/search/TableOperationPage.jsp" flush="true">
		<jsp:param name="actionName" value="function_OuterPersonImport"/>
		<jsp:param name="joinParamPage" value="/model/search/joinparam/OnePage.jsp"/>

		<jsp:param name="poiBoolean" value="true"/>
		<jsp:param name="createBoolean" value="true"/>
		<jsp:param name="deleteBoolean" value="true"/>
	</jsp:include>
	
	
</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>