package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.GarageViewModel
import aguilera.code.mantenimientogaraje.databinding.FragmentNewConceptVehicleBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.text.DateFormat
import java.text.SimpleDateFormat

class NewConceptVehicleFragment : Fragment() {

    private var _binding: FragmentNewConceptVehicleBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModal: GarageViewModel
    var eventID: Int? = null

    var matricula: String? = ""
    var edit: Boolean = false

    lateinit var oldConcept: Concepto

    val format = SimpleDateFormat("DD/MM/YYYY")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewConceptVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        //(activity as MainActivity).changeActionBar("$marca $modelo","$matricula")

        matricula = arguments?.getString("matricula")

        val type = arguments?.getString("type")

        if (type == "Edit") {
            edit = true

            oldConcept = arguments?.getSerializable("concept") as Concepto

            eventID = oldConcept.id_concept
        }

        setValues(type)

        // adding click listener to our save button.
        binding.btnSave.setOnClickListener {

            saveValues(type)

            activity?.supportFragmentManager?.popBackStack()
        }

        changeFragmentActionBar()

    }

    fun setValues(type: String?) {
        if (edit) {
            // setting data to edit text.
            binding.etConcepto.setText(oldConcept.concepto)
            eventID = arguments?.getString("id").toString().toIntOrNull()
            binding.etFecha.setText(oldConcept.fecha)
            binding.etPrecio.setText(oldConcept.precio.toString())
            binding.etKMSC.setText(oldConcept.kms.toString())
            binding.etTaller.setText(oldConcept.taller)
            binding.etDetallesC.setText(oldConcept.detalles)
            binding.cbRecordar.isChecked = oldConcept.recordar
            binding.etRFecha.setText(oldConcept.rFecha)
            binding.etRKMS.setText(oldConcept.rKms.toString())
            binding.btnSave.setText("Update Concept")
        } else {
            binding.btnSave.setText("Save Concept")
        }
    }

    fun saveValues(type: String?) {
        val concepto = binding.etConcepto.text.toString()
        val fecha = binding.etFecha.text.toString()
        val precio = binding.etPrecio.text.toString().toFloatOrNull()
        val kms = binding.etKMSC.text.toString().toIntOrNull()
        val taller = binding.etTaller.text.toString()
        val detalles = binding.etDetallesC.text.toString()
        val recordar = binding.cbRecordar.text.toString().toBoolean()
        val rfecha = binding.etRFecha.text.toString()
        val rkms = binding.etRKMS.text.toString().toIntOrNull()
        var visible = true
        if (edit) {
            visible = format.parse(oldConcept.fecha) < format.parse(fecha)
        }

        Log.i("miapp", "${format.parse(oldConcept.fecha)}")
        Log.i("miapp", "${format.parse(fecha)}")
        Log.i("miapp", "$visible")

        val concept = matricula?.let { it1 ->
            Concepto(
                eventID,
                it1,
                concepto,
                fecha,
                kms,
                precio,
                taller,
                detalles,
                recordar,
                rfecha,
                rkms,
                visible
            )
        }
        // checking the type and then saving or updating the data.
        if (edit) {

            if (concepto.isNotEmpty()) {
                binding.etConcepto.error = null
                if (concept != null) {
                    if (visible) {
                        concept.id_concept = null
                        viewModal.insertConcept(concept)
                        oldConcept.visible = false
                        viewModal.updateConcept(oldConcept)
                    } else {
                        viewModal.updateConcept(concept)
                    }
                }
                Toast.makeText(requireActivity(), "Concepto Modificado", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            if (concepto.isNotEmpty()) {
                binding.etConceptoLay.error = null
                // if the string is not empty we are calling
                // add event method to add data to our room database.
                //why id null? because id is auto generate
                if (concept != null) {
                    viewModal.insertConcept(concept)
                }
                Toast.makeText(requireActivity(), "Concepto Añadido", Toast.LENGTH_LONG).show()
            } else {
                binding.etConceptoLay.error = "Debe introducir un concepto valido"
            }
        }
    }

    fun changeFragmentActionBar() {
        if (edit) {
            (activity as MainActivity).changeActionBar(
                "Modificar ${binding.etConcepto.text.toString()}",
                "$matricula"
            )
        } else {
            (activity as MainActivity).changeActionBar("Añadir Concepto a", "$matricula")
        }
    }
}