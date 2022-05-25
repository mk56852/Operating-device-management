package com.example.ODM.Configuration.SecurityConfiguration;


import com.google.common.net.HttpHeaders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.util.Arrays;


@ConfigurationProperties(prefix = "application.jwt")
@NoArgsConstructor
@Getter
@Setter
@Component
public class JwtConfig {

    private String secretKey ;
    private String tokenPrefix ;
    private Integer tokenExpirationAfterDays ;


    public SecretKey getSecretKeyForSignIn(){
        return Keys.hmacShaKeyFor(secretKey.getBytes()) ;
    }

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION ;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        source.registerCorsConfiguration("/**", config.applyPermitDefaultValues());
        //allow Authorization to be exposed
        config.setExposedHeaders(Arrays.asList("Authorization"));

        return source;
    }

}
