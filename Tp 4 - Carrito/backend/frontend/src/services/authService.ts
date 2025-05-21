import { Usuario, LoginCredentials } from '../types/types'; 

const API_BASE_URL = 'http://localhost:8080/api/auth'; // Base URL de tus endpoints de autenticación

/**
 * Realiza la petición de login al backend.
 * @param credentials - Nombre de usuario y clave.
 * @returns Una promesa que resuelve al objeto Usuario si el login es exitoso.
 * @throws Un error si el login falla.
 */

export const loginRequest = async (credentials: LoginCredentials): Promise<Usuario> => {
    const response = await fetch(`${API_BASE_URL}/login`, { 
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
        credentials: 'include', 
    });

    if (!response.ok) {
        let errorMessage = `Error ${response.status}: ${response.statusText}`;
        try {
            const errorData = await response.json();
            // Asumiendo que tu backend envía un JSON con una propiedad 'message' o 'error'
            errorMessage = errorData.message || errorData.error || errorMessage;
        } catch (e) {
            // El cuerpo no era JSON o hubo otro error al parsearlo
            console.warn("No se pudo parsear el cuerpo del error de login como JSON.");
        }
        throw new Error(errorMessage);
    }
    return response.json() as Promise<Usuario>;
};

/**
 * Realiza la petición de logout al backend.
 * @returns Una promesa que resuelve si el logout es exitoso.
 * @throws Un error si el logout falla.
 */

export const logoutRequest = async (): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/logout`, { // Asegúrate que '/logout' sea tu endpoint
        method: 'POST', // Spring Security por defecto espera POST para logout
        credentials: 'include', // Para enviar la cookie JSESSIONID
    });

    if (!response.ok) {
        // El HttpStatusReturningLogoutSuccessHandler devuelve OK incluso si no había sesión,
        // pero si hay un error de red o un 500, esto lo atrapará.
        throw new Error('Error al cerrar sesión en el servidor');
    }
};

/**
 * Obtiene la información del usuario actualmente autenticado desde el backend.
 * @returns Una promesa que resuelve al objeto Usuario si está autenticado, o null si no.
 * @throws Un error si hay un problema de red o un error inesperado del servidor.
 */

export const getCurrentUserRequest = async (): Promise<Usuario | null> => {
    try {
        const response = await fetch(`${API_BASE_URL}/me`, { // Endpoint para obtener el usuario actual
            method: 'GET',
            credentials: 'include', 
        });

        if (response.ok) {
            return response.json() as Promise<Usuario>;
        }
        if (response.status === 401) { // No autorizado (no hay sesión válida)
            return null;
        }
        // Otros errores (ej. 500 Internal Server Error)
        const errorText = await response.text(); // Intenta obtener texto si no es JSON
        throw new Error(`Error ${response.status}: ${response.statusText}. ${errorText}`);
    } catch (error) {
        // Esto podría ser un error de red (servidor caído) o el error lanzado arriba
        console.error("Error al obtener el usuario actual:", error);
        return null; 
    }
};