import Header from './components/Header/Header.tsx'
import Footer from './components/Footer/Footer.tsx'
// import { BrowserRouter as Router } from 'react-router-dom' YA ESTA EN MAIN
import { Container } from 'react-bootstrap'
import AppRoutes from './AppRoutes.tsx'
import { useEffect, useState } from 'react'

function App() {
  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem("darkMode") === "true";
  });

  useEffect(() => {
    document.body.classList.toggle("dark-mode", darkMode);
    localStorage.setItem("darkMode", String(darkMode));
  }, [darkMode]);

  const toggleDarkMode = () => {
    setDarkMode(prev => !prev);
  };

  return (
    <>
        <Header darkMode={darkMode} toggleDarkMode={toggleDarkMode} />
        <Container className='min-vh-100'>
          <AppRoutes />
        </Container>
        <Footer />
    </>
  )
}

export default App
