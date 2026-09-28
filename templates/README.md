# WGC Core Android Native - Live Templates para Android Studio

Este diretório contém os Live Templates e Code Snippets oficiais para acelerar o desenvolvimento de aplicações consumidoras do **CoreAndroidNative**.

## 🚀 Snippets Disponíveis

| Abreviação | Descrição | Exemplo de Saída |
| :--- | :--- | :--- |
| `coreRepo` | Cria esqueleto de repositório padronizado com ResultWrapper e injeção do Core | `class ExampleRepository @Inject constructor(...)` |
| `coreLogger` | Instancia e dispara logs estruturados com tag automática | `CoreLogger.withTag("Tag").d { "..." }` |
| `coreSync` | Cria bloco de sincronização bidirecional com resolução LWW | `BidirectionalSyncEngine(syncManager, resolver).sync(...)` |
| `coreMask` | Aplica mascaramento LGPD automático em CPF, Cartões ou E-mails | `SensitiveDataMasker.maskCpf(rawCpf)` |
| `coreSecureMem` | Aloca bloco de memória volátil com auto-destruição / zeroização (`SecureByteArray`) | `SecureByteArray(32).use { ... }` |
| `coreTest` | Cria teste unitário com Coroutines, FakeCoreLogger e gerador de massa de testes (CPFs válidos) | `@Test fun should_succeed...` |

---

## 🛠️ Como Instalar no Android Studio

### Opção 1: Cópia Direta de Arquivo
Copie o arquivo `AndroidStudio_Core_LiveTemplates.xml` para a pasta de templates do seu Android Studio:
- **Windows:** `%APPDATA%\Google\AndroidStudio<versao>\templates\`
- **macOS:** `~/Library/Application Support/Google/AndroidStudio<versao>/templates/`
- **Linux:** `~/.config/Google/AndroidStudio<versao>/templates/`

Em seguida, reinicie o Android Studio.

### Opção 2: Importação Manual
1. Abra o Android Studio.
2. Vá em **File** -> **Settings** (ou **Preferences** no macOS) -> **Editor** -> **Live Templates**.
3. Clique no ícone de engrenagem ou `+` e selecione **Import Settings...**
4. Aponte para `templates/AndroidStudio_Core_LiveTemplates.xml` e clique em **Apply**.
