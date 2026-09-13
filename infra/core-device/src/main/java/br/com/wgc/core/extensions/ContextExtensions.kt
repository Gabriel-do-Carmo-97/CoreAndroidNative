package br.com.wgc.core.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat

/**
 * Verifica se a aplicação possui uma determinada permissão concedida pelo usuário ou sistema.
 *
 * @param permission Nome da permissão (ex: [android.Manifest.permission.POST_NOTIFICATIONS]).
 * @return `true` se a permissão estiver concedida ([PackageManager.PERMISSION_GRANTED]), `false` caso contrário.
 */
fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission,
    ) == PackageManager.PERMISSION_GRANTED

/**
 * Exibe rapidamente uma notificação Toast na tela.
 *
 * @param message Texto da mensagem a ser exibida.
 * @param duration Duração da exibição ([Toast.LENGTH_SHORT] ou [Toast.LENGTH_LONG]). Padrão: [Toast.LENGTH_SHORT].
 */
fun Context.showToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT,
) {
    Toast.makeText(this, message, duration).show()
}
