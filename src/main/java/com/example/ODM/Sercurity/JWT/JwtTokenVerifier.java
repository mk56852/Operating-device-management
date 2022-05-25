package com.example.ODM.Sercurity.JWT;

import com.example.ODM.Configuration.SecurityConfiguration.JwtConfig;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class JwtTokenVerifier extends OncePerRequestFilter {


    private JwtConfig jwtConfig ;

    @Autowired
    public JwtTokenVerifier(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader( jwtConfig.getAuthorizationHeader()) ;
        if(Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith(jwtConfig.getTokenPrefix()) )
        {
            filterChain.doFilter(request,response);
            return;
        }
        try {
            String token = authorizationHeader.replace(jwtConfig.getTokenPrefix() , "") ;

           Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecretKeyForSignIn())
                    .parseClaimsJws(token) ;

           Claims body = claimsJws.getBody() ;
           String userName = body.getSubject() ;
            Authentication auth = new UsernamePasswordAuthenticationToken(userName,null,null);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        catch (JwtException e) {
            log.info(" Token Cannot be Truest") ;
        }
        filterChain.doFilter(request,response);
    }
}
