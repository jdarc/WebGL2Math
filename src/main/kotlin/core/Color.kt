package core

@Suppress("MemberVisibilityCanBePrivate", "unused")
data class Color internal constructor(val red: Float, val grn: Float, val blu: Float) {
    companion object {
        val BLACK = create(0x000000)
        val WHITE = create(0xFFFFFF)

        fun create(rgb: Int): Color {
            val r = 0xFF and rgb.shr(16)
            val g = 0xFF and rgb.shr(8)
            val b = 0xFF and rgb
            return create(r / 255F, g / 255F, b / 255F)
        }

        fun create(r: Float, g: Float, b: Float) = Color(r.coerceIn(0F, 1F), g.coerceIn(0F, 1F), b.coerceIn(0F, 1F))
    }
}
