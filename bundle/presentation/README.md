# Bundle `:bundle:presentation` 🎨

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:bundle--presentation-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-SIM-success.svg)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:bundle:presentation`** é um bundle voltado à integração de captura visual e hardware: `:infra:core-common`, `:infra:core-device` e `:infra:core-camera`. *(Para componentes visuais, tokens e telas, utilize o DesignSystemAndroid)*.

---

## 🛠️ O que contém este bundle?

### 1. Inicializador e Fachada Hilt (`br.com.wgc.bundle.presentation`)
- [`PresentationInitializer`](src/main/java/br/com/wgc/bundle/presentation/PresentationInitializer.kt): Módulo Dagger/Hilt (`@InstallIn(SingletonComponent::class)`) que fornece instâncias singleton da fachada de apresentação.
- [`PresentationFacade`](src/main/java/br/com/wgc/bundle/presentation/PresentationFacade.kt): Ponto de entrada unificado para status e integração de câmera.

### 2. Dependências Subjacentes
- **`:infra:core-camera`**: Integração com CameraX para preview de câmera e leitura de QR Codes via ML Kit.
- **`:infra:core-device`**: Informações de dispositivo e feedback tátil.

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor (ex: `:app`):

```kotlin
dependencies {
    implementation("br.com.wgc:bundle-presentation:1.2.0")
}
```

---

## 💡 Exemplo de Uso

```kotlin
@Inject
lateinit var presentationFacade: PresentationFacade

fun checkStatus() {
    val status = presentationFacade.getStatus()
    println(status)
}
```
