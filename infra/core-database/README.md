# Submódulo `:infra:core-database` 💾

[![Artefato](https://img.shields.io/badge/Artifact-br.com.wgc:core--database-blue.svg)](https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative)
![Min SDK](https://img.shields.io/badge/minSdk-29-green.svg)

O **`:infra:core-database`** fornece a infraestrutura de persistência relacional local utilizando **Room Database**, DAOs genéricos reutilizáveis, criptografia avançada de banco de dados via **SQLCipher** e suporte a paginação remota com **Paging 3**.

---

## 🛠️ O que contém este submódulo?

### 1. DAOs & Conversores (`br.com.wgc.core.database.dao` / `converters`)
- [`BaseDao<T>`](src/main/java/br/com/wgc/core/database/dao/BaseDao.kt):
  - Interface genérica contendo operações comuns de banco de dados (`insert`, `update`, `delete`, `upsert`).
- [`RoomConverters`](src/main/java/br/com/wgc/core/database/converters/RoomConverters.kt):
  - Conversores de tipo do Room para datas (`Date` <-> `Long`), UUIDs, listas e JSONs.

### 2. Criptografia SQLCipher (`br.com.wgc.core.database.security`)
- [`DatabaseEncrypter`](src/main/java/br/com/wgc/core/database/security/DatabaseEncrypter.kt):
  - Fornece suporte à criptografia transparente do banco de dados SQLite usando SQLCipher e chaves protegidas pelo Android Keystore.

### 3. Paginação com Paging 3 e RemoteMediator (`br.com.wgc.core.database.paging`)
- [`BaseRemoteMediator<Key, Value>`](src/main/java/br/com/wgc/core/database/paging/BaseRemoteMediator.kt):
  - Classe abstrata de `RemoteMediator` para orquestrar paginação do Room com dados da API de forma padronizada.
  - Trata estratégias de carregamento `REFRESH`, `PREPEND` e `APPEND` sem vazamento de estado.
- [`RemoteKeyEntity`](src/main/java/br/com/wgc/core/database/paging/RemoteKeyEntity.kt):
  - Entidade de chave remota para armazenar cursores e tokens de página anterior (`prevKey`) e próxima (`nextKey`) por item.

---

## 📥 Como importar

```kotlin
dependencies {
    implementation("br.com.wgc:core-database:1.2.0")
}
```

---

## 🚀 Quando deve ser usado?

- Para armazenar dados relacionais locais sensíveis ou estruturados que exigem alta performance, consultas complexas, criptografia em repouso e suporte a listas paginadas offline-first.
