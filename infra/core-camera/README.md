# Submódulo `:infra:core:camera` 📸

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--camera-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-SIM-success.svg)]()
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:infra:core:camera`** encapsula funcionalidades de câmera baseadas em **CameraX** e leitura de códigos de barras (QR Codes) utilizando o **ML Kit**.

---

## 🛠️ O que contém este submódulo?

### 1. Visualizador de Câmera CameraX (`br.com.wgc.core.camera`)
- [`CameraPreview`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/infra/core-camera/src/main/java/br/com/wgc/core/camera/CameraPreview.kt):
  - Componível Jetpack Compose para exibir o preview em tempo real da câmera traseira/frontal.
  - Gerenciamento automático do ciclo de vida da câmera (`LifecycleOwner`).

### 2. Leitor de QR Code / Barcode (`br.com.wgc.core.camera`)
- [`QrCodeScannerAnalyzer`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/infra/core-camera/src/main/java/br/com/wgc/core/camera/QrCodeScannerAnalyzer.kt):
  - `ImageAnalysis.Analyzer` integrado ao ML Kit Barcode Scanning.
  - Detecta e decodifica QR Codes e códigos de barras em tempo real a partir do stream da câmera, retornando o resultado via callback.

---

## 📥 Como importar

```kotlin
dependencies {
    implementation("br.com.wgc:core-camera:0.0.x")
}
```

---

## 🚀 Quando deve ser usado?

- Em telas de escaneamento de QR Code, leitura de documentos, Pix ou captura de fotos dentro do aplicativo.
