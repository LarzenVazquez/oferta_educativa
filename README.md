# Sistema de Oferta Educativa

Microservicio para gestionar la oferta educativa de la institución (divisiones académicas y carreras).
El **Administrador** registra y edita la información, el **Secretario Académico** aprueba o rechaza lo que se
publica y el **Aspirante** consulta en la página web solo lo aprobado.

## Tecnologías

| Herramienta | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.2 (Web MVC, Data JPA, Validation) |
| Maven (wrapper) | 3.10.0 |
| Base de datos | H2 en memoria |
| Frontend | HTML, CSS y JavaScript |

## Estructura del repositorio

```
oferta_educativa/
├── pom.xml                          Dependencias y build (Maven)
├── mvnw / mvnw.cmd / .mvn/          Maven Wrapper
├── .gitignore / .gitattributes      Configuración de Git
└── src/
    ├── main/java/com/oferta_academica/of_academica/     BACKEND
    │   ├── OfAcademicaApplication.java
    │   ├── config/        CORS
    │   ├── controller/    Endpoints REST
    │   ├── entity/        Entidades JPA y enums
    │   └── repository/    Repositorios Spring Data JPA
    ├── main/resources/
    │   ├── application.properties
    │   └── static/                                       FRONTEND
    │       ├── index.html             Listado de oferta (HU03, HU06, HU07, HU08, HU09)
    │       ├── division-form.html     Registrar / editar división (HU01, HU02)
    │       ├── carrera-form.html      Registrar / editar carrera (HU04, HU05)
    │       ├── panel-secretario.html  Solicitudes del Secretario (HU10, HU11, HU12)
    │       ├── js/api.js              Cliente de la API
    │       └── css/styles.css
    └── test/java/...                  Pruebas
```

## Flujo de trabajo (Fork & Pull Request)

1. Cada integrante hace **fork** de `LarzenVazquez/oferta_educativa` (opción *Copy the main branch only*), un fork por historia de usuario.
2. Clona su fork, programa la historia y hace commits con el ID de la historia (`HU05-EditarCarreras`).
3. Hace `git push` a su fork y abre un **pull request** de `su-fork:main` hacia `LarzenVazquez/oferta_educativa:main`.
4. El PR se revisa (conflictos y comentarios) y el dueño del repositorio lo integra con **Merge pull request**.
5. Los demás integrantes actualizan su fork con **Sync fork** antes de iniciar la siguiente historia.

## Endpoints por historia de usuario

| HU | Método | Ruta |
|---|---|---|
| HU01 | POST | `/api/admin/divisiones` |
| HU02 | GET / PUT | `/api/admin/divisiones/{id}` |
| HU03 | PATCH | `/api/admin/divisiones/{id}/solicitar-desactivacion` |
| HU04 | POST | `/api/admin/carreras` |
| HU05 | GET / PUT | `/api/admin/carreras/{id}` |
| HU06 | PATCH | `/api/admin/carreras/{id}/solicitar-desactivacion` |
| HU07 | GET | `/api/admin/oferta?nombre=&divisionId=` |
| HU08 | PATCH | `/api/admin/divisiones/{id}/enviar-aprobacion` · `/api/admin/carreras/{id}/enviar-aprobacion` |
| HU09 | GET | `/api/admin/oferta` (estado de aprobación y motivo de rechazo) |
| HU10 | GET | `/api/admin/secretario/solicitudes` |
| HU11 | PATCH | `/api/admin/secretario/divisiones/{id}/aprobar` · `/api/admin/secretario/carreras/{id}/aprobar` |
| HU12 | PATCH | `/api/admin/divisiones/{id}/rechazar` · `/api/admin/carreras/{id}/rechazar` |
| HU13 | PATCH | `/api/admin/carreras/{id}/despublicar` |
| HU14 | GET | `/api/public/oferta` |
| HU15 | GET | `/api/public/oferta/carrera/{id}` |

## Ejecutar

```bash
git clone https://github.com/LarzenVazquez/oferta_educativa.git
cd oferta_educativa
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

- Frontend: http://localhost:8080
- Consola H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:oferta_educativa`, usuario `sa`)
