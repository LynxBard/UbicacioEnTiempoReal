# Rastreador GPS

Aplicación nativa de Android desarrollada para la unidad de aprendizaje Desarrollo de Aplicaciones Móviles Nativas de la Escuela Superior de Cómputo (ESCOM - IPN).

Esta aplicación permite el rastreo de la ubicación del usuario en tiempo real, manteniendo el registro incluso cuando la aplicación se encuentra en segundo plano, y visualizando la ruta trazada sobre un mapa de Google.

## 📋 Características Principales (Requisitos del Examen)

### 1. 🛰️ Rastreo de Ubicación

Obtención de coordenadas GPS (Latitud, Longitud) mediante FusedLocationProviderClient.

Intervalos Configurables: El usuario puede seleccionar actualizaciones cada:

+ 10 segundos

+ 60 segundos

+ 5 minutos

Segundo Plano: Funciona minimizada gracias a un Foreground Service con notificación persistente.

### 2. 🗺️ Visualización en Mapa

+ Integración de Google Maps SDK.

+ Marcador en la posición actual en tiempo real.

+ Dibujado de la ruta (Polyline) conectando los puntos históricos.

+ Animación de cámara automática siguiendo al usuario.

### 3. 💾 Almacenamiento Local (Persistencia)

+ Uso de Room Database (SQLite) para guardar el historial.

+ Datos almacenados: Latitud, Longitud, Timestamp y Precisión.

+ Persistencia de datos entre reinicios de la aplicación.

### 4. 🎨 Interfaz y Personalización (UI)

Desarrollada 100% en Jetpack Compose.

Temas Dinámicos: Selector para cambiar entre:

  🟣 Tema IPN (Guinda)

  🔵 Tema ESCOM (Azul)

Soporte completo para Modo Oscuro/Claro.

Pantalla de Historial con opción para limpiar registros.

🛠️ Tecnologías Utilizadas

Lenguaje: Kotlin

UI Framework: Jetpack Compose (Material 3)

Arquitectura: MVVM (Model-View-ViewModel)

Base de Datos: Android Room

Mapas: Google Maps Compose Library

Concurrencia: Coroutines & Flow

Servicios: Android Foreground Services

### 🚀 Instalación y Configuración

Para ejecutar este proyecto, necesitas Android Studio y una API Key de Google Maps.

Clonar el repositorio:

git clone [https://github.com/TU_USUARIO/Rastreador-ESCOM.git](https://github.com/TU_USUARIO/Rastreador-ESCOM.git)


Configurar la API Key:

Obtén una API Key en Google Cloud Console habilitando el Maps SDK for Android.

Abre el archivo local.properties 

Agrega la siguiente linea: MAPS_API_KEY=TuAPIKey

Compilar:

Sincroniza el proyecto con Gradle.

Ejecuta en un emulador o dispositivo físico (Android 7.0+).

Nota: Para probar el rastreo en el emulador, recuerda usar las herramientas extendidas del emulador (tres puntos > Location) para simular movimiento o rutas GPS.

📸 Capturas de Pantalla

| Mapa (Tema IPN) | Historial | Mapa (Tema ESCOM) |

|![TemaIPN](https://github.com/user-attachments/assets/0ce45126-7e0b-4270-8976-26de17eddbee)|![Historial](https://github.com/user-attachments/assets/41f29fc4-37d0-4c8c-986b-accd1b5acc1b)|![TemaEscom](https://github.com/user-attachments/assets/c3f25e6f-6d53-4d12-8a40-44683e2645e3)|
