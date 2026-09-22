# Submódulo `:infra:core-storage` 💾

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--storage-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-NÃO-success.svg)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:infra:core-storage`** é o módulo de persistência local, segurança criptográfica, sincronização offline resiliente e gerenciamento de cache de alta performance. Ele foi projetado para desacoplar as APIs de armazenamento do Android sob interfaces testáveis e fornecer **logout atômico** para múltiplos apps corporativos.

---

## 🛠️ O que contém este submódulo?

### 1. DataStore Preferences Reativo (`br.com.wgc.core.dataStorePreferences`)
- [`KeyValueDataStore`](src/main/java/br/com/wgc/core/dataStorePreferences/KeyValueDataStore.kt): Interface assíncrona baseada em corrotinas e Kotlin `Flow`.
- [`DataStorePreferencesCore`](src/main/java/br/com/wgc/core/dataStorePreferences/DataStorePreferencesCore.kt): Implementação robusta com tratamento de exceções de I/O em corrupção de disco e suporte polimórfico (`saveAny`, `getStringFlow`, `getIntFlow`, etc.).

### 2. SharedPreferences Síncrono (`br.com.wgc.core.sharedPreferences`)
- [`KeyValueStorage`](src/main/java/br/com/wgc/core/sharedPreferences/KeyValueStorage.kt): Interface síncrona padronizada para leitura e gravação imediata de tipos primitivos.
- [`SharedPreferencesCore`](src/main/java/br/com/wgc/core/sharedPreferences/SharedPreferencesCore.kt): Implementação padrão utilizando o SharedPreferences nativo do Android.

### 3. Criptografia Segura com Android KeyStore (`br.com.wgc.core.security`)
- [`EncryptedSharedPreferencesCore`](src/main/java/br/com/wgc/core/security/EncryptedSharedPreferencesCore.kt): Implementação de `KeyValueStorage` com criptografia de ponta a ponta (chaves mestras gerenciadas pelo **Android KeyStore** com AES-256 GCM e esquema de chaves SIV), ideal para tokens JWT, credenciais e dados sensíveis.

### 4. Cache em Dois Níveis com TTL (`br.com.wgc.core.storage.cache`)
- [`TwoLevelCache<T>`](src/main/java/br/com/wgc/core/storage/cache/TwoLevelCache.kt):
  - Camada L1 rápida em memória RAM (`LruCache`) + Camada L2 persistente em disco (JSON).
  - Suporte a tempo de vida configurável ([`CacheEntry`](src/main/java/br/com/wgc/core/storage/cache/CacheEntry.kt)) com expiração automática por TTL e auto-eviction.

### 5. Motor de Sincronização Offline & Outbox Pattern (`br.com.wgc.core.sync`)
- [`OutboxQueue`](src/main/java/br/com/wgc/core/sync/OutboxQueue.kt): Fila persistente em disco thread-safe com `StateFlow` reativo para enfileiramento de requisições durante falta de conectividade.
- [`SyncWorker`](src/main/java/br/com/wgc/core/sync/SyncWorker.kt): `CoroutineWorker` do WorkManager disparado automaticamente quando há conectividade de rede (`NetworkType.CONNECTED`).
- [`SyncManager`](src/main/java/br/com/wgc/core/sync/SyncManager.kt): Ponto central para agendar e orquestrar sincronizações em background com constraints de bateria e rede.

### 6. Autenticação Biométrica com AndroidX (`br.com.wgc.core.security.biometric`)
- [`BiometricAuthHelper`](src/main/java/br/com/wgc/core/security/biometric/BiometricAuthHelper.kt) e [`DefaultBiometricAuthHelper`](src/main/java/br/com/wgc/core/security/biometric/BiometricAuthHelper.kt):
  - Verificação de disponibilidade de hardware e credenciais via `canAuthenticate()`.
  - Disparo de prompt nativo via `authenticate()` com suporte opcional a `CryptoObject`.
  - Resultados tipados (`BiometricAuthResult.Success`, `Failed`, `Cancelled`, `Error`).

### 7. Gerenciamento de Arquivos e Diretórios (`br.com.wgc.core.file`)
- [`FileManager`](src/main/java/br/com/wgc/core/file/FileManager.kt) e [`DefaultFileManager`](src/main/java/br/com/wgc/core/file/FileManager.kt):
  - `createTempFile()`: Criação segura de arquivos temporários em disco.
  - `getCacheSizeBytes()`: Cálculo recursivo do volume em bytes ocupado pelo cache.
  - `clearCache()`: Limpeza recursiva dos diretórios de cache.
  - `deleteFile()`: Exclusão segura de arquivos ou pastas recursivamente.

### 8. Logout Atômico Unificado (`br.com.wgc.core.session`)
- [`SessionManager`](src/main/java/br/com/wgc/core/session/SessionManager.kt) e [`DefaultSessionManager`](src/main/java/br/com/wgc/core/session/SessionManager.kt):
  - `clearSession(clearCache: Boolean)`: Orquestra simultaneamente no despachante I/O a limpeza de Preferences DataStore, SharedPreferences padrão, SharedPreferences criptografados e diretórios de cache temporário.

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor (ex: `:data`):

```kotlin
dependencies {
    implementation("br.com.wgc:core-storage:1.2.0")
}
```

---

## 💡 Exemplos de Uso

### 1. Logout Atômico
```kotlin
// Injetado via Hilt ou instanciado manualmente
val sessionManager: SessionManager = ...

lifecycleScope.launch {
    sessionManager.clearSession(clearCache = true)
}
```

### 2. Cache em Dois Níveis com TTL
```kotlin
val cache = TwoLevelCache<UserProfile>(
    cacheDir = context.cacheDir,
    maxMemoryEntries = 50,
    defaultTtlMs = 5 * 60 * 1000L // 5 minutos
)

cache.put("user_123", profile)
val user = cache.get("user_123") // Lê da memória RAM L1
```

### 3. Enfileiramento Offline com Outbox Pattern
```kotlin
outboxQueue.enqueue(
    OutboxRequest(
        id = UUID.randomUUID().toString(),
        endpoint = "/api/v1/pedidos",
        payload = jsonPayload,
        method = "POST"
    )
)
syncManager.scheduleSync()
```
