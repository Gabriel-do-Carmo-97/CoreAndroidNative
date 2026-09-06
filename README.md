# CoreAndroidNative 🚀

[![Android CI](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative/actions/workflows/android.yaml/badge.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative/actions)
![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

**CoreAndroidNative** é uma biblioteca/módulo base nativa para Android desenvolvida em Kotlin, projetada para fornecer uma fundação sólida, moderna e segura para aplicativos Android.

Ela encapsula utilitários essenciais do ecossistema Android, como persistência com **DataStore** e **SharedPreferences Criptografado**, monitoramento de **Conectividade de Rede**, construtores de **Notificações**, manipuladores de **ResultState** e **Extensions para Jetpack Compose**.

---

## 📦 Instalação

Adicione o repositório do **GitHub Packages** no seu arquivo `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/CoreAndroidNative")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

No seu módulo `build.gradle.kts` (app ou feature):

```kotlin
dependencies {
    implementation("br.com.wgc:core-android-native:0.0.x")
}
```

---

## ✨ Recursos Principais

### 1. 💾 DataStore Preferences (`DataStorePreferencesCore`)
Wrapper reativo baseado em `Preferences DataStore` com tratamento de exceções I/O e suporte nativo a `Set<String>` e tipos arbitrários.

```kotlin
@Inject
lateinit var dataStore: DataStorePreferencesCore

// Salvar dados
lifecycleScope.launch {
    dataStore.saveString("user_name", "Gabriel")
    dataStore.saveStringSet("roles", setOf("ADMIN", "USER"))
}

// Ler como Flow
dataStore.getStringFlow("user_name").collect { name ->
    println("Usuário: $name")
}
```

---

### 🔒 2. SharedPreferences Criptografado (`EncryptedSharedPreferencesCore`)
Armazenamento seguro protegido por **Android Keystore (AES-256 GCM)** para guardar tokens JWT e segredos da aplicação.

```kotlin
@Inject
lateinit var encryptedStorage: EncryptedSharedPreferencesCore

// Salvar token com segurança
encryptedStorage.saveString("auth_token", "eyJhbGciOiJIUzI1NiI...")

// Recuperar
val token = encryptedStorage.getString("auth_token")
```

---

### 🌐 3. Monitor de Conectividade (`NetworkMonitor`)
Monitora o estado da conexão de internet em tempo real via `StateFlow`.

```kotlin
@Inject
lateinit var networkMonitor: NetworkMonitor

// Observar estado da rede no Compose ou ViewModel
networkMonitor.isConnected.collect { isConnected ->
    if (isConnected) {
        // Conectado à internet
    }
}
```

---

### ⚡ 4. Estado de UI e Resultado (`ResultState`)
Wrapper genérico para tratamento padronizado de estados de chamadas assíncronas no Clean Architecture / MVI.

```kotlin
sealed interface UiState {
    data object Loading : UiState
    data class Success(val data: User) : UiState
    data class Error(val message: String) : UiState
}

// Utilização das extensões map, isSuccess, getOrNull:
val result: ResultState<User> = ResultState.Success(user)
if (result.isSuccess()) {
    val data = result.getOrNull()
}
```

---

### 🔔 5. Construtor de Notificações e Canais (`CreateNotification`, `CreateChannelNotification`)
Padrão Builder simplificado para criação de canais e notificações customizadas no Android.

```kotlin
val channelCreator = CreateChannelNotification(context)
channelCreator.with(channelId = "alerts", name = "Alertas")
    .withImportance(NotificationManager.IMPORTANCE_HIGH)
    .create()

val notification = CreateNotification(context)
notification.with(channelId = "alerts", textTitle = "Novo Alerta")
    .withContent(R.drawable.ic_notification, "Sua tarefa foi concluída!")
    .show(notificationId = 1)
```

---

### 🎯 6. Extension Functions (`Context`, `Modifier`)
- `Context.hasPermission(permission)`: Verifica permissão do sistema.
- `Context.showToast(message)`: Exibe toast rapidamente.
- `Modifier.debouncedClick()`: Previne cliques múltiplos consecutivos em componentes do Compose.

---

## 💉 Injeção de Dependência com Hilt

O módulo `:core` inclui o `CoreModule` pronto para ser instalado no Hilt `@HiltAndroidApp`:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: DataStorePreferencesCore

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    // ...
}
```

---

## 🧪 Testes

Para rodar a suíte de testes unitários do módulo `:core`:

```bash
./gradlew :core:testDebugUnitTest
```

---

## 📄 Licença

Distribuído sob a licença **Apache 2.0**. Veja `LICENSE` para mais detalhes.
