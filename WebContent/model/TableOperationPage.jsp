<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
    
	
<div class="bag">
	<% request.setAttribute("actionName",request.getParameter("actionName"));%>
		
	<!-- ################# -->
	<table width="80%"><tbody>
		<tr><td>
			<div class="listHeader">
				<div class="listHeaderLeft">
					<s:property value="tableName"/>
				</div>
			<% String restraintPage=request.getParameter("restraintPage"); 
			   if(restraintPage!=null && restraintPage.contains("allSearchRestraint")){ %>
				<div class="right">
					修改操作表:
					<!-- name="tableName" --> 
					<s:select id="tableName" cssClass="title_button" style="width:200px;margin-bottom:2px;"
					value="%{tableName}"
					list="%{tableNames}" theme="simple"
					onchange="window.location.href=window.location.href.substring(0,(
					window.location.href.indexOf('?')<0?window.location.length:window.location.href.indexOf('?'))
					)+'?tableName='+this.value"/>
				</div>
			<% } %>
			</div>
		</td></tr>
		<tr><td>
			<ul class="listContent">
				<li>
					<span class="time">	<!-- 模板下载 -->
						[<a href="<s:url action='%{#request.actionName}_download'/>?tableName=<s:property value='tableName'/>">
						下载模板</a>]
					</span>
					<s:form action="%{#request.actionName}_upload" method="post" theme="simple" enctype="multipart/form-data">
						<s:hidden name="tableName" value="%{tableName}" />
						新增数据
						<s:file label="上传数据" theme="simple" name="uploadFile" class="buttonInline"/>
						<s:submit value="上传" cssClass="buttonInline"/>
						<s:token />
					</s:form>
				</li>
			</ul>
		</td></tr>
		<tr><td height="30px"></td></tr>
		<!-- ################# -->
		<!-- 查询限制条件 -->
		<tr><td>
			<% if(restraintPage!=null){ %>
				<jsp:include page="${param.restraintPage}" flush="true">
					<jsp:param name="actionName" value="${actionName}"/>
				</jsp:include>
			<% }else{ %>
				<table style="width:100%"><tbody><s:form action="%{#request.actionName}_execute" method="get" theme="simple">
					<tr><td colspan="1" style="width:20%;">
						<s:submit value="查询" cssClass="button" />
					</td></tr>
				</s:form></tbody></table>
			<% } %>
		</td></tr>
	</tbody></table>
	<!-- ################# -->
	<!-- 结果集 -->
	<div style="overflow-x:scroll;">
	<table width="80%"><tbody>
		<s:if test="search.result!=null">
			<!-- ###### 表头 ###### -->
			<tr class="mytableTitle">
				<td colspan="3">&nbsp;</td>
				<s:iterator value="search.classInfo.allFieldsNameString" var="__Row" status="__Status">
					<td style="word-wrap:break-word;word-break:break-all;text-align:center;">
						<s:property value="#__Row"/>
						<s:if test="search.classInfo.allFieldsKeyBoolean[#__Status.index] == true">
						*
						</s:if>
					</td>
				</s:iterator>
			</tr>
			<tr class="mytableTitle">
				<td style="width:13px;text-align:center;">序号</td>
				<td style="text-align:center;" colspan="2">操作</td>
				<s:iterator value="search.classInfo.allFieldsDescriptionString" var="__Row" status="__Status">
					<td style="word-wrap:break-word;word-break:break-all;text-align:center;">
						<s:property value="%{#__Row}"/>
						<s:if test="search.classInfo.allFieldsKeyBoolean[#__Status.index] == true">
						*
						</s:if>
					</td>
				</s:iterator>
			</tr>
			<!-- ###### 新增行 ###### -->
			<tr>
				<!-- 序号 -->
				<td class="listContent" style="width:13px;text-align:center;">
					+
				</td>
				<!-- 新增行 -->
				<s:form action="%{#request.actionName}_create" method="post" theme="simple">
					<!-- 操作 -->
					<td class="listContent" style="width:27px;text-align:center;padding:0px;" colspan="2">
						<s:submit value="create" cssClass="inlineButton" theme="simple"/>
					</td>
					<!-- 内容 -->
					<s:iterator value="search.classInfo.allFieldsNameString" var="__Row" status="__Status">
						<td style="text-align:center;">
							<s:if test="search.classInfo.allFieldsSourceListDescription[#__Status.index] == null">
								<s:textfield theme="simple"
								style="padding:0px;width:95%;min-width:30px;"
								name="createNewBase.%{#__Row}" value="%{createNewBase.%{#__Row}}"/>
							</s:if><s:else>
								<s:select list="search.classInfo.allFieldsSourceListDescription[#__Status.index]"
								headerKey="" headerValue="无"
								theme="simple"
								style="padding:0px;"
								name="createNewBase.%{#__Row}" value="%{createNewBase.#__Row}"/>
							</s:else>
						</td>
					</s:iterator>
				</s:form>
			</tr>
			<!-- ###### resultSet 内容 ###### -->
			<s:iterator value="search.result" var="__Row" status="__Status">
				<tr>
					<!-- 序号 -->
					<td class="listContent" style="width:13px;text-align:center;">
						<s:property value="%{#__Status.count}" />
					</td>
					<!-- 操作 -->
					<td class="listContent" style="width:27px;text-align:center;padding:0px;">
						<s:form action="%{#request.actionName}_delete" method="get" theme="simple">
							<s:hidden name="choose" value="%{#__Status.index}" theme="simple"/>
							<s:submit value="X" cssClass="inlineButton"
							style="color:red;" theme="simple"/>
						</s:form>
					</td>
					<!-- 内容 -->
					<s:if test="choose != #__Status.index">
						<!-- 操作 -->
						<td class="listContent" style="width:40px;text-align:center;padding:0px;">
							<s:form action="%{#request.actionName}_display" method="get" theme="simple">
								<s:hidden name="choose" value="%{#__Status.index}" theme="simple"/>
								<s:submit value="修改" cssClass="inlineButton" theme="simple"/>
							</s:form>
						</td>
						<!-- 内容 -->
						<s:iterator value="%{#__Row.allFieldsValueString}" var="__Col">
							<td style="text-align:center;">
								<s:property value="#__Col" />
							</td>
						</s:iterator>
					</s:if>
					<s:else>
						<s:form action="%{#request.actionName}_update" method="get" theme="simple">
							<!-- 操作 -->
							<td class="listContent" style="width:40px;text-align:center;">
								<s:hidden name="choose" value="%{#__Status.index}" theme="simple"/>
								<s:submit value="保存" cssClass="inlineButton"
								style="color:#ffffff;background-color:#0071bc" theme="simple"/>
							</td>
							<!-- 内容 -->
							<s:iterator value="#__Row.allFieldsValueString" var="__Col" status="__ColStatus">
								<td style="text-align:center;">
									<s:if test="search.classInfo.allFieldsKeyBoolean[#__ColStatus.index] == false">
										<s:if test="search.classInfo.allFieldsSourceListDescription[#__ColStatus.index] == null">
											<s:textfield theme="simple"
											style="padding:0px;width:95%;min-width:30px;"
											value="%{#__Col}"
											name="search.result[%{#__Status.index}].%{search.classInfo.allFieldsNameString[#__ColStatus.index]}" />
										</s:if><s:else>
											<s:select list="search.classInfo.allFieldsSourceListDescription[#__ColStatus.index]"
											headerKey="" headerValue="无"
											theme="simple"
											style="padding:0px;"
											value="%{#__Col}"
											name="search.result[%{#__Status.index}].%{search.classInfo.allFieldsNameString[#__ColStatus.index]}" />
										</s:else>
									</s:if><s:else>
										<div style="padding:0px;width:95%;min-width:30px;text-align:center;">
											<s:property value="#__Col" />
										</div>
									</s:else>
								</td>
							</s:iterator>
						</s:form>
					</s:else>
				</tr>
			</s:iterator>
		</s:if>
		<s:else>
			<tr><td>这里会显示结果集...</td></tr>
		</s:else>
	</tbody></table></div>
	<!-- ################# -->
	
	
	
</div>
