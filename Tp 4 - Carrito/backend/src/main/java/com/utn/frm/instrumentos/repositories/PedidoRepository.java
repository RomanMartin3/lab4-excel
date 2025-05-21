package com.utn.frm.instrumentos.repositories;

import com.utn.frm.instrumentos.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.time.ZonedDateTime;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.instrumento")
    List<Pedido> findAllWithDetalles();

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.instrumento WHERE p.id = :id")
    Optional<Pedido> findByIdWithDetallesAndInstrumentos(@Param("id") Long id);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.instrumento WHERE p.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Pedido> findByFechaBetweenWithDetalles(
            @Param("fechaInicio") ZonedDateTime fechaInicio,
            @Param("fechaFin") ZonedDateTime fechaFin
    );

}