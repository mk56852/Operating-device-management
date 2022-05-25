package com.example.ODM.Repository;

import com.example.ODM.Domain.Meter.MeterElec;
import com.example.ODM.Domain.Meter.MeterGaz;
import com.example.ODM.Domain.Meter.MeterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeterGazRepository extends JpaRepository<MeterGaz,Long> {

    @Modifying
    @Query( value = "delete from MeterGaz where shipmentFileId=?1 " )
    public void deleteByShipmentFileId(int id);

    public List<MeterGaz> findAllByOrderByIdDesc();
    public Page<MeterGaz> findAll(Pageable pageable) ;
    public List<MeterGaz> findAll() ;
    public Page<MeterGaz> findAllByStatus(MeterStatus status , Pageable pageable) ;
    public List<MeterGaz> findAllByShipmentFileId(int shipmentFileId) ;
    public Page<MeterGaz> findAllByShipmentFileId(int shipmentFileId ,Pageable pageable) ;


    @Query (value = "select count(id) from MeterGaz where status=?1" )
    public int countByStatus(MeterStatus status ) ;

    @Query(value = "select  count(id) from MeterGaz ")
    public int countAll() ;
}
