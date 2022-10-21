package aguilera.code.mantenimientogaraje.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "vehiculos")
data class Vehiculo(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "matricula")
    var matricula: String,

    @ColumnInfo(name = "marca")
    var marca: String?,

    @ColumnInfo(name = "modelo")
    var modelo: String?,

    @ColumnInfo(name = "kms")
    var kms: Int?,

    @ColumnInfo(name = "vin")
    var vin: String?,

    @ColumnInfo(name = "detalles")
    var detalles: String?
) : Serializable {

    override fun toString(): String {
        return "$matricula, $marca, $modelo', $kms', $vin, $detalles"
    }
}