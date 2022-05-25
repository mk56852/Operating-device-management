package com.example.ODM.Domain.User;

import com.example.ODM.Domain.ShipmentFile.ShipmentFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="users")

public class User implements UserDetails  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    @Column(nullable = false)
    private String userName ;
    @Column(nullable = false)
    private String password ;
    private String description;

    @Column(nullable = false)
    private Role role ;
    private String email ;
    private String phoneNumber ;
    private boolean isAccountNonExpired ;
    private boolean isAccountNonLocked ;
    private boolean isEnable  ;

    @OneToMany(
            mappedBy = "owner",
            cascade = CascadeType.ALL ,
            orphanRemoval = true ,
            fetch = FetchType.EAGER
    )
    private List<ShipmentFile> shipmentFiles ;


    public User(String userName , String email , String password){
        this.userName = userName ;
        this.email = email ;
        this.password = password ;
        this.description="";
        this.phoneNumber="";
        this.role = Role.ROLE_USER ;
        this.isAccountNonExpired = true ;
        this.isAccountNonLocked = true ;
        this.isEnable = true ;

    }

    public User(String userName , String email , String password , String description , Role role , String phoneNumber ) {
        this.userName = userName ;
        this.email=email ;
        this.password = password;
        this.description = description ;
        this.role = role ;
        this.phoneNumber = phoneNumber ;
        this.isEnable= true ;
        this.isAccountNonExpired = true ;
        this.isAccountNonLocked = true ;
    }


    public User(String userName , String email , String password , String description , Role role , String phoneNumber, boolean isEnable) {
        this.userName = userName ;
        this.email=email ;
        this.password = password;
        this.description = description ;
        this.role = role ;
        this.phoneNumber = phoneNumber ;
        this.isEnable = isEnable ;
        this.isAccountNonExpired = true ;
        this.isAccountNonLocked = true ;
    }


    public User(String userName , String email , String password, Role role , boolean isEnable) {
        this.userName = userName ;
        this.email = email ;
        this.password = password ;
        this.role = role ;
        this.description="";
        this.phoneNumber="";
        this.isAccountNonExpired = true ;
        this.isAccountNonLocked = true ;
        this.isEnable = isEnable ;


    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList authorities= new ArrayList() ;
        authorities.add(new SimpleGrantedAuthority(role.name())) ;
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnable;
    }
}
