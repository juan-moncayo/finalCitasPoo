package com.clinica.citas.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.clinica.citas.domain.dto.CitaDTO;

public interface ICita {
    List<CitaDTO> getAll();
    Optional<CitaDTO> getById(Long id);
    CitaDTO save(CitaDTO dto);
    CitaDTO update(Long id, CitaDTO dto);
    boolean delete(Long id);

    // Métodos avanzados
    List<CitaDTO> getByPacienteId(Long pacienteId);
    List<CitaDTO> getByMedicoId(Long medicoId);
    List<CitaDTO> getByFecha(LocalDateTime inicio, LocalDateTime fin);
    List<CitaDTO> getByEspecialidad(Long especialidadId);
    boolean isMedicoDisponible(Long medicoId, LocalDateTime fechaHora);
    List<Long> getMedicosDisponiblesPorEspecialidad(Long especialidadId, LocalDateTime fechaHora);
}