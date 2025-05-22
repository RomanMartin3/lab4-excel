import React, { useState, useEffect, useCallback } from "react"; // Importar useCallback
import {
  PieChart,
  Pie,
  Cell,
  Legend,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import Contenedor from "../Contenedor/Contenedor";

interface RechartsPieData {
  instrumento: string;
  cantidad: number;
}

// Componente de Tooltip personalizado
const CustomTooltip: React.FC<any> = ({ active, payload }) => {
  if (active && payload && payload.length) {
    return (
      <div style={{
        backgroundColor: '#fff',
        border: '1px solid #ccc',
        padding: '10px',
        borderRadius: '5px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
      }}>
        <p style={{ margin: '0 0 5px 0', fontWeight: 'bold' }}>{payload[0].name}</p>
        <p style={{ margin: 0 }}>Cantidad: {payload[0].value}</p>
      </div>
    );
  }
  return null;
};


const PieChartPedidos: React.FC = () => {
  const [pieChartData, setPieChartData] = useState<RechartsPieData[] | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeIndex, setActiveIndex] = useState<number | undefined>(undefined); // Nuevo estado para el índice activo

  useEffect(() => {
    const fetchPieChartData = async () => {
      try {
        setLoading(true);
        setError(null);

        const pieChartResponse = await fetch(
          "http://localhost:8080/api/pedidos/chart/quantities-by-instrument",
          {
            credentials: "include",
          }
        );
        if (!pieChartResponse.ok) {
          throw new Error(
            `Error al obtener datos del gráfico de torta: ${pieChartResponse.statusText}`
          );
        }
        const pieChartRawData: [string, number][] =
          await pieChartResponse.json();

        const formattedRechartsPieData: RechartsPieData[] = pieChartRawData.map(
          ([instrumento, cantidad]) => ({
            instrumento: String(instrumento),
            cantidad: Number(cantidad),
          })
        );

        setPieChartData(formattedRechartsPieData);
      } catch (err) {
        console.error("Error al cargar datos del gráfico de torta:", err);
        setError(
          `Error al cargar los datos del gráfico de torta: ${
            err instanceof Error ? err.message : String(err)
          }`
        );
      } finally {
        setLoading(false);
      }
    };

    fetchPieChartData();
  }, []);

  const COLORS = [
    "#E63946",
    "#457B9D",
    "#1D3557",
    "#A8DADC",
    "#F4A261",
    "#2A9D8F",
    "#FFC300",
    "#C70039",
    "#58B09C",
    "#F7D060",
    "#6A0572",
    "#90BE6D",
    "#E27D60",
    "#80ED99",
    "#DDA0DD",
    "#FF6B6B",
    "#6A0572",
    "#3D5B99",
    "#7FD8BE",
    "#FF9F1C",
  ];

  // Callback para manejar el hover en el Pie Chart
  const onPieEnter = useCallback(
    (_, index) => {
      setActiveIndex(index);
    },
    [setActiveIndex]
  );

  // Callback para manejar el fin del hover
  const onPieLeave = useCallback(() => {
    setActiveIndex(undefined);
  }, [setActiveIndex]);


  if (loading) return <div>Cargando gráfico de torta...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!pieChartData) return null;

  return (
    <Contenedor>
      {pieChartData.length > 0 ? (
        <div className="chart-container pie-chart-container">
          <h3>Cantidad de Instrumentos Vendidos</h3>
          <ResponsiveContainer width="100%" height={400}>
            <PieChart>
              <Pie
                data={pieChartData}
                dataKey="cantidad"
                nameKey="instrumento"
                cx="50%"
                cy="50%"
                outerRadius={120}
                // innerRadius={60} // Puedes descomentar para hacer un donut chart
                fill="#8884d8"
                // Deshabilitamos la etiqueta predeterminada para usar solo el tooltip y la animación
                label={false}
                // Añadimos props para el efecto de hover
                activeIndex={activeIndex}
                activeShape={{ outerRadius: 130 }} // La forma activa será un poco más grande
                onMouseEnter={onPieEnter}
                onMouseLeave={onPieLeave}
              >
                {pieChartData.map((entry, index) => (
                  <Cell
                    key={`cell-${index}`}
                    fill={COLORS[index % COLORS.length]}
                  />
                ))}
              </Pie>
              <Legend />
              <Tooltip content={<CustomTooltip />} /> {/* Usamos nuestro Tooltip personalizado */}
            </PieChart>
          </ResponsiveContainer>
        </div>
      ) : (
        <p className="no-data-message">
          No hay datos suficientes para el gráfico de instrumentos vendidos.
        </p>
      )}
    </Contenedor>
  );
};

export default PieChartPedidos;