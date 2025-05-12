package com.clinica.citas.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinica.citas.domain.dto.CitaDTO;
import com.clinica.citas.domain.repository.ICita;
import com.clinica.citas.infrastructure.client.MedicoClient;
import com.clinica.citas.infrastructure.client.PacienteClient;
import com.clinica.citas.infrastructure.client.dto.MedicoDTO;
import com.clinica.citas.infrastructure.client.dto.PacienteDTO;

@Service
public class CitaService {

    @Autowired
    private ICita repo;
    
    @Autowired
    private PacienteClient pacienteClient;
    
    @Autowired
    private MedicoClient medicoClient;

    public List<CitaDTO> obtenerTodo() { 
        List<CitaDTO> citas = repo.getAll();
        // Enriquecer con datos de pacientes y médicos
        citas.forEach(this::enriquecerCita);
        return citas;
    }

    public Optional<CitaDTO> obtenerPorId(Long id) {
        Optional<CitaDTO> cita = repo.getById(id);
        cita.ifPresent(this::enriquecerCita);
        return cita;
    }

    public CitaDTO guardar(CitaDTO dto) {
        // Validar que el médico esté disponible
        if (!repo.isMedicoDisponible(dto.getMedicoId(), dto.getFechaHora())) {
            throw new RuntimeException("El médico no está disponible en ese horario");
        }
        
        // Validar que el paciente exista
        try {
            pacienteClient.getPacienteById(dto.getPacienteId());
        } catch (Exception e) {
            throw new RuntimeException("El paciente no existe");
        }
        
        // Validar que el médico exista
        try {
            medicoClient.getMedicoById(dto.getMedicoId());
        } catch (Exception e) {
            throw new RuntimeException("El médico no existe");
        }
        
        // Por defecto, la cita se crea en estado PROGRAMADA
        if (dto.getEstado() == null) {
            dto.setEstado("PROGRAMADA");
        }
        
        CitaDTO citaGuardada = repo.save(dto);
        enriquecerCita(citaGuardada);
        return citaGuardada;
    }

    public CitaDTO actualizar(Long id, CitaDTO dto) {
        // Si cambia el médico o la fecha, validar disponibilidad
        Optional<CitaDTO> citaExistente = repo.getById(id);
        if (citaExistente.isPresent()) {
            CitaDTO existente = citaExistente.get();
            if (!existente.getMedicoId().equals(dto.getMedicoId()) || 
                !existente.getFechaHora().equals(dto.getFechaHora())) {
                if (!repo.isMedicoDisponible(dto.getMedicoId(), dto.getFechaHora())) {
                    throw new RuntimeException("El médico no está disponible en ese horario");
                }
            }
        }
        
        CitaDTO citaActualizada = repo.update(id, dto);
        if (citaActualizada != null) {
            enriquecerCita(citaActualizada);
        }
        return citaActualizada;
    }

    public boolean eliminar(Long id) {
        return repo.delete(id);
    }

    public List<CitaDTO> obtenerPorPaciente(Long pacienteId) {
        List<CitaDTO> citas = repo.getByPacienteId(pacienteId);
        citas.forEach(this::enriquecerCita);
        return citas;
    }
    
    public List<CitaDTO> obtenerPorMedico(Long medicoId) {
        List<CitaDTO> citas = repo.getByMedicoId(medicoId);
        citas.forEach(this::enriquecerCita);
        return citas;
    }
    
    public List<CitaDTO> obtenerPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        List<CitaDTO> citas = repo.getByFecha(inicio, fin);
        citas.forEach(this::enriquecerCita);
        return citas;
    }
    
    public List<CitaDTO> obtenerPorEspecialidad(Long especialidadId) {
        List<CitaDTO> citas = repo.getByEspecialidad(especialidadId);
        citas.forEach(this::enriquecerCita);
        return citas;
    }
    
    public List<Long> obtenerMedicosDisponibles(Long especialidadId, LocalDateTime fechaHora) {
        return repo.getMedicosDisponiblesPorEspecialidad(especialidadId, fechaHora);
    }
    
    // Método para enriquecer la cita con datos de paciente y médico
    private void enriquecerCita(CitaDTO cita) {
        try {
            PacienteDTO paciente = pacienteClient.getPacienteById(cita.getPacienteId());
            cita.setNombrePaciente(paciente.getFirstName() + " " + paciente.getLastName());
        } catch (Exception e) {
            cita.setNombrePaciente("Paciente no encontrado");
        }
        
        try {
            MedicoDTO medico = medicoClient.getMedicoById(cita.getMedicoId());
            cita.setNombreMedico(medico.getNombre() + " " + medico.getApellido());
            cita.setEspecialidadMedico(medico.getEspecialidad().getNombre());
        } catch (Exception e) {
            cita.setNombreMedico("Médico no encontrado");
            cita.setEspecialidadMedico("Desconocida");
        }
    }
}