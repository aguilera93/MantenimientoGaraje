package aguilera.code.mantenimientogaraje.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "conceptos")
data class Concepto(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_concept")
    var id_concept: Int?,

    @ColumnInfo(name = "matricula")
    var matricula: String,

    @ColumnInfo(name = "concepto")
    var concepto: String,

    @ColumnInfo(name = "fecha")
    var fecha: String,

    @ColumnInfo(name = "kms")
    var kms: Int?,

    @ColumnInfo(name = "precio")
    var precio: Float?,

    @ColumnInfo(name = "taller")
    var taller: String?,

    @ColumnInfo(name = "detalles")
    var detalles: String?,

    @ColumnInfo(name = "recordar")
    var recordar: Boolean,

    @ColumnInfo(name = "rfecha")
    var rFecha: String?,

    @ColumnInfo(name = "rkms")
    var rKms: Int? = null,

    @ColumnInfo(name = "visible")
    var visible: Boolean = true
): Serializable