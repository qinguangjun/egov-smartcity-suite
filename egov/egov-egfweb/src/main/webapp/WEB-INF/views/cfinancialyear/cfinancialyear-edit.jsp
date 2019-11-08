<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
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
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
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
  ~
  --%>



<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<form:form role="form" action="../update"
	modelAttribute="CFinancialYear" id="cFinancialYearform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	<%@ include file="cfinancialyear-form.jsp"%>
  <input type="hidden" name="CFinancialYear" value="${CFinancialYear.id}" /> 
	</div>
	</div>
	</div>
	</div>
	<div class="form-group">
		<div class="text-center">
			<button type='submit' class='btn btn-primary' id="buttonSubmit" 
								onchange="isSpecialChar(this);" onblur="isSpecialChar(this);" onclick="isSpecialChar(this);">
				<spring:message code='lbl.update' />
			</button>
			<a href='javascript:void(0)' class='btn btn-default'
				onclick='self.close()'><spring:message code='lbl.close' /></a>
		</div>
	</div>
</form:form>
<script>
	$('#buttonSubmit').click(function(e) {
		if ($('form').valid()) {
		} else {
			e.preventDefault();
		}
	});
	function replaceSpecialChar(e) {
	    var k;
	    document.all ? k = e.keyCode : k = e.which;
	    return ((k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32 || (k >= 48 && k <= 57));
	}
	function isSpecialChar(Obj){
		alert("here");
		var finYear = document.getElementById('finYearRange').value;
		var startDate = document.getElementById('startingDate')value;
		var endingDate = document.getElementById('endingDate')value;
	    var pattern=/[`~!@#$%^&*()_|+\-=?;:'",.<>\{\}\[\]\\\/]/gi;
	    if(document.getElementById('finYearRange').value.match(pattern)){
            var replacedNumber = finYear.replace(/[`~!@#$%^&*()_|+\=ï¿½ï¿½?;:><'",.<>\{\}\[\]\\\]/gi, '');
            document.getElementById('finYearRange').value = replacedNumber;
            return false;
        }
	    if(document.getElementById('startingDate').value.match(pattern)){
            var replacedNumber = startDate.replace(/[`~!@#$%^&*()_|+\=ï¿½ï¿½?;:><'",.<>\{\}\[\]\\\]/gi, '');
            document.getElementById('startingDate').value = replacedNumber;
            return false;
        }
	    if(document.getElementById('endingDate').value.match(pattern)){
            var replacedNumber = endingDate.replace(/[`~!@#$%^&*()_|+\=ï¿½ï¿½?;:><'",.<>\{\}\[\]\\\]/gi, '');
            document.getElementById('endingDate').value = replacedNumber;
            return false;
        }
	}
</script>