package ru.justspeak.config;

import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

    @Bean
    public Settings jooqSettings() {
        return new Settings()
                .withRenderSchema(false)
                .withRenderNameCase(RenderNameCase.LOWER)
                .withRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED);
    }

    @Bean
    public DefaultConfigurationCustomizer jooqConfigurationCustomizer(Settings jooqSettings) {
        return configuration -> {
            configuration.set(SQLDialect.POSTGRES);
            configuration.set(jooqSettings);
        };
    }
}
