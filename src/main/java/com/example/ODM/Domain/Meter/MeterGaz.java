package com.example.ODM.Domain.Meter;
import com.sun.istack.Nullable;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@AuditTable("meterHistory")
@ToString
public class MeterGaz extends Meter {


    @NotAudited
    @Column(name = "channel" , nullable = true, columnDefinition = "INTEGER DEFAULT null" )
    private Integer channel ;



    @NotAudited
    @ManyToOne( cascade = {CascadeType.ALL})
    @JoinColumn(name = "meterElecId"  ,referencedColumnName = "id" , columnDefinition = "INTEGER DEFAULT null")

    private MeterElec meterElecId ;


}
