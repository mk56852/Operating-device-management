package com.example.ODM.Sercurity.JWT;

import com.example.ODM.Configuration.SecurityConfiguration.JwtConfig;

import com.example.ODM.Domain.User.User;
import com.example.ODM.Util.JwtAuthenticationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;



public class JwtAutheticationFilter extends UsernamePasswordAuthenticationFilter {


    private AuthenticationManager authenticationManager ;
    private JwtConfig jwtConfig ;

    @Autowired
    public JwtAutheticationFilter(AuthenticationManager authenticationManager , JwtConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig ;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            JwtAuthenticationRequest jwtAuthenticationRequest = new ObjectMapper().readValue(request.getInputStream(), JwtAuthenticationRequest.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(jwtAuthenticationRequest.getUserName() , jwtAuthenticationRequest.getPassword());
            Authentication auth =  authenticationManager.authenticate(authentication) ;
            return  auth ;

        }
        catch (IOException e ){
            throw new RuntimeException(e);
        }



    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user  = (User)authResult.getPrincipal();
        String token = Jwts.builder().setSubject(user.getUsername()).claim("role",user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                .signWith(jwtConfig.getSecretKeyForSignIn())
                .compact() ;

        response.addHeader(jwtConfig.getAuthorizationHeader(),jwtConfig.getTokenPrefix()+token) ;

        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request,response);
    }
}
