package com.example.ODM.Service.ConfigParamInfoServices;


import com.example.ODM.Domain.ConfigParam.ConfigParamInfo;
import com.example.ODM.Repository.ConfigParamInfoRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ConfigParamInfoServiceImpl implements ConfigParamInfoService {

    @Autowired
    private ConfigParamInfoRepository configParamInfoRepository ;


    @Override
    public List<ConfigParamInfo> getAllConfigParamOfMeter(Long meterId) {
        return configParamInfoRepository.findAllByMeterId(meterId) ;
    }
}
