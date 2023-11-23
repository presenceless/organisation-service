package cd.presenceless.organisationservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrgResponse {
    private Long id;
    private String name;
    private String regNumber;
    private String email;
    private Date date;
    private boolean is_deleted;
    private boolean is_approved;
    private String address;
    private List<String> documents;
}
