<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="bag">
	<% String noBackButton=request.getParameter("noBackButton"); 
	 if(noBackButton==null || noBackButton.isEmpty() || noBackButton.toUpperCase().equals("FALSE")){
	 %>
	<table style="width: 180px; border: 0; cellspacing: 0; cellpadding: 0"><tbody>
		<tr>
			<td height="100px" width="100%" colspan="3" valign="top" />
		</tr><tr>
			<td>
				<!--  a href='JavaScript:history.back(1)' class="button">返回</a -->
				<s:form action="login" method="post" theme="simple">
					<s:hidden name="back" value="true" theme="simple" />
					<s:submit value="返回" cssClass="button" />
				</s:form>
			</td>
		</tr>
	</tbody></table>
	<% } %>

	<table style="width: 180px; border: 0; cellspacing: 0; cellpadding: 0">
		<tbody>
			<tr>
				<td height="160px" width="100%" colspan="3" valign="top" />
			</tr>
		</tbody>
	</table>
</div>
<div class="footer">
	<div class="footer_center">
		友情链接: <a href="http://www1.bnu.edu.cn/xiaoban/index.html"
			title="北京师范大学校长办公室" target="_blank"> 北京师范大学校长办公室 </a> <a
			href="http://www.bnu.edu.cn/" title="北京师范大学" target="_blank">
			北京师范大学 </a>
	</div>
	<div class="footer_center">北京师范大学教务处 新街口外大街19号，北京·中国(100875)</div>
	<div class="footer_center">E-mail: bnuwangfy@foxmail.com    Tel: 010-58802413</div>
</div>
