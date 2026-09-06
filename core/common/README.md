# Submódulo `:core:common` 📦

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--common-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-NÃO-success.svg)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:core:common`** é o módulo de infraestrutura base e lógica pura da biblioteca. Ele foi estritamente concebido para ser **totalmente agnóstico de interface de usuário (Zero Jetpack Compose)**, tornando-o perfeito para ser importado em camadas de regras de negócio (`:domain`), camadas de dados (`:data`) e de apresentação (`:presentation`).

---

## 🛠️ O que contém este submódulo?

### 1. Concorrência & Resiliência (`br.com.wgc.core.coroutines`)
- [`CoroutineDispatchers`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/src/main/java/br/com/wgc/core/coroutines/CoroutineDispatchers.kt): Contrato de abstração dos despachantes (`main`, `io`, `default`, `unconfined`). Facilita testes unitários determinísticos substituindo-os por `StandardTestDispatcher`.
- [`DefaultCoroutineDispatchers`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/src/main/java/br/com/wgc/core/coroutines/DefaultCoroutineDispatchers.kt): Implementação padrão delegando para `Dispatchers`.
- [`retryWithBackoff`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/src/main/java/br/com/wgc/core/coroutines/CoroutineExtensions.kt): Função utilitária com política configurável de retentativas e recuo exponencial (exponential backoff) para chamadas assíncronas sujeitas a falhas transitórias.

### 2. Estado Reativo de UI (`br.com.wgc.core.result`)
- [`ResultState<T>`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/src/main/java/br/com/wgc/core/result/ResultState.kt): Sealed class para modelar estados assíncronos (`Loading`, `Success`, `Error`).
- `Flow<T>.asResultState()`: Extensão que converte qualquer `Flow<T>` em `Flow<ResultState<T>>`, emitindo `Loading` imediatamente e capturando exceções em `Error`.

### 3. Observabilidade, Analytics & Proteção de PII (`br.com.wgc.core.logging` / `br.com.wgc.core.analytics`)
- [`CoreLogger`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/src/main/java/br/com/wgc/core/logging/CoreLogger.kt) e [`DefaultCoreLogger`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/src/main/java/br/com/wgc/core/logging/DefaultCoreLogger.kt):
  - Filtragem configurável por severidade ([`LogLevel`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/src/main/java/br/com/wgc/core/logging/CoreLogger.kt)): `VERBOSE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `NONE`.
  - **Sanitização Automática de PII:** Mascara automaticamente CPFs (`***.***.***-**`), cartões de crédito (`****-****-****-****`) e Bearer tokens (`Bearer [MASKED_TOKEN]`).
  - Fallback automático para `println` em testes unitários JVM puros.
- [`AnalyticsTracker`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/src/main/java/br/com/wgc/core/analytics/AnalyticsTracker.kt) e [`CompositeAnalyticsTracker`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/src/main/java/br/com/wgc/core/analytics/CompositeAnalyticsTracker.kt):
  - Rastreamento unificado de eventos de domínio, propriedades de usuário e ID de autenticação.
  - Fan-out transparente para múltiplos provedores (ex: Firebase, Mixpanel, AppsFlyer).
  - Sanitização automática preventiva de PII em parâmetros antes do envio.

### 4. Validadores Corporativos (`br.com.wgc.core.validators`)
- [`Validators.kt`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/src/main/java/br/com/wgc/core/validators/Validators.kt):
  - `String?.isValidCpf()`: Validação matemática completa com cálculo dos dois dígitos verificadores (Módulo 11) e rejeição de sequências repetidas.
  - `String?.isValidCnpj()`: Validação ponderada oficial dos 14 dígitos do CNPJ.
  - `String?.isValidEmail()`: Validação de conformidade RFC para endereços de e-mail.
  - `String?.isValidPhone()`: Validação para números fixos (10 dígitos) e celulares (11 dígitos com 9 inicial e DDD).
  - `String?.isValidCep()`: Validação de CEP contendo 8 dígitos numéricos.

### 5. Formatadores Utilitários (`br.com.wgc.core.formatters`)
- [`Formatters.kt`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/common/src/main/java/br/com/wgc/core/formatters/Formatters.kt):
  - `Double.toCurrencyFormatted()` e `BigDecimal.toCurrencyFormatted()`: Formatação monetária (padrão `pt-BR` R$).
  - `Long.toFormattedDate()`: Conversão de timestamp Epoch para texto formatado.
  - `formatCpf()`, `formatCnpj()`, `formatCep()`, `formatPhone()`.
  - `String.unmask()`: Remove caracteres não numéricos.

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor (ex: `:domain` ou `:data`):

```kotlin
dependencies {
    implementation("br.com.wgc:core-common:0.0.x")
}
```

---

## 💡 Exemplo de Uso

```kotlin
import br.com.wgc.core.validators.isValidCpf
import br.com.wgc.core.formatters.toCurrencyFormatted
import br.com.wgc.core.coroutines.retryWithBackoff

// Validação simples
val isCpfValid = "123.456.789-01".isValidCpf()

// Formatação monetária
val precoTexto = 149.90.toCurrencyFormatted() // "R$ 149,90"

// Retentativa resiliente
val dados = retryWithBackoff(times = 3, initialDelayMs = 500L) {
    servicoRemoto.obterDados()
}
```
