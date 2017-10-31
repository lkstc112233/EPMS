<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
    
	
<%
Object tmp=session.getAttribute("errorTips");
String errorTips = tmp==null?"":tmp.toString();
session.removeAttribute("errorTips");
%>
<script>
	var x="<%=errorTips%>";
	if(x!=""){
		alert(x);
	}
</script>

<div>
	<div class="title">
		<div class="right">
			<table id="title_tabel"><tbody>
				<tr height="">
	         		<td height="60">
	         		</td>
	       		</tr>
	            <tr>
					<s:if test="#session.inner!=null">
						<td>
							<div class="title_label">
							欢迎
							<a href='<s:url action="inner_info"/>'><s:property value="#session.inner.name"/></a>
						<!--
							<s:form action="inner_info" method="post">
								<s:submit value="%{#session.inner.name}" />
							</s:form>
						-->
							！
							</div>
						</td>
						<!--
						<td>
							<s:form action="inner_info" method="excute">
								<s:submit value="个人信息" cssClass="title_button"/>
							</s:form>
						</td>
						-->
					</s:if>
					<s:if test="#session.inner==null">
						<td colspan="2">
		            		<a href="login.jsp" class="title_label">登录</a>
		            	</td>
					</s:if><s:else>
						<td>
							<s:form action="login" method="post" cssClass="myform">
								<s:submit value="返回" cssClass="title_button"/>
							</s:form>
						</td><td>
							<s:form action="logout"  method="get">
								<s:submit value="注销" cssClass="title_button" />
							</s:form>
						</td>
					</s:else>
					
					<td width="160px"></td>
	           </tr>
	     </tbody></table>
		</div>
	</div>
	<div class="title_xian"></div>
	
	<div class="bag">
		<table style="width:100%;border:0;cellspacing:0;cellpadding:0"><tbody>
			<tr><td height="12px" width="100%" colspan="3" valign="top" /></tr>
		</tbody></table>
	</div>
</div>