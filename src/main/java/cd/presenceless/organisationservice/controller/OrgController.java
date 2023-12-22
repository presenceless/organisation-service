package cd.presenceless.organisationservice.controller;

import cd.presenceless.organisationservice.client.IdentityClient;
import cd.presenceless.organisationservice.request.OrgRequest;
import cd.presenceless.organisationservice.response.OrgResponse;
import cd.presenceless.organisationservice.service.OrganisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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

    @GetMapping("/")
    public ResponseEntity<Iterable<OrgResponse>> all(
            @RequestParam(value = "approved", required = false) boolean approved,
            @RequestParam(value = "deleted", required = false) boolean deleted
    ) {
        Map<String, Boolean> params = Map.of("approved", approved, "deleted", deleted);
        return ResponseEntity.ok(organisationService.getAllOrgs(params));
    }

    @GetMapping("/hi")
    public String hi() {
        return identityClient.hello();
    }

    @PostMapping(value = "/")
    public ResponseEntity<OrgResponse> register(@RequestBody OrgRequest org) {
        try {
            final var org_ = organisationService.registerOrganisation(org);

            return ResponseEntity.status(HttpStatus.CREATED).body(org_);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{orgId}/documents")
    public ResponseEntity<Object>
        uploadDocuments(@PathVariable Long orgId, @RequestParam MultipartFile[] docs) {
        try {
            final var org = organisationService.uploadDocuments(orgId, docs);

            return ResponseEntity.status(HttpStatus.CREATED).body(org);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{orgId}/api-keys")
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

    @GetMapping("/{orgId}/approve")
    public ResponseEntity<Boolean> approve(@PathVariable Long orgId) {
        // email the organization
        // generate API keys
        organisationService.approve(orgId);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/{orgId}/reject")
    public ResponseEntity<Boolean> reject(@PathVariable Long orgId) {
        // email the organization
        organisationService.deny(orgId);
        return ResponseEntity.ok(true);
    }
}
