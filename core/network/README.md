# 🌐 Core Network Module (`:core:network`)

Módulo corporativo de rede resiliente para Android, fornecendo interceptação transparente de autenticação OAuth2 / JWT, sincronização de renovação de tokens via `Mutex`, proteção contra ataques Man-In-The-Middle (MITM) via **SSL Certificate Pinning**, e padronização unificada de respostas e exceções HTTP.

---

## 📦 Instalação

Adicione a dependência ao seu `build.gradle.kts`:

```kotlin
dependencies {
    // Via submódulo granular
    implementation("br.com.wgc:core-network:<versao>")

    // Ou via guarda-chuva :core
    implementation("br.com.wgc:core:<versao>")
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
        // Chamada à API de renovação de refresh token
        val newTokens = authApi.refresh(getRefreshToken()) ?: return false
        sessionManager.saveTokens(newTokens.access, newTokens.refresh)
        return true
    }

    override fun onSessionExpired() {
        // Notifique o app ou redirecione para a tela de Login
        sessionManager.clearSession()
    }
}

// 2. Configure o OkHttpClient com AuthInterceptor e TokenAuthenticator
val tokenProvider = AppTokenProvider(sessionManager)

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor(tokenProvider))
    .authenticator(TokenAuthenticator(tokenProvider))
    .build()
```

---

### 2. Configurando SSL Certificate Pinning

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

### 3. Tratamento de Respostas com `ApiResult`

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
./gradlew :core:network:testDebugUnitTest
```
