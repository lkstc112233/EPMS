<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-确认实习大区信息（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table width="80%"><tbody>
	<s:iterator value="regionAndPracticeBase.list" var="__Row" status="__Status">
		<!-- ########### 大区信息 ########### -->
		<tr><td colspan="100">
			<div class="listHeader">
				<s:if test="#__Row.region == null">
					未分配大区的实习基地
				</s:if><s:else>
					大区（<s:property value="%{#__Row.region.name}" />）
					<div class="right">
						<a href="<s:url action='jwc_function_RegionInfo_display'/>?region.name=<s:property value='%{#__Row.region.name}'/>"
							style="color:#fff;text-decoration:none;" class="buttonInline">
						修改大区信息</a>
					</div>
				</s:else>
			</div>
		</td></tr>
		<!-- ########### 表头 ########### -->
		<tr class="mytableTitle" style="visibility:hidden;">
			<td colspan="2">&nbsp;</td>
			<s:iterator value="practiceBaseAllFieldsNameString" var="__Col" status="__colStatus">
				<td style="word-wrap:break-word;word-break:break-all;text-align:center;">
					<s:property value="#__Col"/>
					<s:if test="practiceBaseAllFieldsKeyBoolean[#__colStatus.index] == true">
					*
					</s:if>
				</td>
			</s:iterator>
		</tr>
		<tr class="mytableTitle">
			<td style="width:13px;text-align:center;">选择</td>
			<td style="width:13px;text-align:center;">序号</td>
			<s:iterator value="practiceBaseAllFieldsDescriptionString" var="__Col" status="__colStatus">
				<td style="word-wrap:break-word;word-break:break-all;text-align:center;">
					<s:property value="%{#__Col}"/>
					<s:if test="practiceBaseAllFieldsKeyBoolean[#__colStatus.index] == true">
					*
					</s:if>
				</td>
			</s:iterator>
		</tr>
		<!-- ########### 实习基地列表 ########### -->
		<s:if test="#__Row.region == null">
			<s:form action="jwc_function_RegionArrangement_execute" method="post" theme="simple">
				<s:iterator value="#__Row.practiceBases" var="__pbsRow" status="__pbsStatus"><tr>
					<!-- 选择 -->
					<td class="listContent" style="width:13px;text-align:center;">
						<s:checkbox name="checkBox[%{#__pbsStatus.index}]" id="%{#__Status.index}_%{#__pbsStatus.index}"/>
					</td>
					<!-- 序号 -->
					<td class="listContent" style="width:13px;text-align:center;">
						<s:property value="%{#__pbsStatus.count}" />
					</td>
					<!-- 内容 -->
					<s:iterator value="%{#__pbsRow.allFieldsValueString}" var="__Col">
						<td style="text-align:center;">
							<s:property value="#__Col" />
						</td>
					</s:iterator>
				</tr></s:iterator>
				<tr><td colspan="100" style="text-align:center;border-bottom:#000 solid 3px;border-top: double;height:30px">
					将选中基地放入大区（若不存在则新建）：
					<s:textfield name="regionName" />
					<s:submit value="放入新建" cssClass="buttonInline" style="padding-top:1px;"/>
				</td></tr>
				<tr><td height="20px" width="100%" colspan="100" valign="top" /></tr>
				<s:token />
			</s:form>
		</s:if><s:else>
			<s:form action="jwc_function_RegionArrangement_delete" method="post" theme="simple">
				<s:iterator value="#__Row.practiceBases" var="__pbsRow" status="__pbsStatus"><tr>
					<!-- 选择 -->
					<td class="listContent" style="width:13px;text-align:center;">
						<s:checkbox name="checkBox[%{#__pbsStatus.index}]" id="%{#__Status.index}_%{#__pbsStatus.index}"/>
					</td>
					<!-- 序号 -->
					<td class="listContent" style="width:13px;text-align:center;">
						<s:property value="%{#__pbsStatus.count}" />
					</td>
					<!-- 内容 -->
					<s:iterator value="%{#__pbsRow.allFieldsValueString}" var="__Col">
						<td style="text-align:center;">
							<s:property value="#__Col" />
						</td>
					</s:iterator>
				</tr></s:iterator>
				<tr><td colspan="100" style="text-align:center;border-bottom:#000 solid 3px;border-top: double;height:30px">	
					从该大区移除
					<s:hidden name="regionName" value="%{#__Row.region.name}" />
					<s:submit value="移除" cssClass="buttonInline" style="padding-top:1px;"/>
				</td></tr>
				<tr><td height="20px" width="100%" colspan="100" valign="top" /></tr>
				<s:token />
			</s:form>
		</s:else>
	</s:iterator>
	</tbody></table>
	


	<table style="width:180px;border:0;cellspacing:0;cellpadding:0"><tbody>
		<tr><td height="100px" width="100%" colspan="3" valign="top" /></tr>
		<tr><td><a href='JavaScript:history.back(1)' class="button">返回</a></td></tr>
	</tbody></table>
</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>