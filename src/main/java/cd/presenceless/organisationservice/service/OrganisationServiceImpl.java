package cd.presenceless.organisationservice.service;

import cd.presenceless.organisationservice.entity.Address;
import cd.presenceless.organisationservice.entity.Document;
import cd.presenceless.organisationservice.entity.Organisation;
import cd.presenceless.organisationservice.repository.AddressRepository;
import cd.presenceless.organisationservice.repository.DocumentRepository;
import cd.presenceless.organisationservice.repository.OrganisationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class OrganisationServiceImpl
        implements OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final DocumentRepository documentRepository;
    private final AddressRepository addressRepository;

    public OrganisationServiceImpl(OrganisationRepository organisationRepository, DocumentRepository documentRepository, AddressRepository addressRepository) {
        this.organisationRepository = organisationRepository;
        this.documentRepository = documentRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public Document saveDocument(Organisation organisation, MultipartFile file) throws Exception {
        // https://www.baeldung.com/jpa-joincolumn-vs-mappedby
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if(fileName.contains("..")) {
                throw  new Exception("Filename contains invalid path sequence " + fileName);
            }

            if (file.getBytes().length > (1024 * 1024)) {
                throw new Exception("File size exceeds maximum limit");
            }

            Document document = Document.builder()
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .organisation(organisation)
                    .data(file.getBytes())
                    .build();

            return documentRepository.save(document);
        } catch (MaxUploadSizeExceededException e) {
            throw new MaxUploadSizeExceededException(file.getSize());
        } catch (Exception e) {
            throw new Exception("Could not save File: " + fileName);
        }
    }

    @Override
    public List<String> save(Organisation organisation, MultipartFile[] files) {
        Organisation org = Organisation.builder()
                .name(organisation.getName())
                .regNumber(organisation.getRegNumber())
                .date(organisation.getDate())
                .email(organisation.getEmail())
                .is_deleted(organisation.is_deleted())
                .is_approved(organisation.is_approved())
                .build();
        Organisation saved = organisationRepository.save(org);

        saveAddress(saved, organisation.getAddress());

        return Arrays.stream(files).map(file -> {
            try {
                Document attachment = saveDocument(organisation, file);
                return ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/download/")
                        .path(String.valueOf(attachment.getId()))
                        .toUriString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    @Override
    public void saveAddress(Organisation organisation, Address address) {
        Address.builder()
                .organisation(organisation)
                .province(address.getProvince())
                .ville(address.getVille())
                .commune(address.getCommune())
                .quartier(address.getQuartier())
                .avenue(address.getAvenue())
                .no(address.getNo())
                .build();

        addressRepository.save(address);
    }

    @Override
    public List<Organisation> getAllFiles() {
        return organisationRepository.findAll();
    }
}
