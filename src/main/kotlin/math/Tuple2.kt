package math

data class Tuple2(val x: Float, val y: Float) {
    fun toArray(dst: FloatArray = FloatArray(2), offset: Int = 0): FloatArray {
        dst[offset + 0x0] = x; dst[offset + 0x1] = y
        return dst
    }
}
