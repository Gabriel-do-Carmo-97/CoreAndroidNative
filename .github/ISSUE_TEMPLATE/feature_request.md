---
name: Solicitação de Funcionalidade 💡
about: Sugira uma nova ideia ou evolução para a biblioteca
title: '[FEAT]: '
labels: ['enhancement']
assignees: ''
---

## 💡 Descrição da Funcionalidade
Uma descrição clara do que você gostaria que a biblioteca fizesse.

## 🎯 Caso de Uso / Motivação
Qual problema ou necessidade dos aplicativos consumidores esta funcionalidade resolve?

## 📦 Módulo Proposto
- [ ] `infra:core-common`
- [ ] `infra:core-storage`
- [ ] `infra:core-database`
- [ ] `infra:core-network`
- [ ] `infra:core-analytics`
- [ ] `infra:core-device`
- [ ] `infra:core-location`
- [ ] `infra:core-camera`
- [ ] `infra:core-ui`
- [ ] `bundle` (Novo bundle ou ampliação de existente)

## 📐 Proposta de API (Interface / Assinatura)
```kotlin
// Exemplo de como a função ou classe pública seria chamada:
interface MinhaNovaCapacidade {
    fun executarAcao(): Flow<ResultState<String>>
}
```
