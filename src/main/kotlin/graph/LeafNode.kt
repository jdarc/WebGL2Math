package graph

import core.Device

class LeafNode(private val renderable: Renderable) : Node() {
    override fun render(device: Device) {
        device.transform = worldTransform
        renderable.render(device)
    }
}
