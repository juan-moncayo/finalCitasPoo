package com.clinica.citas.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.citas.domain.dto.CitaDTO;
import com.clinica.citas.domain.dto.DisponibilidadMedicoDTO;
import com.clinica.citas.domain.service.CitaService;

@RestController
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaService svc;

    // CRUD básicos
    @GetMapping
    public List<CitaDTO> getAll() {
        return svc.obtenerTodo();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaDTO> getById(@PathVariable Long id) {
        Optional<CitaDTO> c = svc.obtenerPorId(id);
        return c.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CitaDTO> create(@RequestBody CitaDTO dto) {
        return ResponseEntity.ok(svc.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaDTO> update(
            @PathVariable Long id,
            @RequestBody CitaDTO dto) {
        return ResponseEntity.ok(svc.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (svc.eliminar(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoints avanzados
    @GetMapping("/paciente/{pacienteId}")
    public List<CitaDTO> getByPaciente(@PathVariable Long pacienteId) {
        return svc.obtenerPorPaciente(pacienteId);
    }
    
    @GetMapping("/medico/{medicoId}")
    public List<CitaDTO> getByMedico(@PathVariable Long medicoId) {
        return svc.obtenerPorMedico(medicoId);
    }
    
    @GetMapping("/fecha")
    public List<CitaDTO> getByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return svc.obtenerPorFecha(inicio, fin);
    }
    
    @GetMapping("/especialidad/{especialidadId}")
    public List<CitaDTO> getByEspecialidad(@PathVariable Long especialidadId) {
        return svc.obtenerPorEspecialidad(especialidadId);
    }
    
    // Endpoint modificado para aceptar solo fecha (sin hora)
    @GetMapping("/medicos-disponibles")
    public List<DisponibilidadMedicoDTO> getMedicosDisponibles(
            @RequestParam Long especialidadId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return svc.obtenerDisponibilidadMedicosPorFecha(especialidadId, fecha);
    }
    
    // Mantener el endpoint original para compatibilidad
    @GetMapping("/medicos-disponibles-hora")
    public List<DisponibilidadMedicoDTO> getMedicosDisponiblesPorHora(
            @RequestParam Long especialidadId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {
        return svc.obtenerDisponibilidadMedicos(especialidadId, fechaHora);
    }
    
    @PostMapping("/agenda")
    public ResponseEntity<CitaDTO> agendarCita(@RequestBody CitaDTO dto) {
        return ResponseEntity.ok(svc.guardar(dto));
    }
    
    // Endpoints para integración con pagos
    @PutMapping("/{id}/estado-pago")
    public ResponseEntity<CitaDTO> actualizarEstadoPago(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        
        String estadoPago = (String) payload.get("estadoPago");
        Long pagoId = payload.containsKey("pagoId") ? Long.valueOf(payload.get("pagoId").toString()) : null;
        
        CitaDTO citaActualizada = svc.actualizarEstadoPago(id, estadoPago, pagoId);
        if (citaActualizada != null) {
            return ResponseEntity.ok(citaActualizada);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/pago")
    public ResponseEntity<?> obtenerPagoDeCita(@PathVariable Long id) {
        Optional<CitaDTO> citaOpt = svc.obtenerPorId(id);
        if (citaOpt.isPresent()) {
            CitaDTO cita = citaOpt.get();
            if (cita.getPago() != null) {
                return ResponseEntity.ok(cita.getPago());
            } else {
                return ResponseEntity.ok(Map.of("mensaje", "La cita no tiene pago asociado"));
            }
        }
        return ResponseEntity.notFound().build();
    }
}
