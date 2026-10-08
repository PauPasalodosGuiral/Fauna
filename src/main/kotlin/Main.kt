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
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
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
const val TAMANO_ID = Int.SIZE_BYTES // 4 bytes
const val TAMANO_NOMBRE = 30 // 30 bytes
const val TAMANO_ORIGEN = 30 // 30 bytes
const val TAMANO_ESPERANZA = Int.SIZE_BYTES // 4 bytes
const val TAMANO_PESO = Double.SIZE_BYTES // 8 bytes
const val TAMANO_REGISTRO = TAMANO_ID + TAMANO_NOMBRE + TAMANO_ORIGEN + TAMANO_ESPERANZA + TAMANO_PESO // 76 bytes

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
        println("4. Convertir JSON a CSV")
        println("5. Convertir JSON a XML")
        println("6. Convertir XML a JSON")
        println("7. Convertir XML a CSV")
        println("8. Convertir CSV a JSON")
        println("9. Convertir CSV a XML")
        println("10. Gestión fichero BIN")
        println("0. Salir")
        val eleccion: Int = readln().toInt()
        when (eleccion) {
            1 -> menuCSV()
            2 -> leerXML(rutaXML)
            3 -> leerJSON(rutaJSON)
            4 -> transcripcionJSONaCSV()
            5 -> transcripcionJSONaXML()
            6 -> transcripcionXMLaJSON()
            7 -> transcripcionXMLaCSV()
            8 -> transcripcionCSVaJSON()
            9 -> transcripcionCSVaXML()
            10 -> menuBIN()
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
fun menuBIN() {
    var repite: Boolean = true
    val ruta = Path.of("datos", "fauna.bin")
    while (repite) {
        try {
            println("--------------------------------------")
            println("---------- CRUD fichero BIN ----------")
            println("--------------------------------------")
            println("1. Importar datos desde fichero de texto plano.")
            println("2. Leer información del fichero binario.")
            println("3. Añadir un registro nuevo")
            println("4. Modificar un registro existente (por ID)")
            println("5. Eliminar un registro existente (por ID)")
            println("0. Volver al menú principal")
            val eleccion: Int = readln().toInt()
            when (eleccion) {
                1 -> importarDatos()
                2 -> leerBinario()
                3 -> añadirRegistro()
                4 -> modificarRegistro()
                5 -> eliminarRegistro()
                0 -> repite = false
                else -> println("Escoge un numero del menu")
            }
        } catch (e: NumberFormatException) {
            println("Error")
        }
    }

}
fun importarDatos() {
    val rutaCSV = Path.of("datos", "fauna.csv")
    val rutaBin = Path.of("datos", "fauna.bin")

    try {
        if (rutaBin.parent != null) {
            Files.createDirectories(rutaBin.parent)
        }

        val animales = leerCSV(rutaCSV)

        FileChannel.open(
            rutaBin,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        ).use { canal ->
            for (animal in animales) {
                val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

                buffer.putInt(animal.id_animal)

                val nombreBytes = animal.nombre
                    .padEnd(TAMANO_NOMBRE, ' ')
                    .toByteArray(Charsets.ISO_8859_1)
                buffer.put(nombreBytes, 0, TAMANO_NOMBRE)

                val origenBytes = animal.origen
                    .padEnd(TAMANO_ORIGEN, ' ')
                    .toByteArray(Charsets.ISO_8859_1)
                buffer.put(origenBytes, 0, TAMANO_ORIGEN)

                buffer.putInt(animal.esperanza_vida)

                buffer.putDouble(animal.peso_medio)

                buffer.flip()
                while (buffer.hasRemaining()) {
                    canal.write(buffer)
                }
            }
        }
        println("--- Datos importados con éxito en '${rutaBin.fileName}' (${animales.size} registros).")
    } catch (e: Exception) {
        println("Error al importar los datos: ${e.message}")
    }
}
fun leerBinario() {
    val rutaBin = Path.of("datos", "fauna.bin")

    if (!Files.isReadable(rutaBin)) {
        println("Error No se encuentra o no se puede leer el fichero binario.")
        return
    }

    try {
        FileChannel.open(rutaBin, StandardOpenOption.READ).use { canal ->
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
            println("\n--- Contenido leído del fichero binario (.bin): ---")

            while (canal.read(buffer) > 0) {
                buffer.flip()

                val id = buffer.getInt()

                val nombreBytes = ByteArray(TAMANO_NOMBRE)
                buffer.get(nombreBytes)
                val nombre = String(nombreBytes, Charsets.ISO_8859_1).trim()

                val origenBytes = ByteArray(TAMANO_ORIGEN)
                buffer.get(origenBytes)
                val origen = String(origenBytes, Charsets.ISO_8859_1).trim()

                val esperanza = buffer.getInt()
                val peso = buffer.getDouble()

                val animal = Animal(id, nombre, origen, esperanza, peso)
                println(" - ID: ${animal.id_animal}, " +
                        "Nombre: ${animal.nombre}, " +
                        "Origen: ${animal.origen}, " +
                        "Esperanza de vida: ${animal.esperanza_vida} " +
                        "años, " +
                        "Peso medio: ${animal.peso_medio} kg")

                buffer.clear()
            }
        }
    } catch (e: Exception) {
        println("Error al leer el fichero binario: ${e.message}")
    }
}
fun añadirRegistro() {
    val rutaBin = Path.of("datos", "fauna.bin")

    if (rutaBin.parent != null) {
        Files.createDirectories(rutaBin.parent)
    }

    var idValido: Int? = null

    while (idValido == null) {
        try {
            print("Introduce el ID del nuevo animal: ")
            val inputId = readln().toInt()
            var existe = false

            if (Files.exists(rutaBin) && Files.size(rutaBin) > 0) {
                FileChannel.open(rutaBin, StandardOpenOption.READ).use { canal ->
                    val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
                    while (canal.read(buffer) > 0 && !existe) {
                        buffer.flip()
                        val idExistente = buffer.getInt()
                        if (idExistente == inputId) {
                            existe = true
                        }
                        buffer.clear()
                    }
                }
            }

            if (existe) {
                println("El ID $inputId ya existe en el fichero binario. Intenta con otro.")
            } else {
                idValido = inputId
            }
        } catch (e: NumberFormatException) {
            println("Error El ID debe ser un número entero válido.")
        }
    }

    print("Dame el nombre: ")
    val nombreInput = readln()

    print("Dame el origen: ")
    val origenInput = readln()

    var esperanzaInput: Int? = null
    while (esperanzaInput == null) {
        try {
            print("Dame la esperanza de vida (número entero): ")
            esperanzaInput = readln().toInt()
        } catch (e: NumberFormatException) {
            println("Error Formato incorrecto. Debe ser un entero.")
        }
    }

    var pesoInput: Double? = null
    while (pesoInput == null) {
        try {
            print("Dame el peso medio (número decimal): ")
            pesoInput = readln().replace(',', '.').toDouble()
        } catch (e: NumberFormatException) {
            println("Error Formato incorrecto. Debe ser un número decimal.")
        }
    }

    // Escritura al final del fichero (APPEND)
    try {
        FileChannel.open(
            rutaBin,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        ).use { canal ->
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

            buffer.putInt(idValido)

            val nombreBytes = nombreInput.padEnd(TAMANO_NOMBRE, ' ').toByteArray(Charsets.ISO_8859_1)
            buffer.put(nombreBytes, 0, TAMANO_NOMBRE)

            val origenBytes = origenInput.padEnd(TAMANO_ORIGEN, ' ').toByteArray(Charsets.ISO_8859_1)
            buffer.put(origenBytes, 0, TAMANO_ORIGEN)

            buffer.putInt(esperanzaInput)
            buffer.putDouble(pesoInput)

            buffer.flip()
            while (buffer.hasRemaining()) {
                canal.write(buffer)
            }
            println("--- Registro con ID $idValido añadido correctamente al final del fichero binario.")
        }
    } catch (e: Exception) {
        println("Error al añadir el registro: ${e.message}")
    }
}
fun modificarRegistro() {
    val rutaBin = Path.of("datos", "fauna.bin")

    if (!Files.isReadable(rutaBin)) {
        println("El fichero binario no existe o no se puede leer.")
        return
    }

    var idModificar: Int? = null
    while (idModificar == null) {
        try {
            print("Introduce el ID del registro a modificar: ")
            idModificar = readln().toInt()
        } catch (e: NumberFormatException) {
            println("Error El ID debe ser un entero válido.")
        }
    }

    try {
        FileChannel.open(rutaBin, StandardOpenOption.READ, StandardOpenOption.WRITE).use { canal ->
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
            var encontrado = false

            while (canal.read(buffer) > 0 && !encontrado) {
                val posicionActual = canal.position()
                buffer.flip()

                val id = buffer.getInt()
                if (id == idModificar) {
                    encontrado = true

                    val nombreBytes = ByteArray(TAMANO_NOMBRE)
                    buffer.get(nombreBytes)
                    val nombreExistente = String(nombreBytes, Charsets.ISO_8859_1).trim()

                    println("Registro encontrado: $nombreExistente (ID: $idModificar)")

                    print("Dame el nuevo nombre: ")
                    val nuevoNombre = readln()

                    print("Dame el nuevo origen: ")
                    val nuevoOrigen = readln()

                    var nuevaEsperanza: Int? = null
                    while (nuevaEsperanza == null) {
                        try {
                            print("Dame la nueva esperanza de vida: ")
                            nuevaEsperanza = readln().toInt()
                        } catch (e: NumberFormatException) {
                            println("Error Formato numérico incorrecto.")
                        }
                    }

                    var nuevoPeso: Double? = null
                    while (nuevoPeso == null) {
                        try {
                            print("Dame el nuevo peso medio: ")
                            nuevoPeso = readln().replace(',', '.').toDouble()
                        } catch (e: NumberFormatException) {
                            println("Error Formato numérico incorrecto.")
                        }
                    }

                    val inicioRegistro = posicionActual - TAMANO_REGISTRO
                    canal.position(inicioRegistro)

                    val bufferModificado = ByteBuffer.allocate(TAMANO_REGISTRO)
                    bufferModificado.putInt(idModificar)

                    val nBytes = nuevoNombre.padEnd(TAMANO_NOMBRE, ' ').toByteArray(Charsets.ISO_8859_1)
                    bufferModificado.put(nBytes, 0, TAMANO_NOMBRE)

                    val oBytes = nuevoOrigen.padEnd(TAMANO_ORIGEN, ' ').toByteArray(Charsets.ISO_8859_1)
                    bufferModificado.put(oBytes, 0, TAMANO_ORIGEN)

                    bufferModificado.putInt(nuevaEsperanza)
                    bufferModificado.putDouble(nuevoPeso)

                    bufferModificado.flip()
                    while (bufferModificado.hasRemaining()) {
                        canal.write(bufferModificado)
                    }

                    println("--- Registro con ID $idModificar modificado correctamente.")
                }
                buffer.clear()
            }

            if (!encontrado) {
                println("No se encontró ningún registro con el ID: $idModificar")
            }
        }
    } catch (e: Exception) {
        println("Error al modificar el registro: ${e.message}")
    }
}

fun eliminarRegistro() {
    val rutaBin = Path.of("datos", "fauna.bin")
    val rutaTemporal = Path.of("datos", "fauna.bin.tmp")

    if (!Files.isReadable(rutaBin)) {
        println("El fichero binario no existe o no se puede leer.")
        return
    }

    var idEliminar: Int? = null
    while (idEliminar == null) {
        try {
            print("Introduce el ID del registro a eliminar: ")
            idEliminar = readln().toInt()
        } catch (e: NumberFormatException) {
            println("Error El ID debe ser un número entero.")
        }
    }

    var encontrado = false

    try {
        FileChannel.open(rutaBin, StandardOpenOption.READ).use { canalLectura ->
            FileChannel.open(
                rutaTemporal,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            ).use { canalEscritura ->
                val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

                while (canalLectura.read(buffer) > 0) {
                    buffer.flip()
                    val id = buffer.getInt()

                    if (id == idEliminar) {
                        encontrado = true

                        val nombreBytes = ByteArray(TAMANO_NOMBRE)
                        buffer.get(nombreBytes)
                        val nombre = String(nombreBytes, Charsets.ISO_8859_1).trim()

                        println("Registro encontrado: $nombre (ID: $idEliminar)")
                        print("¿Estás seguro de que deseas eliminar este registro? (s/n): ")
                        val respuesta = readln().trim().lowercase()

                        if (respuesta == "s" || respuesta == "si" || respuesta == "sí") {
                            println("Confirmado. El registro será eliminado.")
                        } else {
                            println("Operación cancelada. El registro se conservará.")
                            buffer.rewind()
                            canalEscritura.write(buffer)
                        }
                    } else {
                        buffer.rewind()
                        canalEscritura.write(buffer)
                    }
                    buffer.clear()
                }
            }
        }

        if (encontrado) {
            Files.move(rutaTemporal, rutaBin, StandardCopyOption.REPLACE_EXISTING)
        } else {
            Files.deleteIfExists(rutaTemporal)
            println("No se encontró ningún registro con el ID: $idEliminar")
        }
    } catch (e: Exception) {
        println("Error durante la eliminación del registro: ${e.message}")
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
            println("Error Uno de los datos numéricos introducidos no es válido.")
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
            println("Error Formato numérico incorrecto.")
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
            println("Error Formato numérico incorrecto.")
        } catch (e: Exception) {
            println("Error al eliminar el registro: ${e.message}")
            bandera = false
        }
    }
}
fun leerXML(ruta: Path):List<AnimalXML> {
    var contenedor = AnimalesWrapper(emptyList())

    if (!Files.isReadable(ruta)) {
        println("Error No se puede leer el fichero en la ruta: $ruta")
    } else {
        val fichero = ruta.toFile()
        val xmlMapper = XmlMapper().registerKotlinModule()

        contenedor = xmlMapper.readValue(fichero)
        println("--- Información leída con éxito de: $ruta")
    }
    println(contenedor.listaAnimales)
    return contenedor.listaAnimales

}
fun leerJSON(ruta: Path): List<AnimalJSON> {
    var animales: List<AnimalJSON> = emptyList()

    if (!Files.isReadable(ruta)) {
        println("Error No se puede leer el fichero en la ruta: $ruta")
    } else {
        val jsonString = Files.readString(ruta)
        animales = Json.decodeFromString<List<AnimalJSON>>(jsonString)
        println("--- Información leída con éxito de: $ruta")
    }
    println(animales)
    return animales
}
fun transcripcionXMLaCSV() {
    val entradaXML: Path = Path.of("datos", "fauna.xml")
    val ruta: Path = Path.of("datos", "Transcripcion.csv")
    val datos = leerXML(entradaXML)

    try {
        val fichero: File = ruta.toFile()
        csvWriter {
            delimiter = ';'
        }.writeAll(
            listOf(listOf("id_animal", "nombre", "origen", "esperanza_vida", "peso_medio")) +
                    datos.map { animal ->
                        listOf(
                            animal.id_animal.toString(),
                            animal.nombre,
                            animal.origen,
                            animal.esperanza_vida.toString(),
                            animal.peso_medio.toString()
                        )
                    }, fichero
        )
        println("Archivo CSV transcrito desde XML exitosamente")
    } catch (e: Exception) {
        println("Error al transcribir el archivo XML a CSV: ${e.message}")
    }
}
fun transcripcionJSONaCSV() {
    val entradaJSON: Path = Path.of("datos", "fauna.json")
    val ruta: Path = Path.of("datos", "Transcripcion.csv")
    val datos = leerJSON(entradaJSON)

    try {
        val fichero: File = ruta.toFile()
        csvWriter {
            delimiter = ';'
        }.writeAll(
            listOf(listOf("id_animal", "nombre", "origen", "esperanza_vida", "peso_medio")) +
                    datos.map { animal ->
                        listOf(
                            animal.id_Animal.toString(),
                            animal.nombre,
                            animal.origen,
                            animal.esperanza_vida.toString(),
                            animal.peso_medio.toString()
                        )
                    }, fichero
        )
        println("Archivo CSV transcrito desde JSON exitosamente")
    } catch (e: Exception) {
        println("Error al transcribir el archivo JSON a CSV: ${e.message}")
    }
}
fun transcripcionCSVaXML() {
    val entradaCSV: Path = Path.of("datos", "fauna.csv")
    val ruta: Path = Path.of("datos", "Transcripcion.xml")
    val datos = leerCSV(entradaCSV)

    val datosXML: List<AnimalXML> = datos.map { animal ->
        AnimalXML(
            id_animal = animal.id_animal,
            nombre = animal.nombre,
            origen = animal.origen,
            esperanza_vida = animal.esperanza_vida,
            peso_medio = animal.peso_medio
        )
    }

    try {
        val fichero = ruta.toFile()
        val contenedor = AnimalesWrapper(datosXML)
        val xmlMapper = XmlMapper().registerKotlinModule()

        val xmlString = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contenedor)
        fichero.writeText(xmlString)

        println("--- Información guardada en XML: $fichero")
    } catch (e: Exception) {
        println("Error al guardar XML: ${e.message}")
    }
}
fun transcripcionJSONaXML() {
    val entradaJSON: Path = Path.of("datos", "fauna.json")
    val ruta: Path = Path.of("datos", "Transcripcion.xml")
    val datos = leerJSON(entradaJSON)

    val datosXML: List<AnimalXML> = datos.map { animal ->
        AnimalXML(
            id_animal = animal.id_Animal,
            nombre = animal.nombre,
            origen = animal.origen,
            esperanza_vida = animal.esperanza_vida,
            peso_medio = animal.peso_medio
        )
    }

    try {
        val fichero = ruta.toFile()
        val contenedor = AnimalesWrapper(datosXML)
        val xmlMapper = XmlMapper().registerKotlinModule()

        val xmlString = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contenedor)
        fichero.writeText(xmlString)

        println("--- Información guardada en XML: $fichero")
    } catch (e: Exception) {
        println("Error al guardar XML: ${e.message}")
    }
}
fun transcripcionCSVaJSON() {
    val entradaCSV: Path = Path.of("datos", "fauna.csv")
    val ruta: Path = Path.of("datos", "Transcripcion.json")

    val datos = leerCSV(entradaCSV)

    val datosJSON: List<AnimalJSON> = datos.map { animal ->
        AnimalJSON(
            id_Animal = animal.id_animal,
            nombre = animal.nombre,
            origen = animal.origen,
            esperanza_vida = animal.esperanza_vida,
            peso_medio = animal.peso_medio
        )
    }

    try {
        val jsonConfigurador = Json { prettyPrint = true }
        val jsonString = jsonConfigurador.encodeToString(datosJSON)

        Files.writeString(ruta, jsonString)
        println("--- Información guardada en: $ruta")
    } catch (e: Exception) {
        println("Error al guardar JSON: ${e.message}")
    }
}
fun transcripcionXMLaJSON() {
    val entradaXML: Path = Path.of("datos", "fauna.xml")
    val ruta: Path = Path.of("datos", "Transcripcion.json")

    val datos = leerXML(entradaXML)

    val datosJSON: List<AnimalJSON> = datos.map { animal ->
        AnimalJSON(
            id_Animal = animal.id_animal,
            nombre = animal.nombre,
            origen = animal.origen,
            esperanza_vida = animal.esperanza_vida,
            peso_medio = animal.peso_medio
        )
    }

    try {
        val jsonConfigurador = Json { prettyPrint = true }
        val jsonString = jsonConfigurador.encodeToString(datosJSON)

        Files.writeString(ruta, jsonString)
        println("--- Información guardada en: $ruta")
    } catch (e: Exception) {
        println("Error al guardar JSON: ${e.message}")
    }
}
