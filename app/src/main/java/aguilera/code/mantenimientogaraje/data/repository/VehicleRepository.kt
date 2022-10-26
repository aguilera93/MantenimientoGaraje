package aguilera.code.mantenimientogaraje.data.repository

import aguilera.code.mantenimientogaraje.data.db.VehiculoDao
import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import androidx.lifecycle.LiveData

class VehicleRepository(private val vehiculoDao: VehiculoDao) {

    // get all the events
    fun getAllVehicles(): LiveData<List<Vehiculo>> = vehiculoDao.getAllVehicles()

    // adds an event to our database.
    suspend fun insertVehicle(vehiculo: Vehiculo) {
        vehiculoDao.insertVehicle(vehiculo)
    }

    // deletes an event from database.
    suspend fun deleteVehicle(vehiculo: Vehiculo) {
        vehiculoDao.deleteVehicle(vehiculo)
    }

    // updates an event from database.
    suspend fun updateVehicle(vehiculo: Vehiculo) {
        vehiculoDao.updateVehicle(vehiculo)
    }

    suspend fun updateMaxKmsVehicle(matricula: String, kms: Int) {
        vehiculoDao.updateMaxKmsVehicle(matricula, kms)
    }

    //delete an event by id.
    suspend fun deleteVehicleByMatricula(matricula: String) =
        vehiculoDao.deleteVehicleByMatricula(matricula)

    // delete all events
    suspend fun clearVehicles() = vehiculoDao.clearVehicles()
}