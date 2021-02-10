import core.Camera
import core.Color
import core.Device
import core.Material
import graph.BranchNode
import graph.LeafNode
import graph.Scene
import io.Wavefront
import kotlinx.browser.window
import math.Matrix4
import math.Vector3
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import utils.AxisRotationNode
import utils.CameraController
import utils.PrimitiveFactory
import utils.WebGL2RenderingContext

class SceneGraphDemo(canvas: HTMLCanvasElement) {
    private val gl = canvas.getContext("webgl2") as WebGL2RenderingContext
    private val device = Device(gl)
    private val camera = Camera()
    private val controller = CameraController(camera)
    private var scene = Scene()
    private var lastTimeStamp = 0.0

    suspend fun run() {
        device.initialise()
        device.moveLightTo(Vector3(0F, 10F, 30F))

        camera.lookAt(0F, 0F, 0F)
        camera.moveTo(0.5F, 1.0F, 5.0F)
        controller.sync()

        window.addEventListener("mousedown", { controller.mouseDown(it as MouseEvent) })
        window.addEventListener("mouseup", { controller.mouseUp(it as MouseEvent) })
        window.addEventListener("mousemove", { controller.mouseMove(it as MouseEvent) })
        window.addEventListener("keydown", { controller.keyDown(it as KeyboardEvent) })
        window.addEventListener("keyup", { controller.keyUp(it as KeyboardEvent) })

//        createTeapotScene()
        createSolarSystemScene()

        window.requestAnimationFrame { loop(it) }
    }

    private fun loop(timestamp: Double) {
        val delta = timestamp - lastTimeStamp
        controller.update(delta, 0.01)

        scene.update(delta)
        scene.render(camera, device)

        lastTimeStamp = timestamp
        window.requestAnimationFrame { loop(it) }
    }

    private fun createSolarSystemScene() {
        val sunMaterial = Material("Sun")
        sunMaterial.diffuseColor = Color.create(1F, 1F, 0F)

        val earthMaterial = Material("Earth")
        earthMaterial.diffuseColor = Color.create(0F, 0F, 1F)

        val moonMaterial = Material("Moon")
        moonMaterial.diffuseColor = Color.create(0.5F, 0.5F, 0.5F)

        val marsMaterial = Material("Moon")
        marsMaterial.diffuseColor = Color.create(1.0F, 0F, 0F)

        val sun = PrimitiveFactory.createSphere(0.5F, 8, 8, sunMaterial)
        val earth = PrimitiveFactory.createSphere(0.2F, 8, 8, earthMaterial)
        val moon = PrimitiveFactory.createSphere(0.03F, 8, 8, moonMaterial)
        val mars = PrimitiveFactory.createSphere(0.08F, 8, 8, marsMaterial)

        scene.root.add(
            BranchNode().add(
                LeafNode(sun)
            ).add(
                AxisRotationNode(Vector3.UNIT_Y, 0.0005F).add(
                    BranchNode(Matrix4.createTranslation(Vector3(1.5F, 0F, 0F))).add(
                        BranchNode().add(
                            LeafNode(earth)
                        ).add(
                            BranchNode(Matrix4.createTranslation(Vector3(0.5F, 0F, 0F))).add(
                                LeafNode(moon)
                            )
                        )
                    )
                )
            ).add(
                AxisRotationNode(Vector3.UNIT_Y, 0.0002F).add(
                    BranchNode(Matrix4.createTranslation(Vector3(3.0F, 0F, 0F))).add(
                        LeafNode(mars)
                    )
                )
            )
        )
    }

    private suspend fun createTeapotScene() {
        val blueMaterial = Material("Blue")
        blueMaterial.diffuseColor = Color.create(0xFF)

        val redMaterial = Material("Red")
        redMaterial.diffuseColor = Color.create(0xFF0000)

        val greenMaterial = Material("Green")
        greenMaterial.diffuseColor = Color.create(0xFF00)

        val blueCube = PrimitiveFactory.createCube(0.5F, blueMaterial)
        val redSphere = PrimitiveFactory.createSphere(0.5F, 5, 16, redMaterial)
        val teapot = Wavefront.read("models", "teapot.obj")

        scene.root.add(
            BranchNode(Matrix4.createTranslation(Vector3(-0.5F, 0.2F, -1F))).add(
                AxisRotationNode(Vector3.UNIT_Y, 0.005F).add(
                    LeafNode(teapot)
                )
            )
        ).add(
            AxisRotationNode(Vector3.UNIT_X, 0.005F).add(
                BranchNode(Matrix4.createTranslation(Vector3(0.75F, 1.2F, 0.5F))).add(
                    LeafNode(teapot)
                )
            )
        ).add(
            AxisRotationNode(Vector3.UNIT_X, 0.005F).add(
                LeafNode(blueCube)
            )
        ).add(
            AxisRotationNode(Vector3.UNIT_Y, 0.001F).add(
                BranchNode(Matrix4.createTranslation(Vector3(-2.5F, 1.2F, 0.5F))).add(
                    LeafNode(redSphere)
                )
            )
        )
    }
}
