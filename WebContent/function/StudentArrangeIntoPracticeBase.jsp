<%@page import="obj.annualTable.list.Leaf"%>
<%@page import="obj.annualTable.list.Node"%>
<%@page import="obj.annualTable.list.PracticeBaseWithRegion"%>
<%@page import="obj.annualTable.*"%>
<%@page import="obj.Pair"%>
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
				window.location.href.lastIndexOf('/')<0?window.location.length:window.location.href.lastIndexOf('/'))
				)+'/function_StudentArrangeIntoPracticeBase_display.action?majorName='
				+this.value"
				/>
				实习生到实习基地（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	
	<% boolean[] StudentFieldDisplay=new boolean[]{
			false,true,true,true,true,
			false,false,false,
			true,true,true,false,
			false,false,false,false};
		int i; %>
		
	<!-- ###### 未分配实习生 ###### -->
	<table width="80%"><tbody>
		<s:if test="list.undistributedStudents.size() == 0">
			<tr><td colspan="100">
				<div class="listHeader" style="width:80%;background:linear-gradient(to right,#00f500,rgba(0,0,0,0));border:0;
				color:red;font-size:16px;">
					实习生已分配完毕！
				</div>
			</td></tr>
		</s:if><s:else>
			<tr><td colspan="100">
				<div class="listHeader" style="width:80%;background:linear-gradient(to right,gold,rgba(0,0,0,0));border:0;
				color:red;font-size:16px;">
					未分配实习基地的实习生
				</div>
			</td></tr>
			<!-- 表头 --><tr class="wtableHeader">
				<td style="width:25px;">选择</td>
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
				<td style="word-wrap:break-word;word-break:break-all;">
					是否回生源地实习
				</td>
				<td style="word-wrap:break-word;word-break:break-all;">
					性别
				</td>
			</tr>
			<s:form action="function_StudentArrangeIntoPracticeBase_execute" method="post" theme="simple">
				<s:iterator value="list.undistributedStudents" var="__studentRow" status="__studentStatus">
				<tr class="wtableContent">
					<!-- 选择 --><td style="width:25px;padding:0;border:0;">
						<s:checkbox name="checkBox[%{#__studentStatus.index}]" id="%{#__Status.index}_%{#__studentStatus.index}"
						style="width:100%;height:100%;margin:0;" theme="simple" />
					</td>
					<!-- 序号 --><td style="width:13px;">
						<s:property value="%{#__studentStatus.count}" />
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
					<!-- 是否回生源地实习 --><td style="width:55px;font-size:30px;line-height:5px;">
						<s:if test="#__studentRow.hxyx == true">
							✔
						</s:if><s:else>
							✘
						</s:else>
					</td>
					<!-- 性别 --><td style="width:55px;">
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
			</s:form>
		</s:else>
		<tr><td height="45px" colspan="100" valign="top" /></tr>
	</tbody></table>
	<!-- ###### 已分配实习生 ###### -->
	<table width="80%"><tbody>
	<s:iterator value="list.list" var="__rpRow" status="__rpStatus">
	<s:iterator value="#__rpRow.list" var="__Row" status="__Status">
		<tr>
			<s:if test="#__Status.index == 0">
				<% @SuppressWarnings("unchecked")
				Node<Region,Leaf<Pair<PracticeBaseWithRegion,Plan>,Student>> rp=
				(Node<Region,Leaf<Pair<PracticeBaseWithRegion,Plan>,Student>>)
					request.getAttribute("__rpRow");
				int tmpStudentCnt=0;
				for(Leaf<?,Student> iter:rp.getList()) tmpStudentCnt+=iter.getSize();
				pageContext.setAttribute("_rowspan",
						tmpStudentCnt+rp.getSize()*3-1); %>
				<td rowspan="${_rowspan}" class="listHeader"
				style="width:30px;background:#0071bc;text-indent:0px;text-align:center;" >
					<s:property value="#__rpRow.t.name" />
				</td>
			</s:if>
			<td colspan="100">
				<div class="listHeader" style="width:80%;background:linear-gradient(to right,#0071bc,rgba(0,0,0,0));border:0;">
					<s:if test="#__Row.t.practiceBase.status">
						<span style="float:left;color:red;">
							<s:property value="#__Row.t.practiceBase.name" />
						</span>
					</s:if><s:else>
						<span style="float:left;">
							<s:property value="#__Row.t.practiceBase.name" />
						</span>
					</s:else>
					<span style="font-size:12px;margin-left:20px;">
						<s:if test="#__Row.t.practiceBase.hx">
							回生源地实习基地
						</s:if><s:else>
							北京及周边实习基地
						</s:else>
					</span>
					<span style="font-size:12px;margin-left:30px;">
						<s:if test="(#__Row.t.plan.refuseSex!=null&&#__Row.t.plan.refuseSex.isEmpty()!=true)
						 || (#__Row.t.plan.refuseNation!=null&&#__Row.t.plan.refuseNation.isEmpty()!=true)
						 || (#__Row.t.practiceBase.refuseNation!=null&&#__Row.t.practiceBase.refuseNation.isEmpty()!=true)">
							不接收：&nbsp;
						</s:if>
					</span>
					<span style="font-size:12px;margin-left:0px;word-wrap: break-word;word-break: break-all;color:white;">
						<s:if test="(#__Row.t.plan.refuseSex!=null&&#__Row.t.plan.refuseSex.isEmpty()!=true)">
							性别(<s:property value="#__Row.t.plan.refuseSex" />)
						</s:if>
						<s:if test="(#__Row.t.plan.refuseNation!=null&&#__Row.t.plan.refuseNation.isEmpty()!=true)">
							<s:if test="(#__Row.t.plan.refuseSex!=null&&#__Row.t.plan.refuseSex.isEmpty()!=true)">
								、
							</s:if>
							<s:property value="#__Row.t.plan.refuseNation" />
						</s:if>
						<s:if test="(#__Row.t.practiceBase.refuseNation!=null&&#__Row.t.practiceBase.refuseNation.isEmpty()!=true)">
							<s:if test="(#__Row.t.plan.refuseSex!=null&&#__Row.t.plan.refuseSex.isEmpty()!=true)
							&& (#__Row.t.plan.refuseNation!=null&&#__Row.t.plan.refuseNation.isEmpty()!=true)">
								、
							</s:if>
							<s:property value="#__Row.t.practiceBase.refuseNation" />
						</s:if>
					</span>
					<s:if test="#__Row.size < #__Row.t.plan.number">
						<div class="right" style="color:red;font-size:16px;">
							人数:<s:property value="#__Row.size"/>/<s:property value="#__Row.t.plan.number"/>
						</div>
					</s:if><s:else>
						<div class="right" style="color:black;">
							人数:<s:property value="#__Row.size"/>/<s:property value="#__Row.t.plan.number"/>
						</div>
					</s:else>
				</div>
			</td>
		</tr>
		<s:form action="function_StudentArrangeIntoPracticeBase_delete" method="post" theme="simple">
			<s:if test="#__Row.size == 0">
				<tr class="wtableContent">
					<td colspan="100" style="border-bottom:#000 solid 3px;border-top: double;height:30px">	
						尚未分配实习学生！
					</td>
				</tr>
			</s:if><s:else>
				<s:iterator value="#__Row.list" var="__studentRow" status="__studentStatus">
				<tr class="wtableContent">
					<!-- 选择 --><td style="width:25px;padding:0;border:0">
						<s:checkbox name="checkBox[%{#__studentStatus.index}]" id="%{#__Status.index}_%{#__studentStatus.index}"
						style="width:100%;height:100%;margin:0;" theme="simple" />
					</td>
					<!-- 序号 --><td style="width:13px;">
						<s:property value="%{#__studentStatus.count}" />
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
				</tr></s:iterator>
				<tr class="wtableContent">
					<td colspan="100" style="border-bottom:#000 solid 3px;border-top: double;height:30px">	
						从该基地移出：
						<s:hidden name="practiceBaseName" value="%{#__Row.t.practiceBase.name}" />
						<s:submit value="移出" cssClass="buttonInline"
						style="padding-top:0;height:auto;" theme="simple"/>
					</td>
				</tr>
			</s:else>
			<tr><td height="35px" colspan="100" valign="top" /></tr>
		</s:form>
	</s:iterator>
	</s:iterator>
	</tbody></table>
	


</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>