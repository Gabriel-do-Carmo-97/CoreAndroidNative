# Submódulo `:core:ui` 🎨

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--ui-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-SIM-blue.svg)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:core:ui`** é o **único submódulo** da biblioteca que declara dependência do **Jetpack Compose**. Ele encapsula utilitários de interface de usuário, máscaras dinâmicas de campos de texto e canais de efeitos colaterais MVI para a camada de apresentação.

---

## 🛠️ O que contém este submódulo?

### 1. Máscaras de Campos de Texto (`br.com.wgc.core.formatters.transformations`)
Implementações de `VisualTransformation` com mapeamento bidirecional seguro de cursor (`OffsetMapping`), garantindo navegação natural sem alterar os dados puros do estado:
- [`CpfVisualTransformation`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/ui/src/main/java/br/com/wgc/core/formatters/transformations/CpfVisualTransformation.kt): Formata dinamicamente campos de CPF (`000.000.000-00`).
- [`PhoneVisualTransformation`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/ui/src/main/java/br/com/wgc/core/formatters/transformations/PhoneVisualTransformation.kt): Formata telefones fixos `(00) 0000-0000` (10 dígitos) ou celulares `(00) 00000-0000` (11 dígitos).
- [`CepVisualTransformation`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/ui/src/main/java/br/com/wgc/core/formatters/transformations/CepVisualTransformation.kt): Formata CEPs brasileiros (`00000-000`).

### 2. Barramento de Efeitos de UI MVI (`br.com.wgc.core.result`)
- [`UiEffectChannel`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/ui/src/main/java/br/com/wgc/core/result/UiEffectChannel.kt) e [`DefaultUiEffectChannel`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/ui/src/main/java/br/com/wgc/core/result/UiEffectChannel.kt):
  - Canal de disparo único (*single-shot events*) baseado em Kotlin `Channel.BUFFERED`.
  - Evita reexecução indevida de eventos transitórios (navegação, abertura de bottom sheets, Toasts, Snackbars) durante recomposições do Jetpack Compose ou recriação de Activities por rotação de tela.

### 3. Efeito Shimmer para Skeletons (`br.com.wgc.core.ui.shimmer`)
- [`Modifier.shimmerEffect()`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/ui/src/main/java/br/com/wgc/core/ui/shimmer/ShimmerModifier.kt):
  - Animação de carregamento skeleton fluida e configurável (cores, duração de ciclo e visibilidade).
  - Permite alternar dinamicamente entre o skeleton e o componente real sem duplicar árvores composable.

### 4. Anotações de Multi-Preview Corporativas (`br.com.wgc.core.ui.previews`)
- [`MultiPreviews.kt`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/ui/src/main/java/br/com/wgc/core/ui/previews/MultiPreviews.kt):
  - `@ThemePreviews`: Renderização simultânea em Light e Dark Mode no Android Studio.
  - `@DevicePreviews`: Validação visual em Phone, Tablet e Foldable.
  - `@FontScalePreviews`: Teste de acessibilidade visual com fontes normais (1.0x) e ampliadas (1.5x).
  - `@CompletePreviews`: Combinação de temas e múltiplos dispositivos em uma única anotação.

### 5. Compressor de Imagens (`br.com.wgc.core.ui.media`)
- [`ImageCompressor`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/ui/src/main/java/br/com/wgc/core/ui/media/ImageCompressor.kt) e [`DefaultImageCompressor`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/ui/src/main/java/br/com/wgc/core/ui/media/ImageCompressor.kt):
  - Redimensionamento proporcional preservando a proporção de tela (aspect ratio).
  - Compressão assíncrona em background via Coroutines (`Dispatchers.IO` / `Default`) para JPEG, WEBP ou PNG antes de upload para a nuvem.

### 6. Extensões de Modificadores Compose (`br.com.wgc.core.extensions`)
- [`ModifierExtensions.kt`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/ui/src/main/java/br/com/wgc/core/extensions/ModifierExtensions.kt):
  - `Modifier.clickableDebounced()`: Previne cliques múltiplos acidentais consecutivos em botões e componentes clicáveis.

---

## 📥 Como importar

No `build.gradle.kts` do seu módulo de UI / apresentação (ex: `:presentation` ou `:feature`):

```kotlin
dependencies {
    implementation("br.com.wgc:core-ui:0.0.x")
}
```

---

## 💡 Exemplo de Uso

### Campo de Texto com Máscara de CPF no Compose:
```kotlin
var cpfInput by remember { mutableStateOf("") }

OutlinedTextField(
    value = cpfInput,
    onValueChange = { novoTexto ->
        val digitos = novoTexto.unmask()
        if (digitos.length <= 11) {
            cpfInput = digitos
        }
    },
    label = { Text("CPF") },
    visualTransformation = CpfVisualTransformation(),
    isError = cpfInput.length == 11 && !cpfInput.isValidCpf()
)
```

### Efeito Colateral de Disparo Único no MVI:
```kotlin
// No ViewModel:
sealed interface LoginEffect {
    data class NavigateToHome(val userId: String) : LoginEffect
    object ShowErrorSnackbar : LoginEffect
}

val effectChannel: UiEffectChannel<LoginEffect> = DefaultUiEffectChannel()

fun onLoginClicked() {
    viewModelScope.launch {
        effectChannel.sendEffect(LoginEffect.NavigateToHome("123"))
    }
}

// Na Tela Compose:
LaunchedEffect(Unit) {
    viewModel.effectChannel.effects.collect { effect ->
        when (effect) {
            is LoginEffect.NavigateToHome -> navController.navigate("home/${effect.userId}")
            is LoginEffect.ShowErrorSnackbar -> snackbarHostState.showSnackbar("Erro ao logar")
        }
    }
}
```
