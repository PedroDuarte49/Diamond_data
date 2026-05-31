### 🌐 Configuración de la Conexión a la API (Retrofit)

Para que la aplicación Android pueda comunicarse correctamente con tu servidor local de Django, debes ajustar la URL en el archivo de configuración dependiendo de dónde ejecutes la app. 

Abre el archivo `RetrofitClient.java`  y modifica la variable `BASE_URL`:

* **Si usas el Emulador de Android Studio:**
  Utiliza la IP `10.0.2.2`. Esta es una dirección especial que usa el emulador para referirse al `localhost` del ordenador físico.
  ```java
  private static final String BASE_URL = "http://10.0.2.2:8000/";
