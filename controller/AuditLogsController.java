package com.nkxgen.spring.jdbc.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nkxgen.spring.jdbc.DaoInterfaces.AuditLogDAO;
import com.nkxgen.spring.jdbc.DaoInterfaces.AuditLogRepository;
import com.nkxgen.spring.jdbc.model.AuditLogs;

@Controller
public class AuditLogsController {

	private AuditLogDAO auditLogDAO;
	private AuditLogRepository auditLogRepository;
	
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


	@Autowired
	public AuditLogsController(AuditLogDAO auditLogDAO, AuditLogRepository auditLogRepository) {
	    this.auditLogDAO = auditLogDAO;
	    this.auditLogRepository = auditLogRepository;
	    
	    // Log the instantiation of AuditLogsController with dependencies
	    LOGGER.info("AuditLogsController instantiated with AuditLogDAO and AuditLogRepository.");
	}

	@RequestMapping(value = "logs")
	public String listAuditLogs(Model model) {
	    // Retrieve all audit logs from the auditLogDAO
	    List<AuditLogs> auditLogs = auditLogDAO.getAllAuditLogs();
	    
	    // Log the retrieval of audit logs
	    LOGGER.info("Retrieved {} audit logs from AuditLogDAO.", auditLogs.size());

	    // Add the audit logs as an attribute to the model
	    model.addAttribute("auditLogs", auditLogs);
	    
	    // Return the view name "auditlogs" to render the audit logs
	    return "audit-logs";
	}


	@GetMapping("/auditLogs")
	public String getAuditLogs(@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int pageSize, Model model) {
	    // Create a PageRequest object with the specified page, pageSize, and sorting order
	    org.springframework.data.domain.PageRequest pageable = org.springframework.data.domain.PageRequest.of(page,
	            pageSize, Sort.by("timestamp").descending());
	    
	    // Log the creation of PageRequest object
	    LOGGER.info("Created PageRequest with page={}, pageSize={}, and sorting by timestamp in descending order.", page, pageSize);

	    // Retrieve a page of audit logs from the auditLogRepository, ordered by timestamp in descending order
	    Page<AuditLogs> auditLogPage = auditLogRepository.findAllByOrderByTimestampDesc(pageable);
	    
	    // Log the retrieval of audit logs page
	    LOGGER.info("Retrieved audit logs page {} with pageSize={} from AuditLogRepository.", page, pageSize);

	    // Add the audit logs, current page, and total number of pages as attributes to the model
	    model.addAttribute("auditLogs", auditLogPage.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", auditLogPage.getTotalPages());
	    
	    int totalPages = auditLogPage.getTotalPages();
	    int maxPageNumbers = 5; // Maximum number of page numbers to display

	    int startPage;
	    int endPage;

	    if (totalPages <= maxPageNumbers) {
	        startPage = 1; // Adjusted from 0 to 1
	        endPage = totalPages;
	    } else {
	        if (page <= maxPageNumbers / 2) {
	            startPage = 0;
	            endPage = maxPageNumbers - 1;
	        } else if (page >= totalPages - maxPageNumbers / 2) {
	            startPage = totalPages - maxPageNumbers;
	            endPage = totalPages - 1;
	        } else {
	            startPage = page - maxPageNumbers / 2;
	            endPage = page + maxPageNumbers / 2;
	        }
	    }

	    // Generate a list of page numbers to display in the pagination UI
	    List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());
	    
	    // Log the generation of page numbers for pagination
	    LOGGER.info("Generated page numbers for pagination: {}.", pageNumbers);

	    // Add the list of page numbers as an attribute to the model
	    model.addAttribute("pageNumbers", pageNumbers);
	    
	    // Return the view name "auditlogs" to render the audit logs
	    return "audit-logs";
	}

}
