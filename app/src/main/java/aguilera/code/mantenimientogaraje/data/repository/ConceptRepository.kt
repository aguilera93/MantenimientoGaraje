package aguilera.code.mantenimientogaraje.data.repository

import aguilera.code.mantenimientogaraje.data.db.ConceptoDao
import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import androidx.lifecycle.LiveData

class ConceptRepository(private val conceptoDao: ConceptoDao) {

    // get all the events
    fun getAllConcepts(): LiveData<List<Concepto>> = conceptoDao.getAllConcepts()

    suspend fun getConcepts(): List<Concepto> = conceptoDao.getConcepts()

    suspend fun getRememberConcepts(): List<Concepto> = conceptoDao.getRememberConcepts()

    suspend fun getMaxKmsVehicle(matricula: String): Int = conceptoDao.getMaxKmsVehicle(matricula)

    // adds an event to our database.
    suspend fun insertConcept(concepto: Concepto) {
        conceptoDao.insertConcept(concepto)
    }

    // deletes an event from database.
    suspend fun deleteConcept(concepto: Concepto) {
        conceptoDao.deleteConcept(concepto)
    }

    // updates an event from database.
    suspend fun updateConcept(concepto: Concepto) {
        conceptoDao.updateConcept(concepto)
    }

    suspend fun updateFechConcept(id_concept: Int, rFech: String) {
        conceptoDao.updateFechConcept(id_concept, rFech)
    }

    //delete an event by id.
    suspend fun deleteConceptByMatricula(matricula: String) =
        conceptoDao.deleteConceptByMatricula(matricula)

    // delete all events
    suspend fun clearConcepts() = conceptoDao.clearConcepts()

    fun getConceptByMatricula(matricula: String): LiveData<List<Concepto>> =
        conceptoDao.getConceptByMatricula(matricula)

    suspend fun showPreviusConceptByUpdate(concepto: Concepto) {
        conceptoDao.showPreviusConceptByUpdate(concepto.matricula, concepto.concepto)
    }

    suspend fun clearRememberConceptByUpdate(concepto: Concepto) {
        concepto.id_concept?.let { conceptoDao.clearRememberConceptByUpdate(it) }
    }

}