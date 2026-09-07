# Submódulo Guarda-Chuva `:infra:core` ☂️

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--android--native-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:infra:core`** é o módulo agregador (guarda-chuva) da arquitetura. Sua principal função é exportar (`api(...)`) todos os submódulos de infraestrutura (`:infra:core-common`, `:infra:core-storage`, `:infra:core-device`, `:infra:core-network`, `:infra:core-ui`, `:infra:core-database`, `:infra:core-location`, `:infra:core-camera`, `:infra:core-analytics`) em um único ponto de dependência unificado.

---

## 🛠️ O que contém este submódulo?

- **[`CoreModule`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/infra/core/src/main/java/br/com/wgc/core/di/CoreModule.kt)**: Módulo Dagger/Hilt global que fornece instâncias singleton compartilhadas de utilitários, gerenciadores de sessão e provedores de segurança para toda a aplicação.
- Agregação centralizada de dependências para consumo simplificado em aplicações consumidoras.

---

## 📥 Como importar

No `build.gradle.kts` do módulo `:app` ou consumidor final:

```kotlin
dependencies {
    // Importa todos os submódulos de infraestrutura de uma só vez
    implementation("br.com.wgc:core-android-native:0.0.x")
}
```

---

## 🚀 Quando deve ser usado?

- Quando o aplicativo consumidor deseja ter acesso imediato a toda a suíte de ferramentas de infraestrutura sem precisar declarar cada submódulo (`:infra:core-common`, `:infra:core-storage`, etc.) individualmente.
