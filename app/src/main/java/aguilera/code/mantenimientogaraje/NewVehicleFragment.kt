package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import aguilera.code.mantenimientogaraje.data.ui.GarageViewModel
import aguilera.code.mantenimientogaraje.databinding.FragmentNewVehicleBinding
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class NewVehicleFragment : Fragment() {

    private var binding: FragmentNewVehicleBinding? = null

    lateinit var viewModal: GarageViewModel
    var matricula: String = ""
    var edit: Boolean = false
    lateinit var oldVehicle: Vehiculo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewVehicleBinding.inflate(inflater, container, false)

        // initializes the viewmodel.
        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        getValues()

        setValues()

        // adding click listener to our save button.
        binding?.btnSave?.setOnClickListener {
            saveValues()
        }

        changeFragmentActionBar()
    }

    fun getValues() {
        matricula = arguments?.getString("matricula").toString()

        if (arguments?.getString("type") == "Edit") {
            edit = true
            oldVehicle = arguments?.getSerializable("vehiculo") as Vehiculo
        }
    }

    fun setValues() {

        if (edit) {
            binding?.etMatricula?.setText(matricula)
            //Esconde la vista que contiene el campo de la matricula
            binding?.etMatriculaLay?.isEnabled = false
            //------------------------------------------------------
            binding?.etMarca?.setText(oldVehicle.marca)
            binding?.etModelo?.setText(oldVehicle.modelo)
            binding?.etKMSV?.setText(if (oldVehicle.kms == null) "" else oldVehicle.kms.toString())
            binding?.etVIN?.setText(oldVehicle.vin)
            binding?.etDetallesV?.setText(oldVehicle.detalles)
            binding?.btnSave?.setText(getString(R.string.btn_update))
        } else {
            binding?.btnSave?.setText(getString(R.string.btn_save))
        }
    }

    fun saveValues() {
        val matricula = binding?.etMatricula?.text.toString().uppercase()
        val marca = binding?.etMarca?.text.toString()
        val modelo = binding?.etModelo?.text.toString()
        val kms = binding?.etKMSV?.text.toString().toIntOrNull()
        val vin = binding?.etVIN?.text.toString()
        val detalles = binding?.etDetallesV?.text.toString()

        val vehiculo = Vehiculo(matricula, marca, modelo, kms, vin, detalles)
        // checking the type and then saving or updating the data.
        if (edit) {
            if (!matricula.isNullOrBlank()) {
                binding?.etMatriculaLay?.error = null
                viewModal.updateVehicle(vehiculo)
                (activity as MainActivity).toast("${vehiculo.matricula} ${getString(R.string.update)}")
                activity?.supportFragmentManager?.popBackStack()
            }
        } else {
            if (!matricula.isNullOrBlank()) {
                binding?.etMatriculaLay?.error = null
                viewModal.insertVehicle(vehiculo)
                (activity as MainActivity).toast("${vehiculo.matricula} ${getString(R.string.saved)}")
                activity?.supportFragmentManager?.popBackStack()
            } else {
                binding?.etMatriculaLay?.error = getString(R.string.err_campo_obligatorio)
            }
        }
    }

    fun changeFragmentActionBar() {
        if (edit) {
            (activity as MainActivity).changeActionBar(
                getString(R.string.edit_vehicle),""
                //"${binding?.etMatricula?.text.toString()}"
            )
        } else {
            (activity as MainActivity).changeActionBar(getString(R.string.add_vehicle), "")
        }
    }
}