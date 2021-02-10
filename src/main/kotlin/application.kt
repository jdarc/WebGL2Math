import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import utils.WebGL2RenderingContext

suspend fun main() {
    val rootDivElement = document.querySelector("#root") as HTMLDivElement
    val canvasElement = document.createElement("canvas") as HTMLCanvasElement
    rootDivElement.appendChild(canvasElement)
    val gl = canvasElement.getContext("webgl2") as WebGL2RenderingContext

//    HardWorkDemo(gl).run()
    SceneGraphDemo(gl).run()
}
