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
	<jsp:include page="../common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table width="80%" border="0" cellspacing="0" cellpadding="0"><tbody>
		<tr><td valign="top">
			<div class="listHeader">
				<div class="listHeaderLeft">管理功能</div>
			</div>
			<ul class="listContent">
				<li>
					<span class="time">[All time]</span>
					<a href='<s:url action="jwc_sql_operation_select"/>'>
						数据库管理
					</a>
				</li>
				<li>
					<span class="time">[All time]</span>
					<a href='<s:url action="jwc_TableOperation_display"/>?tableName=Student'>
						数据表管理
					</a>
				</li>
			</ul>
		</td></tr>
		<tr>
			<td valign="top">
				<div class="listHeader">
					<div class="listHeaderLeft">自然年管理功能</div>
					<div class="right">
						当前自然年: 
						<s:select id="year" name="year" cssClass="title_button" style="width:120px;margin-bottom:2px;"
						list="{'2016','2017','2018','2019','2020','2021'}" theme="simple"
						onchange="window.location.href=window.location.href.substring(0,(
						window.location.href.indexOf('?')<0?window.location.length:window.location.href.indexOf('?'))
						)+'?year='+this.value"/>
					</div>
				</div>
			</td>
		</tr>
		<tr><td valign="top">
			<ul class="listContent">
				<li>
					<span class="time">[All time]</span>
					<a href='<s:url action="jwc_timeManager"/>' onclick="this.href=this.href+'?year='+document.getElementById('year').value">
						时间节点管理
					</a>
				</li>
			</ul>
		</td></tr>
		<s:iterator value="times" var="__tableRow" status="__status">
			<tr><td valign="middle">
		<!--	<td class="tdLabel"><s:property value="%{#__status.count}" /></td>	-->
				<ul class="listContent">
					<li>
						<span class="time">
							[
							<s:if test="#__tableRow.time1!=null">
								<b><s:date name="%{#__tableRow.time1}" format="yyyy-MM-dd"/></b>&nbsp;
								<s:date name="%{#__tableRow.time1}" format="HH:mm:ss"/>&nbsp;
								->
								&nbsp;<b><s:date name="%{#__tableRow.time2}" format="yyyy-MM-dd"/></b>
								&nbsp;<s:date name="%{#__tableRow.time2}" format="HH:mm:ss"/>
							</s:if>
							<s:else>
								null
							</s:else>
							]
						</span>
						<a href='<s:url action="%{actionPrefix}_function_%{#__tableRow.actionClass}_display"/>' onclick="this.href=this.href+'?year='+document.getElementById('year').value">
							<s:property value="%{#__tableRow.project}" />
						</a>
					</li>  
				</ul>
		<!--	</td>	-->
			</td></tr>
		</s:iterator>
	</tbody></table>
	
	
	
	
	<table style="width:180px;border:0;cellspacing:0;cellpadding:0"><tbody>
		<tr><td height="100px" width="100%" colspan="3" valign="top" /></tr>
		<tr><td><a href='JavaScript:history.back(1)' class="button">返回</a></td></tr>
	</tbody></table>
</div>

	<jsp:include page="../common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>