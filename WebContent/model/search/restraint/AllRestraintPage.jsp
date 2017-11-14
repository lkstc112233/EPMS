<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
    
	
<div class="bag">
	<% request.setAttribute("actionName",request.getParameter("actionName"));%>
		
	<table style="width:100%"><tbody><s:form action="%{#request.actionName}_execute" method="get" theme="simple">
		<tr><td style="border:2px solid #0071BC;">
			<table style="width:100%"><tbody>
				<tr><td colspan="100">
					<s:iterator value="search.baseRestraint.restraint.where" var="__restraintPartRow" status="__restraintPartStatus">
						<div style="line-height:32px;float:left">
							<!-- 限制 -->
							<div style="float:left;width:120px;text-align:center">
								<s:property value="%{#__restraintPartRow.fieldName}" />
							</div><div style="float:left;text-align:center">
								<s:select
								list="search.baseRestraint.restraint.typeList"
								headerKey="" headerValue="-约束-"
								listKey="key" listValue="value" value="%{#__Row.type}"
								name="search.baseRestraint.restraint.where[%{#__restraintPartStatus.index}].type"/>
								<s:textfield value="%{#__restraintPartRow.value}"
								name="search.baseRestraint.restraint.where[%{#__restraintPartStatus.index}].value"/>
							</div>
							<div style="float:left;width:18px;">
								&nbsp;
							</div>
						</div>
						<!--<s:property value="%{#__restraintStatus.count}" /> -->
					</s:iterator>
					<s:iterator value="search.baseRestraint.restraint.order" var="__restraintOrderRow" status="__restraintOrderStatus">
						<div style="line-height:32px;float:left">
							<div style="line-height:32px;float:left">
								第<s:property value="#__restraintOrderStatus.count" />排序项
							</div><div style="float:left;text-align:center">
								<s:select
								list="allSelectFields"
								headerKey="" headerValue="-排序-"
								listKey="name" listValue="name"
								value="%{search.baseRestraint.restraint.order[#__restraintOrderStatus.index]}"
								name="search.baseRestraint.restraint.order[%{#__restraintOrderStatus.index}]"/>
							</div>
							<div style="float:left;width:18px;">
								&nbsp;
							</div>
						</div>
					</s:iterator>
				</td></tr>
			</tbody></table>
		</td></tr>
		<tr><td colspan="100" style="width:20%;">
			&nbsp;
		</td></tr>
		<tr><td colspan="100" style="width:20%;">
			<s:submit value="查询" cssClass="button" />
		</td></tr>
		<tr><td colspan="100" style="width:20%;">
			&nbsp;
		</td></tr>
	</s:form></tbody></table>
	
	
</div>
