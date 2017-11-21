<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-时间节点管理（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>

<body>
	<center>
		<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>

<div class="bag">
	<table style="width:80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				时间节点管理（<s:property value="annual.year" />年）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	<table class="wtable">
		<s:form action="sudo_TimeManager_execute" method="post" cssClass="myform" theme="simple">
			<tr class="wtableHeader">
				<td style="width:10px">序号</td>
				<td>project(<s:property value="annual.year" />)</td>
				<td>time start</td>
				<td>time end</td>
			</tr>
			<s:iterator value="times" var="__tableRow" status="__status">
				<tr class="wtableContent">
					<td style="width:10px">
						<s:property value="%{#__tableRow.id}" />
						<s:hidden name="times[%{#__status.index}].id"
						value="%{id}" theme="simple"/>
					</td>
					<td style="text-align:left;">
						<s:property value="%{#__tableRow.project}" />
						<s:hidden name="times[%{#__status.index}].project"
						value="%{#__tableRow.project}"  theme="simple"/>
					</td>
					<td style="width:180px;padding:0;">
						<s:textfield name="times[%{#__status.index}].time1" theme="simple" style="width:100%;height:100%;border:0;text-align:center;">
							<s:param name="value">
								<s:date name="%{#__tableRow.time1}" format="yyyy-MM-dd HH:mm:ss"/>
							</s:param>
						</s:textfield>
					</td>
					<td style="width:180px;padding:0;">
						<s:textfield name="times[%{#__status.index}].time2" theme="simple" style="width:100%;height:100%;border:0;text-align:center;">
							<s:param name="value">
								<s:date name="%{#__tableRow.time2}" format="yyyy-MM-dd HH:mm:ss"/>
							</s:param>
						</s:textfield>
					</td>
				</tr>
			</s:iterator>
			<tr>
				<td colspan="4">
					<s:submit value="提交修改" cssClass="button" theme="simple" />
				</td>
			</tr>
			<s:hidden name="annual.year" value="%{annual.year}" theme="simple" />
		</s:form>
		
	</table>
</div>

		<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
	</center>
</body>
</html>