Supermercado Jota Jota

Sistema web de gestion para un supermercado desarrollado en Java con Spring Boot y Thymeleaf. La aplicacion permite administrar el inventario de productos, gestionar un carrito de compras, encolar pedidos segun su prioridad y llevar un registro historico de entregas, combinando estructuras de datos en memoria RAM con persistencia en base de datos PostgreSQL.

1. Tecnologias Utilizadas

Lenguaje: Java

Framework Backend: Spring Boot (Spring Web, Spring Data JPA)

Motor de Plantillas (Frontend): Thymeleaf 

Base de Datos: PostgreSQL

ORM: Hibernate / Jakarta Persistence API (JPA)

Gestor de Dependencias: gradle

2. Arquitectura del Sistema

El proyecto implementa una arquitectura MVC (Modelo - Vista - Controlador) apoyada por una capa de servicio y una capa de persistencia:

Vista (index.html): Interfaz grafica renderizada desde el servidor mediante atributos th: de Thymeleaf. Incluye un buscador dinamico en JavaScript para filtrar productos disponibles en tiempo real.

Controlador (PaginaController): Recibe las peticiones HTTP (GET y POST) desde los formularios del navegador, se comunica con el servicio y recarga la vista principal enviando notificaciones de estado.

Servicio (ProductoService): Coordina la logica de negocio, valida los datos de entrada y mantiene sincronizadas las estructuras de datos en memoria con las tablas de PostgreSQL.

Repositorios (ProductoRepository, PedidoRepository, RegistroEntregaRepository): Interfaces de Spring Data JPA encargadas de ejecutar las consultas hacia la base de datos sin necesidad de escribir sentencias SQL manuales.

3. Estructuras de Datos en Memoria

Para optimizar las consultas y aplicar reglas de negocio especificas durante la ejecucion, el sistema mantiene cuatro estructuras:

Inventario (Inventario): Utiliza un mapa hash (HashMap<String, Producto>) donde la clave es el nombre del producto. Permite buscar existencias, validar duplicados y actualizar el stock en tiempo constante.

Cola de Entregas (ColaEntregas): Implementa una cola de prioridad (PriorityQueue / manejo de prioridad por cupon) donde los pedidos con cupon de rapidez son atendidos antes que los pedidos normales, respetando el orden de llegada (FIFO) dentro de cada categoria.

Historial de Entregas (RegistroEntregas): Almacena en una lista (ArrayList<RegistroEntrega>) todos los pedidos que ya fueron despachados.

4. Persistencia y Sincronizacion de Datos

Generacion Automatica de IDs: Las entidades Producto, Pedido y RegistroEntrega utilizan las anotaciones @Entity, @Id y @GeneratedValue(strategy = GenerationType.IDENTITY). El usuario solo ingresa el nombre, precio y stock en el formulario web, mientras que PostgreSQL asigna el identificador unico de forma secuencial y segura.

Sincronizacion Transaccional: Las operaciones criticas como confirmar una compra o despachar un pedido estan protegidas con @Transactional y el metodo ejecutarDespuesDeConfirmar. Esto garantiza que las estructuras y el archivo de registro solo se modifiquen cuando PostgreSQL haya confirmado el guardado definitivo (afterCommit) en el disco duro.

5. Flujo de Funcionalidades en la Interfaz Web

Registrar Producto (POST /productos/registrar):

El usuario ingresa nombre, precio (mayor a 0) y stock (mayor o igual a 0).

El sistema verifica que el nombre no este duplicado, guarda el producto en PostgreSQL con un ID autoincremental y lo agrega al Inventario en memoria.

Agregar al Carrito (POST /carrito/agregar):

El usuario busca el producto mediante el autocompletado e indica la cantidad.

El sistema comprueba el stock disponible en el Inventario y acumula las unidades en el Carrito.

Confirmar Compra (POST /carrito/confirmar):

El usuario ingresa el nombre del cliente y selecciona opcionalmente la casilla de cupon de rapidez.

El sistema descuenta las unidades compradas del stock en PostgreSQL y en memoria, crea un nuevo Pedido en estado PENDIENTE, lo ubica en la ColaEntregas segun su prioridad y vacia el carrito.

Procesar Siguiente Pedido (POST /pedidos/procesar):

El sistema toma el pedido mas prioritario de la ColaEntregas.

Cambia su estado a EN CAMINO, guarda el comprobante en la tabla de RegistroEntrega junto con la fecha actual, escribe el evento en el archivo de texto local y retira el pedido de la cola de espera.

6. Estructura del Proyecto

src/main/
├── java/org/example/supermercadojotajotas/
│   ├── controller/
│   │   └── PaginaController.java
│   ├── model/
│   │   ├── Carrito.java
│   │   ├── Pedido.java
│   │   ├── Producto.java
│   │   └── RegistroEntrega.java
│   ├── repository/
│   │   ├── PedidoRepository.java
│   │   ├── ProductoRepository.java
│   │   └── RegistroEntregaRepository.java
│   ├── service/
│   │   └── ProductoService.java
│   ├── structures/
│   │   ├── ColaEntregas.java
│   │   ├── Inventario.java
│   │   └── RegistroEntregas.java
│   └── SupermercadoJotaJotasApplication.java
└── resources/
    ├── templates/
    │   └── index.html
    └── application.properties


7. Requisitos y Ejecucion

Tener instalado Java JDK 17 (o superior) y PostgreSQL.

Crear la base de datos en PostgreSQL con el nombre supermercado_jota_jota y verificar las credenciales de conexion (spring.datasource.url, username y password) en el archivo src/main/resources/application.properties.


Ejecutar la clase principal SupermercadoJotaJotasApplication.

Abrir el navegador web e ingresar a http://localhost:8080/.
