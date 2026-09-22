# Submódulo `:infra:core-device` 📱

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--device-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-NÃO-success.svg)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:infra:core-device`** abstrai as características de hardware, sistema operacional Android, conectividade de rede, sensores físicos (NFC, BLE) e notificações corporativas. Ele isola as APIs nativas do Android Framework atrás de contratos desacoplados e fáceis de simular em testes unitários.

---

## 🛠️ O que contém este submódulo?

### 1. Metadados do Dispositivo (`br.com.wgc.core.device`)
- [`DeviceInfo`](src/main/java/br/com/wgc/core/device/DeviceInfo.kt) e [`DefaultDeviceInfo`](src/main/java/br/com/wgc/core/device/DeviceInfo.kt):
  - `versionName`: Versão semântica do app (ex: `"1.2.0"`).
  - `versionCode`: Código numérico de build da aplicação (ex: `42L`).
  - `sdkInt`: Nível da API Android em execução (ex: `34`).
  - `deviceModel`: Modelo do aparelho (ex: `"Pixel 8 Pro"`).
  - `manufacturer`: Fabricante (ex: `"Google"`, `"Samsung"`).
  - `isEmulator`: Detecção confiável se o app está executando em emulador.

### 2. Feedback Tátil Resiliente (`br.com.wgc.core.device`)
- [`HapticFeedbackHelper`](src/main/java/br/com/wgc/core/device/HapticFeedbackHelper.kt) e [`DefaultHapticFeedbackHelper`](src/main/java/br/com/wgc/core/device/HapticFeedbackHelper.kt):
  - `vibrateClick()`: Toque sutil para botões e seleções.
  - `vibrateSuccess()`: Padrão duplo suave indicativo de sucesso.
  - `vibrateError()`: Padrão firme indicando falha ou ação bloqueada.
  - Compatibilidade com `VibratorManager` (Android 12+ / API 31+) e fallback resiliente sem lançar exceções se o dispositivo não tiver vibrador ou permissão.

### 3. Segurança e Integridade do Dispositivo (`br.com.wgc.core.device`)
- [`DeviceSecurityHelper`](src/main/java/br/com/wgc/core/device/DeviceSecurityHelper.kt) e [`DefaultDeviceSecurityHelper`](src/main/java/br/com/wgc/core/device/DeviceSecurityHelper.kt):
  - **Detecção de Root 100% Opcional e Configurável**: Parâmetro `isRootDetectionEnabled = false` por padrão.
  - Verificação de tags de build `test-keys`, binários `su` e pacotes Superuser quando habilitado.
  - Detecção de depuração USB ativa (`isAdbEnabled`).
  - Diagnóstico integrado via `checkSecurity(): DeviceSecurityReport`.

### 4. Notificações Corporativas & Canais (`br.com.wgc.core.device.notification`)
- [`NotificationManagerHelper`](src/main/java/br/com/wgc/core/device/notification/NotificationManagerHelper.kt):
  - Central de disparo de notificações com compatibilidade de versão Android 13+ (`POST_NOTIFICATIONS`).
- [`NotificationChannelConfig`](src/main/java/br/com/wgc/core/device/notification/NotificationChannelConfig.kt):
  - Configuração declarativa de canais com presets prontos (`SECURITY_ALERTS`, `TRANSACTIONS`, `GENERAL_UPDATES`).
- [`NotificationPayloadParser`](src/main/java/br/com/wgc/core/device/notification/NotificationPayloadParser.kt):
  - Parser seguro para payloads remotos de push notifications (Firebase Cloud Messaging) com extração de deep links.

### 5. Sensores de Hardware (NFC & BLE) (`br.com.wgc.core.device.hardware`)
- [`NfcHelper`](src/main/java/br/com/wgc/core/device/hardware/NfcHelper.kt):
  - Verificação de presença e estado ativo do sensor NFC, além de parsing seguro de tags NDEF.
- [`BleScannerHelper`](src/main/java/br/com/wgc/core/device/hardware/BleScannerHelper.kt):
  - Scanner reativo de dispositivos Bluetooth Low Energy (BLE) emitindo via Kotlin `Flow`.

### 6. Monitoramento de Rede (`br.com.wgc.core.network`)
- [`NetworkMonitor`](src/main/java/br/com/wgc/core/network/NetworkMonitor.kt):
  - `isConnected: StateFlow<Boolean>`: Observa o estado da conectividade com a internet via `ConnectivityManager.NetworkCallback` em tempo real.

### 7. Gerenciamento de Permissões (`br.com.wgc.core.device`)
- [`PermissionManager`](src/main/java/br/com/wgc/core/device/PermissionManager.kt) e [`DefaultPermissionManager`](src/main/java/br/com/wgc/core/device/PermissionManager.kt):
  - Consulta se permissões de tempo de execução estão concedidas e atalho direto para configurações (`openAppSettings()`).

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor:

```kotlin
dependencies {
    implementation("br.com.wgc:core-device:1.2.0")
}
```

---

## 💡 Exemplos de Uso

```kotlin
// Monitorar internet
lifecycleScope.launch {
    networkMonitor.isConnected.collect { isOnline ->
        if (!isOnline) {
            hapticHelper.vibrateError()
        }
    }
}

// Disparar notificação em canal corporativo
notificationManagerHelper.showNotification(
    id = 1001,
    channelId = NotificationChannelConfig.TRANSACTIONS.id,
    title = "Transação Aprovada",
    message = "Pix de R$ 150,00 enviado com sucesso."
)
```
