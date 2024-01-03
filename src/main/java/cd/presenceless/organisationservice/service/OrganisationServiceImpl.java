package cd.presenceless.organisationservice.service;

import cd.presenceless.organisationservice.entity.Address;
import cd.presenceless.organisationservice.entity.Document;
import cd.presenceless.organisationservice.entity.Organisation;
import cd.presenceless.organisationservice.repository.AddressRepository;
import cd.presenceless.organisationservice.repository.DocumentRepository;
import cd.presenceless.organisationservice.repository.OrganisationRepository;
import cd.presenceless.organisationservice.request.AddrRequest;
import cd.presenceless.organisationservice.request.OrgRequest;
import cd.presenceless.organisationservice.response.AppNotifyResponse;
import cd.presenceless.organisationservice.response.OrgResponse;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.*;

@Service
public class OrganisationServiceImpl
        implements OrganisationService {

    @Value("${rabbitmq.organisation-service.exchange}")
    public String exchange;

    @Value("${rabbitmq.organisation-service.routing-key.application-notification}")
    public String applicationNotificationRoutingKey;
    @Value("${rabbitmq.organisation-service.routing-key.approval-notification}")
    public String approvalNotificationRoutingKey;
    @Value("${rabbitmq.organisation-service.routing-key.rejection-notification}")
    public String rejectionNotificationRoutingKey;
    @Value("${rabbitmq.organisation-service.routing-key.api-keys-notification}")
    public String apiKeysNotificationRoutingKey;

    private final OrganisationRepository organisationRepository;
    private final DocumentRepository documentRepository;
    private final AddressRepository addressRepository;
    private final AmqpTemplate template;
    private final FileService fileService;
    private final JWTService jwtService;

    public OrganisationServiceImpl(
            OrganisationRepository organisationRepository,
            DocumentRepository documentRepository,
            AddressRepository addressRepository,
            AmqpTemplate template, FileService fileService, JWTService jwtService)
    {
        this.organisationRepository = organisationRepository;
        this.documentRepository = documentRepository;
        this.addressRepository = addressRepository;
        this.template = template;
        this.fileService = fileService;
        this.jwtService = jwtService;
    }

    @Override
    public Document saveDocument(Organisation org, MultipartFile file) throws Exception {
        // https://www.baeldung.com/jpa-joincolumn-vs-mappedby
        String fileName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if(fileName.contains("..")) {
                throw  new Exception("Filename contains invalid path sequence " + fileName);
            }

            if (file.getBytes().length > (1024 * 1024 * 50)) {
                throw new Exception("File size exceeds maximum limit");
            }

            final var fileInfo = fileService.uploadFile(file, org.getId());

            Document document = Document.builder()
                    .fileName(fileInfo.get("fileName"))
                    .fileType(file.getContentType())
                    .organisation(org)
                    .url(fileInfo.get("mediaLink"))
                    .build();

            return documentRepository.save(document);
        } catch (MaxUploadSizeExceededException e) {
            throw new MaxUploadSizeExceededException(file.getSize());
        } catch (Exception e) {
            throw new Exception("Could not save File: " + e.getMessage());
        }
    }

    @Override
    public OrgResponse registerOrganisation(OrgRequest organisation) {
        Organisation org = Organisation
                .builder()
                .name(organisation.getName())
                .regNumber(organisation.getRegNumber())
                .date(new Date(System.currentTimeMillis()))
                .email(organisation.getEmail())
                .is_deleted(false)
                .is_approved(false)
                .build();

        final var savedOrg = organisationRepository.save(org);

        final var addr = saveAddress(savedOrg, organisation.getAddress());

        // Send email notification to the organisation
        template.convertAndSend(
                exchange,
                applicationNotificationRoutingKey,
                AppNotifyResponse.builder()
                        .organisationName(savedOrg.getName())
                        .organisationEmail(savedOrg.getEmail())
                        .organisationRegNumber(savedOrg.getRegNumber())
                        .build()
        );

        return OrgResponse
                .builder()
                .id(savedOrg.getId())
                .name(savedOrg.getName())
                .regNumber(savedOrg.getRegNumber())
                .date(savedOrg.getDate())
                .email(savedOrg.getEmail())
                .address(addr.toString())
                .build();
    }

    @Override
    public List<String> uploadDocuments(Long orgId, MultipartFile[] files) {
        final var org = organisationRepository.findById(orgId)
                .orElseThrow();

        return Arrays.stream(files).map(file -> {
            try {
                return saveDocument(org, file).getUrl();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    @Override
    public Address saveAddress(Organisation organisation, AddrRequest address) {
        final var addr = Address.builder()
                .organisation(organisation)
                .province(address.getProvince())
                .ville(address.getVille())
                .commune(address.getCommune())
                .quartier(address.getQuartier())
                .avenue(address.getAvenue())
                .no(address.getNo())
                .build();

        return addressRepository.save(addr);
    }

    @Override
    public boolean approve(Long orgId) throws NoSuchElementException {
        final var org = organisationRepository.findById(orgId)
                .orElseThrow();

        org.set_approved(true);
        final var saved = organisationRepository.save(org);

        template.convertAndSend(
                exchange,
                approvalNotificationRoutingKey,
                AppNotifyResponse.builder()
                        .organisationName(org.getName())
                        .organisationEmail(org.getEmail())
                        .organisationRegNumber(org.getRegNumber())
                        .build()
        );

        // send api keys to the organisation
        template.convertAndSend(
                exchange,
                apiKeysNotificationRoutingKey,
                AppNotifyResponse.builder()
                        .organisationName(org.getName())
                        .organisationEmail(org.getEmail())
                        .organisationRegNumber(org.getRegNumber())
                        .extra(generateToken(org.getId()))
                        .build()
        );

        return saved.is_approved();
    }

    @Override
    public void deny(Long organisation) {
        final var org = organisationRepository.findById(organisation)
                .orElseThrow();
        organisationRepository.deleteById(org.getId());

        template.convertAndSend(
                exchange,
                rejectionNotificationRoutingKey,
                AppNotifyResponse.builder()
                        .organisationName(org.getName())
                        .organisationEmail(org.getEmail())
                        .organisationRegNumber(org.getRegNumber())
                        .build()
        );
    }

    @Override
    public Iterable<OrgResponse> getAllOrgs(Map<String, Boolean> params) {
        return organisationRepository.findAll().stream()
                .filter(org -> isApprovedFilter(params, org))
                .filter(org -> isDeletedFilter(params, org))
                .map(this::mapToOrgResponse)
                .toList();
    }

    private OrgResponse mapToOrgResponse(Organisation org) {
        OrgResponse orgResponse = OrgResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .regNumber(org.getRegNumber())
                .date(org.getDate())
                .email(org.getEmail())
                .build();

        if (org.getAddress() != null) {
            orgResponse.setAddress(org.getAddress().toString());
        }

        if (org.getDocuments() != null) {
            orgResponse.setDocuments(org.getDocuments().stream()
                    .map(Document::getUrl)
                    .toList());
        }

        return orgResponse;
    }

    /**
     * @param orgId - id of the organisation
     * @return a map containing the sandbox and production tokens
     */
    @Override
    public Map<String, Object> generateToken(Long orgId) {
        Map<String, Object> sandboxClaims = new HashMap<>();
        sandboxClaims.put("sandbox", true);
        sandboxClaims.put("rate limit", 100);

        Map<String, Object> productionClaims = new HashMap<>();
        productionClaims.put("production", true);

        return Map.of(
                "sandbox", jwtService.createToken(orgId, sandboxClaims),
                "production", jwtService.createToken(orgId, productionClaims)
        );
    }

    private boolean isApprovedFilter(Map<String, Boolean> params, Organisation org) {
        if (params.containsKey("approved")) {
            return org.is_approved() == params.get("approved");
        }
        return true;
    }

    private boolean isDeletedFilter(Map<String, Boolean> params, Organisation org) {
        if (params.containsKey("deleted")) {
            return org.is_deleted() == params.get("deleted");
        }
        return true;
    }
}
