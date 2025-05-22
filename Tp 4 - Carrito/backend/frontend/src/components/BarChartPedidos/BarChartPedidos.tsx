import React, { useState, useEffect } from "react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import Contenedor from "../Contenedor/Contenedor"; // Asegúrate de que esta ruta sea correcta

interface RechartsBarData {
  monthYear: string;
  count: number;
}

const BarChartPedidos: React.FC = () => {
  const [barChartData, setBarChartData] = useState<RechartsBarData[] | null>(
    null
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchBarChartData = async () => {
      try {
        setLoading(true);
        setError(null);

        const barChartResponse = await fetch(
          "http://localhost:8080/api/pedidos/chart/pedidos-by-month",
          {
            credentials: "include",
          }
        );
        if (!barChartResponse.ok) {
          throw new Error(
            `Error al obtener datos del gráfico de barras: ${barChartResponse.statusText}`
          );
        }
        const barChartRawData: [string, number][] =
          await barChartResponse.json();

        const formattedRechartsBarData: RechartsBarData[] = barChartRawData.map(
          ([monthYear, count]) => ({
            monthYear: String(monthYear),
            count: Number(count),
          })
        );

        setBarChartData(formattedRechartsBarData);
      } catch (err) {
        console.error("Error al cargar datos del gráfico de barras:", err);
        setError(
          `Error al cargar los datos del gráfico de barras: ${
            err instanceof Error ? err.message : String(err)
          }`
        );
      } finally {
        setLoading(false);
      }
    };

    fetchBarChartData();
  }, []);

  if (loading) return <div>Cargando gráfico de barras...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!barChartData) return null; // Esto no debería ocurrir si loading y error se manejan

  return (
    <Contenedor>
      {barChartData.length > 0 ? (
        <div className="chart-container bar-chart-container">
          <h3>Cantidad de Pedidos por Mes y Año</h3>
          <ResponsiveContainer width="100%" height={400}>
            <BarChart
              data={barChartData}
              margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="monthYear" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="count" fill="#197278" name="Cantidad de Pedidos" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      ) : (
        <p className="no-data-message">
          No hay datos suficientes para el gráfico de pedidos por mes y año.
        </p>
      )}
    </Contenedor>
  );
};

export default BarChartPedidos;
