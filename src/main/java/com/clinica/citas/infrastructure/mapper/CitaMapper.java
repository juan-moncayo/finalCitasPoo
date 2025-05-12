package com.clinica.citas.infrastructure.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.clinica.citas.domain.dto.CitaDTO;
import com.clinica.citas.infrastructure.entity.Cita;

@Mapper(componentModel = "spring")
public interface CitaMapper {

    @Mapping(target = "nombrePaciente", ignore = true)
    @Mapping(target = "nombreMedico", ignore = true)
    @Mapping(target = "especialidadMedico", ignore = true)
    CitaDTO toCitaDTO(Cita cita);

    List<CitaDTO> toCitasDTO(List<Cita> citas);

    @InheritInverseConfiguration
    Cita toCita(CitaDTO dto);

    List<Cita> toCitas(List<CitaDTO> dtos);
}