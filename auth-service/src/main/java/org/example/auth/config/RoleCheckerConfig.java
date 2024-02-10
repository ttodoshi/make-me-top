package org.example.auth.config;

import org.example.auth.utils.role.RoleChecker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class RoleCheckerConfig {
    @Bean
    @Qualifier("roleCheckerMap")
    public Map<String, RoleChecker> roleCheckerMap(List<RoleChecker> roleCheckerList) {
        return roleCheckerList.stream().collect(
                Collectors.toMap(
                        RoleChecker::getType,
                        Function.identity()
                ));
    }
}
