package cd.presenceless.organisationservice.service;

import cd.presenceless.organisationservice.client.IdentityClient;
import cd.presenceless.organisationservice.request.IdentityReq;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class IdentityService {

    private final IdentityClient identityWebClient;

    public IdentityService(IdentityClient identityWebClient) {
        this.identityWebClient = identityWebClient;
    }

    public Object identify(IdentityReq identityReq, Map<String, Boolean> params) {
        return identityWebClient.identify(
                identityReq,
                params.get("address"),
                params.get("photograph"),
                params.get("mobileNumber"),
                params.get("email")
        ).block();
    }
}
