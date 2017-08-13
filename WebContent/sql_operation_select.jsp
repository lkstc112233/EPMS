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
					<s:select label="table" name="table"
						list="{'InnerOffice','Major'}" headerKey="-1" headerValue="请选择数据表名"/>
					<s:submit value="查询" />
				</s:form>
			</td>
		<!--<td width="10%"></td>  右缝 -->
		</tr>
	</tbody></table>
	<table width="80%" border="5" cellspacing="1" cellpadding="1"><tbody>
		<tr>
			<td valign="top">
				<s:if test="table!=null">
					welcome
					<tr>
						<s:iterator value="labels" var="__label">
							<td>
								<s:property value="%{#__label}"/>
							</td>
						</s:iterator>
					</tr>
					<s:iterator value="list" var="__tableRow" status="__status">
						<tr>
							<s:iterator value="__tableRow" var="__tableCol">
								<td>
									<s:property value="__tableCol" />
								</td>
							</s:iterator>
							<td width="100px" valign="middle">
								<s:form action="sql_operation" method="post">
									<s:hidden name="chooseIndex" value="%{#__status.index}" />
									<s:submit value="操作" />
								</s:form>
							</td>
						</tr>
					</s:iterator>
				</s:if>
			</td>
		</tr>
	</tbody></table>
</div>

		<jsp:include page="common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
	</center>
</body>
</html>