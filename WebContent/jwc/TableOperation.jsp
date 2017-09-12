<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-<s:property value="tableName" />数据处理</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="../common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<!-- ################# -->
	<table width="80%"><tbody>
		<tr><td>
			<div class="listHeader">
				<div class="listHeaderLeft">
					<s:property value="tableName"/>
				</div>
				<div class="right">
					修改操作表: 
					<s:select id="tableName" name="tableName" cssClass="title_button" style="width:200px;margin-bottom:2px;"
					list="{
					'Student',
					'ACCESS','InnerOffice','Major','OuterOffice','Province','School','ZZMM',
					'Time'
					}" theme="simple"
					onchange="window.location.href=window.location.href.substring(0,(
					window.location.href.indexOf('?')<0?window.location.length:window.location.href.indexOf('?'))
					)+'?tableName='+this.value"/>
				</div>
			</div>
		</td></tr>
		<tr><td>
			<ul class="listContent">
				<li>
					<span class="time">	<!-- 模板下载 -->
						[<a href="<s:url action='jwc_TableImportOperation_download'/>?tableName=<s:property value='tableName'/>">
						下载模板</a>]
					</span>
					<s:form action="jwc_TableImportOperation_upload" method="post" theme="simple" enctype="multipart/form-data">
						<s:hidden name="tableName" value="%{tableName}" />
						新增数据：
						<s:file label="上传数据" theme="simple" name="uploadFile" class="buttonInline"/>
						<s:submit value="上传" cssClass="buttonInline"/>
						<s:token />
					</s:form>
				</li>
			</ul>
		</td></tr>
		<tr><td height="30px"></td></tr>
		<!-- ################# -->
		<tr><td>
			<table width="100%"><tbody><s:form action="jwc_TableOperation_execute" method="get" theme="simple">
				<s:hidden name="choose" value="-1" theme="simple"/>
				<tr><td style="border:2px solid #0071BC;">
					<s:iterator value="search.restraint" var="__restraintRow" status="__restraintStatus">
						<div style="line-height:32px;float:left">
							<div style="float:left;width:120px;text-align:center">
								<s:property value="%{#__restraintRow.fieldName}" />
							</div>
							<div style="float:left;text-align:center">
								<s:select list="search.restraintTypeList" headerKey="" headerValue="-约束-"
								listKey="key" listValue="value" value="%{#__restraintRow.type}"
								name="search.restraint[%{#__restraintStatus.index}].type"/>
								<s:textfield value="%{#__restraintRow.value}"
								name="search.restraint[%{#__restraintStatus.index}].value"/>
							</div>
							<div style="float:left;width:18px;">
								&nbsp;
							</div>
						</div>
						<!--<s:property value="%{#__restraintStatus.count}" /> -->
					</s:iterator>
				</td></tr>
				<tr><td colspan="1" style="width:20%;">
					<s:submit value="查询" cssClass="button" />
				</td></tr>
			</s:form></tbody></table>
		</td></tr>
	</tbody></table>
	<!-- ################# -->
	<div style="overflow-x:scroll;">
	<table width="80%"><tbody>
		<s:if test="search.resultSet!=null">
			<!-- ###### 表头 ###### -->
			<tr class="mytableTitle">
				<td style="width:13px;text-align:center;">序号</td>
				<td style="text-align:center;" colspan="2">操作</td>
				<s:iterator value="search.restraint" var="__label">
					<td style="word-wrap:break-word;word-break:break-all;text-align:center;">
						<s:property value="%{#__label.fieldName}"/>
					</td>
				</s:iterator>
			</tr>
			<!-- ###### 新增行 ###### -->
			<tr>
				<!-- 序号 -->
				<td class="listContent" style="width:13px;text-align:center;">
					+
				</td>
				<!-- 内容 -->
				<s:form action="jwc_TableOperation_create" method="post" theme="simple">
					<!-- 操作 -->
					<td class="listContent" style="width:27px;text-align:center;padding:0px;" colspan="2">
						<s:submit value="create" cssClass="inlineButton" theme="simple"/>
					</td>
					<!-- 内容 -->
					<s:iterator value="createNewBase.iteratorFieldsValue" var="__tableCol" status="__colStatus">
						<td style="text-align:center;">
							<s:if test='#__tableCol==null || #__tableCol==""'>
								<s:textfield value="" theme="simple"
								style="padding:0px;width:95%;min-width:30px;"
								name="createNewBase.%{createNewBase.iteratorFieldsName[#__colStatus.index]}" />
							</s:if>
							<s:else>
								<s:textfield value="%{#__tableCol}" theme="simple"
								style="padding:0px;width:auto;"
								name="createNewBase.%{createNewBase.iteratorFieldsName[#__colStatus.index]}" />
							</s:else>
						</td>
					</s:iterator>
				</s:form>
			</tr>
			<!-- ###### resultSet 内容 ###### -->
			<s:iterator value="search.resultSet" var="__tableRow" status="__status">
				<tr>
					<!-- 序号 -->
					<td class="listContent" style="width:13px;text-align:center;">
						<s:property value="%{#__status.count}" />
					</td>
					<!-- 操作 -->
					<td class="listContent" style="width:27px;text-align:center;padding:0px;">
						<s:form action="jwc_TableOperation_delete" method="get" theme="simple">
							<s:hidden name="choose" value="%{#__status.index}" theme="simple"/>
							<s:submit value="X" cssClass="inlineButton"
							style="color:red;" theme="simple"/>
						</s:form>
					</td>
					<!-- 内容 -->
					<s:if test="choose != #__status.index">
						<!-- 操作 -->
						<td class="listContent" style="width:40px;text-align:center;padding:0px;">
							<s:form action="jwc_TableOperation_display" method="get" theme="simple">
								<s:hidden name="choose" value="%{#__status.index}" theme="simple"/>
								<s:submit value="修改" cssClass="inlineButton" theme="simple"/>
							</s:form>
						</td>
						<!-- 内容 -->
						<s:iterator value="#__tableRow.iteratorFieldsValue" var="__tableCol">
							<td>
								<s:property value="#__tableCol" />
							</td>
						</s:iterator>
					</s:if>
					<s:else>
						<s:form action="jwc_TableOperation_update" method="get" theme="simple">
							<!-- 操作 -->
							<td class="listContent" style="width:40px;text-align:center;">
								<s:hidden name="choose" value="%{#__status.index}" theme="simple"/>
								<s:submit value="保存" cssClass="inlineButton"
								style="color:#ffffff;background-color:#0071bc" theme="simple"/>
							</td>
							<!-- 内容 -->
							<s:iterator value="#__tableRow.iteratorFieldsValue" var="__tableCol" status="__colStatus">
								<td style="text-align:center;">
									<s:if test='#__tableCol==null || #__tableCol==""'>
										<s:textfield value="" theme="simple"
										style="padding:0px;width:95%;min-width:30px;"
										name="search.resultSet[%{#__status.index}].%{#__tableRow.iteratorFieldsName[#__colStatus.index]}" />
									</s:if>
									<s:else>
										<s:textfield value="%{#__tableCol}" theme="simple"
										style="padding:0px;width:auto;"
										name="search.resultSet[%{#__status.index}].%{#__tableRow.iteratorFieldsName[#__colStatus.index]}" />
									</s:else>
								</td>
							</s:iterator>
						</s:form>
					</s:else>
				</tr>
			</s:iterator>
		</s:if>
		<s:else>
			<tr><td>这里会显示结果集...</td></tr>
		</s:else>
	</tbody></table></div>
	<!-- ################# -->
</div>

	<jsp:include page="../common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>