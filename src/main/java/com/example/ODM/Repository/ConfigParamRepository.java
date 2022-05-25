package com.example.ODM.Repository;

import com.example.ODM.Domain.ConfigParam.ConfigParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ConfigParamRepository extends JpaRepository<ConfigParam, Long> {
    public ConfigParam findByName(String name) ;
    public List<ConfigParam> findAllByScope(String scoop);
    @Query("select c from ConfigParam c where c.modelVersion = ?1 and c.scope like 'modelVersion' ")
    public  List<ConfigParam> findAllByModelVersion(String modelVersion) ;

}
