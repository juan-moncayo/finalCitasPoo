package com.clinica.citas.infrastructure.client.dto;

public class EspecialidadDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    
    // Constructores
    public EspecialidadDTO() {}
    
    public EspecialidadDTO(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}