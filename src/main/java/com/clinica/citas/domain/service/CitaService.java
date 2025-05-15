package com.clinica.citas.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinica.citas.domain.dto.CitaDTO;
import com.clinica.citas.domain.dto.DisponibilidadMedicoDTO;
import com.clinica.citas.domain.dto.PagoDTO;
import com.clinica.citas.domain.repository.ICita;
import com.clinica.citas.infrastructure.client.MedicoClient;
import com.clinica.citas.infrastructure.client.PacienteClient;
import com.clinica.citas.infrastructure.client.PagoClient;
import com.clinica.citas.infrastructure.client.dto.AvailabilityDTO;
import com.clinica.citas.infrastructure.client.dto.MedicoDTO;
import com.clinica.citas.infrastructure.client.dto.PacienteDTO;
import com.clinica.citas.infrastructure.crud.CitaRepository;
import com.clinica.citas.infrastructure.entity.Cita;
import com.clinica.citas.infrastructure.mapper.CitaMapper;

@Service
public class CitaService {

    @Autowired
    private ICita repo;
    
    @Autowired
    private PacienteClient pacienteClient;
    
    @Autowired
    private MedicoClient medicoClient;
    
    @Autowired
    private PagoClient pagoClient;
    
    @Autowired
    private CitaMapper mapper;
    
    @Autowired
    private CitaRepository citaRepository;

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
        if (!isMedicoDisponible(dto.getMedicoId(), dto.getFechaHora())) {
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
        
        // Por defecto, el estado de pago es PENDIENTE
        dto.setEstadoPago("PENDIENTE");
        
        // Guardar la cita
        CitaDTO citaGuardada = repo.save(dto);
        
        // Crear un pago pendiente automáticamente
        try {
            PagoDTO pagoDTO = new PagoDTO();
            pagoDTO.setCitaId(citaGuardada.getId());
            pagoDTO.setMonto(new BigDecimal("100.00")); // Monto predeterminado
            pagoDTO.setEstado("PENDIENTE");
            pagoDTO.setMetodoPago("PENDIENTE");
            
            PagoDTO pagoCreado = pagoClient.crearPago(pagoDTO);
            
            // Actualizar la cita con el ID del pago
            if (pagoCreado != null && pagoCreado.getId() != null) {
                Cita cita = mapper.toCita(citaGuardada);
                cita.setPagoId(pagoCreado.getId());
                cita = citaRepository.save(cita);
                citaGuardada = mapper.toCitaDTO(cita);
                citaGuardada.setPago(pagoCreado);
            }
        } catch (Exception e) {
            System.err.println("Error al crear pago: " + e.getMessage());
        }
        
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
                if (!isMedicoDisponible(dto.getMedicoId(), dto.getFechaHora())) {
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
    
    public CitaDTO actualizarEstadoPago(Long id, String estadoPago, Long pagoId) {
        Optional<CitaDTO> citaOpt = repo.getById(id);
        if (citaOpt.isPresent()) {
            CitaDTO citaDTO = citaOpt.get();
            citaDTO.setEstadoPago(estadoPago);
            if (pagoId != null) {
                citaDTO.setPagoId(pagoId);
            }
            CitaDTO citaActualizada = repo.update(id, citaDTO);
            enriquecerCita(citaActualizada);
            return citaActualizada;
        }
        return null;
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
        // Adaptación para DoctorAPI: convertir el ID de especialidad a nombre
        String especialidadNombre = getEspecialidadNombre(especialidadId);
        
        try {
            // Obtener médicos de la especialidad
            List<MedicoDTO> medicos = medicoClient.getMedicosByEspecialidad(especialidadNombre);
            List<Long> medicosIds = medicos.stream()
                    .map(MedicoDTO::getId)
                    .collect(Collectors.toList());
            
            // Filtrar citas por médicos de esa especialidad
            List<CitaDTO> citas = obtenerTodo().stream()
                    .filter(cita -> medicosIds.contains(cita.getMedicoId()))
                    .collect(Collectors.toList());
            
            citas.forEach(this::enriquecerCita);
            return citas;
        } catch (Exception e) {
            System.err.println("Error al obtener citas por especialidad: " + e.getMessage());
            return List.of();
        }
    }
    
    // Nuevo método para obtener disponibilidad detallada de médicos
    public List<DisponibilidadMedicoDTO> obtenerDisponibilidadMedicos(Long especialidadId, LocalDateTime fechaHora) {
        String especialidadNombre = getEspecialidadNombre(especialidadId);
        String fechaStr = fechaHora.toLocalDate().format(DateTimeFormatter.ISO_DATE);
        List<DisponibilidadMedicoDTO> resultado = new ArrayList<>();
        
        try {
            // Primero obtenemos los médicos de la especialidad
            List<MedicoDTO> medicos = medicoClient.getMedicosByEspecialidad(especialidadNombre);
            
            // Para cada médico, obtenemos su disponibilidad
            for (MedicoDTO medico : medicos) {
                try {
                    // Llamar al endpoint de disponibilidad del médico
                    List<AvailabilityDTO> disponibilidades = medicoClient.getDisponibilidadMedico(
                            fechaStr, medico.getId());
                    
                    // Extraer los horarios disponibles
                    List<LocalDateTime> horariosDisponibles = disponibilidades.stream()
                            .map(AvailabilityDTO::getAvailableTime)
                            .collect(Collectors.toList());
                    
                    // Solo incluimos médicos que tengan horarios disponibles
                    if (!horariosDisponibles.isEmpty()) {
                        // Obtener el nombre del médico
                        String nombreMedico = medico.getName() != null ? 
                                medico.getName() : 
                                medico.getNombre() + " " + (medico.getApellido() != null ? medico.getApellido() : "");
                        
                        // Obtener la especialidad
                        String especialidad = medico.getSpecialty() != null ? 
                                medico.getSpecialty() : 
                                (medico.getEspecialidad() != null ? medico.getEspecialidad().getNombre() : especialidadNombre);
                        
                        // Crear y añadir el DTO de disponibilidad
                        DisponibilidadMedicoDTO disponibilidadDTO = new DisponibilidadMedicoDTO(
                                medico.getId(),
                                nombreMedico,
                                especialidad,
                                horariosDisponibles
                        );
                        
                        resultado.add(disponibilidadDTO);
                    }
                } catch (Exception e) {
                    System.err.println("Error al obtener disponibilidad del médico " + medico.getId() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener médicos por especialidad: " + e.getMessage());
        }
        
        return resultado;
    }
    
    // Nuevo método para obtener disponibilidad por fecha (sin hora)
    public List<DisponibilidadMedicoDTO> obtenerDisponibilidadMedicosPorFecha(Long especialidadId, LocalDate fecha) {
        String especialidadNombre = getEspecialidadNombre(especialidadId);
        String fechaStr = fecha.format(DateTimeFormatter.ISO_DATE);
        List<DisponibilidadMedicoDTO> resultado = new ArrayList<>();
        
        try {
            // Primero obtenemos los médicos de la especialidad
            List<MedicoDTO> medicos = medicoClient.getMedicosByEspecialidad(especialidadNombre);
            
            // Para cada médico, obtenemos su disponibilidad
            for (MedicoDTO medico : medicos) {
                try {
                    // Llamar al endpoint de disponibilidad del médico
                    List<AvailabilityDTO> disponibilidades = medicoClient.getDisponibilidadMedico(
                            fechaStr, medico.getId());
                    
                    // Extraer los horarios disponibles
                    List<LocalDateTime> horariosDisponibles = disponibilidades.stream()
                            .map(AvailabilityDTO::getAvailableTime)
                            .collect(Collectors.toList());
                    
                    // Filtrar los horarios que ya están ocupados
                    horariosDisponibles = filtrarHorariosOcupados(medico.getId(), horariosDisponibles);
                    
                    // Solo incluimos médicos que tengan horarios disponibles
                    if (!horariosDisponibles.isEmpty()) {
                        // Obtener el nombre del médico
                        String nombreMedico = medico.getName() != null ? 
                                medico.getName() : 
                                medico.getNombre() + " " + (medico.getApellido() != null ? medico.getApellido() : "");
                        
                        // Obtener la especialidad
                        String especialidad = medico.getSpecialty() != null ? 
                                medico.getSpecialty() : 
                                (medico.getEspecialidad() != null ? medico.getEspecialidad().getNombre() : especialidadNombre);
                        
                        // Crear y añadir el DTO de disponibilidad
                        DisponibilidadMedicoDTO disponibilidadDTO = new DisponibilidadMedicoDTO(
                                medico.getId(),
                                nombreMedico,
                                especialidad,
                                horariosDisponibles
                        );
                        
                        resultado.add(disponibilidadDTO);
                    }
                } catch (Exception e) {
                    System.err.println("Error al obtener disponibilidad del médico " + medico.getId() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener médicos por especialidad: " + e.getMessage());
        }
        
        return resultado;
    }
    
    // Método auxiliar para filtrar horarios que ya están ocupados
    private List<LocalDateTime> filtrarHorariosOcupados(Long medicoId, List<LocalDateTime> horarios) {
        return horarios.stream()
                .filter(horario -> repo.isMedicoDisponible(medicoId, horario))
                .collect(Collectors.toList());
    }
    
    // Método auxiliar para convertir ID de especialidad a nombre
    private String getEspecialidadNombre(Long especialidadId) {
        // Mapeo simple de IDs a nombres de especialidad
        // En un sistema real, esto podría venir de una base de datos o servicio
        switch (especialidadId.intValue()) {
            case 1: return "Cardiología";
            case 2: return "Dermatología";
            case 3: return "Pediatría";
            case 4: return "Neurología";
            case 5: return "Oftalmología";
            default: return "General";
        }
    }
    
    public List<Long> obtenerMedicosDisponibles(Long especialidadId, LocalDateTime fechaHora) {
        // Adaptación para DoctorAPI
        String especialidadNombre = getEspecialidadNombre(especialidadId);
        String fechaStr = fechaHora.toLocalDate().format(DateTimeFormatter.ISO_DATE);
        
        try {
            List<AvailabilityDTO> disponibilidades = medicoClient.getMedicosDisponiblesPorEspecialidad(
                    fechaStr, especialidadNombre);
            
            // Filtrar por la hora específica
            return disponibilidades.stream()
                    .filter(d -> d.getAvailableTime().getHour() == fechaHora.getHour())
                    .map(AvailabilityDTO::getDoctorId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al obtener médicos disponibles: " + e.getMessage());
            return List.of();
        }
    }
    
    // Método para verificar si un médico está disponible en una fecha y hora específica
    private boolean isMedicoDisponible(Long medicoId, LocalDateTime fechaHora) {
        // Verificar primero si ya hay citas para ese médico en esa fecha y hora
        if (!repo.isMedicoDisponible(medicoId, fechaHora)) {
            return false;
        }
        
        // Verificar con DoctorAPI si el médico está disponible
        try {
            String fechaStr = fechaHora.toLocalDate().format(DateTimeFormatter.ISO_DATE);
            List<AvailabilityDTO> disponibilidades = medicoClient.getDisponibilidadMedico(
                    fechaStr, medicoId);
            
            // Verificar si hay disponibilidad para la hora específica
            return disponibilidades.stream()
                    .anyMatch(d -> d.getAvailableTime().getHour() == fechaHora.getHour());
        } catch (Exception e) {
            System.err.println("Error al verificar disponibilidad del médico: " + e.getMessage());
            // Si hay un error en la comunicación, asumimos que está disponible
            // y confiamos en la verificación local
            return true;
        }
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
            // Adaptación para DoctorAPI: usar name en lugar de nombre+apellido
            if (medico.getName() != null) {
                cita.setNombreMedico(medico.getName());
            } else {
                cita.setNombreMedico(medico.getNombre() + " " + 
                    (medico.getApellido() != null ? medico.getApellido() : ""));
            }
            
            // Adaptación para DoctorAPI: usar specialty en lugar de especialidad.nombre
            if (medico.getSpecialty() != null) {
                cita.setEspecialidadMedico(medico.getSpecialty());
            } else if (medico.getEspecialidad() != null) {
                cita.setEspecialidadMedico(medico.getEspecialidad().getNombre());
            } else {
                cita.setEspecialidadMedico("Desconocida");
            }
        } catch (Exception e) {
            cita.setNombreMedico("Médico no encontrado");
            cita.setEspecialidadMedico("Desconocida");
        }
        
        // Obtener información del pago si existe
        if (cita.getPagoId() != null) {
            try {
                PagoDTO pago = pagoClient.getPagoById(cita.getPagoId());
                cita.setPago(pago);
            } catch (Exception e) {
                System.err.println("Error al obtener pago: " + e.getMessage());
            }
        }
    }
}
