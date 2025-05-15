package com.clinica.citas.domain.dto;

import java.time.LocalDateTime;

public class CitaDTO {
    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private LocalDateTime fechaHora;
    private String motivo;
    private String estado; 
    private String observaciones;
    private String estadoPago; // PENDIENTE, PAGADO, ANULADO
    private Long pagoId;
    
    private String nombrePaciente;
    private String nombreMedico;
    private String especialidadMedico;
    private PagoDTO pago;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public String getNombreMedico() { return nombreMedico; }
    public void setNombreMedico(String nombreMedico) { this.nombreMedico = nombreMedico; }

    public String getEspecialidadMedico() { return especialidadMedico; }
    public void setEspecialidadMedico(String especialidadMedico) { this.especialidadMedico = especialidadMedico; }
    
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    
    public Long getPagoId() { return pagoId; }
    public void setPagoId(Long pagoId) { this.pagoId = pagoId; }
    
    public PagoDTO getPago() { return pago; }
    public void setPago(PagoDTO pago) { this.pago = pago; }
}