package core

import math.Matrix4
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLProgram
import utils.Glu
import utils.Glu.pack
import utils.WebGL2RenderingContext

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

private class AttributeLocations(gl: WebGL2RenderingContext, program: WebGLProgram) {
    val position = gl.getAttribLocation(program, "a_position")
    val normal = gl.getAttribLocation(program, "a_normal")
}

class Program(private val gl: WebGL2RenderingContext, vertexShaderSrc: String, fragmentShaderSrc: String) {
    private val glProgram = Glu.createProgramFromSource(gl, vertexShaderSrc, fragmentShaderSrc)

    private val uniformLocations = UniformLocations(gl, glProgram)
    private val attributeLocations = AttributeLocations(gl, glProgram)

    private var viewMatrix = Matrix4.IDENTITY

    fun use(): Program {
        gl.useProgram(glProgram)
        gl.enableVertexAttribArray(attributeLocations.position)
        gl.enableVertexAttribArray(attributeLocations.normal)
        return this
    }

    fun setWorldMatrix(m: Matrix4) {
        gl.uniformMatrix4fv(uniformLocations.modelViewMatrix, false, (viewMatrix * m).toArray().pack())
        gl.uniformMatrix4fv(uniformLocations.normalMatrix, false, Matrix4.transpose(Matrix4.invert(m)).toArray().pack())
    }

    fun setViewMatrix(m: Matrix4) {
        viewMatrix = m
    }

    fun setProjectionMatrix(m: Matrix4) = gl.uniformMatrix4fv(uniformLocations.projectionMatrix, false, m.toArray().pack())

    fun material(m: Material) {
        gl.uniform3f(uniformLocations.ambientColor, m.ambientColor.red, m.ambientColor.grn, m.ambientColor.blu)
        gl.uniform3f(uniformLocations.diffuseColor, m.diffuseColor.red, m.diffuseColor.grn, m.diffuseColor.blu)
        gl.uniform3f(uniformLocations.specularColor, m.specularColor.red, m.specularColor.grn, m.specularColor.blu)
        gl.uniform1f(uniformLocations.shininess, m.shininess)
    }

    fun ambientIntensity(s: Float) = gl.uniform1f(uniformLocations.ambientIntensity, s)

    fun lightPosition(x: Float, y: Float, z: Float) = gl.uniform3f(uniformLocations.lightPosition, x, y, z)

    fun bindVertexBuffer(buffer: WebGLBuffer) {
        gl.bindBuffer(gl.ARRAY_BUFFER, buffer)
        gl.vertexAttribPointer(attributeLocations.position, 3, gl.FLOAT, false, 8 * 4, 0)
        gl.vertexAttribPointer(attributeLocations.normal, 3, gl.FLOAT, false, 8 * 4, 3 * 4)
    }
}
