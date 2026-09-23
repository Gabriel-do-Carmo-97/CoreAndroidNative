# 📘 Guia de Integração Corporativo — CoreAndroidNative

Bem-vindo ao **CoreAndroidNative**! Este guia fornece aos desenvolvedores e squads todas as diretrizes, padrões arquiteturais e exemplos práticos para integrar a infraestrutura corporativa nativa em seus aplicativos e módulos de feature.

---

## 🧭 Visão Geral e Princípios Arquiteturais

O **CoreAndroidNative** é construído sob os fundamentos de **Platform Engineering** e **Clean Architecture**:

1. **Separação Rígida de Camadas:** Módulos de domínio e regras de negócio não devem conhecer frameworks de UI. O submódulo `:infra:core-common` é 100% livre de Jetpack Compose.
2. **Desacoplamento Total do Design System:** O ecossistema de UI (tokens visuais, componentes de marca, máscaras e templates) reside exclusivamente no repositório `DesignSystemAndroid`. O `CoreAndroidNative` é focado em infraestrutura de dados, rede, segurança e hardware (`:infra:core-camera`).
3. **Distribuição em Dois Níveis:**
   - **Módulos Atômicos (`:infra:core-*`):** Para squads que exigem isolamento granular e menor pegada de compilação.
   - **Bundles Temáticos (`:bundle:*`):** Pacotes agregadores prontos para consumo por domínio funcional (`persistence`, `networking`, `hardware`, `presentation`).
4. **Inicialização Transparente:** Graças ao **Jetpack App Startup**, configurações essenciais (ex: `StrictModeHelper` em builds de debug) são aplicadas automaticamente via Manifest Merger, sem poluir o `Application.onCreate()` do app consumidor.

---

## 🚀 Como Configurar no Projeto Consumidor

### 1. Adicionar o Repositório de Pacotes

No seu arquivo `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/CoreAndroidNative")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

### 2. Escolher a Estratégia de Consumo

#### Estratégia A: Por Bundles Temáticos (Recomendada para Produtividade)

Ideal para a maioria dos módulos de feature, reduzindo a complexidade do `build.gradle.kts`:

```kotlin
dependencies {
    // Banco de Dados Criptografado + DataStore + SharedPreferences
    implementation("br.com.wgc:bundle-persistence:1.2.0")

    // OkHttp/Retrofit + Interceptors + Autenticação + Telemetria LGPD
    implementation("br.com.wgc:bundle-networking:1.2.0")

    // Localização FusedLocation + Monitor de Rede + Sensores + Notificações
    implementation("br.com.wgc:bundle-hardware:1.2.0")

    // Máscaras Visuais Compose + CameraX Preview com Scanner QR
    implementation("br.com.wgc:bundle-presentation:1.2.0")
}
```

#### Estratégia B: Consumo Granular (Clean Architecture Estrita)

Para arquiteturas corporativas de grande escala com isolamento absoluto por camadas:

```kotlin
// 📁 No módulo :feature-login:domain (sem dependências de UI nem de BD)
dependencies {
    implementation("br.com.wgc:core-common:1.2.0")
}

// 📁 No módulo :feature-login:data (persistência e rede)
dependencies {
    implementation("br.com.wgc:core-storage:1.2.0")
    implementation("br.com.wgc:core-network:1.2.0")
}

// 📁 No módulo :feature-login:presentation (telas Compose do Design System)
dependencies {
    implementation("br.com.wgc:design-system:1.0.0")
    // Se utilizar scanner QR / câmera:
    // implementation("br.com.wgc:core-camera:1.2.0")
}
```

---

## 🛠️ Práticas e Padrões de Uso

### 1. Injeção de Dependências com Hilt

O `CoreAndroidNative` provê módulos Hilt prontos em `@InstallIn(SingletonComponent::class)`. Para injetar os serviços do Core:

```kotlin
@HiltViewModel
class MinhaFeatureViewModel @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val networkMonitor: NetworkMonitor,
    private val consentManager: LgpdConsentManager
) : ViewModel() {

    fun monitorarConectividade() {
        viewModelScope.launch(dispatchers.io) {
            networkMonitor.isOnline.collect { isConnected ->
                // reagir ao status de conectividade
            }
        }
    }
}
```

### 2. Persistência Segura com Room & SQLCipher

Crie bancos criptografados utilizando o `DatabaseEncrypter`:

```kotlin
@Provides
@Singleton
fun provideAppDatabase(
    @ApplicationContext context: Context,
    encrypter: DatabaseEncrypter
): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "secure_database.db")
        .openHelperFactory(encrypter.getSupportFactory())
        .build()
}
```

### 3. Chamadas de Rede Seguras com Resiliência

Utilize o `ResultState` e o helper `retryWithBackoff`:

```kotlin
suspend fun fetchUserData(userId: String): ResultState<UserDto> = withContext(dispatchers.io) {
    retryWithBackoff(maxRetries = 3, initialDelayMs = 500L) {
        val response = api.getUser(userId)
        ResultState.Success(response)
    }
}
```

---

## 🧪 Estratégia de Testes com `:infra:core-testing`

O módulo `:infra:core-testing` elimina boilerplate de testes nas features:

```kotlin
dependencies {
    testImplementation("br.com.wgc:core-testing:1.2.0")
}
```

### 1. Controlando Corrotinas com `MainDispatcherRule`

Substitui o `Dispatchers.Main` durante os testes de ViewModel e corrotinas:

```kotlin
class MinhaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testDispatchers = TestCoroutineDispatchers(mainDispatcherRule.testDispatcher)

    @Test
    fun `deve carregar dados com sucesso`() = runTest {
        val viewModel = MinhaViewModel(dispatchers = testDispatchers)
        viewModel.carregar()
        // assertions...
    }
}
```

### 2. Fakes e Mocks Integrados

- **`FakeTokenProvider`**: Teste interceptors e renovação de tokens JWT sem necessidade de mockar APIs de autenticação.
- **`MockWebServerHelper`**: Configure cenários de sucesso (200), erro de cliente (401/404) ou servidor (500) com respostas JSON em arquivos ou strings.

---

## 🛡️ Governança e Qualidade

Ao contribuir ou integrar com o `CoreAndroidNative`:

| Comando | Descrição |
| :--- | :--- |
| `./gradlew spotlessCheck` | Verifica formatação e padrões de estilo Kotlin. |
| `./gradlew spotlessApply` | Corrige formatação automaticamente. |
| `./gradlew detekt` | Análise estática profunda de complexidade e smells. |
| `./gradlew apiCheck` | Garante que nenhuma API pública foi quebrada sem atualização do dump de compatibilidade. |
| `./gradlew apiDump` | Regenera arquivos `.api` quando uma nova API pública for intencionalmente adicionada. |
| `./gradlew jacocoRootReport` | Gera o relatório consolidado de cobertura de testes em HTML e XML. |

---

## 🤝 Suporte e Dúvidas

Para dúvidas sobre arquitetura, solicitação de novas capacidades ou reporte de bugs, abra uma issue ou consulte a [Documentação Dokka](https://gabriel-do-carmo-97.github.io/CoreAndroidNative/).
