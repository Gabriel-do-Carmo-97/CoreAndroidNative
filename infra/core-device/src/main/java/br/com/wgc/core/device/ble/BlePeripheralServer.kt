package br.com.wgc.core.device.ble

import java.util.UUID

/**
 * Advertisement packet configuration for BLE peripheral mode.
 *
 * @property serviceUuid Primary GATT service UUID broadcasted.
 * @property deviceName Local advertised name.
 * @property includeTxPowerLevel Include transmission power level in advertising PDU.
 */
data class BlePeripheralConfig(
    val serviceUuid: UUID,
    val deviceName: String,
    val includeTxPowerLevel: Boolean = true,
)

/**
 * State of the BLE peripheral advertising engine.
 */
enum class BlePeripheralState {
    IDLE,
    ADVERTISING,
    CONNECTED,
    STOPPED,
    ERROR,
}

/**
 * Enterprise contract for Android Bluetooth Low Energy Peripheral Mode (GATT Server & Advertising).
 */
interface BlePeripheralServer {
    /**
     * Current lifecycle state of the BLE peripheral.
     */
    val state: BlePeripheralState

    /**
     * Starts advertising the GATT service specified in [config].
     */
    fun startAdvertising(config: BlePeripheralConfig): Boolean

    /**
     * Stops advertising and disconnects any connected central devices.
     */
    fun stopAdvertising()
}
