package com.yeribank.core.config;

import org.flywaydb.core.api.exception.FlywayValidateException;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FlywayRecoveryConfig.FlywayRecoveryProperties.class)
public class FlywayRecoveryConfig {

  @Bean
  public FlywayMigrationStrategy flywayMigrationStrategy(FlywayRecoveryProperties properties) {
    return flyway -> {
      try {
        flyway.migrate();
      } catch (FlywayValidateException ex) {
        if (!properties.autoRepairOnValidationError()) {
          throw ex;
        }

        flyway.repair();
        flyway.migrate();
      }
    };
  }

  @ConfigurationProperties(prefix = "app.flyway")
  public record FlywayRecoveryProperties(boolean autoRepairOnValidationError) {}
}
