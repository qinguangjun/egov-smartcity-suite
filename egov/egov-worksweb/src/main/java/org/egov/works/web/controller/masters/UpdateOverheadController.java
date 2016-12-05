/*
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
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.works.web.controller.masters;

import org.egov.commons.dao.ChartOfAccountsHibernateDAO;
import org.egov.infra.exception.ApplicationException;
import org.egov.works.masters.entity.Overhead;
import org.egov.works.masters.service.OverheadService;
import org.egov.works.services.WorksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/masters")
public class UpdateOverheadController {

    @Autowired
    private OverheadService overheadService;

    @Autowired
    private WorksService worksService;

    @Autowired
    private ChartOfAccountsHibernateDAO chartOfAccountsHibernateDAO;

    @RequestMapping(value = "/overhead-update/{overheadId}", method = RequestMethod.GET)
    public String updateOverhead(final Model model, @PathVariable final String overheadId)
            throws ApplicationException {
        final Overhead overhead = overheadService.getOverheadById(Long.parseLong(overheadId));
        overhead.setTempOverheadRateValues(overhead.getOverheadRates());
        setDropDownValues(model);
        model.addAttribute("overhead", overhead);
        model.addAttribute("mode", "edit");
        return "overhead-form";
    }

    private void setDropDownValues(final Model model) {
        if (worksService.getWorksConfigValue("OVERHEAD_PURPOSE") != null)
            model.addAttribute("accounts",
                    chartOfAccountsHibernateDAO
                            .getAccountCodeByPurpose(Integer.valueOf(worksService.getWorksConfigValue("OVERHEAD_PURPOSE"))));
    }

}
