import { useEffect, useState } from "react";
import { PedidoResponse } from "../../types/types";
import Titulo from "../Titulo/Titulo";
import "./GrillaPedidos.sass";
import Contenedor from "../Contenedor/Contenedor";
import { fetchPedidos } from "../../services/api";

// Importa los nuevos componentes de gráficos
import BarChartPedidos from "../BarChartPedidos/BarChartPedidos";
import PieChartPedidos from "../PieChartPedidos/PieChartPedidos";

const GrillaPedidos = () => {
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [fechaDesde, setFechaDesde] = useState("");
  const [fechaHasta, setFechaHasta] = useState("");

  const descargarExcel = async () => {
    try {
      const url = new URL("http://localhost:8080/api/pedidos/excel-pedidos");

      if (fechaDesde) {
        const [yearDesde, monthDesde, dayDesde] = fechaDesde
          .split("-")
          .map(Number);
        const dateDesdeObj = new Date(
          yearDesde,
          monthDesde - 1,
          dayDesde,
          0,
          0,
          0,
          0
        );
        url.searchParams.append("fechaDesde", dateDesdeObj.toISOString());
      }

      if (fechaHasta) {
        const [yearHasta, monthHasta, dayHasta] = fechaHasta
          .split("-")
          .map(Number);
        const dateHastaObj = new Date(
          yearHasta,
          monthHasta - 1,
          dayHasta,
          23,
          59,
          59,
          999
        );
        url.searchParams.append("fechaHasta", dateHastaObj.toISOString());
      }

      console.log(
        "URL con fechas formateadas para el backend:",
        url.toString()
      );

      const response = await fetch(url.toString(), {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        if (response.status === 401) {
          throw new Error(
            "No autorizado. Tu sesión puede haber expirado. Por favor, inicia sesión nuevamente."
          );
        }
        throw new Error(
          errorData.message ||
            `Error ${response.status}: ${response.statusText}`
        );
      }

      const blob = await response.blob();
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = downloadUrl;
      link.setAttribute("download", "reporte_pedidos.xlsx");
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(downloadUrl);
    } catch (err) {
      console.error("Error completo al generar el reporte:", err);
      setError(
        `Error al generar el reporte: ${
          err instanceof Error ? err.message : String(err)
        }`
      );
    }
  };

  useEffect(() => {
    const cargarPedidos = async () => {
      try {
        const data = await fetchPedidos();
        setPedidos(data);
      } catch (err) {
        if (err instanceof Error) {
          setError(err.message);
        } else {
          setError("Error desconocido al cargar pedidos");
        }
      } finally {
        setLoading(false);
      }
    };
    cargarPedidos();
  }, []);

  const formatFecha = (fechaISO: string) => {
    const fecha = new Date(fechaISO);
    const opciones: Intl.DateTimeFormatOptions = {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      timeZone: "America/Argentina/Buenos_Aires",
    };
    return fecha.toLocaleString("es-AR", opciones);
  };

  if (loading) return <div>Cargando pedidos...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="lista-pedidos">
      <Titulo texto="Lista de pedidos" />

      <div className="filtros-reporte">
        <div className="filtro-item">
          <label>Fecha Desde:</label>
          <input
            type="date"
            value={fechaDesde}
            onChange={(e) => setFechaDesde(e.target.value)}
          />
        </div>
        <div className="filtro-item">
          <label>Fecha Hasta:</label>
          <input
            type="date"
            value={fechaHasta}
            onChange={(e) => setFechaHasta(e.target.value)}
          />
        </div>
        <button className="btn-excel" onClick={descargarExcel}>
          Generar Excel
        </button>
      </div>

      <Contenedor>
        {pedidos.length === 0 ? (
          <p>No hay pedidos registrados</p>
        ) : (
          <div className="grilla-pedidos">
            {pedidos.map((pedido) => (
              <div key={pedido.id} className="pedido-card">
                <div className="pedido-header">
                  <span>Pedido #{pedido.id}</span>
                  <span>{formatFecha(pedido.fecha)}</span>
                  <span>Total: ${pedido.total.toFixed(2)}</span>
                </div>
                {pedido.detalles.map((detalle, index) => (
                  <div key={`${pedido.id}-${index}`} className="detalle-item">
                    <span>{detalle.instrumento.instrumento}</span>
                    <div className="detalle-subitem">
                      <span>Cantidad: {detalle.cantidad}</span>
                      <span>P/U: ${detalle.precioUnitario.toFixed(2)}</span>
                      <span>
                        Subtotal: $
                        {(detalle.cantidad * detalle.precioUnitario).toFixed(2)}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            ))}
          </div>
        )}
      </Contenedor>

      {/* Renderiza los nuevos componentes de gráficos aquí */}
      <div className="charts-section">
        <BarChartPedidos />
        <PieChartPedidos />
      </div>
    </div>
  );
};

export default GrillaPedidos;