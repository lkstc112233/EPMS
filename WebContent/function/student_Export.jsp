<%@page import="obj.annualTable.list.Leaf"%>
<%@page import="obj.annualTable.list.Node"%>
<%@page import="obj.annualTable.list.PracticeBaseWithRegion"%>
<%@page import="obj.annualTable.Student"%>
<%@page import="obj.annualTable.Region"%>
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
				<s:select id="MajorName" name="majorName" list="majors"
				listKey="name" listValue="subject"
				theme="simple" cssClass="title_button" style="margin-bottom:2px;width:auto;"
				onchange="window.location.href=window.location.href.substring(0,(
				window.location.href.lastIndexOf('/')<0?window.location.length:window.location.href.lastIndexOf('/'))
				)+'/function_student_Export_display.action?majorName='
				+this.value"
				/>
				实习生名单
				（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
		<tr><td colspan="100" style="text-align:center;">
			<span style="float:left;margin-left:50px;">
			<s:form action="function_student_ExportAllStudentList_download" method="post" theme="simple">
				<s:hidden name="majorName" value="%{majorName}" theme="simple"/>
				<s:hidden name="status" value="false" theme="simple"/>
				<s:submit value="实习生名单" theme="simple"
				style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
			</s:form>
			</span>
			<s:if test="majorName == allMajors.name">
				<span style="float:left;margin-left:20px;">
				<s:form action="function_student_ExportAllStudentList_download" method="post" theme="simple">
					<s:hidden name="majorName" value="%{majorName}" theme="simple"/>
					<s:hidden name="status" value="true" theme="simple"/>
					<s:submit value="特殊基地实习生名单" theme="simple"
					style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
				</s:form>
				</span>
				<span style="float:left;margin-left:20px;">
				<s:form action="function_student_ExportStudentInsuranceList_download" method="post" theme="simple">
					<s:submit value="投保确认单" theme="simple"
					style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
				</s:form>
				</span>
				<span style="float:left;margin-left:20px;">
				<s:form action="function_student_ExportAllPracticeBaseConsultationLetter_download" method="post" theme="simple">
					<s:hidden name="majorName" value="%{allMajors.name}" theme="simple"/>
					<s:submit value="基地商洽函" theme="simple"
					style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
				</s:form>
				</span>
			</s:if>
			<span style="float:left;margin-left:20px;">
			<s:form action="function_student_ExportPlanDesign_download" method="post" theme="simple">
				<s:submit value="教育实习布局规划" theme="simple"
				style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
			</s:form>
			</span>
			<s:if test="majorName == allMajors.name">
				<span style="float:left;margin-left:20px;">
				<s:form action="function_student_ExportPlanDesign_download" method="post" theme="simple">
					<s:hidden name="status" value="" theme="simple"/>
					<s:submit value="教育实习布局规划(含特殊基地)" theme="simple"
					style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
				</s:form>
				</span>
			</s:if>
			<span style="float:left;margin-left:20px;">
			<s:form action="function_student_ExportPlanMedia_download" method="post" theme="simple">
				<s:hidden name="status" value="" theme="simple"/>
				<s:submit value="设备发放规划" theme="simple"
				style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
			</s:form>
			</span>
			<span style="float:left;margin-left:20px;">
			<s:form action="function_student_ExportTeacherList_download" method="post" theme="simple">
				<s:hidden name="status" value="false" theme="simple"/>
				<s:submit value="指导教师名单(全部专业)" theme="simple"
				style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
			</s:form>
			</span>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	
	<% boolean[] StudentFieldDisplay=new boolean[]{
			false,true,true,true,true,
			true,false,false,
			true,true,true,false,
			false,false,false,false};
		int i; %>
		
	<!-- ###### 已分配实习生 ###### -->
	<table width="80%"><tbody>
	<s:iterator value="list.list" var="__rpRow" status="__rpStatus">
	<s:iterator value="#__rpRow.list" var="__Row" status="__Status">
		<% Object tmpPracticeBase=pageContext.findAttribute("#__Row");
		String practiceBaseName="";
		if(tmpPracticeBase!=null && tmpPracticeBase instanceof Leaf){//Leaf<PracticeBaseWithRegion,Student>
			@SuppressWarnings("unchecked")
			String tmp=((Leaf<PracticeBaseWithRegion,Student>)tmpPracticeBase).getT().getPracticeBase().getName();
			practiceBaseName=tmp;
		}
		%>
		<tr>
			<s:if test="#__Status.index == 0">
				<% @SuppressWarnings("unchecked")
				Node<Region,Leaf<PracticeBaseWithRegion,Student>> rp=(Node<Region,Leaf<PracticeBaseWithRegion,Student>>)
					request.getAttribute("__rpRow");
				int tmpStudentCnt=0;
				for(Leaf<PracticeBaseWithRegion,Student> iter:rp.getList()) tmpStudentCnt+=iter.getSize();
				pageContext.setAttribute("_rowspan",
						tmpStudentCnt+rp.getSize()*3-1); %>
				<td rowspan="${_rowspan}" class="listHeader"
				style="width:30px;background:#0071bc;text-indent:0px;text-align:center;" >
					<s:property value="#__rpRow.t.name" />
				</td>
			</s:if>
			<td colspan="100">
				<div class="listHeader" style="width:100%;background:linear-gradient(to right,#0071bc,rgba(0,0,0,0));border:0;">
					<s:if test="#__Row.practiceBase.status">
						<span style="float:left;color:red;">
							<s:property value="#__Row.t.practiceBase.name" />
						</span>
					</s:if><s:else>
						<span style="float:left;">
							<s:property value="#__Row.t.practiceBase.name" />
						</span>
					</s:else>
					<span style="float:left;font-size:12px;margin-left:20px;">
						<s:if test="#__Row.t.practiceBase.hx">
							回生源地实习基地
						</s:if><s:else>
							北京及周边实习基地
						</s:else>
					</span>
					<span style="float:left;font-size:12px;margin-left:20px;">
						<s:property value="#__Row.size"/>人
					</span>
					<span style="float:left;font-size:12px;margin-left:20px;margin-top:5px;">
						<s:form action="function_student_ExportStudentList_download" method="post" theme="simple">
							<s:hidden name="practiceBaseName" value="%{#__Row.t.practiceBase.name}" theme="simple"/>
							<s:hidden name="majorName" value="%{majorName}" theme="simple"/>
							<s:submit value="下载实习生名单" theme="simple"
							style="width:auto;background:rgba(255,255,255,0.85);border:double 3px #0071bc;font-weight:600;height:25px;"/>
						</s:form>
					</span>
					<span style="float:left;font-size:12px;margin-left:5px;margin-top:5px;">
						<s:if test="#__Row.t.practiceBase.status==false">
							<s:form action="function_student_ExportPracticeBaseConsultationLetter_download" method="post" theme="simple">
								<s:hidden name="practiceBaseName" value="%{#__Row.t.practiceBase.name}" theme="simple"/>
								<s:hidden name="majorName" value="%{majorName}" theme="simple"/>
								<s:submit value="下载商洽函" theme="simple"
								style="width:auto;background:rgba(255,255,255,0.85);border:double 3px #0071bc;font-weight:600;height:25px;"/>
							</s:form>
						</s:if>
					</span>
				</div>
			</td>
		</tr>
		<!-- 表头 --><tr class="wtableHeader">
			<td style="width:13px;">序号</td>
			<% i=0; %>
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
			<td style="white-space:nowrap;">
				性别
			</td>
			<td style="white-space:nowrap;">
				指导老师
			</td>
			<td style="white-space:nowrap;">
				学生大组长
			</td>
		</tr>
		<s:iterator value="#__Row.list" var="__studentRow" status="__studentStatus">
		<tr class="wtableContent">
			<% Object tmpStu=pageContext.findAttribute("#__Row");
			String studentId="";
			if(tmpStu!=null && tmpStu instanceof obj.annualTable.Student)
				studentId=((obj.annualTable.Student)tmpStu).getId();
			%>
			<!-- 序号 --><td style="width:13px;">
				<s:property value="%{#__studentStatus.count}" />
				<% Object tmpId=pageContext.findAttribute("__studentRow");
				if(tmpId!=null && tmpId instanceof obj.annualTable.Student){
					pageContext.setAttribute("_id",String.format("choose%s",((obj.annualTable.Student)tmpId).getId()));
					%>
					<div id="${_id}"></div>
				<% } %>
			</td>
			<!-- 内容 --><% i=0; %>
			<s:iterator value="%{#__studentRow.fieldsValue}" var="__Col">
				<% if(StudentFieldDisplay[i++]){ %>
				<td style="word-wrap:break-word;word-break:break-all;">
				<% }else{ %>
				<td style="word-wrap:break-word;word-break:break-all;display:none;">
				<% } %>
					<s:property value="#__Col" />
				</td>
			</s:iterator>
			<!-- 性别 --><td style="width:20px;">
				<s:property value="#__studentRow.sex" />
			</td>
			<!-- 指导老师 --><td style="width:145px;padding:0;">
				<s:select list="innerPersons"
				listKey="id" listValue="description"
				headerKey="" headerValue="-未定-"
				value="%{#__studentRow.teacherId}"
				disabled="true"
				style="width:100%;height:100%;border:0;background:#00000000;color:black;font-size:14px;" theme="simple" />
			</td>
			<!-- 推荐大组长 --><td style="width:36px;padding:0;border:0">
				<s:if test="#__studentRow.id==#__Row.t.region.studentGroupLeaderId"><!-- 已推荐 -->
					<s:submit value="✔"
						style="padding:0;margin:0;border:3px black double;background:gold;width:30px;height:30px;font-size:30px;line-height:5px;" theme="simple" />
				</s:if><s:else><!-- 未推荐 -->
					<s:submit value=" "
						style="padding:0;margin:0;border:3px black double;background:white;width:30px;height:30px;" theme="simple" />
				</s:else>
			</td>
		</tr></s:iterator>
		<tr><td height="35px" colspan="100" valign="top" /></tr>
	</s:iterator>
	</s:iterator>
	</tbody></table>
	


</div>





	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>