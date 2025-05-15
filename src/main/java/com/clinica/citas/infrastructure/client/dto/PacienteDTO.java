package com.clinica.citas.infrastructure.client.dto;

import java.time.LocalDate;

public class PacienteDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String idNumber;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private Boolean active;

    // Constructores
    public PacienteDTO() {}
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}