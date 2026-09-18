# Changelog

Todas as alterações notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado no [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.0.0] - 2026-09-18

### Adicionado
- **Arquitetura Hipergranular**: Divisão em 9 bibliotecas base `:infra` e 4 agregadores `:bundle`.
- **Pipeline CI/CD DAG Paralela**: GitHub Actions com 23 jobs otimizados, cache incremental e publicação por camadas.
- **Suporte a SemVer no Maven**: Metadados corporativos POM com licença Apache 2.0, desenvolvedores e SCM.
- **Detekt & Lint Quality Gates**: Enforcement rigoroso sem supressão de falhas (`ignoreFailures = false`, `abortOnError = true`).
- **Binary Compatibility Validator (BCV)**: Monitoramento automático de compatibilidade binária pública de API (`apiCheck`).
- **SQLCipher 256-bit Key Management**: Gerenciamento de chave raw de 32 bytes via `DatabaseEncrypter` com hardware KeyStore.
- **NetworkClientFactory & SslPinningHelper**: Fábrica padronizada de OkHttpClient corporativo com timeouts de 15s e SSL Pinning.
- **TokenAuthenticator & AuthInterceptor**: Renovação transparente de token JWT com serialização de concorrência via Mutex.
- **LGPD Consent Management**: `LgpdConsentManager` para conformidade com privacidade e opt-in/opt-out granular.
- **GPS Location Flow**: `DefaultLocationClient` baseado no FusedLocationProviderClient reativo com cancelamento limpo.
- **Acessibilidade Compose**: Semântica e leitores de tela em componentes de UI (`GenericErrorScreen`, `EmptyStateScreen`).
- **Governança**: `CODEOWNERS` e catálogo de showcase interativo no aplicativo de demonstração.

### Modificado
- `isMinifyEnabled`: Ativado R8 / ProGuard no build type `release` do aplicativo de exemplo.
- Suíte de testes unitários expandida com MockK e kotlinx-coroutines-test em todos os módulos de infraestrutura.
