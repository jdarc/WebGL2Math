package graph

import math.Matrix4
import math.Vector3

class AxisRotationNode(val axis: Vector3, val speed: Float) : GroupNode(Matrix4.IDENTITY) {
    private var angle = 0.0

    override fun update(seconds: Double): Boolean {
        angle += speed * seconds
        localTransform = Matrix4.createFromAxisAngle(axis, angle.toFloat())
        return true
    }
}
