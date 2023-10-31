package cd.presenceless.organisationservice.client;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface IdentityClient {

    @GetExchange("")
    public String hello();
}
