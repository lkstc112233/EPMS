<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-指定总领队及督导老师（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table style="width:80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				指定总领队及督导老师（<s:property value="annual.year" />）
			</div>
		</td></tr>
	</tbody></table>
	<s:form action="jwy_function_RegionLeaderAndSupervisorArragement_execute" method="post" theme="simple">
		<table width="80%" border="1" cellpadding="0" cellspacing="0" style="table-layout:fixed;"><tbody>
			<tr class="mytableTitle">
				<td style="width:3%;">大区</td>
				<td>基地名称</td>
				<td>总领队老师</td>
				<td>入校督导老师</td>
				<td>中期督导老师</td>
				<td>返校督导老师</td>
			</tr>
			<s:iterator value="regionAndPracticeBase.list" var="__regionRow" status="__regionStatus">
			<s:iterator value="#__regionRow.practiceBases" var="__practiceBaseRow" status="__practiceBaseStatus">
				<tr class="mytable">
					<s:set var="_colspan" value="%{#__regionRow.size}" />
					<s:if test="#__practiceBaseStatus.index == 0">
						<td rowspan="${_colspan}" style="width:3%;">
							<s:property value="#__regionRow.region.name" />
						</td>
					</s:if>
					<td style="width:160px;text-align:left;padding-left:10px;">
						<s:property value="#__practiceBaseRow.name" />
					</td>
					<!-- 总领队 -->
					<s:if test="#__practiceBaseStatus.index == 0">
						<td rowspan="${_colspan}">
							<s:if test="innerPersonLeaders[#__regionStatus.index].isSameSchool">
								<s:select list="innerPersons"
									listKey="id" listValue="name"
									headerKey="" headerValue="-无-"
									name="regionAndPracticeBase.list[%{#__regionStatus.index}].region.leaderId"
									value="%{#__regionRow.region.leaderId}" />
							</s:if><s:else>
								<s:property value="%{innerPersonLeaders[#__regionStatus.index].name}" />
							</s:else>
						</td>
					</s:if>
					<!-- 督导 -->
					<s:iterator value="superviseTypeList" status="__typeStatus"><td>
						<s:if test="innerPersonSupervisors[#__typeStatus.index][#__regionStatus.index][#__practiceBaseStatus.index].isSameSchool">
							<s:select list="innerPersons"
									listKey="id" listValue="name"
									headerKey="" headerValue="-无-"
									name="supervises[%{#__typeStatus.index}][%{#__regionStatus.index}][%{#__practiceBaseStatus.index}].supervisorId"
									value="%{supervises[#__typeStatus.index][#__regionStatus.index][#__practiceBaseStatus.index].supervisorId}" />
						</s:if><s:else>
							<s:property value="%{innerPersonSupervisors[#__typeStatus.index][#__regionStatus.index][#__practiceBaseStatus.index].name}" />
						</s:else>
					</td></s:iterator>
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