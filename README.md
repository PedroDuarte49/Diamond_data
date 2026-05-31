<h1 align="center"> Diamond Data Analysis ⚾ </h1>

<p align="center">
 <img width="320" height="320" alt="ic_diamondata" src="https://github.com/user-attachments/assets/cf8de4f2-038c-4241-b3b5-f4040d5c9323" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/STATUS-EN%20DESARROLLO-green" alt="Badge en Desarrollo">
  <img src="https://img.shields.io/badge/Android-Java-orange" alt="Badge Android Java">
  <img src="https://img.shields.io/badge/Backend-Django_REST-092E20" alt="Badge Django">
  <img src="https://img.shields.io/badge/License-MIT-blue" alt="Badge Licencia">
</p>

---

## Índice

* [Descripción del Proyecto](#descripción-del-proyecto)
* [Estado del Proyecto](#estado-del-proyecto)
* [Características y Funcionalidades](#características-y-funcionalidades)
* [Acceso y Ejecución del Proyecto](#acceso-y-ejecución-del-proyecto)
* [Tecnologías Utilizadas](#tecnologías-utilizadas)
* [Autores](#autores)
* [Licencia](#licencia)

---

## Descripción del Proyecto

**Diamond Data Analysis** es una aplicación móvil nativa con arquitectura cliente-servidor diseñada para digitalizar la gestión integral y el análisis estadístico de equipos de béisbol. 

Tradicionalmente, el registro de estadísticas (*box score*) se realiza en papel, lo que dificulta el seguimiento del historial y la suma de acumulados. Este proyecto nace para ofrecer a entrenadores y mánagers una herramienta digital estructurada, rápida y eficiente a pie de campo. La app delega toda la carga matemática pesada a una API REST en Django, mostrando los datos procesados en una interfaz moderna en Android.

---

## Estado del Proyecto

<h4 align="center">
🚧 Proyecto funcional en fase de expansión 🚧
</h4>

El núcleo principal de la aplicación (CRUD completo de equipos, jugadores, partidos y motor de cálculo estadístico) está finalizado. Actualmente, se encuentran bloqueadas algunas funciones de la interfaz (como "Mi Perfil") que están planificadas para implementarse en actualizaciones futuras.

---

## Características y Funcionalidades

## :hammer: Funcionalidades principales

* `Gestión de Equipos`: Creación, edición, visualización y borrado en cascada de equipos completos.
* `Roster Dinámico`: Fichaje de jugadores asignando nombre, dorsal y posición táctica (Fielder o Pitcher).
* `Registro de Partidos`: Calendario interactivo con el registro de resultados contra equipos oponentes.
* `Box Score Detallado`: Anotación individual de estadísticas de juego para cada jugador participante en un encuentro.
* `Motor Estadístico Automático`: Cálculo en tiempo real de totales históricos (hits, promedios, ponches) agrupados por equipo y temporada.


---

## Acceso y Ejecución del Proyecto

### 📁 Acceso al código

Puedes descargar el código fuente de este proyecto clonando el repositorio mediante la terminal:
`git clone https://github.com/[TU_USUARIO]/diamond-data-analysis.git`

### 🛠️ Abre y ejecuta el servidor (Backend Django)

Para ejecutar la API REST que nutre a la aplicación móvil, sigue estos pasos en la terminal:

1. Navega a la carpeta del servidor: `cd backend`
2. Crea un entorno virtual: `python -m venv venv`
3. Activa el entorno virtual: `source venv/bin/activate` (en Windows usa `venv\Scripts\activate`)
4. Instala las dependencias: `pip install -r requirements.txt`
5. Ejecuta las migraciones de la base de datos: `python manage.py migrate`
6. Levanta el servidor local: `python manage.py runserver`

### 📱 Abre y ejecuta la aplicación (Frontend Android)

1. Abre **Android Studio**.
2. Selecciona `File > Open` y navega hasta la carpeta del cliente Android.
3. Espera a que Gradle sincronice las dependencias (Retrofit, Material Components).
4. Verifica que la URL base en `RetrofitClient.java` apunte a tu servidor local (ej. `http://10.0.2.2:8000/api/` si usas el emulador).
5. Pulsa el botón verde **Run (Play)** para instalar la app en tu emulador o dispositivo físico.

---

## Tecnologías Utilizadas

* **Cliente Móvil:** Android Nativo (Java), XML, Material Design.
* **Consumo de API:** Retrofit2, Gson.
* **Servidor / Backend:** Python, Django, Django REST Framework (DRF).
* **Documentación API:** Swagger UI, drf-spectacular (OpenAPI 3.0).
* **Base de Datos:** SQLite (escalable a PostgreSQL).

---

## Autores

| <img width="192" height="256" alt="IMG-20240827-WA0047" src="https://github.com/user-attachments/assets/5276c958-9b08-4d6d-b32f-d0a5eb64817d" /><br><sub>Pedro Neira
</sub>[(PedroDuarte49)](https://github.com/PedroDuarte49) |
| :---: |

---

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.
