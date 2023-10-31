package cd.presenceless.organisationservice.controller;

import cd.presenceless.organisationservice.client.IdentityClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/org/")
public class OrgController {

    private final IdentityClient identityClient;

    public OrgController(IdentityClient identityClient) {
        this.identityClient = identityClient;
    }

    @GetMapping("")
    public String hi() {
        return identityClient.hello();
    }

    @PostMapping
    public Object register(@RequestBody Object org) {
        return org;
    }
}
