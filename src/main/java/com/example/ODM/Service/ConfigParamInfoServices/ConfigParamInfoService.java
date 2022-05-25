package com.example.ODM.Service.ConfigParamInfoServices;


import com.example.ODM.Domain.ConfigParam.ConfigParamInfo;

import java.util.List;

public interface ConfigParamInfoService {

    public List<ConfigParamInfo> getAllConfigParamOfMeter(Long meterId) ;
}
