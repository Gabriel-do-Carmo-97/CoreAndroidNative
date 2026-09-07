# Submódulo `:infra:core:location` 📍

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--location-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:infra:core:location`** gerencia serviços de geolocalização do dispositivo e cálculos geoespaciais utilizando a API do Google Play Services Location.

---

## 🛠️ O que contém este submódulo?

### 1. Cliente de Localização (`br.com.wgc.core.location`)
- [`LocationClient`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/infra/core-location/src/main/java/br/com/wgc/core/location/LocationClient.kt):
  - Abstração reativa para obtenção da localização atual do usuário (`FusedLocationProviderClient`) com suporte a coroutines e `Flow`.

### 2. Utilitários Geoespaciais (`br.com.wgc.core.location`)
- [`DistanceUtils`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/infra/core-location/src/main/java/br/com/wgc/core/location/DistanceUtils.kt):
  - Funções para cálculo de distância em metros/quilômetros entre duas coordenadas geográficas (Latitude/Longitude) usando a fórmula de Haversine.

---

## 📥 Como importar

```kotlin
dependencies {
    implementation("br.com.wgc:core-location:0.0.x")
}
```

---

## 🚀 Quando deve ser usado?

- Em aplicativos que rastreiam posição do usuário, calculam rotas, proximidade de pontos de interesse ou geofencing.
