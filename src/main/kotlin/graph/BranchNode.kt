package graph

import math.Matrix4

open class BranchNode(transform: Matrix4 = Matrix4.IDENTITY) : Node(transform) {
    private val children = mutableSetOf<Node>()

    fun add(node: Node): BranchNode {
        if (node != this && !children.contains(node)) {
            children.add(node)
            node.setParent(this)
        }
        return this
    }

    fun remove(node: Node): BranchNode {
        if (children.contains(node)) {
            children.remove(node)
            node.setParent(null)
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
