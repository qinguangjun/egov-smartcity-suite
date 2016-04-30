/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

        1) All versions of this program, verbatim or modified must carry this
           Legal Notice.

        2) Any misrepresentation of the origin of the material is prohibited. It
           is required that all modified versions of this material be marked in
           reasonable ways as different from the original version.

        3) This license does not grant any rights to any user of the program
           with regards to rights under trademark law for use of the trade names
           or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.works.lineestimate.service;

import org.egov.commons.CFinancialYear;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.persistence.utils.DBSequenceGenerator;
import org.egov.infra.persistence.utils.SequenceNumberGenerator;
import org.egov.works.lineestimate.entity.LineEstimateDetails;
import org.egov.works.lineestimate.entity.enums.WorkCategory;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

@Service
public class WorkOrderIdentificationNumberGenerator {

    private static final String PROJECTCODE_SEQ_PREFIX = "SEQ_PROJECTCODE";

    @Autowired
    private SequenceNumberGenerator sequenceNumberGenerator;

    @Autowired
    private DBSequenceGenerator dbSequenceGenerator;

    @Autowired
    private FinancialYearHibernateDAO financialYearHibernateDAO;

    @Transactional
    public String generateWorkOrderIdentificationNumber(final LineEstimateDetails lineEstimateDetails) {
        try {
            final CFinancialYear financialYear = financialYearHibernateDAO
                    .getFinancialYearByDate(lineEstimateDetails.getLineEstimate().getLineEstimateDate());
            final String finYearRange[] = financialYear.getFinYearRange().split("-");
            final String sequenceName = PROJECTCODE_SEQ_PREFIX + "_" + finYearRange[0] + "_" + finYearRange[1];
            final String typeOfWork;
            if (lineEstimateDetails.getLineEstimate().getWorkCategory().toString().equals(WorkCategory.SLUM_WORK.toString()))
                typeOfWork = "SL";
            else
                typeOfWork = "NS";
            Serializable sequenceNumber;
            try {
                sequenceNumber = sequenceNumberGenerator.getNextSequence(sequenceName);
            } catch (final SQLGrammarException e) {
                sequenceNumber = dbSequenceGenerator.createAndGetNextSequence(sequenceName);
            }
            return String.format("%s/%s/%05d/%02d/%s", lineEstimateDetails.getLineEstimate().getExecutingDepartment().getCode(),
                    typeOfWork, sequenceNumber,
                    getMonthOfTransaction(lineEstimateDetails.getLineEstimate().getLineEstimateDate()),
                    financialYear.getFinYearRange());
        } catch (final SQLException e) {
            throw new ApplicationRuntimeException("Error occurred while generating WIN", e);
        }
    }

    private int getMonthOfTransaction(final Date lineEstimateDate) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(lineEstimateDate);
        return cal.get(Calendar.MONTH) + 1;
    }

}
