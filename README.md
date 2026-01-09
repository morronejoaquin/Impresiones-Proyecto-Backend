# Sistema de Impresión - Backend

Este repositorio contiene el backend del Sistema de Impresión, desarrollado como parte de la tesis para la Tecnicatura Universitaria en Programación de la Universidad Tecnológica Nacional de Mar del Plata. El sistema completo permite administrar trabajos de impresión, controlar usuarios, registrar el historial de documentos enviados a impresión y visualizar estadísticas de impresiones.

El frontend correspondiente se encuentra en un repositorio separado: [Impresiones-Proyecto-Frontend](https://github.com/morronejoaquin/Impresiones-Proyecto-Frontend).

## Tecnologías Utilizadas

- **Framework principal**: Spring Boot (con Java).
- **Base de datos**: MySQL.
- **Herramienta de build**: Maven.
- **Otras dependencias**: En desarrollo; se incluirán librerías necesarias para manejo de API REST, conexión a base de datos y seguridad (por definir).

El backend proporciona una API REST mínima para la comunicación con el frontend, permitiendo operaciones como CRUD de datos relacionados con impresiones, usuarios y historiales.

## Requisitos Previos

Para ejecutar el backend localmente, necesitas:
- Java JDK instalado (Nosotros recomendamos: 17 o superior).
- MySQL Server instalado y configurado.
- Maven instalado.

## Instalación y Configuración

1. Clona el repositorio:
   ```
   git clone https://github.com/morronejoaquin/Impresiones-Proyecto-Backend.git
   ```

2. Navega al directorio del proyecto:
   ```
   cd Sistema-de-Impresion-Backend
   ```

3. Configura las variables de entorno:
   - Crea un archivo `.env` basado en el ejemplo proporcionado (`.env.example` si está disponible).
   - Incluye detalles como la conexión a la base de datos (e.g., `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`), claves de API u otras configuraciones sensibles.

4. Instala las dependencias:
   - `mvn install`.

5. Ejecuta la aplicación:
   - `mvn spring-boot:run`.

El servidor se iniciará en el puerto predeterminado (por ejemplo, 8080). Asegúrate de que la base de datos MySQL esté corriendo y configurada correctamente.

## Uso

Una vez iniciado, el backend expone endpoints de API REST para interactuar con el sistema. Ejemplos preliminares (sujetos a cambios durante el desarrollo):
- `GET /api/impresiones`: Obtiene la lista de trabajos de impresión.
- `POST /api/usuarios`: Registra un nuevo usuario.
- `GET /api/estadisticas`: Muestra estadísticas de impresiones.

Para probar la API, usaremos Postman principalmente. También podés usar cURL si preferís. El frontend se conectará a estos endpoints para manejar la lógica del cliente.

## Notas Adicionales

- Este proyecto está en fase de desarrollo para la tesis, con entrega prevista para febrero. Las funcionalidades y dependencias se están definiendo, por lo que el README se actualizará conforme avance.
- No hay tests implementados aún, pero se planea agregar cobertura en futuras iteraciones.
- El despliegue no está configurado por el momento; se evaluará opciones como un servidor propio o plataformas cloud.
