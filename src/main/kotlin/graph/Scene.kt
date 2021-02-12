package graph

import core.Camera
import core.Color
import core.Device

@Suppress("MemberVisibilityCanBePrivate")
class Scene {

    val root = GroupNode()

    var background = Color.BLACK

    fun update(seconds: Double) {
        root.traverseDown {
            it.update(seconds)
            it.updateTransform()
            true
        }
    }

    fun render(camera: Camera, device: Device) {
        device.resize()
        camera.aspectRatio = device.gl.drawingBufferWidth.toFloat() / device.gl.drawingBufferHeight.toFloat()
        device.view = camera.view
        device.projection = camera.projection
        device.clear(background)
        root.traverseDown { it.render(device) }
    }
}
