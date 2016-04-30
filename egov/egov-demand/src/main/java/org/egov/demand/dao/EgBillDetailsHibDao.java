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
package org.egov.demand.dao;

import org.egov.demand.model.EgBill;
import org.egov.demand.model.EgBillDetails;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository(value = "egBillDetailsDAO")
@Transactional(readOnly = true)
public class EgBillDetailsHibDao implements EgBillDetailsDao {

    @PersistenceContext
    private EntityManager entityManager;

    private Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    @Override
    public List<EgBillDetails> getBillDetailsByBill(EgBill egBill) {
        List<EgBillDetails> billDetList = null;
        Query qry2 = getCurrentSession().createQuery(
                "from EgBillDetails billDet where billDet.egBill =:bill");

        if (egBill != null) {
            qry2.setEntity("bill", egBill);
            billDetList = qry2.list();
        }
        return billDetList;
    }

    @Override
    public EgBillDetails findById(Integer id, boolean lock) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EgBillDetails> findAll() {
        return getCurrentSession().createQuery("from EgBillDetails").list();
    }

    @Override
    public EgBillDetails create(EgBillDetails egBillDetails) {
        getCurrentSession().saveOrUpdate(egBillDetails);
        return egBillDetails;
    }

    @Override
    public void delete(EgBillDetails egBillDetails) {
        getCurrentSession().delete(egBillDetails);
    }

    @Override
    public EgBillDetails update(EgBillDetails egBillDetails) {
        getCurrentSession().saveOrUpdate(egBillDetails);
        return egBillDetails;
    }

}
