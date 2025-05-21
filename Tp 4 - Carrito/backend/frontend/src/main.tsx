
import React from 'react'; // importar React si se usas <React.StrictMode>
import { createRoot } from 'react-dom/client';
import './styles/main.sass'; //archivo de estilos principal
import App from './App.tsx';
import { CartProvider } from './context/CartContext.tsx'; // CartProvider existente
import { AuthProvider } from './context/AuthContext.tsx'; // Importa AuthProvider
import { BrowserRouter } from 'react-router-dom';

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

const root = createRoot(rootElement);

root.render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <App />
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>,
);