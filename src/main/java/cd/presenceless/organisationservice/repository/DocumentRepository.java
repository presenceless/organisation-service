package cd.presenceless.organisationservice.repository;

import cd.presenceless.organisationservice.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository
        extends JpaRepository<Document, Long> { }
