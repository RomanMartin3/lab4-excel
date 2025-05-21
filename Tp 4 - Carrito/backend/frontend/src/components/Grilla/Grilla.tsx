import './Grilla.sass'
import { useNavigate } from 'react-router-dom'
import { useState, useEffect } from 'react'
import { Instrumento, Categoria } from '../../types/types'
import Titulo from '../Titulo/Titulo'
import Contenedor from '../Contenedor/Contenedor'
import { fetchCategorias, fetchInstrumentos, fetchInstrumentosPorCategoria, deleteInstrumento } from '../../services/api'
import { useAuth } from '../../context/AuthContext'

const Grilla: React.FC = () => {
  const [instrumentos, setInstrumentos] = useState<Instrumento[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [selectedCategoria, setSelectedCategoria] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();

  const esAdmin = isAuthenticated && user?.rol === 'ADMIN';


  // Obtener categorías
  useEffect(() => {
    const cargarCategorias = async () => {
      try {
        const data = await fetchCategorias();
        setCategorias(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Error desconocido");
      }
    };
    if (isAuthenticated && (user?.rol === 'ADMIN' || user?.rol === 'OPERADOR')) {
      cargarCategorias();
    } else if (isAuthenticated && user?.rol === 'VISOR') {
      setError("No tiene permisos para ver categorías y filtrar.");
    }
  }, [isAuthenticated, user]); // Añadir isAuthenticated y user como dependencias

  // Obtener instrumentos
  useEffect(() => {
    const cargarInstrumentos = async () => {
      try {
        const data = selectedCategoria !== null
          ? await fetchInstrumentosPorCategoria(selectedCategoria)
          : await fetchInstrumentos();
        setInstrumentos(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Error al cargar instrumentos");
      }
    };
    cargarInstrumentos();
  }, [selectedCategoria]);

  // Eliminar instrumento
  const handleEliminar = async (id: number) => {
    if (!esAdmin) return;
    try {
      await deleteInstrumento(id);
      setInstrumentos(prev => prev.filter(instrumento => instrumento.id !== id));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Error al eliminar instrumento");
    }
  };
  if (error && !error.includes("categorías") && !error.includes("permisos para ver")) {
    return <div className="error-message">{error}</div>;
  }

  // Si el usuario es VISOR y no tiene permiso para categorías (y por ende filtrar)
  // podrías mostrar un mensaje diferente o solo la tabla sin filtro
  if (isAuthenticated && user?.rol === 'VISOR' && error && error.includes("permisos para ver")) {
    // Continuar para mostrar la tabla de instrumentos (que es pública) pero sin filtro de categorías
    // y el mensaje de error de categorías se mostrará si lo deseas.
    // O podrías tener una UI completamente diferente para el VISOR aquí.
  }

  if (error) return <div className="error-message">{error}</div>;

  return (
    <>
      <Titulo texto='Grilla de instrumentos' />
      <Contenedor>
        {/* Filtro de Categorías: Solo visible si hay categorías cargadas (implica que el usuario es ADMIN u OPERADOR) */}
        {categorias.length > 0 && (
          <div className='filtro-categoria'>
            <div className='contenedor-select-categoria'>
              <label>Filtrar por categoría </label>
              <select
                className="select-form"
                value={selectedCategoria || ''}
                onChange={(e) => setSelectedCategoria(e.target.value ? Number(e.target.value) : null)}
              >
                <option value="">Todas las categorías</option>
                {categorias.map((categoria) => (
                  <option key={categoria.id} value={categoria.id}>
                    {categoria.denominacion}
                  </option>
                ))}
              </select>
            </div>
            {selectedCategoria && (
              <button
                className='boton-limpiar'
                onClick={() => setSelectedCategoria(null)}
              >
                Limpiar filtro
              </button>
            )}
          </div>
        )}
        {error && error.includes("categorías") && user?.rol !== 'VISOR' && (
          <div className="error-message" style={{ textAlign: 'center', margin: '10px', color: 'red' }}>
            {error}
          </div>
        )}

        <table>
          <thead>
            <tr>
              <th>Instrumento</th>
              <th>Marca</th>
              <th>Modelo</th>
              <th>Precio</th>
              <th>Costo de Envío</th>
              <th>Cantidad Vendida</th>
              <th>Descripción</th>
              <th>Categoría</th>
              {esAdmin && 
              (<th>Acciones</th>
              )} 
            </tr>
          </thead>
          <tbody>
            {instrumentos.map((instrumento) => (
              <tr key={instrumento.id}>
                <td>{instrumento.instrumento}</td>
                <td>{instrumento.marca}</td>
                <td>{instrumento.modelo}</td>
                <td>${instrumento.precio.toFixed(2)}</td>
                <td>{instrumento.costoEnvio === "G" ? "Gratis" : `$${instrumento.costoEnvio}`}</td>
                <td>{instrumento.cantidadVendida}</td>
                <td>{instrumento.descripcion}</td>
                <td>{instrumento.categoria?.denominacion || 'N/A'}</td>
                {/* <--- 4. MOSTRAR BOTONES DE ACCIONES SOLO SI ES ADMIN --- */}
                {esAdmin && (
                  <td>
                    <div className='acciones'>
                      <button
                        className='boton-modificar'
                        onClick={() => navigate(`/grilla/editar/${instrumento.id}`)}
                      >
                        Modificar
                      </button>
                      <button
                        className='boton-eliminar'
                        onClick={() => handleEliminar(instrumento.id!)}
                      >
                        Eliminar
                      </button>
                    </div>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
        {/* <--- 5. MOSTRAR BOTÓN "CREAR NUEVO INSTRUMENTO" SOLO SI ES ADMIN --- */}
        {esAdmin && (
          <div>
            <button
              className='boton-crear'
              onClick={() => navigate('/grilla/crear')}
            >
              Crear Nuevo Instrumento
            </button>
          </div>
        )}
      </Contenedor>
    </>
  );
};

export default Grilla;