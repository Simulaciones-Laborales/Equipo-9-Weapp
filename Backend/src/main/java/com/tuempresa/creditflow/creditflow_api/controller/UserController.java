package com.tuempresa.creditflow.creditflow_api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "Gestionar todos los End-Points de usuarios. (RESTRINGIDO)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    
}
