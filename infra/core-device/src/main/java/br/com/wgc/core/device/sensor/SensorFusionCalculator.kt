package br.com.wgc.core.device.sensor

/**
 * Orientation azimuth, pitch, and roll calculation in degrees.
 *
 * @property azimuth Rotation around -Z axis (0 to 360 degrees).
 * @property pitch Rotation around X axis (-180 to 180 degrees).
 * @property roll Rotation around Y axis (-90 to 90 degrees).
 */
data class SpatialOrientation3D(
    val azimuth: Float,
    val pitch: Float,
    val roll: Float,
)

/**
 * Enterprise sensor fusion mathematics combining Accelerometer and Magnetometer into orientation.
 */
object SensorFusionCalculator {
    /**
     * Computes 3D orientation angles from accelerometer and geomagnetic vectors.
     */
    fun computeOrientation(
        gravity: FloatArray,
        geomagnetic: FloatArray,
    ): SpatialOrientation3D? {
        if (gravity.size < VECTOR_DIMENSIONS || geomagnetic.size < VECTOR_DIMENSIONS) return null

        val rotationMatrix = FloatArray(MATRIX_SIZE)
        val inclinationMatrix = FloatArray(MATRIX_SIZE)
        val success =
            android.hardware.SensorManager.getRotationMatrix(
                rotationMatrix,
                inclinationMatrix,
                gravity,
                geomagnetic,
            )

        if (!success) return null

        val orientation = FloatArray(VECTOR_DIMENSIONS)
        android.hardware.SensorManager.getOrientation(rotationMatrix, orientation)

        return SpatialOrientation3D(
            azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat(),
            pitch = Math.toDegrees(orientation[1].toDouble()).toFloat(),
            roll = Math.toDegrees(orientation[2].toDouble()).toFloat(),
        )
    }

    private const val VECTOR_DIMENSIONS = 3
    private const val MATRIX_SIZE = 9
}
