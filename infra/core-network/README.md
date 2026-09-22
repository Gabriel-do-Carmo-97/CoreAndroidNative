# Submódulo `:infra:core-network` 🌐

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--network-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

Módulo corporativo de rede resiliente para Android, fornecendo interceptação transparente de autenticação OAuth2 / JWT, sincronização de renovação de tokens via `Mutex`, proteção contra ataques Man-In-The-Middle (MITM) via **SSL Certificate Pinning**, comunicação em tempo real via **WebSocket reativo** e streaming **Server-Sent Events (SSE)**.

---

## 📦 Instalação

Adicione a dependência ao seu `build.gradle.kts`:

```kotlin
dependencies {
    // Via submódulo granular
    implementation("br.com.wgc:core-network:1.2.0")

    // Ou via bundle de networking
    implementation("br.com.wgc:bundle-networking:1.2.0")
}
```

---

## 🚀 Recursos Principais

| Componente | Descrição |
|---|---|
| `TokenProvider` | Contrato de fornecimento de `accessToken`, `refreshToken` e callback de expiração de sessão. |
| `AuthInterceptor` | Interceptor OkHttp que anexa o cabeçalho `Authorization: Bearer <token>` automaticamente. |
| `TokenAuthenticator` | Renovador automático thread-safe de tokens em respostas HTTP 401 via `Mutex` com retentativa única. |
| `SslPinningHelper` | Utilitário seguro para registrar Certificate Pinning SHA-256 no `OkHttpClient`. |
| `CoreWebSocketClient` | Cliente WebSocket reativo baseado em Corrotinas com reconexão automática e emissão via `SharedFlow<WebSocketEvent>`. |
| `ServerSentEventClient` | Cliente SSE para streaming unidirecional de eventos do servidor via Kotlin `Flow`. |
| `ApiResult<T>` | Sealed class com estados tipados (`Success`, `HttpError`, `NetworkError`, `UnknownError`). |
| `NetworkException` | Exceções estruturadas para erros HTTP e falhas de conexão de rede. |

---

## 🛠️ Exemplos de Uso

### 1. Configurando Autenticação e Renovação Automática com OkHttp

```kotlin
// 1. Implemente o contrato TokenProvider
class AppTokenProvider(private val sessionManager: SessionManager) : TokenProvider {
    override suspend fun getAccessToken(): String? = sessionManager.getToken()
    override suspend fun getRefreshToken(): String? = sessionManager.getRefreshToken()
    
    override suspend fun refreshTokens(): Boolean {
        val newTokens = authApi.refresh(getRefreshToken()) ?: return false
        sessionManager.saveTokens(newTokens.access, newTokens.refresh)
        return true
    }

    override fun onSessionExpired() {
        sessionManager.clearSession()
    }
}

// 2. Configure o OkHttpClient com AuthInterceptor e TokenAuthenticator
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor(tokenProvider))
    .authenticator(TokenAuthenticator(tokenProvider))
    .build()
```

---

### 2. Comunicação em Tempo Real via WebSocket

```kotlin
val webSocketClient = CoreWebSocketClient(okHttpClient)

// Observar eventos de forma reativa
lifecycleScope.launch {
    webSocketClient.events.collect { event ->
        when (event) {
            is WebSocketEvent.OnOpen -> println("Conectado ao servidor!")
            is WebSocketEvent.OnMessage -> println("Mensagem recebida: ${event.text}")
            is WebSocketEvent.OnFailure -> println("Falha na conexão: ${event.throwable.message}")
            is WebSocketEvent.OnClosed -> println("Conexão encerrada")
            else -> Unit
        }
    }
}

// Conectar e enviar mensagens
webSocketClient.connect("wss://echo.websocket.org")
webSocketClient.send("Olá servidor!")
```

---

### 3. Configurando SSL Certificate Pinning

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .apply {
        SslPinningHelper.configurePinning(
            builder = this,
            hostname = "api.minhaempresa.com.br",
            "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=" // Backup pin
        )
    }
    .build()
```

---

### 4. Tratamento de Respostas com `ApiResult`

```kotlin
suspend fun getUserProfile(): ApiResult<UserProfile> {
    return safeApiCall {
        apiService.fetchUserProfile()
    }
}

// No ViewModel:
when (val result = repository.getUserProfile()) {
    is ApiResult.Success -> showProfile(result.data)
    is ApiResult.HttpError -> showError("Erro ${result.code}: ${result.message}")
    is ApiResult.NetworkError -> showOfflineBanner()
    is ApiResult.UnknownError -> showGenericError()
}
```

---

## 🧪 Testes Unitários

Execute os testes com cobertura do módulo:

```bash
./gradlew :infra:core-network:testDebugUnitTest
```
