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
            1 -> menuCSV()
            0 -> repite = false
            else -> println("Escoge un numero del menu")
        }
    }
    } catch (e: NumberFormatException) {
        println("Error")
    }
}
fun menuCSV() {
    var repite: Boolean = true
    try {
        while (repite) {
            println("--------------------------------------")
            println("-------------- CRUD CSV --------------")
            println("--------------------------------------")
            println("1. Leer datos desde CSV")
            println("2. Añadir un registro nuevo al final del fichero")
            println("3. Modificar un registro existente (por ID)")
            println("4. Eliminar un registro existente (por ID)")
            println("0. Volver al menú principal")
            val eleccion: Int = readln().toInt()
            when (eleccion) {
                1 -> println("LEER")
                2 -> println("AÑADIR")
                3 -> println("MODIFICAR")
                4 -> println("ELIMINAR")
                0 -> repite = false
                else -> println("Escoge un numero del menu")
            }
        }
    } catch (e: NumberFormatException) {
        println("Error")
    }
}

fun leerCSV() {

}
fun anadirCSV() {

}
fun modificarCSV() {

}
fun eliminarCSV() {

}