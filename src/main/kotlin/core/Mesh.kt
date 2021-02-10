package core

class Mesh(private val buffer: FloatArray, private val count: Int) {
    fun render(device: Device) = device.draw(buffer, count)
}
