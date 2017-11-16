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
				分配实习生到实习基地（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	
	<table width="80%"><tbody>
	<s:iterator value="practiceBaseAndStudents.list" var="__Row" status="__Status">
		<!-- ########### 大区信息 ########### -->
		<tr><td colspan="100">
			<div class="listHeader">
				<s:if test="#__Row.practiceBase == null">
					未分配实习基地的实习生
				</s:if><s:else>
					实习基地（<s:property value="%{#__Row.practiceBase.name}" />）
				</s:else>
			</div>
		</td></tr>
		<!-- ########### 表头 ########### -->
		<tr class="wtableHeader">
			<td style="width:13px;">选择</td>
			<td style="width:13px;">序号</td>
			<s:iterator value="student.fields" var="__opField" status="__opFieldStatus">
				<td style="word-wrap:break-word;word-break:break-all;">
					<s:property value="#__opField.description"/>
					<s:if test="#__opField.notNull == true">
					*
					</s:if>
				</td>
			</s:iterator>
		</tr>
		<!-- ########### 实习基地列表 ########### -->
		<s:if test="#__Row.practiceBase == null">
			<s:form action="function_StudentArrangeIntoPracticeBase_execute" method="post" theme="simple">
				<s:iterator value="#__Row.students" var="__studentRow" status="__studentStatus">
				<tr class="wtableContent">
					<!-- 选择 -->
					<td style="width:13px;">
						<s:checkbox name="checkBox[%{#__studentStatus.index}]" id="%{#__Status.index}_%{#__studentStatus.index}"/>
					</td>
					<!-- 序号 -->
					<td style="width:13px;">
						<s:property value="%{#__studentStatus.count}" />
					</td>
					<!-- 内容 -->
					<s:iterator value="%{#__studentRow.fieldsValue}" var="__Col">
						<td>
							<s:property value="#__Col" />
						</td>
					</s:iterator>
				</tr></s:iterator>
				<tr class="wtableContent">
					<td colspan="100" style="border-bottom:#000 solid 3px;border-top: double;height:30px">
						将选中实习生分配至实习基地：
						<!-- s:textfield name="regionName" -->
						<s:select list="practiceBases"
						listKey="name" listValue="description"
						headerKey="" headerValue="-无-"
						name="practiceBaseName" />
						<s:submit value="放入" cssClass="buttonInline" style="padding-top:1px;"/>
					</td>
				</tr>
				<tr><td height="20px" width="100%" colspan="100" valign="top" /></tr>
				<s:token />
			</s:form>
		</s:if><s:else>
			<s:form action="function_StudentArrangeIntoPracticeBase_delete" method="post" theme="simple">
				<s:iterator value="#__Row.students" var="__studentRow" status="__studentStatus">
				<tr class="wtableContent">
					<!-- 选择 -->
					<td style="width:13px;">
						<s:checkbox name="checkBox[%{#__studentStatus.index}]" id="%{#__Status.index}_%{#__studentStatus.index}"/>
					</td>
					<!-- 序号 -->
					<td style="width:13px;">
						<s:property value="%{#__studentStatus.count}" />
					</td>
					<!-- 内容 -->
					<s:iterator value="%{#__studentRow.fieldsValue}" var="__Col">
						<td>
							<s:property value="#__Col" />
						</td>
					</s:iterator>
				</tr></s:iterator>
				<tr class="wtableContent">
					<td colspan="100" style="border-bottom:#000 solid 3px;border-top: double;height:30px">	
						从该基地移出
						<s:hidden name="practiceBaseName" value="%{#__Row.practiceBase.name}" />
						<s:submit value="移出" cssClass="buttonInline" style="padding-top:1px;"/>
					</td>
				</tr>
				<tr><td height="20px" width="100%" colspan="100" valign="top" /></tr>
				<s:token />
			</s:form>
		</s:else>
	</s:iterator>
	</tbody></table>
	


</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>