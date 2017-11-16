<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" />-全面确认布局规划（各实习基地学科接纳人数）（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table style="width:80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				全面确认布局规划（各实习基地学科接纳人数）（<s:property value="annual.year" />）
			</div>
		</td></tr>
	</tbody></table>
	<s:form action="function_PlanAllDesign_execute" method="post" theme="simple">
		<table width="80%" border="1" cellpadding="0" cellspacing="0" style="table-layout:fixed;"><tbody>
			<tr class="mytableTitle">
				<td style="width:3%;">大区</td>
				<td >基地名称</td>
				<s:iterator value="majors" var="__majorCol">
					<td style="width:5%;"><s:property value="#__majorCol.name" /></td>
				</s:iterator>
			</tr>
			<s:iterator value="regionAndPracticeBase.list" var="__regionRow" status="__regionStatus">
			<s:iterator value="#__regionRow.practiceBases" var="__practiceBaseRow" status="__practiceBaseStatus">
				<tr class="mytable">
					<s:if test="#__practiceBaseStatus.index == 0">
						<s:set var="_colspan" value="%{#__regionRow.size}" />
						<td rowspan="${_colspan}" style="width:3%;">
							<s:property value="#__regionRow.region.name" />
						</td>
					</s:if>
					<td style="width:160px;text-align:left;padding-left:10px;">
						<s:property value="#__practiceBaseRow.name" />
					</td>
					<s:iterator value="majors" status="__majorStatus">
						<td style="width:5%;padding:0px;">
							<s:textfield theme="simple"
								style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;"
								name="numbers[%{#__majorStatus.index}][%{#__regionStatus.index}][%{#__practiceBaseStatus.index}]"
								value="%{numbers[#__majorStatus.index][#__regionStatus.index][#__practiceBaseStatus.index]}" />
						</td>
					</s:iterator>
				</tr>
			</s:iterator></s:iterator>
		</tbody></table>
		<table width="300px"><tbody>
			<tr><td>
				&nbsp;<s:submit value="提交修改" cssClass="button"/>
			</td></tr>
		</tbody></table>
	</s:form>
	
</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>