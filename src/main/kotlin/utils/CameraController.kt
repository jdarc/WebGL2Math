package utils

import core.Camera
import math.Vector3
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

private const val MOVE_FORWARD = 0x1
private const val MOVE_BACKWARD = 0x2
private const val MOVE_LEFT = 0x4
private const val MOVE_RIGHT = 0x8

class CameraController(val camera: Camera) {
    private var yaw = camera.yaw
    private var pitch = camera.pitch
    private var mouseX = 0
    private var mouseY = 0
    private var dragging = false
    private var movementMask = 0

    fun sync() {
        yaw = camera.yaw
        pitch = camera.pitch
    }

    fun keyUp(e: KeyboardEvent) {
        when {
            e.code == "KeyW" -> movementMask = movementMask xor MOVE_FORWARD
            e.code == "KeyS" -> movementMask = movementMask xor MOVE_BACKWARD
            e.code == "KeyA" -> movementMask = movementMask xor MOVE_LEFT
            e.code == "KeyD" -> movementMask = movementMask xor MOVE_RIGHT
        }
    }

    fun keyDown(e: KeyboardEvent) {
        when {
            e.code == "KeyW" -> movementMask = movementMask or MOVE_FORWARD
            e.code == "KeyS" -> movementMask = movementMask or MOVE_BACKWARD
            e.code == "KeyA" -> movementMask = movementMask or MOVE_LEFT
            e.code == "KeyD" -> movementMask = movementMask or MOVE_RIGHT
        }
    }

    fun mouseDown(e: MouseEvent) {
        dragging = true
    }

    fun mouseMove(e: MouseEvent) {
        if (dragging) {
            yaw += (mouseX - e.clientX) * 0.005F
            pitch = min(1.57F, max(-1.57F, pitch + (mouseY - e.clientY) * 0.005F))
        }
        mouseX = e.clientX
        mouseY = e.clientY
    }

    fun mouseUp(e: MouseEvent) {
        dragging = false
    }

    fun update(seconds: Double, speed: Double) {
        val scaledSpeed = (seconds * speed).toFloat()

        if (movementMask and MOVE_FORWARD == MOVE_FORWARD) camera.moveForward(scaledSpeed)
        else if (movementMask and MOVE_BACKWARD == MOVE_BACKWARD) camera.moveBackward(scaledSpeed)

        if (movementMask and MOVE_LEFT == MOVE_LEFT) camera.moveLeft(scaledSpeed)
        else if (movementMask and MOVE_RIGHT == MOVE_RIGHT) camera.moveRight(scaledSpeed)

        camera.orient(Vector3(-sin(yaw) * cos(pitch), sin(pitch), -cos(yaw) * cos(pitch)))
    }
}
