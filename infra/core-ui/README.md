# Submódulo `:infra:core-ui` 🎨

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--ui-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-SIM-blue.svg)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:infra:core-ui`** encapsula utilitários de interface de usuário com Jetpack Compose, máscaras dinâmicas de campos de texto, efeitos de shimmer para skeletons e barramentos de efeitos colaterais MVI para a camada de apresentação.

---

## 🛠️ O que contém este submódulo?

### 1. Máscaras de Campos de Texto (`br.com.wgc.core.formatters.transformations`)
Implementações de `VisualTransformation` com mapeamento bidirecional seguro de cursor (`OffsetMapping`), garantindo navegação natural sem alterar os dados puros do estado:
- [`CpfVisualTransformation`](src/main/java/br/com/wgc/core/formatters/transformations/CpfVisualTransformation.kt): Formata dinamicamente campos de CPF (`000.000.000-00`).
- [`PhoneVisualTransformation`](src/main/java/br/com/wgc/core/formatters/transformations/PhoneVisualTransformation.kt): Formata telefones fixos `(00) 0000-0000` (10 dígitos) ou celulares `(00) 00000-0000` (11 dígitos).
- [`CepVisualTransformation`](src/main/java/br/com/wgc/core/formatters/transformations/CepVisualTransformation.kt): Formata CEPs brasileiros (`00000-000`).

### 2. Barramento de Efeitos de UI MVI (`br.com.wgc.core.result`)
- [`UiEffectChannel`](src/main/java/br/com/wgc/core/result/UiEffectChannel.kt) e [`DefaultUiEffectChannel`](src/main/java/br/com/wgc/core/result/UiEffectChannel.kt):
  - Canal de disparo único (*single-shot events*) baseado em Kotlin `Channel.BUFFERED`.
  - Evita reexecução indevida de eventos transitórios (navegação, abertura de bottom sheets, Snackbars) durante recomposições do Jetpack Compose ou rotação de tela.

### 3. Efeito Shimmer para Skeletons (`br.com.wgc.core.ui.shimmer`)
- [`Modifier.shimmerEffect()`](src/main/java/br/com/wgc/core/ui/shimmer/ShimmerModifier.kt):
  - Animação de carregamento skeleton fluida e configurável.
  - Alternância dinâmica entre skeleton e componente real sem duplicar nós composable.

### 4. Anotações de Multi-Preview Corporativas (`br.com.wgc.core.ui.previews`)
- [`MultiPreviews.kt`](src/main/java/br/com/wgc/core/ui/previews/MultiPreviews.kt):
  - `@ThemePreviews`: Renderização simultânea em Light e Dark Mode no Android Studio.
  - `@DevicePreviews`: Validação visual em Phone, Tablet e Foldable.
  - `@FontScalePreviews`: Teste de acessibilidade com fontes normais (1.0x) e ampliadas (1.5x).
  - `@CompletePreviews`: Combinação de temas e múltiplos dispositivos em uma única anotação.

### 5. Compressor de Imagens (`br.com.wgc.core.ui.media`)
- [`ImageCompressor`](src/main/java/br/com/wgc/core/ui/media/ImageCompressor.kt) e [`DefaultImageCompressor`](src/main/java/br/com/wgc/core/ui/media/ImageCompressor.kt):
  - Redimensionamento proporcional preservando a proporção de tela (aspect ratio).
  - Compressão assíncrona em background via Coroutines para JPEG, WEBP ou PNG antes de upload.

### 6. Extensões de Modificadores Compose (`br.com.wgc.core.extensions`)
- [`ModifierExtensions.kt`](src/main/java/br/com/wgc/core/extensions/ModifierExtensions.kt):
  - `Modifier.clickableDebounced()`: Previne cliques múltiplos acidentais consecutivos em botões e componentes clicáveis.

---

## 📥 Como importar

No `build.gradle.kts` do seu módulo de apresentação (ex: `:presentation` ou `:feature`):

```kotlin
dependencies {
    implementation("br.com.wgc:core-ui:1.2.0")
}
```
