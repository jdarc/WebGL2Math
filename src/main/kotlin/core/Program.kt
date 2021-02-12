package core

import math.Matrix4
import math.Vector3
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLProgram
import utils.Glu
import utils.Glu.pack
import utils.WebGL2RenderingContext

class Program(private val gl: WebGL2RenderingContext, vertexShaderSrc: String, fragmentShaderSrc: String) {
    private val glProgram = Glu.createProgramFromSource(gl, vertexShaderSrc, fragmentShaderSrc)

    private val uniformLocations = UniformLocations(gl, glProgram)
    private val attributeLocations = AttributeLocations(gl, glProgram)

    fun use(): Program {
        gl.useProgram(glProgram)
        gl.enableVertexAttribArray(attributeLocations.position)
        gl.enableVertexAttribArray(attributeLocations.normal)
        return this
    }

    var transform: Matrix4
        get() = throw UnsupportedOperationException()
        set(value) {
            gl.uniformMatrix4fv(uniformLocations.modelViewMatrix, false, (view * value).toArray().pack())
            gl.uniformMatrix4fv(uniformLocations.normalMatrix, false, Matrix4.transpose(Matrix4.invert(value)).toArray().pack())
        }

    var view = Matrix4.IDENTITY

    var projection: Matrix4
        get() = throw UnsupportedOperationException()
        set(value) = gl.uniformMatrix4fv(uniformLocations.projectionMatrix, false, value.toArray().pack())

    var material: Material
        get() = throw UnsupportedOperationException()
        set(value) {
            gl.uniform3f(uniformLocations.ambientColor, value.ambientColor.red, value.ambientColor.grn, value.ambientColor.blu)
            gl.uniform3f(uniformLocations.diffuseColor, value.diffuseColor.red, value.diffuseColor.grn, value.diffuseColor.blu)
            gl.uniform3f(uniformLocations.specularColor, value.specularColor.red, value.specularColor.grn, value.specularColor.blu)
            gl.uniform1f(uniformLocations.shininess, value.shininess)
        }

    var ambientIntensity: Float
        get() = throw UnsupportedOperationException()
        set(value) = gl.uniform1f(uniformLocations.ambientIntensity, value)

    var lightPosition: Vector3
        get() = throw UnsupportedOperationException()
        set(value) = gl.uniform3f(uniformLocations.lightPosition, value.x, value.y, value.z)

    fun bindVertexBuffer(buffer: WebGLBuffer) {
        gl.bindBuffer(gl.ARRAY_BUFFER, buffer)
        gl.vertexAttribPointer(attributeLocations.position, 3, gl.FLOAT, false, 8 * 4, 0)
        gl.vertexAttribPointer(attributeLocations.normal, 3, gl.FLOAT, false, 8 * 4, 3 * 4)
    }

    companion object {
        private class AttributeLocations(gl: WebGL2RenderingContext, program: WebGLProgram) {
            val position = gl.getAttribLocation(program, "a_position")
            val normal = gl.getAttribLocation(program, "a_normal")
        }

        private class UniformLocations(gl: WebGL2RenderingContext, program: WebGLProgram) {
            val modelViewMatrix = gl.getUniformLocation(program, "u_modelViewMatrix")
            val normalMatrix = gl.getUniformLocation(program, "u_normalMatrix")
            val projectionMatrix = gl.getUniformLocation(program, "u_projectionMatrix")
            val ambientColor = gl.getUniformLocation(program, "u_ambientColor")
            val diffuseColor = gl.getUniformLocation(program, "u_diffuseColor")
            val specularColor = gl.getUniformLocation(program, "u_specularColor")
            val ambientIntensity = gl.getUniformLocation(program, "u_ambientIntensity")
            val shininess = gl.getUniformLocation(program, "u_shininess")
            val lightPosition = gl.getUniformLocation(program, "u_lightPosition")
        }
    }
}
