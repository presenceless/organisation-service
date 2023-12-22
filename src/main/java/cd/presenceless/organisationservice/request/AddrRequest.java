package cd.presenceless.organisationservice.request;


import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddrRequest {
    private String province, ville,
            commune, quartier, avenue, no;
}
