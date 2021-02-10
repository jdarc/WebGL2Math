package math

object Scalar {
    const val PI = 3.1415926535F
    const val EPSILON = 0.000001F

    fun equals(a: Float, b: Float, epsilon: Float = EPSILON) = !(a - b).isNaN() && kotlin.math.abs(a - b) <= epsilon
}
