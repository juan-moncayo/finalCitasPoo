package com.clinica.citas.infrastructure.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.clinica.citas.infrastructure.client.dto.AvailabilityDTO;
import com.clinica.citas.infrastructure.client.dto.MedicoDTO;

@FeignClient(name = "doctores-service", url = "${doctores.service.url}")
public interface MedicoClient {
    
    @GetMapping("/doctores/{id}")
    MedicoDTO getMedicoById(@PathVariable("id") Long id);
    
    @GetMapping("/doctores/especialidad/{especialidadId}")
    List<MedicoDTO> getMedicosByEspecialidad(@PathVariable("especialidadId") String especialidadId);
    
    @GetMapping("/doctores/disponibles")
    List<AvailabilityDTO> getMedicosDisponibles(@RequestParam("fecha") String fecha);
    
    @GetMapping("/doctores/disponibles/especialidad/{especialidad}")
    List<AvailabilityDTO> getMedicosDisponiblesPorEspecialidad(
            @RequestParam("fecha") String fecha,
            @PathVariable("especialidad") String especialidad);
    
    @GetMapping("/doctores/disponibles/doctor/{id}")
    List<AvailabilityDTO> getDisponibilidadMedico(
            @RequestParam("fecha") String fecha,
            @PathVariable("id") Long id);
}
