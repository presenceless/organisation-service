package cd.presenceless.organisationservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Address {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    private String province, ville, commune, quartier, avenue, no;

    public Address(
            Organisation organisation,
            String province,
            String ville,
            String commune,
            String quartier,
            String avenue,
            String no
    ) {
        this.organisation = organisation;
        this.province = province;
        this.ville = ville;
        this.commune = commune;
        this.quartier = quartier;
        this.avenue = avenue;
        this.no = no;
    }

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
