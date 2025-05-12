package com.clinica.citas.infrastructure.crud;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinica.citas.infrastructure.entity.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByPacienteId(Long pacienteId);
    List<Cita> findByMedicoId(Long medicoId);
    
    @Query("SELECT c FROM Cita c WHERE c.fechaHora BETWEEN :inicio AND :fin")
    List<Cita> findByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT c FROM Cita c WHERE c.medicoId = :medicoId AND c.fechaHora = :fechaHora AND c.estado != 'CANCELADA'")
    List<Cita> findByMedicoIdAndFechaHora(@Param("medicoId") Long medicoId, @Param("fechaHora") LocalDateTime fechaHora);
}