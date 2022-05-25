package com.example.ODM.Service.UserService;
import com.example.ODM.Domain.User.User;
import com.example.ODM.Dto.UserDto;
import com.example.ODM.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserDetailsService , UserService {


    private UserRepository userRepository ;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ModelMapper modelMapper  ;




    @Override
    public User findUserByUserName(String Username)  {
        User user =  userRepository.findByUserName(Username) ;
        return user ;

    }

    @Override
    public User findUserById(int id) {
        User user = userRepository.getById(id) ;
        return user ;
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }

    @Override
    public boolean enableUserApp(String Username) {
        return false;
    }



    @Override
    public UserDto getUserDetailsById(int id) {
        User user = findUserById(id) ;
        return modelMapper.map(user,UserDto.class) ;
    }

    @Override
    public UserDto getUserDetailsByUserName(String userName) {
        User user = this.findUserByUserName(userName);
        if (user != null) {
            UserDto userDetails = modelMapper.map(user, UserDto.class);
            return userDetails;
        }
        return null ;
    }

    @Override
    public Set<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll() ;
        Set<UserDto> usersDetails = new HashSet<>();
        users.forEach(user -> {usersDetails.add(modelMapper.map(user,UserDto.class) );});
        return usersDetails ;

    }

    @Override
    public boolean addUser(User user)  {

        UserDetails userVerifier = this.findUserByUserName(user.getUsername());
        if (userVerifier != null) {
            throw new IllegalStateException("UserName already Taken");
        } else {
            String encodedPassword =  bCryptPasswordEncoder.encode(user.getPassword()) ;
            user.setPassword(encodedPassword);
            userRepository.save(user);
            return true ;
        }
    }

    @Override
    public boolean updateUser(String UserName, User user) {
        User user1  =  this.findUserByUserName(UserName) ;
        if(user1 == null)
            return false ;
        user.setId(user1.getId());
        user.setPassword(user1.getPassword());
        userRepository.save(user) ;
        return true ;
    }

    @Override
    public boolean DeleteUser(int id) {
        User user = this.findUserById(id) ;
        if(user == null )
            return false ;
        userRepository.delete(user);
        return true ;

    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            User user = userRepository.findByUserName(username) ;
            if (user == null)
                throw new UsernameNotFoundException(String.format("UserName  :%s  is NOT found " , username)) ;
            else
                return user ;


    }
}
