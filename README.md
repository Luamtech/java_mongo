# Blog Form API  -jeje🖍

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![MongoDB](https://img.shields.io/badge/MongoDB-4.4%2B-green)
![License](https://img.shields.io/badge/license-MIT-blue)

API REST desarrollada con Spring Boot para la gestión de formularios de blog, con soporte para MongoDB y configuración de logs por ambiente.

## 📋 Tabla de Contenidos
- [Características](#-características)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Uso de la API](#-uso-de-la-api)
- [Documentación de Endpoints](#-documentación-de-endpoints)
- [Perfiles de Ejecución](#-perfiles-de-ejecución)
- [Monitoreo](#-monitoreo)
- [Ejemplos](#-ejemplos)
- [Manejo de Errores](#-manejo-de-errores)edw
- [Docker](#-docker)

## ✨ Características

* 📌 CRUD completo para formularios
* 📜 Paginación y ordenamiento
* 🔍 Filtros por país, nombre y fechas
* 👑 Validación de registros únicos por email
* 📡 Protección CSRF para solicitudes POST
* 📝 Documentación Swagger/OpenAPI
* 🔧 Logs configurables por ambiente
* 👌 MongoDB como base de datos

## 🔧 Requisitos Previos

* Java 17
* MongoDB 4.4+
* Maven o Gradle

## 🚀 Instalación y Configuración

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/blog-form-api.git
   cd blog-form-api
   ```

2. **Variables de Entorno**
   ```properties
   MONGODB_URI=mongodb://localhost:27017/blogdb
   SPRING_PROFILES_ACTIVE=dev
   SERVER_PORT=8080
   ```

3. **Compilar**
   ```bash
   # Maven
   ./mvnw clean package

   # Gradle
   ./gradlew clean build
   ```

4. **Ejecutar**
   ```bash
   java -jar target/blog-form-api-1.0.0.jar
   ```

## 🔄 Uso de la API

### Modelo de Datos
```json
{
    "email": "ejemplo@correo.com",
    "fullName": "Juan Pérez",
    "description": "Descripción opcional",
    "country": "México"
}
```

### Validaciones
| Campo | Validación |
|-------|------------|
| email | Formato válido de email |
| fullName | Requerido, entre 3 y 100 caracteres |
| description | Máximo 500 caracteres |
| country | Opcional |

## 📒 Documentación de Endpoints

### Crear Formulario (Protección CSRF)
```http
POST /api/blog-forms
```
Para realizar una solicitud `POST`, primero debes obtener el token CSRF mediante el endpoint correspondiente y luego incluirlo en el encabezado de tu solicitud.

#### Flujo de Solicitud:
1. **Obtener el Token CSRF**:
   ```http
   GET /api/csrf
   ```
   Respuesta:
   ```json
   {
       "token": "<TOKEN>",
       "headerName": "X-XSRF-TOKEN",
       "parameterName": "_csrf"
   }
   ```

2. **Realizar la Solicitud POST**:
   ```http
   POST /api/blog-forms
   Headers:
       Accept: */*
       Content-Type: application/json
       X-XSRF-TOKEN: <TOKEN>
       Authorization: Basic <CREDENCIALES EN BASE64>
   Body:
       {
           "email": "ejemplo@correo.com",
           "fullName": "Juan Pérez",
           "description": "Descripción",
           "country": "México"
       }
   ```

### Listar Formularios (Paginado)
```http
GET /api/blog-forms?page=0&size=10&sortBy=createdAt&direction=DESC
```

#### Parámetros de Paginación
| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| page | Integer | 0 | Número de página |
| size | Integer | 10 | Elementos por página |
| sortBy | String | createdAt | Campo para ordenar |
| direction | String | DESC | Dirección (ASC/DESC) |

### Obtener por ID
```http
GET /api/blog-forms/{id}
```

### Actualizar
```http
PUT /api/blog-forms/{id}
```

### Eliminar
```http
DELETE /api/blog-forms/{id}
```

## 🔧 Perfiles de Ejecución

| Perfil | Comando | Configuración |
|--------|---------|---------------|
| DEV | `--spring.profiles.active=dev` | Logs: ERROR, INFO |
| QA | `--spring.profiles.active=qa` | Logs: DEBUG, INFO, ERROR |
| PRE | `--spring.profiles.active=pre` | Logs: DEBUG |
| PROD | `--spring.profiles.active=prod` | Logs: INFO, ERROR |

## 📊 Monitoreo

```http
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

## 💡 Ejemplos

### Crear Registro
```bash
curl -X POST http://localhost:8080/api/blog-forms   -H "Content-Type: application/json"   -H "X-XSRF-TOKEN: <TOKEN>"   -H "Authorization: Basic <CREDENCIALES EN BASE64>"   -d '{
    "email": "ejemplo@correo.com",
    "fullName": "Juan Pérez",
    "description": "Descripción",
    "country": "México"
  }'
```

### Consulta Paginada con Filtros
```bash
curl "http://localhost:8080/api/blog-forms?page=0&size=10&country=México"
```

## ❌ Manejo de Errores

| Código | Descripción |
|--------|-------------|
| 200 | Éxito |
| 201 | Creado |
| 400 | Error de validación |
| 404 | No encontrado |
| 409 | Conflicto (Email duplicado) |
| 500 | Error interno |

Ejemplo de Error:
```json
{
    "timestamp": "2025-01-15T12:00:00",
    "status": 409,
    "error": "Conflict",
    "message": "A blog form with the given email already exists."
}
```

## 🐳 Docker

```bash
# Construir
docker build -t blog-form-api .

# Ejecutar
docker run -p 8080:8080   -e SPRING_PROFILES_ACTIVE=prod   -e MONGODB_URI=mongodb://mongo:27017/blogdb   blog-form-api
```

## 📜 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para detalles

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor, lee [CONTRIBUTING.md](CONTRIBUTING.md) para detalles sobre nuestro código de conducta y el proceso para enviarnos pull requests.

---
🚲 con ❤️ por [Emmanuel](https://github.com/global-manu-man)
