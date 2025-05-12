package com.clinica.citas.infrastructure.client.dto;

public class MedicoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String cedula;
    private String email;
    private String telefono;
    private EspecialidadDTO especialidad;
    private Boolean activo;
    
    // Constructores
    public MedicoDTO() {}
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public EspecialidadDTO getEspecialidad() { return especialidad; }
    public void setEspecialidad(EspecialidadDTO especialidad) { this.especialidad = especialidad; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}