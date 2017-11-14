<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
    
	
<div class="bag">
	<% request.setAttribute("actionName",request.getParameter("actionName"));%>
		
	<table style="width:100%"><tbody><s:form action="%{#request.actionName}_execute" method="get" theme="simple">
		<tr><td style="border:2px solid #0071BC;">
			restraint
		</td></tr>
		<tr><td colspan="1" style="width:20%;">
			&nbsp;
		</td></tr>
		<tr><td colspan="1" style="width:20%;">
			<s:submit value="查询" cssClass="button" />
		</td></tr>
	</s:form></tbody></table>
	
	
</div>
