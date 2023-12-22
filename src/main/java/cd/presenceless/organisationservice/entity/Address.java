package cd.presenceless.organisationservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "organisation_address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    private String province, ville,
            commune, quartier, avenue, no;

    public String toString() {
        return
                "Province du " + province +
                ", ville de '" + ville +
                ", commune de '" + commune +
                ", quartier '" + quartier +
                ", avenue '" + avenue +
                ", no '" + no;
    }
}
