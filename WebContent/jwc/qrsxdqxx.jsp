<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-确认实习大区信息（<s:property value="year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="../common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table width="80%"><tbody>
		<s:iterator value="regionAndPracticeBase.list" var="__Row" status="__Status">
		<s:if test="#__Row.region == null">
			<!-- ######### 未分配大区的基地 ######## -->
			<tr><td colspan="2">
				<div class="listHeader">
					未分配大区的实习基地
				</div>
			</td></tr>
			<s:form action="jwc_function_qrsxdqxx_execute" method="post" theme="simple" enctype="multipart/form-data">
				<!-- ###### 表头 ###### -->
				<tr class="mytableTitle">
					<td>选择</td>
					<td>学校名称</td>
				</tr>
				<s:iterator value="#__Row.practiceBases" var="__pbsRow" status="__pbsStatus">
					<tr><td class="listContent" style="width:13px;text-align:center;">
						<s:checkbox name="checkBox[%{#__pbsStatus.index}]" />
					</td><td style="text-align:center;">
						<s:property value="%{#__pbsRow.name}" />
					</td></tr>
				</s:iterator>
				<s:hidden name="executive" value="true" />
				<s:token />
				<tr><td colspan="2" style="text-align:left;">
					将选中基地放入大区（若不存在则新建）：
					<s:textfield name="newRegionName" />
					<s:submit value="放入新建" cssClass="inlineButton"/>
				</td></tr>
			</s:form>
		</s:if><s:else>
			<!-- ######### 已有大区的基地 ######## -->
			<tr><td colspan="2">
				<div class="listHeader">
					大区（<s:property value="%{#__Row.region.name}" />）
				</div>
			</td></tr>
			<s:form action="jwc_function_qrsxdqxx_delete" method="post" theme="simple" enctype="multipart/form-data">
				<!-- ###### 表头 ###### -->
				<tr class="mytableTitle">
					<td>选择</td>
					<td>学校名称</td>
				</tr>
				<s:iterator value="#__Row.practiceBases" var="__pbsRow" status="__pbsStatus">
					<tr><td class="listContent" style="width:13px;text-align:center;">
						<s:checkbox name="checkBox[%{#__pbsStatus.index}]" />
					</td><td style="text-align:center;">
						<s:property value="%{#__pbsRow.name}" />
					</td></tr>
				</s:iterator>
				<s:hidden name="executive" value="true" />
				<s:hidden name="newRegionName" value="%{#__Row.region.name}" />
				<s:token />
				<tr><td colspan="2" style="text-align:left;">
					从该大区移除
					<s:submit value="移除" cssClass="inlineButton"/>
				</td></tr>
			</s:form>
		</s:else>
		</s:iterator>
	</tbody></table>
	
	<s:form action="login" method="post" cssClass="myform">
		<s:submit value="返回" cssClass="button"/>
	</s:form>
</div>

	<jsp:include page="../common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>