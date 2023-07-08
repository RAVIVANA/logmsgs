package com.nkxgen.spring.jdbc.Dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nkxgen.spring.jdbc.DaoInterfaces.AuditLogDAO;
import com.nkxgen.spring.jdbc.controller.LoginController;
import com.nkxgen.spring.jdbc.model.AuditLogs;

@Repository
public class AuditDAOImpl implements AuditLogDAO {
	@PersistenceContext
	private EntityManager entityManager;
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


	@Transactional
	   public void saveAudit(AuditLogs audits) {
        LOGGER.info("Saving audit: {}", audits);

        // Persist the audit log object in the database
        entityManager.persist(audits);
    }

    @Transactional
    public List<AuditLogs> getAllAuditLogs() {
        LOGGER.info("Retrieving all audit logs");

        // Create a typed query to retrieve all audit logs from the database, ordered by ID in descending order
        TypedQuery<AuditLogs> query = entityManager.createQuery("SELECT a FROM AuditLogs a ORDER BY a.id DESC",
                AuditLogs.class);

        // Execute the query and return the list of audit logs
        List<AuditLogs> auditLogsList = query.getResultList();

        LOGGER.info("Retrieved {} audit logs", auditLogsList.size());
        return auditLogsList;
    }
}
