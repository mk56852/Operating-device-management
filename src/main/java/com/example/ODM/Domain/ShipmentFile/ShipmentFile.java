package com.example.ODM.Domain.ShipmentFile;


import com.example.ODM.Domain.User.User;
import lombok.*;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;


import static com.example.ODM.Domain.ShipmentFile.ShipmentFileStatus.TO_TREAT;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Audited
public class ShipmentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    @NotAudited
    private String name ;
    @NotAudited
    private String typeCompteur ;
    private ShipmentFileStatus status ;

    @ManyToOne

    @Audited(targetAuditMode = NOT_AUDITED )
    @AuditJoinTable(name = "owner")
    private User owner ;
    private String reason ;


    public ShipmentFile(String name, String typeCompteur, ShipmentFileStatus status , String reason) {
        this.name = name;
        this.typeCompteur = typeCompteur;
        this.status = status;
        this.reason = reason ;

    }

    public ShipmentFile(String name, String typeCompteur) {
        this.name = name;
        this.typeCompteur = typeCompteur;
        this.status = TO_TREAT;

    }
}
