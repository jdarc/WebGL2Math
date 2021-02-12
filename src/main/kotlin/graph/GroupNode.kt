package graph

import math.Matrix4

open class GroupNode(transform: Matrix4 = Matrix4.IDENTITY) : Node(transform) {
    private val children = mutableSetOf<Node>()

    fun add(vararg nodes: Node): GroupNode {
        nodes.forEach {
            if (it != this && !children.contains(it)) {
                children.add(it)
                it.setParent(this)
            }
        }
        return this
    }

    fun remove(vararg nodes: Node): GroupNode {
        nodes.forEach {
            if (children.contains(it)) {
                children.remove(it)
                it.setParent(null)
            }
        }
        return this
    }

    override fun traverseDown(visitor: (Node) -> Boolean) {
        if (!visitor(this)) return
        children.forEach { it.traverseDown(visitor) }
    }

    override fun traverseUp(visitor: (Node) -> Unit) {
        children.forEach { it.traverseUp(visitor) }
        visitor(this)
    }
}
