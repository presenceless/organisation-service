package cd.presenceless.organisationservice.service;

import cd.presenceless.organisationservice.entity.Address;
import cd.presenceless.organisationservice.entity.Document;
import cd.presenceless.organisationservice.entity.Organisation;
import cd.presenceless.organisationservice.request.AddrRequest;
import cd.presenceless.organisationservice.request.OrgRequest;
import cd.presenceless.organisationservice.response.OrgResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface OrganisationService {
    Document saveDocument(Organisation organisation, MultipartFile file)
            throws Exception;
    OrgResponse registerOrganisation(OrgRequest organisation) throws Exception;
    Address saveAddress(Organisation organisation, AddrRequest address);
    void approve(Long organisation);
    void deny(Long organisation);
    Iterable<OrgResponse> getAllOrgs(Map<String, Boolean> params);
    Object uploadDocuments(Long orgId, MultipartFile[] docs);
}
