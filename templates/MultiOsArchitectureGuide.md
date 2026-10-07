# 🌍 Guia de Arquitetura Multi-OS (KMP) do WGC Core

Este guia define os padrões e convenções adotados para a evolução do `CoreAndroidNative` para suporte multiplataforma (Android, iOS e Desktop).

## 🏛️ Camadas Arquiteturais

1. **Camada de Domínio e Contratos (`commonMain` / `:infra:core-common`):**
   - Modelagem de erros com `ResultWrapper` e `CoreDomainError`.
   - Injeção de dependências desacoplada via `CoreServiceLocator`.
   - IO agnóstico com `CoreFileSystem`.
   - Versionamento de mensagens com `SchemaVersionedSerializer`.

2. **Camada de Persistência (`:infra:core-storage` / `:infra:core-database`):**
   - Interface universal `KeyValueStorage` com implementação `InMemoryKeyValueStorage` para testes e JVM.
   - Contratos DAO puros `CoreEntityDao` desacoplados do Android Room runtime.
   - Fornecimento de chaves de banco desacoplado via `DatabaseCipherKeyProvider`.

3. **Camada de Comunicação de Rede (`:infra:core-network`):**
   - Interface `CoreHttpClient` que abstrai engines específicas (`OkHttp` no Android, `Ktor` no iOS).
   - Tipos de requisição e resposta puros `CoreHttpRequest` e `CoreHttpResponse`.
