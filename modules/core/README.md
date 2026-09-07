# Módulo Agregador `:core` (Umbrella) ☂️

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--android--native-purple.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:core`** é o módulo **guarda-chuva (umbrella aggregator)** da biblioteca `CoreAndroidNative`. Ele agrega todos os 4 submódulos especializados da biblioteca via dependências `api(...)`, fornecendo compatibilidade retroativa imediata e injeção de dependências pronta para uso via **Hilt**.

---

## 🏛️ Submódulos Agregados

O `:core` expõe transitivamente para seus consumidores os seguintes módulos:

1. **[`:core:common`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/README.md):** Coroutines, ResultState, Validadores, Formatadores e CoreLogger.
2. **[`:core:storage`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/README.md):** DataStore, SharedPreferences normais e criptografados, FileManager e SessionManager.
3. **[`:core:device`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/README.md):** DeviceInfo, HapticFeedbackHelper, NetworkMonitor e Notificações.
4. **[`:core:ui`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/ui/README.md):** Jetpack Compose VisualTransformations, Modifiers e UiEffectChannel.

---

## 💉 Injeção de Dependências Hilt

Este módulo contém o [`CoreModule.kt`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/src/main/java/br/com/wgc/core/di/CoreModule.kt), que registra instâncias `@Singleton` de todas as interfaces corporativas da biblioteca:

- `CoroutineDispatchers` -> `DefaultCoroutineDispatchers`
- `CoreLogger` -> `DefaultCoreLogger`
- `KeyValueDataStore` -> `DataStorePreferencesCore`
- `KeyValueStorage` (Padrão) -> `SharedPreferencesCore`
- `KeyValueStorage` (Criptografada) -> `EncryptedSharedPreferencesCore`
- `FileManager` -> `DefaultFileManager`
- `SessionManager` -> `DefaultSessionManager`
- `DeviceInfo` -> `DefaultDeviceInfo`
- `HapticFeedbackHelper` -> `DefaultHapticFeedbackHelper`
- `NetworkMonitor` -> `NetworkMonitor`

---

## 📥 Como importar

No `build.gradle.kts` da aplicação consumidora:

```kotlin
dependencies {
    implementation("br.com.wgc:core-android-native:0.0.x")
}
```
