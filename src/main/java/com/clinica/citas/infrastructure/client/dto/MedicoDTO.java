package com.clinica.citas.infrastructure.client.dto;

public class MedicoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private EspecialidadDTO especialidad;
    private Boolean activo;
    
    private String name;
    private String specialty;
    private String phone;
    
    public MedicoDTO() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { 
        return nombre != null ? nombre : name; 
    }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getCedula() { return null; } 
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { 
        return telefono != null ? telefono : phone; 
    }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public EspecialidadDTO getEspecialidad() { 
        if (especialidad == null && specialty != null) {
            EspecialidadDTO esp = new EspecialidadDTO();
            esp.setId(0L); 
            esp.setNombre(specialty);
            esp.setDescripcion("");
            return esp;
        }
        return especialidad; 
    }
    public void setEspecialidad(EspecialidadDTO especialidad) { this.especialidad = especialidad; }
    
    public Boolean getActivo() { return activo != null ? activo : true; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}