import core.*
import graph.BranchNode
import graph.LeafNode
import graph.Scene
import io.Wavefront
import kotlinx.browser.window
import math.Matrix4
import math.Vector3
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import utils.AxisRotationNode
import utils.CameraController
import utils.Primitives
import utils.WebGL2RenderingContext

class SceneGraphDemo(gl: WebGL2RenderingContext) {
    private val device = Device(gl)
    private val camera = Camera()
    private val controller = CameraController(camera)
    private var scene = Scene()

    suspend fun run() {
        device.initialise()
        device.moveLightTo(Vector3(0F, 100F, 0F))

        camera.lookAt(0F, 0F, 0F)
        camera.moveTo(0F, 0.5F, 2.0F)
        controller.sync()

        window.addEventListener("mousedown", { controller.mouseDown(it as MouseEvent) })
        window.addEventListener("mouseup", { controller.mouseUp(it as MouseEvent) })
        window.addEventListener("mousemove", { controller.mouseMove(it as MouseEvent) })
        window.addEventListener("keydown", { controller.keyDown(it as KeyboardEvent) })
        window.addEventListener("keyup", { controller.keyUp(it as KeyboardEvent) })

        val blueMaterial = Material("Blue")
        blueMaterial.diffuseColor = Color.create(0xFF)

        val greenMaterial = Material("Green")
        greenMaterial.diffuseColor = Color.create(0xFF00)

        val blueCube = Primitives.createCube(0.5F, blueMaterial)
        val greenCube = Primitives.createCube(0.25F, greenMaterial)
        val teapot = Wavefront.read("models", "teapot.obj")

        scene.root.add(
            BranchNode(Matrix4.createTranslation(Vector3(-0.5F, 0.2F, -1F))).add(
                AxisRotationNode(Vector3.UNIT_Y, 0.005F).add(LeafNode(teapot))
            )
        )

        scene.root.add(
            BranchNode(Matrix4.createTranslation(Vector3(0.75F, -0.2F, -0.5F))).add(
                AxisRotationNode(Vector3.UNIT_X, 0.005F).add(LeafNode(teapot))
            )
        )

        scene.root.add(AxisRotationNode(Vector3.UNIT_X, 0.005F).add(LeafNode(blueCube)))
        window.requestAnimationFrame { loop(it) }
    }

    private fun loop(timestamp: Double) {
        controller.update(timestamp, 0.000001)

        scene.update(timestamp)
        scene.render(camera, device)

        window.requestAnimationFrame { loop(it) }
    }
}
