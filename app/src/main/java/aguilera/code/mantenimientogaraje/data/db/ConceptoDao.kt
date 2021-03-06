package aguilera.code.mantenimientogaraje.data.db

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ConceptoDao {

    // adds a new entry to our database.
    // if some data is same/conflict, it'll be replace with new data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConcept(concept: Concepto)

    // deletes an event
    @Delete
    suspend fun deleteConcept(concept: Concepto)

    // updates an event.
    @Update
    suspend fun updateConcept(concept: Concepto)

    // read all the events from eventTable
    // and arrange events in ascending order
    // of their ids
    @Query("Select * from conceptos order by matricula ASC")
    fun getAllConcepts(): LiveData<List<Concepto>>
    // why not use suspend ? because Room does not support LiveData with suspended functions.
    // LiveData already works on a background thread and should be used directly without using coroutines

    // delete all events
    @Query("DELETE FROM conceptos")
    suspend fun clearConcepts()

    //you can use this too, to delete an event by id.
    @Query("DELETE FROM conceptos WHERE matricula = :matricula")
    suspend fun deleteConceptByMatricula(matricula: String)

    @Query("Select * from conceptos WHERE matricula = :matricula")
    fun getConceptByMatricula(matricula: String): LiveData<List<Concepto>>

}