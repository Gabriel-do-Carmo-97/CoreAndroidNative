package br.com.wgc.core.device.sensor

/**
 * Estimativa de orientação espacial tridimensional fundindo dados de acelerômetro e giroscópio.
 *
 * @property pitch Ângulo de inclinação longitudinal (em graus).
 * @property roll Ângulo de rotação lateral (em graus).
 */
data class OrientationAngles(
    val pitch: Float,
    val roll: Float,
)

/**
 * Motor de fusão sensorial corporativo baseado em Filtro Complementar.
 * Elimina o desvio acumulado (drift) do giroscópio utilizando a gravidade absoluta do acelerômetro
 * e filtra os ruídos mecânicos de alta frequência do acelerômetro.
 */
class SensorFusionEngine(
    private val alpha: Float = 0.98f, // 98% peso no giroscópio, 2% no acelerômetro
) {
    private var pitch = 0f
    private var roll = 0f
    private var lastTimestampNs: Long = 0L

    fun update(
        accelX: Float,
        accelY: Float,
        accelZ: Float,
        gyroX: Float,
        gyroY: Float,
        timestampNs: Long,
    ): OrientationAngles {
        if (lastTimestampNs == 0L) {
            lastTimestampNs = timestampNs
            pitch = computePitch(accelX, accelY, accelZ)
            roll = computeRoll(accelX, accelZ)
            return OrientationAngles(pitch, roll)
        }

        val dt = (timestampNs - lastTimestampNs) / 1_000_000_000f
        lastTimestampNs = timestampNs

        // Integração do giroscópio (rad/s convertidos para deg/s)
        val gyroPitchDelta = Math.toDegrees(gyroX.toDouble()).toFloat() * dt
        val gyroRollDelta = Math.toDegrees(gyroY.toDouble()).toFloat() * dt

        // Cálculo dos ângulos absolutos do acelerômetro
        val accelPitch = computePitch(accelX, accelY, accelZ)
        val accelRoll = computeRoll(accelX, accelZ)

        // Filtro Complementar
        pitch = alpha * (pitch + gyroPitchDelta) + (1f - alpha) * accelPitch
        roll = alpha * (roll + gyroRollDelta) + (1f - alpha) * accelRoll

        return OrientationAngles(pitch, roll)
    }

    fun reset() {
        pitch = 0f
        roll = 0f
        lastTimestampNs = 0L
    }

    private fun computePitch(
        x: Float,
        y: Float,
        z: Float,
    ): Float {
        val magnitude = kotlin.math.sqrt((x * x + z * z).toDouble())
        return Math.toDegrees(kotlin.math.atan2(y.toDouble(), magnitude)).toFloat()
    }

    private fun computeRoll(
        x: Float,
        z: Float,
    ): Float {
        return Math.toDegrees(kotlin.math.atan2(-x.toDouble(), z.toDouble())).toFloat()
    }
}
