# Submódulo `:infra:core:analytics` 📊

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--analytics-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:infra:core:analytics`** é o submódulo dedicado ao gerenciamento de LGPD, consentimento de privacidade e rastreamento analítico avançado de interações do usuário.

---

## 🛠️ O que contém este submódulo?

### 1. Gestão de Consentimento LGPD (`br.com.wgc.core.analytics.consent`)
- [`LgpdConsentManager`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/infra/core-analytics/src/main/java/br/com/wgc/core/analytics/consent/LgpdConsentManager.kt):
  - Gerencia o aceite de termos de privacidade e preferências de rastreamento (Analytics, Marketing, Funcional).
  - Persiste as escolhas do usuário de forma segura e reativa (`StateFlow`).
  - Bloqueia automaticamente o disparo de eventos analíticos caso o usuário não tenha dado o consentimento explícito.

---

## 📥 Como importar

```kotlin
dependencies {
    implementation("br.com.wgc:core-analytics:0.0.x")
}
```

---

## 🚀 Quando deve ser usado?

- Em aplicações que necessitam cumprir regulamentações de privacidade (LGPD/GDPR) antes de inicializar SDKs de rastreamento (Firebase Analytics, AppsFlyer, etc.).
