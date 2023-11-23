package cd.presenceless.organisationservice.repository;

import cd.presenceless.organisationservice.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository
        extends JpaRepository<Address, Long> { }
