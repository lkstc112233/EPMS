<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table width="80%" border="0" cellspacing="0" cellpadding="0"><tbody>
		<tr>
		<!--<td width="10%"></td>  左缝 -->
			<td valign="top">
				<div class="listHeader">
					<div class="listHeaderLeft">人员管理功能</div>
				</div>
				<ul class="listContent">
					<li>
						<span class="time">[All time]</span>
						<a href='<s:url action="sql_operation_select"/>'>静态数据库表管理</a>
					</li>
				</ul>
			</td>
	<!--	<td width="2%"></td> <!-- 中缝 -->
	<!--	<td valign="top">
				<div class="zi0">
					<span class="lanmuzi">实习风采</span>
					<a class="gd0" href="http://jsjysj.bnu.edu.cn/sxfc/">&gt;&gt;更多&nbsp;&nbsp;&nbsp;</a>
				</div>
				<ul class="ul_list">
					<li>
						<span class="newtime">[2017-06-01]</span>
						<a href="sxfc/32069.html" title="我怀念的" target="_blank"> 我怀念的 </a>
					</li>
				</ul>
			</td>
	-->
	<!--<td width="10%"></td>  右缝 -->
		</tr>
	</tbody></table>
</div>

	<jsp:include page="common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>