package utils

import org.khronos.webgl.*

@Suppress("MemberVisibilityCanBePrivate")
object Glu {

    fun compileShader(gl: WebGL2RenderingContext, shaderSource: String, shaderType: Int): WebGLShader {
        val shader = gl.createShader(shaderType)
        gl.shaderSource(shader, shaderSource)
        gl.compileShader(shader)
        val success = gl.getShaderParameter(shader, gl.COMPILE_STATUS) as Boolean
        if (!success) throw RuntimeException("shader failed to compile: ${gl.getShaderInfoLog(shader)}")
        return shader ?: throw RuntimeException("failed to create shader")
    }

    fun createProgram(gl: WebGL2RenderingContext, vertexShader: WebGLShader, fragmentShader: WebGLShader): WebGLProgram {
        val program = gl.createProgram()
        gl.attachShader(program, vertexShader)
        gl.attachShader(program, fragmentShader)
        gl.linkProgram(program)
        val success = gl.getProgramParameter(program, gl.LINK_STATUS) as Boolean
        if (!success) throw RuntimeException("program failed to link: ${gl.getProgramInfoLog(program)}")
        return program ?: throw RuntimeException("failed to create program")
    }

    fun createProgramFromSource(gl: WebGL2RenderingContext, vertexSource: String, fragmentSource: String): WebGLProgram {
        val vs = compileShader(gl, vertexSource, gl.VERTEX_SHADER)
        val fs = compileShader(gl, fragmentSource, gl.FRAGMENT_SHADER)
        return createProgram(gl, vs, fs)
    }

    fun extractAttributeLocations(gl: WebGL2RenderingContext, program: WebGLProgram): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        val count = gl.getProgramParameter(program, gl.ACTIVE_ATTRIBUTES) as Int
        for (i in 0 until count) {
            val info = gl.getActiveAttrib(program, i)!!
            result[info.name] = gl.getAttribLocation(program, info.name)
        }
        return result
    }

    fun extractUniformLocations(gl: WebGL2RenderingContext, program: WebGLProgram): Map<String, WebGLUniformLocation> {
        val result = mutableMapOf<String, WebGLUniformLocation>()
        val count = gl.getProgramParameter(program, gl.ACTIVE_UNIFORMS) as Int
        for (i in 0 until count) {
            val info = gl.getActiveUniform(program, i)!!
            result[info.name] = gl.getUniformLocation(program, info.name)!!
        }
        return result
    }

    fun createVertexBuffer(gl: WebGL2RenderingContext, data: Float32Array): WebGLBuffer {
        val buffer = gl.createBuffer()
        gl.bindBuffer(gl.ARRAY_BUFFER, buffer)
        gl.bufferData(gl.ARRAY_BUFFER, data, gl.STATIC_DRAW)
        return buffer ?: throw RuntimeException("failed to create buffer")
    }

    fun FloatArray.pack() = Float32Array(this.toTypedArray())
}
