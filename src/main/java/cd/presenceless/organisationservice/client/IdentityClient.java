package cd.presenceless.organisationservice.client;

import cd.presenceless.organisationservice.request.IdentityReq;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@HttpExchange(value = "/api/v1/identity", contentType = MediaType.APPLICATION_JSON_VALUE)
public interface IdentityClient {

    @PostExchange("/")
    Mono<ResponseEntity<Object>> identify(
            @RequestBody IdentityReq identity,
            @RequestParam(value = "address", required = false) boolean address,
            @RequestParam(value = "photograph", required = false) boolean photograph,
            @RequestParam(value = "mobileNumber", required = false) boolean mobileNumber,
            @RequestParam(value = "email", required = false) boolean email
    );
}
