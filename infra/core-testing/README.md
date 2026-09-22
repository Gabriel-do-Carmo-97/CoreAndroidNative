# Submódulo `:infra:core-testing` 🧪

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--testing-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:infra:core-testing`** é o módulo de utilitários e infraestrutura de testes unitários e de integração do ecossistema. Ele provê regras JUnit, fakes de segurança e helpers para MockWebServer, eliminando código repetitivo (boilerplate) e garantindo testes determinísticos nos módulos consumidores.

---

## 🛠️ O que contém este submódulo?

### 1. Regra de Corrotinas para JUnit (`br.com.wgc.core.testing.rules`)
- [`MainDispatcherRule`](src/main/java/br/com/wgc/core/testing/rules/MainDispatcherRule.kt):
  - JUnit `TestWatcher` que substitui `Dispatchers.Main` pelo `StandardTestDispatcher` ou `UnconfinedTestDispatcher`.
  - Restaura o despachante padrão automaticamente após a conclusão de cada teste.

### 2. Provedor Falso de Tokens de Autenticação (`br.com.wgc.core.testing.fakes`)
- [`FakeTokenProvider`](src/main/java/br/com/wgc/core/testing/fakes/FakeTokenProvider.kt):
  - Implementação em memória do contrato `TokenProvider` do `:infra:core-network`.
  - Permite configurar previamente `accessToken`, `refreshToken`, forçar expiração de sessão e simular falhas em `refreshTokens()` para validar fluxos de interceptação e autenticação sem rede.

### 3. Utilitários para MockWebServer (`br.com.wgc.core.testing.network`)
- [`MockWebServerHelper`](src/main/java/br/com/wgc/core/testing/network/MockWebServerHelper.kt):
  - Helper fluente para enfileirar respostas HTTP simuladas com corpo JSON, código de status e atrasos simulados (`throttle`).
  - Suporte a respostas estáticas ou carregadas diretamente de arquivos de recurso em `test/resources/`.

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor (ex: `:feature`, `:data` ou `:presentation`):

```kotlin
dependencies {
    testImplementation("br.com.wgc:core-testing:1.2.0")
}
```

---

## 💡 Exemplos de Uso

### 1. Testando ViewModels com `MainDispatcherRule`
```kotlin
class MyFeatureViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `deve emitir estado de sucesso apos carregamento`() = runTest {
        val viewModel = MyFeatureViewModel()
        viewModel.carregarDados()
        
        // Assertions determinísticas com corrotinas
        assertEquals(ViewState.Success, viewModel.state.value)
    }
}
```

### 2. Testando Autenticação e Retry com `FakeTokenProvider`
```kotlin
@Test
fun `deve anexar token de autorizacao valido nas chamadas`() = runTest {
    val fakeTokenProvider = FakeTokenProvider(
        initialAccessToken = "valid_token_xyz",
        initialRefreshToken = "refresh_token_abc"
    )
    
    val interceptor = AuthInterceptor(fakeTokenProvider)
    // Validar anexo do header "Authorization: Bearer valid_token_xyz"
}
```

### 3. Simulando Respostas de Rede com `MockWebServerHelper`
```kotlin
val server = MockWebServer()
server.start()

MockWebServerHelper.enqueueJsonResponse(
    server = server,
    responseCode = 200,
    jsonBody = """{"id": "123", "name": "Gabriel"}"""
)
```
