<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
    
	
<div class="bag">
	<% request.setAttribute("actionName",request.getParameter("actionName"));%>
		
	<table style="width:100%"><tbody><s:form action="%{#request.actionName}_execute" method="get" theme="simple">
		<tr><td style="border:2px solid #0071BC;">
			<s:iterator value="search.restraint.triple" var="__Row" status="__Status">
				<div style="line-height:32px;float:left">
				<!-- 限制 -->
					<div style="float:left;width:120px;text-align:center">
						<s:property value="%{#__Row.fieldName}" />
					</div><div style="float:left;text-align:center">
						<s:select list="search.restraint.restraintTypeList" headerKey="" headerValue="-约束-"
						listKey="key" listValue="value" value="%{#__Row.type}"
						name="search.restraint.triple[%{#__Status.index}].type"/>
						<s:textfield value="%{#__Row.value}"
						name="search.restraint.triple[%{#__Status.index}].value"/>
					</div>
					<div style="float:left;width:18px;">
						&nbsp;
					</div>
				</div>
				<!--<s:property value="%{#__restraintStatus.count}" /> -->
			</s:iterator>
			<div style="line-height:32px;float:left">
				<div style="line-height:32px;float:left">
					排序
				</div><div style="float:left;text-align:center">
					<s:select list="search.restraint.triple" headerKey="" headerValue="-约束-"
					listKey="fieldName" listValue="fieldName"
					value="%{search.restraint.orderField}"
					name="search.restraint.orderField"/>
					&nbsp;
				</div>
			</div>
		</td></tr>
		<tr><td colspan="1" style="width:20%;">
			&nbsp;
		</td></tr>
		<tr><td colspan="1" style="width:20%;">
			<s:submit value="查询" cssClass="button" />
		</td></tr>
	</s:form></tbody></table>
	
	
</div>
