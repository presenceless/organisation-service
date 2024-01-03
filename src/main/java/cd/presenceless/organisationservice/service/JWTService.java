package cd.presenceless.organisationservice.service;

import java.util.Map;

public interface JWTService {
    boolean validateToken(final String token);

    String createToken(Long orgId, Map<String, Object> claims);
}
