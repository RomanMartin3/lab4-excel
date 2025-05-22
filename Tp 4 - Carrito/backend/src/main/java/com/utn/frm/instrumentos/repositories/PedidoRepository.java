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

    // Consulta para el Gráfico de Barras: Pedidos por Mes y Año
    // Usa FUNCTION('FORMATDATETIME', ...) para agrupar por mes y año directamente en la consulta JPQL
    @Query(value = "SELECT DATE_FORMAT(p.fecha, '%Y-%m'), COUNT(p.id) " +
            "FROM pedido p GROUP BY DATE_FORMAT(p.fecha, '%Y-%m') ORDER BY DATE_FORMAT(p.fecha, '%Y-%m')",
            nativeQuery = true)
    List<Object[]> contadorPedidosPorMesYAño();
    
    // Consulta para el Gráfico de Torta: Cantidad de Instrumentos Vendidos
    // Suma las cantidades de los detalles de pedido agrupados por instrumento
    @Query("SELECT pd.instrumento.instrumento, SUM(pd.cantidad) " +
            "FROM PedidoDetalle pd GROUP BY pd.instrumento.instrumento ORDER BY SUM(pd.cantidad) DESC")
    List<Object[]> sumaCantidadesPorInstrumento();



}