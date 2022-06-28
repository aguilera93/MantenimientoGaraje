package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import aguilera.code.mantenimientogaraje.data.ui.GarageViewModel
import aguilera.code.mantenimientogaraje.databinding.FragmentMainBinding
import aguilera.code.mantenimientogaraje.databinding.FragmentNewConceptVehicleBinding
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class NewConceptVehicleFragment : Fragment() {

    private var _binding: FragmentNewConceptVehicleBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModal: GarageViewModel
    var eventID: Int? = null

    var matricula: String? = ""
    var edit: Boolean = false

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
        if (type == "Edit") edit = true

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
            binding.etConcepto.setText(arguments?.getString("concepto"))
            eventID = arguments?.getString("id").toString().toIntOrNull()
            binding.etFecha.setText(arguments?.getString("fecha"))
            binding.etPrecio.setText(arguments?.getString("precio"))
            binding.etKMSC.setText(arguments?.getString("kms"))
            binding.etTaller.setText(arguments?.getString("taller"))
            binding.etDetallesC.setText(arguments?.getString("detalles"))
            binding.cbRecordar.setText(arguments?.getString("recordar"))
            binding.etRFecha.setText(arguments?.getString("rfecha"))
            binding.etRKMS.setText(arguments?.getString("rkms"))
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
                rkms
            )
        }
        // checking the type and then saving or updating the data.
        if (edit) {

            if (concepto.isNotEmpty()) {
                binding.etConcepto.error = null
                if (concept != null) {
                    viewModal.updateConcept(concept)
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