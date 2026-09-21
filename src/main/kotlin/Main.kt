import java.util.InputMismatchException

data class Fauna(
    val id_animal: Int,
    val nombre: String,
    val origen: String,
    val esperanza_vida: Int,
    val peso_medio: Double
)

fun main() {

    var repite: Boolean = true
    try {


    while (repite) {
        println("--------------------------------------")
        println("----------- MENÚ PRINCIPAL -----------")
        println("--------------------------------------")
        println("1. Gestión CSV")
        println("0. Salir")
        val eleccion: Int = readln().toInt()
        when (eleccion) {
            1 -> println("TO DO")
            0 -> repite = false
            else -> println("Escoge un numero del menu")
        }
    }
    } catch (e: NumberFormatException) {
        println("Error")
    }
}