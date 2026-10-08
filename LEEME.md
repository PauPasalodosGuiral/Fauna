# Gestor de fauna

Este es un programa de consola desarrollado en Kotlin para gestionar un catálogo de animales. Los datos se pueden leer, manipular y convertir entre distintos formatos estructurados (CSV, XML, JSON) y se almacenan de manera local en un fichero binario de acceso aleatorio llamado *fauna.bin*.

## 1. Estructura de datos

### **Data Classes:**
```kotlin
data class Animal(
    val id_animal: Int,
    val nombre: String,
    val origen: String,
    val esperanza_vida: Int,
    val peso_medio: Double
)
```

**Estructura del registro binario:**

- **id_animal**: Int - 4 bytes (desplazamiento 0 a 3)
- **nombre**: String - 30 bytes (longitud fija rellenada con espacios en ISO-8859-1, desplazamiento 4 a 33)
- **origen**: String - 30 bytes (longitud fija rellenada con espacios en ISO-8859-1, desplazamiento 34 a 63)
- **esperanza_vida**: Int - 4 bytes (desplazamiento 64 a 67)
- **peso_medio**: Double - 8 bytes (desplazamiento 68 a 75)
- **Tamaño Total del Registro**: 4 + 30 + 30 + 4 + 8 = 76 bytes

---

## 2. Instrucciones de ejecución

- **Requisitos previos**: Asegúrate de tener instalado un JDK (versión 17 o superior).
- **Compilación**: Abre el proyecto en tu IDE (ej. IntelliJ IDEA) y deja que Gradle sincronice las dependencias del archivo `build.gradle.kts`.
- **Ejecución**: Lanza la función main contenida en el archivo de entrada `Main.kt`.
- **Ficheros necesarios**: El programa buscará o generará los ficheros correspondientes (*fauna.csv*, *fauna.xml*, *fauna.json*, *fauna.bin*) dentro del directorio `datos/` en la raíz del proyecto para realizar la lectura, conversión o importación de datos.

---

## 3. Decisiones de diseño

- **Soporte multiformato y transcripción**: Se implementó compatibilidad completa con CSV, XML y JSON mediante librerías especializadas (`kotlin-csv`, `jackson-dataformat-xml` y `kotlinx.serialization`). Esto facilita la interoperabilidad entre diferentes fuentes de información y la conversión directa de datos entre cualquier combinación de formatos.
- **Tamaño del registro en binario**: Se definieron 30 bytes fijos para los campos `nombre` y `origen`. Se considera un margen adecuado para registrar especies de animales y sus procedencias sin desperdiciar almacenamiento.
- **Formato del fichero**: Se optó por usar un fichero `.bin` para la persistencia local de la fauna. El uso de registros con longitud fija de 76 bytes mediante `FileChannel` y `ByteBuffer` permite realizar operaciones CRUD (lectura, adición, modificación y borrado) con acceso aleatorio directo y de forma óptima.