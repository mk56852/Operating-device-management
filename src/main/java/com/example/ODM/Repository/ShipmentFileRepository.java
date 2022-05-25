package com.example.ODM.Repository;

import com.example.ODM.Domain.ShipmentFile.ShipmentFile;
import com.example.ODM.Domain.ShipmentFile.ShipmentFileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentFileRepository extends RevisionRepository<ShipmentFile, Integer, Integer>, JpaRepository<ShipmentFile, Integer> {
    public ShipmentFile findById(int id) ;
    public ShipmentFile findByName(String name);
    public List<ShipmentFile> findAllByOrderByIdDesc();

    @Query(value = "select  count(id) from ShipmentFile")
    public int countAll() ;

    @Query(value = "select  count(id) from ShipmentFile  where status =?1")
    public int countByStatus(ShipmentFileStatus status) ;

    public List<ShipmentFile> findTop5ByOrderByIdDesc();


}
