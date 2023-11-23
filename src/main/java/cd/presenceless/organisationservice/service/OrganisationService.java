package cd.presenceless.organisationservice.service;

import cd.presenceless.organisationservice.entity.Address;
import cd.presenceless.organisationservice.entity.Document;
import cd.presenceless.organisationservice.entity.Organisation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrganisationService {
    Document saveDocument(Organisation organisation, MultipartFile file)
            throws Exception;
    List<String> save(Organisation organisation, MultipartFile[] files) throws Exception;
    void saveAddress(Organisation organisation, Address address);
    List<Organisation> getAllFiles();
}
