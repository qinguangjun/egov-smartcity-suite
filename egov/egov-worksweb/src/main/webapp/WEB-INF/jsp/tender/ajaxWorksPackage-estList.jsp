<%@ page contentType="text/json" %>
<%@ taglib prefix="s" uri="/struts-tags" %>  
<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2015>  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>

<table width="100%" border="0" cellspacing="0" cellpadding="0" id="estimateListTable" name="estimateListTable">
<s:iterator var="e" value="abstractEstimateList" status="s">
	<tr>
		<input type="hidden" name="estId" id="estId" value="<s:property value='%{id}'/>" />
		<input type="hidden" name="wvIncldTaxes" id="wvIncldTaxes" value="<s:property value='%{workValueIncludingTaxes.formattedString}'/>" />
		<td width="5%"  class="whitebox3wk">&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="text" name="slNo" id="slNo" size="1" disabled="true" value='<s:property value='%{#s.index+1}' />'/>
		</td>
		<td width="15%" class="whitebox3wk">&nbsp;&nbsp;&nbsp;&nbsp;
			<s:property value='%{estimateNumber}' />
		</td>
		<td width="40%" class="whiteboxwkwrap" style="WORD-BREAK:BREAK-ALL">
			<s:property value='%{name}' />
		</td>
		<td width="10%" class="whitebox3wk">&nbsp;&nbsp;
			<s:date name="estimateDate" format="dd/MM/yyyy" /> 
		</td>
		<td width="15%" class="whitebox3wk">
			<div align="right">
				<s:property value='%{workValueIncludingTaxes.formattedString}' />
			</div>
		</td>
		<td align="right" width="10%" class="headingwk" style="border-left-width: 0px">
       		<a id="delHref" href="#" onclick="deleterow(this)">
       		<img border="0" alt="Delete Estimates" src="/egworks/resources/erp2/images/cancel.png" /></a>
       	</td>
	</tr>
</s:iterator>	
<tr><td colspan="5" style="background-color:#F4F4F4;" align="right"><b>Total:</b>&nbsp;
<input type="text" size="8" name="totalAmount" id="totalAmount" value="<s:property value="%{worktotalValue.formattedString}" />" 
class="amount" readonly="readonly"/></td>				
<td colspan="5" style="background-color:#F4F4F4;">&nbsp;</td>
</tr>	
</table>
