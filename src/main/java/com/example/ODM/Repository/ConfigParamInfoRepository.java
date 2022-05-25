package com.example.ODM.Repository;

import com.example.ODM.Domain.ConfigParam.ConfigParamInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ConfigParamInfoRepository extends JpaRepository<ConfigParamInfo,Long> {

    public ConfigParamInfo findByName(String name) ;
    public ConfigParamInfo findByNameAndMeterId(String name,Long meterId) ;
    public List<ConfigParamInfo> findAllByMeterId(Long meterId) ;

}
