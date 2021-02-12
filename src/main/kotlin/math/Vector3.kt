package math

import kotlin.math.sqrt

@Suppress("unused")
data class Vector3(val x: Float, val y: Float, val z: Float) {

    constructor(s: Float) : this(s, s, s)

    val length get() = sqrt(dot(this, this))

    operator fun unaryMinus() = Vector3(-x, -y, -z)

    operator fun plus(rhs: Vector3) = Vector3(x + rhs.x, y + rhs.y, z + rhs.z)

    operator fun minus(rhs: Vector3) = Vector3(x - rhs.x, y - rhs.y, z - rhs.z)

    operator fun times(s: Float) = Vector3(x * s, y * s, z * s)

    operator fun div(s: Float) = Vector3(x / s, y / s, z / s)

    fun toArray(dst: FloatArray = FloatArray(3), offset: Int = 0): FloatArray {
        dst[offset + 0x0] = x; dst[offset + 0x1] = y; dst[offset + 0x2] = z
        return dst
    }

    companion object {

        val ONE = Vector3(1F, 1F, 1F)
        val ZERO = Vector3(0F, 0F, 0F)

        val UNIT_X = Vector3(1F, 0F, 0F)
        val UNIT_Y = Vector3(0F, 1F, 0F)
        val UNIT_Z = Vector3(0F, 0F, 1F)

        fun dot(lhs: Vector3, rhs: Vector3) = (lhs.x * rhs.x) + (lhs.y * rhs.y) + (lhs.z * rhs.z)

        fun normalize(v: Vector3) = v / v.length

        fun cross(lhs: Vector3, rhs: Vector3): Vector3 {
            val x = (lhs.y * rhs.z) - (lhs.z * rhs.y)
            val y = (lhs.z * rhs.x) - (lhs.x * rhs.z)
            val z = (lhs.x * rhs.y) - (lhs.y * rhs.x)
            return Vector3(x, y, z)
        }

        fun equals(lhs: Vector3, rhs: Vector3, epsilon: Float = Scalar.EPSILON) =
            Scalar.equals(lhs.x, rhs.x, epsilon) &&
            Scalar.equals(lhs.y, rhs.y, epsilon) &&
            Scalar.equals(lhs.z, rhs.z, epsilon)

        fun clamp(v: Vector3, min: Float, max: Float): Vector3 {
            val x = v.x.coerceIn(min, max)
            val y = v.y.coerceIn(min, max)
            val z = v.z.coerceIn(min, max)
            return Vector3(x, y, z)
        }
    }
}
