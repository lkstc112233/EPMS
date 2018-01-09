<%@page import="obj.annualTable.ListOfPracticeBaseAndStudents"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" />-教育实习基地经费（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table style="width:80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				经费-教育实习基地经费
				（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
		<tr><td colspan="100" style="text-align:center;">
			<span style="float:left;margin-left:50px;">
			<s:form action="function_moneyPB_ExportAllMoneyPB_download" method="post" theme="simple">
				<s:submit value="下载所有实习基地经费表" theme="simple"
				style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
			</s:form>
			</span>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	
	<% boolean[] MoneyPBFieldDisplay=new boolean[]{
			false,false,
			true,true,true,true,true,true,true,true,false};
		int i; %>
	<table class="wtable" style="cellpadding:0;cellspacing:0;table-layout:fixed;"><tbody id="Table_2" style="display:;">
		<tr class="wtableHeader" style="height:auto;">
			<td style="width:3%;">大区</td>
			<td>基地名称</td>
			<td>实习人数<br/>(实验员数)</td>
			<% i=0; %>
			<s:iterator value="moneyPB.fields" var="__opField" status="__opFieldStatus">
				<% if(MoneyPBFieldDisplay[i++]){ %>
				<td style="word-wrap:break-word;word-break:break-all;">
				<% }else{ %>
				<td style="word-wrap:break-word;word-break:break-all;display:none;">
				<% } %>
					<s:property value="#__opField.description"/>
					<s:if test="#__opField.notNull == true">
				*
					</s:if>
				</td>
			</s:iterator>
			<td>总计</td>
			<td>回执单</td>
			<td>备注</td>
		</tr>
		<s:iterator value="practiceBaseAndMoney.list" var="__rpRow" status="__rpStatus">
		<s:iterator value="#__rpRow.list" var="__pairRow" status="__pairStatus">
			<tr class="wtableContent">
				<s:set var="_colspan" value="%{#__rpRow.size}" />
				<s:if test="#__pairStatus.index == 0">
					<td rowspan="${_colspan}" style="width:3%;background-color:white;">
						<s:property value="#__rpRow.region.name" />
					</td>
				</s:if>
				<!-- 基地名称 -->
				<td style="padding:0;background-color:lightyellow;font-weight:800;">
					<a href="<s:url action='function_moneyPB_ExportMoneyPB_download'/>?practiceBaseName=<s:property value='#__pairRow.practiceBase.name'/>">
						<s:property value="#__pairRow.practiceBase.name" />
					</a>
				</td>
				<!-- 实习人数(实验员) -->
				<!-- 各项 -->
				<% i=0; %>
				<s:iterator value="%{#__pairRow.sum.fieldsValue}" var="__Col">
					<% if(MoneyPBFieldDisplay[i++]){ %>
					<td style="word-wrap:break-word;word-break:break-all;">
					<% }else{ %>
					<td style="word-wrap:break-word;word-break:break-all;display:none;">
					<% } %>
						<s:property value="#__Col" />
					</td>
				</s:iterator>
				<!-- 总计 -->
				<td style="padding:0;background-color:lightyellow;font-weight:800;">
					<a href="<s:url action='function_moneyPB_MoneyPBInfo_display'/>?practiceBaseName=<s:property value='#__pairRow.practiceBase.name'/>">
						<s:property value="#__pairRow.sum.sum" />
					</a>
				</td>
				<!-- 回执单 -->
				<td style="padding:0;">
					<s:form action="function_moneyPB_Export_execute" method="post" theme="simple">
						<s:checkbox
						name="practiceBaseAndMoney.list[%{#__rpStatus}].list[%{#__pairStatus}].region.moneyBack"
						style="width:100%;height:100%;margin:0;" theme="simple" />
						<s:hidden name="practiceBaseName" value="%{#__pairRow.practiceBase.name}" />
						<s:hidden name="type" value="Region" />
					</s:form>
				</td>
				<!-- 备注 -->
				<td style="padding:0;">
					<s:form action="function_moneyPB_Export_execute" method="post" theme="simple">
						<s:textfield value="#__pairRow.practiceBase.remark" />
						<s:hidden name="practiceBaseName" value="%{#__pairRow.practiceBase.name}" />
						<s:hidden name="type" value="PracticeBase" />
					</s:form>
				</td>
			</tr>
		</s:iterator></s:iterator>
	</tbody></table>
	

</div>







	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>