package com.renzo.core.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "boilerplate.authorization")

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientProperties{
    Map<String, AuthorizationProperties> authInfoMap;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorizationProperties {
        List<String> clientIp;
    }
}
