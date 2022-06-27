package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.databinding.FragmentMainBinding
import aguilera.code.mantenimientogaraje.databinding.FragmentNewConceptVehicleBinding
import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

class NewConceptVehicleFragment : Fragment() {
    private var _binding: FragmentNewConceptVehicleBinding? = null
    private val binding get() = _binding!!

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

        val matricula = arguments?.getString("matricula")

        /*if (miConcepto != null) {
            binding.etConcepto.setText(miConcepto.concepto.toString())
            binding.etFecha.setText(miConcepto.fecha.toString())
            binding.etKMSC.setText(miConcepto.kms.toString())
            binding.etPrecio.setText(miConcepto.precio.toString())
            binding.etTaller.setText(miConcepto.taller.toString())
            binding.etDetallesC.setText(miConcepto.detalles.toString())
        }*/

        binding.btnOKC.setOnClickListener {
            /*val concepto = Concepto()

            concepto.concepto = binding.etConcepto.text.toString()
            concepto.fecha = binding.etFecha.text.toString()//fecha
            concepto.kms = binding.etKMSC.text.toString().toInt()
            concepto.precio = binding.etPrecio.text.toString().toFloat()
            concepto.taller = binding.etTaller.text.toString()
            concepto.detalles = binding.etDetallesC.text.toString()
            concepto.recordar =
                binding.cbRecordar.isChecked//checkbox pida fecha y kms ventana emergente
            //concepto.rFecha=recordarFecha)
            //concepto.rKms=recordarKms)

            val dbHandler = MyDBHandler(requireActivity())
            if (matricula != null) {
                dbHandler.addConcept(concepto, matricula)
            }
            Toast.makeText(requireActivity(), "Concepto añadido correctamente", Toast.LENGTH_SHORT)
                .show()
            activity?.supportFragmentManager?.popBackStack()*/
        }
    }
}