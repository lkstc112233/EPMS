<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EPMS-<s:property value="#session.role.name" />-全面确认布局规划（各实习基地学科接纳人数）（<s:property value="annual.year" />年）</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>


<body><center>
	<jsp:include page="/model/common_top.jsp" flush="true" /><%-- 动态包含  页面头 --%>
		
<div class="bag">
	<table style="width:80%"><tbody>
		<tr><td colspan="100">
			<div class="listHeader">
				全面确认布局规划（各实习基地学科接纳人数）（<s:property value="annual.year" />）
			</div>
		</td></tr>
		<tr><td colspan="100" style="width:20%;border:0;height:25px;">
			&nbsp;
		</td></tr>
	</tbody></table>
	
	<s:form action="function_PlanAllDesign_execute" method="post" theme="simple">
		<table class="wtable" style="cellpadding:0;cellspacing:0;table-layout:fixed;"><tbody>
			<tr class="wtableHeader" style="border:0;min-height:80px;height:80px;">
				<td style="width:12px;">大区</td>
				<td>基地名称</td>
				<s:iterator value="majors" var="__majorCol">
					<td style="width:20px;"><s:property value="#__majorCol.subject" /></td>
				</s:iterator>
				<td>备注</td>
			</tr>
			<!-- ###总数量### -->
			<tr class="wtableContent">
				<td colspan="2" style="font-weight:900;background:rgba(255,234,46,0.5);">
					<s:property value="#__regionRow.region.name"/>教育实习总人数
				</td>
				<s:iterator value="majorsCounts" var="__count">
					<td style="width:20px;padding:0px;font-weight:700;background:rgba(255,234,46,0.5);">
						<s:property value="#__count" />
					</td>
				</s:iterator>
				<td style="background:rgba(255,234,46,0.5);"></td>
			</tr>
			<!-- ###规划### -->
			<s:iterator value="list.list" var="__regionRow" status="__regionStatus">
				<s:iterator value="#__regionRow.list" var="__practiceBaseRow" status="__practiceBaseStatus">
					<tr class="wtableContent">
						<s:if test="#__practiceBaseStatus.index == 0">
							<s:set var="_colspan" value="%{#__regionRow.size}" />
							<td rowspan="${_colspan}" style="width:3%;">
								<s:property value="#__regionRow.t.name" />
							</td>
						</s:if>
						<td style="text-align:left;padding-left:10px;">
							<s:if test="#__practiceBaseRow.practiceBase.status">
								<div style="color:red;">
									<s:property value="#__practiceBaseRow.practiceBase.name" />
								</div>
							</s:if><s:else>
								<div>
									<s:property value="#__practiceBaseRow.practiceBase.name" />
								</div>
							</s:else>
						</td>
						<s:iterator value="majors" status="__majorStatus">
							<td style="width:20px;padding:0px;">
								<s:if test="numbers[#__majorStatus.index][#__regionStatus.index][#__practiceBaseStatus.index]==0">
									<s:textfield theme="simple"
										style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;"
										name="numbers[%{#__majorStatus.index}][%{#__regionStatus.index}][%{#__practiceBaseStatus.index}]"
										value="" />
								</s:if><s:else>
									<s:textfield theme="simple"
										style="width:100%;height:100%;margin:0px;padding:0px;text-align:center;font-size:14px;border:0px;"
										name="numbers[%{#__majorStatus.index}][%{#__regionStatus.index}][%{#__practiceBaseStatus.index}]"
										value="%{numbers[#__majorStatus.index][#__regionStatus.index][#__practiceBaseStatus.index]}" />
								</s:else>
							</td>
						</s:iterator>
						<!-- 备注 --><td style="padding:0px;">
							<s:textarea value="%{#__practiceBaseRow.practiceBase.remark}"
							name="list.list[%{#__regionStatus.index}].list[%{#__practiceBaseStatus.index}].practiceBase.remark"
							style="margin:0;padding:0;resize:none;border:0;width:100%;heigth:100%;" theme="simple" />
						</td>
					</tr>
				</s:iterator>
				<!-- ###总数量### -->
				<s:if test="#__regionRow.t.hx == null">
					<tr><td colspan="100">ERROR！</td></tr>
				</s:if><s:elseif test="#__regionRow.t.hx == true">
					<!-- 回乡的 -->
					<tr class="wtableContent">
						<td colspan="2" style="font-weight:700;background:rgba(255,234,46,0.22);">
							<s:property value="#__regionRow.t.name"/>合计
						</td>
						<s:iterator value="majors" status="__majorStatus">
							<!-- 相等 --><s:if test="majorsRegionsCountsIsError[#__majorStatus.index][#__regionStatus.index]==null">
								<td style="width:20px;padding:0px;font-weight:700;background:rgba(255,234,46,0.22);color:green;">
									<s:property value="majorsRegionsHxCounts[#__majorStatus.index][#__regionStatus.index]" />
								</td>
							<!-- 超出 --></s:if><s:elseif test="majorsRegionsCountsIsError[#__majorStatus.index][#__regionStatus.index]">
								<td style="width:20px;padding:0px;font-weight:700;background:rgba(255,234,46,0.22);color:red;">
									<s:property value="majorsRegionsHxCounts[#__majorStatus.index][#__regionStatus.index]" />
								</td>
							<!-- 不足 --></s:elseif><s:else>
								<td style="width:20px;padding:0px;font-weight:700;background:rgba(255,234,46,0.22);color:black;">
									<s:property value="majorsRegionsHxCounts[#__majorStatus.index][#__regionStatus.index]" />
								</td>
							</s:else>
						</s:iterator>
						<td style="background:rgba(255,234,46,0.22);"></td>
					</tr>
				</s:elseif><s:else>
					<!-- 非回乡的 --><s:if test="#__regionStatus.index == lastNHxRegionIndex">
					<tr class="wtableContent">
						<td colspan="2" style="font-weight:700;background:rgba(255,234,46,0.5);">
							<s:property value="#__regionRow.t.name"/>北京及周边地区实习人数
						</td>
						<s:iterator value="majorsNHxCounts" var="__nhxcount" status="__majorStatus">
							<s:if test="majorsRegionsCountsIsError[#__majorStatus.index][#__regionStatus.index] == null">
								<td style="width:20px;padding:0px;font-weight:700;background:rgba(255,234,46,0.5);color:green;">
									<s:property value="#__nhxcount" />
								</td>
							</s:if><s:elseif test="majorsRegionsCountsIsError[#__majorStatus.index][#__regionStatus.index]">
								<td style="width:20px;padding:0px;font-weight:700;background:rgba(255,234,46,0.5);color:red;">
									<s:property value="#__nhxcount" />
								</td>
							</s:elseif><s:else>
								<td style="width:20px;padding:0px;font-weight:700;background:rgba(255,234,46,0.5);color:black;">
									<s:property value="#__nhxcount" />
								</td>
							</s:else>
						</s:iterator>
						<td style="background:rgba(255,234,46,0.5);"></td>
					</tr>
					</s:if>
				</s:else>
			</s:iterator>
		</tbody></table>
		<table width="300px"><tbody>
			<tr><td>
				&nbsp;<s:submit value="提交修改" cssClass="button"/>
			</td></tr>
		</tbody></table>
	</s:form>
	
</div>

	<jsp:include page="/model/common_bottom.jsp" flush="true" /><%-- 动态包含  页面头 --%>
</center></body>
</html>