<%@page import="obj.annualTable.ListOfPracticeBaseAndStudents"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" />-入校督导详细信息（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table class="wtable"><tbody>
	<tr class="wtableHeader"><td style="border:double 6px #0071bc;">
	<ul class="listContent"><li onclick="document.getElementById('Table_1').style.display=document.getElementById('Table_1').style.display.length<=0?'none':''"
	style="font-size:15px;font-weight:700;background:inherit;">
		入校督导详细信息
	</li></ul>
	</td></tr>
	</tbody></table>
	<table class="wtable" style="cellpadding:0;cellspacing:0;table-layout:fixed;">
	<tbody id="Table_1" style="display:;">
		<tr class="wtableHeader" style="height:auto;">
			<td style="border:solid 1px;width:3%;" rowspan="2">大区</td>
			<td style="border:solid 1px;" rowspan="2">基地名称</td>
			<td style="border:solid 1px;" colspan="5">入校督导老师</td>
			<td style="border:solid 1px;width:28%;" colspan="7">经费</td>
			<td style="border:solid 1px;" rowspan="2">备注</td>
			<td style="border:solid 1px;" rowspan="2">保存</td>
		</tr>
		<tr class="wtableHeader" style="height:auto;border:solid 1px;">
			<td style="border:solid 1px;">部院系</td>
			<td style="border:solid 1px;">姓名</td>
			<td style="border:solid 1px;">工号</td>
			<td style="border:solid 1px;">手机</td>
			<td style="border:solid 1px;">邮箱</td>
			<td style="border:solid 1px;width:4%;">机票费</td>
			<td style="border:solid 1px;width:4%;">订票费</td>
			<td style="border:solid 1px;width:4%;">保险费</td>
			<td style="border:solid 1px;width:4%;">火车票</td>
			<td style="border:solid 1px;width:4%;">住宿费</td>
			<td style="border:solid 1px;width:4%;">住宿天数</td>
			<td style="border:solid 1px;width:4%;">其他费用</td>
		</tr>
		<s:iterator value="regionAndPracticeBaseAndInnerPerson.list" var="__rpRow" status="__rpStatus">
		<s:iterator value="#__rpRow.list" var="__pairRow" status="__pairStatus">
			<tr class="wtableContent">
				<s:set var="_colspan" value="%{#__rpRow.size}" />
				<s:if test="#__pairStatus.index == 0">
					<td rowspan="${_colspan}" style="width:3%;background-color:white;">
						<s:property value="#__rpRow.region.name" />
					</td>
				</s:if>
				<!-- 基地名称 -->
				<td style="padding:0;background-color:lightyellow;word-wrap:break-word;font-weight:bold;">
					<s:property value="#__pairRow.practiceBase.name" />
				</td>
				<!-- 督导 -->
				<td style="padding:0;background-color:white;word-wrap:break-word;">
					<s:property value="#__pairRow.supervisor[typeIndex].school" />
				</td>
				<td style="padding:0;background-color:lightyellow;word-wrap:break-word;font-weight:bold;">
					<s:property value="#__pairRow.supervisor[typeIndex].name" />
				</td>
				<td style="padding:0;background-color:white;word-wrap:break-word;">
					<s:property value="#__pairRow.supervisor[typeIndex].id" />
				</td>
				<td style="padding:0;background-color:white;word-wrap:break-word;">
					<s:property value="#__pairRow.supervisor[typeIndex].phone" />
				</td>
				<td style="padding:0;background-color:white;word-wrap:break-word;">
					<s:property value="#__pairRow.supervisor[typeIndex].email" />
				</td>
				<!-- 经费 -->
				<s:form action="function_Supervise0Page_execute" method="post" theme="simple">
					<td style="padding:0;background-color:white;">
						<s:if test="#__pairRow.supervise[typeIndex].moneyPlane == 0">
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyPlane"
							value=""
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:if><s:else>
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyPlane"
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:else>
					</td>
					<td style="padding:0;background-color:white;">
						<s:if test="#__pairRow.supervise[typeIndex].moneyBooking == 0">
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyBooking"
							value=""
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:if><s:else>
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyBooking"
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:else>
					</td>
					<td style="padding:0;background-color:white;">
						<s:if test="#__pairRow.supervise[typeIndex].moneyInsurance == 0">
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyInsurance"
							value=""
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:if><s:else>
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyInsurance"
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:else>
					</td>
					<td style="padding:0;background-color:white;">
						<s:if test="#__pairRow.supervise[typeIndex].moneyTrain == 0">
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyTrain"
							value=""
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:if><s:else>
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyTrain"
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:else>
					</td>
					<td style="padding:0;background-color:white;">
						<s:if test="#__pairRow.supervise[typeIndex].moneyAccommodation == 0">
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyAccommodation"
							value=""
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:if><s:else>
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyAccommodation"
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:else>
					</td>
					<td style="padding:0;background-color:white;">
						<s:if test="#__pairRow.supervise[typeIndex].moneyAccommodationNum == 0">
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyAccommodationNum"
							value=""
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:if><s:else>
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyAccommodationNum"
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:else>
					</td>
					<td style="padding:0;background-color:white;">
						<s:if test="#__pairRow.supervise[typeIndex].moneyElse == 0">
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyElse"
							value=""
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:if><s:else>
							<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].moneyElse"
							style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
						</s:else>
					</td>
					<td style="padding:0;background-color:white;">
						<s:textfield name="regionAndPracticeBaseAndInnerPerson.list[%{#__rpStatus.index}].list[%{#__pairStatus.index}].supervise[%{typeIndex}].remark"
						style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;" />
					</td>
					<td>
						<s:hidden name="typeIndex" value="%{typeIndex}" />
						<s:hidden name="practiceBaseName" value="%{#__pairRow.practiceBase.name}" />
						<s:submit value="保存"
						theme="simple" />
					</td>
				</s:form>
			</tr>
		</s:iterator></s:iterator>
	</tbody></table>
	
</div>








<% //跳转到当前操作的条目,choose[0]表示块index、choose[1]表示行号
Object tmp=pageContext.findAttribute("choose");
String x=null;
if(tmp!=null){
	if(tmp instanceof String[] && ((String[])tmp).length>=3)
		x=((String[])tmp)[1];//[1]表示学生
}
if(x!=null && !x.isEmpty()){ %>
	<script>
		var jumpX=document.getElementById("choose<%=x%>");
		if(jumpX!=null)
			jumpX.scrollIntoView();
	</script>
<% } %>



	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>