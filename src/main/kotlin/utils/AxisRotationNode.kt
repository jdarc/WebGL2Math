package utils

import graph.BranchNode
import math.Matrix4
import math.Vector3

class AxisRotationNode(private val axis: Vector3, private val speed: Float) : BranchNode(Matrix4.IDENTITY) {
    var angle = 0.0

    override fun update(seconds: Double) {
        angle += speed * seconds
        localTransform = Matrix4.createFromAxisAngle(axis, angle.toFloat())
    }
}
