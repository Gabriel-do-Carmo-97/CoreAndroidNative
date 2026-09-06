# Submódulo `:core:device` 📱

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--device-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-NÃO-success.svg)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:core:device`** abstrai as características de hardware, sistema operacional Android, conectividade de rede e notificações. Ele isola as APIs nativas do Android Framework atrás de contratos desacoplados e fáceis de simular em testes unitários.

---

## 🛠️ O que contém este submódulo?

### 1. Metadados do Dispositivo (`br.com.wgc.core.device`)
- [`DeviceInfo`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/src/main/java/br/com/wgc/core/device/DeviceInfo.kt) e [`DefaultDeviceInfo`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/src/main/java/br/com/wgc/core/device/DeviceInfo.kt):
  - `versionName`: Versão semântica do app (ex: `"1.2.0"`).
  - `versionCode`: Código numérico de build da aplicação (ex: `42L`).
  - `sdkInt`: Nível da API Android em execução (ex: `34`).
  - `deviceModel`: Modelo do aparelho (ex: `"Pixel 8 Pro"`).
  - `manufacturer`: Fabricante (ex: `"Google"`, `"Samsung"`).
  - `isEmulator`: Detecção confiável se o app está executando em emulador.

### 2. Feedback Tátil Resiliente (`br.com.wgc.core.device`)
- [`HapticFeedbackHelper`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/src/main/java/br/com/wgc/core/device/HapticFeedbackHelper.kt) e [`DefaultHapticFeedbackHelper`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/src/main/java/br/com/wgc/core/device/HapticFeedbackHelper.kt):
  - `vibrateClick()`: Toque sutil para botões e seleções.
  - `vibrateSuccess()`: Padrão duplo suave indicativo de sucesso.
  - `vibrateError()`: Padrão firme indicando falha ou ação bloqueada.
  - Compatibilidade com `VibratorManager` (Android 12+ / API 31+) e fallback resiliente sem lançar exceções se o dispositivo não tiver vibrador ou permissão.

### 3. Segurança e Integridade do Dispositivo (`br.com.wgc.core.device`)
- [`DeviceSecurityHelper`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/src/main/java/br/com/wgc/core/device/DeviceSecurityHelper.kt) e [`DefaultDeviceSecurityHelper`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/src/main/java/br/com/wgc/core/device/DeviceSecurityHelper.kt):
  - **Detecção de Root 100% Opcional e Configurável**: Parâmetro `isRootDetectionEnabled = false` por padrão (ideal para e-commerces, apps de varejo e utilitários que não necessitam de bloqueio por root).
  - Verificação de tags de build `test-keys`, binários `su` e pacotes Superuser quando habilitado.
  - Detecção de depuração USB ativa (`isAdbEnabled`).
  - Diagnóstico integrado via `checkSecurity(): DeviceSecurityReport`.

### 4. Gerenciamento Moderno de Permissões (`br.com.wgc.core.device`)
- [`PermissionManager`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/src/main/java/br/com/wgc/core/device/PermissionManager.kt) e [`DefaultPermissionManager`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/src/main/java/br/com/wgc/core/device/PermissionManager.kt):
  - Consulta se permissões de tempo de execução estão concedidas (`isGranted`).
  - Verificação com justificativa (`checkPermissionState(activity, permission): PermissionState`).
  - Redirecionamento direto para a tela de configurações do aplicativo (`openAppSettings()`).

### 5. Monitoramento de Rede (`br.com.wgc.core.network`)
- [`NetworkMonitor`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/src/main/java/br/com/wgc/core/network/NetworkMonitor.kt):
  - `isConnected: StateFlow<Boolean>`: Observa o estado da conectividade com a internet via `ConnectivityManager.NetworkCallback` em tempo real.

### 6. Notificações Android (`br.com.wgc.core.notification`)
- [`NotificationHelper`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/src/main/java/br/com/wgc/core/notification/NotificationHelper.kt):
  - `CreateNotification.Builder`: Builder fluente para criação de notificações ricas com ícone, título, corpo, prioridade e intenções de toque.
  - `CreateChannelNotification.Builder`: Criação padronizada de canais de notificação (obrigatórios a partir do Android 8.0 / API 26).

### 7. Extensões de Contexto (`br.com.wgc.core.extensions`)
- [`ContextExtensions.kt`](file:///c:/Users/gcarm/Documents/GitHub/CoreAndroidNative/core/device/src/main/java/br/com/wgc/core/extensions/ContextExtensions.kt):
  - `Context.showToast()`: Exibição simples de mensagens Toast.
  - `Context.hasPermission()`: Verificação segura de permissões de tempo de execução.

---

## 📥 Como importar

No `build.gradle.kts` do módulo consumidor:

```kotlin
dependencies {
    implementation("br.com.wgc:core-device:0.0.x")
}
```

---

## 💡 Exemplo de Uso

```kotlin
// Monitorar internet
lifecycleScope.launch {
    networkMonitor.isConnected.collect { isOnline ->
        if (!isOnline) {
            hapticHelper.vibrateError()
        }
    }
}

// Disparar vibração de clique em botão
hapticHelper.vibrateClick()
```
