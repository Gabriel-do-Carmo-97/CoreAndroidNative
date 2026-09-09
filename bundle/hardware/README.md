# Bundle `:bundle:hardware` 🔌

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:bundle--hardware-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:bundle:hardware`** é um bundle de alto nível que agrupa e integra os módulos de infraestrutura voltados ao ecossistema físico do dispositivo e localização: `:infra:core-common`, `:infra:core-device` e `:infra:core-location`.

---

## 🛠️ O que contém este bundle?

### 1. Inicializador e Fachada Hilt (`br.com.wgc.bundle.hardware`)
- [`HardwareInitializer`](file:///C:/Users/gcarm/AndroidStudioProjects/CoreAndroidNative/bundle/hardware/src/main/java/br/com/wgc/bundle/hardware/HardwareInitializer.kt): Módulo Dagger/Hilt (@InstallIn(SingletonComponent::class)) que fornece instâncias singleton da fachada de hardware.
- [`HardwareFacade`](file:///C:/Users/gcarm/AndroidStudioProjects/CoreAndroidNative/bundle/hardware/src/main/java/br/com/wgc/bundle/hardware/HardwareInitializer.kt): Ponto de entrada unificado para interações com sensores, bateria, conectividade física e geolocalização do dispositivo.

### 2. Dependências Subjacentes
- **`:infra:core-device`**: Informações de hardware, bateria, tela e permissões do dispositivo.
- **`:infra:core-location`**: Monitoramento de GPS e coordenadas geográficas com suporte a corrotinas e Flow.

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor (ex: `:app`):

```kotlin
dependencies {
    implementation("br.com.wgc:bundle-hardware:0.0.x")
}
```

---

## 💡 Exemplo de Uso

```kotlin
@Inject
lateinit var hardwareFacade: HardwareFacade

fun checkStatus() {
    val status = hardwareFacade.getStatus()
    println(status)
}
```
