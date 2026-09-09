# Bundle `:bundle:networking` 🌐

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:bundle--networking-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:bundle:networking`** é um bundle de alto nível que agrupa e integra os módulos de infraestrutura voltados à comunicação de rede, requisições HTTP e conectividade: `:infra:core-common`, `:infra:core-device` e `:infra:core-network`.

---

## 🛠️ O que contém este bundle?

### 1. Inicializador e Fachada Hilt (`br.com.wgc.bundle.networking`)
- [`NetworkingInitializer`](file:///C:/Users/gcarm/AndroidStudioProjects/CoreAndroidNative/bundle/networking/src/main/java/br/com/wgc/bundle/networking/NetworkingInitializer.kt): Módulo Dagger/Hilt (@InstallIn(SingletonComponent::class)) que fornece instâncias singleton da fachada de networking.
- [`NetworkingFacade`](file:///C:/Users/gcarm/AndroidStudioProjects/CoreAndroidNative/bundle/networking/src/main/java/br/com/wgc/bundle/networking/NetworkingInitializer.kt): Ponto de entrada unificado para clientes HTTP, interceptors, tratamento de erros de rede e monitoramento de conectividade.

### 2. Dependências Subjacentes
- **`:infra:core-network`**: Clientes HTTP configurados (Retrofit/OkHttp), serialização JSON e tratamento de resiliência.
- **`:infra:core-device`**: Checagem de estado de conexão e tipo de rede (Wi-Fi, Celular, etc.).

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor (ex: `:app`):

```kotlin
dependencies {
    implementation("br.com.wgc:bundle-networking:0.0.x")
}
```

---

## 💡 Exemplo de Uso

```kotlin
@Inject
lateinit var networkingFacade: NetworkingFacade

fun checkStatus() {
    val status = networkingFacade.getStatus()
    println(status)
}
```
