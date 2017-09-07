<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-导入免费师范生数据</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="../common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table width="80%"><tbody>
		<tr>
			<td valign="top">
				<div class="listHeader">
					Student
				</div>
			</td>
		</tr>
		<tr><td valign="middle">
			<ul class="listContent">
				<li>
					<a href="<s:url action='jwc_function_drmfsfssj_download'/>?tableName=Student">下载模板</a>
				</li>
				<li>
					<s:form action="jwc_function_drmfsfssj_download" method="post" theme="simple">
						<s:hidden name="tableName" value="Student" />
						<s:submit value="下载模板" cssClass="title_button" style="width:auto;"/>
					</s:form>
				</li>
				<li>
					<s:form action="jwc_function_drmfsfssj_upload" method="post" theme="simple" enctype="multipart/form-data">
						<s:hidden name="tableName" value="Student" />
						<s:file label="上传数据" theme="simple" name="file" />
						<!-- cssClass="button" style="background-color:rgba(0,0,0,0);color:#000" -->
					</s:form>
				</li>
			</ul>
		</td></tr>
	</tbody></table>
	
	<s:form action="login" method="post" cssClass="myform">
		<s:submit value="返回" cssClass="button"/>
	</s:form>
</div>

	<jsp:include page="../common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>