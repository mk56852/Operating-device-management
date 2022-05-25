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
public interface MeterElecRepository extends JpaRepository<MeterElec,Long> {

    @Modifying
    @Query("delete  from MeterElec m where m.shipmentFileId=?1" )
    void deleteByShipmentFileId(int id);


    public Page<MeterElec> findAll(Pageable pageable) ;
    public List<MeterElec> findAll() ;
    public List<MeterElec> findAllByOrderByIdDesc();
    public Page<MeterElec> findAllByStatus(MeterStatus status , Pageable pageable) ;
    public List<MeterElec> findAllByStatus(MeterStatus status);
    public List<MeterElec> findAllByShipmentFileId(int shipmentFileId) ;
    public Page<MeterElec> findAllByShipmentFileId(int shipmentFileId , Pageable pageable) ;

    @Query (value = "select count(id) from MeterElec where status=?1" )
    public int countByStatus(MeterStatus status ) ;

    @Query(value = "select  count(id) from MeterElec ")
    public int countAll() ;




}
