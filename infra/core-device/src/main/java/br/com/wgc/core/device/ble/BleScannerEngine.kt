package br.com.wgc.core.device.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Dispositivo Bluetooth Low Energy descoberto durante o escaneamento.
 */
data class DiscoveredBleDevice(
    val name: String?,
    val address: String,
    val rssi: Int,
    val scanRecordBytes: ByteArray?,
)

/**
 * Motor reativo de escaneamento de periféricos Bluetooth Low Energy (BLE).
 * Converte os callbacks do [BluetoothLeScanner] em um [Flow] assíncrono seguro.
 */
class BleScannerEngine(
    private val bluetoothAdapter: BluetoothAdapter?,
) {
    val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    /**
     * Inicia o scan contínuo de dispositivos BLE e emite os resultados via Flow.
     * Cancela o scan do hardware automaticamente quando o Flow é cancelado ou fechado.
     *
     * @param serviceUuid UUID opcional do serviço GATT a ser filtrado.
     */
    @SuppressLint("MissingPermission")
    fun scanBleDevices(serviceUuid: String? = null): Flow<DiscoveredBleDevice> =
        callbackFlow {
            val scanner = bluetoothAdapter?.bluetoothLeScanner
            if (scanner == null || !isBluetoothEnabled) {
                close(IllegalStateException("Bluetooth LE is not available or disabled"))
                return@callbackFlow
            }

            val filters = mutableListOf<ScanFilter>()
            if (!serviceUuid.isNullOrBlank()) {
                filters.add(
                    ScanFilter
                        .Builder()
                        .setServiceUuid(ParcelUuid.fromString(serviceUuid))
                        .build(),
                )
            }

            val settings =
                ScanSettings
                    .Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build()

            val callback =
                object : ScanCallback() {
                    override fun onScanResult(
                        callbackType: Int,
                        result: ScanResult?,
                    ) {
                        result?.let {
                            val device =
                                DiscoveredBleDevice(
                                    name = it.device?.name,
                                    address = it.device?.address ?: "UNKNOWN",
                                    rssi = it.rssi,
                                    scanRecordBytes = it.scanRecord?.bytes,
                                )
                            trySend(device)
                        }
                    }

                    override fun onScanFailed(errorCode: Int) {
                        close(RuntimeException("BLE Scan failed with errorCode: $errorCode"))
                    }
                }

            try {
                scanner.startScan(filters, settings, callback)
            } catch (e: Exception) {
                close(e)
                return@callbackFlow
            }

            awaitClose {
                try {
                    scanner.stopScan(callback)
                } catch (_: Exception) {
                    // Ignora se o adapter já estiver desligado
                }
            }
        }
}
