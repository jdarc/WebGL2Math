import core.Camera
import core.Color
import core.Device
import core.Material
import graph.*
import io.Wavefront
import kotlinx.browser.window
import math.Matrix4
import math.Vector3
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
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

        createTeapotScene()
//        createSolarSystemScene()

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
        val sphere = PrimitiveFactory.createSphere(0.5F, 16, 16)

        val sun = Material("Sun").apply { diffuseColor = Color.create(0xFFFF00) }
        val earth = Material("Earth").apply { diffuseColor = Color.create(0x0000FF) }
        val moon = Material("Moon").apply { diffuseColor = Color.create(0x888888) }
        val mars = Material("Mars").apply { diffuseColor = Color.create(0xFF0000) }

        scene.root.add(
            MaterialNode(sun).add(RenderNode(sphere)),
            AxisRotationNode(Vector3.UNIT_Y, 0.0005F).add(
                GroupNode(Matrix4.createTranslation(Vector3(2F, 0F, 0F))).add(
                    AxisRotationNode(Vector3.UNIT_Y, 0.001F).add(
                        MaterialNode(earth).add(
                            RenderNode(sphere, Matrix4.create(Vector3.ZERO, Matrix4.IDENTITY, Vector3(0.3F)))
                        )
                    ),
                    AxisRotationNode(Vector3.UNIT_Y, 0.005F).add(
                        MaterialNode(moon).add(
                            RenderNode(sphere, Matrix4.create(Vector3(0.5F, 0F, 0F), Matrix4.IDENTITY, Vector3(0.08F)))
                        )
                    )
                )
            ),
            AxisRotationNode(Vector3.UNIT_Y, 0.0002F).add(
                MaterialNode(mars).add(
                    RenderNode(sphere, Matrix4.create(Vector3(3.0F, 0F, 0F), Matrix4.IDENTITY, Vector3(0.15F)))
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

        val cube = PrimitiveFactory.createCube(1F)
        val sphere = PrimitiveFactory.createSphere(0.5F, 16, 16)
        val teapot = Wavefront.read("models", "teapot.obj").meshes[0]

        scene.root.add(
            AxisRotationNode(Vector3.UNIT_Y, 0.005F).add(
                GroupNode(Matrix4.createTranslation(Vector3(-0.5F, 0.2F, -1F))).add(
                    MaterialNode(greenMaterial).add(RenderNode(teapot))
                )
            ),
            GroupNode(Matrix4.createTranslation(Vector3(0.75F, 1.2F, 0.5F))).add(
                AxisRotationNode(Vector3.UNIT_X, 0.005F).add(RenderNode(teapot))
            ),
            AxisRotationNode(Vector3.UNIT_X, 0.005F).add(MaterialNode(blueMaterial).add(RenderNode(cube))),
            AxisRotationNode(Vector3.UNIT_Y, 0.001F).add(
                GroupNode(Matrix4.createTranslation(Vector3(-2.5F, 1.2F, 0.5F))).add(
                    MaterialNode(redMaterial).add(RenderNode(sphere))
                )
            )
        )
    }
}
