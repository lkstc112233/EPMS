<%@page import="obj.annualTable.ListOfPracticeBaseAndStudents"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" />-导出督导任务书等相关数据（<s:property value="annual.year" />年）</title>
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
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
		<s:if test="schoolName == '教务处'">
			<tr><td colspan="100" style="text-align:center;">
				<span style="float:left;margin-left:50px;">
				<s:form action="function_teacher_ExportAllSuperviseList_download" method="post" theme="simple">
					<s:submit value="督导任务列表" theme="simple"
					style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
				</s:form>
				</span>
			</td></tr>
			<tr><td colspan="100" style="width:20%;border:0;height:25px;">
				&nbsp;
			</td></tr>
		</s:if>
		<tr><td colspan="100" style="text-align:center;">
			<table class="wtable" style="width:100%;"><tbody>
			<tr class="wtableHeader"><td style="border:double 6px #0071bc;">
			<ul class="listContent"><li onclick="document.getElementById('Table_1').style.display=document.getElementById('Table_1').style.display.length<=0?'none':''"
			style="font-size:15px;font-weight:700;background:inherit;">
				督导老师一览&nbsp;
				<span class="time">（点击下列按钮下载该督导老师全部督导任务书）</span>
			</li></ul>
			</td></tr>
			</tbody></table>
			<table class="wtable" style="width:100%;"><tbody style="display:;" id="Table_1">
			<s:iterator value="supervisors" var="__schoolRow" status="__schoolStatus">
				<s:if test="#__schoolRow.isEmpty() == false">
					<tr class="wtableContent">
						<td style="background:white;text-align:left;font-size:15px;font-weight:700;width:150px;">
							<s:property value="schools[#__schoolStatus.index].name" />
							<span style="float:right;">
							：
							</span>
						</td>
						<td style="background:white;height:30px">
						<s:iterator value="#__schoolRow" var="__supervisorRow">
							<s:form action="function_teacher_ExportSupervisorAllMandate_download" method="post" theme="simple">
								<s:hidden name="supervisorId" value="%{#__supervisorRow.id}" theme="simple"/>
								<s:submit value="%{#__supervisorRow.name}" theme="simple"
								style="float:left;margin-right:10px;"
								/>
							</s:form>
						</s:iterator>
						</td>
					<!--<td colspan="100" style="background:white;padding:0;color:#666;font-size:8px;align-text:right;">
							[nobody else]
						</td>-->
					</tr>
				</s:if>
			</s:iterator>
			</tbody></table>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	
	<table class="wtable"><tbody>
	<tr class="wtableHeader"><td style="border:double 6px #0071bc;">
	<ul class="listContent"><li onclick="document.getElementById('Table_2').style.display=document.getElementById('Table_2').style.display.length<=0?'none':''"
	style="font-size:15px;font-weight:700;background:inherit;">
		总领队及督导任务表&nbsp;
		<span class="time">（点击大区或基地名称下载实习基地信息，点击督导老师姓名下载督导任务书）</span>
	</li></ul>
	</td></tr>
	</tbody></table>
	<table class="wtable" style="cellpadding:0;cellspacing:0;table-layout:fixed;"><tbody id="Table_2" style="display:;">
		<tr class="wtableHeader" style="height:auto;">
			<td style="width:3%;">大区</td>
			<td>基地名称
			<!--<span style="color:gray;font-weight:100;font-size:12px;float:right;">
				点击下载该实习基地信息</span>-->
			</td>
			<td>总领队老师
			</td>
			<td>入校督导老师
			<!--<span style="color:gray;font-weight:100;font-size:12px;float:right;">
				点击下载督导任务书</span>-->
			</td>
			<td>中期督导老师
			<!--<span style="color:gray;font-weight:100;font-size:12px;float:right;">
				点击下载督导任务书</span>-->
			</td>
			<td>返校督导老师
			<!--<span style="color:gray;font-weight:100;font-size:12px;float:right;">
				点击下载督导任务书</span>-->
			</td>
		</tr>
		<s:iterator value="list.list" var="__rpRow" status="__rpStatus">
		<s:iterator value="#__rpRow.list" var="__pairRow" status="__pairStatus">
			<tr class="wtableContent">
				<s:set var="_colspan" value="%{#__rpRow.size}" />
				<s:if test="#__pairStatus.index == 0">
					<td rowspan="${_colspan}" style="width:3%;background-color:white;">
						<s:property value="#__rpRow.t.region.name" />
					</td>
				</s:if>
				<!-- 基地名称 -->
					<td style="padding:0;background-color:lightyellow;font-weight:800;">
						<a href="<s:url action='function_teacher_ExportPracticeBaseInfomation_download'/>?practiceBaseName=<s:property value='#__pairRow.practiceBase.name'/>">
							<s:property value="#__pairRow.practiceBase.name" />
						</a>
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
				<s:iterator value="#__pairRow.supervisors" var="__supervisor" status="__typeStatus">
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





	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>