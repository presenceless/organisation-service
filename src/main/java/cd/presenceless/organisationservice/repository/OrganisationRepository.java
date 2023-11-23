package cd.presenceless.organisationservice.repository;

import cd.presenceless.organisationservice.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository
        extends JpaRepository<Organisation, Long> { }
