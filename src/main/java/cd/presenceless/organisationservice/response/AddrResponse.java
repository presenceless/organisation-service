package cd.presenceless.organisationservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddrResponse {
    private Long id;
    private Long organisationId;
    private String province, ville, commune, quartier, avenue, no;
}
