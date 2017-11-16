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
		<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>

<div class="bag">
	<table style="width:80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				数据库管理
			</div>
		</td></tr>
	</tbody></table>
	
	<table class="wtable"><tbody>
		<s:form action="sudo_SQLOperation_execute" method="post" cssClass="myform" theme="simple">
			<tr><td>
				<s:textarea name="sql" value="%{sql}" style="width:360px;height:60px;"/>
			</td></tr>
			<tr><td>
				<s:submit value="执行SQL语句" cssClass="button"/>
			</td></tr>
			<s:token />
		</s:form>
		<tr><td height="30px"></td></tr>
	</tbody></table>
	<table class="wtable"><tbody>
		<s:if test="list!=null && labels!=null">
			<tr class="wtableHeader">
				<td style="width:13px">序号</td>
				<s:iterator value="labels" var="__label">
					<td style="word-wrap:break-word;word-break:break-all;">
						<s:property value="%{#__label}"/>
					</td>
				</s:iterator>
			</tr>
			<s:iterator value="list" var="__tableRow" status="__status">
				<tr class="wtableContent">
					<td class="listContent" style="width:13px"><s:property value="%{#__status.count}" /></td>
					<s:iterator value="__tableRow" var="__tableCol">
						<td>
							<s:property value="__tableCol" />
						</td>
					</s:iterator>
				</tr>
			</s:iterator>
		</s:if>
		<s:else>
			<tr class="wtableContent"><td>
				这里会显示结果集...
			</td></tr>
		</s:else>
	</tbody></table>
	
	
</div>

		<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
	</center>
</body>
</html>