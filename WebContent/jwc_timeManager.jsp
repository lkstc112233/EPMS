<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-时间节点管理</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>

<body>
	<center>
		<jsp:include page="common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>

<div class="bag">
	<s:form action="jwc_timeManager" method="post" cssClass="myform">
			<tr class="mytableTitle">
				<td class="tdLabel">序号</td>
				<td class="tdLabel">project</td>
				<td class="tdLabel">time(<s:property value="year" />)</td>
			</tr>
			<s:iterator value="projects" var="__tableRow" status="__status">
				<tr>
					<td class="tdLabel"><s:property value="%{#__status.count}" /></td>
					<s:iterator value="__tableRow" var="__tableCol">
						<td class="tdInput" style="width:400px;">
							<s:property value="%{#__tableCol.project}" />
							<s:hidden name="projects[%{#__status.index}].year"
							value="%{year}" theme="simple"/>
							<s:hidden name="projects[%{#__status.index}].project"
							value="%{#__tableCol.project}"  theme="simple"/>
						</td>
						<td class="tdInput" style="width:230px;">
							<s:textfield name="projects[%{#__status.index}].time" theme="simple">
								<s:param name="value">
									<s:date name="%{#__tableCol.time}" format="yyyy-MM-dd hh:mm:ss"/>
								</s:param>
							</s:textfield>  
						</td>
					</s:iterator>
				</tr>
			</s:iterator>
			<s:hidden name="year" value="%{year}" />
			<s:hidden name="executive" value="true" />
			<tr>
				<td colspan="3">
					<s:submit value="提交修改" cssClass="button" theme="simple" />
				</td>
			</tr>
	</s:form>
	<s:form action="login" method="post" cssClass="myform">
		<s:submit value="返回" cssClass="button"/>
	</s:form>
</div>

		<jsp:include page="common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
	</center>
</body>
</html>