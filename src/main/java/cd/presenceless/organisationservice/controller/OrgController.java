package cd.presenceless.organisationservice.controller;

import cd.presenceless.organisationservice.client.IdentityClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orgs")
public class OrgController {

    private final IdentityClient identityClient;

    public OrgController(IdentityClient identityClient) {
        this.identityClient = identityClient;
    }

    @GetMapping("/hi")
    public String hi() {
        return identityClient.hello();
    }

    @PostMapping("/apply")
    public Object register(@RequestBody Object org) {
        return org;
    }

    @PostMapping("/api-keys")
    public boolean requestAPIKeys() {
       // generate API keys
       // Sandbox and Production
        return true;
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
}
