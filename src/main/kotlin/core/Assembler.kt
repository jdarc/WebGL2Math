package core

import math.Tuple2
import math.Vector3

const val ELEMENTS_PER_VERTEX = 3 + 3 + 2
const val VERTICES_PER_FACE = 3

private class Face(val v: IntArray, val vn: IntArray, val vt: IntArray, val material: String)

class Assembler {
    private val vertices = mutableListOf<Vector3>()
    private val normals = mutableListOf<Vector3>()
    private val texCoord = mutableListOf<Tuple2>()
    private val faces = mutableListOf<Face>()
    private val materials = mutableMapOf<String, Material>()
    private var materialName = ""

    fun addMaterial(material: Material) {
        materials[material.name] = material
    }

    fun useMaterial(name: String) {
        materialName = name
    }

    fun addVertex(x: Float, y: Float, z: Float) {
        vertices.add(Vector3(x, y, z))
    }

    fun addNormal(x: Float, y: Float, z: Float) {
        normals.add(Vector3(x, y, z))
    }

    fun addTextureCoordinate(u: Float, v: Float) {
        texCoord.add(Tuple2(u, v))
    }

    fun addFace(v: IntArray, vn: IntArray, vt: IntArray) {
        faces.add(Face(v, vn, vt, materialName))
    }

    fun compile(): Model {
        val groups = mutableMapOf<Material, Mesh>()
        val materialBuckets = faces.groupBy { it.material }.toMap()
        for (bucket in materialBuckets) {
            val faces = bucket.value
            val buffer = FloatArray(faces.size * VERTICES_PER_FACE * ELEMENTS_PER_VERTEX)
            var offset = 0
            faces.forEach { face ->
                (pack(face, 0) + pack(face, 1) + pack(face, 2)).copyInto(buffer, offset)
                offset += VERTICES_PER_FACE * ELEMENTS_PER_VERTEX
            }
            groups[materials[bucket.key]!!] = Mesh(buffer, faces.size * VERTICES_PER_FACE)
        }
        return Model(groups)
    }

    private fun pack(face: Face, index: Int): FloatArray {
        val v = vertices[face.v[index]]
        val vn = normals[face.vn[index]]
        val vt = texCoord[face.vt[index]]
        return floatArrayOf(v.x, v.y, v.z, vn.x, vn.y, vn.z, vt.x, vt.y)
    }
}
