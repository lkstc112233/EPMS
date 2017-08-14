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
	<table width="80%" border="0" cellspacing="0" cellpadding="0"><tbody>
		<tr>
		<!--<td width="10%"></td>  左缝 -->
			<td valign="top">
				<s:form action="sql_operation_select" method="post">
					<s:textarea name="sql" value="%{sql}" style="width:960px;height:60px;"/>
					<s:submit value="执行SQL语句" />
				</s:form>
			</td>
		<!--<td width="10%"></td>  右缝 -->
		</tr>
	</tbody></table>
	<table width="80%" border="3" cellspacing="0" cellpadding="1"><tbody>
		<tr>
			<td valign="middle" align="center">
				<s:if test="list!=null && labels!=null">
					<tr>
						<td>序号</td>
						<s:iterator value="labels" var="__label">
							<td style="word-wrap:break-word;word-break:break-all;">
								<s:property value="%{#__label}"/>
							</td>
						</s:iterator>
					</tr>
					<s:iterator value="list" var="__tableRow" status="__status">
						<tr>
							<td><s:property value="%{#__status.count}" /></td>
							<s:iterator value="__tableRow" var="__tableCol">
								<td ><s:property value="__tableCol" /></td>
							</s:iterator>
						</tr>
					</s:iterator>
				</s:if>
				<s:else>
				<tr><td>这里会显示结果集...</td></tr>
				</s:else>
			</td>
		</tr>
	</tbody></table>
</div>

		<jsp:include page="common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
	</center>
</body>
</html>