/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wittakarn.inflow.query;

import com.wittakarn.inflow.util.StringUtils;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Wittakarn
 */
public class BASECustomerQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(BASECustomerQuery.class.getName());

    public static List<Object[]> customerListQuery(EntityManager em, String name, Date date, List<String> stL) {
        logger.info("Begin SOSalesOrderQuery.customerListQuery...");
        StringBuffer sb = null;
        Query query = null;
        try {
            sb = new StringBuffer();
            sb.append("SELECT bc, ss ");
            sb.append("FROM BASECustomer bc, SOSalesOrder ss ");
            sb.append("WHERE bc.customerId = ss.customerId.customerId ");
            if (!StringUtils.isNullOrEmpty(name)) {
                sb.append("AND bc.name LIKE '%").append(name).append("%' ");
            }
            if (date != null) {
                sb.append("AND ss.orderDate BETWEEN  :date1 AND :date2 ");
            }
            if (stL != null ) {
                for(String a : stL)
                    if(stL.indexOf(a) == 0){    
                        sb.append("AND ss.pricingSchemeId.name = :pname".concat(((Integer)stL.indexOf(a)).toString()));
                    }else{
                         sb.append(" OR :pname".concat(((Integer)stL.indexOf(a)).toString()));
                    }
                                 
            }
              
            
            logger.info("SOSalesOrderQuery.customerListQuery :".concat(sb.toString()));
            query = em.createQuery(sb.toString());
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");

            if (date != null) {
                date.setYear((new Date()).getYear());
                Date date2 = (Date)date.clone();
                date2.setHours(23);
                date2.setMinutes(59);
                date2.setSeconds(59);
                query.setParameter("date1", date);
                query.setParameter("date2", date2);
                logger.info("date :".concat(date.toString()));
                logger.info("date :".concat(date2.toString()));
            }
            
            if (stL != null) {
                for(String a : stL){                  
                   query.setParameter("pname".concat(((Integer)stL.indexOf(a)).toString()), a);
                   logger.info("pname".concat(((Integer)stL.indexOf(a)).toString()).concat(":").concat(a));
                }            
              
            }

            return query.getResultList();
        } finally {
            sb = null;
            query = null;
            logger.info("End SOSalesOrderQuery.customerListQuery...");
        }
    }
}
