import { Container, Nav, Navbar } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'
import { useCart } from '../../context/CartContext'
import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCartShopping, faMoon, faSun } from '@fortawesome/free-solid-svg-icons'
import { CarritoAside } from '../Carrito/CarritoAside'
import './Header.sass'
import { useAuth } from '../../context/AuthContext'; // Importa useAuth

interface HeaderProps {
  darkMode: boolean;
  toggleDarkMode: () => void;
}

const Header: React.FC<HeaderProps> = ({ darkMode, toggleDarkMode }) => {

  const { modificarCantidad, eliminarItem } = useCart();
  const navigate = useNavigate();
  const { carrito } = useCart();
  const [mostrarCarrito, setMostrarCarrito] = useState(false);
  const { isAuthenticated, user, logout, isLoading: isAuthLoading } = useAuth();

  const cantidadTotal = carrito.reduce((acc, item) => acc + item.cantidad, 0);

  const handleLogout = async () => {
    try {
      await logout(); // Esto llama a la función logout del AuthContext
      // El AuthContext ya redirige a /login, así que no necesitas navigate('/') aquí necesariamente
      // a menos que quieras un comportamiento diferente.
    } catch (error) {
      console.error("Error al cerrar sesión desde el Header:", error);
      // Podrías mostrar un mensaje de error al usuario si el logout falla
    }
  };

  return (
    <>
      <Navbar expand="lg" className='header'>
        <Container>
          <Navbar.Brand onClick={() => navigate('/')}>
            <img
              className='logo'
              src="./images/lab-4-logo.png"
              alt="laboratorio-logo"
            />
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="me-auto">
              <Nav.Link className='link' onClick={() => navigate('/')}>Inicio</Nav.Link>
              <Nav.Link className='link' onClick={() => navigate('/instrumentos')}>Instrumentos</Nav.Link>
              <Nav.Link className='link' onClick={() => navigate('/grilla')}>Grilla</Nav.Link>
              <Nav.Link className='link' onClick={() => navigate('/pedidos')}>Pedidos</Nav.Link>
            </Nav>
            <div className="acciones-header" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <div className="modo-icono" onClick={toggleDarkMode} style={{ cursor: 'pointer' }}>
                <FontAwesomeIcon icon={darkMode ? faSun : faMoon} />
              </div>
              <div className="carrito-icono" onClick={() => setMostrarCarrito(true)} style={{ cursor: 'pointer', position: 'relative' }}>
                <FontAwesomeIcon icon={faCartShopping} />
                {cantidadTotal > 0 && (
                  <span style={{
                    position: 'absolute',
                    top: '-5px',
                    right: '-10px',
                    backgroundColor: 'red',
                    color: 'white',
                    borderRadius: '50%',
                    padding: '2px 6px',
                    fontSize: '12px'
                  }}>
                    {cantidadTotal}
                  </span>
                )}
              </div>
              <div className="auth-actions">
                {isAuthenticated ? (
                  <>
                    <span style={{ marginRight: '10px', color: darkMode ? '#eee' : '#212529' }}> {/* Ajusta color para dark mode */}
                      Hola, {user?.nombreUsuario} ({user?.rol})
                    </span>
                    <button
                      onClick={handleLogout}
                      disabled={isAuthLoading} // Deshabilita si AuthContext está cargando algo (ej. durante el logout)
                      className="btn btn-outline-secondary btn-sm" // Puedes usar clases de Bootstrap o las tuyas
                      style={{ color: darkMode ? '#eee' : '#212529', borderColor: darkMode ? '#6c757d' : '#6c757d' }} // Estilo básico
                    >
                      {isAuthLoading ? 'Saliendo...' : 'Logout'}
                    </button>
                  </>
                ) : (
                  <Nav.Link
                    className='link' // Usa tu clase 'link' o una específica para login
                    onClick={() => navigate('/login')}
                    disabled={isAuthLoading} // Deshabilita si AuthContext está en estado de carga inicial
                    style={{ color: darkMode ? '#eee' : '#212529' }} // Ajusta color para dark mode
                  >
                    Login
                  </Nav.Link>
                )}
              </div>
            </div>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      <CarritoAside
        visible={mostrarCarrito}
        onClose={() => setMostrarCarrito(false)}
        modificarCantidad={(id, cantidad) => modificarCantidad(id, cantidad)}
        eliminarItem={(id) => eliminarItem(id)}
      />
    </>
  );
};

export default Header;
