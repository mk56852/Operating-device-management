package com.example.ODM.Configuration.SecurityConfiguration;

import com.example.ODM.Configuration.SecurityConfiguration.JwtConfig;
import com.example.ODM.Sercurity.JWT.JwtAutheticationFilter;
import com.example.ODM.Sercurity.JWT.JwtTokenVerifier;
import com.example.ODM.Service.UserService.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@EnableWebSecurity
@AllArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {



    private final UserServiceImpl userServiceImpl;
    private final BCryptPasswordEncoder bCryptPasswordEncoder ;
    private final JwtConfig jwtConfig ;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider()) ;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider()
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(userServiceImpl);
        return provider ;
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()


                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtAutheticationFilter(authenticationManager() , jwtConfig))
                .addFilterAfter(new JwtTokenVerifier(jwtConfig) , JwtAutheticationFilter.class)
                .authorizeRequests()

                .antMatchers("/js/*","/css/*"  ).permitAll()
                .antMatchers("/login*" ).permitAll()
                .antMatchers("/ws-message/**").permitAll()

                .anyRequest()
                .authenticated();


    }








}
