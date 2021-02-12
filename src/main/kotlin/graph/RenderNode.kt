package graph

import core.Device
import math.Matrix4

class RenderNode(private val renderable: Renderable, transform: Matrix4 = Matrix4.IDENTITY) : Node(transform) {
    override fun render(device: Device): Boolean {
        device.transform = worldTransform
        renderable.render(device)
        return true
    }
}
