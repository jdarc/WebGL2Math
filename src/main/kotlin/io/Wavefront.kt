package io

import core.Assembler
import core.Color
import core.Material
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope

object Wavefront {

    suspend fun read(path: String, filename: String) = coroutineScope {
        val factory = Assembler()
        val directives = window.fetch("$path/$filename").await().text().await()
        directives.split('\n').filter { it.isNotEmpty() && it[0] != '#' }.forEach { line ->
            val fragments = line.trim().split(' ')
            when {
                fragments[0].equals("mtllib", true) -> readMaterials(path, fragments[1]).forEach { factory.addMaterial(it.value) }
                fragments[0].equals("usemtl", true) -> factory.useMaterial(fragments[1])
                fragments[0].equals("v", true) -> {
                    val x = fragments[1].trim().toFloat()
                    val y = fragments[2].trim().toFloat()
                    val z = fragments[3].trim().toFloat()
                    factory.addVertex(x, y, z)
                }
                fragments[0].equals("vn", true) -> {
                    val x = fragments[1].trim().toFloat()
                    val y = fragments[2].trim().toFloat()
                    val z = fragments[3].trim().toFloat()
                    factory.addNormal(x, y, z)
                }
                fragments[0].equals("vt", true) -> {
                    val u = fragments[1].trim().toFloat()
                    val v = fragments[2].trim().toFloat()
                    factory.addTextureCoordinate(u, v)
                }
                fragments[0].equals("f", true) -> {
                    val i0 = fragments[1].split("/").map { it.trim().toInt() - 1 }.toIntArray()
                    val i1 = fragments[2].split("/").map { it.trim().toInt() - 1 }.toIntArray()
                    val i2 = fragments[3].split("/").map { it.trim().toInt() - 1 }.toIntArray()
                    val vx = intArrayOf(i0[0], i1[0], i2[0])
                    val vt = intArrayOf(i0[1], i1[1], i2[1])
                    val vn = intArrayOf(i0[2], i1[2], i2[2])
                    factory.addFace(vx, vn, vt)
                }
            }
        }
        factory.compile()
    }

    private suspend fun readMaterials(path: String, filename: String) = coroutineScope {
        var currentMaterial = Material.DEFAULT
        val materials = mutableMapOf<String, Material>()
        val directives = window.fetch("$path/$filename").await().text().await()
        directives.split('\n').filter { it.isNotEmpty() && it[0] != '#' }.forEach { line ->
            when {
                line.startsWith("newmtl", true) -> {
                    val name = line.trim().split(' ')[1]
                    currentMaterial = Material(name)
                    materials[name] = currentMaterial
                }
                line.startsWith("ka", true) -> currentMaterial.ambientColor = parseColor(line)
                line.startsWith("kd", true) -> currentMaterial.diffuseColor = parseColor(line)
                line.startsWith("ks", true) -> currentMaterial.specularColor = parseColor(line)
                line.startsWith("ns", true) -> currentMaterial.shininess = line.split(' ')[1].trim().toFloat()
            }
        }
        materials
    }

    private fun parseColor(line: String): Color {
        val data = line.trim().split(' ')
        return Color.create(data[1].toFloat(), data[2].toFloat(), data[3].toFloat())
    }
}
