package aguilera.code.mantenimientogaraje.data.ui

import aguilera.code.mantenimientogaraje.MainActivity
import aguilera.code.mantenimientogaraje.data.db.GarageDatabase
import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import aguilera.code.mantenimientogaraje.data.repository.ConceptRepository
import aguilera.code.mantenimientogaraje.data.repository.VehicleRepository
import android.app.Application
import android.content.ClipData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GarageViewModel(
    application: Application
) : AndroidViewModel(application) {

    val allVehicles: LiveData<List<Vehiculo>>
    val repositoryV: VehicleRepository

    val allConcepts: LiveData<List<Concepto>>
    val repositoryC: ConceptRepository

    // initialize dao, repository and all events
    init {
        val daoV = GarageDatabase.getDatabase(application).getVehiculoDao()
        val daoC = GarageDatabase.getDatabase(application).getConceptoDao()
        repositoryV = VehicleRepository(daoV)
        allVehicles = repositoryV.getAllVehicles()

        repositoryC = ConceptRepository(daoC)
        allConcepts = repositoryC.getAllConcepts()
    }

    suspend fun getVehicleByMatricula(matricula: String): Vehiculo =
        repositoryV.getVehicleByMatricula(matricula)

    suspend fun checkVehiculo(matricula: String): Int = repositoryV.checkVehiculo(matricula)

    fun insertVehicle(vehiculo: Vehiculo) =
        viewModelScope.launch(Dispatchers.IO) { repositoryV.insertVehicle(vehiculo) }

    fun updateVehicle(vehiculo: Vehiculo) =
        viewModelScope.launch(Dispatchers.IO) { repositoryV.updateVehicle(vehiculo) }

    fun updateMaxKmsVehicle(matricula: String, kms: Int) =
        viewModelScope.launch(Dispatchers.IO) { repositoryV.updateMaxKmsVehicle(matricula, kms) }

    fun deleteVehicle(vehiculo: Vehiculo) =
        viewModelScope.launch(Dispatchers.IO) { repositoryV.deleteVehicle(vehiculo) }

    fun deleteVehicleByMatricula(matricula: String) =
        viewModelScope.launch(Dispatchers.IO) { repositoryV.deleteVehicleByMatricula(matricula) }

    fun clearVehicles() =
        viewModelScope.launch(Dispatchers.IO) { repositoryV.clearVehicles() }

    //----------------------------------------------------------------------------------------------
    @JvmName("getConcepts1")
    suspend fun getConcepts(): List<Concepto> =
        repositoryC.getConcepts()

    suspend fun getConceptsByMatricula(matricula: String): List<Concepto> =
        repositoryC.getConceptsByMatricula(matricula)

    suspend fun getRememberConcepts(): List<Concepto> =
        repositoryC.getRememberConcepts()

    suspend fun getMaxKmsVehicle(matricula: String): Int = repositoryC.getMaxKmsVehicle(matricula)

    suspend fun checkConcept(concepto: String, matricula: String): Int =
        repositoryC.checkConcept(concepto,matricula)

    fun insertConcept(concepto: Concepto) =
        viewModelScope.launch(Dispatchers.IO) { repositoryC.insertConcept(concepto) }

    fun updateConcept(concepto: Concepto) =
        viewModelScope.launch(Dispatchers.IO) { repositoryC.updateConcept(concepto) }

    fun updateFechConcept(id_concepto: Int, rFecha: String) =
        viewModelScope.launch(Dispatchers.IO) { repositoryC.updateFechConcept(id_concepto, rFecha) }

    fun deleteConcept(concepto: Concepto) =
        viewModelScope.launch(Dispatchers.IO) { repositoryC.deleteConcept(concepto) }

    fun showPreviusConceptByUpdate(concepto: Concepto) =
        viewModelScope.launch(Dispatchers.IO) { repositoryC.showPreviusConceptByUpdate(concepto) }

    fun clearRememberConceptByUpdate(concepto: Concepto) =
        viewModelScope.launch(Dispatchers.IO) { repositoryC.clearRememberConceptByUpdate(concepto) }

    fun deleteConceptsByMatricula(matricula: String) =
        viewModelScope.launch(Dispatchers.IO) { repositoryC.deleteConceptsByMatricula(matricula) }

    fun clearConcepts() =
        viewModelScope.launch(Dispatchers.IO) { repositoryC.clearConcepts() }

}