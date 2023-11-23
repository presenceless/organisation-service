package cd.presenceless.organisationservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DocResponse {
    private String fileName;
    private String downloadUrl;
    private String fileType;
    private long fileSize;
}
