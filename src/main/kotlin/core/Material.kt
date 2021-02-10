package core

class Material(val name: String) {
    var ambientColor = Color.WHITE
    var diffuseColor = Color.BLACK
    var specularColor = Color.WHITE
    var shininess = 30F

    companion object {
        val DEFAULT = Material("")
    }
}
