<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" />-安排总领队及督导任务学科规划（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table style="width:80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				安排总领队及督导任务学科规划（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	
	<table class="wtable" style="cellpadding:0;cellspacing:0;table-layout:fixed;"><tbody>
		<tr class="wtableHeader">
			<td style="width:3%;">大区</td>
			<td>基地名称</td>
			<td>总领队老师</td>
			<s:iterator value="superviseTypeNameList" var="__typeName">
				<td><s:property value="#__typeName" />老师</td>
			</s:iterator>
			<td>入校时间</td>
			<td>入校地点</td>
			<td style="width:40px;">是否住宿</td>
			<td>备注</td>
			<td>保存</td>
		</tr>
		<s:iterator value="list.list" var="__regionRow" status="__regionStatus">
		<s:iterator value="#__regionRow.list" var="__practiceBaseRow" status="__practiceBaseStatus">
		<s:form action="function_RegionInfo_execute" method="post" theme="simple">
			<tr class="wtableContent">
				<s:set var="_colspan" value="%{#__regionRow.size}" />
				<s:if test="#__practiceBaseStatus.index == 0">
					<td rowspan="${_colspan}" style="width:3%;">
						<s:property value="#__regionRow.t.name" />
					</td>
				</s:if>
				<td style="width:160px;text-align:left;padding-left:10px;">
					<s:if test="#__practiceBaseRow.practiceBase.status">
						<span style="color:red;">
							<s:property value="#__practiceBaseRow.practiceBase.name" />
						</span>
					</s:if><s:else>
						<s:property value="#__practiceBaseRow.practiceBase.name" />
					</s:else>
				</td>
				<!-- 总领队 -->
				<s:if test="#__practiceBaseStatus.index == 0">
					<td rowspan="${_colspan}" style="padding:0;">
						<s:select list="innerPersons"
							listKey="id" listValue="name"
							headerKey="" headerValue="-无-"
							name="list.list[%{#__regionStatus.index}].list[%{#__practiceBaseStatus.index}].t.leaderId"
							value="%{#__regionRow.t.leaderId}"
							style="text-align:center;width:100%;height:100%;border:0;" theme="simple"/>
					</td>
				</s:if>
				<!-- 督导 -->
				<s:iterator value="superviseTypeList" status="__typeStatus">
					<td style="padding:0;">
						<s:select list="innerPersons"
								listKey="id" listValue="name"
								headerKey="" headerValue="-无-"
								name="supervises[%{#__typeStatus.index}][%{#__regionStatus.index}][%{#__practiceBaseStatus.index}].supervisorId"
								value="%{supervises[#__typeStatus.index][#__regionStatus.index][#__practiceBaseStatus.index].supervisorId}"
								style="text-align:center;width:100%;height:100%;border:0;" theme="simple"/>
					</td>
				</s:iterator>
				<!-- 入校时间 -->
				<td><s:textfield theme="simple"
					style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;"
					name="list.list[%{#__regionStatus.index}].list[%{#__practiceBaseStatus.index}].t.enterPracticeBaseTime">
						<s:param name="value">
							<s:date name="%{#__practiceBaseRow.t.enterPracticeBaseTime}"
							format="yyyy-MM-dd"/>
						</s:param>
					</s:textfield>
				</td>
				<!-- 入校地点 -->
				<td><s:textfield theme="simple"
					style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;"
					name="list.list[%{#__regionStatus.index}].list[%{#__practiceBaseStatus.index}].t.enterPracticeBasePlace" />
				</td>
				<!-- 是否住宿 -->
				<td style="width:40px;"><s:checkbox theme="simple"
					style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;"
					name="list.list[%{#__regionStatus.index}].list[%{#__practiceBaseStatus.index}].t.accommodation" />
				</td>
				<!-- 备注 -->
				<td style="padding:0;word-wrap:break-word;word-break:break-all;">
					<s:textarea
					name="list.list[%{#__regionStatus.index}].list[%{#__practiceBaseStatus.index}].t.remark"
					style="margin:0;padding:0;resize:none;border:0;width:100%;heigth:100%;" theme="simple" />
				</td>
				<!-- 保存 -->
				<td>
					<s:hidden name="practiceBaseName" value="%{#__practiceBaseRow.practiceBase.name}" />
					<s:submit value="保存修改" />
				</td>
			</tr>
		</s:form>
		</s:iterator>
		</s:iterator>
	</tbody></table>
	
	
</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>