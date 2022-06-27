package aguilera.code.mantenimientogaraje.data.db

import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Vehiculo::class], version = 1, exportSchema = false)
abstract class GarageDatabase : RoomDatabase() {

    abstract fun getVehiculoDao(): VehiculoDao

    companion object {
        // Volatile annotation means any change to this field
        // are immediately made visible to other threads.
        @Volatile
        private var INSTANCE: GarageDatabase? = null

        private const val DB_NAME = "garage_database.db"

        fun getDatabase(context: Context): GarageDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            // here synchronised used for blocking the other thread
            // from accessing another while in a specific code execution.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GarageDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}