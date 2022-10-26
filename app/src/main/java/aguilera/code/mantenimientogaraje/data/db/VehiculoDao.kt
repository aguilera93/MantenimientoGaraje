package aguilera.code.mantenimientogaraje.data.db

import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VehiculoDao {

    // adds a new entry to our database.
    // if some data is same/conflict, it'll be replace with new data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehiculo)

    // deletes an event
    @Delete
    suspend fun deleteVehicle(vehicle: Vehiculo)

    // updates an event.
    @Update
    suspend fun updateVehicle(vehicle: Vehiculo)

    //
    @Query("UPDATE vehiculos SET kms=:kms WHERE matricula = :matricula")
    suspend fun updateMaxKmsVehicle(matricula: String, kms: Int)

    // read all the events from eventTable
    // and arrange events in ascending order
    // of their ids
    @Query("Select * from vehiculos order by matricula ASC")
    fun getAllVehicles(): LiveData<List<Vehiculo>>
    // why not use suspend ? because Room does not support LiveData with suspended functions.
    // LiveData already works on a background thread and should be used directly without using coroutines

    // delete all events
    @Query("DELETE FROM vehiculos")
    suspend fun clearVehicles()

    //you can use this too, to delete an event by id.
    @Query("DELETE FROM vehiculos WHERE matricula = :matricula")
    suspend fun deleteVehicleByMatricula(matricula: String)

}