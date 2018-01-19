<%@page import="obj.annualTable.ListOfPracticeBaseAndStudents"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" />-教育实习基地经费（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table style="width:80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				经费-部院系实习经费
				（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
		<tr><td colspan="100" style="text-align:center;">
			<span style="float:left;margin-left:50px;">
			<s:form action="function_moneyMajor_MoneyMajorTrainInfo_display" method="post" theme="simple">
				<s:submit value="查看/修改交通费标准" theme="simple"
				style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
			</s:form>
			</span>
			<span style="float:left;margin-left:50px;">
			<s:form action="function_moneyMajor_Export_download" method="post" theme="simple">
				<s:submit value="下载部院系经费汇总表" theme="simple"
				style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
			</s:form>
			</span>
			<span style="float:left;margin-left:50px;">
			<s:form action="function_moneyMajor_ExportAllMoneyMajor_download" method="post" theme="simple">
				<s:submit value="下载所有部院系经费包干单" theme="simple"
				style="width:auto;background:white;border:double 6px #0071bc;font-weight:600;height:40px;padding-left:5px;padding-right:5px;"/>
			</s:form>
			</span>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	
	<table class="wtable"><tbody>
	<tr class="wtableHeader"><td style="border:double 6px #0071bc;">
	<ul class="listContent"><li onclick="document.getElementById('Table_2').style.display=document.getElementById('Table_2').style.display.length<=0?'none':''"
	style="font-size:15px;font-weight:700;background:inherit;">
		部院系经费汇总表&nbsp;
		<span class="time">（点击部院系名称查看该部院系经费包干单及学生补助明细表）</span>
	</li></ul>
	</td></tr>
	</tbody></table>
	<table class="wtable" style="cellpadding:0;cellspacing:0;table-layout:fixed;"><tbody id="Table_2" style="display:;">
		<tr class="wtableHeader" style="height:auto;">
			<td rowspan="2">部院系</td>
			<td colspan="3">误餐补助</td>
			<td colspan="3">交通补助</td>
			<td colspan="2">其他补助</td>
		</tr>
		<tr class="wtableHeader" style="height:auto;">
			<td>北京市（不住宿）</td>
			<td>北京市（住宿）</td>
			<td>外地</td>
			<td>北京市（不住宿）</td>
			<td>北京市（住宿）</td>
			<td>外地</td>
			<td>实习教材及复印费</td>
			<td>机动经费</td>
		</tr>
		<s:iterator value="schools" var="__school" status="__schoolStatus">
			<tr class="wtableContent">
				<!-- 部院系 -->
				<td style="font-weight:800;">
					<a href="<s:url action='function_moneyMajor_MoneyMajorInfo_display'/>?school=<s:property value='#__school.name'/>">
						<s:property value="#__school.name" />
					</a>
				</td>
				<!-- 误餐补助-->
				<!-- 交通补助-->
				<!-- 其他补助-->
			</tr>
		</s:iterator>
	</tbody></table>
	

</div>







	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>