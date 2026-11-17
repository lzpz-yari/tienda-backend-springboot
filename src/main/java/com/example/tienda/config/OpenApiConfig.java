package com.example.tienda.config;

// CLASE PARA CONFIGURAR SWAGGER (el título y descripción que aparecen arriba de la página)

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Tienda - Productos y Ventas")
                        .version("1.0")
                        .description("Microservicio con Spring Boot y PostgreSQL. "
                                + "Módulo Productos: catálogo e inventario. "
                                + "Módulo Ventas: registro de ventas que descuentan stock."));
    }
}