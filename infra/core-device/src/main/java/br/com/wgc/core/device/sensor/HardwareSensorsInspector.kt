package br.com.wgc.core.device.sensor

import android.hardware.Sensor
import android.hardware.SensorManager

/**
 * Diagnostic record of an onboard hardware sensor.
 *
 * @property name Sensor marketing identifier.
 * @property vendor Chipset vendor.
 * @property type Sensor type constant.
 * @property powerMilliAmps Current draw in mA.
 */
data class HardwareSensorInfo(
    val name: String,
    val vendor: String,
    val type: Int,
    val powerMilliAmps: Float,
)

/**
 * Inspector discovering and classifying all hardware sensors present on the device.
 */
class HardwareSensorsInspector(
    private val sensorManager: SensorManager?,
) {
    /**
     * Lists all hardware sensors present on the device.
     */
    fun listSensors(): List<HardwareSensorInfo> {
        val sensors = sensorManager?.getSensorList(Sensor.TYPE_ALL) ?: emptyList()
        return sensors.map {
            HardwareSensorInfo(
                name = it.name,
                vendor = it.vendor,
                type = it.type,
                powerMilliAmps = it.power,
            )
        }
    }

    /**
     * Checks if a specific sensor type is present on the device.
     */
    fun hasSensor(sensorType: Int): Boolean {
        return sensorManager?.getDefaultSensor(sensorType) != null
    }
}
