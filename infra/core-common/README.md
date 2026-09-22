# Submódulo `:infra:core-common` 📦

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--common-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-NÃO-success.svg)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:infra:core-common`** é o módulo de infraestrutura base e lógica pura da biblioteca. Ele foi estritamente concebido para ser **totalmente agnóstico de interface de usuário (Zero Jetpack Compose)**, tornando-o perfeito para ser importado em camadas de regras de negócio (`:domain`), camadas de dados (`:data`) e de apresentação (`:presentation`).

---

## 🛠️ O que contém este submódulo?

### 1. Concorrência & Resiliência (`br.com.wgc.core.coroutines`)
- [`CoroutineDispatchers`](src/main/java/br/com/wgc/core/coroutines/CoroutineDispatchers.kt): Contrato de abstração dos despachantes (`main`, `io`, `default`, `unconfined`). Facilita testes unitários determinísticos substituindo-os por `StandardTestDispatcher`.
- [`DefaultCoroutineDispatchers`](src/main/java/br/com/wgc/core/coroutines/DefaultCoroutineDispatchers.kt): Implementação padrão delegando para `Dispatchers`.
- [`retryWithBackoff`](src/main/java/br/com/wgc/core/coroutines/CoroutineExtensions.kt): Função utilitária com política configurável de retentativas e recuo exponencial (exponential backoff) para chamadas assíncronas sujeitas a falhas transitórias.

### 2. Estado Reativo de UI (`br.com.wgc.core.result`)
- [`ResultState<T>`](src/main/java/br/com/wgc/core/result/ResultState.kt): Sealed class para modelar estados assíncronos (`Loading`, `Success`, `Error`).
- `Flow<T>.asResultState()`: Extensão que converte qualquer `Flow<T>` em `Flow<ResultState<T>>`, emitindo `Loading` imediatamente e capturando exceções em `Error`.

### 3. Feature Flags & Rollout Determinístico (`br.com.wgc.core.featureflag`)
- [`FeatureToggleManager`](src/main/java/br/com/wgc/core/featureflag/FeatureToggleManager.kt):
  - Ativação/desativação dinâmica de toggles em tempo de execução.
  - Rollout percentual determinístico (`isRolloutEnabled(toggle, userId, rolloutPercentage)`) baseado em hashing consistente CRC32, garantindo que o mesmo usuário sempre caia no mesmo bucket para um dado percentual.

### 4. Observabilidade, Logging & Proteção de PII (`br.com.wgc.core.logging`)
- [`CoreLogger`](src/main/java/br/com/wgc/core/logging/CoreLogger.kt) e [`DefaultCoreLogger`](src/main/java/br/com/wgc/core/logging/DefaultCoreLogger.kt):
  - Filtragem configurável por severidade (`VERBOSE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `NONE`).
  - **Sanitização Automática de PII:** Mascara automaticamente CPFs (`***.***.***-**`), cartões de crédito (`****-****-****-****`) e Bearer tokens (`Bearer [MASKED_TOKEN]`).
  - Fallback automático para `println` em testes unitários JVM puros.

### 5. Validadores Corporativos (`br.com.wgc.core.validators`)
- [`Validators.kt`](src/main/java/br/com/wgc/core/validators/Validators.kt):
  - `String?.isValidCpf()`: Validação matemática completa com cálculo dos dois dígitos verificadores (Módulo 11) e rejeição de sequências repetidas.
  - `String?.isValidCnpj()`: Validação ponderada oficial dos 14 dígitos do CNPJ.
  - `String?.isValidEmail()`: Validação de conformidade RFC para endereços de e-mail.
  - `String?.isValidPhone()`: Validação para números fixos (10 dígitos) e celulares (11 dígitos com 9 inicial e DDD).
  - `String?.isValidCep()`: Validação de CEP contendo 8 dígitos numéricos.

### 6. Formatadores Utilitários (`br.com.wgc.core.formatters`)
- [`Formatters.kt`](src/main/java/br/com/wgc/core/formatters/Formatters.kt):
  - `Double.toCurrencyFormatted()` e `BigDecimal.toCurrencyFormatted()`: Formatação monetária (padrão `pt-BR` R$).
  - `Long.toFormattedDate()`: Conversão de timestamp Epoch para texto formatado.
  - `formatCpf()`, `formatCnpj()`, `formatCep()`, `formatPhone()`.
  - `String.unmask()`: Remove caracteres não numéricos.

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor (ex: `:domain` ou `:data`):

```kotlin
dependencies {
    implementation("br.com.wgc:core-common:1.2.0")
}
```

---

## 💡 Exemplos de Uso

```kotlin
// Feature Flag com Rollout Percentual
val isNewCheckoutEnabled = featureToggleManager.isRolloutEnabled(
    toggle = "new_checkout_flow",
    userId = "user_98765",
    rolloutPercentage = 30 // 30% da base recebe a feature
)

// Validação e formatação
val isCpfValid = "123.456.789-01".isValidCpf()
val precoTexto = 149.90.toCurrencyFormatted() // "R$ 149,90"
```
