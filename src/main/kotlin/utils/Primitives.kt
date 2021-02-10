package utils

import core.Assembler
import core.Material
import core.Model

object Primitives {

    fun createCube(size: Float, material: Material): Model {
        val assembler = Assembler()
        assembler.addMaterial(material)
        assembler.useMaterial(material.name)

        val halfSize = size / 2F

        assembler.addVertex(-halfSize, halfSize, -halfSize)
        assembler.addVertex(halfSize, halfSize, -halfSize)
        assembler.addVertex(halfSize, -halfSize, -halfSize)
        assembler.addVertex(-halfSize, -halfSize, -halfSize)
        assembler.addVertex(halfSize, halfSize, halfSize)
        assembler.addVertex(-halfSize, halfSize, halfSize)
        assembler.addVertex(-halfSize, -halfSize, halfSize)
        assembler.addVertex(halfSize, -halfSize, halfSize)

        assembler.addNormal(0.0F, 0.0F, -1.0F)
        assembler.addNormal(1.0F, 0.0F, 0.0F)
        assembler.addNormal(0.0F, 0.0F, 1.0F)
        assembler.addNormal(-1.0F, 0.0F, 0.0F)
        assembler.addNormal(0.0F, 1.0F, 0.0F)
        assembler.addNormal(0.0F, -1.0F, 0.0F)

        assembler.addTextureCoordinate(0.0F, 0.0F)
        assembler.addTextureCoordinate(1.0F, 0.0F)
        assembler.addTextureCoordinate(1.0F, 1.0F)
        assembler.addTextureCoordinate(0.0F, 1.0F)

        assembler.addFace(intArrayOf(0, 1, 2), intArrayOf(0, 0, 0), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(2, 3, 0), intArrayOf(0, 0, 0), intArrayOf(2, 3, 0))
        assembler.addFace(intArrayOf(1, 4, 7), intArrayOf(1, 1, 1), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(7, 2, 1), intArrayOf(1, 1, 1), intArrayOf(2, 3, 0))
        assembler.addFace(intArrayOf(4, 5, 6), intArrayOf(2, 2, 2), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(6, 7, 4), intArrayOf(2, 2, 2), intArrayOf(2, 3, 0))
        assembler.addFace(intArrayOf(0, 3, 6), intArrayOf(3, 3, 3), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(6, 5, 0), intArrayOf(3, 3, 3), intArrayOf(2, 3, 0))
        assembler.addFace(intArrayOf(0, 5, 4), intArrayOf(4, 4, 4), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(4, 1, 0), intArrayOf(4, 4, 4), intArrayOf(2, 3, 0))
        assembler.addFace(intArrayOf(3, 2, 7), intArrayOf(5, 5, 5), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(7, 6, 3), intArrayOf(5, 5, 5), intArrayOf(2, 3, 0))

        return assembler.compile()
    }
}
