Shop Project
Plataforma de comercio electrónico full-stack desarrollada con Spring Boot, Angular y PostgreSQL, orquestada con Docker Compose.
Stack tecnológico
Capa
Tecnología
Frontend
Angular 19 + Angular Material
Backend
Spring Boot 3 + Spring Security + JWT
Base de datos
PostgreSQL 17
Contenedores
Docker + Docker Compose
Exposición pública
Cloudflare Tunnel
Funcionalidades
• Autenticación con JWT y roles ROLE_ADMIN / ROLE_USER
• Catálogo de productos con filtros y búsqueda
• Carrito de compras con validación de stock en tiempo real
• Proceso de checkout con descuento automático de inventario
• Historial de ventas por usuario y vista global para administradores
• CRUD completo de productos y usuarios desde panel administrativo
• Dashboard con estadísticas, gráficos de ventas mensuales y top productos
• Protección del último administrador activo
• Precios en CLP (peso chileno)
Requisitos previos
Herramienta
Versión mínima
Docker Engine
24.x
Docker Compose
2.x (plugin)
Git
cualquiera
No es necesario tener Java, Node.js ni PostgreSQL instalados localmente.
Configuración
Crea un archivo .env en la raíz del proyecto:
POSTGRES_PASSWORD=tu_contraseña_segura
JWT_SECRET=tu_clave_jwt_de_al_menos_256_bits
Nunca subas el archivo .env al repositorio. Está incluido en .gitignore.
Ejecución
# Clonar el repositorio
git clone https://github.com/incifoxdev/shop-project.git
cd shop-project

# Crear el archivo de variables de entorno
cp .env.example .env

# Levantar todos los servicios
docker compose up --build -d

# Verificar que los contenedores estén corriendo
docker compose ps
Accede a la aplicación en:
Recurso
URL
Frontend
http://localhost
API REST
http://localhost:8080
Estructura del proyecto
.
├── docker-compose.yml
├── .env                  # variables de entorno (no subir al repo)
├── .env.example          # plantilla de variables
├── backend/
│   ├── Dockerfile
│   └── src/              # código fuente Spring Boot
└── frontend/
    ├── Dockerfile
    └── src/              # código fuente Angular
Arquitectura
Tres servicios Docker que se comunican en red interna:
Usuario → Cloudflare Tunnel → cloudflared → Frontend (Nginx :80)
                                          → API REST (Spring Boot :8080)
                                          → PostgreSQL (:5432)
El frontend se sirve mediante Nginx. La API REST se comunica con PostgreSQL a través de la red interna de Docker. PostgreSQL nunca queda expuesto a internet en producción.
Despliegue público
El proyecto se puede exponer a internet desde un servidor local usando Cloudflare Tunnel, sin necesidad de IP pública ni abrir puertos en el router.
# Instalar cloudflared
curl -L https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64.deb -o cloudflared.deb
sudo dpkg -i cloudflared.deb

# Levantar túnel temporal (pruebas)
cloudflared tunnel --url http://localhost:80
Para producción se recomienda configurar un túnel permanente con nombre desde el dashboard de Cloudflare.
Documentación
Documentación técnica completa disponible en /docs (arquitectura, endpoints, modelo de datos y consideraciones de seguridad).
Autor
Desarrollado por @incifoxdev.