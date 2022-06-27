package aguilera.code.mantenimientogaraje.data.db.entity

import java.io.Serializable
import java.util.*

class Concepto() : Serializable {
    var concepto: String = ""
    var fecha: String? = null
    var kms: Int? = null
    var precio: Float? = null
    var taller: String? = null
    var detalles: String? = null
    var recordar: Boolean = false
    var rFecha: String? = null
    var rKms: Int? = null
}