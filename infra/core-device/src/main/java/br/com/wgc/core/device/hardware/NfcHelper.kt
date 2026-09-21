package br.com.wgc.core.device.hardware

import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import java.nio.charset.Charset

/**
 * Utilitário corporativo para detecção de suporte e leitura de mensagens NFC (Near Field Communication).
 *
 * @property context Contexto de aplicação.
 */
class NfcHelper(
    private val context: Context,
) {
    private val nfcAdapter: NfcAdapter? by lazy { NfcAdapter.getDefaultAdapter(context) }

    fun isNfcSupported(): Boolean = nfcAdapter != null

    fun isNfcEnabled(): Boolean = nfcAdapter?.isEnabled == true

    fun parseNdefPayload(record: NdefRecord): String {
        val payload = record.payload
        if (payload.isEmpty()) return ""
        val languageCodeLength = payload[0].toInt() and 0x3F
        val textBytes = payload.copyOfRange(1 + languageCodeLength, payload.size)
        return String(textBytes, Charset.forName("UTF-8"))
    }

    fun parseNdefMessages(messages: Array<NdefMessage>): List<String> {
        val results = mutableListOf<String>()
        messages.forEach { message ->
            message.records.forEach { record ->
                val parsed = parseNdefPayload(record)
                if (parsed.isNotEmpty()) {
                    results.add(parsed)
                }
            }
        }
        return results
    }
}
