package cd.presenceless.organisationservice.controller;

import cd.presenceless.organisationservice.client.IdentityClient;
import cd.presenceless.organisationservice.entity.Organisation;
import cd.presenceless.organisationservice.response.OrgResponse;
import cd.presenceless.organisationservice.service.OrganisationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// https://medium.com/@patelsajal2/how-to-create-a-spring-boot-rest-api-for-multipart-file-uploads-a-comprehensive-guide-b4d95ce3022b
@RestController
@RequestMapping("/api/v1/orgs")
public class OrgController {

    // When an organization wants to use our services,
    // they will need to register with us.
    // A registration request will be sent and stored in the database.
    // The organization will be notified of the request.
    // The request  will be reviewed by government officials.
    // If the request is approved, the organization will be notified including the API keys.
    // If the request is rejected, the organization will be notified.

    // When the organization is approved, an entry will be created in notification queue.
    private final IdentityClient identityClient;
    private final OrganisationService organisationService;

    public OrgController(IdentityClient identityClient, OrganisationService organisationService) {
        this.identityClient = identityClient;
        this.organisationService = organisationService;
    }

    @GetMapping("/hi")
    public String hi() {
        return identityClient.hello();
    }

    @PostMapping("/apply")
    public ResponseEntity<OrgResponse>
        register(@RequestBody Organisation org, @RequestParam("files") MultipartFile[] docs) {
        try {
            final var docs_ = organisationService.save(org, docs);

            return ResponseEntity.ok(
                    new OrgResponse(
                            org.getId(),
                            org.getName(),
                            org.getRegNumber(),
                            org.getDate(),
                            org.is_deleted(),
                            org.is_approved(),
                            org.getAddress().toString(),
                            docs_
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api-keys/{orgId}")
    public String apiKeys(@PathVariable String orgId) {
        // get API keys
        // Sandbox and Production
        return orgId;
    }

    @PostMapping("/citizen/identity")
    public boolean requestIdentity() {
        // check a user
        // returns a YES or No
        return true;
    }

    // approve an organization
    @GetMapping("/approve/{orgId}")
    public boolean approve(@PathVariable String orgId) {
        // email the organization
        return true;
    }

    // reject an organization
    @GetMapping("/reject/{orgId}")
    public boolean reject(@PathVariable String orgId) {
        // email the organization
        return true;
    }
}
