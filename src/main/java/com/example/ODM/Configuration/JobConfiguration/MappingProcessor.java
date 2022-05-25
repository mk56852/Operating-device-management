package com.example.ODM.Configuration.JobConfiguration;

import com.example.ODM.Domain.Meter.Meter;
import com.example.ODM.Util.M2mMappingEntity;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


@Component
public class MappingProcessor implements ItemProcessor<Meter, M2mMappingEntity> {
    @Override
    public M2mMappingEntity process(Meter meter) throws Exception {
        return new M2mMappingEntity(meter.getDeviceName(), meter.getLogicalDeviceName()) ;
    }
}