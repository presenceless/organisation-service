package cd.presenceless.organisationservice.response;


import lombok.*;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AppNotifyResponse {
    private String organisationName;
    private String organisationEmail;
    private String organisationRegNumber;
    private Map<String, Object> extra;
}
