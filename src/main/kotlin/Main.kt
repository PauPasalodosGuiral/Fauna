import java.nio.file.Path
import java.io.File
import java.nio.file.Files

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
data class Animal(
    val id_animal: Int,
    val nombre: String,
    val origen: String,
    val esperanza_vida: Int,
    val peso_medio: Double
)

fun main() {

    var repite: Boolean = true

    while (repite) {
        try {

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
    } catch (e: NumberFormatException) {
            println("Error")
        }
    }
}
fun menuCSV() {
    var repite: Boolean = true
    val ruta = Path.of("datos", "fauna.csv")
        while (repite) {
            try {
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
                1 -> leerCSV(ruta)
                2 -> anadirCSV(ruta)
                3 -> modificarCSV()
                4 -> eliminarCSV()
                0 -> repite = false
                else -> println("Escoge un numero del menu")
            }
            } catch (e: NumberFormatException) {
            println("Error")
        }
        }

}

fun leerCSV(ruta: Path): List<Animal> {
    var animales: List<Animal> = emptyList()
    if (!Files.isReadable(ruta)) {
        println("Error No se puede leer el fichero en la ruta: $ruta")
    } else {
        val reader = csvReader { delimiter = ';' }

        val filas: List<List<String>> = reader.readAll(ruta.toFile())

        animales = filas.mapNotNull { columnas ->
            if (columnas.size >= 5) {
                try {
                    val id_animal = columnas[0].toInt()
                    val nombre = columnas[1]
                    val origen = columnas[2]
                    val esperanza_vida = columnas[3].toInt()
                    val peso_medio = columnas[4].toDouble()
                    Animal(id_animal, nombre, origen, esperanza_vida, peso_medio)
                } catch (e: Exception) {
                    println("Fila inválida ignorada: $columnas -> Error: ${e.message}")
                    null
                }
            } else {
                println("Fila con formato incompleto ignorada: $columnas")
                null
            }
        }

    }
    println("--- Información leída con éxito de: $ruta")
    return animales
}
fun anadirCSV(ruta: Path) {
    val animales = leerCSV(ruta)
    var bandera: Boolean = true
    while (bandera) {
        println("Dame una id válida")
        val id: Int = readln().toInt()

        if (animales.map { animal ->
            listOf(
                animal.id_animal
            )
            } !in id)

        try {
            val fichero: File = ruta.toFile()
            csvWriter {
                delimiter ';'
            }.writeAll(
                animales.map { animal ->
                    listOf(
                        animal.id_animal.toString(),
                        animal.nombre,
                        animal.origen,
                        animal.esperanza_vida.toString(),
                        animal.peso_medio.toString()
                    )
                },
                fichero
            )
            println("--- Información guardada con éxito en: $fichero")
        } catch (e: Exception) {
            println("Error al escribir el fichero CSV: ${e.message}")
        }
    }
}
fun modificarCSV() {

}
fun eliminarCSV() {

}