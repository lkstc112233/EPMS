<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
    
	
<div class="bag">
	<% request.setAttribute("actionName",request.getParameter("actionName"));%>
		
	<div id="ActionName"></div>
	<table style="width:100%"><tbody><s:form action="%{#request.actionName}_display" method="get" theme="simple">
		<tr><td style="border:2px solid #0071BC;">
			<div style="padding-top:15px;padding-bottom:15px;">
				请选择操作表:
				<s:select list="tableNames"
				name="tableName"
				value="%{tableName}"
				onchange="window.location.href=window.location.href.substring(0,(
				window.location.href.lastIndexOf('_')<0?window.location.length:window.location.href.lastIndexOf('_'))
				)+'_display?tableName='+this.value"
				theme="simple"
				style="width:80%;" />
			</div>
		</td></tr>
	</s:form></tbody></table>
	
	
</div>
