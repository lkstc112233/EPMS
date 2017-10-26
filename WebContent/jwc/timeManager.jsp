<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-时间节点管理（<s:property value="year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>

<body>
	<center>
		<jsp:include page="../common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>

<div class="bag">
	<s:form action="jwc_timeManager" method="post" cssClass="myform">
		<tr class="mytableTitle">
			<td class="tdLabel">序号</td>
			<td class="tdLabel">project(<s:property value="year" />)</td>
			<td class="tdLabel">time start</td>
			<td class="tdLabel">time end</td>
		</tr>
		<s:iterator value="times" var="__tableRow" status="__status">
			<tr>
				<td class="tdLabel">
					<s:property value="%{#__tableRow.id}" />
					<s:hidden name="times[%{#__status.index}].id"
					value="%{id}" theme="simple"/>
				</td>
				<td class="tdInput" style="width:380px;">
					<s:property value="%{#__tableRow.project}" />
					<s:hidden name="times[%{#__status.index}].year"
					value="%{year}" theme="simple"/>
					<s:hidden name="times[%{#__status.index}].project"
					value="%{#__tableRow.project}"  theme="simple"/>
				</td>
				<td class="tdInput" style="width:180px;">
					<s:textfield name="times[%{#__status.index}].time1" theme="simple">
						<s:param name="value">
							<s:date name="%{#__tableRow.time1}" format="yyyy-MM-dd HH:mm:ss"/>
						</s:param>
					</s:textfield>
				</td>
				<td class="tdInput" style="width:180px;">
					<s:textfield name="times[%{#__status.index}].time2" theme="simple">
						<s:param name="value">
							<s:date name="%{#__tableRow.time2}" format="yyyy-MM-dd HH:mm:ss"/>
						</s:param>
					</s:textfield>
				</td>
			</tr>
		</s:iterator>
		<s:hidden name="year" value="%{year}" />
		<s:hidden name="executive" value="true" />
		<tr>
			<td colspan="4">
				<s:submit value="提交修改" cssClass="button" theme="simple" />
			</td>
		</tr>
		<s:token />
	</s:form>
	
	
	
	<table style="width:60%;border:0;cellspacing:0;cellpadding:0"><tbody>
		<tr><td height="100px" width="100%" colspan="3" valign="top" /></tr>
		<tr><td><a href='JavaScript:history.back(1)' class="button">返回</a></td></tr>
	</tbody></table>
</div>

		<jsp:include page="../common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
	</center>
</body>
</html>