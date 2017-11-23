<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" />-确认实习大区信息（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table class="wtable"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				实习大区（<s:property value="region.name" />）
			</div>
		</td></tr>
		<s:form action="function_RegionInfo_execute" method="post" cssClass="myform">
			<tr><td>
				<s:textfield label="新的大区名称（如无需修改可不填）" name="newRegionName"
				value="%{region.name}"/>
			</td></tr>
			<tr><td>
				<s:select label="总领队老师"
				list="innerPersons"
				listKey="id" listValue="description"
				headerKey="" headerValue="-无-"
				name="region.leaderId"
				value="%{region.leaderId}" />
			</td></tr>
			<tr><td>
				<s:textfield label="入校时间" name="region.enterPracticeBaseTime" />
			</td></tr>
			<tr><td>
				<s:textfield label="入校地点" name="region.enterPracticeBasePlace" />
			</td></tr>
			<tr><td>
				<s:textfield label="动员会时间" name="region.mobilizationTime" />
			</td></tr>
			<tr><td>
				<s:textfield label="动员会地点" name="region.mobilizationPlace" />
			</td></tr>
			<tr><td>
				<s:textarea label="备注信息" name="region.remark"
				style="height:70px;" />
			</td></tr>
			<tr><td>
				<s:hidden name="region.year" value="%{annual.year}" />
				<s:hidden name="region.name" value="%{region.name}" />
				<s:hidden name="region.orderId" value="%{region.orderId}" />
				<s:submit value="提交修改" cssClass="button"/>
			</td></tr>
			<s:token />
		</s:form>
	</tbody></table>
	
	
</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>