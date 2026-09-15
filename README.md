# ⚡ ECOGO - Cargadores Eléctricos & Buscador de Estaciones de Carga

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%2024%2B-10B981?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin%202.2.10-10B981?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-10B981?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Maps-Google%20Maps%20Compose-10B981?style=for-the-badge&logo=googlemaps&logoColor=white" alt="Google Maps" />
</p>

---

## 🎯 Finalidad de la Aplicación

**ECOGO** es una plataforma móvil nativa para Android diseñada para **localizar, filtrar y monitorear en tiempo real estaciones de carga para vehículos eléctricos (EV) e híbridos enchufables** en Argentina.

### 💡 Problemas clave que resuelve:
1. **Eliminación de la Ansiedad de Autonomía (*Range Anxiety*)**: Permite a los conductores de taxis, flotas y vehículos particulares conocer la ubicación exacta de los tótems de carga rápida (DC) y lenta (AC).
2. **Monitoreo de Disponibilidad en Tiempo Real**: Muestra si los cargadores están **Disponibles (🟢)**, **Ocupados (🟠)** o **Fuera de Servicio (🔴)** antes de realizar un desplazamiento en vano.
3. **Filtros por Compatibilidad Técnica**: Permite filtrar estaciones estrictamente según la potencia necesaria ($22\text{ kW}$, $50\text{ kW}$, $150\text{ kW+}$) y el tipo de conector del vehículo (*CCS Tipo 2*, *Tipo 2 Mennekes*, *CHAdeMO*, *GB/T*, *Tesla Supercharger*).
4. **Navegación GPS Inmediata**: Integración con un solo toque hacia Google Maps (*"Cómo llegar"*) para trazar la ruta óptima hacia la estación.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje**: [Kotlin 2.2.10](https://kotlinlang.org/) + Coroutines + StateFlow para programación reactiva.
* **UI & Diseño**: [Jetpack Compose](https://developer.android.com/jetpack/compose) + **Material Design 3** con paleta corporativa **Eco-Green** (Verde Esmeralda `#10B981`, Menta y Verde Bosque).
* **Mapas**:
  * **Google Maps SDK for Android** ([`maps-compose`](https://github.com/googlemaps/android-maps-compose)) con marcadores personalizados e insignias de potencia.
  * **OpenStreetMap (Osmdroid HD)** como mapa vectorial de respaldo.
* **Navegación & Estado**: Android Jetpack Navigation Compose + Lifecycle ViewModel Compose.
* **Inyección de Secretos**: Gradle Secrets Injection desde `local.properties` para resguardar la API Key de Google Maps sin exponerla en el repositorio.
* **Construcción**: Gradle Kotlin DSL (`build.gradle.kts`) + Version Catalog (`gradle/libs.versions.toml`).

---

## 📁 Estructura del Proyecto

El código fuente está organizado siguiendo los principios de **Clean Architecture** y **MVVM**:

```
com.example.evfinder/
├── model/                            # Modelos del Dominio
│   ├── ChargingStation.kt            # Entidad de la estación de carga
│   ├── ConnectorType.kt              # Tipos de conector (CCS2, Type 2, CHAdeMO, etc.)
│   ├── PowerCategory.kt              # Rangos de potencia (Lenta AC, Rápida DC, etc.)
│   ├── StationStatus.kt              # Estados de disponibilidad (AVAILABLE, BUSY, etc.)
│   ├── StationFilter.kt              # Estado inmutable de filtros
│   └── User.kt                       # Usuario registrado vs Invitado
├── data/                             # Repositorios de Datos
│   ├── AuthRepository.kt             # Autenticación, registro y modo invitado
│   └── StationRepository.kt          # Dataset de estaciones en Argentina y favoritos
├── viewmodel/                        # Estado de UI y Gestión de Flujos
│   └── MainViewModel.kt              # ViewModel principal con StateFlow reactivo
├── ui/                               # Capa de Presentación (Jetpack Compose)
│   ├── theme/                        # Sistema de Diseño Eco-Green (Color, Theme, Type)
│   ├── components/                   # Componentes Reutilizables
│   │   ├── GoogleMapView.kt          # Integración nativa de Google Maps
│   │   ├── OpenMapView.kt            # Vista vectorial de OpenStreetMap
│   │   ├── StationSummaryCard.kt     # Tarjeta flotante con botón "Cómo llegar"
│   │   ├── FilterBottomSheet.kt      # Modal de filtros por potencia, conector y estado
│   │   ├── AuthDialog.kt             # Modal de Login / Registro
│   │   └── AppHeader.kt              # Barra superior con logo de EvFinder
│   ├── screens/                      # Pantallas Principales
│   │   ├── MapScreen.kt              # Mapa interactivo con buscador plegable
│   │   ├── FavoritesScreen.kt        # Estaciones favoritas del usuario
│   │   ├── ProfileScreen.kt          # Perfil e impacto ecológico estimado
│   │   └── StationDetailScreen.kt    # Detalle técnico completo de la estación
│   └── navigation/                   # Flujo de Navegación
│       └── NavGraph.kt               # Barra de navegación inferior y rutas
└── MainActivity.kt                   # Punto de entrada de la aplicación
```

---

## 🚀 Cómo Levantar y Probar el Proyecto

### 📋 Requisitos Previos:
* **Android Studio** Ladybug (2024.2.1+) o posterior.
* **JDK 17** o posterior.
* Dispositivo físico o Emulador Android con **Android 7.0 (API 24)** o superior.

### ⚙️ Pasos para ejecutar:

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/Agustin-CH/EvFinder.git
   cd EvFinder
   ```

2. **Configurar las Claves Locales (`local.properties`)**:
   Copia la plantilla de configuración e ingresa tu clave de Google Maps:
   ```bash
   cp local.properties.template local.properties
   ```
   Abre el archivo `local.properties` y añade tu clave:
   ```properties
   MAPS_API_KEY=AIzaSy_Tu_Clave_Aqui
   ```

3. **Compilar el proyecto**:
   En la terminal integrada ejecuta:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Ejecutar en el Dispositivo / Emulador**:
   * Abre el proyecto en Android Studio.
   * Haz clic en el botón **Run ▶** (o presiona `Shift + F10`).

---

## 🧪 Pruebas Unitarias

Para ejecutar la suite de pruebas unitarias automáticas:
```bash
./gradlew test
```
