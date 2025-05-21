
import React, { useState, FormEvent } from 'react';
import { useAuth } from '../../context/AuthContext'; 
 //import { useNavigate, useLocation } from 'react-router-dom'; // No es necesario aquí si la redirección está en AuthContext
import Titulo from '../Titulo/Titulo'; // Ajusta la ruta si es necesario
import './LoginForm.sass'; 

const LoginForm: React.FC = () => {
    const [nombreUsuario, setNombreUsuario] = useState('');
    const [clave, setClave] = useState('');
    const [error, setError] = useState<string | null>(null);
    const { login, isLoading: isAuthLoading } = useAuth();
    // const navigate = useNavigate(); // La navegación se maneja en AuthContext o en LoginPage si es necesario
    // const location = useLocation();
    // const from = location.state?.from?.pathname || "/";

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError(null);

        if (!nombreUsuario.trim()) {
            setError('Por favor, ingresa tu nombre de usuario.');
            return;
        }
        if (!clave.trim()) {
            setError('Por favor, ingresa tu clave.');
            return;
        }

        try {
            await login({ nombreUsuario, clave });
            // La redirección la maneja el AuthContext al finalizar el login exitoso.
            // Si no, y quisieras redirigir desde aquí, necesitarías useNavigate.
            // navigate(from, { replace: true });
        } catch (err) {
            if (err instanceof Error) {
                setError(err.message || 'Error de autenticación. Verifica tus credenciales.');
            } else {
                setError('Ocurrió un error inesperado durante el login.');
            }
        }
    };

    return (
        <div className="login-form-wrapper">
            <Titulo texto="Iniciar Sesión" /> {/* O podrías pasar el título como prop si quieres */}
            <form onSubmit={handleSubmit} className="login-form">
                <div className="form-group">
                    <label htmlFor="login-nombreUsuario">Nombre de Usuario:</label>
                    <input
                        type="text"
                        id="login-nombreUsuario" // ID único para el input
                        value={nombreUsuario}
                        onChange={(e) => setNombreUsuario(e.target.value)}
                        disabled={isAuthLoading}
                        required
                        autoComplete="username"
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="login-clave">Clave:</label>
                    <input
                        type="password"
                        id="login-clave" // ID único para el input
                        value={clave}
                        onChange={(e) => setClave(e.target.value)}
                        disabled={isAuthLoading}
                        required
                        autoComplete="current-password"
                    />
                </div>
                {error && <p className="error-message">{error}</p>}
                <button type="submit" className="btn-login" disabled={isAuthLoading}>
                    {isAuthLoading ? 'Ingresando...' : 'Ingresar'}
                </button>
            </form>
            {/* Opcional: Enlace a una página de registro si la tienes
            <div className="register-link">
                <p>¿No tienes cuenta? <Link to="/registro">Regístrate aquí</Link></p>
            </div>
            */}
        </div>
    );
};

export default LoginForm;