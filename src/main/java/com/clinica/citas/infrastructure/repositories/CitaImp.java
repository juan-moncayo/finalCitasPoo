package com.clinica.citas.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clinica.citas.domain.dto.CitaDTO;
import com.clinica.citas.domain.repository.ICita;
import com.clinica.citas.infrastructure.client.MedicoClient;
import com.clinica.citas.infrastructure.client.dto.MedicoDTO;
import com.clinica.citas.infrastructure.crud.CitaRepository;
import com.clinica.citas.infrastructure.entity.Cita;
import com.clinica.citas.infrastructure.mapper.CitaMapper;

@Repository
public class CitaImp implements ICita {

    @Autowired
    public CitaRepository repo;

    @Autowired
    private CitaMapper mapper;
    
    @Autowired
    private MedicoClient medicoClient;

    @Override
    public List<CitaDTO> getAll() {
        return mapper.toCitasDTO(repo.findAll());
    }

    @Override
    public Optional<CitaDTO> getById(Long id) {
        return repo.findById(id).map(mapper::toCitaDTO);
    }

    @Override
    public CitaDTO save(CitaDTO dto) {
        Cita ent = mapper.toCita(dto);
        return mapper.toCitaDTO(repo.save(ent));
    }

    @Override
    public CitaDTO update(Long id, CitaDTO dto) {
        return repo.findById(id).map(existingEntity -> {
            Cita ent = mapper.toCita(dto);
            ent.setId(id);
            return mapper.toCitaDTO(repo.save(ent));
        }).orElse(null);
    }

    @Override
    public boolean delete(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<CitaDTO> getByPacienteId(Long pacienteId) {
        return mapper.toCitasDTO(repo.findByPacienteId(pacienteId));
    }

    @Override
    public List<CitaDTO> getByMedicoId(Long medicoId) {
        return mapper.toCitasDTO(repo.findByMedicoId(medicoId));
    }

    @Override
    public List<CitaDTO> getByFecha(LocalDateTime inicio, LocalDateTime fin) {
        return mapper.toCitasDTO(repo.findByFechaBetween(inicio, fin));
    }

    @Override
    public List<CitaDTO> getByEspecialidad(Long especialidadId) {
        try {
            // Obtener médicos de la especialidad - convertir Long a String
            String especialidadNombre = getEspecialidadNombre(especialidadId);
            List<MedicoDTO> medicos = medicoClient.getMedicosByEspecialidad(especialidadNombre);
            
            List<Long> medicosIds = medicos.stream()
                    .map(MedicoDTO::getId)
                    .collect(Collectors.toList());
            
            // Filtrar citas por médicos de esa especialidad
            return repo.findAll().stream()
                    .filter(cita -> medicosIds.contains(cita.getMedicoId()))
                    .map(mapper::toCitaDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean isMedicoDisponible(Long medicoId, LocalDateTime fechaHora) {
        // Un médico está disponible si no tiene citas en ese horario
        return repo.findByMedicoIdAndFechaHora(medicoId, fechaHora).isEmpty();
    }

    @Override
    public List<Long> getMedicosDisponiblesPorEspecialidad(Long especialidadId, LocalDateTime fechaHora) {
        try {
            // Obtener médicos de la especialidad - convertir Long a String
            String especialidadNombre = getEspecialidadNombre(especialidadId);
            List<MedicoDTO> medicos = medicoClient.getMedicosByEspecialidad(especialidadNombre);
            
            // Filtrar solo los disponibles
            return medicos.stream()
                    .map(MedicoDTO::getId)
                    .filter(medicoId -> isMedicoDisponible(medicoId, fechaHora))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    // Método auxiliar para convertir ID de especialidad a nombre
    private String getEspecialidadNombre(Long especialidadId) {
        // Mapeo simple de IDs a nombres de especialidad
        switch (especialidadId.intValue()) {
            case 1: return "Cardiología";
            case 2: return "Dermatología";
            case 3: return "Pediatría";
            case 4: return "Neurología";
            case 5: return "Oftalmología";
            default: return "General";
        }
    }
}
