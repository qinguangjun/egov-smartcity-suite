/*******************************************************************************
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 * 	1) All versions of this program, verbatim or modified must carry this
 * 	   Legal Notice.
 *
 * 	2) Any misrepresentation of the origin of the material is prohibited. It
 * 	   is required that all modified versions of this material be marked in
 * 	   reasonable ways as different from the original version.
 *
 * 	3) This license does not grant any rights to any user of the program
 * 	   with regards to rights under trademark law for use of the trade names
 * 	   or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 ******************************************************************************/
package org.egov.web.actions.voucher;

import static org.egov.infstr.utils.DateUtils.getFormattedDate;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.CGeneralLedger;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.utils.EntityType;
import org.egov.egf.commons.EgovCommon;
import org.egov.infra.admin.master.entity.City;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infstr.utils.DateUtils;
import org.egov.model.bills.EgBillregistermis;
import org.egov.services.bills.BillsService;
import org.egov.services.budget.BudgetAppropriationService;
import org.egov.utils.Constants;
import org.egov.utils.ReportHelper;
import org.springframework.beans.factory.annotation.Autowired;

@Results(value = {
        @Result(name = "PDF", type = "stream", location = "inputStream", params = { "inputName", "inputStream",
                "contentType", "application/pdf", "contentDisposition", "no-cache;filename=JournalVoucherReport.pdf" }),
        @Result(name = "XLS", type = "stream", location = "inputStream", params = { "inputName", "inputStream",
                "contentType", "application/xls", "contentDisposition", "no-cache;filename=JournalVoucherReport.xls" }),
        @Result(name = "HTML", type = "stream", location = "inputStream", params = { "inputName", "inputStream",
                "contentType", "text/html" }) })
@ParentPackage("egov")
public class JournalVoucherPrintAction extends BaseFormAction {
    String jasperpath = "/reports/templates/journalVoucherReport.jasper";
    private static final long serialVersionUID = 1L;
    private static final String PRINT = "print";
    private CVoucherHeader voucher = new CVoucherHeader();
    List<Object> voucherReportList = new ArrayList<Object>();
    List<Object> budgetReportList = new ArrayList<Object>();
    InputStream inputStream;
    ReportHelper reportHelper;
    Long id;
    List<WorkFlowHistoryItem> inboxHistory = new ArrayList<WorkFlowHistoryItem>();
    private CityService cityService;
    private BillsService billsManager;
    private static final String ACCDETAILTYPEQUERY = " from Accountdetailtype where id=?";
    private BudgetAppropriationService budgetAppropriationService;
    private @Autowired EgovCommon egovCommon;

    public void setBillsService(final BillsService billsManager) {
        this.billsManager = billsManager;
    }

    public void setCityService(final CityService cityService) {
        this.cityService = cityService;
    }

    public Long getId() {
        return id;
    }

    public void setReportHelper(final ReportHelper helper) {
        reportHelper = helper;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public List<Object> getVoucherReportList() {
        return voucherReportList;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String execute() {
        return exportHtml();
    }

    @Action(value = "/voucher/journalVoucherPrint-ajaxPrint")
    public String ajaxPrint() {
        return exportHtml();
    }

    @Override
    public void prepare() {
        populateVoucher();
    }

    @Override
    public Object getModel() {
        return voucher;
    }

    @Action(value = "/voucher/journalVoucherPrint-print")
    public String print() {
        return PRINT;
    }

    private void populateVoucher() {
        if (!StringUtils.isBlank(parameters.get("id")[0])) {

            final Long id = Long.valueOf(parameters.get("id")[0]);
            final CVoucherHeader voucherHeader = (CVoucherHeader) persistenceService.find(
                    "from CVoucherHeader where id =?", id);
            if (voucherHeader != null) {
                voucher = voucherHeader;
                generateVoucherReportList();
            }
        }
    }

    private void generateVoucherReportList() {
        if (voucher != null) {
            for (final CGeneralLedger vd : voucher.getGeneralledger())
                if (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(vd.getCreditAmount().doubleValue())) == 0) {
                    final VoucherReport voucherReport = new VoucherReport(persistenceService, Integer.valueOf(voucher
                            .getId().toString()), vd, egovCommon);
                    voucherReportList.add(voucherReport);
                }

            for (final CGeneralLedger vd : voucher.getGeneralledger())
                if (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(vd.getDebitAmount().doubleValue())) == 0) {
                    final VoucherReport voucherReport = new VoucherReport(persistenceService, Integer.valueOf(voucher
                            .getId().toString()), vd, egovCommon);
                    voucherReportList.add(voucherReport);
                }
        }
    }

    public String getFundName() {
        if (voucher != null && voucher.getFundId() != null) {
            // persistenceService.setType(Fund.class);
            final Fund fund = (Fund) persistenceService.find("from Fund where id=? ", voucher.getFundId().getId());
            return fund == null ? "" : fund.getName();
        }
        return "";
    }

    public String getDepartmentName() {
        if (voucher != null && voucher.getVouchermis() != null && voucher.getVouchermis().getDepartmentid() != null) {
            // persistenceService.setType(Department.class);
            final Department dept = (Department) persistenceService.find("from Department where id=? ", voucher
                    .getVouchermis().getDepartmentid().getId());
            return dept == null ? "" : dept.getName();
        }
        return "";
    }

    @Action(value = "/voucher/journalVoucherPrint-exportPdf")
    public String exportPdf() throws JRException, IOException {
        inputStream = reportHelper.exportPdf(inputStream, jasperpath, getParamMap(), voucherReportList);
        return "PDF";
    }

    public String exportHtml() {
        inputStream = reportHelper.exportHtml(inputStream, jasperpath, getParamMap(), voucherReportList, "px");
        return "HTML";
    }

    @Action(value = "/voucher/journalVoucherPrint-exportXls")
    public String exportXls() throws JRException, IOException {
        inputStream = reportHelper.exportXls(inputStream, jasperpath, getParamMap(), voucherReportList);
        return "XLS";
    }

    protected Map<String, Object> getParamMap() {
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("fundName", getFundName());
        paramMap.put("departmentName", getDepartmentName());
        paramMap.put("voucherNumber", getVoucherNumber());
        paramMap.put("voucherDate", getVoucherDate());
        paramMap.put("voucherDescription", getVoucherDescription());
        if (voucher != null && voucher.getState() != null)
            loadHistory(voucher.getState());
        paramMap.put("workFlowHistory", inboxHistory);
        paramMap.put("workFlowJasper",
                reportHelper.getClass().getResourceAsStream("/reports/templates/workFlowHistoryReport.jasper"));
        final HttpServletRequest request = ServletActionContext.getRequest();
        final HttpSession session = request.getSession();
        final City cityWebsite = cityService.getCityByURL((String) session.getAttribute("cityurl"));
        String billType = billsManager.getBillTypeforVoucher(voucher);
        if (null == billType)
            billType = "General";
        else if ("Works".equalsIgnoreCase(billType))
            billType = "Contractor";
        else if ("".equalsIgnoreCase(billType))
            billType = "General";
        if ("Purchase".equalsIgnoreCase(billType))
            billType = billsManager.getBillSubTypeforVoucher(voucher);
        final EgBillregistermis billRegistermis = (EgBillregistermis) persistenceService.find(
                "from EgBillregistermis where voucherHeader.id=?", voucher.getId());
        final StringBuffer cityName = new StringBuffer(100);
        cityName.append(cityWebsite.getName().toUpperCase());
        paramMap.put("cityName", cityName.toString());
        paramMap.put("voucherName", billType.toUpperCase().concat(" JOURNAL VOUCHER"));
        paramMap.put("budgetAppropriationDetailJasper",
                reportHelper.getClass().getResourceAsStream("/reports/templates/budgetAppropriationDetail.jasper"));
        if (billRegistermis != null && billRegistermis.getBudgetaryAppnumber() != null
                && !"".equalsIgnoreCase(billRegistermis.getBudgetaryAppnumber()))
            paramMap.put("budgetDetail",
                    budgetAppropriationService.getBudgetDetailsForBill(billRegistermis.getEgBillregister()));
        else if (voucher != null && voucher.getVouchermis().getBudgetaryAppnumber() != null
                && !"".equalsIgnoreCase(voucher.getVouchermis().getBudgetaryAppnumber()))
            paramMap.put("budgetDetail", budgetAppropriationService.getBudgetDetailsForVoucher(voucher));
        else
            paramMap.put("budgetDetail", new ArrayList<Object>());
        return paramMap;
    }

    public Map<String, Object> getAccountDetails(final Integer detailtypeid, final Integer detailkeyid,
            final Map<String, Object> tempMap) throws ApplicationException {
        final Accountdetailtype detailtype = (Accountdetailtype) getPersistenceService().find(ACCDETAILTYPEQUERY,
                detailtypeid);
        tempMap.put("detailtype", detailtype.getName());
        tempMap.put("detailtypeid", detailtype.getId());
        tempMap.put("detailkeyid", detailkeyid);

        egovCommon.setPersistenceService(persistenceService);
        final EntityType entityType = egovCommon.getEntityType(detailtype, detailkeyid);
        tempMap.put(Constants.DETAILKEY, entityType.getName());
        tempMap.put(Constants.DETAILCODE, entityType.getCode());
        return tempMap;
    }

    private String getVoucherNumber() {
        return voucher == null || voucher.getVoucherNumber() == null ? "" : voucher.getVoucherNumber();
    }

    private String getVoucherDescription() {
        return voucher == null || voucher.getDescription() == null ? "" : voucher.getDescription();
    }

    private String getVoucherDate() {
        return voucher == null || voucher.getVoucherDate() == null ? "" : DateUtils.getDefaultFormattedDate(voucher
                .getVoucherDate());
    }

    private void loadHistory(State state) {
        loadInboxHistoryData(state);
    }

    /*
     * private Position getStateUser(State state) { if (state.getPrevious() !=
     * null) return state.getPrevious().getOwner(); else return null;//
     * inboxService.getPrimaryPositionForUser(state.getCreatedBy().getId(),
     * state.getCreatedDate()); }
     */

    private void loadInboxHistoryData(final State states) throws ApplicationRuntimeException {
        if (states != null) {
            final List<StateHistory> stateHistory = states.getHistory();
            Collections.reverse(stateHistory);
            for (final StateHistory state : stateHistory) {

                // WorkflowTypes workflowTypes =
                // inboxService.getWorkflowType(state.getType());
                final String pos = state.getSenderName().concat(" / ").concat(state.getSenderName());
                final String nextAction = state.getNextAction();
                if (!"NEW".equalsIgnoreCase(state.getValue())) {
                    final WorkFlowHistoryItem inboxHistoryItem = new WorkFlowHistoryItem(getFormattedDate(
                            state.getCreatedDate(), "dd/MM/yyyy hh:mm a"), pos, nextAction, state.getValue(),
                            state.getComments() != null ? removeSpecialCharacters(state.getComments()) : "");
                    inboxHistory.add(inboxHistoryItem);
                }
            }
        }
    }

    private String removeSpecialCharacters(final String str) {
        return str.replaceAll("\\s\\s+|\\r\\n", "<br/>").replaceAll("\'", "\\\\'");
    }

    private String getNextAction(final StateHistory state) {
        if (state.getNextAction() == null)
            return "";
        else {
            final org.egov.infstr.workflow.Action action = (org.egov.infstr.workflow.Action) persistenceService
                    .findByNamedQuery(org.egov.infstr.workflow.Action.BY_NAME_AND_TYPE, state.getNextAction(), null);
            if (action != null)
                return " - " + (action.getDescription() != null ? action.getDescription() : state.getNextAction());
            else
                return " - " + state.getNextAction();
        }
    }

    public void setBudgetAppropriationService(final BudgetAppropriationService budgetAppropriationService) {
        this.budgetAppropriationService = budgetAppropriationService;
    }
}
