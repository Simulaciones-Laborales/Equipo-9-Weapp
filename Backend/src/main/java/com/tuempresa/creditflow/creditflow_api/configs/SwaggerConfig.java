package com.tuempresa.creditflow.creditflow_api.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Documentation: Killa-Deco")
                        .version("1.0")
                        .description("""
                            Documentación de la API de Killa-Deco.

                            🔗 Repositorio en GitHub: [Killa-Deco Backend](https://github.com/Java-SpringBoot-ECommerce-KillaDeco)  
                            🌐 Frontend Deploy: [killa-deco.vercel.app](https://killa-deco.vercel.app/)
                            """)
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Killa Deco"))
                )
                .addServersItem(new Server().url("https://killa-deco.onrender.com").description("Servidor de Producción"))
                .addServersItem(new Server().url("http://localhost:8080").description("Servidor de Desarrollo"))
                .addSecurityItem(new SecurityRequirement().addList("TOKEN"))
                .components(new Components()
                        .addSecuritySchemes("TOKEN",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
