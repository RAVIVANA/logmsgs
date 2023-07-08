package com.nkxgen.spring.jdbc.controller;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nkxgen.spring.jdbc.DaoInterfaces.AccountProcessingDAO;
import com.nkxgen.spring.jdbc.DaoInterfaces.AuditLogDAO;
import com.nkxgen.spring.jdbc.model.AuditLogs;
import com.nkxgen.spring.jdbc.model.LoanTransactions;
import com.nkxgen.spring.jdbc.model.Transaction;
import com.nkxgen.spring.jdbc.utilities.Generator;
import com.nkxgen.spring.jdbc.utilities.PdfGenerator;

@Controller
public class DownloadController {

	private AuditLogDAO auditLogDAO;
	private Generator generator;
	private AccountProcessingDAO interestCalDao;
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	public DownloadController(AuditLogDAO auditLogDAO, PdfGenerator generator, AccountProcessingDAO interestCalDao) {
	    this.auditLogDAO = auditLogDAO;
	    this.generator = generator;
	    this.interestCalDao = interestCalDao;
	    
	    // Log the instantiation of DownloadController with dependencies
	    LOGGER.info("DownloadController instantiated with AuditLogDAO, PdfGenerator, and AccountProcessingDAO.");
	}


	@RequestMapping(value = "auditDownloads")
	public void downloadAuditData(HttpServletResponse response) {
	    try (OutputStream outputStream = response.getOutputStream()) {
	        response.setContentType("application/pdf");
	        response.setHeader("Content-Disposition", "attachment; filename=audit_logs.pdf");

	        List<AuditLogs> auditLogsList = auditLogDAO.getAllAuditLogs();

	        generator.generateAuditLogsPdf(auditLogsList, outputStream);
	        
	        // Log the successful generation of audit logs PDF
	        LOGGER.info("Generated audit logs PDF and downloaded it.");
	    } catch (Exception e) {
	        // Log the exception if an error occurs
	        LOGGER.error("Error occurred while downloading audit logs PDF: {}", e.getMessage());
	        e.printStackTrace();
	    }
	}

	@RequestMapping(value = "/generateAccountTransactionsPDF", method = RequestMethod.POST)
	public void downloadAccountData(HttpServletResponse response, @RequestParam("accountId") String accountid) {
	    try (OutputStream outputStream = response.getOutputStream()) {
	        response.setContentType("application/pdf");
	        response.setHeader("Content-Disposition", "attachment; filename=transaction_list.pdf");
	        int accountId = Integer.parseInt(accountid);

	        LOGGER.info("Account ID: {}", accountId);

	        List<Transaction> transactionList = interestCalDao.AccountTransactionStatementGeneration(accountId);
	        generator.generateAccountTransactionPDF(transactionList, outputStream);
	        
	        // Log the successful generation of account transactions PDF
	        LOGGER.info("Generated account transactions PDF and downloaded it.");
	    } catch (Exception e) {
	        // Log the exception if an error occurs
	        LOGGER.error("Error occurred while downloading account transactions PDF: {}", e.getMessage());
	        e.printStackTrace();
	    }
	}


	@RequestMapping(value = "/generateLoanTransactionsPDF", method = RequestMethod.POST)
	public void downloadLoanData(HttpServletResponse response, @RequestParam("accountId") String accountid) {
	    try (OutputStream outputStream = response.getOutputStream()) {
	        response.setContentType("application/pdf");
	        response.setHeader("Content-Disposition", "attachment; filename=transaction_list.pdf");
	        int accountId = Integer.parseInt(accountid);

	        LOGGER.info("Account ID: {}", accountId);

	        List<LoanTransactions> transactionList = interestCalDao.LoanTransactionStatementGeneration(accountId);
	        generator.generateLoanTransactionPDF(transactionList, outputStream);
	        
	        // Log the successful generation of loan transactions PDF
	        LOGGER.info("Generated loan transactions PDF and downloaded it.");
	    } catch (Exception e) {
	        // Log the exception if an error occurs
	        LOGGER.error("Error occurred while downloading loan transactions PDF: {}", e.getMessage());
	        e.printStackTrace();
	    }
	}


}
