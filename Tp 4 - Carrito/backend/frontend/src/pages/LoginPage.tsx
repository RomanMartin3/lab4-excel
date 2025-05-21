
import React from 'react';
import LoginForm from '../components/Login/LoginForm'; // Ajusta la ruta si es necesario
import Contenedor from '../components/Contenedor/Contenedor'; // Tu componente Contenedor
// import Titulo from '../components/Titulo/Titulo'; // El Titulo ya está en LoginForm

const LoginPage: React.FC = () => {
    return (
        <Contenedor className="login-page-container" fluid> {/* Clase para centrar o dar estilos a la página */}
            <LoginForm />
        </Contenedor>
    );
};

export default LoginPage;