package br.com.wgc.core.startup

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.startup.Initializer
import br.com.wgc.core.performance.StrictModeHelper

/**
 * Inicializador corporativo automático do CoreAndroidNative via Jetpack App Startup.
 *
 * Executado de forma transparente pelo sistema no lançamento do processo através do Manifest Merger,
 * eliminando a necessidade de inicializações manuais no `Application.onCreate()`.
 */
class CoreInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        StrictModeHelper.enableStrictModeInDebug(isDebug)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
