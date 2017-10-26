<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-教务处-导入免费师范生数据（<s:property value="year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="../common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table width="80%"><tbody>
		<!-- ################# -->
		<tr><td>
			<div class="listHeader">
				免费师范生
			</div>
		</td></tr>
		<tr><td>
			<ul class="listContent">
				<li>
					<span class="time">	<!-- 模板下载 -->
						[<a href="<s:url action='jwc_function_drmfsfssj_download'/>?tableName=Student">
						下载模板</a>]
					</span>
					<s:form action="jwc_function_drmfsfssj_upload" method="post" theme="simple" enctype="multipart/form-data">
						<s:hidden name="tableName" value="Student" />
						<s:file label="上传数据" theme="simple" name="uploadFile" class="buttonInline"/>
						<!-- cssClass="button" style="background-color:rgba(0,0,0,0);color:#000" -->
						<s:submit value="上传" cssClass="buttonInline"/>
						<s:token />
					</s:form>
				</li>
			</ul>
		</td></tr>
		<!-- ################# -->
		<tr><td>
			<div class="listHeader">查询信息（<s:property value="year" />年）</div>
		</td></tr>
		<s:form action="jwc_function_drmfssfsj_execute" method="get">
			<s:hidden name="year" value="Student" />
			<tr><td><s:textfield label="id" name="student.id"/></td></tr>
			<tr><td><s:textfield label="name" name="student.name"/></td></tr>
			
			<tr><td>
				<s:submit value="查询" cssClass="buttonInline" />
			</td></tr>
		</s:form>
		<!-- ################# -->
	</tbody></table>
	
	
	
	
	
	<table style="width:60%;border:0;cellspacing:0;cellpadding:0"><tbody>
		<tr><td height="100px" width="100%" colspan="3" valign="top" /></tr>
		<tr><td><a href='JavaScript:history.back(1)' class="button">返回</a></td></tr>
	</tbody></table>
</div>

	<jsp:include page="../common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>