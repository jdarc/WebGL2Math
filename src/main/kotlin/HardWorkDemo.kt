import kotlinx.browser.window
import kotlinx.coroutines.await
import math.Matrix4
import math.Scalar
import math.Tuple2
import math.Vector3
import org.khronos.webgl.Float32Array
import utils.Glu
import utils.Glu.pack
import utils.WebGL2RenderingContext

class HardWorkDemo(private val gl: WebGL2RenderingContext) {

    suspend fun run() {
        // Load teapot model
        val model = loadWavefront("models/teapot.obj")

        val vertexSrc = window.fetch("shaders/vertex.glsl").await().text().await()
        val fragmentSrc = window.fetch("shaders/fragment.glsl").await().text().await()
        val program = Glu.createProgramFromSource(gl, vertexSrc, fragmentSrc)

        val attributeLocations = Glu.extractAttributeLocations(gl, program)
        val uniformLocations = Glu.extractUniformLocations(gl, program)

        // look up attribute (inputs) locations
        val positionAttributeLocation = attributeLocations["a_position"] ?: throw RuntimeException("attribute location missing")
        val normalAttributeLocation = attributeLocations["a_normal"] ?: throw RuntimeException("attribute location missing")

        // look up uniform (constants) locations
        val modelViewMatrixLocation = uniformLocations["u_modelViewMatrix"] ?: throw RuntimeException("uniform location missing")
        val normalMatrixLocation = uniformLocations["u_normalMatrix"] ?: throw RuntimeException("uniform location missing")
        val projectionMatrixLocation = uniformLocations["u_projectionMatrix"] ?: throw RuntimeException("uniform location missing")
        val diffuseColorLocation = uniformLocations["u_diffuseColor"] ?: throw RuntimeException("uniform location missing")
        val specularColorLocation = uniformLocations["u_specularColor"] ?: throw RuntimeException("uniform location missing")
        val shininessLocation = uniformLocations["u_shininess"] ?: throw RuntimeException("uniform location missing")
        val lightPositionLocation = uniformLocations["u_lightPosition"] ?: throw RuntimeException("uniform location missing")

        // Create a buffer
        val positionBuffer = gl.createBuffer()

        // Create a vertex array object (attribute state)
        val vao = gl.createVertexArray()

        // and make it the one we're currently working with
        gl.bindVertexArray(vao)

        // Turn on the attributes
        gl.enableVertexAttribArray(positionAttributeLocation)
        gl.enableVertexAttribArray(normalAttributeLocation)

        // Bind it to ARRAY_BUFFER (think of it as ARRAY_BUFFER = positionBuffer)
        gl.bindBuffer(gl.ARRAY_BUFFER, positionBuffer)

        // Upload model into graphics buffer
        gl.bufferData(gl.ARRAY_BUFFER, Float32Array(model), gl.STATIC_DRAW)

        // Tell the attribute how to get data out of positionBuffer (ARRAY_BUFFER)
        gl.vertexAttribPointer(positionAttributeLocation, 3, gl.FLOAT, false, 8 * 4, 0)
        gl.vertexAttribPointer(normalAttributeLocation, 3, gl.FLOAT, false, 8 * 4, 3 * 4)

        gl.bindVertexArray(null)
        gl.disableVertexAttribArray(positionAttributeLocation)
        gl.disableVertexAttribArray(normalAttributeLocation)

        // Clear the canvas
        gl.clearColor(0F, 0F, 0F, 0F)
        gl.clear(gl.COLOR_BUFFER_BIT or gl.DEPTH_BUFFER_BIT)

        // turn on depth testing
        gl.enable(gl.DEPTH_TEST)

        // tell webgl to cull faces
        gl.enable(gl.CULL_FACE)

        fun loop(timestamp: Double) {
            // Tell WebGL how to go from clip space to screen space
            val displayWidth = gl.canvas.clientWidth
            val displayHeight = gl.canvas.clientHeight
            if (gl.canvas.width != displayWidth || gl.canvas.height != displayHeight) {
                gl.canvas.width = displayWidth
                gl.canvas.height = displayHeight
                gl.viewport(0, 0, gl.canvas.width, gl.canvas.height)
            }

            // Tell it to use our program (pair of shaders)
            gl.useProgram(program)

            // Update light source position
            gl.uniform3f(lightPositionLocation, 0F, 40F, 50F)

            // Update material colors
            gl.uniform3f(diffuseColorLocation, 0.9F, 0.9F, 0.9F)
            gl.uniform3f(specularColorLocation, 0.9F, 0.9F, 0.9F)
            gl.uniform1f(shininessLocation, 40F)

            // Bind the attribute/buffer set we want.
            gl.bindVertexArray(vao)

            // Compute the matrices
            val worldMatrix = Matrix4.createRotationY((timestamp / 1000.0).toFloat())
            val normalMatrix = Matrix4.transpose(Matrix4.invert(worldMatrix))
            val viewMatrix = Matrix4.createLookAt(Vector3(0F, 0.5F, 2.0F), Vector3(0F, 0F, 0F), Vector3(0F, 1F, 0F))
            val aspect = gl.drawingBufferWidth.toFloat() / gl.drawingBufferHeight.toFloat()
            val projMatrix = Matrix4.createPerspectiveFov(Scalar.PI / 4F, aspect, 1F, 100F)

            // Update matrix uniforms
            gl.uniformMatrix4fv(modelViewMatrixLocation, false, Float32Array((viewMatrix * worldMatrix).toArray().toTypedArray()))
            gl.uniformMatrix4fv(normalMatrixLocation, false, normalMatrix.toArray().pack())
            gl.uniformMatrix4fv(projectionMatrixLocation, false, projMatrix.toArray().pack())

            // Draw the geometry....
            gl.drawArrays(gl.TRIANGLES, 0, model.size / 8)

            // Update matrix uniforms
            val worldMatrix2 = Matrix4.createTranslation(-1F, 0F, -2F) * Matrix4.createFromYawPitchRoll(2.3F, 0.4F, 0F)
            val normalMatrix2 = Matrix4.transpose(Matrix4.invert(worldMatrix2))
            gl.uniformMatrix4fv(modelViewMatrixLocation, false, Float32Array((viewMatrix * worldMatrix2).toArray().toTypedArray()))
            gl.uniformMatrix4fv(normalMatrixLocation, false, Float32Array(normalMatrix2.toArray().toTypedArray()))
            gl.drawArrays(gl.TRIANGLES, 0, model.size / 8)

            // Render next frame
            window.requestAnimationFrame { loop(it) }
        }

        window.requestAnimationFrame { loop(it) }
    }

    private suspend fun loadWavefront(filename: String): Array<Float> {
        data class Indices(val v: Int, val vt: Int, val vn: Int)

        fun parseIndices(triple: String): Indices {
            val indices = triple.split('/').map { it.toInt() - 1 }.toIntArray()
            return Indices(indices[0], indices[1], indices[2])
        }

        val vertices = mutableListOf<Vector3>()
        val normals = mutableListOf<Vector3>()
        val texCoords = mutableListOf<Tuple2>()
        val buffer = mutableListOf<Float>()

        fun pack(i0: Indices, i1: Indices, i2: Indices): Array<Float> {
            val v0 = vertices[i0.v]
            val n0 = normals[i0.vn]
            val t0 = texCoords[i0.vt]
            val v1 = vertices[i1.v]
            val n1 = normals[i1.vn]
            val t1 = texCoords[i1.vt]
            val v2 = vertices[i2.v]
            val n2 = normals[i2.vn]
            val t2 = texCoords[i2.vt]
            return floatArrayOf(
                v0.x, v0.y, v0.z, n0.x, n0.y, n0.z, t0.x, t0.y,
                v1.x, v1.y, v1.z, n1.x, n1.y, n1.z, t1.x, t1.y,
                v2.x, v2.y, v2.z, n2.x, n2.y, n2.z, t2.x, t2.y
            ).toTypedArray()
        }

        val directives = window.fetch(filename).await().text().await()
        directives.split('\n').map { it.trim() }.filter { it.isNotEmpty() }.forEach {
            val parts = it.split(' ')
            when (parts.first()) {
                "v" -> {
                    val x = parts[1].toFloat()
                    val y = parts[2].toFloat()
                    val z = parts[3].toFloat()
                    vertices.add(Vector3(x, y, z))
                }
                "vn" -> {
                    val x = parts[1].toFloat()
                    val y = parts[2].toFloat()
                    val z = parts[3].toFloat()
                    normals.add(Vector3(x, y, z))
                }
                "vt" -> {
                    val u = parts[1].toFloat()
                    val v = parts[2].toFloat()
                    texCoords.add(Tuple2(u, v))
                }
                "f" -> {
                    val i0 = parseIndices(parts[1])
                    val i1 = parseIndices(parts[2])
                    val i2 = parseIndices(parts[3])
                    buffer.addAll(pack(i0, i1, i2))
                }
            }
        }
        return buffer.toTypedArray()
    }
}
