# Submódulo `:infra:core:database` 💾

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--database-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:infra:core:database`** fornece a infraestrutura de persistência relacional local utilizando **Room Database**, DAOs genéricos reutilizáveis e criptografia avançada de banco de dados via **SQLCipher**.

---

## 🛠️ O que contém este submódulo?

### 1. DAOs & Conversores (`br.com.wgc.core.database.dao` / `converters`)
- [`BaseDao<T>`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/infra/core-database/src/main/java/br/com/wgc/core/database/dao/BaseDao.kt):
  - Interface genérica contendo operações comuns de banco de dados (`insert`, `update`, `delete`, `upsert`).
- [`RoomConverters`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/infra/core-database/src/main/java/br/com/wgc/core/database/converters/RoomConverters.kt):
  - Conversores de tipo do Room para datas (`Date` <-> `Long`), UUIDs, listas e JSONs.

### 2. Criptografia SQLCipher (`br.com.wgc.core.database.security`)
- [`DatabaseEncrypter`](file:///C:/Users/gcarm/Documents/GitHub/CoreAndroidNative/infra/core-database/src/main/java/br/com/wgc/core/database/security/DatabaseEncrypter.kt):
  - Fornece suporte à criptografia transparente do banco de dados SQLite usando SQLCipher e chaves protegidas pelo Android Keystore.

---

## 📥 Como importar

```kotlin
dependencies {
    implementation("br.com.wgc:core-database:0.0.x")
}
```

---

## 🚀 Когда deve ser usado?

- Para armazenar dados relacionais locais sensíveis ou estruturados que exigem alta performance, consultas complexas e criptografia em repouso.
