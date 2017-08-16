<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table width="80%" border="0" cellspacing="0" cellpadding="0"><tbody>
		<tr>
			<td valign="top">
				<div class="listHeader">
					<div class="listHeaderLeft">人员管理功能</div>
				</div>
				<ul class="listContent">
					<li>
						<span class="time">[All time]</span>
						<a href='<s:url action="jwc_sql_operation_select"/>'>数据库管理</a>
					</li>
				</ul>
			</td>
		</tr>
		<tr>
			<td valign="top">
				<div class="listHeader">
					<div class="listHeaderLeft">自然年管理功能</div>
					<div class="right">
						当前自然年: 
						<s:select id="year" cssClass="title_button" style="width:120px;margin-top:4px;"
						list="{'2017','2018'}" theme="simple"/>
					</div>
				</div>
			</td>
		</tr>
		<tr><td valign="top">
			<ul class="listContent">
				<li>
					<span class="time">[All time]</span>
					<a href='<s:url action="jwc_timeManager"/>' onclick="this.href=this.href+'?year='+document.getElementById('year').value">
					时间节点管理</a>
				</li>
			</ul>
		</td></tr>
	</tbody></table>
</div>

	<jsp:include page="common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>