package com.pakal.cloud.controller;

import jakarta.servlet.http.HttpServletRequest;  // Para manejar las solicitudes HTTP

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;  // Para el mapeo de solicitudes GET
import org.springframework.web.bind.annotation.RequestMapping;  // Para definir la ruta base del controlador
import org.springframework.web.bind.annotation.RestController;  // Para marcar esta clase como un controlador REST
import org.springframework.security.web.csrf.CsrfToken;  // Para manejar el token CSRF
import java.util.HashMap;  // Para manejar los datos de la respuesta
import java.util.Map;  // Para manejar los datos de la respuesta

@CrossOrigin(origins = {"http://localhost:8082", "https://java-mongo.onrender.com", "http://localhost:8083/swagger-ui/index.html", "http://localhost:8083/swagger-ui.html"})  // Permitir solicitudes desde estos orígenes
@RestController
@RequestMapping("/api")
public class CsrfController {
    @GetMapping("/csrf")
    public Map<String, String> getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        Map<String, String> response = new HashMap<>();
        response.put("csrfToken", csrfToken.getToken());
        response.put("headerName", csrfToken.getHeaderName());
        response.put("parameterName", csrfToken.getParameterName());
        return response;
    }
}
