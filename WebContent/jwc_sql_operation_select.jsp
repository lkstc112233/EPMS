<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-数据库管理</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>

<body>
	<center>
		<jsp:include page="common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>

<div class="bag">
	<table width="80%"><tbody>
		<tr>
			<td valign="top">
				<s:form action="jwc_sql_operation_select" method="post" cssClass="myform">
					<s:textarea name="sql" value="%{sql}" style="width:360px;height:60px;"/>
					<s:submit value="执行SQL语句" cssClass="button"/>
				</s:form>
			</td>
		</tr>
		<tr><td height="30px"></td></tr>
	</tbody></table>
	<table width="80%" class="mytable"><tbody>
		<s:if test="list!=null && labels!=null">
			<tr class="mytableTitle">
				<td>序号</td>
				<s:iterator value="labels" var="__label">
					<td style="word-wrap:break-word;word-break:break-all;">
						<s:property value="%{#__label}"/>
					</td>
				</s:iterator>
			</tr>
			<s:iterator value="list" var="__tableRow" status="__status">
				<tr>
					<td class="listContent"><s:property value="%{#__status.count}" /></td>
					<s:iterator value="__tableRow" var="__tableCol">
						<td>
							<s:property value="__tableCol" />
						</td>
					</s:iterator>
				</tr>
			</s:iterator>
		</s:if>
		<s:else>
		<tr><td>这里会显示结果集...</td></tr>
		</s:else>
	</tbody></table>
</div>

		<jsp:include page="common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
	</center>
</body>
</html>