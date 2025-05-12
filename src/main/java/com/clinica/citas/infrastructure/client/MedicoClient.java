package com.clinica.citas.infrastructure.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.clinica.citas.infrastructure.client.dto.MedicoDTO;

@FeignClient(name = "medicos-service", url = "${medicos.service.url}")
public interface MedicoClient {
    
    @GetMapping("/medicos/{id}")
    MedicoDTO getMedicoById(@PathVariable("id") Long id);
    
    @GetMapping("/medicos/especialidad/{especialidadId}")
    List<MedicoDTO> getMedicosByEspecialidad(@PathVariable("especialidadId") Long especialidadId);
}