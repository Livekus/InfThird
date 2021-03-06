/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wittakarn.inflow.view;

import com.wittakarn.inflow.entity.BASECompany;
import com.wittakarn.inflow.entity.BASECustomer;
import com.wittakarn.inflow.entity.BASEFileAttachment;
import com.wittakarn.inflow.entity.SOSalesOrder;
import com.wittakarn.inflow.entity.SOSalesOrderLine;
import com.wittakarn.inflow.interfaces.InvoiceServiceable;
import com.wittakarn.inflow.jasper.JasperContext;
import com.wittakarn.inflow.model.CustomerForm;
import com.wittakarn.inflow.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperPrint;

/**
 *
 * @author Wittakarn
 */
@ManagedBean(name = "invoiceBean")
@ViewScoped
public class InvoiceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(InvoiceBean.class.getName());
    private List<String> selectedMovies;
    private Map<String,String> movies;  
    private String customerName;
    private Date orderDate;
    private List<CustomerForm> customerFormList;

    @EJB
    private InvoiceServiceable invoiceServiceable;

    public InvoiceBean() {
        this.selectedMovies = new ArrayList<String>();
        this.movies = new HashMap<String, String>();  
        this.movies.put("พี่ปืน", "INVAT_P'PUUN");  
        this.movies.put("พ่อ", "INVAT_NORMAL_PRICE");
        
    }

    @PostConstruct
    public void init() {
        logger.info("Begin init...");
    }

    public void searchCustomerOrder(ActionEvent event) {
        List<CustomerForm> cusFormList;
        try {
            logger.info("Begin searchCustomerOrder...");
           System.out.println(getSelectedMovies());
           if (getSelectedMovies().isEmpty() || getSelectedMovies().size() > 1){
            cusFormList = invoiceServiceable.getCustomerOrder(getCustomerName(), getOrderDate(), null);
           }else{
            cusFormList = invoiceServiceable.getCustomerOrder(getCustomerName(), getOrderDate(), getSelectedMovies());
           }
           //logger.info("cusFormList.size() : " + ((CustomerForm)cusFormList.get(0)).getSoSalesOrder().getOrderDate());
            logger.info("cusFormList.size() : " + cusFormList.size());
            customerFormList = cusFormList;
            String stangt = Trans.spell(Double.parseDouble("90013"));
        logger.info("test" + stangt);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "", ex);
        } finally {
            cusFormList = null;
            logger.info("End searchCustomerOrder...");
        }
    }

    public void manipolateData(ActionEvent event) {
        BASECustomer baseCustomer;
        SOSalesOrder soSalesOrder;
        CustomerForm customerFormSelected;
        JasperPrint print;
        try {
            customerFormSelected = (CustomerForm) event.getComponent().getAttributes().get("customerSelected");
            logger.info("Begin manipolateData...");
            baseCustomer = customerFormSelected.getBaseCustomer();
            soSalesOrder = customerFormSelected.getSoSalesOrder();

            HashMap<String, Object> parameters = new HashMap<>();
            List<SOSalesOrderLine> orderLine = createList(parameters, soSalesOrder);
            createParameter(parameters, baseCustomer, soSalesOrder);
            if(baseCustomer.getName().equalsIgnoreCase("บริษัท โนรา กรุ๊ป แมนเนจเม้นท์ จำกัด")){
                      print = JasperContext.initJasper("C:\\ireportFiles\\Nora.jasper", parameters, orderLine);
            }else{
                print = JasperContext.initJasper("C:\\ireportFiles\\reportInvoice.jasper", parameters, orderLine);
            }
            JasperContext.printPDF(print, getServletOutputStream());
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "", ex);
        } finally {
            baseCustomer = null;
            soSalesOrder = null;
            customerFormSelected = null;
            logger.info("End manipolateData...");
        }
    }

    private List<SOSalesOrderLine> createList(HashMap<String, Object> parameters, SOSalesOrder soSalesOrder) throws Exception {
        BigDecimal pp1 = BigDecimal.ZERO, pp2 = BigDecimal.ZERO;

        List<SOSalesOrderLine> orderLine = invoiceServiceable.getOrderList(soSalesOrder.getSalesOrderId());
        for (SOSalesOrderLine sOSalesOrderLine : orderLine) {
            if (sOSalesOrderLine.getItemTaxCodeId().getItemTaxCodeId().equals(100)) {
                pp2 = pp2.add(sOSalesOrderLine.getSubTotal());
            } else {
                pp1 = pp1.add(sOSalesOrderLine.getSubTotal());
            }

            sOSalesOrderLine.refreshData();
        }
        
        String stang = Trans.spell(Double.parseDouble(pp1.add(pp2).toString()));
        parameters.put("TotalString", stang);
        parameters.put("pp1", pp1);
        parameters.put("pp2", pp2);
        
        return orderLine;
    }

    private void createParameter(HashMap<String, Object> parameters, BASECustomer baseCustomer, SOSalesOrder soSalesOrder) throws Exception {
        BASECompany company = invoiceServiceable.getCompany(1);
        BASEFileAttachment fileAttachment = company.getPictureFileAttachmentId();
        String[] companyAddressContent = {company.getAddress2(), company.getCity(), company.getState(), company.getPostalCode()};
        String[] customerAddressContent = {baseCustomer.getAddress2(), baseCustomer.getCity(), baseCustomer.getState(),baseCustomer.getPostalCode()};
        String[] customerAddressContentAddition = {baseCustomer.getCountry(), baseCustomer.getPostalCode()};
       
        parameters.put("Picture", new ByteArrayInputStream(fileAttachment.getData()));
        parameters.put("Name", company.getName());
        parameters.put("NameCus", baseCustomer.getName());
        parameters.put("Address1", company.getAddress1());
        parameters.put("Address2", StringUtils.concatString(companyAddressContent));
        parameters.put("Phone", company.getPhone());
        parameters.put("Fax", company.getFax());
        parameters.put("TaxNumber", company.getTaxNumber());
        parameters.put("BillingAddress1", baseCustomer.getAddress1());
        parameters.put("BillingAddress2", StringUtils.concatString(customerAddressContent));
        parameters.put("CusTel", soSalesOrder.getCustomerId().getPhone());
        parameters.put("CusFax", soSalesOrder.getCustomerId().getFax());
        parameters.put("BillingAddressRemarks", baseCustomer.getBillingAddressRemarks());
        parameters.put("OrderNumber", soSalesOrder.getOrderNumber());
        parameters.put("OrderDate", soSalesOrder.getOrderDate());
        parameters.put("PaymentName", soSalesOrder.getPaymentTermsId().getName());
        if (!soSalesOrder.getOrderRemarks().equalsIgnoreCase("")){
        parameters.put("OrderRemarks", soSalesOrder.getOrderRemarks());
         parameters.put("RemarkText", "หมายเหตุ");
        }
        logger.info("orderremark " +soSalesOrder.getOrderRemarks() );
        parameters.put("OrderTax1", soSalesOrder.getOrderTax1());
        parameters.put("OrderTotal", soSalesOrder.getOrderTotal());
    }

    private ServletOutputStream getServletOutputStream() throws Exception {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) faceContext.getExternalContext().getResponse();
        //response.setHeader("Content-disposition", "attachment; filename=invoice.pdf");

        return response.getOutputStream();
    }

    /**
     * @return the customerFormList
     */
    public List<CustomerForm> getCustomerFormList() {
        return customerFormList;
    }

    /**
     * @param customerFormList the customerFormList to set
     */
    public void setCustomerFormList(List<CustomerForm> customerFormList) {
        this.customerFormList = customerFormList;
    }

    /**
     * @return the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return the orderDate
     */
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * @param orderDate the orderDate to set
     */
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public List<String> getSelectedMovies() {
        return selectedMovies;
    }

    public void setSelectedMovies(List<String> selectedMovies) {
        this.selectedMovies = selectedMovies;
    }

    public Map<String, String> getMovies() {
        return movies;
    }

    public void setMovies(Map<String, String> movies) {
        this.movies = movies;
    }
    
     
}
