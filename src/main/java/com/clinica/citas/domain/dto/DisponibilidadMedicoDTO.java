package com.clinica.citas.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DisponibilidadMedicoDTO {
    private Long medicoId;
    private String nombreMedico;
    private String especialidad;
    private List<LocalDateTime> horariosDisponibles;

    public DisponibilidadMedicoDTO() {
    }

    public DisponibilidadMedicoDTO(Long medicoId, String nombreMedico, String especialidad, List<LocalDateTime> horariosDisponibles) {
        this.medicoId = medicoId;
        this.nombreMedico = nombreMedico;
        this.especialidad = especialidad;
        this.horariosDisponibles = horariosDisponibles;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public List<LocalDateTime> getHorariosDisponibles() {
        return horariosDisponibles;
    }

    public void setHorariosDisponibles(List<LocalDateTime> horariosDisponibles) {
        this.horariosDisponibles = horariosDisponibles;
    }
}
