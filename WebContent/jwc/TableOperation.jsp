<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-数据处理（<s:property value="year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="../common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table width="80%"><tbody>
		<!-- ################# -->
		<tr><td>
			<div class="listHeader">
				<s:property value="tableName" />
			</div>
		</td></tr>
		<tr><td>
			<ul class="listContent">
				<li>
					<span class="time">	<!-- 模板下载 -->
						[<a href="<s:url action='jwc_TableImportOperation_download'/>?tableName=Student">
						下载模板</a>]
					</span>
					<s:form action="jwc_TableImportOperation_upload" method="post" theme="simple" enctype="multipart/form-data">
						<s:hidden name="tableName" value="Student" />
						新增数据：
						<s:file label="上传数据" theme="simple" name="uploadFile" class="buttonInline"/>
						<s:submit value="上传" cssClass="buttonInline"/>
						<s:token />
					</s:form>
				</li>
			</ul>
		</td></tr>
		<!-- ################# -->
		<tr><td>
			<div class="listHeader">查询信息（<s:property value="year" />年）</div>
		</td></tr>
		<tr><td>
			<table width="100%"><tbody><s:form action="jwc_function_TableOperation_execute" method="get" theme="simple">
				<s:hidden name="executive" value="true" />
				<s:iterator value="search.restraint" var="__restraintRow" status="__restraintStatus">
					<tr>
						<td><s:property value="%{#__restraintStatus.count}" /></td>
						<td><s:property value="%{#__restraintRow.fieldName}" /></td>
						<td><s:select list="search.restraintTypeList" headerKey="" headerValue="请选择约束方式"
						listKey="key" listValue="value" value="%{#__restraintRow.type}"
						name="search.restraint[%{#__restraintStatus.index}].type"/></td>
						<td><s:textfield value="%{#__restraintRow.value}"
						name="search.restraint[%{#__restraintStatus.index}].value"/></td>
					</tr>
				</s:iterator>
				<tr><td colspan="4" style="width:20%;">
					<s:submit value="查询" cssClass="button" />
				</td></tr>
			</s:form></tbody></table>
		</td></tr>
		<tr><td>
			<table width="100%"><tbody>
				<s:if test="search.resultSet!=null && !search.resultSet.isEmpty()">
					<tr class="mytableTitle">
						<td style="width:13px">序号</td>
						<s:iterator value="search.restraint" var="__label">
							<td style="word-wrap:break-word;word-break:break-all;">
								<s:property value="%{#__label.fieldName}"/>
							</td>
						</s:iterator>
					</tr>
					<s:iterator value="search.resultSet" var="__tableRow" status="__status">
						<tr>
							<td class="listContent" style="width:13px">
								<s:property value="%{#__status.count}" />
							</td>
							<s:iterator value="#__tableRow.iteratorFieldsValue" var="__tableCol">
								<td>
									<s:property value="#__tableCol" />
								</td>
							</s:iterator>
						</tr>
					</s:iterator>
				</s:if>
				<s:else>
					<tr><td>这里会显示结果集...</td></tr>
				</s:else>
			</tbody></table>
		</td></tr>
		<!-- ################# -->
	</tbody></table>
	
	<s:form action="login" method="post" cssClass="myform">
		<s:submit value="返回" cssClass="button"/>
	</s:form>
</div>

	<jsp:include page="../common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>