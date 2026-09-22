# Módulo Aplicativo `:app` 📱

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)]()
![Min SDK](https://img.shields.io/badge/minSdk-29-blue.svg)
![Target SDK](https://img.shields.io/badge/targetSdk-37-blue.svg)

O **`:app`** é o módulo principal executável da aplicação Android. Ele serve como ponto de entrada (`Application`, Activities) e vitrine interativa (Showcase) de demonstração e teste de todos os submódulos e bundles corporativos do **CoreAndroidNative**.

---

## 🛠️ O que contém este módulo?

- **[`CoreAndroidNativeApp`](src/main/java/br/com/wgc/coreandroidnative/CoreAndroidNativeApp.kt)**: Classe `@HiltAndroidApp` responsável por inicializar a injeção de dependências global via Dagger/Hilt.
- **[`MainActivity`](src/main/java/br/com/wgc/coreandroidnative/MainActivity.kt)**: Atividade principal desenvolvida em Jetpack Compose com Material 3, estruturada em abas navegáveis via `ScrollableTabRow`:
  1. **Armazenamento:** DataStore Preferences reativo, SharedPreferences criptografado com Android KeyStore e logout atômico via `SessionManager`.
  2. **Rede & Telemetria:** Interceptors de autenticação JWT, renovação com Mutex, SSL Pinning e gerenciamento de consentimento LGPD.
  3. **UI & Formulários:** Máscaras visuais de CPF, CNPJ, Telefone e CEP com OffsetMapping, Shimmer e cliques com debounce.
  4. **Câmera & Scanner:** Integração de CameraPreview com ciclo de vida Compose e leitor de QR Code via ML Kit.
  5. **Hardware & Sensores:** Informações do dispositivo, conectividade de rede em tempo real, haptics e cálculo de distância de GPS (Haversine).
  6. **Bundles:** Verificação do status de injeção dos bundles temáticos (`persistence`, `networking`, `presentation`, `hardware`).
  7. **⚡ Avançado:** Testes interativos de enfileiramento na Outbox Queue (offline-first), Cache L1/L2 com TTL, canais corporativos de notificação, status de NFC e Bluetooth Low Energy (BLE), e rollout percentual determinístico de Feature Flags.

---

## 🚀 Como executar o Showcase

Basta compilar e instalar na máquina conectada ou emulador:

```bash
./gradlew :app:installDebug
```
