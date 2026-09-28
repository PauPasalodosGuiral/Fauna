import java.nio.file.Path
import java.io.File
import java.nio.file.Files

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

import kotlinx.serialization.*
import kotlinx.serialization.json.*

data class Animal(
    val id_animal: Int,
    val nombre: String,
    val origen: String,
    val esperanza_vida: Int,
    val peso_medio: Double
)
data class AnimalXML(
    @JacksonXmlProperty(localName = "id_animal")
    val id_animal: Int,
    @JacksonXmlProperty(localName = "nombre")
    val nombre: String,
    @JacksonXmlProperty(localName = "origen")
    val origen: String,
    @JacksonXmlProperty(localName = "esperanza_vida")
    val esperanza_vida: Int,
    @JacksonXmlProperty(localName = "peso_medio")
    val peso_medio: Double
)

@JacksonXmlRootElement(localName = "animales")
data class AnimalesWrapper(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "animal")
    val listaAnimales: List<AnimalXML> = emptyList()
)

@Serializable
data class AnimalJSON(
    @SerialName("id_animal") val id_Animal: Int,
    @SerialName("nombre") val nombre: String,
    @SerialName("origen") val origen: String,
    @SerialName("esperanza_vida") val esperanza_vida: Int,
    @SerialName("peso_medio") val peso_medio: Double
)


fun main() {
    val rutaXML = Path.of("datos", "fauna.xml")
    val rutaJSON = Path.of("datos", "fauna.json")

    var repite: Boolean = true

    while (repite) {
        try {

        println("--------------------------------------")
        println("----------- MENÚ PRINCIPAL -----------")
        println("--------------------------------------")
        println("1. Gestión CSV")
        println("2. Leer datos desde XML")
        println("3. Leer datos desde JSON")
        println("0. Salir")
        val eleccion: Int = readln().toInt()
        when (eleccion) {
            1 -> menuCSV()
            2 -> leerXML(rutaXML)
            3 -> leerJSON(rutaJSON)
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
                3 -> modificarCSV(ruta)
                4 -> eliminarCSV(ruta)
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

        val filas: List<Map<String, String>> = reader.readAllWithHeader(ruta.toFile())

        animales = filas.mapNotNull { columnas ->
            try {
                val id_animal = columnas["id_animal"]?.trim()?.toInt()!!
                val nombre = columnas["nombre"]?.trim()!!
                val origen = columnas["origen"]?.trim()!!
                val esperanza_vida = columnas["esperanza_vida"]?.trim()?.toInt()!!
                val peso_medio = columnas["peso_medio"]?.trim()?.toDouble()!!
                Animal(id_animal, nombre, origen, esperanza_vida, peso_medio)
            } catch (e: Exception) {
                println("Fila inválida ignorada: $columnas ")
                null
            }
        }
        println("--- Información leída con éxito de: $ruta")
        for (a in animales) {
            println(a)
        }
    }
    return animales
}
fun anadirCSV(ruta: Path) {
    val animales = leerCSV(ruta).toMutableList()
    var bandera: Boolean = true

    while (bandera) {
        try {
            println("Dame una id válida")
            val id: Int = readln().toInt()

            if (animales.any { it.id_animal == id }) {
                println("El ID ya existe en el fichero. Intenta con otro.")
            } else {
                println("Dame el nombre:")
                val nombre = readln()

                println("Dame el origen:")
                val origen = readln()

                println("Dame la esperanza de vida (número entero):")
                val esperanza_vida = readln().toInt()

                println("Dame el peso medio (número decimal):")
                val peso_medio = readln().toDouble()

                val nuevoAnimal = Animal(id, nombre, origen, esperanza_vida, peso_medio)
                animales.add(nuevoAnimal)

                val fichero: File = ruta.toFile()
                csvWriter {
                    delimiter = ';'
                }.writeAll(

                    listOf(listOf("id_animal", "nombre", "origen", "esperanza_vida", "peso_medio")) +
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
                bandera = false
            }
        } catch (e: NumberFormatException) {
            println("Error: Uno de los datos numéricos introducidos no es válido.")
        } catch (e: Exception) {
            println("Error al escribir el fichero CSV: ${e.message}")
            bandera = false
        }
    }
}

fun modificarCSV (ruta: Path) {
    val animales = leerCSV(ruta).toMutableList()
    var bandera: Boolean = true
    while (bandera) {
        try {
            println("Introduce el ID del registro a modificar:")
            val id: Int = readln().toInt()

            val animalEncontrado = animales.find { it.id_animal == id }

            if (animalEncontrado == null) {
                println("No se ha encontrado ningún registro con el ID: $id")
                bandera = false
            } else {
                println("Registro encontrado: ${animalEncontrado.nombre}")

                println("Dame el nuevo nombre:")
                val nuevoNombre = readln()

                println("Dame el nuevo origen:")
                val nuevoOrigen = readln()

                println("Dame la nueva esperanza de vida:")
                val nuevaEsperanza = readln().toInt()

                println("Dame el nuevo peso medio:")
                val nuevoPeso = readln().replace(',', '.').toDouble()

                val index = animales.indexOf(animalEncontrado)
                animales[index] = Animal(id, nuevoNombre, nuevoOrigen, nuevaEsperanza, nuevoPeso)

                val fichero: File = ruta.toFile()
                csvWriter {
                    delimiter = ';'
                }.writeAll(
                    listOf(listOf("id_animal", "nombre", "origen", "esperanza_vida", "peso_medio")) +
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
                println("--- Registro modificado e información guardada con éxito.")
                bandera = false
            }
        } catch (e: NumberFormatException) {
            println("Error: Formato numérico incorrecto.")
        } catch (e: Exception) {
            println("Error al modificar el fichero: ${e.message}")
            bandera = false
        }
    }
}
fun eliminarCSV(ruta: Path) {
    val animales = leerCSV(ruta).toMutableList()
    if (animales.isEmpty()) return

    var bandera: Boolean = true
    while (bandera) {
        try {
            println("Introduce el ID del registro a eliminar:")
            val id: Int = readln().toInt()

            val animalEncontrado = animales.find { it.id_animal == id }

            if (animalEncontrado == null) {
                println("No se ha encontrado ningún registro con el ID: $id")
                bandera = false
            } else {
                println("Registro encontrado: ${animalEncontrado.nombre}")
                println("¿Estás seguro de que deseas eliminar este registro? (s/n):")
                val respuesta = readln().trim().lowercase()

                if (respuesta == "s" || respuesta == "si" || respuesta == "sí") {
                    animales.remove(animalEncontrado)

                    val fichero: File = ruta.toFile()
                    csvWriter {
                        delimiter = ';'
                    }.writeAll(
                        listOf(listOf("id_animal", "nombre", "origen", "esperanza_vida", "peso_medio")) +
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
                    println("--- Registro eliminado correctamente.")
                } else {
                    println("Operación cancelada. No se ha eliminado ningún registro.")
                }
                bandera = false
            }
        } catch (e: NumberFormatException) {
            println("Error: Formato numérico incorrecto.")
        } catch (e: Exception) {
            println("Error al eliminar el registro: ${e.message}")
            bandera = false
        }
    }
}

fun leerXML(ruta: Path):List<AnimalXML> {
    var contenedor = AnimalesWrapper(emptyList())

    if (!Files.isReadable(ruta)) {
        println("Error: No se puede leer el fichero en la ruta: $ruta")
    } else {
        val fichero = ruta.toFile()
        val xmlMapper = XmlMapper().registerKotlinModule()

        // Leemos el XML directamente sobre la clase contenedora wrapper
        contenedor = xmlMapper.readValue(fichero)
        println("--- Información leída con éxito de: $ruta")
    }
    println(contenedor.listaAnimales)
    return contenedor.listaAnimales

}
fun leerJSON(ruta: Path): List<AnimalJSON> {
    var animales: List<AnimalJSON> = emptyList()

    if (!Files.isReadable(ruta)) {
        println("Error: No se puede leer el fichero en la ruta: $ruta")
    } else {

        // Leemos el contenido completo del JSON como String
        val jsonString = Files.readString(ruta)

        // Convertimos de texto JSON a una lista de objetos Animal
        animales = Json.decodeFromString<List<AnimalJSON>>(jsonString)
        println("--- Información leída con éxito de: $ruta")
    }
    println(animales)
    return animales
}