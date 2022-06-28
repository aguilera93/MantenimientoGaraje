package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import aguilera.code.mantenimientogaraje.data.ui.GarageViewModel
import aguilera.code.mantenimientogaraje.databinding.FragmentNewVehicleBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class NewVehicleFragment : Fragment() {

    private var _binding: FragmentNewVehicleBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModal: GarageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewVehicleBinding.inflate(inflater, container, false)

        // initializes the viewmodel.
        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // getting data passed via an intent.
        val type = arguments?.getString("type")
        if (type.equals("Edit")) {
            // setting data to edit text.

            binding.etMatricula.setText(arguments?.getString("matricula"))
            binding.etMarca.setText(arguments?.getString("marca"))
            binding.etModelo.setText(arguments?.getString("modelo"))
            binding.etKMSV.setText(arguments?.getString("kms"))
            binding.etVIN.setText(arguments?.getString("vin"))
            binding.etDetallesV.setText(arguments?.getString("detalles"))
            binding.btnSave.setText("Update Vehicle")
        } else {
            binding.btnSave.setText("Save Vehicle")
        }

        // adding click listener to our save button.
        binding.btnSave.setOnClickListener {

            // getting title and desc from edit text.
            val matricula = binding.etMatricula.text.toString()
            val marca = binding.etMarca.text.toString()
            val modelo = binding.etModelo.text.toString()
            val kms = binding.etKMSV.text.toString().toIntOrNull()
            val vin = binding.etVIN.text.toString()
            val detalles = binding.etDetallesV.text.toString()

            val vehiculo = Vehiculo(matricula, marca, modelo, kms, vin, detalles)
            // checking the type and then saving or updating the data.
            if (type.equals("Edit")) {

                if (matricula.isNotEmpty()) {
                    binding.etMatriculaLay.error = null
                    viewModal.updateVehicle(vehiculo)
                    Toast.makeText(requireActivity(), "Vehiculo Modificado", Toast.LENGTH_LONG).show()
                }
            } else {
                if (matricula.isNotEmpty()) {
                    binding.etMatriculaLay.error = null
                    // if the string is not empty we are calling
                    // add event method to add data to our room database.
                    //why id null? because id is auto generate
                    viewModal.insertVehicle(vehiculo)
                    Toast.makeText(requireActivity(), "Vehiculo Añadido", Toast.LENGTH_LONG).show()
                } else {
                    binding.etMatriculaLay.error = "Debe introducir una matricula valida"
                }
            }
            activity?.supportFragmentManager?.popBackStack()
        }
    }
}