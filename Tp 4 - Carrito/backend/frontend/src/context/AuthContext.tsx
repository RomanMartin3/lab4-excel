
import { createContext, useState, useContext, useEffect, ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { Usuario, LoginCredentials } from '../types/types'; // Asegúrate de tener estos tipos definidos
import { loginRequest, logoutRequest, getCurrentUserRequest } from '../services/authService'; // Crearemos este servicio

interface AuthContextType {
    isAuthenticated: boolean;
    user: Usuario | null;
    isLoading: boolean; // Para manejar el estado de carga inicial y durante el login/logout
    login: (credentials: LoginCredentials) => Promise<void>;
    logout: () => Promise<void>;
    checkAuthStatus: () => Promise<void>; // Para verificar al inicio
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [user, setUser] = useState<Usuario | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true); // Inicia como true para verificar el estado inicial
    const navigate = useNavigate();

    // Función para verificar el estado de autenticación al cargar la aplicación
    const checkAuthStatus = async () => {
        setIsLoading(true);
        try {
            const currentUser = await getCurrentUserRequest(); // Llama al backend (/api/auth/me o similar)
            if (currentUser) {
                setUser(currentUser);
                setIsAuthenticated(true);
            } else {
                // Esto podría pasar si la API devuelve null o un error esperado para no autenticado
                setUser(null);
                setIsAuthenticated(false);
            }
        } catch (error) {
            console.warn("No hay sesión activa o error al verificar:", error);
            setUser(null);
            setIsAuthenticated(false);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        checkAuthStatus();
    }, []); // Se ejecuta solo una vez al montar el provider

    const login = async (credentials: LoginCredentials) => {
        setIsLoading(true);
        try {
            const loggedInUser = await loginRequest(credentials); // Llama al backend
            setUser(loggedInUser);
            setIsAuthenticated(true);
            setIsLoading(false);
            // Redirigir después del login exitoso.
            // Puedes redirigir a una página específica o a la que el usuario intentaba acceder.
            // Por ahora, a la página principal o a una específica.
            navigate(loggedInUser.rol === 'ADMIN' ? '/grilla' : '/instrumentos'); // Ejemplo de redirección basada en rol
        } catch (error) {
            setIsLoading(false);
            setIsAuthenticated(false);
            setUser(null);
            console.error("Error de login:", error);
            throw error; // Relanza el error para que el componente Login lo maneje (ej. mostrar mensaje)
        }
    };

    const logout = async () => {
        setIsLoading(true);
        try {
            await logoutRequest(); // Llama al backend para invalidar la sesión
            setUser(null);
            setIsAuthenticated(false);
            setIsLoading(false);
            navigate('/login'); // Redirige a la página de login
        } catch (error) {
            setIsLoading(false);
            console.error("Error de logout:", error);
            // Incluso si hay un error, intentamos desloguear del lado del cliente
            setUser(null);
            setIsAuthenticated(false);
            navigate('/login');
            throw error;
        }
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, user, isLoading, login, logout, checkAuthStatus }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth debe ser usado dentro de un AuthProvider');
    }
    return context;
};