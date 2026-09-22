# 🏛️ Arquitetura e Diretrizes Técnicas — CoreAndroidNative

O **CoreAndroidNative** é a fundação de plataforma compartilhada para aplicações corporativas nativas em Android. Este documento detalha as decisões arquiteturais, o modelo de isolamento por camadas, os padrões de resiliência e as restrições invioláveis de engenharia.

---

## 🧭 Princípios Norteadores

1. **Zero UI nas Camadas Base:**
   - Módulos de regras de negócio (`:infra:core-common`), armazenamento (`:infra:core-storage`), banco de dados (`:infra:core-database`), rede (`:infra:core-network`) e sistema (`:infra:core-device`) **NÃO** dependem de Jetpack Compose nem de frameworks de interface.
   - Apenas `:infra:core-ui` e `:infra:core-camera` contêm dependências visuais.
2. **Abstração Baseada em Contratos:**
   - Todo componente reutilizável expõe uma `interface` pública (ex: `KeyValueDataStore`, `NetworkMonitor`, `TokenProvider`, `LocationClient`) antes da sua implementação concreta, facilitando a substituição em testes com fakes determinísticos.
3. **Distribuição em Dois Níveis:**
   - **Módulos Atômicos (`:infra:core-*`):** Para squads que priorizam arquiteturas estritas e tempos mínimos de compilação.
   - **Bundles Agregadores (`:bundle:*`):** Soluções temáticas pré-empacotadas (`persistence`, `networking`, `presentation`, `hardware`) que reduzem o boilerplate nos `build.gradle.kts`.
   - **Módulo Guarda-Chuva (`:infra:core`):** Ponto único de entrada com injeção de dependências global via Hilt (`@InstallIn(SingletonComponent::class)`).
4. **Inicialização Transparente:**
   - Inicializações essenciais (como `StrictModeHelper` em builds de debug) utilizam o **Jetpack App Startup**, eliminando a necessidade de acoplar lógica manual no `Application.onCreate()` dos apps consumidores.

---

## 🗺️ Mapa de Camadas e Dependências

```mermaid
graph TD
    classDef common fill:#FBBC04,stroke:#F29900,stroke-width:2px,color:#000;
    classDef infra fill:#34A853,stroke:#1E8E3E,stroke-width:2px,color:#fff;
    classDef bundle fill:#9C27B0,stroke:#7B1FA2,stroke-width:2px,color:#fff;
    classDef umbrella fill:#EA4335,stroke:#D93025,stroke-width:2px,color:#fff;

    Common[":infra:core-common<br/>(ResultState, Coroutines, Formatters, Validators)"]:::common

    Storage[":infra:core-storage<br/>(DataStore, SharedPreferences, Outbox, Cache L1/L2)"]:::infra
    Database[":infra:core-database<br/>(Room, SQLCipher, Paging 3)"]:::infra
    Network[":infra:core-network<br/>(OkHttp, Auth, WebSocket, SSE)"]:::infra
    Analytics[":infra:core-analytics<br/>(LGPD Consent, Telemetria)"]:::infra
    Device[":infra:core-device<br/>(DeviceInfo, Notificações, NFC, BLE)"]:::infra
    Location[":infra:core-location<br/>(GPS, DistanceUtils)"]:::infra
    Camera[":infra:core-camera<br/>(CameraX, ML Kit QR)"]:::infra
    UI[":infra:core-ui<br/>(Máscaras, Shimmer, MVI Effects)"]:::infra
    Testing[":infra:core-testing<br/>(MainDispatcherRule, Fakes)"]:::infra

    BundlePersist["📦 :bundle:persistence"]:::bundle
    BundleNet["📦 :bundle:networking"]:::bundle
    BundlePres["📦 :bundle:presentation"]:::bundle
    BundleHW["📦 :bundle:hardware"]:::bundle

    Umbrella["🛡️ :infra:core<br/>(Guarda-Chuva Hilt)"]:::umbrella

    Common --> Storage
    Common --> Database
    Common --> Network
    Common --> Analytics
    Common --> Device
    Common --> Location
    Common --> Camera
    Common --> UI
    Common --> Testing

    Storage --> Database
    Storage --> BundlePersist
    Database --> BundlePersist

    Network --> BundleNet
    Device --> BundleNet
    Analytics --> BundleNet

    UI --> BundlePres
    Camera --> BundlePres

    Device --> BundleHW
    Location --> BundleHW

    BundlePersist --> Umbrella
    BundleNet --> Umbrella
    BundlePres --> Umbrella
    BundleHW --> Umbrella
```

---

## 🔒 Segurança em Repouso e em Trânsito

- **Persistência Criptografada:**
  - Valores de preferências confidenciais utilizam `EncryptedSharedPreferencesCore` apoiado pelo **Android KeyStore** com chaves AES-256 GCM e esquemas SIV.
  - O banco de dados SQLite local utiliza criptografia transparente de 256 bits com **SQLCipher** via `DatabaseEncrypter`.
- **Tráfego Seguro e Antifraude:**
  - `AuthInterceptor` e `TokenAuthenticator` garantem renovação segura de tokens JWT com trava atômica `Mutex`.
  - `SslPinningHelper` previne ataques Man-In-The-Middle (MITM) validando hashes SPKI SHA-256.
  - Sanitização preventiva de PII em logs e telemetria (`CoreLogger` mascara CPFs, cartões e tokens).

---

## ⚡ Estratégias de Concorrência & Offline-First

- **Concorrência Segura:**
  - Toda operação assíncrona é ancorada em despachantes explícitos via `CoroutineDispatchers` injetável (`main`, `io`, `default`, `unconfined`).
- **Offline Sync & Outbox Pattern:**
  - Durante instabilidade de conexão, requisições mutativas de rede são enfileiradas na `OutboxQueue` (persistente em disco e thread-safe).
  - O `SyncManager` despacha o processamento via `WorkManager` respeitando restrições de bateria e rede ativa.
- **Dois Níveis de Cache (L1 + L2):**
  - O `TwoLevelCache` provê velocidade instantânea de leitura em memória RAM (`LruCache`) combinada com persistência em disco com política de expiração por TTL (*Time-to-Live*).

---

## 🎯 Governança & Portões de Qualidade (Quality Gates)

Todas as contribuições e pull requests são submetidos a verificações estritas:

| Ferramenta / Job | Critério de Aceite |
| :--- | :--- |
| **Spotless (ktlint)** | 0 violações de formatação de código. |
| **Detekt** | 0 *code smells* ou violações de complexidade ciclomática. |
| **Binary Compatibility (BCV)** | Nenhuma alteração não intencional em APIs públicas (validação com `apiCheck`). |
| **Testes Unitários** | 100% de sucesso nas suítes JVM locais com Robolectric e Turbine. |
| **Jacoco** | Relatório de cobertura consolidado gerado via task agregadora `jacocoRootReport`. |
