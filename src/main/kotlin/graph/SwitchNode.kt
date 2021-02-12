package graph

import core.Device

class SwitchNode(initialValue: Boolean = true) : GroupNode() {
    var on = initialValue

    override fun update(seconds: Double) = on

    override fun render(device: Device) = on
}
