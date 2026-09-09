# Bundle `:bundle:presentation` 🎨

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:bundle--presentation-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-SIM-success.svg)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:bundle:presentation`** é um bundle de alto nível voltado à camada de apresentação, interfaces de usuário reativas com Jetpack Compose, design system e captura de mídia/câmera: `:infra:core-common`, `:infra:core-device`, `:infra:core-ui` e `:infra:core-camera`.

---

## 🛠️ O que contém este bundle?

### 1. Inicializador e Fachada Hilt (`br.com.wgc.bundle.presentation`)
- [`PresentationInitializer`](file:///C:/Users/gcarm/AndroidStudioProjects/CoreAndroidNative/bundle/presentation/src/main/java/br/com/wgc/bundle/presentation/PresentationInitializer.kt): Módulo Dagger/Hilt (@InstallIn(SingletonComponent::class)) que fornece instâncias singleton da fachada de apresentação.
- [`PresentationFacade`](file:///C:/Users/gcarm/AndroidStudioProjects/CoreAndroidNative/bundle/presentation/src/main/java/br/com/wgc/bundle/presentation/PresentationFacade.kt): Ponto de entrada unificado para componentes de UI, temas, navegação e integração de câmera.

### 2. Dependências Subjacentes
- **`:infra:core-ui`**: Design System Material 3, temas, componentes visuais reutilizáveis e utilitários de Compose.
- **`:infra:core-camera`**: Integração com CameraX para captura de fotos, vídeos e leitura de QR Codes.

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor (ex: `:app`):

```kotlin
dependencies {
    implementation("br.com.wgc:bundle-presentation:0.0.x")
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
