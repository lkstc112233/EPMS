<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-全面确认布局规划（各实习基地学科接纳人数）（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table width="80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				全面确认布局规划（各实习基地学科接纳人数）（<s:property value="annual.year" />）
			</div>
		</td></tr>
	</tbody></table>
	<s:form action="jwc_function_PlanAllDesign_execute" method="post" theme="simple">
		<table width="80%" border="1" cellpadding="0" cellspacing="0" style="table-layout:fixed;"><tbody>
			<tr>
				<td style="text-align:center;width:3%;">大区</td>
				<td style="text-align:center">基地名称</td>
				<s:iterator value="majors" var="__majorCol">
					<td style="text-align:center;width:5%;"><s:property value="#__majorCol.name" /></td>
				</s:iterator>
			</tr>
			<s:iterator value="regionAndPracticeBase.list" var="__regionRow" status="__regionStatus">
			<s:iterator value="#__regionRow.practiceBases" var="__practiceBaseRow" status="__practiceBaseStatus">
				<tr>
					<s:if test="#__practiceBaseStatus.index == 0">
						<s:set var="_colspan" value="%{#__regionRow.size}" />
						<td rowspan="${_colspan}" style="text-align:center;width:3%;">
							<s:property value="#__regionRow.region.name" />
						</td>
					</s:if>
					<td width="160px" style="text-align:center"><s:property value="#__practiceBaseRow.name" /></td>
					<s:iterator value="majors" status="__majorStatus">
						<td style="text-align:center;width:5%;">
							<s:textfield theme="simple" style="width:85%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;"
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
	
	
	<table style="width:180px;border:0;cellspacing:0;cellpadding:0"><tbody>
		<tr><td height="100px" width="100%" colspan="3" valign="top" /></tr>
		<tr><td><a href='JavaScript:history.back(1)' class="button">返回</a></td></tr>
	</tbody></table>
</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>