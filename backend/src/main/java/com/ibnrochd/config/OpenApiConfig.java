package com.ibnrochd.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version:@latest}") String appVersion) {
        return new OpenAPI()
                .info(new Info().title("API du Collège Ibn Rochd")
                        .version(appVersion)
                        .description("Documentation de l'API pour l'application de gestion du Collège Ibn Rochd. " +
                                "Cette API permet de gérer les étudiants, professeurs, classes, cours, etc.")
                        .termsOfService("http://swagger.io/terms/") // Mettez vos propres termes de service
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
        // Vous pouvez ajouter ici la configuration pour la sécurité (JWT Bearer, etc.)
        // .components(new Components().addSecuritySchemes(...))
        // .addSecurityItem(new SecurityRequirement().addList(...))
    }
}