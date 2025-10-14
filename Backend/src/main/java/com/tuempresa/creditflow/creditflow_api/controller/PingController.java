package com.tuempresa.creditflow.creditflow_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para confirmar que esté activa y funcionando la API.
 */
@Tag(name = "Ping", description = "Valida que la API esté activa y funcionando con normalidad.")
@RequestMapping(path = "/ping")
@RestController
public class PingController {
    @Operation(
            summary = "Pong",
            description = "No recibe ni devuelve ningún tipo de dato, tampoco realiza ninguna operación."
    )
    @ApiResponse(
            responseCode = "200",
            description = "API funcionando con normalidad.",
            content = {@Content}
    )
    @GetMapping(path = "/pong")
    public ResponseEntity<Void> pong() {
        return ResponseEntity.ok().build();
    }
}
