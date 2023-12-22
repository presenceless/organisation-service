package cd.presenceless.organisationservice.response;


import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AppNotifyResponse {
    private String organisationName;
    private String organisationEmail;
    private String organisationRegNumber;
    private String message;
}
