package cd.presenceless.organisationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "organisations")
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organisation")
    private List<Document> documents;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "organisation")
    private Address address;

    private String name;
    private String regNumber;
    private String email;
    private Date date;
    private boolean is_deleted,
            is_approved;

    public Organisation(
            String name,
            String regNumber,
            String email,
            Date date, boolean is_deleted, boolean is_approved) {
        this.name = name;
        this.regNumber = regNumber;
        this.date = date;
        this.is_deleted = is_deleted;
        this.is_approved = is_approved;
        this.email = email;
    }
}
