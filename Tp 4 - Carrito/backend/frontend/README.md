# Proyecto React + TypeScript con Vite y Mercado Pago

Este proyecto es una aplicación de tienda de instrumentos musicales desarrollada con React, TypeScript y Vite, que incluye un carrito de compras y la integración de Mercado Pago para procesar los pagos.

## 1. Crear Proyecto (Instrucciones Originales)

-   `npm create vite@latest [nombre-del-proyecto]`
    -   Crea la estructura del proyecto React.
-   `cd [nombre-del-proyecto]`
    -   Entra a la carpeta del proyecto.
-   `npm install`
    -   Instala las dependencias necesarias (crea la carpeta `node_modules`).
-   `npm run dev`
    -   Inicia el proyecto en un servidor local para ver el desarrollo en tiempo real.
-   `npm install [nombre-del-paquete]`
    -   Ejemplo: `npm install sass` para instalar SASS.
-   `npm rm [nombre-del-paquete]`
    -   Desinstala un paquete.

## 2. Clonar Repositorio y Ejecutarlo

1.  **Clonar el repositorio:**
    ```bash
    git clone [URL-del-repositorio]
    ```
2.  **Navegar a la carpeta del proyecto:**
    ```bash
    cd [nombre-del-repositorio]
    ```
3.  **Instalar dependencias generales:**
    ```bash
    npm install
    ```
4.  **Instalar dependencias específicas del proyecto (si no están ya en `package.json` como dependencias principales):**
    ```bash
    npm install sass
    npm install react-bootstrap bootstrap
    npm install react-router-dom @types/react-router-dom @fortawesome/fontawesome-svg-core @fortawesome/free-solid-svg-icons @fortawesome/react-fontawesome axios
    ```
    *(Nota: Es buena práctica tener todas las dependencias necesarias listadas en `package.json` para que un solo `npm install` sea suficiente después de clonar).*

5.  **Ejecutar el proyecto frontend:**
    ```bash
    npm run dev
    ```
6.  Abrir el enlace proporcionado en la consola (generalmente `http://localhost:5173` o similar) en tu navegador. Para ello, puedes hacer `Ctrl + click` en el enlace.

7.  **Ejecutar el Backend:**
    Este proyecto frontend requiere un backend para funcionar completamente (guardar pedidos, interactuar con Mercado Pago, etc.). Asegúrate de tener el servidor backend (desarrollado en Java/Spring Boot según el contexto) clonado, configurado y ejecutándose. Generalmente se ejecuta en `http://localhost:8080`.

## 3. Configuración y Pruebas de Mercado Pago

Para probar la funcionalidad de pago con Mercado Pago en un entorno de desarrollo, se utilizan credenciales y usuarios de prueba.

### 3.1. Configuración del Backend

El backend debe estar configurado con:

* Un **Access Token de Vendedor de Prueba** de Mercado Pago. Este se configura en el archivo `application.properties` del proyecto backend.
    ```properties
    mercadopago.access_token=TU_ACCESS_TOKEN_DE_PRUEBA_DEL_VENDEDOR
    ```
* Un **Email de Comprador de Prueba** para el objeto `payer` al crear la preferencia de pago. Este está configurado en la clase `MercadoPagoService.java` del backend.
    * Email del Comprador de Prueba (configurado en el backend): `test_user_1998127515@testuser.com`

### 3.2. Configuración del Frontend
El frontend debe estar configurado con:

* Una **Public Key de Vendedor de Prueba** de Mercado Pago. Esta se configura en el archivo `src/components/Carrito/CarritoAside.tsx`.
    ```typescript
    const MERCADOPAGO_PUBLIC_KEY = "TU_PUBLIC_KEY_DE_PRUEBA_DEL_VENDEDOR";
    ```

### 3.3. Proceso de Prueba del Checkout

1.  Asegúrate de que tanto el servidor backend como el frontend (`npm run dev`) estén ejecutándose.
2.  Navega por la tienda, añade instrumentos al carrito.
3.  Ve al carrito y haz clic en "Pagar con Mercado Pago".
4.  Serás redirigido al Checkout Pro de Mercado Pago.
5.  Para simular el pago, puedes usar las siguientes tarjetas de prueba proporcionadas por Mercado Pago:

    **Credenciales del Usuario Comprador de Prueba (para iniciar sesión en la ventana de Mercado Pago si es necesario):**
    * **Usuario:** `TESTUSER1998127515`
    * **Contraseña:** `CTh18LVecf`

    **Tarjetas de Prueba para el Checkout:**

    * **Mastercard Crédito Aprobada:**
        * Número: `5031 7557 3453 0604`
        * Código de Seguridad (CVV): `123`
        * Fecha de Vencimiento (MM/AA): `11/30` 
        * Nombre del titular: Test Test

    * **Visa Crédito Aprobada:**
        * Número: `4509 9535 6623 3704`
        * Código de Seguridad (CVV): `123`
        * Fecha de Vencimiento (MM/AA): `11/30`
        * Nombre del titular: Test Test

    * **American Express Crédito Aprobada:**
        * Número: `3711 803032 57522`
        * Código de Seguridad (CVV): `1234`
        * Fecha de Vencimiento (MM/AA): `11/30` 
        * Nombre del titular: Test Test

    * **Mastercard Débito Aprobada:**
        * Número: `5287 3383 1025 3304`
        * Código de Seguridad (CVV): `123`
        * Fecha de Vencimiento (MM/AA): `11/30` 
        * Nombre del titular: Test Test

6.  Una vez completado el pago en el entorno de Mercado Pago, serás redirigido de vuelta a la aplicación frontend a la URL de `success`, `failure` o `pending` configurada en el backend (ej. `google.com.ar` en caso de success).

## 4. Estructura del Proyecto (Frontend)

*(Puedes añadir aquí una breve descripción de las carpetas principales si lo deseas, ej: `src/components`, `src/pages`, `src/context`, `src/services`)*

## 5. Dependencias Clave

* React
* TypeScript
* Vite
* React Router DOM (para enrutamiento)
* React Bootstrap & Bootstrap (para componentes UI)
* SASS (para estilos)
* Axios (para peticiones HTTP)
* FontAwesome (para iconos)

---