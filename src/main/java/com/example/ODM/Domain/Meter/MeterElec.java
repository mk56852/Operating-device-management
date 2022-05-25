package com.example.ODM.Domain.Meter;


import com.example.ODM.Domain.ConfigParam.ConfigParamInfo;
import com.example.ODM.Domain.Location.Location;
import com.sun.istack.Nullable;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@AuditTable("meterHistory")
public class MeterElec  extends Meter {



    @NotAudited
    @Nullable
    @OneToMany(mappedBy = "meterElecId" , cascade = {CascadeType.ALL})
    private List<MeterGaz> meterGazList ;




}
