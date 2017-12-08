<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" /></title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table width="80%" border="0" cellspacing="0" cellpadding="0"><tbody>
		<s:if test="#session.role.name == '教务处'">
			<tr><td valign="top" colspan="100">
				<div class="listHeader">
					<div class="listHeaderLeft">管理功能</div>
				</div>
				<ul class="listContent">
					<li>
						<span class="time">[All time]</span>
						<a href='<s:url action="sudo_SQLOperation_display"/>'>
							数据库管理
						</a>
					</li>
					<li>
						<span class="time">[All time]</span>
						<a href='<s:url action="sudo_TableOperation_display"/>?tableName=ACCESS'>
							数据表管理
						</a>
					</li>
				</ul>
			</td></tr>
		</s:if>
		<tr>
			<td valign="top" colspan="100">
				<div class="listHeader">
					<div class="listHeaderLeft">自然年管理功能</div>
					<div class="right">
						当前自然年: 
						<s:select id="annualYear" name="annual.year" cssClass="title_button" style="width:120px;margin-bottom:2px;"
						list="{'2016','2017','2018','2019','2020','2021'}" theme="simple"
						onchange="window.location.href=window.location.href.substring(0,(
						window.location.href.lastIndexOf('/')<0?window.location.length:window.location.href.lastIndexOf('/'))
						)+'/menu.action?annual.year='+this.value"/>
					</div>
				</div>
			</td>
		</tr>
		<s:if test="#session.role.name == '教务处'">
			<tr><td valign="top" colspan="100">
				<ul class="listContent">
					<li>
						<span class="time">[All time]</span>
						<a href='<s:url action="sudo_TimeManager_display"/>' onclick="this.href=this.href+'?annual.year='+document.getElementById('annualYear').value">
							时间节点管理
						</a>
					</li>
				</ul>
			</td></tr>
			<s:if test="times.isEmpty() == false">
				<tr><td valign="top" colspan="100">
					<ul class="listContent">
						<li>
							<span class="time">[All time]</span>
							<a href='<s:url action="sudo_TimeReset_execute"/>' onclick="this.href=this.href+'?annual.year='+document.getElementById('annualYear').value">
								重设今年项目
							</a>
						</li>
					</ul>
				</td></tr>
			</s:if>
		</s:if>
		<s:iterator value="times" var="__tableRow" status="__status">
			<tr>
				<td style="width:13px;"><s:property value="%{#__status.count}" /></td>
				<td valign="middle"><ul class="listContent">
					<li>
						<span class="time">
							<s:if test="#__tableRow.time1!=null">
								[<b><s:date name="%{#__tableRow.time1}" format="yyyy-MM-dd"/></b>&nbsp;
								<s:date name="%{#__tableRow.time1}" format="HH:mm:ss"/>&nbsp;
								->
								&nbsp;<b><s:date name="%{#__tableRow.time2}" format="yyyy-MM-dd"/></b>
								&nbsp;<s:date name="%{#__tableRow.time2}" format="HH:mm:ss"/>
								]
							</s:if>
							<s:else>
								&nbsp;
							</s:else>
						</span>
						<s:if test="isSpecialACCESSName(#__tableRow.project)">
							<s:property value="%{#__tableRow.project}" />
						</s:if><s:else>
							<a href='<s:url action="function_%{#__tableRow.actionClass}_display"/>' onclick="this.href=this.href+'?annual.year='+document.getElementById('annualYear').value">
								<s:property value="%{#__tableRow.project}" />
							</a>
						</s:else>
					</li>  
				</ul></td>
			</tr>
		</s:iterator>
	</tbody></table>
	
	
</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>