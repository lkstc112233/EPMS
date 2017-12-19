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
				分配媒体设备（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	<table class="wtable" style="cellpadding:0;cellspacing:0;table-layout:fixed;"><tbody>
		<tr class="wtableHeader" style="border:0;min-height:80px;height:80px;">
			<td style="width:12px;">大区</td>
			<td>基地名称</td>
			<td>设备总计</td>
			<s:iterator value="majors" var="__majorCol">
				<td style="width:20px;"><s:property value="#__majorCol.subject" /></td>
			</s:iterator>
		</tr>
		<!-- ###总数量### -->
		<tr class="wtableContent">
			<td colspan="2" style="font-weight:900;background:rgba(255,234,46,0.5);">
				<s:property value="#__regionRow.region.name"/>数字媒体设备数量
			</td>
			<td style="font-weight:900;background:rgba(255,234,46,0.5);">
				<s:property value="mediaCount"/>
			</td>
			<s:iterator value="mediaMajorCounts" var="__count">
				<td style="width:20px;padding:0px;font-weight:700;background:rgba(255,234,46,0.5);">
					<s:property value="#__count" />
				</td>
			</s:iterator>
			<td style="background:rgba(255,234,46,0.5);"></td>
		</tr>
		<!-- ###媒体设备规划### -->
		<s:iterator value="regionAndPracticeBase.list" var="__regionRow" status="__regionStatus">
			<s:iterator value="#__regionRow.practiceBases" var="__practiceBaseRow" status="__practiceBaseStatus">
				<tr class="wtableContent">
					<s:if test="#__practiceBaseStatus.index == 0">
						<s:set var="_colspan" value="%{#__regionRow.size}" />
						<td rowspan="${_colspan}" style="width:3%;">
							<s:property value="#__regionRow.region.name" />
						</td>
					</s:if>
					<td style="text-align:left;padding-left:10px;">
						<s:property value="#__practiceBaseRow.name" />
					</td>
					<td>
						<s:property value="mediaPracticeBaseCounts[#__regionStatus.index][#__practiceBaseStatus.index]" />
					</td>
					<s:iterator value="majors" status="__majorStatus">
						<td style="width:20px;padding:0px;">
							<s:form action="function_PlanMedia_create" method="post" theme="simple">
								<s:hidden name="clickIndex[0]" value="%{#__majorStatus.index}" />
								<s:hidden name="clickIndex[1]" value="%{#__regionStatus.index}" />
								<s:hidden name="clickIndex[2]" value="%{#__practiceBaseStatus.index}" />
								<s:if test="numbers[#__majorStatus.index][#__regionStatus.index][#__practiceBaseStatus.index]==0">
									<s:if test="media[#__majorStatus.index][#__regionStatus.index][#__practiceBaseStatus.index] == true">
										<s:submit value="&nbsp;"
										theme="simple"
										style="width:100%;height:100%;" />
									</s:if><s:else>
										<s:submit value="&nbsp;"
										theme="simple"
										style="width:100%;height:100%;color:red;" />
									</s:else>
								</s:if><s:else>
									<s:if test="media[#__majorStatus.index][#__regionStatus.index][#__practiceBaseStatus.index] == true">
										<s:submit value="%{numbers[#__majorStatus.index][#__regionStatus.index][#__practiceBaseStatus.index]}"
											theme="simple"
											style="width:100%;height:100%;" />
									</s:if><s:else>
										<s:submit value="%{numbers[#__majorStatus.index][#__regionStatus.index][#__practiceBaseStatus.index]}"
											theme="simple"
											style="width:100%;height:100%;color:red;" />
									</s:else>
								</s:else>
							</s:form>
						</td>
					</s:iterator>
				</tr>
			</s:iterator>
		</s:iterator>
	</tbody></table>
	<table width="300px"><tbody>
		<tr><td>
			&nbsp;<s:submit value="提交修改" cssClass="button"/>
		</td></tr>
	</tbody></table>
	
</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>