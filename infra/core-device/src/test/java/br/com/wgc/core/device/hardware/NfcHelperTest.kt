package br.com.wgc.core.device.hardware

import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class NfcHelperTest {
    private lateinit var helper: NfcHelper

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        helper = NfcHelper(context)
    }

    @Test
    fun `parseNdefPayload should extract valid text from NDEF record`() {
        val text = "WGC_SECURE_TOKEN"
        val record = NdefRecord.createTextRecord("pt", text)

        val parsed = helper.parseNdefPayload(record)
        assertEquals(text, parsed)
    }

    @Test
    fun `parseNdefMessages should collect text from multiple records`() {
        val msg1 = NdefMessage(arrayOf(NdefRecord.createTextRecord("pt", "TOKEN_A")))
        val msg2 = NdefMessage(arrayOf(NdefRecord.createTextRecord("pt", "TOKEN_B")))

        val results = helper.parseNdefMessages(arrayOf(msg1, msg2))
        assertEquals(2, results.size)
        assertEquals("TOKEN_A", results[0])
        assertEquals("TOKEN_B", results[1])
    }

    @Test
    fun `isNfcSupported should return boolean without throwing`() {
        assertNotNull(helper.isNfcSupported())
    }
}
