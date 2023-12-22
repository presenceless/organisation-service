package cd.presenceless.organisationservice.request;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrgRequest {
    private AddrRequest address;
    private String name;
    private String regNumber;
    private String email;
    private boolean is_deleted,
            is_approved;
}
