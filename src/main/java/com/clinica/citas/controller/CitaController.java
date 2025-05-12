package com.clinica.citas.controller;

import java.time.LocalDateTime;
import java.util.List;
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
    
    @GetMapping("/medicos-disponibles")
    public List<Long> getMedicosDisponibles(
            @RequestParam Long especialidadId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {
        return svc.obtenerMedicosDisponibles(especialidadId, fechaHora);
    }
    
    @PostMapping("/agenda")
    public ResponseEntity<CitaDTO> agendarCita(@RequestBody CitaDTO dto) {
        return ResponseEntity.ok(svc.guardar(dto));
    }
}