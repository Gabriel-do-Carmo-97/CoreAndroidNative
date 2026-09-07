package br.com.wgc.core.network

/**
 * Contrato corporativo para obtenção e renovação automática de tokens de autenticação (OAuth2 / JWT).
 */
interface TokenProvider {

    /**
     * Retorna o token de acesso atual retido em armazenamento seguro.
     *
     * @return O token em formato texto ou `null` se o usuário não estiver autenticado.
     */
    suspend fun getAccessToken(): String?

    /**
     * Executa a requisição remota assíncrona para renovação do token de acesso expirado.
     *
     * @return O novo token gerado com sucesso, ou `null` caso a sessão tenha expirado definitivamente.
     */
    suspend fun refreshToken(): String?

    /**
     * Callback invocado quando a renovação falha de forma irrecuperável,
     * permitindo que a aplicação consumidora limpe a sessão e redirecione para a tela de autenticação.
     */
    suspend fun onSessionExpired()
}
