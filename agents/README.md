# Agentes — CoreAndroidNative 🤖

Esta pasta contém as definições, identidades, prompts e regras de operação dos agentes autônomos especializados do repositório **CoreAndroidNative**.

---

## 🎯 Visão Geral

O **CoreAndroidNative** é uma biblioteca de infraestrutura Android corporativa nativa em Kotlin dividida em camadas (`infra/`, `bundle/` e `app/`). Os agentes atuam de forma coordenada para garantir que qualquer evolução no código preserve retrocompatibilidade, robustez em runtime, cobertura de testes, segurança e automação de publicação.

---

## 👥 Agentes Disponíveis

### Orquestrador
| Agente | Arquivo | Papel / Especialidade |
|---|---|---|
| **Orchestrator** | [`core-orchestrator.md`](./core-orchestrator.md) | Coordena tarefas, delega trabalho aos especialistas de domínio e valida resultados. |

### Especialistas de Domínio (Infraestrutura & Bundles)
| Agente | Arquivo | Módulos sob Responsabilidade |
|---|---|---|
| **Data & Storage** | [`data-storage-agent.md`](./data-storage-agent.md) | `infra:core-storage`, `infra:core-database`, `bundle:persistence` (DataStore, SharedPreferences, Room, SQLCipher, Migrations). |
| **Network & Analytics** | [`network-analytics-agent.md`](./network-analytics-agent.md) | `infra:core-network`, `infra:core-analytics`, `bundle:networking` (OkHttp, Interceptors, Telemetria, Mascaramento PII/LGPD). |
| **UI & Presentation** | [`ui-presentation-agent.md`](./ui-presentation-agent.md) | `infra:core-ui`, `bundle:presentation` (Jetpack Compose, VisualTransformations de CPF/CNPJ/Phone, Modifiers, UiEffectChannel). |
| **Device & Hardware** | [`device-hardware-agent.md`](./device-hardware-agent.md) | `infra:core-device`, `infra:core-location`, `infra:core-camera`, `bundle:hardware` (DeviceInfo, Notificações, Haptics, GPS, CameraX). |
| **Core Foundation** | [`core-library-agent.md`](./core-library-agent.md) | `infra:core-common` e `infra:core` (ResultState, CoroutineDispatchers, Formatters, Validators, DI Hilt central). |

### Aplicação & Suporte Transversal
| Agente | Arquivo | Papel / Especialidade |
|---|---|---|
| **Sample App** | [`sample-app-agent.md`](./sample-app-agent.md) | Mantém o aplicativo de demonstração e testes manuais (`:app`) que consome as bibliotecas. |
| **Testing** | [`testing-agent.md`](./testing-agent.md) | Desenvolve testes unitários (Robolectric + Turbine) e instrumentados. |
| **Code Reviewer** | [`code-reviewer-agent.md`](./code-reviewer-agent.md) | Inspeciona qualidade, segurança (OWASP), padrões de API pública e emite parecer APROVADO / REPROVADO. |
| **Gradle & Build** | [`gradle-agent.md`](./gradle-agent.md) | Gerencia scripts Gradle, Version Catalog (`libs.versions.toml`), KSP e publicação Maven. |
| **Dev Workflow & CI/CD** | [`github-agent.md`](./github-agent.md) | Gerencia Git, PRs, GitHub Actions (`android.yaml`), Dependabot, tags e releases. |

---

## 🔄 Modelo de Ativação

```
              [Solicitação do Desenvolvedor]
                            │
                            ▼
                  [core-orchestrator]
                            │
      ┌─────────────────────┼─────────────────────┬─────────────────────┐
      ▼                     ▼                     ▼                     ▼
[data-storage]    [network-analytics]     [ui-presentation]     [device-hardware]
      │                     │                     │                     │
      └─────────────────────┼─────────────────────┴─────────────────────┘
                            ▼
                     [testing-agent]
                            │
                            ▼
                  [code-reviewer-agent]
                            │
                            ▼
                    [Parecer / Entrega]
```

---

## 📜 Regras Gerais dos Agentes

1. **Retrocompatibilidade**: Os módulos de infraestrutura e bundles são consumidos por outros aplicativos. Mudanças que quebram contratos públicos requerem autorização expressa.
2. **Qualidade Contínua**: Nenhuma alteração é entregue sem validação com `./gradlew :<modulo>:testDebugUnitTest` e `./gradlew :<modulo>:detekt`.
3. **Desacoplamento Estrito**: Camadas base de dados e rede NÃO devem importar Jetpack Compose ou bibliotecas de UI.
