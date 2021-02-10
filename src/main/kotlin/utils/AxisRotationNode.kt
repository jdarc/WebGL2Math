package utils

import graph.BranchNode
import math.Matrix4
import math.Vector3

class AxisRotationNode(private val axis: Vector3, private val speed: Float): BranchNode(Matrix4.IDENTITY) {
    override fun update(seconds: Double) {
        localTransform = Matrix4.createFromAxisAngle(axis, (speed * seconds).toFloat())
    }
}
