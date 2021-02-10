package graph

import core.Device
import math.Matrix4

abstract class Node(transform: Matrix4 = Matrix4.IDENTITY) {
    private var parent: BranchNode? = null
    protected var worldTransform = Matrix4.IDENTITY
    var localTransform = transform

    fun isParent(node: Node) = node == parent

    fun setParent(node: BranchNode?) {
        parent?.remove(this)
        parent = node
        parent?.add(this)
    }

    open fun traverseDown(visitor: (Node) -> Boolean) {
        visitor(this)
    }

    open fun traverseUp(visitor: (Node) -> Unit) {
        visitor(this)
    }

    open fun updateTransform() {
        worldTransform = (parent?.worldTransform ?: Matrix4.IDENTITY) * localTransform
    }

    open fun update(seconds: Double) = Unit

    open fun render(device: Device) = Unit
}
