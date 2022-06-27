package aguilera.code.mantenimientogaraje.data.ui

import aguilera.code.mantenimientogaraje.data.db.GarageDatabase
import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import aguilera.code.mantenimientogaraje.data.repository.VehicleRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VehicleViewModel(
    application: Application
) : AndroidViewModel(application) {


    val allVehicles: LiveData<List<Vehiculo>>
    val repository: VehicleRepository

    // initialize dao, repository and all events
    init {
        val dao = GarageDatabase.getDatabase(application).getVehiculoDao()
        repository = VehicleRepository(dao)
        allVehicles = repository.getAllVehicles()
    }

    fun insertVehicle(vehiculo: Vehiculo) =
        viewModelScope.launch(Dispatchers.IO) { repository.insertVehicle(vehiculo) }

    fun updateVehicle(vehiculo: Vehiculo) =
        viewModelScope.launch(Dispatchers.IO) { repository.updateVehicle(vehiculo) }

    fun deleteVehicle(vehiculo: Vehiculo) =
        viewModelScope.launch(Dispatchers.IO) { repository.deleteVehicle(vehiculo) }

    fun deleteVehicleByMatricula(matricula: String) =
        viewModelScope.launch(Dispatchers.IO) { repository.deleteVehicleByMatricula(matricula) }

    fun clearVehicles() =
        viewModelScope.launch(Dispatchers.IO) { repository.clearVehicles() }

}