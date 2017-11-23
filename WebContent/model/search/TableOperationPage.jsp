<%@page import="obj.JoinParam"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
    
	
<div class="bag">
	<% request.setAttribute("actionName",request.getParameter("actionName"));%>
		
	<table class="wtable"><tbody>
		<!-- ################# -->
		<!-- 联合查询配置 -->
		<% String joinParamPage=request.getParameter("joinParamPage"); 
		   if(joinParamPage!=null){ %>
			<tr class="wtableContent"><td colspan="100" style="padding:0;border:0;">
				<jsp:include page="${param.joinParamPage}" flush="true">
					<jsp:param name="actionName" value="${actionName}"/>
				</jsp:include>
			</td></tr>
		<% } %>
		<!-- ################# -->
		<!-- 查询限制条件 -->
		<tr class="wtableContent"><td colspan="100" style="padding:0;border:0;">
			<% String restraintPage=request.getParameter("restraintPage"); 
				if(restraintPage!=null){ %>
				<jsp:include page="${param.restraintPage}" flush="true">
					<jsp:param name="actionName" value="${actionName}"/>
				</jsp:include>
			<% }else{ %>
				<s:form action="%{#request.actionName}_execute" method="post" theme="simple">
					<s:submit value="查询" cssClass="title_button" theme="simple" style="width:20%"/>
				</s:form>
			<% } %>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	<div style="overflow-x:scroll;"><table class="wtable"><tbody>
		<tr>
			<td style="width:13px;">&nbsp;</td>
			<s:iterator value="search.param.list" var="__Part" status="__PartStatus">
				<% pageContext.setAttribute("_colspan",obj.Field.getFields(((obj.JoinParam.Part)request.getAttribute("__Part")).getClazz()).length+2); %>
				<td colspan="${_colspan}">
					<div class="listHeader" style="width:80%;background:linear-gradient(to right,#0071bc,rgba(0,0,0,0));border:0;">
					<div class="listHeaderLeft">
						<s:property value="#__Part.SQLTableName" />
					</div></div>
				</td>
			</s:iterator>
		</tr>
		<% String poiBoolean=request.getParameter("poiBoolean");
			if(Boolean.valueOf(poiBoolean)){ %>
			<tr>
				<td style="width:13px;">&nbsp;</td>
				<s:iterator value="search.param.list" var="__Part" status="__PartStatus">
					<% pageContext.setAttribute("_colspan",obj.Field.getFields(((obj.JoinParam.Part)request.getAttribute("__Part")).getClazz()).length+2); %>
					<td colspan="${_colspan}">
						<ul class="listContent">
							<li>
								<span class="time">	<!-- 模板下载 -->
									[<a href="<s:url action='%{#request.actionName}_download'/>?tableName=<s:property value='tableName'/>">
									down</a>]
								</span>
								<s:form action="%{#request.actionName}_upload" method="post" theme="simple" enctype="multipart/form-data">
									<s:hidden name="tableName" value="%{tableName}" />
									新增
									<s:file label="上传数据" theme="simple" name="uploadFile" class="buttonInline"/>
									<s:submit value="上传" cssClass="buttonInline"/>
									<s:token />
								</s:form>
							</li>
						</ul>
					</td>
				</s:iterator>
			</tr>
		<% } %>
		<tr class="wtableHeader" >
			<td style="width:13px;border:0;line-height:50px;">序号</td>
			<s:iterator value="search.param.list" var="__Part" status="__PartStatus">
				<td colspan="2" style="border:0;">操作</td>
				<s:iterator value="#__Part.fields" var="__Field" status="__FieldStatus">
					<td style="border:0;">
						<s:property value="#__Field.description"/>
						<s:if test="#__Field.notNull == true">
						*
						</s:if>
					</td>
				</s:iterator>
			</s:iterator>
		</tr>
		<% String createBoolean=request.getParameter("createBoolean");
			if(Boolean.valueOf(createBoolean)){ %>
			<tr class="wtableContent">
				<td style="width:13px;border:0;">
					+
				</td>
				<s:iterator value="search.param.list" var="__Part" status="__PartStatus">
					<s:if test="choose[0] == #__PartStatus.index && (choose[1] == null || choose[1] < 0)">
						<s:form action="%{#request.actionName}_create" method="post" theme="simple">
							<td style="width:27px;border:0;" colspan="2">
								<s:submit value="create" cssClass="inlineButton" style="width:100%;" theme="simple" />
							</td>
							<s:iterator value="operateBase.fields" var="__opField" status="__opFieldStatus">
								<s:if test="fieldsDisplay[#__PartStatus.index][#__opFieldStatus.index] == false">
								<td style="border:0;">&nbsp;</td>
								</s:if><s:else>
									<td style="border:0;padding:0;">
										<s:if test="#__opField.source == null">
											<s:textfield name="operateBase.%{#__opField.name}"
											value="%{operateBase.fieldsValue[#__opFieldStatus.index]}"
											style="text-align:center;border:0px;width:100%;height:100%;" theme="simple" />
										</s:if><s:else>
											<s:select list="%{#__opField.sourceList}"
											listKey="key" listValue="value"
											headerKey="" headerValue="无"
											name="operateBase.%{#__opField.name}"
											value="%{operateBase.fieldsValue[#__opFieldStatus.index]}"
											style="text-align:center;border:0px;width:100%;height:100%" theme="simple" />
										</s:else>
									</td>
								</s:else>
							</s:iterator>
						</s:form>
					</s:if><s:else>
						<% pageContext.setAttribute("_colspan",obj.Field.getFields(((obj.JoinParam.Part)request.getAttribute("__Part")).getClazz()).length+2); %>
						<td colspan="${_colspan}" style="border:0;padding:0;">
							<s:form action="%{#request.actionName}_display" method="post" theme="simple">
								<s:hidden name="choose[0]" value="%{#__PartStatus.index}" theme="simple"/>
								<s:submit value="+ create +" cssClass="inlineButton" style="width:100%;height:100%;" theme="simple"/>
							</s:form>
						</td>
					</s:else>
				</s:iterator>
			</tr>
		<% } %>
		<s:if test="search == null || search.result == null || search.result.isEmpty()">
			<tr><td colspan="100">
				这里会显示结果集
			</td></tr>
		</s:if><s:else>
			<s:iterator value="search.result" var="__Row" status="__Status">
				<tr class="wtableContent">
					<td style="width:13px;">
						<s:property value="%{#__Status.count}" />
					</td>
					<s:iterator value="search.param.list" var="__Part" status="__PartStatus">
						<td style="width:27px;">
							<% String deleteBoolean=request.getParameter("deleteBoolean");
								if(Boolean.valueOf(deleteBoolean)){ %>
								<s:form action="%{#request.actionName}_delete" method="post" theme="simple">
									<s:hidden name="choose[0]" value="%{#__PartStatus.index}" theme="simple"/>
									<s:hidden name="choose[1]" value="%{#__Status.index}" theme="simple"/>
									<s:submit value="X" cssClass="inlineButton"
									style="color:red;width:100%;" theme="simple"/>
								</s:form>
							<% }else{ %>
								<input value="X" disabled="disabled" type="submit"
								style="color:#999999;width:100%;disable:true;" />
							<% } %>
						</td>
						<s:if test="choose!=null && choose[0] == #__PartStatus.index && choose[1] == #__Status.index">
							<s:form action="%{#request.actionName}_update" method="post" theme="simple">
								<td style="width:40px;">
									<s:hidden name="choose[0]" value="%{#__PartStatus.index}" theme="simple"/>
									<s:hidden name="choose[1]" value="%{#__Status.index}" theme="simple"/>
									<s:submit value="保存" cssClass="inlineButton"
									style="color:#ffffff;background-color:#0071bc;width:95%;" theme="simple" />
								</td>
								<s:iterator value="#__Row[#__PartStatus.index].fields" var="__opField" status="__opFieldStatus">
									<s:if test="fieldsDisplay[#__PartStatus.index][#__opFieldStatus.index] == false">
									<td></td>
									</s:if><s:else>
										<td style="padding:0;white-space: nowrap;">
											<s:if test="#__opField.source == null">
												<s:textfield
												name="search.result[%{#__Status.index}][%{#__PartStatus.index}].%{#__opField.name}"
												value="%{#__Row[#__PartStatus.index].fieldsValue[#__opFieldStatus.index]}"
												style="text-align:center;border:0px;width:100%;height:100%;" theme="simple" />
											</s:if><s:else>
												<s:select list="%{#__opField.sourceList}"
												listKey="key" listValue="value"
												name="search.result[%{#__Status.index}][%{#__PartStatus.index}].%{#__opField.name}"
												value="%{#__Row[#__PartStatus.index].fieldsValue[#__opFieldStatus.index]}"
												headerKey="" headerValue="无"
												style="text-align:center;border:0px;width:100%;height:100%;" theme="simple" />
											</s:else>
										</td>
									</s:else>
								</s:iterator>
							</s:form>
						</s:if><s:else>
							<td style="width:40px;">
								<s:form action="%{#request.actionName}_display" method="post" theme="simple">
									<s:hidden name="choose[0]" value="%{#__PartStatus.index}" theme="simple"/>
									<s:hidden name="choose[1]" value="%{#__Status.index}" theme="simple"/>
									<s:submit value="修改" cssClass="inlineButton"
									style="width:100%;" theme="simple" />
								</s:form>
							</td>
							<s:iterator value="%{#__Row[#__PartStatus.index].fields}" var="__opField" status="__opFieldStatus">
								<s:if test="fieldsDisplay[#__PartStatus.index][#__opFieldStatus.index] == false">
								<td></td>
								</s:if><s:else>
									<td style="white-space: nowrap;">
										<s:property value="#__Row[#__PartStatus.index].fieldsValue[#__opFieldStatus.index]" />
									</td>
								</s:else>
							</s:iterator>
						</s:else>
					</s:iterator>
				</tr>
			</s:iterator>
		</s:else>
	</tbody></table></div>
	
	
</div>