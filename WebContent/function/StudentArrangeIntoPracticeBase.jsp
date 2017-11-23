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
				分配
				<s:select id="MajorName" name="majorName" list="majors"
				listKey="name" listValue="subject"
				theme="simple" cssClass="title_button" style="margin-bottom:2px;"
				onchange="window.location.href=window.location.href.substring(0,(
				window.location.href.indexOf('?')<0?window.location.length:window.location.href.indexOf('?'))
				)+'?majorName='+this.value"
				/>
				实习生到实习基地（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	
	<table width="80%"><tbody>
	<% boolean[] StudentFieldDisplay=new boolean[]{
			true,true,true,true,true,
			false,false,false,
			true,true,true,true,
			false,false,false,}; %>
	<s:iterator value="practiceBaseAndStudents.list" var="__Row" status="__Status">
		<!-- ########### 大区信息 ########### -->
		<tr><td colspan="100">
			<s:if test="#__Row.practiceBase == null">
				<div class="listHeader" style="width:80%;background:linear-gradient(to right,gold,rgba(0,0,0,0));border:0;
				color:red;font-size:16px;">
					未分配实习基地的实习生
				</div>
			</s:if><s:else>
				<div class="listHeader" style="width:80%;background:linear-gradient(to right,#0071bc,rgba(0,0,0,0));border:0;">
					<s:property value="#__Row.practiceBase.name" />
					<span style="font-size:12px;margin-left:20px;">
						<s:if test="#__Row.practiceBase.hx">
							回生源地实习基地
						</s:if><s:else>
							北京及周边实习基地
						</s:else>
					</span>
					<span style="font-size:12px;margin-left:30px;">
						<s:if test="#__Row.plan.refuseSex != null || #__Row.plan.refuseNation != null || #__Row.practiceBase.refuseNation != null">
							不接收：&nbsp;
						</s:if>
					</span>
					<span style="font-size:12px;margin-left:0px;word-wrap: break-word;word-break: break-all;color:white;">
						<s:if test="#__Row.plan.refuseSex != null">
							性别(<s:property value="#__Row.plan.refuseSex" />)
						</s:if>
						<s:if test="#__Row.plan.refuseNation != null">
							<s:if test="#__Row.plan.refuseSex != null">
								、
							</s:if>
							<s:property value="#__Row.plan.refuseNation" />
						</s:if>
						<s:if test="#__Row.practiceBase.refuseNation != null">
							<s:if test="#__Row.plan.refuseSex != null && #__Row.plan.refuseNation == null">
								、
							</s:if>
							<s:property value="#__Row.practiceBase.refuseNation" />
						</s:if>
					</span>
					<s:if test="#__Row.size < #__Row.plan.number">
						<div class="right" style="color:red;font-size:16px;">
							人数:<s:property value="#__Row.size"/>/<s:property value="#__Row.plan.number"/>
						</div>
					</s:if><s:else>
						<div class="right" style="color:black;">
							人数:<s:property value="#__Row.size"/>/<s:property value="#__Row.plan.number"/>
						</div>
					</s:else>
				</div>
			</s:else>
		</td></tr>
		<!-- ########### 表头 ########### -->
		<s:if test="#__Row.practiceBase == null">
			<tr class="wtableHeader">
				<td style="width:13px;">选择</td>
				<td style="width:13px;">序号</td>
				<% int i=0; %>
				<s:iterator value="student.fields" var="__opField" status="__opFieldStatus">
					<% if(StudentFieldDisplay[i++]){ %>
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
				<td style="word-wrap:break-word;word-break:break-all;">
					性别
				</td>
			</tr>
		</s:if>
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
					<% int i=0; %>
					<s:iterator value="%{#__studentRow.fieldsValue}" var="__Col">
						<% if(StudentFieldDisplay[i++]){ %>
						<td style="word-wrap:break-word;word-break:break-all;">
						<% }else{ %>
						<td style="word-wrap:break-word;word-break:break-all;display:none;">
						<% } %>
							<s:property value="#__Col" />
						</td>
					</s:iterator>
					<!-- 性别 -->
					<td style="word-wrap:break-word;word-break:break-all;">
						<s:property value="#__studentRow.sex" />
					</td>
				</tr></s:iterator>
				<tr class="wtableContent">
					<td colspan="100" style="border-bottom:#000 solid 3px;border-top: double;height:30px">
						将选中实习生分配至实习基地：
						<s:select list="practiceBases"
						listKey="name" listValue="description"
						headerKey="" headerValue="-无-"
						name="practiceBaseName" />
						<s:submit value="放入" cssClass="buttonInline"
						style="padding-top:0;height:auto;" theme="simple"/>
					</td>
				</tr>
				<tr><td height="45px" width="100%" colspan="100" valign="top" /></tr>
				<s:hidden name="majorName" value="%{majorName}" theme="simple" />
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
					<% int i=0; %>
					<s:iterator value="%{#__studentRow.fieldsValue}" var="__Col">
						<% if(StudentFieldDisplay[i++]){ %>
						<td style="word-wrap:break-word;word-break:break-all;">
						<% }else{ %>
						<td style="word-wrap:break-word;word-break:break-all;display:none;">
						<% } %>
							<s:property value="#__Col" />
						</td>
					</s:iterator>
				</tr></s:iterator>
				<tr class="wtableContent">
					<td colspan="100" style="border-bottom:#000 solid 3px;border-top: double;height:30px">	
						从该基地移出：
						<s:hidden name="practiceBaseName" value="%{#__Row.practiceBase.name}" />
						<s:submit value="移出" cssClass="buttonInline"
						style="padding-top:0;height:auto;" theme="simple"/>
					</td>
				</tr>
				<tr><td height="35px" width="100%" colspan="100" valign="top" /></tr>
				<s:hidden name="majorName" value="%{majorName}" theme="simple" />
			</s:form>
		</s:else>
	</s:iterator>
	</tbody></table>
	


</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>