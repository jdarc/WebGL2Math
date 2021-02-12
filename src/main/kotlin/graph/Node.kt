package graph

import core.Device
import math.Matrix4

@Suppress("MemberVisibilityCanBePrivate")
abstract class Node(transform: Matrix4 = Matrix4.IDENTITY) {
    private var parent: GroupNode? = null
    protected var worldTransform = Matrix4.IDENTITY
    var localTransform = transform

    fun isParent(node: GroupNode?) = node == parent

    fun setParent(node: GroupNode?) {
        if (isParent(node)) return
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

    open fun update(seconds: Double) = true

    open fun render(device: Device) = true
}
