package core

import graph.Renderable

class Mesh(private val buffer: FloatArray, private val count: Int) : Renderable {
    override fun render(device: Device) = device.draw(buffer, count)
}
