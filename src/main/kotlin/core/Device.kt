package core

import kotlinx.browser.window
import kotlinx.coroutines.await
import math.Matrix4
import math.Vector3
import org.khronos.webgl.WebGLBuffer
import utils.Glu
import utils.Glu.pack
import utils.WebGL2RenderingContext

class Device(val gl: WebGL2RenderingContext) {
    private val buffers = mutableMapOf<FloatArray, WebGLBuffer>()
    private lateinit var activeProgram: Program

    var material = Material.DEFAULT
        set(value) {
            field = value
            activeProgram.material = value
        }

    var transform = Matrix4.IDENTITY
        set(value) {
            field = value
            activeProgram.transform = field
        }

    var view = Matrix4.IDENTITY
        set(value) {
            field = value
            activeProgram.view = field
        }

    var projection = Matrix4.IDENTITY
        set(value) {
            field = value
            activeProgram.projection = field
        }

    suspend fun initialise() {
        val vertexSrc = window.fetch("shaders/vertex.glsl").await().text().await()
        val fragmentSrc = window.fetch("shaders/fragment.glsl").await().text().await()
        activeProgram = Program(gl, vertexSrc, fragmentSrc)
        activeProgram.use()
        activeProgram.ambientIntensity = 0.2F
        gl.enable(gl.DEPTH_TEST)
        gl.enable(gl.CULL_FACE)
    }

    fun resize() {
        val canvas = gl.canvas
        val displayWidth = canvas.clientWidth
        val displayHeight = canvas.clientHeight
        if (canvas.width != displayWidth || canvas.height != displayHeight) {
            canvas.width = displayWidth
            canvas.height = displayHeight
            gl.viewport(0, 0, canvas.width, canvas.height)

        }
    }

    fun moveLightTo(position: Vector3) {
        activeProgram.lightPosition = position
    }

    fun clear(color: Color) {
        gl.clearColor(color.red, color.grn, color.blu, 1F)
        gl.clear(gl.COLOR_BUFFER_BIT or gl.DEPTH_BUFFER_BIT)
    }

    fun draw(data: FloatArray, count: Int) {
        activeProgram.bindVertexBuffer(buffers.getOrPut(data, { Glu.createVertexBuffer(gl, data.pack()) }))
        gl.drawArrays(gl.TRIANGLES, 0, count)
    }
}
