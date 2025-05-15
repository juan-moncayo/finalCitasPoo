package com.clinica.citas.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.clinica.citas.domain.dto.PagoDTO;

@FeignClient(name = "pagos-service", url = "${pagos.service.url}")
public interface PagoClient {
    
    @GetMapping("/pagos/{id}")
    PagoDTO getPagoById(@PathVariable("id") Long id);
    
    @GetMapping("/pagos/cita/{citaId}")
    PagoDTO[] getPagosByCitaId(@PathVariable("citaId") Long citaId);
    
    @PostMapping("/pagos")
    PagoDTO crearPago(@RequestBody PagoDTO pagoDTO);
}
