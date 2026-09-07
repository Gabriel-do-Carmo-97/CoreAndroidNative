# Módulo de Convenções de Build `:build-logic` ⚙️

[![Gradle](https://img.shields.io/badge/Gradle-Convention%20Plugins-blue.svg)]()
[![Kotlin DSL](https://img.shields.io/badge/Kotlin%20DSL-100%25-purple.svg)]()

O **`:build-logic`** é um plugin composto (composite build) responsável por centralizar e padronizar toda a lógica de construção, compilação, versionamento e publicação (`convention plugins`) de todos os submódulos do projeto.

---

## 🛠️ O que contém este módulo?

### Convention Plugins (Kotlin DSL)
1. **[`AndroidLibraryConventionPlugin`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/build-logic/src/main/kotlin/AndroidLibraryConventionPlugin.kt)**: Configuração padrão para bibliotecas Android (`com.android.library`), definindo `compileSdk = 37`, `minSdk = 29`, Java 11 bytecode, testes unitários com recursos Android habilitados, Lint e Detekt.
2. **[`AndroidLibraryComposeConventionPlugin`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/build-logic/src/main/kotlin/AndroidLibraryComposeConventionPlugin.kt)**: Ativa o suporte ao Jetpack Compose (`compose = true`) e aplica o compilador do Compose.
3. **[`AndroidHiltConventionPlugin`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/build-logic/src/main/kotlin/AndroidHiltConventionPlugin.kt)**: Configura automaticamente o plugin e as dependências do Dagger Hilt com KSP (`com.google.dagger.hilt.android` e `com.google.devtools.ksp`).
4. **[`AndroidPublishConventionPlugin`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/build-logic/src/main/kotlin/AndroidPublishConventionPlugin.kt)**: Configura o plugin `maven-publish` para gerar e publicar artefatos AAR em repositórios Maven (GitHub Packages) com versionamento dinâmico.
5. **[`AndroidDokkaConventionPlugin`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/build-logic/src/main/kotlin/AndroidDokkaConventionPlugin.kt)**: Configura a geração de documentação em formato de API (Javadoc/HTML) via Dokka.

---

## 🚀 Quando deve ser usado?

- Automático em todos os módulos que aplicam seus IDs (ex: `id("wgc.android.library")`, `id("wgc.android.hilt")`). Garante consistência de versões e regras de compilação em todo o repositório.
