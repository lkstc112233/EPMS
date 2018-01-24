<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" />-分配实习基地到实习大区（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table style="width:80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				分配实习基地到实习大区（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	
	<table width="80%"><tbody>
	<% boolean[] PracticeBaseFieldDisplay=new boolean[]{
			false,true,true,true,true,
			false,true,true,
			false,false,false,true}; %>
	<s:iterator value="list.list" var="__Row" status="__Status">
		<!-- ########### 大区信息 ########### -->
		<tr><td colspan="100">
			<div class="listHeader" style="width:80%;background:linear-gradient(to right,#0071bc,rgba(0,0,0,0));border:0;">
				<s:if test="#__Row.t == null">
					未分配大区的实习基地
				</s:if><s:else>
					<s:property value="%{#__Row.t.name}" />
				</s:else>
			</div>
		</td></tr>
		<!-- ########### 表头 ########### -->
		<s:if test="#__Row.t == null">
			<tr class="wtableHeader">
				<td style="width:13px;">选择</td>
				<td style="width:13px;">序号</td>
				<% int i=0; %>
				<s:iterator value="practiceBase.fields" var="__opField" status="__opFieldStatus">
					<% if(PracticeBaseFieldDisplay[i++]){ %>
					<td style="word-wrap:break-word;word-break:break-all;">
					<% }else{ %>
					<td style="word-wrap:break-word;word-break:break-all;display:none;">
					<% } %>
						<s:property value="#__opField.description"/>
						<s:if test="#__opField.notNull == true">
						*
						</s:if>
					</td>
				</s:iterator>
			</tr>
		</s:if>
		<!-- ########### 实习基地列表 ########### -->
		<s:if test="#__Row.t == null">
			<s:form action="function_PracticeBaseArrangeIntoRegion_execute" method="post" theme="simple">
				<s:iterator value="#__Row.list" var="__pbrRow" status="__pbrStatus">
				<tr class="wtableContent">
					<!-- 选择 -->
					<td style="width:13px;padding:0;border:0;">
						<s:checkbox name="checkBox[%{#__pbrStatus.index}]" id="%{#__Status.index}_%{#__pbrStatus.index}"
						style="width:100%;height:100%;margin:0;" theme="simple" />
					</td>
					<!-- 序号 -->
					<td style="width:13px;">
						<s:property value="%{#__pbrStatus.count}" />
					</td>
					<!-- 内容 -->
					<% int i=0; %>
					<s:iterator value="%{#__pbrRow.practiceBase.fieldsValue}" var="__Col">
						<% if(PracticeBaseFieldDisplay[i++]){ %>
						<td style="word-wrap:break-word;word-break:break-all;">
						<% }else{ %>
						<td style="word-wrap:break-word;word-break:break-all;display:none;">
						<% } %>
							<s:if test="#__Col.equals(\"✔\")">
								<div style="font-size:26px;color:green;">✔</div>
							</s:if><s:elseif test="#__Col.equals(\"✘\")">
								<div style="color:red;">✘</div>
							</s:elseif><s:else>
								<s:if test="#__pbrRow.practiceBase.status">
									<div style="color:red;">
										<s:property value="#__Col" />
									</div>
								</s:if><s:else>
									<div>
										<s:property value="#__Col" />
									</div>
								</s:else>
							</s:else>
						</td>
					</s:iterator>
				</tr></s:iterator>
				<tr class="wtableContent">
					<td colspan="100" style="border-bottom:#000 solid 3px;border-top: double;height:30px">
						将选中基地放入大区（若不存在则新建）：
						<s:textfield name="regionName" theme="simple"/>
						<s:submit value="放入新建" cssClass="buttonInline"
						style="padding-top:0;height:auto;" theme="simple"/>
					</td>
				</tr>
				<tr><td height="45px" width="100%" colspan="100" valign="top" /></tr>
				<s:token />
			</s:form>
		</s:if><s:else>
			<s:form action="function_PracticeBaseArrangeIntoRegion_delete" method="post" theme="simple">
				<s:iterator value="#__Row.list" var="__pbrRow" status="__pbrStatus">
				<tr class="wtableContent">
					<!-- 选择 -->
					<td style="width:13px;padding:0;border:0;">
						<s:checkbox name="checkBox[%{#__pbrStatus.index}]" id="%{#__Status.index}_%{#__pbrStatus.index}"
						style="width:100%;height:100%;margin:0;" theme="simple" />
					</td>
					<!-- 序号 -->
					<td style="width:13px;">
						<s:property value="%{#__pbrStatus.count}" />
					</td>
					<!-- 内容 -->
					<% int i=0; %>
					<s:iterator value="%{#__pbrRow.practiceBase.fieldsValue}" var="__Col">
						<% if(PracticeBaseFieldDisplay[i++]){ %>
						<td style="word-wrap:break-word;word-break:break-all;">
						<% }else{ %>
						<td style="word-wrap:break-word;word-break:break-all;display:none;">
						<% } %>
							<s:if test="#__Col.equals(\"✔\")">
								<div style="font-size:26px;color:green;">✔</div>
							</s:if><s:elseif test="#__Col.equals(\"✘\")">
								<div style="color:red;">✘</div>
							</s:elseif><s:else>
								<s:if test="#__pbrRow.practiceBase.status">
									<div style="color:red;">
										<s:property value="#__Col" />
									</div>
								</s:if><s:else>
									<div>
										<s:property value="#__Col" />
									</div>
								</s:else>
							</s:else>
						</td>
					</s:iterator>
				</tr></s:iterator>
				<tr class="wtableContent">
					<td colspan="100" style="border-bottom:#000 solid 3px;border-top: double;height:30px">	
						从该大区移除
						<s:hidden name="regionName" value="%{#__Row.t.name}" />
						<s:submit value="移除" cssClass="buttonInline"
						style="padding-top:0;height:auto;" theme="simple"/>
					</td>
				</tr>
				<tr><td height="35px" width="100%" colspan="100" valign="top" /></tr>
				<s:token />
			</s:form>
		</s:else>
	</s:iterator>
	</tbody></table>
	


</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>