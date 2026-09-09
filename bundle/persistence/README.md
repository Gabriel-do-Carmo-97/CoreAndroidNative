# Bundle `:bundle:persistence` 💾

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:bundle--persistence-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:bundle:persistence`** é um bundle de alto nível que agrupa e integra os módulos de infraestrutura voltados à persistência local de dados, armazenamento seguro e bancos de dados relacionais: `:infra:core-common`, `:infra:core-storage` e `:infra:core-database`.

---

## 🛠️ O que contém este bundle?

### 1. Inicializador e Fachada Hilt (`br.com.wgc.bundle.persistence`)
- [`PersistenceInitializer`](file:///C:/Users/gcarm/AndroidStudioProjects/CoreAndroidNative/bundle/persistence/src/main/java/br/com/wgc/bundle/persistence/PersistenceInitializer.kt): Módulo Dagger/Hilt (@InstallIn(SingletonComponent::class)) que fornece instâncias singleton da fachada de persistência.
- [`PersistenceFacade`](file:///C:/Users/gcarm/AndroidStudioProjects/CoreAndroidNative/bundle/persistence/src/main/java/br/com/wgc/bundle/persistence/PersistenceInitializer.kt): Ponto de entrada unificado para gerenciamento de preferências, banco de dados Room e armazenamento criptografado.

### 2. Dependências Subjacentes
- **`:infra:core-storage`**: DataStore, SharedPreferences criptografados com Android KeyStore, biometria e gerenciamento de arquivos.
- **`:infra:core-database`**: Configuração e DAOs do Room Database.

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor (ex: `:app`):

```kotlin
dependencies {
    implementation("br.com.wgc:bundle-persistence:0.0.x")
}
```

---

## 💡 Exemplo de Uso

```kotlin
@Inject
lateinit var persistenceFacade: PersistenceFacade

fun checkStatus() {
    val status = persistenceFacade.getStatus()
    println(status)
}
```
