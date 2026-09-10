# Guia de Contribuição — CoreAndroidNative 🚀

Obrigado pelo seu interesse em contribuir com o **CoreAndroidNative**! 
Este repositório segue rigorosos padrões de engenharia de software, Clean Architecture e convenções modernas para Android.

---

## 🏛️ Princípios Arquiteturais Obrigatórios

1. **Zero Compose na Base:** Módulos de persistência (`core-storage`, `core-database`), rede (`core-network`) e sistema (`core-device`) **nunca** devem depender de Jetpack Compose.
2. **Abstração por Interfaces:** Sempre declare interfaces públicas para serviços (ex: `KeyValueDataStore`, `NetworkMonitor`, `LocationClient`) antes da implementação concreta.
3. **KDoc Exaustivo:** Qualquer classe, interface, propriedade ou método público DEVE conter documentação KDoc com tags `@param`, `@return`, `@throws` e `@RequiresPermission` quando aplicável.
4. **Retrocompatibilidade:** Mudanças que quebram contratos públicos de bibliotecas não devem ser enviadas sem autorização prévia.

---

## 🛠️ Padrão de Commits (Conventional Commits)

Todas as mensagens de commit e títulos de Pull Requests **DEVEM** seguir a especificação de [Conventional Commits](https://www.conventionalcommits.org/):

* `feat(<modulo>):` Nova funcionalidade (ex: `feat(storage): add DataStore double support`)
* `fix(<modulo>):` Correção de bug (ex: `fix(network): prevent callbackFlow leak`)
* `refactor(<modulo>):` Refatoração de código sem alteração de comportamento externo
* `test(<modulo>):` Adição ou alteração de testes unitários ou de integração
* `docs(<modulo>):` Alterações exclusivas na documentação ou KDocs
* `chore(<modulo>):` Tarefas de build, dependências ou scripts Gradle

---

## 🧪 Validação Local Antes de Abrir PR

Antes de enviar seus commits para o repositório remoto, certifique-se de que todas as validações passam localmente:

```bash
# 1. Análise estática com Detekt
./gradlew detekt

# 2. Executar suíte de testes unitários
./gradlew testDebugUnitTest

# 3. Compilar AARs de release
./gradlew assembleRelease
```

---

## 🔀 Fluxo de Branches e Pull Requests

1. Crie uma branch a partir de `master`:
   ```bash
   git checkout -b feat/<nome-da-feature>
   # ou
   git checkout -b fix/<nome-do-bug>
   ```
2. Realize commits atômicos com mensagens padronizadas.
3. Envie a branch para o remoto:
   ```bash
   git push -u origin feat/<nome-da-feature>
   ```
4. Abra um **Pull Request** para a branch `master` preenchendo detalhadamente o template de PR.
5. Aguarde a validação da esteira do GitHub Actions (todos os 16 jobs do grafo devem passar com sucesso).
