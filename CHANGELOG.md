# Changelog

Todas as alterações notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

---

## [Unreleased]

## [1.2.0] - 2026-09-20

### Added
- **Motor de Sincronização Offline & Outbox Pattern (`:infra:core-storage`)**:
  - `OutboxRequest`, `OutboxQueue` (thread-safe com StateFlow) e `SyncManager`.
  - Agendamento de background jobs resilientes com `WorkManager` e `SyncWorker`.
- **Comunicação Reativa em Tempo Real (`:infra:core-network`)**:
  - `CoreWebSocketClient` reativo sobre OkHttp baseado em corrotinas e `Flow<WebSocketEvent>`.
  - `ServerSentEventClient` para consumo de streaming Server-Sent Events (SSE).
- **Paginação Genérica com Cache Local (`:infra:core-database`)**:
  - `BaseRemoteMediator` e `RemoteKeyEntity` com suporte ao Jetpack Paging 3 integrado ao Room.
- **Armazenamento de Alta Performance (`:infra:core-storage`)**:
  - `TwoLevelCache` combinando L1 (RAM `LruCache`) e L2 (Disco) com política de invalidação temporal (TTL).
- **Central de Notificações Corporativas (`:infra:core-device`)**:
  - `NotificationChannelConfig` (Security, Transactions, General), `NotificationPayloadParser` e `NotificationManagerHelper`.
- **Conectividade com Sensores de Hardware (`:infra:core-device`)**:
  - `NfcHelper` para leitura de tags NDEF e verificação de hardware.
  - `BleScannerHelper` reativo via corrotinas para descoberta de dispositivos Bluetooth Low Energy (BLE).
- **Rollout Percentual Determinístico de Feature Flags (`:infra:core-common`)**:
  - Método `isRolloutEnabled` com hashing consistente de usuário.
- **Showcase Interativo Completo no `:app`**:
  - Nova aba interativa para testes de Outbox, Two-Level Cache, Canais de Notificação, Sensores de Hardware e Rollout de Feature Flags.
- **Integração Validada no Consumidor Real (`SolutionsAndroid`)**:
  - Consumo direto dos pacotes `1.1.0` via Maven Local e build validado com sucesso no hub de soluções.

## [1.1.0] - 2026-09-20

### Added
- **Autoinicialização Transparente (`:infra:core-common`)**:
  - Implementação do `CoreInitializer` via **Jetpack App Startup** (`androidx.startup:startup-runtime:1.2.0`).
  - Ativação automática do `StrictModeHelper` em builds debug via Manifest Merger sem poluir o `Application.onCreate()`.
  - Cobertura de testes unitários com Robolectric em `CoreInitializerTest`.
- **Relatório Consolidado de Cobertura Jacoco (`jacocoRootReport`)**:
  - Task raiz agregadora unindo classes e relatórios de execução de todos os submódulos da biblioteca.
  - Exclusões corporativas configuradas (gerados do Hilt, Room, Dagger, Synthetic Lambdas, R e BuildConfig).
  - Job dedicado `coverage-report` inserido na esteira de CI/CD do GitHub Actions.
- **Governança Técnica e Segurança de Dependências**:
  - `INTEGRATION_GUIDE.md`: Guia de integração corporativo completo para desenvolvedores e squads consumidoras.
  - `sonar-project.properties`: Configuração centralizada para análise estática e métricas no SonarQube/SonarCloud.
  - `dependency-check-suppressions.xml`: Arquivo de supressão corporativa para auditoria de CVEs e segurança via OWASP Dependency-Check.

---

## [1.0.0] - 2026-09-20

### Added
- **Lançamento Inicial da Infraestrutura Corporativa Android Native**:
  - **Módulos Atômicos de Infraestrutura**:
    - `:infra:core-common`: `ResultState`, `CoroutineDispatchers`, `retryWithBackoff`, `CoreLogger` com mascaramento automático de PII, validadores (CPF, CNPJ, Email, Telefone, CEP) e formatadores.
    - `:infra:core-storage`: `KeyValueDataStore`, `DataStorePreferencesCore`, `EncryptedSharedPreferencesCore` (AES-256 GCM) e `SessionManager`.
    - `:infra:core-database`: Room com criptografia **SQLCipher**, `BaseDao`, `RoomConverters` e suporte a migrações seguras.
    - `:infra:core-network`: `AuthInterceptor`, `TokenAuthenticator`, `SslPinningHelper` e gerenciamento resiliente de requisições.
    - `:infra:core-analytics`: `LgpdConsentManager` (consentimento explícito conforme LGPD/GDPR) e telemetria.
    - `:infra:core-device`: `DeviceInfo`, `NetworkMonitor` reativo com StateFlow, `HapticFeedbackHelper`, `NotificationHelper` e `DeviceSecurityHelper` (detecção de Root e depuradores).
    - `:infra:core-location`: `LocationClient` reativo via Google Play Services FusedLocationProvider e cálculo de distância de Haversine.
    - `:infra:core-camera`: `CameraPreview` com ciclo de vida Compose e `QrCodeScannerAnalyzer` via Google ML Kit Barcode Scanning.
    - `:infra:core-ui`: `VisualTransformations` para campos formatados, `debouncedClick` para evitar múltiplos cliques e `UiEffectChannel`.
    - `:infra:core-testing`: Framework de testes unitários com `MainDispatcherRule`, `FakeTokenProvider` e `MockWebServerHelper`.
  - **Bundles de Distribuição Temáticos**:
    - `:bundle:persistence`: Solução unificada de dados (Storage + Database).
    - `:bundle:networking`: Solução unificada de comunicação (Network + Analytics + Conectividade).
    - `:bundle:presentation`: Camada de UI e câmera integrada (UI + Camera).
    - `:bundle:hardware`: Integração de dispositivos e sensores (Device + Location).
  - **Módulo Guarda-chuva**:
    - `:infra:core` (`core-android-native`): Agregador completo com injeção de dependência central via Hilt.
  - **Engenharia e Governança**:
    - Esteira CI/CD em Grafo (DAG) com 16 jobs paralelos e isolados por módulo.
    - Spotless (ktlint) e Detekt com tolerância zero a code smells.
    - Validador de Compatibilidade Binária de API (BCV).
    - Geração e publicação de documentação automática com Dokka no GitHub Pages.
    - Regras ProGuard / R8 encapsuladas (`consumer-rules.pro`).
