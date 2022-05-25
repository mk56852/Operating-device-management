package com.example.ODM.Repository;

import com.example.ODM.Domain.Location.Location;
import com.example.ODM.Util.LocationId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LocationRepository extends JpaRepository<Location, LocationId> {


    public Location findByMeterId(Long meterId) ;


}
