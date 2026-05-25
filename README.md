# CheckYourFinance

## 1. Título del proyecto

**CheckYourFinance** — Aplicación Android nativa para gestión personal de finanzas y gastos.

---
## Inicio rápido en 3 pasos

```bash
git clone https://github.com/AlanGVEGA/CheckYourFinance.git
cd CheckYourFinance
./gradlew installDebug
```
## 2. Descripción general de la aplicación

**CheckYourFinance** es una aplicación Android orientada a la gestión personal de ingresos y gastos. Permite **registro e inicio de sesión local**, **persistencia en el dispositivo** y operaciones **CRUD** sobre la entidad principal **Gasto (`Expense`)**.

La aplicación **no utiliza backend en la nube**: usuarios y transacciones se almacenan localmente mediante **Room (SQLite)** en el teléfono o tablet. La sesión del usuario autenticado se conserva con **SharedPreferences**, de modo que la app puede reabrirse sin volver a iniciar sesión.

**Identificadores del proyecto:**

| Parámetro | Valor |
|-----------|-------|
| **Nombre del proyecto (Gradle)** | `CheckYourFinance` |
| **Nombre visible de la app** | `CheckYourFinance` |
| **applicationId** | `com.example.checkyourfinance` |
| **versionName** | `1.0` |
| **versionCode** | `1` |

---

## 3. Funcionalidades principales

- **Registro e inicio de sesión local** con validación de email y contraseña (mínimo 6 caracteres).
- **Sesión persistente** mediante `SharedPreferences` (reapertura de la app sin volver a autenticarse).
- **Dashboard** con resumen financiero, navegación principal y estado de sesión.- **Listado de gastos** con `RecyclerView`, filtros por categoría y total gastado.
- **CRUD completo de gastos**: crear, listar, ver detalle, editar y eliminar registros asociados al usuario autenticado.- **Marcar gastos como favoritos** (persistido en Room).
- **Tipos de transacción** (`INCOME` / `EXPENSE`) y **métodos de pago** (efectivo, tarjetas, transferencia, etc.).
- **Modo invitado** (*Continue as Guest*): acceso al dashboard con datos de demostración (`ExpenseSampleData`); el CRUD real requiere sesión iniciada.
- **Compartir** un gasto como texto desde la pantalla de detalle.
- **Cerrar sesión** desde el dashboard.

---

## 4. Tecnologías utilizadas

| Tecnología | Versión / detalle | Uso en el proyecto |
|------------|-------------------|-------------------|
| **Kotlin** | `2.23.21` | Lenguaje principal |
| **XML** | — | Layouts de interfaz (`res/layout/`) |
| **Android Gradle Plugin (AGP)** | `9.1.0` | Compilación del módulo Android |
| **Gradle** | `9.3.1` (Gradle Wrapper) | Sistema de compilación |
| **Room** | `2.7.1` | Persistencia SQLite tipada |
| **KSP** | `2.2.10-2.0.2` | Generación de código Room |
| **SharedPreferences** | API Android | Sesión del usuario autenticado |
| **RecyclerView** | `1.3.2` | Lista de gastos |
| **Material Components** | `1.10.0` | Botones, chips, campos de texto, diálogos |
| **Kotlin Coroutines** | `1.10.1` | Operaciones de base de datos en segundo plano |
| **AndroidX AppCompat** | `1.6.1` | Compatibilidad de Activities |
| **AndroidX Activity** | `1.8.0` | APIs de Activity modernas |
| **ConstraintLayout** | `2.1.4` | Diseño de pantallas |
| **Lifecycle Runtime KTX** | `2.8.7` | `lifecycleScope` en Activities |
| **Gradle Kotlin DSL** | — | Archivos `*.gradle.kts` |

---

## 5. Arquitectura del proyecto

**Patrón identificado: MVC + capa Repository** (no MVVM completo: no hay clases `ViewModel` ni observables `LiveData`/`Flow` enlazados a la UI).

| Capa | Ubicación | Responsabilidad |
|------|-----------|-----------------|
| **Vista** | Activities + `res/layout/` + `ExpenseAdapter` | Mostrar UI y capturar eventos |
| **Controlador** | `MainActivity`, `DashboardActivity`, `ExpenseListActivity`, `ExpenseFormActivity`, `ExpenseDetailActivity` | Validar entrada, coordinar navegación, lanzar corrutinas y llamar al repositorio |
| **Modelo / persistencia** | `data/model/`, `data/local/` | Entidades Room (`UserEntity`, `ExpenseEntity`), DAOs y `AppDatabase` |
| **Repositorio** | `data/repository/FinanceRepository.kt` | API centralizada de acceso a datos |
| **Sesión** | `session/SessionManager.kt` | Estado de login en SharedPreferences |
| **Application** | `CheckYourFinanceApplication.kt` | Inicialización de base de datos, repositorio y sesión |

---

## 6. Estructura general de carpetas

```
CheckYourFinance/
├── app/
│   ├── build.gradle.kts              # SDK, dependencias del módulo app
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml   # Activities y configuración de la app
│       │   ├── java/com/example/checkyourfinance/
│       │   │   ├── MainActivity.kt              # Login / Registro / Invitado
│       │   │   ├── DashboardActivity.kt         # Dashboard post-login
│       │   │   ├── ExpenseListActivity.kt       # Listado CRUD (RecyclerView)
│       │   │   ├── ExpenseDetailActivity.kt     # Detalle de gasto
│       │   │   ├── ExpenseFormActivity.kt       # Crear / Editar gasto
│       │   │   ├── ExpenseAdapter.kt
│       │   │   ├── ExpenseUiModel.kt
│       │   │   ├── ExpenseSampleData.kt           # Datos demo (modo invitado)
│       │   │   ├── ExpenseCategories.kt
│       │   │   ├── TransactionMetadata.kt
│       │   │   ├── CheckYourFinanceApplication.kt
│       │   │   ├── data/
│       │   │   │   ├── model/                   # UserEntity, ExpenseEntity
│       │   │   │   ├── local/                   # AppDatabase, DAOs, PasswordHasher
│       │   │   │   └── repository/              # FinanceRepository
│       │   │   └── session/                     # SessionManager
│       │   └── res/
│       │       ├── layout/                      # Pantallas XML
│       │       ├── values/                      # strings, colors, themes
│       │       └── drawable/
│       ├── androidTest/                         # Pruebas instrumentadas
│       └── test/                                # Pruebas unitarias
├── gradle/
│   ├── libs.versions.toml            # Versiones centralizadas de dependencias
│   └── wrapper/
│       └── gradle-wrapper.properties # Versión de Gradle (9.3.1)
├── build.gradle.kts                  # Configuración raíz del proyecto
├── settings.gradle.kts
├── gradle.properties
├── gradlew                           # Wrapper Gradle (Linux/macOS)
├── gradlew.bat                       # Wrapper Gradle (Windows)
# Documentación técnica ampliada
└── README.md                         
```

---

## 7. Requisitos previos

| Requisito | Valor detectado en el repositorio |
|-----------|-----------------------------------|
| **Android Studio (versión recomendada)** | `Panda 2 2025.3.2` |
| **Android Gradle Plugin** | `9.1.0` |
| **Gradle** | `9.3.1` (incluido vía Gradle Wrapper) |
| **JDK** | `11` (`sourceCompatibility` / `targetCompatibility` en `app/build.gradle.kts`) |
| **compileSdk** | `36` |
| **minSdk** | `24` (Android 7.0) |
| **targetSdk** | `36` |
| **Android SDK** | API 36 (compileSdk) — debe estar instalado en el entorno de desarrollo |
| **Git** | Opcional (para clonar el repositorio) |

---

## 8. Instrucciones para compilar el proyecto

### Desde Android Studio

1. Abrir Android Studio.
2. Seleccionar **File → Open** (*Abrir un proyecto existente*) y elegir la carpeta raíz `CheckYourFinance`.
3. Esperar a que finalice la sincronización de Gradle (**Gradle Sync**).
4. Compilar el proyecto:
   - **Build → Make Project**, o
   - **Build → Build Bundle(s) / APK(s) → Build APK(s)** para generar un APK de depuración.

### Desde terminal usando Gradle

Desde la raíz del proyecto:

```bash
# Compilación completa (debug + release + tests)
./gradlew build

# Generar APK de depuración
./gradlew assembleDebug

# Generar APK de release (sin minificación activa)
./gradlew assembleRelease
```

**Salida del APK de depuración:**

```
app/build/outputs/apk/debug/app-debug.apk
```

En Windows, usar `gradlew.bat` en lugar de `./gradlew`.

---

## 9. Instrucciones para ejecutar la aplicación

### Emulador

1. En Android Studio, abrir **Device Manager** (*Administrador de dispositivos*).
2. Crear o seleccionar un **Android Virtual Device (AVD)** con **API ≥ 24**.
3. Seleccionar el módulo `app` y pulsar **Run** (▶).

### Dispositivo físico

1. En el dispositivo Android, activar **Opciones de desarrollador** y **Depuración USB**.
2. Conectar el dispositivo por USB y autorizar la depuración cuando se solicite.
3. Ejecutar desde Android Studio con **Run** (▶), o desde terminal:

```bash
./gradlew installDebug
```

Esto compila e instala la variante `debug` en el dispositivo conectado.

---

## 10. Configuración necesaria

### Permisos

El archivo `AndroidManifest.xml` **no declara permisos explícitos** (`uses-permission`). La aplicación opera con almacenamiento local en el dispositivo.

### Dependencias

Las dependencias se gestionan en:

- `gradle/libs.versions.toml` — versiones centralizadas
- `app/build.gradle.kts` — dependencias del módulo `app`

Principales dependencias de runtime:

- `androidx.room:room-runtime` / `room-ktx` **2.7.1**
- `com.google.android.material:material` **1.10.0**
- `androidx.recyclerview:recyclerview` **1.3.2**
- `androidx.appcompat:appcompat` **1.6.1**
- `androidx.activity:activity` **1.8.0**
- `androidx.constraintlayout:constraintlayout` **2.1.4**
- `org.jetbrains.kotlinx:kotlinx-coroutines-android` **1.10.1**
- `androidx.lifecycle:lifecycle-runtime-ktx` **2.8.7**

No se requiere configuración manual adicional de dependencias: Gradle las resuelve automáticamente al sincronizar el proyecto.

### Base de datos local

| Parámetro | Valor |
|-----------|-------|
| **Motor** | Room (SQLite) |
| **Nombre del archivo** | `check_your_finance.db` |
| **Versión del esquema** | `2` |
| **Entidades** | `UserEntity` (tabla `users`), `ExpenseEntity` (tabla `expenses`) |
| **Migraciones** | `MIGRATION_1_2` (añade `transactionType` y `paymentMethod` a `expenses`) |

La clase `AppDatabase` se inicializa en `CheckYourFinanceApplication`.

### Sesión

`SessionManager` almacena en SharedPreferences: `user_id`, `user_name`, `user_email` y estado de login.

---

## 11. Credenciales o usuarios de prueba

**No hay usuarios precargados en el código ni en la base de datos.** Es necesario **registrar una cuenta** en el primer uso desde `MainActivity`.

**Modo invitado:** en la pantalla de login, el botón **Continue as Guest** (*Continuar como invitado*) abre el dashboard con datos de muestra definidos en `ExpenseSampleData.kt`. En este modo, crear, editar o eliminar gastos reales requiere iniciar sesión (la app muestra un mensaje informativo).

---

## 12. Estado del proyecto

| Aspecto | Estado |
|---------|--------|
| **Login / Registro local** | Implementado |
| **CRUD de gastos** | Implementado (requiere sesión activa) |
| **Persistencia Room + sesión SharedPreferences** | Implementado |
| **Modo invitado (demo)** | Implementado |
| **Arquitectura** | MVC + Repository (sin ViewModels) |
| **Versión** | `1.0` (beta / proyecto académico) |
| **Backend en la nube** | No implementado |
| **Recuperación de contraseña** | No implementado (mensaje "coming soon") |
| **Inicio de sesión con Google / Apple** | No implementado (mensaje "coming soon") |

Estado general: **proyecto funcional en fase beta**, apto para demostración.

---

## 13. Autor o equipo

Alan Gabriel Vega Tinajaca

---

### Pantallas principales (Activities)

| # | Activity | Descripción |
|---|----------|-------------|
| 1 | `MainActivity` | Login, registro y acceso como invitado (launcher) |
| 2 | `DashboardActivity` | Hub de navegación y resumen financiero |
| 3 | `ExpenseListActivity` | Listado CRUD con `RecyclerView` |
| 4 | `ExpenseDetailActivity` | Detalle del gasto |
| 5 | `ExpenseFormActivity` | Formulario para crear o editar |


### Consideraciones técnicas

- Los datos son **locales**; desinstalar la app puede borrar la base de datos.
- Las contraseñas se almacenan como **hash SHA-256** con fines educativos; no es un esquema de seguridad de producción.
- La arquitectura es **MVC + Repository**, no MVVM completo.
---
