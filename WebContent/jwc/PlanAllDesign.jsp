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
				全面确认布局规划（各实习基地学科接纳人数）（<s:property value="region.name" />）
			</div>
		</td></tr>
	</tbody></table>
	<s:form action="jwc_function_PlanAllDesign_execute" method="post" theme="simple">
		<table width="80%" border="1" cellpadding="0" cellspacing="0" style="table-layout:fixed;"><tbody>
			<tr>
				<td>&nbsp;</td>
				<s:iterator value="majors" var="__majorCol">
					<td width="5%"><s:property value="#__majorCol.name" /></td>
				</s:iterator>
			</tr>
			<s:iterator value="practiceBaseWithARegion" var="__pbsRow" status="__pbsStatus">
				<tr>
					<td width="160px" style="text-align:center"><s:property value="#__pbsRow.name" /></td>
					<s:iterator value="majors" status="__majorStatus">
						<td width="5%" style="text-align:center">
							<s:textfield theme="simple" style="width:85%"
								name="numbers[%{#__pbsStatus.index}][%{#__majorStatus.index}]"
								value="%{numbers[#__pbsStatus.index][#__majorStatus.index]}" />
						</td>
					</s:iterator>
				</tr>
			</s:iterator>
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