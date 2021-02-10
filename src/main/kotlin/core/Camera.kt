package core

import math.Matrix4
import math.Scalar
import math.Vector3
import kotlin.math.asin
import kotlin.math.atan2

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Camera(fov: Float = Scalar.PI / 4F, aspectRatio: Float = 1F, nearPlane: Float = 1F, farPlane: Float = 1000F) {
    private var eye = Vector3.UNIT_Z
    private var at = Vector3.ZERO
    private var up = Vector3.UNIT_Y

    var fieldOfView = fov
        set(value) {
            field = value.coerceIn(Float.MIN_VALUE, Scalar.PI)
        }

    var aspectRatio = aspectRatio
        set(value) {
            field = value.coerceIn(Float.MIN_VALUE, Float.MAX_VALUE)
        }

    var nearDistance = nearPlane
        set(value) {
            field = value.coerceIn(Float.MIN_VALUE, Float.MAX_VALUE)
            if (nearDistance >= farDistance) throw RuntimeException("near distance must be less than far distance")
        }

    var farDistance = farPlane
        set(value) {
            field = value.coerceIn(1F, Float.MAX_VALUE)
            if (farDistance <= nearDistance) throw RuntimeException("far distance must be greater than near distance")
        }

    val position get() = eye

    val center get() = at

    val direction get() = Vector3.normalize(at - eye)

    val yaw: Float inline get() { view.apply { return -atan2(-this.m20, this.m22) } }

    val pitch inline get() = -asin(view.m21)

    val view get() = Matrix4.createLookAt(eye, at, up)

    val projection get() = Matrix4.createPerspectiveFov(fieldOfView, aspectRatio, nearDistance, farDistance)

    fun moveTo(v: Vector3) = moveTo(v.x, v.y, v.z)

    fun moveTo(x: Float, y: Float, z: Float) {
        eye = Vector3(x, y, z)
    }

    fun lookAt(v: Vector3) = lookAt(v.x, v.y, v.z)

    fun lookAt(x: Float, y: Float, z: Float) {
        at = Vector3(x, y, z)
    }

    fun worldUp(v: Vector3) = worldUp(v.x, v.y, v.z)

    fun worldUp(x: Float, y: Float, z: Float) {
        up = Vector3.normalize(Vector3(x, y, z))
    }

    fun moveDown(amount: Float) {
        view.apply { strafe(m10 * amount, m11 * amount, m12 * amount) }
    }

    fun moveUp(amount: Float) = moveDown(-amount)

    fun moveForward(amount: Float) {
        view.apply { strafe(m20 * amount, m21 * amount, m22 * amount) }
    }

    fun moveBackward(amount: Float) = moveForward(-amount)

    fun moveLeft(amount: Float) {
        view.apply { strafe(m00 * amount, m01 * amount, m02 * amount) }
    }

    fun moveRight(amount: Float) = moveLeft(-amount)

    fun orient(dir: Vector3) = lookAt(eye.x + dir.x, eye.y + dir.y, eye.z + dir.z)

    private fun strafe(dx: Float, dy: Float, dz: Float) {
        moveTo(eye.x - dx, eye.y - dy, eye.z - dz)
        lookAt(at.x - dx, at.y - dy, at.z - dz)
    }
}
