package br.com.wgc.core.device.hardware

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Representa um periférico Bluetooth Low Energy (BLE) descoberto durante a varredura.
 *
 * @property name Nome do periférico anunciado.
 * @property address Endereço MAC do dispositivo.
 * @property rssi Potência do sinal recebido em dBm.
 */
data class BleDeviceFound(
    val name: String?,
    val address: String,
    val rssi: Int,
)

/**
 * Gerenciador reativo para varredura de periféricos Bluetooth Low Energy (BLE) via [Flow].
 *
 * @property context Contexto de aplicação.
 */
class BleScannerHelper(
    private val context: Context,
) {
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        manager?.adapter
    }

    fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    @SuppressLint("MissingPermission")
    fun scanDevices(): Flow<BleDeviceFound> =
        callbackFlow {
            val scanner = bluetoothAdapter?.bluetoothLeScanner
            if (scanner == null) {
                close(IllegalStateException("Bluetooth LE Scanner indisponível no dispositivo."))
                return@callbackFlow
            }

            val callback =
                object : ScanCallback() {
                    override fun onScanResult(
                        callbackType: Int,
                        result: ScanResult,
                    ) {
                        val device = result.device
                        trySend(
                            BleDeviceFound(
                                name = device.name,
                                address = device.address,
                                rssi = result.rssi,
                            ),
                        )
                    }

                    override fun onScanFailed(errorCode: Int) {
                        close(IllegalStateException("Varredura BLE falhou com código: $errorCode"))
                    }
                }

            scanner.startScan(callback)

            awaitClose {
                scanner.stopScan(callback)
            }
        }
}
