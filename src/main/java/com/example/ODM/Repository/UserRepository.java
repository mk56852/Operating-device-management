package com.example.ODM.Repository;

import com.example.ODM.Domain.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    User findById(int id) ;
    User findByUserName(String username);
    User findByEmail(String email) ;

    @Query(value = "select  count(id) from User")
    public int countAll() ;


}
