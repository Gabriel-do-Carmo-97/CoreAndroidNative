# Módulo Aplicativo `:app` 📱

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)]()
[![Min SDK](https://img.shields.io/badge/minSdk-29-blue.svg)]()
[![Target SDK](https://img.shields.io/badge/targetSdk-37-blue.svg)]()

O **`:app`** é o módulo principal executável da aplicação Android. Ele serve como ponto de entrada (`Application`, Activities) e vitrine de demonstração/exemplo de integração de todos os submódulos de infraestrutura (`:infra:core`, `:infra:core-ui`, `:infra:core-storage`, etc.).

---

## 🛠️ O que contém este módulo?

- **[`CoreAndroidNativeApp`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/app/src/main/java/br/com/wgc/coreandroidnative/CoreAndroidNativeApp.kt)**: Classe `@HiltAndroidApp` responsável por inicializar a injeção de dependências global via Dagger/Hilt.
- **[`MainActivity`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/app/src/main/java/br/com/wgc/coreandroidnative/MainActivity.kt)**: Atividade principal configurada com Jetpack Compose e Material 3, demonstrando fluxos de UI, componentes reutilizáveis, leituras de armazenamento seguro, monitoramento de rede e captura de câmera/QR Code.
- **Injeção de Dependências & Hilt Modules**: Configurações de escopo de aplicativo para conectar os recursos dos módulos de infraestrutura.

---

## 🚀 Quando deve ser usado?

- Exclusivamente para compilar o pacote final instalável (`.apk` ou `.aab`) da aplicação.
- Para testes de instrumentação end-to-end (E2E) e UI tests (`androidTest`).
