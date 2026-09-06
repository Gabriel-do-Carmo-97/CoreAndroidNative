# Agentes — CoreAndroidNative 🤖

Esta pasta contém as definições, identidades, prompts e regras de operação dos agentes autônomos especializados do repositório **CoreAndroidNative**.

---

## 🎯 Visão Geral

O **CoreAndroidNative** é uma biblioteca base (Core Library) nativa para Android em Kotlin. Os agentes atuam de forma coordenada para garantir que qualquer evolução no código preserve retrocompatibilidade, robustez em runtime, cobertura de testes, segurança e automação de publicação.

---

## 👥 Agentes Disponíveis

| Agente | Arquivo | Papel / Especialidade |
|---|---|---|
| **Orchestrator** | [`core-orchestrator.md`](./core-orchestrator.md) | Coordena tarefas, delega trabalho aos especialistas e valida resultados (não gera código diretamente). |
| **Core Library** | [`core-library-agent.md`](./core-library-agent.md) | Desenvolve e mantém utilitários do módulo `:core` (DataStore, SharedPreferences, NetworkMonitor, Notificações, ResultState, Extensions). |
| **Sample App** | [`sample-app-agent.md`](./sample-app-agent.md) | Mantém o aplicativo de demonstração e testes manuais (`:app`) que consome o `:core`. |
| **Testing** | [`testing-agent.md`](./testing-agent.md) | Desenvolve testes unitários (Robolectric + Turbine) e instrumentados (AndroidKeyStore). |
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
   ┌─────────┼──────────────┬──────────────┬──────────────┐
   ▼         ▼              ▼              ▼              ▼
[core-lib] [sample-app] [testing-agent] [gradle-agent] [github-agent]
   │         │              │              │              │
   └─────────┴──────────────┼──────────────┴──────────────┘
                            ▼
                  [code-reviewer-agent]
                            │
                            ▼
                    [Parecer / Entrega]
```

| Tipo de Tarefa | Agentes Ativados |
|---|---|
| **Nova feature no Core** | `orchestrator` → `core-library` → `testing` → `code-reviewer` |
| **Demonstração / Exemplo de uso** | `orchestrator` → `sample-app` → `code-reviewer` |
| **Novos testes / Aumento de cobertura** | `orchestrator` → `testing` → `code-reviewer` |
| **Dependências / Plugins / Publicação** | `orchestrator` → `gradle` → `code-reviewer` |
| **Abertura de PR / Release / CI** | `orchestrator` → `github` |

---

## 📜 Regras Gerais dos Agentes

1. **Retrocompatibilidade**: O módulo `:core` é consumido por outros aplicativos. Mudanças que quebram contratos públicos existentes requerem autorização expressa.
2. **Qualidade Contínua**: Nenhuma alteração no código do `:core` é entregue sem passar com sucesso na suíte de testes (`./gradlew :core:testDebugUnitTest`).
3. **Desacoplamento**: Interfaces devem ser preferidas para componentes de persistência para facilitar testes nos apps consumidores.
