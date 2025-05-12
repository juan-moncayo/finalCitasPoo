package com.clinica.citas.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.clinica.citas.infrastructure.client.dto.PacienteDTO;

@FeignClient(name = "pacientes-service", url = "${pacientes.service.url}")
public interface PacienteClient {
    
    @GetMapping("/pacientes/{id}")
    PacienteDTO getPacienteById(@PathVariable("id") Long id);
}