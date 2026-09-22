package br.com.wgc.core.device.security

import android.os.Debug
import java.io.File

/**
 * Relatório consolidado de integridade e antifraude do ambiente de execução.
 *
 * @property isFridaDetected Indica presença de binários, portas ou bibliotecas dinâmicas do Frida.
 * @property isHookingDetected Indica injeção de frameworks de instrumentação dinâmica (Xposed, Substrate).
 * @property isDebuggerAttached Indica depurador ativo anexado ao processo da aplicação.
 * @property isCompromised `true` se qualquer indicador de violação foi confirmado.
 */
data class IntegrityReport(
    val isFridaDetected: Boolean,
    val isHookingDetected: Boolean,
    val isDebuggerAttached: Boolean,
    val isCompromised: Boolean,
)

/**
 * Utilitário corporativo para detecção de adulteração (anti-tampering), hooking dinâmico e depuração em runtime.
 */
class SecurityIntegrityHelper(
    private val procMapsPath: String = DEFAULT_PROC_MAPS_PATH,
) {
    /**
     * Executa a auditoria completa de integridade e retorna o [IntegrityReport].
     */
    fun checkSecurityIntegrity(): IntegrityReport {
        val frida = isFridaDetected()
        val hooking = isHookingDetected()
        val debugger = isDebuggerAttached()
        val compromised = frida || hooking || debugger

        return IntegrityReport(
            isFridaDetected = frida,
            isHookingDetected = hooking,
            isDebuggerAttached = debugger,
            isCompromised = compromised,
        )
    }

    /**
     * Verifica se há bibliotecas dinâmicas do Frida ou agentes injetados no mapa de memória do processo.
     */
    fun isFridaDetected(): Boolean {
        val mapsContent = readProcMaps()
        return KNOWN_FRIDA_LIBS.any { lib -> mapsContent.contains(lib, ignoreCase = true) }
    }

    /**
     * Verifica a injeção de frameworks conhecidos de hooking (Xposed, Cydia Substrate, etc.).
     */
    fun isHookingDetected(): Boolean {
        val mapsContent = readProcMaps()
        return KNOWN_HOOKING_LIBS.any { lib -> mapsContent.contains(lib, ignoreCase = true) }
    }

    /**
     * Verifica se há um depurador ativo ou aguardando conexão no processo.
     */
    fun isDebuggerAttached(): Boolean {
        return runCatching {
            Debug.isDebuggerConnected() || Debug.waitingForDebugger()
        }.getOrDefault(false)
    }

    private fun readProcMaps(): String {
        return runCatching {
            val file = File(procMapsPath)
            if (file.exists() && file.canRead()) {
                file.readText()
            } else {
                ""
            }
        }.getOrDefault("")
    }

    companion object {
        private const val DEFAULT_PROC_MAPS_PATH = "/proc/self/maps"
        private val KNOWN_FRIDA_LIBS = listOf("frida-agent", "frida-gadget", "libfrida", "gum-js-loop")
        private val KNOWN_HOOKING_LIBS = listOf("xposed", "substrate", "edxposed", "sandhook", "lsposed")
    }
}
