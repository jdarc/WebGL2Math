import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

suspend fun main() = SceneGraphDemo(document.querySelector("canvas") as HTMLCanvasElement).run()

