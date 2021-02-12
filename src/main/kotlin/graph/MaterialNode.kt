package graph

import core.Device
import core.Material

class MaterialNode(private val material: Material) : GroupNode() {
    override fun render(device: Device): Boolean {
        device.material = material
        return true
    }
}

