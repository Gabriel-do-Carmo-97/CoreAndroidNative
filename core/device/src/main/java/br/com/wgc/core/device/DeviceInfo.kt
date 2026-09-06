package br.com.wgc.core.device

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * Contrato de abstração para obtenção de metadados de hardware, sistema operacional e aplicação.
 *
 * Facilita a auditoria em relatórios de telemetria, diagnóstico de bugs e testes unitários via mock.
 */
interface DeviceInfo {

    /** Nome da versão da aplicação (ex: "1.0.0"). */
    val versionName: String

    /** Código numérico da versão da aplicação (ex: 42L). */
    val versionCode: Long

    /** Nível da API do Android em execução (ex: 34). */
    val sdkInt: Int

    /** Nome do modelo de hardware do dispositivo (ex: "Pixel 8 Pro"). */
    val deviceModel: String

    /** Fabricante do dispositivo (ex: "Google", "Samsung"). */
    val manufacturer: String

    /** Identifica se a aplicação está executando em um emulador Android. */
    val isEmulator: Boolean
}

/**
 * Implementação padrão de [DeviceInfo] que consulta as propriedades nativas do [Build]
 * e o gerenciador de pacotes do Android.
 *
 * @property context Contexto da aplicação utilizado para consultar o [PackageManager].
 */
class DefaultDeviceInfo(private val context: Context) : DeviceInfo {

    override val versionName: String by lazy {
        try {
            val packageInfo = getPackageInfo()
            packageInfo.versionName ?: "unknown"
        } catch (@Suppress("SwallowedException", "TooGenericExceptionCaught") e: Exception) {
            "unknown"
        }
    }

    override val versionCode: Long by lazy {
        try {
            val packageInfo = getPackageInfo()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (@Suppress("SwallowedException", "TooGenericExceptionCaught") e: Exception) {
            -1L
        }
    }

    override val sdkInt: Int get() = Build.VERSION.SDK_INT

    override val deviceModel: String get() = Build.MODEL

    override val manufacturer: String get() = Build.MANUFACTURER

    override val isEmulator: Boolean by lazy {
        (Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86") ||
            Build.MANUFACTURER.contains("Genymotion") ||
            Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
            "google_sdk" == Build.PRODUCT)
    }

    private fun getPackageInfo() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.PackageInfoFlags.of(0)
        )
    } else {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(context.packageName, 0)
    }
}
