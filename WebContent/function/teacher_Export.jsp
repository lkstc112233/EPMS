<%@page import="obj.annualTable.ListOfPracticeBaseAndStudents"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" />-分配实习生到实习基地（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table style="width:80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				<s:select id="SchoolName" name="schoolName" list="schools"
				listKey="name" listValue="description"
				theme="simple" cssClass="title_button" style="margin-bottom:2px;width:auto;"
				onchange="window.location.href=window.location.href.substring(0,(
				window.location.href.lastIndexOf('/')<0?window.location.length:window.location.href.lastIndexOf('/'))
				)+'/function_teacher_Export_display.action?schoolName='
				+this.value"
				/>
				督导任务
				（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<s:if test="schoolName == '教务处'">
			<tr><td colspan="100" style="width:20%;border:0;height:25px;">
				&nbsp;
			</td></tr>
			<tr><td colspan="100" style="text-align:center;">
				<span style="float:left;margin-left:50px;">
				<s:form action="function_teacher_ExportAllSuperviseList_download" method="post" theme="simple">
					<s:hidden name="jumpURL" value="function_teacher_Export_display.action" theme="simple"/>
					<s:submit value="督导任务列表" theme="simple"
					style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
				</s:form>
				</span>
			</td></tr>
		</s:if>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
		
	<table class="wtable" style="cellpadding:0;cellspacing:0;table-layout:fixed;"><tbody>
		<tr class="wtableHeader">
			<td style="width:3%;">大区</td>
			<td>基地名称</td>
			<td>总领队老师</td>
			<td>入校督导老师</td>
			<td>中期督导老师</td>
			<td>返校督导老师</td>
		</tr>
		<s:iterator value="regionAndPracticeBaseAndInnerPerson.list" var="__rpRow" status="__rpStatus">
		<s:iterator value="#__rpRow.list" var="__pairRow" status="__pairStatus">
			<tr class="wtableContent">
				<s:set var="_colspan" value="%{#__rpRow.size}" />
				<s:if test="#__pairStatus.index == 0">
					<td rowspan="${_colspan}" style="width:3%;background-color:white;">
						<s:property value="#__rpRow.region.name" />
					</td>
				</s:if>
				<td style="width:160px;text-align:left;padding-left:10px;background-color:white;">
					<s:property value="#__pairRow.practiceBase.name" />
				</td>
				<!-- 总领队 -->
				<s:if test="#__pairStatus.index == 0">
					<s:if test="schoolName == #__pairRow.leader.school">
						<td rowspan="${_colspan}" style="padding:0;background-color:lightyellow;font-weight:800;">
							<s:property value="#__pairRow.leader.name" />
						</td>
					</s:if><s:else>
						<td rowspan="${_colspan}" style="padding:0;background-color:white;">
							<s:property value="#__pairRow.leader.name" />
						</td>
					</s:else>
				</s:if>
				<!-- 督导 -->
				<s:iterator value="#__pairRow.supervisor" var="__supervisor" status="__typeStatus">
					<s:if test="schoolName == #__supervisor.school">
						<td style="padding:0;background-color:lightyellow;font-weight:800;">
							<a href="<s:url action='function_teacher_ExportSupervisorMandate_download'/>?superviseIndex=<s:property value='#__typeStatus.index'/>&practiceBaseName=<s:property value='#__pairRow.practiceBase.name'/>">
								<s:property value="#__supervisor.name" />
							</a>
						</td>
					</s:if><s:elseif test="schoolName == '教务处'">
						<td style="padding:0;background-color:white;">
							<a href="<s:url action='function_teacher_ExportSupervisorMandate_download'/>?superviseIndex=<s:property value='#__typeStatus.index'/>&practiceBaseName=<s:property value='#__pairRow.practiceBase.name'/>">
								<s:property value="#__supervisor.name" />
							</a>
						</td>
					</s:elseif><s:else>
						<td style="padding:0;background-color:white;">
							<s:property value="#__supervisor.name" />
						</td>
					</s:else>
				</s:iterator>
			</tr>
		</s:iterator></s:iterator>
	</tbody></table>
	

</div>








<% //跳转到当前操作的条目,choose[0]表示块index、choose[1]表示行号
Object tmp=pageContext.findAttribute("choose");
String x=null;
if(tmp!=null){
	if(tmp instanceof String[] && ((String[])tmp).length>=3)
		x=((String[])tmp)[1];//[1]表示学生
}
if(x!=null && !x.isEmpty()){ %>
	<script>
		var jumpX=document.getElementById("choose<%=x%>");
		if(jumpX!=null)
			jumpX.scrollIntoView();
	</script>
<% } %>



	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>