# Submódulo `:core:storage` 💾

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--storage-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-NÃO-success.svg)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:core:storage`** é o módulo de persistência local, segurança criptográfica e gerenciamento de arquivos da biblioteca. Ele foi projetado para desacoplar e abstrair as APIs de armazenamento do Android sob interfaces testáveis e fornecer **logout atômico** para múltiplos apps corporativos.

---

## 🛠️ O que contém este submódulo?

### 1. DataStore Preferences Reativo (`br.com.wgc.core.dataStorePreferences`)
- [`KeyValueDataStore`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/dataStorePreferences/KeyValueDataStore.kt): Interface assíncrona baseada em corrotinas e Kotlin `Flow`.
- [`DataStorePreferencesCore`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/dataStorePreferences/DataStorePreferencesCore.kt): Implementação robusta com tratamento de exceções de I/O em corrupção de disco e suporte polimórfico (`saveAny`, `getStringFlow`, `getIntFlow`, etc.).

### 2. SharedPreferences Síncrono (`br.com.wgc.core.sharedPreferences`)
- [`KeyValueStorage`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/sharedPreferences/KeyValueStorage.kt): Interface síncrona padronizada para leitura e gravação imediata de tipos primitivos.
- [`SharedPreferencesCore`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/sharedPreferences/SharedPreferencesCore.kt): Implementação padrão utilizando o SharedPreferences nativo do Android.

### 3. Criptografia Segura com Android KeyStore (`br.com.wgc.core.security`)
- [`EncryptedSharedPreferencesCore`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/security/EncryptedSharedPreferencesCore.kt): Implementação de `KeyValueStorage` com criptografia de ponta a ponta (chaves mestras gerenciadas pelo **Android KeyStore** com AES-256 GCM e esquema de chaves SIV), ideal para tokens JWT, credenciais e dados sensíveis.

### 4. Autenticação Biométrica com AndroidX (`br.com.wgc.core.security.biometric`)
- [`BiometricAuthHelper`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/security/biometric/BiometricAuthHelper.kt) e [`DefaultBiometricAuthHelper`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/security/biometric/BiometricAuthHelper.kt):
  - Verificação de disponibilidade de hardware e credenciais via `canAuthenticate()`.
  - Disparo de prompt nativo via `authenticate()` com suporte opcional a `CryptoObject` (assinaturas digitais respaldadas por hardware).
  - Resultados tipados (`BiometricAuthResult.Success`, `Failed`, `Cancelled`, `Error`).

### 5. Gerenciamento de Arquivos e Cache (`br.com.wgc.core.file`)
- [`FileManager`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/file/FileManager.kt) e [`DefaultFileManager`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/file/FileManager.kt):
  - `createTempFile()`: Criação segura de arquivos temporários em disco.
  - `getCacheSizeBytes()`: Cálculo recursivo do volume em bytes ocupado pelo cache interno e externo do aplicativo.
  - `clearCache()`: Limpeza recursiva dos diretórios de cache.
  - `deleteFile()`: Exclusão segura de arquivos ou pastas recursivamente.

### 6. Logout Atômico Unificado (`br.com.wgc.core.session`)
- [`SessionManager`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/session/SessionManager.kt) e [`DefaultSessionManager`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/session/SessionManager.kt):
  - `clearSession(clearCache: Boolean)`: Orquestra simultaneamente no despachante I/O a limpeza de Preferences DataStore, SharedPreferences padrão, SharedPreferences criptografados e diretórios de cache temporário.

### 7. Exceções Tipadas (`br.com.wgc.core.exceptions`)
- [`StorageException`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/storage/src/main/java/br/com/wgc/core/exceptions/StorageException.kt): Hierarquia de erros (`ReadException`, `WriteException`, `ClearException`, `UnsupportedTypeException`).

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor (ex: `:data`):

```kotlin
dependencies {
    implementation("br.com.wgc:core-storage:0.0.x")
}
```

---

## 💡 Exemplo de Uso

```kotlin
// Injetado via Hilt ou instanciado manualmente
val sessionManager: SessionManager = ...

// Realizar logout do usuário (limpa todas as preferências, tokens e cache)
lifecycleScope.launch {
    sessionManager.clearSession(clearCache = true)
}
```
