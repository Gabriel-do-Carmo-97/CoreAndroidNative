# Política de Segurança — CoreAndroidNative 🔒

O projeto **CoreAndroidNative** é uma biblioteca de infraestrutura corporativa utilizada por aplicações Android críticas. Levamos a segurança e a integridade de dados (especialmente criptografia, tokens, credenciais e conformidade com LGPD/GDPR) com a máxima seriedade.

---

## 🛡️ Versões com Suporte a Correções de Segurança

Apenas as versões mais recentes das ramificações ativas recebem atualizações de patches de segurança:

| Versão | Suporte a Patches de Segurança |
|:---|:---|
| `v0.0.x` (Atual) |  Sim (Versão ativa em desenvolvimento) |
| `< v0.0.30` | ❌ Não (Recomenda-se atualização imediata) |

---

## 🚨 Como Reportar uma Vulnerabilidade de Segurança

**Por favor, NÃO abra uma Issue pública no GitHub para reportar vulnerabilidades de segurança.**

Se você identificou uma falha de segurança, vulnerabilidade em algoritmos criptográficos, vazamento de PII ou brecha em permissões:

1. Envie um e-mail confidencial detalhado para:
   📧 **`gabriel.desenvolvedor.97@gmail.com`**
2. No assunto do e-mail, utilize:
   `[SECURITY VULNERABILITY] CoreAndroidNative - <Resumo Breve>`
3. No corpo da mensagem, inclua:
   - **Descrição detalhada:** Explicação do vetor de ataque ou comportamento anômalo.
   - **Módulos afetados:** (ex: `core-storage`, `core-database`, `core-network`).
   - **Passos para reprodução:** Código de exemplo ou PoC (Proof of Concept).
   - **Impacto potencial:** Avaliação de severidade (ex: vazamento de chaves, bypass de criptografia, crash de aplicação).

---

## ⏱️ Nosso Compromisso e SLA de Resposta

* **Confirmação inicial de recebimento:** Em até **24 a 48 horas**.
* **Avaliação de severidade e triagem:** Em até **72 horas**.
* **Lançamento de patch corretivo:** Prioridade máxima com hotfix imediato publicado no GitHub Packages.

Agradecemos e reconhecemos a colaboração responsável da comunidade e de pesquisadores de segurança.
